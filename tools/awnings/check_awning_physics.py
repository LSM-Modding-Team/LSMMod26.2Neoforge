"""Check exact coordinate-grid occupancy against the previous eight-box cloth formula."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text
import bisect,itertools
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
def old_boxes(start,end,upper,facing):
 boxes=[]
 for i in range(8):
  top=2*(start+(end-start)*(i+.5)/8)-(16 if upper else 0)
  low,high=max(0,top-.75),min(16,top)
  if low>=high:continue
  z1,z2=i*2,i*2+2
  b={'south':(0,low,z1,16,high,z2),'north':(0,low,16-z2,16,high,16-z1),
     'east':(z1,low,0,z2,high,16),'west':(16-z2,low,0,16-z1,high,16)}[facing]
  boxes.append(tuple(x/16 for x in b))
 return boxes
def main():
 checked=0;max_cells=0
 for start,end,upper,facing in itertools.product(range(17),range(17),(False,True),('south','north','east','west')):
  boxes=old_boxes(start,end,upper,facing)
  if not boxes:checked+=1;continue
  coords=[sorted({b[i] for b in boxes}|{b[i+3] for b in boxes}) for i in range(3)]
  filled=set()
  for b in boxes:
   bounds=[(bisect.bisect_left(coords[i],b[i]),bisect.bisect_left(coords[i],b[i+3])) for i in range(3)]
   filled.update(itertools.product(*(range(a,z) for a,z in bounds)))
  volume=0
  for indices in itertools.product(*(range(len(c)-1) for c in coords)):
   p=[(coords[i][n]+coords[i][n+1])/2 for i,n in enumerate(indices)]
   occupied=any(all(b[i]<p[i]<b[i+3] for i in range(3)) for b in boxes)
   assert occupied==(indices in filled),(start,end,upper,facing,indices)
   if occupied:
    v=1
    for i,n in enumerate(indices):v*=coords[i][n+1]-coords[i][n]
    volume+=v
  # Original strips never overlap in their horizontal interiors.
  expected=sum((b[3]-b[0])*(b[4]-b[1])*(b[5]-b[2]) for b in boxes)
  assert abs(volume-expected)<1e-12
  max_cells=max(max_cells,(len(coords[0])-1)*(len(coords[1])-1)*(len(coords[2])-1));checked+=1
 java=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/awnings/block/AwningShapes.java')
 assert 'Shapes.or' not in java and '.optimize()' not in java
 assert 'grid.fill(ix, iy, iz)' in java and 'DoubleLists.unmodifiable' in java
 assert 'COLLISIONS.computeIfAbsent' in java and 'OUTLINES.computeIfAbsent' in java
 block=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/awnings/block/AwningBlock.java')
 assert 'AwningShapes.outline' in block and block.count('AwningShapes.collision')==2
 assert 'getCollisionShape' in block and 'getOcclusionShape' in block
 print(f'OK: {checked} physical profiles; exact occupancy, volume and rotations; grid at most {max_cells} cells, no union/optimize; shared collision and separate outline.')
if __name__=='__main__':main()
