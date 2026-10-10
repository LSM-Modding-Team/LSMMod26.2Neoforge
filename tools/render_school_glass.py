"""External model preview with alpha compositing; not a Minecraft screenshot."""
import json,math,zipfile,io
from pathlib import Path
import numpy as np
from PIL import Image,ImageDraw
from create_school_glass_assets import A,ROOT

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
    canvas=Image.new('RGB',(1920,600),(112,130,128));draw=ImageDraw.Draw(canvas)
    for x,name,label in [(0,'school_glass','Cristal: grosor 8 px'),(480,'supported_school_glass','Cristal / soporte de pared oscura')]:
        m=json.loads((A/f'models/block/{name}.json').read_text());canvas.paste(render(m,[8,8,8],18),(x,0));draw.text((x+20,25),label,fill='white')
    mixed=json.loads((A/'models/block/mixed_school_glass.json').read_text())
    canvas.paste(render(mixed,[8,8,8],18),(960,0));draw.text((980,25),'Cristal / muro claro / concreto blanco',fill='white')
    elements=[]
    for row in range(3):
        for col in range(2):
            m=json.loads((A/f'models/block/tall_classroom_entrance_{col}_{row}.json').read_text())
            for e in m['elements']:
                for k in ('from','to'):e[k]=[e[k][0]+16*col,e[k][1]+16*row,e[k][2]]
                elements.append(e)
    m['elements']=elements;canvas.paste(render(m,[16,24,8],9.5),(1440,0));draw.text((1460,25),'Puerta escalonada: 3 bloques, cristal superior',fill='white')
    p=ROOT/'previews/school_glass_entrance.png';p.parent.mkdir(exist_ok=True);canvas.save(p)
    operation=Image.new('RGB',(960,600),(112,130,128));labels=ImageDraw.Draw(operation)
    for opened in (False,True):
        pieces=[]
        for col in range(2):
            for row in range(3):
                m=json.loads((A/f'models/block/tall_classroom_entrance_{col}_{row}{"_open" if opened else ""}.json').read_text())
                for e in m['elements']:
                    for k in ('from','to'):e[k]=[e[k][0]+16*col,e[k][1]+16*row,e[k][2]]
                    pieces.append(e)
        m['elements']=pieces;operation.paste(render(m,[16,24,8],9.5),(480*int(opened),0))
        labels.text((20+480*int(opened),25),'Puertas '+('abiertas' if opened else 'cerradas')+' / cristal superior fijo',fill='white')
    operation.save(ROOT/'previews/tall_classroom_door_open_closed.png')
    stairs=Image.new('RGB',(1920,600),(112,130,128));labels=ImageDraw.Draw(stairs)
    for index,(name,label) in enumerate([('supported_school_glass','Paredes: original'),('supported_school_glass_reversed','Paredes: materiales invertidos'),('mixed_school_glass','Muro/concreto: original'),('mixed_school_glass_reversed','Muro/concreto: materiales invertidos')]):
        m=json.loads((A/f'models/block/{name}_bottom_straight_1_0.json').read_text())
        upper=json.loads((A/'models/block/school_glass_0_1.json').read_text())
        for e in m['elements']:
            if e['to'][1]==16:e['faces'].pop('up',None)
        for e in upper['elements']:
            if e['from'][1]==0:e['faces'].pop('down',None)
            for k in ('from','to'):e[k][1]+=16
        m['elements']+=upper['elements']
        stairs.paste(render(m,[8,16,8],14),(480*index,0));labels.text((20+480*index,25),label,fill='white')
    stairs.save(ROOT/'previews/school_stairs_reversed_connections.png')

    stack=Image.new('RGB',(1920,600),(112,130,128));labels=ImageDraw.Draw(stack)
    for index,name in enumerate(('supported_school_glass','mixed_school_glass','supported_school_glass_reversed','mixed_school_glass_reversed')):
        pieces=[]
        for row,filename in enumerate((name+'_bottom_straight_1_0','school_glass_1_1',name+'_top_straight_0_1')):
            m=json.loads((A/f'models/block/{filename}.json').read_text())
            for e in m['elements']:
                if row>0 and e['from'][1]==0:e['faces'].pop('down',None)
                if row<2 and e['to'][1]==16:e['faces'].pop('up',None)
                for k in ('from','to'):e[k][1]+=16*row
                pieces.append(e)
        m['elements']=pieces;stack.paste(render(m,[8,24,8],10),(480*index,0))
        labels.text((20+480*index,25),name.replace('_school_glass',''),fill='white')
    stack.save(ROOT/'previews/school_glass_stack_corrected.png')



if __name__=='__main__':main()
