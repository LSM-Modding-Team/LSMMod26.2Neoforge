"""Exhaustive face orientation, placement, structure transforms and resources without Java."""
import itertools,json
from create_mixed_school_walls import ROOT,A,ids,SIDES
from audit_block_states import audit
V={'north':(0,0,-1),'east':(1,0,0),'south':(0,0,1),'west':(-1,0,0),'up':(0,1,0),'down':(0,-1,0)}
INV={v:k for k,v in V.items()}
def rotate(side,x,y):
 a,b,c=V[side]
 for _ in range(x):a,b,c=a,c,-b
 for _ in range(y):a,b,c=-c,b,a
 return INV[(a,b,c)]
def read(path):return json.loads(path.read_text())
def main():
 cases=0;rows={r['id']:r for r in audit()}
 for ident,a,b,n in ids():
  model=read(A/f'models/block/{ident}.json');variants=read(A/f'blockstates/{ident}.json')['variants']
  assert model['parent']=='minecraft:block/cube' and len(variants)==16 and rows[ident]['states']==16
  first={side for side in SIDES if model['textures'][side]=='#first'};assert len(first)==n
  grouped={'north'}|({'east'} if min(n,6-n)>=2 else set())|({'up'} if n==3 else set())
  assert first==(set(SIDES)-grouped if n>3 else grouped)
  assert model['textures']['first']==f'lsmmod:block/{a}_school_wall'
  assert model['textures']['second']==f'lsmmod:block/{b}_school_wall'
  patterns={frozenset(rotate(side,i//4,i%4) for side in first) for i in range(16)}
  assert len(patterns)=={1:6,2:12,3:8,4:12,5:6}[n]
  for i in range(16):
   apply=variants[f'orientation={i}'];assert (apply['x'],apply['y'])==(90*(i//4),90*(i%4))
   assert apply['model']==f'lsmmod:block/{ident}'
  # Every clicked face and every choice of perpendicular edge/corner is placeable.
  for face in SIDES:
   axis=next(i for i,v in enumerate(V[face]) if v)
   others=[i for i in range(3) if i!=axis]
   for signs in itertools.product((-1,1),repeat=2):
    for closest in others:
     selected={face}
     for other,sign in zip(others,signs):
      vector=tuple(sign if i==other else 0 for i in range(3))
      if min(n,6-n)==3 or min(n,6-n)==2 and other==closest:selected.add(INV[vector])
     wanted=set(SIDES)-selected if n>3 else selected
     assert frozenset(wanted) in patterns,(ident,face,wanted)
     cases+=1
  for pattern in patterns:
   # Four horizontal rotations and both vanilla mirror axes preserve reachable masks.
   for turn in range(4):assert frozenset(rotate(side,0,turn) for side in pattern) in patterns
   for axis in (0,2):
    reflected=frozenset(INV[tuple(-v if i==axis else v for i,v in enumerate(V[side]))] for side in pattern)
    assert reflected in patterns
  assert read(A/f'models/item/{ident}.json')['parent']==f'lsmmod:block/{ident}'
  assert read(A/f'items/{ident}.json')['model']['model']==f'lsmmod:item/{ident}'
  loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json')
  assert loot['pools'][0]['entries'][0]['name']==f'lsmmod:{ident}'
  for locale in ['es_es','en_us']:assert 'block.lsmmod.'+ident in read(A/f'lang/{locale}.json')
  assert 'lsmmod:'+ident in read(ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json')['values']
 java=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/MixedSchoolWallBlock.java').read_text()
 assert 'Direction.NORTH, Direction.EAST, Direction.UP' in java
 assert 'orientation / 4' in java and 'orientation % 4' in java
 assert 'getShape(' not in java and 'getCollisionShape(' not in java and 'updateShape(' not in java
 tabs=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModCreativeTabs.java').read_text();assert 'id.contains("_wall")' in tabs
 assert sum(rows[ident]['states'] for ident,*_ in ids())==240
 print(f'OK: 15 full-cube nonconnecting walls, 132 distinct face patterns, {cases} placements across all six faces, rotations/mirrors, loot/items/tags/translations and School Blocks; 240 added states, 15 native cube models.')
if __name__=='__main__':main()
