"""Generate four textured stepped spheres using ordinary Minecraft cuboids."""
from pathlib import Path
import json, math
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
for p in ['textures/item','models/item','items']:(A/p).mkdir(parents=True,exist_ok=True)
for name in ['plastic_ball','football_ball','basketball_ball','volleyball_ball']:
 im=Image.new('RGBA',(16,16));d=ImageDraw.Draw(im)
 if name=='plastic_ball':
  colors=['#df4944','#f5c848','#4b93cf','#5ca567']
  for y in range(16):
   for x in range(16):d.point((x,y),fill=colors[((x//4)+(y//8))%4])
 elif name=='football_ball':
  d.rectangle((0,0,15,15),fill='#efeee5');d.polygon([(5,3),(10,3),(13,8),(9,12),(4,10),(2,6)],fill='#242424')
  for x in [0,15]:d.line((x,0,x,15),fill='#b5b5aa')
 elif name=='basketball_ball':
  d.rectangle((0,0,15,15),fill='#c97829')
  for y in range(16):
   for x in range(16):
    if (x+y)%3==0:d.point((x,y),fill='#b56925')
  d.line((7,0,7,15),fill='#28221d');d.line((0,7,15,7),fill='#28221d')
  d.line([(1,0),(3,3),(4,7),(3,12),(1,15)],fill='#28221d');d.line([(14,0),(12,3),(11,7),(12,12),(14,15)],fill='#28221d')
 else:
  d.rectangle((0,0,15,15),fill='#eeeee6')
  d.polygon([(0,0),(6,0),(15,9),(15,15)],fill='#e6c13d');d.polygon([(0,4),(0,10),(6,15),(12,15)],fill='#3370ad')
  d.line([(0,2),(15,13)],fill='#d2d2c9');d.line([(2,0),(15,11)],fill='#d2d2c9')
 im.save(A/f'textures/item/{name}.png')
 elements=[]
 for y in range(8):
  Y=y*2
  for z in range(8):
   inside=[x for x in range(8) if (x*2+1-8)**2+(Y+1-8)**2+(z*2+1-8)**2 <= 8**2]
   if not inside:continue
   lo=min(inside)*2;hi=(max(inside)+1)*2;Z=z*2
   faces={f:{'texture':'#ball','uv':uv} for f,uv in {'north':[lo,16-Y-2,hi,16-Y],'south':[lo,16-Y-2,hi,16-Y],'east':[Z,16-Y-2,Z+2,16-Y],'west':[Z,16-Y-2,Z+2,16-Y],'up':[lo,Z,hi,Z+2],'down':[lo,Z,hi,Z+2]}.items()}
   elements.append({'from':[lo,Y,Z],'to':[hi,Y+2,Z+2],'faces':faces})

 model={'textures':{'ball':f'lsmmod:item/{name}','particle':f'lsmmod:item/{name}'},'elements':elements,'display':{'gui':{'rotation':[25,35,0],'scale':[.65,.65,.65]},'ground':{'scale':[1,1,1]},'firstperson_righthand':{'translation':[0,1,0],'scale':[.5,.5,.5]},'thirdperson_righthand':{'scale':[.4,.4,.4]}}}
 (A/f'models/item/{name}.json').write_text(json.dumps(model,indent=2)+'\n')
 (A/f'items/{name}.json').write_text(json.dumps({'model':{'type':'minecraft:model','model':f'lsmmod:item/{name}'}},indent=2)+'\n')
print('Generated four voxel-sphere models, four 16x16 RGBA textures and four item definitions.')
