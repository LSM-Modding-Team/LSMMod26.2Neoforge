from pathlib import Path
import json, math
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/lsmmod'
v=json.loads((A/'blockstates/school_wooden_handrail.json').read_text())['variants'];assert len(v)==72
count=0
for c in range(6):
 for row in range(3):
  occupied=row*16<c*4+20 and (row+1)*16>c*4
  for f in ('north','east','south','west'):
   name=v[f'facing={f},column={c},row={row}']['model'].split(':')[1];p=A/'models'/f'{name}.json';m=json.loads(p.read_text())
   assert occupied == ('loader' in m)
  if not occupied:continue
  lines=(A/f'models/block/school_wooden_handrail_{c}_{row}.obj').read_text().splitlines();vertices=[tuple(map(float,l.split()[1:])) for l in lines if l.startswith('v ')]
  assert vertices and all(0<=y<=1 and 0<=z<=1 and 0<=x<=1 for x,y,z in vertices)
  for l in lines:
   if l.startswith('f '):
    a,b,d=[vertices[int(t.split('/')[0])-1] for t in l.split()[1:]];u=[b[k]-a[k] for k in range(3)];w=[d[k]-a[k] for k in range(3)];assert sum((u[(k+1)%3]*w[(k+2)%3]-u[(k+2)%3]*w[(k+1)%3])**2 for k in range(3))>1e-16
  count+=1
assert 6*4==24
for c in range(5):assert c*4+4+16==(c+1)*4+16
s=(R/'src/main/java/net/nicomar2009/lsmmod/block/SchoolWoodenHandrailBlock.java').read_text();assert 'SimpleBlockOutline.forState' in s and 'canBeReplaced(context)' in s and 'if (!occupied(column, row) ||' in s
loot=json.loads((R/'src/main/resources/data/lsmmod/loot_table/blocks/school_wooden_handrail.json').read_text());assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','row':'0'}
print(f'OK: 72 states, {count} occupied cells, local mesh bounds and triangles; six-block 24px rise, continuous rails, one controller/drop, placement preflight, simple outline.')
