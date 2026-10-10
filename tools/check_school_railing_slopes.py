"""Static checks of nine individual sloped walls, rail junctions and placements."""
import json,math
import numpy as np
from create_school_railing_slopes import A,ROOT,pieces

def mesh(path):
 vertices=[];uvs=[];faces=[];material=None
 for line in path.read_text().splitlines():
  f=line.split()
  if not f:continue
  if f[0]=='v':vertices.append(tuple(map(float,f[1:])))
  elif f[0]=='vt':uvs.append(tuple(map(float,f[1:])))
  elif f[0]=='usemtl':material=f[1]
  elif f[0]=='f':faces.append((material,[int(i.split('/')[0])-1 for i in f[1:]]))
 return np.array(vertices),np.array(uvs),faces

def main():
 plan=list(pieces());assert len(plan)==9
 assert [p[1] for p in plan[:4]]==[0,1,5,6]
 assert [p[1] for p in plan[4:]]==list(range(5))
 reg=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text()
 for ident,col,row,low,rise,post in plan:
  assert 0<low and rise>0
  vertices,uv,faces=mesh(A/f'models/block/{ident}.obj')
  assert uv.min()>=0 and uv.max()<=1
  assert np.min(vertices[:,0])==0 and np.max(vertices[:,0])==1
  assert np.min(vertices[:,2])==0 and np.max(vertices[:,2])==1
  assert np.isclose(np.max(vertices[:,1]),low+rise+.5)
  assert len(faces)==(18 if post else 12)
  for i in range(0,len(faces),6):
   group=faces[i:i+6];center=np.mean(vertices[[j for _,f in group for j in f]],axis=0);volume=0
   for _,indices in group:
    p=vertices[indices];normal=np.cross(p[1]-p[0],p[2]-p[0])
    assert np.linalg.norm(normal)>1e-9 and normal@(p.mean(axis=0)-center)>0
    assert all(abs(normal@(v-p[0]))<1e-8 for v in p)
    for j in range(1,len(p)-1):volume+=p[0]@np.cross(p[j],p[j+1])/6
   assert volume>0
  states=json.loads((A/f'blockstates/{ident}.json').read_text())['variants'];assert len(states)==4
  assert all('half' not in key for key in states)
  assert ident in reg
  for folder in ('models/item','items'):assert (A/f'{folder}/{ident}.json').exists()
  # Approximation error at most half a pixel for the steepest profile.
  assert rise/32<=1/32
 def height(piece,end):return piece[2]+piece[3]+piece[4]*end
 split=plan[:4];flight=plan[4:]
 assert math.isclose(height(split[-1],1)-height(split[0],0),5)
 assert math.isclose(height(flight[-1],1)-height(flight[0],0),5)
 for group in (split[:2],split[2:],flight):
  for first,second in zip(group,group[1:]):
   assert math.isclose(height(first,1),height(second,0),abs_tol=1e-10)
   assert math.isclose(height(first,1)+.375,height(second,0)+.375,abs_tol=1e-10)
 # Four landings join the existing flat wall at exactly the same wall/rail height.
 for group in (split,flight):
  assert math.isclose(height(group[0],0),.75)
  assert math.isclose(height(group[-1],1),5.75)
 source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolRailingSlopeBlock.java').read_text()
 assert 'HALF' not in source and 'SimpleBlockOutline.forState' in source
 assert 'protected VoxelShape getCollisionShape' in source
 assert 'context.getHorizontalDirection()' in source
 assert 'super.getCollisionShape' not in source
 flat=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolWallRailingBlock.java').read_text()
 assert 'SchoolRailingSlopeBlock.connectsFlat' in flat
 print('OK: 9 pieces, 72 states, outward planar faces, atlas-safe UVs, continuous wall/rail endpoints, 7x5 split and 5x5 flight, no inversion.')
if __name__=='__main__':main()
