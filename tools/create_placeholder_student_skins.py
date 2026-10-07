"""Generate new 256x256 wide-player placeholder atlases with numbered heads and school uniforms."""
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1];ASSETS=ROOT/'src/main/resources/assets/lsmmod/textures/entity'
S=4
import argparse
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('--font', default='/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', help='Path to a bold TrueType font')
FONT=parser.parse_args().font
RED=(135,39,49,255);DARK=(109,28,40,255);PANTS=(92,100,106,255);SHOE=(24,26,29,255)
def rect(box,color):
 x,y,X,Y=[round(v*S) for v in box];d.rectangle((x,y,max(x,X-1),max(y,Y-1)),fill=color)
def line(points,color,width=1):d.line([(round(x*S),round(y*S)) for x,y in points],fill=color,width=width)
def part(u,v,w,h,depth,color):
 faces={'top':(u+depth,v,u+depth+w,v+depth),'bottom':(u+depth+w,v,u+depth+2*w,v+depth),'right':(u,v+depth,u+depth,v+depth+h),'front':(u+depth,v+depth,u+depth+w,v+depth+h),'left':(u+depth+w,v+depth,u+2*depth+w,v+depth+h),'back':(u+2*depth+w,v+depth,u+2*depth+2*w,v+depth+h)}
 for box in faces.values():rect(box,color)
 return faces
def text(box,label,color,size):
 x,y,X,Y=[int(a*S) for a in box];font=ImageFont.truetype(FONT,size);bbox=font.getbbox(label);w=bbox[2]-bbox[0];h=bbox[3]-bbox[1]
 d.text((x+(X-x-w)//2-bbox[0],y+(Y-y-h)//2-bbox[1]),label,font=font,fill=color)
def generate(level,grade):
 global im,d
 skin=(255,255,255,255) if level!='secondary' else (0,0,0,255)
 ink=(0,0,0,255) if level!='secondary' else (255,255,255,255)
 im=Image.new('RGBA',(256,256));d=ImageDraw.Draw(im)
 head=part(0,0,8,8,8,skin)
 for face in ['front','back','left','right']:text(head[face],str(grade) if grade else 'NPC',ink,30 if grade else 13)
 body=part(16,16,8,12,4,RED)
 # Gentle fabric grain on the torso; seams and collar remain readable at distance.
 for face,b in body.items():
  x,y,X,Y=[int(a*S) for a in b]
  for yy in range(y,Y):
   for xx in range(x,X):
    delta=(xx//3+yy//6)%3-1;im.putpixel((xx,yy),tuple(max(0,min(255,c+delta)) for c in RED[:3])+(255,))
 d.polygon([(80,80),(92,80),(96,86),(90,89)],fill=DARK)
 d.polygon([(96,86),(100,80),(112,80),(102,89)],fill=DARK)
 rect((23.6,21,24.4,24),DARK)
 for y in [21.8,22.6,23.4]:rect((23.8,y,24.2,y+.25),(188,123,128,255))
 line([(20,31.5),(28,31.5)],DARK);line([(32,20.5),(40,20.5)],DARK)
 # Embroidered shield at the student's left chest.
 d.polygon([(102,88),(110,88),(109,96),(106,99),(103,96)],fill=(243,237,231,255))
 text((25.5,22.4,27.5,24),'LSM',RED,5)
 for u,v in [(40,16),(32,48)]:
  arm=part(u,v,4,12,4,RED)
  for face in ['front','back','left','right']:
   x,y,X,Y=arm[face]
   line([(x+.4,y+.5),(x+.4,Y-1.2)],DARK)
   rect((x,Y-1.25,X,Y-1),DARK)
   rect((x,Y-1,X,Y),skin)
  rect(arm['bottom'],skin)
 for u,v in [(0,16),(16,48)]:
  leg=part(u,v,4,12,4,PANTS)
  for face in ['front','back','left','right']:
   x,y,X,Y=leg[face];line([(x+2,y+.6),(x+2,Y-2)],(75,83,90,255))
   rect((x,Y-2,X,Y),SHOE);rect((x,Y-.35,X,Y),(10,12,14,255))
  rect(leg['bottom'],SHOE)
  x,y,X,Y=leg['front']
  for off in [1.6,1.25,.9]:line([(x+.7,Y-off),(X-.7,Y-off)],(67,70,75,255))
 name=f'placeholder_{grade}{"p" if level=="primary" else "s"}.png' if grade else 'placeholder_school_npc.png'
 path=ASSETS/('students' if grade else '')/name;path.parent.mkdir(parents=True,exist_ok=True);im.save(path)
for level,count in [('primary',6),('secondary',5)]:
 for grade in range(1,count+1):generate(level,grade)
generate('primary',0)
print('Generated 11 classroom placeholders and NPC fallback, 256x256 RGBA; no geometry changes.')
