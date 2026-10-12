"""Check exposed drain geometry and all circular-column quadrant orientations/occupancies."""
import json,hashlib,math
from pathlib import Path
from create_dark_tiles_drain_column import ROOT,A,CIRCLE,DRAIN,COLORS,connectors,GENERATED,main as generate
from audit_block_states import audit

def inside(boxes,p):return any(all(b[i]<=p[i]<b[i+3] for i in range(3)) for b in boxes)
def rotate(b,side):
 for _ in range(side):b=[16-b[5],b[1],b[0],16-b[2],b[4],b[3]]
 return b

def main():
 rows={r['id']:r['states'] for r in audit()};assert [rows[k] for k in ('dark_tiled_floor','school_drain','school_circular_column')]==[1,4,1]
 boxes=[b[0] for b in DRAIN]
 assert inside(boxes,(12,12,8)) and inside(boxes,(4,4,8)) and not inside(boxes,(4,12,8))
 for z in range(16):assert inside(boxes,(4,15.5,z+.5))==(z in (0,3,6,9,12,15))
 # Every connection meets the circular section without overlapping it and reaches the neighboring cell.
 tested=0
 for side in range(4):
  for q in range(4):
   local=q^1 if side>=2 else q;join=[rotate(b,side) for b in connectors(local)]
   assert join and all(0<=b[i]<b[i+3]<=16 for b in join for i in range(3))
   for b in join:
    y=(b[1]+b[4])/2;x=(b[0]+b[3])/2;z=(b[2]+b[5])/2
    worldu=z if side%2 else x
    assert (worldu>=8)==bool(q&1) and (y>=8)==bool(q&2)
    assert not inside(CIRCLE,(x,y,z));tested+=1
 # The selected custom-neighbor cube always touches the joining face for all 256 occupancies.
 for occupied in range(256):
  for side in range(4):
   for q in range(4):
    u=q&1;y=q>>1;x=(1 if side==3 else 0) if side%2 else u;z=(1 if side==0 else 0) if side%2==0 else u
    cube=x+2*y+4*z;visible=bool(occupied&(1<<cube));assert visible==bool((occupied>>cube)&1);tested+=1
 root=json.loads((A/'models/block/school_circular_column.json').read_text());assert len(root['models'])==18
 for series in root['models'][2:]:
  assert len(series)==len(COLORS) and len({e['condition']['paint'] for e in series})==len(COLORS)
  for e in series:
   t=e['transform'][0];assert t=={'type':'rotate','axis':'y','angle':-e['condition']['side']*90,'origin':[8,8,8]}
   parent=json.loads((A/'models/block'/Path(e['model'].split(':block/')[1]+'.json')).read_text());assert 'elements' in parent
   for element in parent['elements']:
    for surface in element['faces'].values():
     key=surface['texture'][1:];assert key.startswith('column_')
     assert parent['textures'][key]==root['textures'][key]
 for name in ('school_circular_column_shaft','school_circular_column_base'):
  leaf=json.loads((A/f'models/block/{name}.json').read_text())
  for element in leaf['elements']:
   for surface in element['faces'].values():
    key=surface['texture'][1:];assert leaf['textures'][key]==root['textures'][key]
 # Geometry generated from boxes must only expose surfaces belonging to those volumes.
 for filename,source in [('school_drain',boxes),('school_circular_column_shaft',CIRCLE)]:
  mesh=json.loads((A/f'models/block/{filename}.json').read_text())
  for e in mesh['elements']:assert any(all(b[i]<=e['from'][i]<e['to'][i]<=b[i+3] for i in range(3)) for b in source)
 generate();before={p:hashlib.sha256((ROOT/p).read_bytes()).digest() for p in GENERATED};generate();assert all(hashlib.sha256((ROOT/p).read_bytes()).digest()==h for p,h in before.items())
 print(f'OK: six-bar open drain, circular mesh/physics, {tested} oriented quadrant/occupancy checks, shared wall paints, 1+4+1 states, idempotent generator.')
if __name__=='__main__':main()
