"""Static exact-grid rotation checks for the declared chair collision profiles."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_exists, module_read_text
from pathlib import Path
import re,bisect,itertools
ROOT=Path(__file__).resolve().parents[2];B=ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
def grid(boxes):
 coords=[sorted({b[i] for b in boxes}|{b[i+3] for b in boxes}) for i in range(3)]
 filled=set()
 for b in boxes:
  ranges=[range(bisect.bisect_left(coords[i],b[i]),bisect.bisect_left(coords[i],b[i+3])) for i in range(3)]
  filled.update(itertools.product(*ranges))
 return coords,filled
def main():
 profiles=[]
 for file in ('ChairBlock.java','NewChairShapes.java','StoolBlock.java'):
  p=B/file
  if not module_exists(p):continue
  for name,body in re.findall(r'(?:public|private) static final VoxelShape (\w+) = (?:Shapes.or|ExactVoxelShapes.union)\((.*?)\);',module_read_text(p),re.S):
   boxes=[tuple(float(x.strip())/16 for x in values.split(',')) for values in re.findall(r'(?:Block.box|ExactVoxelShapes.box)\(([\d., \-]+)\)',body)]
   assert boxes,(file,name);profiles.append((file,name,boxes))
 sofa=B/'SofaShapes.java'
 if module_exists(sofa):
  corners={}
  for name,body in re.findall(r'\{ // (\w+)\n(.*?)\n        \}',module_read_text(sofa),re.S):
   boxes=[tuple(float(x.strip())/16 for x in values.split(',')) for values in re.findall(r'\{([\d., \-]+)\}',body)]
   assert boxes,('SofaShapes.java',name)
   assert all(all(b[i]<b[i+3] for i in range(3)) for b in boxes),name
   # The seated player's hips fit in every corner; the opening under the seat stays empty.
   assert not any(all(b[i]<p[i]<b[i+3] for i in range(3)) for b in boxes for p in ((.5,.5,.5),(.5,.0625,.5))),name
   assert any(all(b[i]<=p[i]<=b[i+3] for i in range(3)) for b in boxes for p in ((.5,7/16,.5),)),name
   corners[name]=boxes;profiles.append(('SofaShapes.java',name,boxes))
  assert set(corners)=={'straight','inner_left','inner_right','outer_left','outer_right'}
  for left,right in (('inner_left','inner_right'),('outer_left','outer_right')):
   mirrored=[(1-b[3],b[1],b[2],1-b[0],b[4],b[5]) for b in corners[left]]
   assert grid(mirrored)==grid(corners[right]),(left,right,'mirror occupancy')
 count=0
 for file,name,boxes in profiles:
  coords,occupied=grid(boxes)
  original_coords,original_cells=coords,occupied
  for turn in range(4):
   # Direct index permutation and reversed Z coordinate axis, (x,z)->(1-z,x).
   x,y,z=coords;rotated_coords=[[1-v for v in reversed(z)],y,x]
   direct={(len(z)-2-iz,iy,ix) for ix,iy,iz in occupied}
   turned=[(1-b[5],b[1],b[0],1-b[2],b[4],b[3]) for b in boxes]
   expected_coords,expected=grid(turned)
   assert rotated_coords==expected_coords and direct==expected,(file,name,turn)
   coords,occupied,boxes=rotated_coords,direct,turned;count+=1
  assert occupied==original_cells
  assert all(abs(a-b)<1e-12 for old,new in zip(original_coords,coords) for a,b in zip(old,new))
 java=module_read_text(B/'SharedChairRotations.java');chair=module_read_text(B/'ChairBlock.java')
 assert all(f'OctahedralGroup.BLOCK_ROT_Y_{angle}' in java for angle in (90,180,270))
 assert 'Shapes.or' not in java and '.optimize()' not in java
 assert 'BY_IDENTITY' in java and 'BY_BOXES' in java and 'cached.clone()' in java
 assert 'SharedChairRotations.horizontal(nativeShape)' in chair and 'rotateClockwise' not in chair
 print(f'OK: {len(profiles)} native chair profiles / {count} quarter-turn checks; exact cell occupancy, holes and four-turn round trip; direct vanilla rotation and shared cache configured.')
if __name__=='__main__':main()
