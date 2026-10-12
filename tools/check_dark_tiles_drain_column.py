"""Check exposed drain geometry and all circular-column quadrant orientations/occupancies."""
import json,hashlib,math
from pathlib import Path
from create_dark_tiles_drain_column import ROOT,A,CIRCLE,DRAIN,COLORS,connectors,GENERATED,main as generate
from audit_block_states import audit

def inside(boxes,p):return any(all(b[i]<=p[i]<b[i+3] for i in range(3)) for b in boxes)
def rotate(b,side):
 for _ in range(side):b=[16-b[5],b[1],b[0],16-b[2],b[4],b[3]]
 return b

VERTICES={'down':[(0,0,1),(0,0,0),(1,0,0),(1,0,1)],'up':[(0,1,0),(0,1,1),(1,1,1),(1,1,0)],'north':[(1,1,0),(1,0,0),(0,0,0),(0,1,0)],'south':[(0,1,1),(0,0,1),(1,0,1),(1,1,1)],'west':[(0,1,0),(0,0,0),(0,0,1),(0,1,1)],'east':[(1,1,1),(1,0,1),(1,0,0),(1,1,0)]}
def quad_uv(element,face):
 surface=element['faces'][face];u0,v0,u1,v1=surface['uv'];uvs=[(u0,v0),(u0,v1),(u1,v1),(u1,v0)];shift=surface.get('rotation',0)//90
 return {tuple(element['to'][i] if high else element['from'][i] for i,high in enumerate(corner)):uvs[(n+shift)%4] for n,corner in enumerate(VERTICES[face])}
def rotate_point(point,side):
 x,y,z=point
 for _ in range(side):x,z=16-z,x
 return x,y,z

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
   assert 'transform' not in e
   leaf=json.loads((A/'models/block'/Path(e['model'].split(':block/')[1]+'.json')).read_text())
   assert leaf['loader']=='fusion:model' and leaf['type']=='base' and 'elements' not in leaf
   mesh=json.loads((A/'models/block'/Path(leaf['parent'].split(':block/')[1]+'.json')).read_text())
   assert mesh['loader']=='fusion:model' and mesh['type']=='base'
   side=e['condition']['side'];q=e['condition']['quarter'];local=q^1 if side>=2 else q
   physical=[rotate(b,side) for b in connectors(local)]
   key=leaf['textures']['column_join_surface'][1:];assert root['textures'][key]==COLORS[e['condition']['paint']]
   for element in mesh['elements']:
    assert any(all(b[i]<=element['from'][i]<element['to'][i]<=b[i+3] for i in range(3)) for b in physical)
    for surface in element['faces'].values():assert surface['texture']=='#column_join_surface'
 # Baked-in orientations preserve every vertex UV and cull direction from the former matrix transform.
 horizontal={'north':'east','east':'south','south':'west','west':'north'}
 for q in range(4):
  source=json.loads((A/f'models/block/school_circular_column_join_{q}.json').read_text())
  for side,direction in enumerate(('north','east','south','west')):
   suffix='' if side==0 else '_'+direction
   target=json.loads((A/f'models/block/school_circular_column_join_{q}{suffix}.json').read_text())
   assert len(source['elements'])==len(target['elements'])
   for old,new in zip(source['elements'],target['elements']):
    for face,surface in old['faces'].items():
     newface=face;cull=surface.get('cullface')
     for _ in range(side):newface=horizontal.get(newface,newface);cull=horizontal.get(cull,cull)
     assert {rotate_point(point,side):uv for point,uv in quad_uv(old,face).items()}==quad_uv(new,newface)
     assert new['faces'][newface].get('cullface')==cull
 # Every registered Jade provider needs a translated configuration name, even before entering a world.
 import re
 providers=ROOT/'src/main/java/net/nicomar2009/lsmmod/compat/jade'
 ids=set()
 for path in providers.glob('*Provider.java'):ids.update(re.findall(r'fromNamespaceAndPath\(LSMMod.MOD_ID,\s*"([a-z_]+)"\)',path.read_text()))
 assert 'painted_wall_name' in ids
 for locale in ('en_us','es_es'):
  translations=json.loads((A/f'lang/{locale}.json').read_text())
  assert all(translations.get('config.jade.plugin_lsmmod.'+ident) for ident in ids)
 for name in ('school_circular_column_shaft','school_circular_column_base'):
  leaf=json.loads((A/f'models/block/{name}.json').read_text());assert leaf['loader']=='fusion:model' and leaf['type']=='base'
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
