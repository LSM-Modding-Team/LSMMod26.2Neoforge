"""Generate player school-uniform atlases and 3-D inventory models. Requires Pillow."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import json
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
S=4
NAMES=['fake_school_haircut','uniform_polo','uniform_pants','uniform_shoes']
COLORS=[(23,22,24),(135,39,49),(92,100,106),(25,27,29)]
def write(path,obj):
 path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(obj,indent=2)+'\n')
def rect(box,color):
 x,y,X,Y=[round(v*S) for v in box];d.rectangle((x,y,max(x,X-1),max(y,Y-1)),fill=color)
def line(points,color,width=1):d.line([(round(x*S),round(y*S)) for x,y in points],fill=color,width=width)
def box(x,y,w,h,dep,color,cut=None):
 # Vanilla humanoid cube atlas layout (texture coordinate units).
 for u,v,ww,hh,f in [(x+dep,y,w,dep,'top'),(x+dep+w,y,w,dep,'bottom'),(x,y+dep,dep,h,'side'),(x+dep,y+dep,w,h,'front'),(x+dep+w,y+dep,dep,h,'side'),(x+dep+w+dep,y+dep,w,h,'back')]:
  if cut and ((f=='top' and cut[0]>0) or (f=='bottom' and cut[1]<h)):continue
  start=0 if not cut else cut[0];end=h if not cut else cut[1]
  if f in ['top','bottom']:rect((u,v,u+ww,v+hh),color)
  else:rect((u,v+start,u+ww,v+end),color)
def cube(a,b,uv):return {'from':a,'to':b,'faces':{f:{'texture':'#atlas','uv':v} for f,v in uv.items()}}
def uvbox(x,y,w,h,dep):
 # Atlas is 64x32; model UV coordinates are normalized to 16x16.
 vals={'up':[x+dep,y,x+dep+w,y+dep],'down':[x+dep+w,y,x+dep+2*w,y+dep],'east':[x,y+dep,x+dep,y+dep+h],'north':[x+dep,y+dep,x+dep+w,y+dep+h],'west':[x+dep+w,y+dep,x+2*dep+w,y+dep+h],'south':[x+2*dep+w,y+dep,x+2*dep+2*w,y+dep+h]}
 return {f:[v[0]/4,v[1]/2,v[2]/4,v[3]/2] for f,v in vals.items()}
for name,c in zip(NAMES,COLORS):
 im=Image.new('RGBA',(64*S,32*S));d=ImageDraw.Draw(im)
 if name=='fake_school_haircut':
  box(0,0,8,8,8,c,cut=(0,3))
  # Higher frontal hairline, shorter sides and back; face and ears remain visible.
  rect((8,10,16,11),(0,0,0,0));rect((8,10,12,10.75),c)
  for y in range(1,8):line([(8,y),(16,y-.5)],(38,36,40))
  for x in range(0,32,2):line([(x,8),(x+.5,10)],(34,32,36))
 elif name=='uniform_polo':
  box(16,16,8,12,4,c);box(40,16,4,12,4,c,cut=(0,4))
  # Collar and placket: front torso occupies (20,20)..(28,32).
  d.polygon([(20*S,20*S),(23*S,20*S),(24*S,21.4*S),(22.5*S,22*S)],fill=(112,30,42))
  d.polygon([(24*S,21.4*S),(25*S,20*S),(28*S,20*S),(25.5*S,22*S)],fill=(112,30,42))
  rect((23.6,21,24.4,24),(109,28,38))
  for y in [21.8,22.6,23.4]:rect((23.85,y,24.15,y+.2),(183,114,117))
  line([(20,31.5),(28,31.5)],(115,32,43))
  for x in [40,44,48,52]:line([(x,23.7),(x+4,23.7)],(109,30,40))
  # Small embroidered school shield on the wearer's left chest.
  d.polygon([(int(25.4*S),int(22*S)),(int(27.5*S),int(22*S)),(int(27.4*S),int(24*S)),(int(26.4*S),int(24.6*S)),(int(25.5*S),int(24*S))],fill=(240,232,223))
  font=ImageFont.load_default(size=5);d.text((25.5*S,22.5*S),'LSM',font=font,fill=(127,38,48))
  line([(33,20.5),(39,20.5)],(116,32,43))
 elif name=='uniform_pants':
  box(16,16,8,12,4,c,cut=(10,12));box(0,16,4,12,4,c)
  # Fabric waistband and seams only: no belt or buckle.
  line([(20,30.5),(28,30.5)],(76,84,90));line([(24,30.7),(24,32)],(70,78,84))
  for x in [2,6,10,14]:line([(x,20.5),(x,31.3)],(79,87,93))
  line([(4,31.4),(8,31.4)],(71,80,85))
 else:
  box(0,16,4,12,4,c,cut=(8,12))
  for x in [0,4,8,12]:
   rect((x,31.1,x+4,32),(12,13,15));line([(x,28.5),(x+4,28.5)],(44,46,48))
  # Laces on front and toe seam; no tall boot shaft.
  for y in [28.4,29.1,29.8]:line([(4.7,y),(7.3,y+.2)],(69,71,74))
  line([(4.3,30.6),(7.7,30.6)],(48,50,53))
 # Subtle deterministic fabric/hair shading on opaque texels.
 for y in range(im.height):
  for x in range(im.width):
   rr,gg,bb,aa=im.getpixel((x,y))
   if aa:
    v=(x*3+y*5)%5-2;im.putpixel((x,y),(max(0,rr+v),max(0,gg+v),max(0,bb+v),aa))
 layer='humanoid_leggings' if name=='uniform_pants' else 'humanoid'
 p=A/f'textures/entity/equipment/{layer}/{name}.png';p.parent.mkdir(parents=True,exist_ok=True);im.save(p)
 # Item textures must be stitched into the items atlas, not loaded as entity textures.
 item_path=A/f'textures/item/{name}.png';item_path.parent.mkdir(parents=True,exist_ok=True);im.save(item_path)
 write(A/f'equipment/{name}.json',{'layers':{layer:[{'texture':f'lsmmod:{name}'}]}})
 # Individual 3-D item models, using the same atlas as the worn clothing.
 head=uvbox(0,0,8,8,8);body=uvbox(16,16,8,12,4);arm=uvbox(40,16,4,12,4);leg=uvbox(0,16,4,12,4)
 if name=='fake_school_haircut':
  for f,uv in head.items():
   if f not in ['up','down']:uv[3]=5.5
  head.pop('down')
  elements=[cube([4,10,4],[12,13,12],head)]
 elif name=='uniform_polo':elements=[cube([4,2,6],[12,14,10],body),cube([0,10,6],[4,14,10],arm),cube([12,10,6],[16,14,10],arm)]
 elif name=='uniform_pants':elements=[cube([4,2,6],[7.8,14,10],leg),cube([8.2,2,6],[12,14,10],leg)]
 else:elements=[cube([3,4,3],[7,8,11],leg),cube([9,4,3],[13,8,11],leg)]
 # For short garment item cubes, remap to their opaque atlas strips.
 if name=='uniform_polo':
  for e in elements[1:]:
   for f,v in e['faces'].items():
    if f not in ['up','down']:v['uv'][3]=12
 if name=='uniform_shoes':
  for e in elements:
   for f,v in e['faces'].items():
    if f not in ['up','down']:v['uv'][1]=14
    if f=='up':v['uv']=leg['down']
 write(A/f'models/item/{name}.json',{'textures':{'atlas':f'lsmmod:item/{name}','particle':f'lsmmod:item/{name}'},'elements':elements,'display':{'gui':{'rotation':[15,-25,0],'scale':[.85,.85,.85]},'ground':{'scale':[.5,.5,.5]},'thirdperson_righthand':{'rotation':[0,90,0],'scale':[.65,.65,.65]},'firstperson_righthand':{'rotation':[0,-70,0],'scale':[.7,.7,.7]}}})
 write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{name}'}})
write(ROOT/'src/main/resources/data/lsmmod/recipe/fake_school_haircut.json',{'type':'minecraft:crafting_shaped','category':'equipment','pattern':['WWW','W W'],'key':{'W':'minecraft:black_wool'},'result':{'id':'lsmmod:fake_school_haircut','count':1}})
for tag,name in [('head_armor',NAMES[0]),('chest_armor',NAMES[1]),('leg_armor',NAMES[2]),('foot_armor',NAMES[3])]:write(ROOT/f'src/main/resources/data/minecraft/tags/item/{tag}.json',{'replace':False,'values':[f'lsmmod:{name}']})
print('Generated four player equipment atlases, equipment definitions, 3-D item models, tags and one wool recipe.')
