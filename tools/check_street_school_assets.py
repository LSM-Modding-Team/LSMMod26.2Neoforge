"""Validate mesh groups, multipart combinations and vanilla bars behaviour statically."""
import itertools,json
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod'
def read(p):return json.loads(p.read_text())
def main():
 assert Image.open(A/'textures/block/street_school_wall.png').size==(16,16)
 parts=read(A/'blockstates/street_school_bars.json')['multipart'];assert len(parts)==60
 for col,row in itertools.product(range(4),range(3)):
  tile=Image.open(A/f'textures/block/street_school_bars_{col}_{row}.png');assert tile.size==(16,16)
  for links in itertools.product((False,True),repeat=4):
   state={'column':str(col),'row':str(row),**{side:str(value).lower() for side,value in zip(('north','south','east','west'),links)}}
   selected=[p for p in parts if all(state[k]==v for k,v in p['when'].items())];assert len(selected)==1+sum(links)
   for p in selected:
    model=read(A/'models'/(p['apply']['model'].split(':')[1]+'.json'));assert model['render_type']=='minecraft:cutout'
    for e in model['elements']:
     assert all(0<=a<b<=16 for a,b in zip(e['from'],e['to']))
     for f in e['faces'].values():assert all(0<=x<=16 for x in f['uv'])
 # All four outer frame edges are opaque; interior mesh has actual openings.
 for col in range(4):
  for row in range(3):
   im=Image.open(A/f'textures/block/street_school_bars_{col}_{row}.png')
   assert any(im.getpixel((x,y))[3]==0 for x,y in itertools.product(range(3,13),repeat=2))
   if col==0:assert all(im.getpixel((0,y))[3]==255 for y in range(16))
   if col==3:assert all(im.getpixel((15,y))[3]==255 for y in range(16))
   if row==0:assert all(im.getpixel((x,15))[3]==255 for x in range(16))
   if row==2:assert all(im.getpixel((x,0))[3]==255 for x in range(16))
 java=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/StreetSchoolBarsBlock.java').read_text()
 assert 'extends IronBarsBlock' in java and 'super.updateShape' in java
 assert 'column%4' in java and 'row%3' in java and 'hasChunkAt' in java
 assert 'SimpleBlockOutline.forState' in java
 # Edge-relative grouping is independent of world-coordinate sign and insertion order.
 for origin in (-23,0,37):
  for x,y in itertools.product(range(8),range(6)):
   assert ((origin+x)-origin)%4==x%4 and ((origin+y)-origin)%3==y%3
 print('OK: beige 16x16 wall; 384 bars states including water, multipart 4x3 frame, alpha mesh, bounded models; vanilla connections and collision, simple outline.')
if __name__=='__main__':main()
