"""Static resource and geometry checks; does not compile or launch Minecraft."""
import json, math
from pathlib import Path
import numpy as np
from create_school_rise_assets import A, ROOT, STAIR, SLOPES, polygon

def mesh(path):
 vertices=[];faces=[];coords=[]
 for line in path.read_text().splitlines():
  f=line.split()
  if not f:continue
  if f[0]=='v':vertices.append(tuple(map(float,f[1:])))
  if f[0]=='vt':coords.append(tuple(map(float,f[1:])))
  if f[0]=='f':faces.append([tuple(int(x)-1 for x in v.split('/')) for v in f[1:]])
 return np.array(vertices),faces,coords

def main():
 template=json.loads((ROOT/'tools/vanilla_stair_blockstates.json').read_text())
 for v in template['variants'].values():v['model']=v['model'].replace('minecraft:block/oak_stairs','lsmmod:block/'+STAIR)
 assert json.loads((A/f'blockstates/{STAIR}.json').read_text())==template
 assert len(template['variants'])==40
 registry=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text()
 assert 'SchoolRiseBlock' not in registry
 for ident,offset in SLOPES.items():
  states=json.loads((A/f'blockstates/{ident}.json').read_text())['variants'];assert len(states)==8
  for top in (False,True):
   name=ident+('_top' if top else '')
   model=json.loads((A/f'models/block/{name}.json').read_text());assert model['loader']=='neoforge:obj'
   assert model['textures']['wall']=='lsmmod:block/light_school_wall'
   v,faces,uv=mesh(A/f'models/block/{name}.obj')
   assert np.min(v)>=0 and np.max(v)<=1
   center=np.mean(v,axis=0);volume=0
   for indices in faces:
    points=v[[i[0] for i in indices]]
    normal=np.cross(points[1]-points[0],points[2]-points[0]);assert np.linalg.norm(normal)>1e-8
    assert normal@(points.mean(axis=0)-center)>1e-8
    assert all(abs(normal@(p-points[0]))<1e-8 for p in points)
    for i in range(1,len(points)-1):volume+=points[0]@np.cross(points[i],points[i+1])/6
   poly=polygon(offset);area=abs(sum(a[0]*b[1]-b[0]*a[1] for a,b in zip(poly,poly[1:]+poly[:1])))/2
   assert math.isclose(volume,area,abs_tol=1e-8)
   if top:
    original,_,_=mesh(A/f'models/block/{ident}.obj');original[:,1]=1-original[:,1];assert np.allclose(original,v)
  assert ident in registry
 for i in range(1001):
  t=i/1000;h=min(1,t+.625)+max(0,t-.375);assert math.isclose(h,t+.625,abs_tol=1e-8)
  for offset in SLOPES.values():
   actual=max(0,min(1,t+offset));sample=max(0,min(1,math.ceil(t*32)/32+offset));assert 0<=sample-actual<=1/32+1e-9
 for cls in ('SchoolTiledStairBlock','SchoolSlopeBlock'):
  assert 'SimpleBlockOutline.forState' in (ROOT/f'src/main/java/net/nicomar2009/lsmmod/block/{cls}.java').read_text()
 tiled=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolTiledStairBlock.java').read_text()
 assert 'protected VoxelShape getCollisionShape(' in tiled
 assert 'return super.getShape(state,level,pos,context);' in tiled
 for ident in (STAIR,*SLOPES):
  for folder in ('items','models/item','blockstates'):assert (A/f'{folder}/{ident}.json').is_file()
  assert (ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json').is_file()
 for p in (ROOT/'src/main/resources').rglob('*.json'):
  if p.name.startswith(('school_stairs_','school_slope_')):assert p.stem in {n+s for n in SLOPES for s in ('','_top')}
 print('OK: 1 vanilla stair, 3 slopes; 128 states; outward planar meshes, inversion, volumes and collision approximation.')
if __name__=='__main__':main()
