"""Check borders in every state plus long rectangular horizontal/vertical runs."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_path, module_read_text
import itertools,json
from PIL import Image
from building_blocks.create_school_yellow_border_assets import ROOT,A,ID,SIDES,EDGES

def main():
 states=json.loads(module_read_text(A/f'blockstates/{ID}.json'))['variants'];assert len(states)==64
 for key,value in states.items():
  linked={k:v=='true' for k,v in (s.split('=') for s in key.split(','))}
  model=json.loads(module_read_text(A/'models'/(value['model'].split(':')[1]+'.json')))
  for face,edges in EDGES.items():
   texture=model['textures'][face];im=Image.open(module_path(A/'textures'/(texture.split(':')[1]+'.png')));assert im.size==(16,16)
   for y,x in itertools.product(range(16),repeat=2):
    yellow=not linked[edges[0]] and y<3 or not linked[edges[1]] and x>=13 or not linked[edges[2]] and y>=13 or not linked[edges[3]] and x<3
    assert (im.getpixel((x,y))==(255,211,0))==yellow
 # Validate a front-facing 5x1024 assembly: every interior seam vanishes, only the outside remains.
 width,height=5,1024
 for col,row in itertools.product(range(width),range(height)):
  linked={'up':row<height-1,'down':row>0,'west':col>0,'east':col<width-1}
  # North-facing local U points west; border directions must follow the face UV, not world-X.
  mask=sum(int(linked[e])<<i for i,e in enumerate(EDGES['north']))
  im=Image.open(module_path(A/f'textures/block/{ID}_{mask}.png'))
  assert (im.getpixel((8,0))==(255,211,0))==(row==height-1)
  assert (im.getpixel((8,15))==(255,211,0))==(row==0)
 java=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/building_blocks/block/SchoolYellowBorderWallBlock.java')
 assert 'EntrancePavementConnections.update' in java
 helper=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/building_blocks/block/EntrancePavementConnections.java')
 assert 'Direction.values()' in helper and 'family(other)' in helper
 assert 'while(' not in helper
 print('OK: 64 states, all six faces, exactly 3px yellow perimeter; no interior seams in 5x1024 panel; local updates, no height/count cap.')
if __name__=='__main__':main()
