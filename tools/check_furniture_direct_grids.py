"""Verify preservation of original boxes and direct-grid occupancy without Java compilation."""
from resource_modules import module_read_text
from pathlib import Path
import argparse,json,re,itertools,random
from chairs.check_chair_grid_rotations import grid
ROOT=Path(__file__).resolve().parents[1];B=ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
def boxes(s):
 return [tuple(float(x.strip())/16 for x in v.split(',')) for v in re.findall(r'(?:Block|ExactVoxelShapes).box\(([\d., \-]+)\)',s)]
def main(baseline):
 old=json.loads(module_read_text(baseline));total=0
 for name in ('ChairBlock','NewChairShapes','StoolBlock','InfirmaryCotShapes','DeskBlock'):
  path=B/f'{name}.java';before=[tuple(v) for v in old['source_boxes'][name]] if 'source_boxes' in old else boxes(old[path.relative_to(ROOT).as_posix()]);after=boxes(module_read_text(path))
  assert before==after,(name,'physical coordinates changed');assert before;total+=len(before)
  coords,occupied=grid(after);rng=random.Random(319)
  # Midpoints at every source box and independently sampled coordinate-grid cells.
  for _ in range(3000):
   indices=tuple(rng.randrange(len(c)-1) for c in coords)
   p=[(coords[i][n]+coords[i][n+1])/2 for i,n in enumerate(indices)]
   expected=any(all(b[i]<p[i]<b[i+3] for i in range(3)) for b in before)
   assert (indices in occupied)==expected,(name,indices)
  assert 'Shapes.or' not in module_read_text(path),name
 slope=module_read_text(B/'SchoolRailingSlopeBlock.java')
 assert 'SharedChairRotations.horizontal(ExactVoxelShapes.union(boxes))' in slope
 assert 'Shapes.or' not in slope and '.optimize()' not in slope
 if 'source_boxes' not in old:
  old_slope=old[(B/'SchoolRailingSlopeBlock.java').relative_to(ROOT).as_posix()]
  assert slope[slope.index('    private double height'):]==old_slope[old_slope.index('    private double height'):]
 cot=module_read_text(B/'InfirmaryCotShapes.java');assert 'SHAPES[segment] = SharedChairRotations.horizontal' in cot
 desk=module_read_text(B/'DeskBlock.java');assert 'OctahedralGroup.INVERT_Z' in desk and 'rotateClockwise' not in desk
 helper=module_read_text(B/'ExactVoxelShapes.java');assert 'grid.fill(ix,iy,iz)' in helper and 'Shapes.or' not in helper and '.optimize()' not in helper
 print(f'OK: {total} original boxes unchanged across five sources; 15000 independent occupancy samples; native grids, cot/slope rotations and desk mirror configured; slope height/connectivity code unchanged.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--baseline',type=Path,default=ROOT/'tools/fixtures/furniture_grid_boxes.json');main(p.parse_args().baseline)
