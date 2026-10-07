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
def varsity(level,skin):
 color=(112,21,36,255) if level=='primary' else (67,107,62,255)
 cream=(235,229,213,255);gold=(200,166,91,255);shadow=tuple(int(c*.72) for c in color[:3])+(255,)
 body=part(16,16,8,12,4,color)
 # Ribbed waist and collar wrap around all four sides of the jacket.
 for face in ['front','back','left','right']:
  x,y,X,Y=body[face]
  rect((x,Y-1.8,X,Y),shadow)
  for off in [.45,1.15]:rect((x,Y-off-.22,X,Y-off),cream)
  line([(x+.3,y+1),(x+.3,Y-1.8)],shadow)
 # Varsity collar, contrasting piping, full snap-button opening and slanted pockets.
 d.polygon([(80,80),(92,80),(96,87),(90,90)],fill=shadow)
 d.polygon([(96,87),(100,80),(112,80),(102,90)],fill=shadow)
 line([(20.5,20),(23,20),(24,22),(25,20),(27.5,20)],cream,2)
 rect((23.7,22,24.3,30.2),shadow)
 for y in [22.5,24,25.5,27,28.5,30]:
  d.ellipse((95,round(y*S),97,round(y*S)+2),fill=cream)
 line([(20.8,27),(21.7,29)],cream,2);line([(27.2,27),(26.3,29)],cream,2)
 text((25,22,27.8,25),'Mc',cream if level=='primary' else gold,7)
 for u,v in [(40,16),(32,48)]:
  arm=part(u,v,4,12,4,cream)
  for face in ['front','back','left','right']:
   x,y,X,Y=arm[face]
   line([(x+.35,y+.5),(x+.35,Y-2.4)],(210,205,192,255))
   rect((x,Y-2.5,X,Y-1),color)
   for off in [1.4,2.1]:rect((x,Y-off-.18,X,Y-off),cream)
   rect((x,Y-1,X,Y),skin)
  rect(arm['bottom'],skin)
 if level=='primary':
  text((32,20.5,40,23),'Promo 2026',cream,4)
  text((32,27,40,29.6),'Stellaris',cream,6)
 else:
  text((32,20.2,40,21.8),'Vastos',gold,6)
  text((32,21.8,40,23.5),'Indomitus',gold,5)
  # Angular Ender Dragon emblem: spread wings, square head, horns and long tail.
  def poly(points,c=gold):d.polygon([(round(x*S),round(y*S)) for x,y in points],fill=c)
  poly([(35.7,25.4),(34.1,23.7),(32.3,24),(32.6,27.7),(33.3,26.5),(34.2,27.2),(34.7,25.9),(35.7,27.5)])
  poly([(36.3,25.4),(37.9,23.7),(39.7,24),(39.4,27.7),(38.7,26.5),(37.8,27.2),(37.3,25.9),(36.3,27.5)])
  rect((35.4,24.7,36.6,28),gold);rect((35.2,24,36.8,25.3),gold)
  rect((35.2,23.6,35.6,24.3),gold);rect((36.4,23.6,36.8,24.3),gold)
  rect((35.45,24.3,35.7,24.6),shadow);rect((36.3,24.3,36.55,24.6),shadow)
  line([(36,27.5),(36.5,28.7),(35.5,29.5),(34.5,29.1)],gold,2)
  for x in [35.1,36.6]:line([(x,27.1),(x-.25,28.3)],gold,2)
  highlight=(231,204,138,255)
  line([(32.7,24.3),(34.1,24.1),(35.5,25.6)],highlight)
  line([(39.3,24.3),(37.9,24.1),(36.5,25.6)],highlight)

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
 if (level=='primary' and grade==6) or (level=='secondary' and grade==5):varsity(level,skin)
 name=f'placeholder_{grade}{"p" if level=="primary" else "s"}.png' if grade else 'placeholder_school_npc.png'
 path=ASSETS/('students' if grade else '')/name;path.parent.mkdir(parents=True,exist_ok=True);im.save(path)
for level,count in [('primary',6),('secondary',5)]:
 for grade in range(1,count+1):generate(level,grade)
generate('primary',0)
print('Generated 11 classroom placeholders and NPC fallback, 256x256 RGBA; no geometry changes.')
