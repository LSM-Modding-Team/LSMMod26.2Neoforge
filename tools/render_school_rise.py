"""External textured geometry preview of reusable stairs and slopes."""
import math,json
import numpy as np
from PIL import Image,ImageDraw
from create_school_rise_assets import A,ROOT
from check_school_rise_assets import mesh

def cuboid_faces(e):
 x1,y1,z1=e['from'];x2,y2,z2=e['to']
 corners={'north':[(x2,y2,z1),(x2,y1,z1),(x1,y1,z1),(x1,y2,z1)],'south':[(x1,y2,z2),(x1,y1,z2),(x2,y1,z2),(x2,y2,z2)],'east':[(x2,y2,z2),(x2,y1,z2),(x2,y1,z1),(x2,y2,z1)],'west':[(x1,y2,z1),(x1,y1,z1),(x1,y1,z2),(x1,y2,z2)],'up':[(x1,y2,z1),(x1,y2,z2),(x2,y2,z2),(x2,y2,z1)],'down':[(x2,y1,z1),(x2,y1,z2),(x1,y1,z2),(x1,y1,z1)]}
 for f,face in e['faces'].items():
  u0,v0,u1,v1=face['uv'];yield np.array(corners[f]),np.array([(u0,v0),(u0,v1),(u1,v1),(u1,v0)])/16

def render(slope):
 w,h=960,640;canvas=np.full((h,w,3),(117,134,130),dtype=np.uint8);depth=np.full((h,w),-np.inf)
 az,el=np.radians((48,24));eye=np.array([math.cos(az)*math.cos(el),math.sin(el),math.sin(az)*math.cos(el)])
 right=np.array([-math.sin(az),0,math.cos(az)]);up=np.cross(right,eye);center=np.array([24,40,-40]);scale=4.5
 tex=np.array(Image.open(A/('textures/block/light_school_wall.png' if slope else 'textures/block/classroom_floor.png')).convert('RGB'))
 surfaces=[]
 def add(pts,uv,offset):
  # The established cuboid renderer uses clockwise face corners; OBJ uses outward CCW.
  a,b,c=pts[:3];normal=np.cross(b-a,c-a).astype(float);normal/=np.linalg.norm(normal)
  if normal@eye<=0:return
  shade=.76+.24*max(0,normal@np.array([.4,.85,.3]));p=pts+offset-center
  p=np.column_stack((w/2+p@right*scale,h/2-p@up*scale,p@eye))
  surfaces.append((p,uv,shade))
 for column in range(5):
  for width in range(3):
   offset=np.array([16*width,16*column,-16*column])
   if slope:
    for ident,lift in (('school_slope_raised_base',0),('school_slope_raised_tip',16)):
     vertices,faces,coords=mesh(A/f'models/block/{ident}.obj')
     for face in faces:add(vertices[[i[0] for i in face]]*16,np.array([coords[i[1]] for i in face]),offset+np.array([0,lift,0]))
   else:
    for lo,hi in (([0,0,0],[16,8,16]),([0,8,0],[16,16,8])):
     e={'from':lo,'to':hi,'faces':{f:{'uv':[0,0,16,16]} for f in ('north','south','east','west','up','down')}}
     for pts,uv in cuboid_faces(e):add(pts[::-1],uv[::-1],offset)
   for row in range(column):
    e={'from':[0,0,0],'to':[16,16,16],'faces':{f:{'uv':[0,0,16,16]} for f in ('north','south','east','west','up','down')}}
    for pts,uv in cuboid_faces(e):add(pts[::-1],uv[::-1],np.array([width*16,row*16,-column*16]))
 for poly,uv,shade in surfaces:
  for i in range(1,len(poly)-1):
   p=poly[[0,i,i+1]];t=uv[[0,i,i+1]];xmin,ymin=np.floor(p[:,:2].min(axis=0)).astype(int);xmax,ymax=np.ceil(p[:,:2].max(axis=0)).astype(int)
   xmin,ymin=max(0,xmin),max(0,ymin);xmax,ymax=min(w-1,xmax),min(h-1,ymax)
   if xmin>xmax or ymin>ymax:continue
   xx,yy=np.meshgrid(np.arange(xmin,xmax+1)+.5,np.arange(ymin,ymax+1)+.5)
   a,b,c=p;den=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
   if abs(den)<1e-8:continue
   s=((b[1]-c[1])*(xx-c[0])+(c[0]-b[0])*(yy-c[1]))/den
   q=((c[1]-a[1])*(xx-c[0])+(a[0]-c[0])*(yy-c[1]))/den;r=1-s-q
   z=s*a[2]+q*b[2]+r*c[2];u=s*t[0,0]+q*t[1,0]+r*t[2,0];v=s*t[0,1]+q*t[1,1]+r*t[2,1]
   target=depth[ymin:ymax+1,xmin:xmax+1];mask=(s>=0)&(q>=0)&(r>=0)&(z>target+1e-7)
   colors=tex[np.clip((v*16).astype(int),0,15),np.clip((u*16).astype(int),0,15)]
   target[mask]=z[mask];canvas[ymin:ymax+1,xmin:xmax+1][mask]=(colors[mask]*shade).astype(np.uint8)
 return Image.fromarray(canvas)
def main():
 out=Image.new('RGB',(1920,640))
 for i,slope in enumerate((False,True)):out.paste(render(slope),(960*i,0))
 draw=ImageDraw.Draw(out);draw.text((24,24),'Un solo bloque de escalera - repetido en toda la anchura',fill='white');draw.text((984,24),'Base + remate: pendiente elevada continua - pared clara',fill='white')
 out.save(ROOT/'previews/school_stairs_slopes_reusable.png')
if __name__=='__main__':main()
