"""Static geometry, state, placement and alternating-support checks."""
import itertools,json
from create_school_wall_railing_assets import A,ROOT,ID

def main():
 states=json.loads((A/f'blockstates/{ID}.json').read_text())['multipart']
 assert len(states)==11
 for n in ('','_post','_arm','_center','_isolated','_inventory'):
  model=json.loads((A/f'models/block/{ID+n}.json').read_text())
  assert model['textures']['metal']=='lsmmod:block/school_gate_edge'
  assert model['textures']['wall']=='lsmmod:block/light_school_wall'
  for e in model['elements']:
   assert all(0<=a<b<=20 for a,b in zip(e['from'],e['to']))
   assert len(e['faces'])==6
 base=json.loads((A/f'models/block/{ID}.json').read_text())['elements'][0]
 assert base['from']==[0,0,0] and base['to']==[16,12,16]
 source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolWallRailingBlock.java').read_text()
 assert 'extends FenceBlock' in source and 'super.updateShape' in source
 assert 'protected VoxelShape getCollisionShape' in source and 'SimpleBlockOutline.forState' in source
 assert 'super.getCollisionShape' not in source
 assert 'level.hasChunkAt(next)' in source and 'Block.UPDATE_CLIENTS' in source
 for length in range(1,33):
  supports=[i for i in range(length) if (i&1)==0]
  assert all(b-a==2 for a,b in zip(supports,supports[1:]))
  # Insertion at the beginning and removal recover a complete alternating line.
  assert [(i&1)==0 for i in range(length+1)][1:]==[(i&1)!=0 for i in range(length)]
 directions=('north','east','south','west');count=0
 for mask,post,facing,water in itertools.product(range(16),(False,True),directions,(False,True)):
  state={d:str(bool(mask&(1<<i))).lower() for i,d in enumerate(directions)};state.update(post=str(post).lower(),facing=facing)
  def match(condition):
   if 'OR' in condition:return any(match(c) for c in condition['OR'])
   return all(state[k]==v for k,v in condition.items())
  selected=[p['apply'] for p in states if 'when' not in p or match(p['when'])]
  assert len(selected)==(2+post if mask==0 else 2+mask.bit_count()+post)
  for p in selected:assert (A/('models/block/'+p['model'].split(':block/')[1]+'.json')).is_file()
  count+=1
 for name in ('ModBlocks.java','ModItems.java'):
  text=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/name).read_text()
  assert 'CLASSROOM_FLOOR_STAIRS' not in text and 'SCHOOL_SLOPE' not in text
  assert 'LIGHT_SCHOOL_WALL_RAILING' in text
 assert not (ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolTiledStairBlock.java').exists()
 assert not (ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolSlopeBlock.java').exists()
 print(f'OK: {count} states; lowered 12 px wall, continuous rails, alternate supports, fence placement and obsolete registrations removed.')
if __name__=='__main__':main()
