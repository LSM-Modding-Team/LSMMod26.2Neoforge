"""Check 24px tiles across block boundaries and negative world coordinates."""
import itertools,json
from PIL import Image
from create_school_yellow_border_assets import ROOT,A,ID

def main():
 assert ID=='school_entrance_pavement'
 plain=Image.open(A/f'textures/block/{ID}_15.png')
 for y,x in itertools.product(range(16),repeat=2):assert all(abs(c-b)<=3 for c,b in zip(plain.getpixel((x,y)),(135,141,150)))
 tiles={(x,z):Image.open(A/f'textures/block/bathroom_floor_{x}_{z}.png') for x,z in itertools.product(range(3),repeat=2)}
 assert len(json.loads((A/'blockstates/bathroom_floor.json').read_text())['variants'])==9
 for bx,bz in itertools.product(range(-4,5),repeat=2):
  tile=tiles[bx%3,bz%3]
  for y,x in itertools.product(range(16),repeat=2):
   joint=(bx*16+x)%24==0 or (bz*16+y)%24==0
   assert (tile.getpixel((x,y))==(113,114,110))==joint
 java=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/BathroomFloorBlock.java').read_text();assert 'Math.floorMod' in java
 assert not (A/'blockstates/school_yellow_border_wall.json').exists()
 print('OK: pavement base #878d96, 9 floor phases, exactly 24px joints across positive/negative coordinates; old grey reused, old resource ID removed.')
if __name__=='__main__':main()
