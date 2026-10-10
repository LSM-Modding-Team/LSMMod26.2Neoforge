"""External model preview with alpha compositing; not a Minecraft screenshot."""
import json,math,zipfile,io
from pathlib import Path
import numpy as np
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/"src/main/resources/assets/lsmmod"

def render(model,center,scale):
    w,h=480,600; rgb=np.full((h,w,3),(112,130,128),dtype=np.uint8);depth=np.full((h,w),-np.inf)
    az,el=np.radians([-60,18]);eye=np.array([math.cos(az)*math.cos(el),math.sin(el),math.sin(az)*math.cos(el)])
    right=np.array([-math.sin(az),0,math.cos(az)]);up=np.cross(right,eye)
    surfaces=[]
    for e in model['elements']:
        x1,y1,z1=e['from'];x2,y2,z2=e['to']
        corners={'north':[(x2,y2,z1),(x2,y1,z1),(x1,y1,z1),(x1,y2,z1)],'south':[(x1,y2,z2),(x1,y1,z2),(x2,y1,z2),(x2,y2,z2)],'east':[(x2,y2,z2),(x2,y1,z2),(x2,y1,z1),(x2,y2,z1)],'west':[(x1,y2,z1),(x1,y1,z1),(x1,y1,z2),(x1,y2,z2)],'up':[(x1,y2,z1),(x1,y2,z2),(x2,y2,z2),(x2,y2,z1)],'down':[(x2,y1,z1),(x2,y1,z2),(x1,y1,z2),(x1,y1,z1)]}
        normals={'north':(0,0,-1),'south':(0,0,1),'east':(1,0,0),'west':(-1,0,0),'up':(0,1,0),'down':(0,-1,0)}
        for f,face in e['faces'].items():
            if np.array(normals[f])@eye<=0:continue
            pts=np.array(corners[f])-center
            p=np.column_stack((w/2+pts@right*scale,h/2-pts@up*scale,pts@eye))
            u0,v0,u1,v1=face['uv'];uv=np.array([(u0,v0),(u0,v1),(u1,v1),(u1,v0)])
            texture=model['textures'][face['texture'][1:]];namespace,name=texture.split(':')
            if namespace=='minecraft':
                with zipfile.ZipFile(ROOT/'.build-tools/gradle/caches/neoformruntime/artifacts/minecraft_26.2_client.jar') as jar:
                    tex=np.array(Image.open(io.BytesIO(jar.read('assets/minecraft/textures/'+name+'.png'))).convert('RGBA'))
            else:tex=np.array(Image.open(A/f'textures/{name}.png').convert('RGBA'))
            surfaces.append((p[:,2].mean(),p,uv,tex))
    for _,poly,uv,tex in sorted(surfaces,key=lambda x:x[0]):
        for tri in [(0,1,2),(0,2,3)]:
            p=poly[list(tri)];t=uv[list(tri)]
            xmin,ymin=np.floor(p[:,:2].min(axis=0)).astype(int);xmax,ymax=np.ceil(p[:,:2].max(axis=0)).astype(int)
            xmin,ymin=max(0,xmin),max(0,ymin);xmax,ymax=min(w-1,xmax),min(h-1,ymax)
            if xmin>xmax or ymin>ymax:continue
            xx,yy=np.meshgrid(np.arange(xmin,xmax+1)+.5,np.arange(ymin,ymax+1)+.5)
            a,b,c=p;den=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
            if abs(den)<1e-9:continue
            s=((b[1]-c[1])*(xx-c[0])+(c[0]-b[0])*(yy-c[1]))/den
            q=((c[1]-a[1])*(xx-c[0])+(a[0]-c[0])*(yy-c[1]))/den;r=1-s-q
            z=s*a[2]+q*b[2]+r*c[2];u=s*t[0,0]+q*t[1,0]+r*t[2,0];v=s*t[0,1]+q*t[1,1]+r*t[2,1]
            colors=tex[np.clip((v*tex.shape[0]/16).astype(int),0,tex.shape[0]-1),np.clip((u*tex.shape[1]/16).astype(int),0,tex.shape[1]-1)]
            target=depth[ymin:ymax+1,xmin:xmax+1];mask=(s>=0)&(q>=0)&(r>=0)&(z>target+1e-7)&(colors[:,:,3]>0)
            target[mask]=z[mask];patch=rgb[ymin:ymax+1,xmin:xmax+1];alpha=colors[:,:,3:4]/255
            blended=colors[:,:,:3]*alpha+patch*(1-alpha);patch[mask]=blended[mask].astype(np.uint8)
    return Image.fromarray(rgb)

def main():
    canvas=Image.new('RGB',(960,600),(112,130,128));labels=ImageDraw.Draw(canvas)
    base=json.loads((A/'models/block/projector_screen.json').read_text())
    pieces=[]
    for column in range(4):
        for row in range(3):
            for original in base['elements']:
                e=json.loads(json.dumps(original))
                for k in ('from','to'):e[k]=[e[k][0]+16*column,e[k][1]+16*row,e[k][2]]
                pieces.append(e)
    textures=dict(base['textures']);detail=[]
    for column in range(2):
        m=json.loads((A/f'models/block/wall_projector_{column}.json').read_text());textures.update(m['textures'])
        for original in m['elements']:
            e=json.loads(json.dumps(original))
            for k in ('from','to'):e[k][0]+=16*column
            detail.append(e)
            e=json.loads(json.dumps(e))
            for k in ('from','to'):e[k][0]+=16;e[k][1]+=48
            pieces.append(e)
    canvas.paste(render({'textures':textures,'elements':pieces},[32,32,8],6),(0,0))
    canvas.paste(render({'textures':textures,'elements':detail},[16,8,8],15),(480,0))
    labels.text((20,20),'Pantalla 4 x 3 / proyector centrado encima',fill='white')
    labels.text((500,20),'Proyector en 2 celdas / escala original',fill='white')
    canvas.save(ROOT/'previews/projector_screen_4x3_centered.png')

if __name__=='__main__':main()
