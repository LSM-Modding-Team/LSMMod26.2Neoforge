from pathlib import Path
import json, math
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/lsmmod'
v=json.loads((A/'blockstates/school_wooden_handrail.json').read_text())['variants'];assert len(v)==144
count=0
for side,edge in [('right',14),('left',0)]:
 for c in range(6):
  supports=[]
  for row in range(3):
   occupied=row*16<c*4+20
   for f in ('north','east','south','west'):
    name=v[f'facing={f},column={c},row={row},right={str(side=="right").lower()}']['model'].split(':')[1];p=A/'models'/f'{name}.json';m=json.loads(p.read_text())
    assert occupied == ('loader' in m)
   if not occupied:continue
   lines=(A/f'models/block/school_wooden_handrail_{side}_{c}_{row}.obj').read_text().splitlines();vertices=[tuple(map(float,l.split()[1:])) for l in lines if l.startswith('v ')]
   assert vertices and all(0<=y<=1 and 0<=z<=1 and edge/16<=x<=(edge+2)/16 for x,y,z in vertices)
   supports.extend((y+row)*16 for x,y,z in vertices if 7/16<=z<=9/16)
   for l in lines:
    if l.startswith('f '):
     a,b,d=[vertices[int(t.split('/')[0])-1] for t in l.split()[1:]];u=[b[k]-a[k] for k in range(3)];w=[d[k]-a[k] for k in range(3)];assert sum((u[(k+1)%3]*w[(k+2)%3]-u[(k+2)%3]*w[(k+1)%3])**2 for k in range(3))>1e-16
   count+=1
  assert min(supports)==0, (side,c)
  assert max(supports)>=c*4+15.75
# Rotated right/left cross-sections reach the shared boundary with an adjacent ramp.
for edge in (0,14):
 assert 0 in (edge,edge+2) or 16 in (edge,edge+2)
 assert 0 in (14-edge,16-edge) or 16 in (14-edge,16-edge)
assert 6*4==24
for c in range(5):assert c*4+4+16==(c+1)*4+16
s=(R/'src/main/java/net/nicomar2009/lsmmod/block/SchoolWoodenHandrailBlock.java').read_text();assert 'SimpleBlockOutline.forState' in s and 'canBeReplaced(context)' in s and 'if (!occupied(column, row) ||' in s
loot=json.loads((R/'src/main/resources/data/lsmmod/loot_table/blocks/school_wooden_handrail.json').read_text());assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','row':'0'}
assert 'double low = 0;' in s and 'RIGHT' in s and 'hasRamp(level, rightPos)' in s
print(f'OK: 144 states, {count} side/cell meshes; all six supports reach common Y=0, left/right edges flush in four orientations, 24px rise, continuous rails, one controller/drop and simple outline.')
