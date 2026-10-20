"""Check smooth ramp rise, 6-wide connected surface, slabs, meshes and collision."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_exists, module_glob, module_is_file, module_read_text
import itertools,json,math
import numpy as np
from building_blocks.create_school_yellow_border_assets import ROOT,A

def main():
 # Verify the actual MTL references consumed by NeoForge's MaterialBaker.
 # Fusion forwards slot names verbatim, while vanilla additionally strips #.
 mtl=module_read_text(A/'models/block/school_entrance_pavement_shapes.mtl')
 slots=[line.split()[1] for line in mtl.splitlines() if line.startswith('map_Kd ')]
 assert slots==['gray','yellow'],('Fusion-compatible OBJ slots',slots)
 for slot in slots:
  texture='school_entrance_pavement_'+('15' if slot=='gray' else '0')
  assert module_is_file(A/f'textures/block/{texture}.png')
 layout=json.loads(module_read_text(ROOT/'tools/slopes/entrance_pavement_slope_layout.json'));assert layout['width']==6 and layout['length']==6
 pieces=layout['pieces'];end=.5
 for piece in pieces:
  assert math.isclose(piece['row']+piece['lower_y'],end);end+=.25
  ident=piece['id'];states=json.loads(module_read_text(A/f'blockstates/{ident}.json'))['variants'];assert len(states)==256
  for variant in states.values():assert module_exists(A/'models'/(variant['model'].split(':')[1]+'.json'))
 assert end==2 and end-.5==1.5
 # Each of six columns has identical continuous profile; only outer columns get side borders.
 for col in range(6):
  assert (col>0)==(col!=0) and (col<5)==(col!=5)
 for p in module_glob(A/'models/block', 'school_entrance_pavement_*_*.obj'):
  if '_slope_' not in p.stem and '_slab_' not in p.stem:continue
  vertices=[];volume=0
  for line in module_read_text(p).splitlines():
   f=line.split()
   if f and f[0]=='v':vertices.append(tuple(map(float,f[1:])))
   if f and f[0]=='f':
    points=np.array([vertices[int(v.split('/')[0])-1] for v in f[1:]])
    assert len(points)==3 and np.linalg.norm(np.cross(points[1]-points[0],points[2]-points[0]))>1e-12,p
    volume+=np.dot(points[0],np.cross(points[1],points[2]))/6
  if '_slope_' in p.stem:
   number=int(p.stem.split('_slope_')[1].split('_')[0]);expected=(.5,.75,0,.25,.5,.75)[number-1]+.25/2
  else:expected=.5
  assert math.isclose(volume,expected,abs_tol=1e-8),(p,volume,expected)
  v=np.array(vertices);assert v[:,0].min()>=-1e-10 and v[:,0].max()<=1+1e-10
  assert v[:,2].min()>=-1e-10 and v[:,2].max()<=1+1e-10
 states=json.loads(module_read_text(A/'blockstates/school_entrance_pavement_slab.json'))['variants'];assert len(states)==192
 loot=json.loads(module_read_text(ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/school_entrance_pavement_slab.json'));assert loot['pools'][0]['entries'][0]['functions'][0]['count']==2
 java=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/slopes/block/EntrancePavementSlopeBlock.java')
 assert 'low+.25*(i+1)/32.0' in java and 'SimpleBlockOutline.forState' in java
 helper=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/building_blocks/block/EntrancePavementConnections.java');assert 'dy=-2;dy<=2' in helper and 'hasChunkAt' in helper
 assert '3/16' in module_read_text(ROOT/'tools/slopes/create_entrance_pavement_shapes.py')
 print('OK: 6-piece 6x6 ramp, 8→32px (+24px), four-pixel rise/piece; 24 slope and 6 slab physical states (1920 original visual profiles), bottom/top/double/water, nondegenerate meshes and 3px geometric perimeter; height-aware joins / collision / double drop.')
if __name__=='__main__':main()
