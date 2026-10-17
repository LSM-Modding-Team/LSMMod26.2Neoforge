"""Static geometry/state/texture regression; does not compile or launch Minecraft."""
from resource_modules import module_path, module_read_text
from pathlib import Path
import json
from PIL import Image
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/lsmmod';D=R/'src/main/resources/data'
def rotate_box(lo,hi,turn):
 for _ in range(turn):lo,hi=[16-hi[2],lo[1],lo[0]],[16-lo[2],hi[1],hi[0]]
 return lo,hi
for height in (3,4):
 ident=f'tall_bathroom_door_full_{height}';states=json.loads(module_read_text(A/f'blockstates/{ident}.json'))['variants'];assert len(states)==128
 for key,state in states.items():
  props=dict(t.split('=') for t in key.split(','));row=int(props['row']);opened=props['open']=='true';left=props['hinge']=='left'
  m=json.loads(module_read_text(A/'models'/f'{state["model"].split(":")[1]}.json'));assert m['render_type']=='minecraft:solid'
  assert len(m['elements'])==1;e=m['elements'][0]
  expected=([0,0,0],[16,16,4]) if not opened else (([0,0,0],[4,16,16]) if left else ([12,0,0],[16,16,16]))
  assert (e['from'],e['to'])==expected,(key,e)
  actual=rotate_box(e['from'],e['to'],state['y']//90);wanted=rotate_box(*expected,['north','east','south','west'].index(props['facing']));assert actual==wanted
  assert set(e['faces'])=={'north','south','east','west','up','down'}
  for face in e['faces'].values():assert face['texture']=='#panel' and 'cullface' not in face and all(0<=u<=16 for u in face['uv'])
  assert 'glass' not in json.dumps(m)
  assert m['textures']['panel']==f'lsmmod:block/bathroom_door_{"bottom" if row==0 else "top"}'
 # Upper panel UV bands meet exactly; the last row reaches the top of the texture.
 bands=[]
 for row in range(1,height):
  m=json.loads(module_read_text(A/f'models/block/{ident}_{row}_left_false.json'));uv=m['elements'][0]['faces']['north']['uv'];bands.append((uv[1],uv[3]))
 assert bands[0][1]==16 and bands[-1][0]==0
 for low,high in zip(bands,bands[1:]):assert low[0]==high[1]
 loot=json.loads(module_read_text(D/f'lsmmod/loot_table/blocks/{ident}.json'));assert loot['pools'][0]['conditions'][1]['properties']=={'row':'0'}
for part in ('bottom','top'):assert Image.open(module_path(A/f'textures/block/bathroom_door_{part}.png')).convert('RGBA').getchannel('A').getextrema()==(255,255)
source=json.loads(module_read_text(A/'models/block/school_glass.json'));m=json.loads(module_read_text(A/'models/block/school_tinted_glass.json'));assert m['render_type']=='minecraft:solid'
assert [(e['from'],e['to']) for e in m['elements']]==[(e['from'],e['to']) for e in source['elements']]
for e in m['elements']:
 assert set(e['faces'])=={'north','south','east','west','up','down'}
 assert all('cullface' not in f for f in e['faces'].values())
for texture in ('glass','glass_edge'):assert m['textures'][texture]=='lsmmod:block/school_tinted_glass'
assert Image.open(module_path(A/'textures/block/school_tinted_glass.png')).getchannel('A').getextrema()==(255,255)
states=json.loads(module_read_text(A/'blockstates/school_tinted_glass.json'))['variants'];assert set(states)=={f'facing={f}' for f in ('north','east','south','west')}
s=module_read_text(R/'src/main/java/net/nicomar2009/lsmmod/modules/windows_and_enclosures/block/FullBathroomDoorBlock.java');assert 'SchoolGlazing' not in s and 'row<height' in s and 'if (open==1)' in s and 'SimpleBlockOutline.forState' in s and 'hasNeighborSignal(bottom.above(row))' in s and 'canBeReplaced(context)' in s
s=module_read_text(R/'src/main/java/net/nicomar2009/lsmmod/modules/windows_and_enclosures/block/SchoolTintedGlassBlock.java');assert 'extends Block' in s and 'SchoolGlazing' not in s and 'CONNECTED' not in s and 'builder.add(FACING);' in s and 'SimpleBlockOutline.forState' in s
print('OK: 256 door states; every row is full-height opaque wood and rotates with either hinge in four facings; continuous UV, controller-only loot, placement/redstone guards. Tinted glass preserves standalone geometry, opaque texture on all six faces and no connection states.')
