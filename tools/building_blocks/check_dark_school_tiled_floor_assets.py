"""Check 24px tiles across block boundaries and negative world coordinates."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_exists, module_path, module_read_text
import itertools,json
from PIL import Image
from building_blocks.create_school_yellow_border_assets import ROOT,A,ID

def main():
 assert ID=='school_entrance_pavement'
 plain=Image.open(module_path(A/f'textures/block/{ID}_15.png'))
 for y,x in itertools.product(range(16),repeat=2):assert all(abs(c-b)<=3 for c,b in zip(plain.getpixel((x,y)),(135,141,150)))
 tiles={(x,z):Image.open(module_path(A/f'textures/block/dark_school_tiled_floor_{x}_{z}.png')) for x,z in itertools.product(range(3),repeat=2)}
 assert len(json.loads(module_read_text(A/'blockstates/dark_school_tiled_floor.json'))['variants'])==9
 for bx,bz in itertools.product(range(-4,5),repeat=2):
  tile=tiles[bx%3,bz%3]
  for y,x in itertools.product(range(16),repeat=2):
   joint=(bx*16+x)%24==0 or (bz*16+y)%24==0
   assert (tile.getpixel((x,y))==(113,114,110))==joint
 java=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/building_blocks/block/BathroomFloorBlock.java');assert 'Math.floorMod' in java
 assert not module_exists(A/'blockstates/school_yellow_border_wall.json')
 from facilities.check_wide_bathroom_assets import main as check_wide
 check_wide()
 print('OK: pavement base #878d96, 9 floor phases, exactly 24px joints across positive/negative coordinates; old grey reused, old resource ID removed.')
if __name__=='__main__':main()
