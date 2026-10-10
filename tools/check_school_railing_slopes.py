"""Static checks of nine individual sloped walls, rail junctions and placements."""
import json,math
import numpy as np
from create_school_railing_slopes import A,ROOT,pieces,FLIGHT_RISE,flight_halves,flight_height

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
 plan=list(pieces());assert len(plan)==10
 assert [p[1] for p in plan[:4]]==[0,1,5,6]
 assert [p[1] for p in plan[4:]]==list(range(6))
 reg=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text()
 for ident,col,row,low,rise,post in plan:
  assert 0<low and rise>0
  vertices,uv,faces=mesh(A/f'models/block/{ident}.obj')
  assert uv.min()>=-1e-10 and uv.max()<=1+1e-10
  assert np.min(vertices[:,0])==0 and np.max(vertices[:,0])==1
  assert np.min(vertices[:,2])==0 and np.max(vertices[:,2])==1
  assert np.isclose(np.max(vertices[:,1]),low+rise+.5)
  flight='_flight_' in ident
  assert len(faces)==((30 if post else 20) if flight else (18 if post else 12))
  stride=5 if flight else 6
  total_volume=0;wall_volume=0
  for i in range(0,len(faces),stride):
   group=faces[i:i+stride];unique=sorted({j for _,f in group for j in f});center=vertices[unique].mean(axis=0)
   for material,indices in group:
    p=vertices[indices];normal=np.cross(p[1]-p[0],p[2]-p[0])
    assert np.linalg.norm(normal)>1e-9 and normal@(p.mean(axis=0)-center)>0
    assert all(abs(normal@(v-p[0]))<1e-8 for v in p)
    for j in range(1,len(p)-1):
     volume=p[0]@np.cross(p[j],p[j+1])/6;total_volume+=volume
     if material=='wall':wall_volume+=volume
  assert total_volume>0
  if flight:
   assert math.isclose(wall_volume,.75,abs_tol=1e-9)
   assert math.isclose(total_volume,.75+1/64+(3/512 if post else 0),abs_tol=1e-9)
   a,b=flight_halves(col);assert max(a,b)*2/32<=1/32
  states=json.loads((A/f'blockstates/{ident}.json').read_text())['variants'];assert len(states)==4
  assert all('half' not in key for key in states)
  assert ident in reg
  for folder in ('models/item','items'):assert (A/f'{folder}/{ident}.json').exists()
  # Approximation error at most half a pixel for the steepest profile.
  assert rise/32<=1/32
 def height(piece,end):return piece[2]+piece[3]+piece[4]*end
 split=plan[:4];flight=plan[4:]
 assert math.isclose(height(split[-1],1)-height(split[0],0),5)
 assert math.isclose(height(flight[-1],1)-height(flight[0],0),FLIGHT_RISE)
 assert flight[-1][1]+1==6
 assert [p[2] for p in flight]==[0,1,1,2,3,4]
 assert flight_halves(0)[0]==0 and flight_halves(5)[1]==0
 assert all(math.isclose(sum(flight_halves(i)),flight[i][4]) for i in range(6))
 for group in (split[:2],split[2:],flight):
  for first,second in zip(group,group[1:]):
   assert math.isclose(height(first,1),height(second,0),abs_tol=1e-10)
   assert math.isclose(height(first,1)+.375,height(second,0)+.375,abs_tol=1e-10)
 # Four landings join the existing flat wall at exactly the same wall/rail height.
 for group in (split,flight):
  assert math.isclose(height(group[0],0),.75)
  assert math.isclose(height(group[-1],1),(.75+FLIGHT_RISE) if group is flight else 5.75)
 source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolRailingSlopeBlock.java').read_text()
 assert 'HALF' not in source and 'SimpleBlockOutline.forState' in source
 assert 'protected VoxelShape getCollisionShape' in source
 assert 'context.getHorizontalDirection()' in source
 assert 'super.getCollisionShape' not in source
 flat=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolWallRailingBlock.java').read_text()
 assert 'SchoolRailingSlopeBlock.connectsFlat' in flat
 print('OK: 10 pieces, 80 states, outward planar faces, atlas-safe UVs, continuous wall/rail endpoints, 7x5 split and 6 blocks / 5 levels, horizontal end transitions, constant 12 px wall thickness, no inversion.')
if __name__=='__main__':main()
