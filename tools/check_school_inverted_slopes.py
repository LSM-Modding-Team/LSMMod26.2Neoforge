"""Static regression of exact wall-only Y reflection, start and profile continuity."""
import json,math
import numpy as np
from create_school_inverted_slopes import A,ROOT,pieces
from check_school_railing_slopes import mesh
from create_school_railing_slopes import pieces as originals

def main():
 plan=list(pieces());old=list(originals());assert len(plan)==10
 for (ident,col,row,low,rise),original in zip(plan,old):
  source_id,source_col,source_row,source_low,source_rise,post=original
  assert col==source_col and row==-source_row and rise==source_rise
  v,uv,faces=mesh(A/f'models/block/{ident}.obj');source,_,_=mesh(A/f'models/block/{source_id}.obj')
  wall=source[:24].copy();wall[:,1]=.75-wall[:,1];assert np.allclose(v,wall)
  assert len(v)==24 and len(faces)==6 and all(material=='wall' for material,_ in faces)
  assert uv.min()>=-1e-10 and uv.max()<=1+1e-10
  center=v.mean(axis=0);volume=0
  for _,indices in faces:
   p=v[indices];n=np.cross(p[1]-p[0],p[2]-p[0]);assert np.linalg.norm(n)>1e-9
   assert n@(p.mean(axis=0)-center)>0
   for j in range(1,len(p)-1):volume+=p[0]@np.cross(p[j],p[j+1])/6
  assert math.isclose(volume,source_low+rise/2,abs_tol=1e-8)
  assert np.isclose(v[:,1].max(),.75) and np.isclose(v[:,1].min(),low-rise)
  states=json.loads((A/f'blockstates/{ident}.json').read_text())['variants'];assert len(states)==4
  model=json.loads((A/f'models/block/{ident}.json').read_text());assert set(model['textures'])=={'wall','particle'}
  assert (A/f'items/{ident}.json').is_file()
  # Exact collision envelope differs from the inclined underside by <= half a pixel.
  assert rise/32<=1/32
 for group in (plan[:2],plan[2:4],plan[4:]):
  for first,second in zip(group,group[1:]):assert math.isclose(first[2]+first[3]-first[4],second[2]+second[3],abs_tol=1e-9)
 assert plan[0][2]+plan[0][3]==0 and plan[4][2]+plan[4][3]==0
 assert math.isclose(plan[3][2]+plan[3][3]-plan[3][4],-5)
 assert math.isclose(plan[-1][2]+plan[-1][3]-plan[-1][4],-4)
 s=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolInvertedSlopeBlock.java').read_text()
 assert 'SimpleBlockOutline.forState' in s and 'protected VoxelShape getCollisionShape' in s
 assert 'HALF' not in s and 'SchoolWallRailingBlock' not in s
 print('OK: 10 new wall-only inverted pieces, 80 states, exact Y reflection, Y=0 start, winding/UV/volumes and continuous endpoints.')
if __name__=='__main__':main()
