"""Create vanilla-scale 2D skin layers: 64 skin tones, 8 eyes, 32 male cuts, 32 female cuts in two hair modes, 8 glasses."""
from pathlib import Path
import json
from PIL import Image, ImageDraw, ImageChops
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'src/main/resources/assets/lsmmod/textures/entity/student_appearance'
OUT.mkdir(parents=True,exist_ok=True)
DATA=json.loads((ROOT/'tools/student_appearance_palette.json').read_text())
DARK,MID='#2e2117','#493526'
HEAD=[(8,0,16,8),(16,0,24,8),(0,8,8,16),(8,8,16,16),(16,8,24,16),(24,8,32,16)]
def canvas():
 im=Image.new('RGBA',(64,64));return im,ImageDraw.Draw(im)
def rect(d,b,c):
 x,y,X,Y=b;d.rectangle((x,y,X-1,Y-1),fill=c)
def save(im,name):im.resize((256,256),Image.Resampling.NEAREST).save(OUT/(name+'.png'))
for i,c in enumerate(DATA['skinColors']):
 im,d=canvas()
 for b in HEAD:rect(d,b,c)
 for u,v in [(40,16),(32,48)]:
  rect(d,(u+8,v,u+12,v+4),c)
  for x in [u,u+4,u+8,u+12]:rect(d,(x,v+15,x+4,v+16),c)
 r,g,b=bytes.fromhex(c[1:])
 # Restrained Alex-style nose and mouth, with shading adapted to the selected skin tone.
 rect(d,(11,12,13,13),(int(r*.93),int(g*.89),int(b*.87),255))
 rect(d,(11,14,13,15),(int(r*.77),int(g*.67),int(b*.64),255))
 save(im,f'skin_{i:02}')
for i,c in enumerate(DATA['eyeColors']):
 im,d=canvas()
 for x in [9,13]:
  rect(d,(x,11,x+2,12),'#ece8df')
  rect(d,(x+(1 if x==9 else 0),11,x+(2 if x==9 else 1),12),c)
 save(im,f'eye_{i}')

def hair(style,female=False,free=False):
 im,d=canvas();kind=('straight' if style<16 else 'wavy' if style<24 else 'curly') if not female else DATA['haircutsFemale'][style]['type']
 long=female and free;pony=female and not free
 rect(d,(8,0,16,8),DARK)
 depth=(7 if long else 4 if pony else 3+(style%3))
 for x in [0,16,24]:rect(d,(x,8,x+8,8+depth),DARK)
 # Asymmetrical fringe, exposing both eyes. All texels are whole vanilla skin pixels.
 rect(d,(8,8,16,10),DARK)
 for x in [8+(style%3),14-(style%2)]:rect(d,(x,10,x+1,11),DARK)
 if female:
  for x in [8,15]:rect(d,(x,9,x+1,14 if long or pony else 12),DARK)
 if kind=='straight':
  x=9+style%6;rect(d,(x,0,x+1,8),MID)
  if long:
   rect(d,(26,8,27,15),MID)
 elif kind=='wavy':
  # Connected, sparse stepped locks, avoiding fine noise or tiled checker patterns.
  for origin in [1,5]:
   for y in range(8):
    shift=[0,0,1,1,0,0,-1,-1][(y+style)%8]
    for u,v in [(8,0),(0,8),(16,8),(24,8)]:
     x=u+origin+shift
     if im.getpixel((x,v+y))[3]:d.point((x,v+y),fill=MID)
  d.point((10,9),fill=MID);d.point((13,8),fill=MID)
 else:
  # Isolated 2x2 stepped locks rather than rings filling the entire head.
  for u,v in [(8,0),(0,8),(16,8),(24,8)]:
   for x,y in [(1,1),(5,2),(2,5),(6,6)]:
    xx=u+x;yy=v+y
    for dx,dy in [(0,0),(1,0),(1,1)]:
     if im.getpixel((xx+dx,yy+dy))[3]:d.point((xx+dx,yy+dy),fill=MID)
 if female and style>=6:
  # One restrained part/detail per extra cut; no subpixel texture noise.
  d.point((24+style%8,8+(style//8)%3),fill=MID)
 if long:
  # Hair painted on the torso as in conventional skins. It stops at bust level.
  rect(d,(32,20,40,26),DARK)
  rect(d,(20,20,21,26),DARK);rect(d,(27,20,28,26),DARK)
  # Matching front corners on the existing girl-model bust, without modifying its shape.
  rect(d,(20,36,21,39),DARK);rect(d,(27,36,28,39),DARK)
  if kind=='straight':rect(d,(34,20,35,26),MID)
  else:
   for x,y in [(33,20),(36,22),(38,24)]:
    rect(d,(x,y,x+1,y+1),MID);rect(d,(x+1,y+1,x+2,y+2),MID)
 if not female or (not long and not pony):
  # Distinct cropped nape notches keep all existing male indices meaningful.
  d.point((24+style%8,8+depth-1),fill=(0,0,0,0))
 if female and free:
  # One vanilla-size accent distinguishes the same-length loose silhouettes.
  d.point((16+style%8,12+style//8),fill='#5b4230')
 if female and style==25:
  d.point((12,10),fill=DARK)
 if pony:
  rect(d,(27,12,29,16),DARK)
  rect(d,(35,20,37,25),DARK);rect(d,(35,20,37,21),'#701524')
  rect(d,(35,22,36,24),MID)
  if female and style not in [3]:
   d.point((27+style%2,12+style%3),fill=MID)
   if 'double' in DATA['haircutsFemale'][style]['name']:
    rect(d,(35,20,37,25),(0,0,0,0))
    rect(d,(33,20,34,25),DARK);rect(d,(38,20,39,25),DARK)
    d.point((33,20),fill='#701524');d.point((38,20),fill='#701524')
   if 'braid' in DATA['haircutsFemale'][style]['name']:
    for yy in range(21,25):d.point((35+yy%2,yy),fill=MID)
 return im
for i in range(32):save(hair(i),f'male_hair_{i:02}')
for i in range(32):
 save(hair(i,True,False),f'female_hair_{i:02}')
 save(hair(i,True,True),f'female_free_hair_{i:02}')
for kind,c in enumerate(DATA['glassesColors'],1):
 im,d=canvas()
 for x in [8,12]:
  if kind in [2,3,4]:
   # Square-pixel rounded silhouettes with clear, unpainted lenses.
   for dx,dy in [(1,0),(2,0),(0,1),(3,1),(1,2),(2,2)]:d.point((x+dx,10+dy),fill=c)
   if kind==3:d.point((x+1,13),fill=c);d.point((x+2,13),fill=c)
   if kind==4:d.point((x,10),fill=c);d.point((x+3,12),fill=c)
  elif kind==5:
   rect(d,(x,10,x+4,11),c);d.point((x,11),fill=c);d.point((x+3,11),fill=c)
  elif kind==7:
   rect(d,(x,10,x+4,11),c);d.point((x,11),fill=c);d.point((x+3,11),fill=c);rect(d,(x+1,12,x+3,13),c)
  else:
   d.rectangle((x,10,x+3,12),outline=c,width=1)
   if kind==6:rect(d,(x,9,x+4,10),c)
   if kind==8:d.point((x,13),fill=c);d.point((x+3,13),fill=c)
 d.point((11,11),fill=c);d.point((12,11),fill=c)
 # Temple arms across the two head side faces.
 rect(d,(6,11,8,12),c);rect(d,(16,11,18,12),c)
 save(im,f'glasses_{kind}')
print('Generated 176 vanilla-scale 2D layers, nearest-upscaled to 256x256.')
