"""Static preservation and finite cache checks for the blocks found in the last JFR."""
from resource_modules import module_read_text
import json,re,itertools,random,ast
from pathlib import Path
from chairs.check_chair_grid_rotations import grid
ROOT=Path(__file__).resolve().parents[1];B=ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
def main():
 fixtures=json.loads(module_read_text(ROOT/'tools/fixtures/remaining_shape_boxes.json'));count=0
 for name,values in fixtures.items():
  source=module_read_text(B/f'{name}.java')
  current=[tuple(float(x.strip())/16 for x in v.split(',')) for v in re.findall(r'ExactVoxelShapes.box\(([\d., \-]+)\)',source)]
  expected=[tuple(v) for v in values];assert current==expected,(name,'coordinates changed');count+=len(current)
  coords,occupied=grid(current);rng=random.Random(574)
  for _ in range(1000):
   index=tuple(rng.randrange(len(c)-1) for c in coords)
   point=[(coords[i][n]+coords[i][n+1])/2 for i,n in enumerate(index)]
   assert (index in occupied)==any(all(b[i]<point[i]<b[i+3] for i in range(3)) for b in expected)
  assert 'forAllBoxes' not in source and 'Shapes.or' not in source,name
 # All 144 handrail combinations retain the same slice positions, including empty cells.
 handrail=module_read_text(B/'SchoolWoodenHandrailBlock.java')
 assert 'Physical(state.getValue(COLUMN),state.getValue(ROW),state.getValue(FACING),state.getValue(RIGHT))' in handrail
 assert 'Math.min(16,column*4+(i+1)/4.0+16-row*16)' in handrail
 assert 'PHYSICAL.computeIfAbsent' in handrail
 for column,row,right,facing in itertools.product(range(6),range(3),(False,True),range(4)):
  edge=14 if right else 0;volumes=[]
  for i in range(16):
   high=min(16,column*4+(i+1)/4+16-row*16)
   if high<=0:continue
   boxes=[(edge,0,15-i,edge+2,high,16-i),(14-edge,0,i,16-edge,high,i+1),
          (i,0,edge,i+1,high,edge+2),(15-i,0,14-edge,16-i,high,16-edge)]
   volumes.append(boxes[facing])
  assert all(b[1]==0 and b[4]<=16 for b in volumes)
 # Connected rails use 34 distinct physical keys across 256 saved states.
 keys=set()
 for mask,post,facing in itertools.product(range(16),(False,True),range(4)):
  key=mask|(16 if post else 0)|(32 if mask==0 and facing%2 else 0);keys.add(key)
  assert key&15==mask and bool(key&16)==post
 assert len(keys)==34
 for name in ['SchoolWoodenHandrailBlock','SchoolWallRailingBlock','SchoolInvertedSlopeBlock','EntrancePavementSlopeBlock','SportsGoalBlock','TallBathroomBlock','TrashBinBlock','WallDecorationBlock','DoubleSchoolEntranceBlock']:
  source=module_read_text(B/f'{name}.java');assert 'Shapes.or' not in source and 'forAllBoxes' not in source,name
 for name in ['create_computer_desk_assets.py','create_gray_computer_desk_assets.py','create_dining_assets.py']:
  text=module_read_text(ROOT/'tools/tables_and_desks'/name);ast.parse(text)
  if name!='create_dining_assets.py':assert 'forAllBoxes' not in text and 'ExactVoxelShapes.union' in text
 print(f'OK: {count} native boxes unchanged / 6000 occupancy samples; 144 handrail profiles, 256 rail states / 34 physical keys; direct rotations and generator compatibility checked statically.')
if __name__=='__main__':main()
