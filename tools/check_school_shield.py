"""Static 4x4 slab placement, controller/drop and texture stitching checks."""
from resource_modules import module_path, module_read_text
import json
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
def main():
 s=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/special_blocks/block/SchoolShieldBlock.java')
 assert 'IntegerProperty.create("column", 0, 3)' in s and 'IntegerProperty.create("row", 0, 3)' in s
 assert 'Block.box(0, 0, 0, 16, 8, 16)' in s
 assert 'row < 3' not in s and 'column < 3' not in s
 assert 'SimpleBlockOutline.forState' in s and 'getCollisionShape' in s
 states=json.loads(module_read_text(A/'blockstates/school_shield.json'))['variants'];assert len(states)==64
 mosaic=Image.new('RGBA',(64,64))
 for row in range(4):
  for col in range(4):
   name=f'school_shield_{row}_{col}'
   model=json.loads(module_read_text(A/f'models/block/{name}.json'))
   assert model['elements'][0]['to']==[16,8,16]
   assert model['textures']['floor']=='lsmmod:block/classroom_floor'
   assert model['textures']['crest']=='lsmmod:block/'+name
   tile=Image.open(module_path(A/f'textures/block/{name}.png')).convert('RGBA');assert tile.size==(16,16)
   mosaic.paste(tile,(col*16,row*16))
   for facing,turn in [('north',0),('east',90),('south',180),('west',270)]:
    assert states[f'column={col},facing={facing},row={row}']=={'model':'lsmmod:block/'+name,'y':turn}
 assert mosaic.tobytes()==Image.open(module_path(A/'textures/item/school_shield.png')).convert('RGBA').tobytes()
 # Simulate the Java tile/controller transforms in all orientations, including negative coordinates.
 for fx,fz in [(0,-1),(1,0),(0,1),(-1,0)]:
  rx,rz=-fz,fx;origin=(-13,7);targets=[]
  for row in range(4):
   for col in range(4):
    x=origin[0]+rx*(col-1)+fx*(1-row);z=origin[1]+rz*(col-1)+fz*(1-row)
    assert (x+rx*(1-col)+fx*(row-1),z+rz*(1-col)+fz*(row-1))==origin
    targets.append((x,z))
  assert len(set(targets))==16
 loot=json.loads(module_read_text(ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/school_shield.json'))
 condition=next(c for c in loot['pools'][0]['conditions'] if c['condition']=='minecraft:block_state_property')
 assert condition['properties']=={'column':'1','row':'1'}
 assert sum(col==1 and row==1 for col in range(4) for row in range(4))==1
 print('OK: 64 states, 16 half-height tiles, four placement/controller transforms, single drop; stitched 64x64 texture and classroom_floor background.')
if __name__=='__main__':main()
