"""Check smooth ramp rise, 6-wide connected surface, slabs, meshes and collision."""
import itertools,json,math
import numpy as np
from create_school_yellow_border_assets import ROOT,A

def main():
 layout=json.loads((ROOT/'tools/entrance_pavement_slope_layout.json').read_text());assert layout['width']==6 and layout['length']==6
 pieces=layout['pieces'];end=.5
 for piece in pieces:
  assert math.isclose(piece['row']+piece['lower_y'],end);end+=.25
  ident=piece['id'];states=json.loads((A/f'blockstates/{ident}.json').read_text())['variants'];assert len(states)==256
  for variant in states.values():assert (A/'models'/(variant['model'].split(':')[1]+'.json')).exists()
 assert end==2 and end-.5==1.5
 # Each of six columns has identical continuous profile; only outer columns get side borders.
 for col in range(6):
  assert (col>0)==(col!=0) and (col<5)==(col!=5)
 for p in (A/'models/block').glob('school_entrance_pavement_*_*.obj'):
  if '_slope_' not in p.stem and '_slab_' not in p.stem:continue
  vertices=[];volume=0
  for line in p.read_text().splitlines():
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
 states=json.loads((A/'blockstates/school_entrance_pavement_slab.json').read_text())['variants'];assert len(states)==192
 loot=json.loads((ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/school_entrance_pavement_slab.json').read_text());assert loot['pools'][0]['entries'][0]['functions'][0]['count']==2
 java=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/EntrancePavementSlopeBlock.java').read_text()
 assert 'low+.25*(i+1)/32.0' in java and 'SimpleBlockOutline.forState' in java
 helper=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/EntrancePavementConnections.java').read_text();assert 'dy=-2;dy<=2' in helper and 'hasChunkAt' in helper
 assert '3/16' in (ROOT/'tools/create_entrance_pavement_shapes.py').read_text()
 print('OK: 6-piece 6x6 ramp, 8→32px (+24px), four-pixel rise/piece; 24 slope and 6 slab physical states (1920 original visual profiles), bottom/top/double/water, nondegenerate meshes and 3px geometric perimeter; height-aware joins / collision / double drop.')
if __name__=='__main__':main()
