"""Verify facility dimensions, physical grids, materials, states and legacy paths without Java."""
import hashlib,itertools,json,re
from pathlib import Path
from create_facility_expansion_assets import ROOT,A,D,J,DINING_FIXED,DINING_CLOSED,DINING_OPEN,MICROWAVE,GENERATED,main as generate
from audit_block_states import audit
from create_kiosk_entrance_assets import GATE_CLOSED,GATE_OPEN

def read(p):return json.loads(p.read_text())
def inside(boxes,p):return any(all(b[i]<=p[i]<b[i+3] for i in range(3)) for b in boxes)
def matches(c,state):return all(state[k] in v.split('|') for k,v in c.items())
def resolved(ident,mask=3):
 value=read(A/('models/'+ident.split(':')[1]+'.json'))
 if value.get('type')=='composite':
  return [m for group in value['models'] for entry in group
          if mask in entry['condition'].get('masks',[entry['condition'].get('mask')])
          for m in resolved(entry['model'],mask)]
 if value.get('parent','').startswith('lsmmod:'):
  return [dict(parent,textures={**parent.get('textures',{}),**value.get('textures',{})}) for parent in resolved(value['parent'],mask)]
 return [value]
def main():
 rows={r['id']:r for r in audit()};expected={'infirmary_cot':24,'san_martin_de_porres':96,'dining_door_wide':192,'bathroom_door_wide':192,'school_microwave':4,'school_trophy_gallery':96}
 assert all(rows[k]['states']==v for k,v in expected.items()),{k:rows[k] for k in expected}
 domains={'facing':['north','east','south','west'],'segment':['0','1','2'],'extended':['false','true'],'expanded':['false','true'],'column':['0','1'],'row':['0','1','2'],'depth':['0','1'],'hinge':['left','right'],'open':['false','true'],'half':['lower','upper']}
 total=0
 for ident in expected:
  statefile=read(A/f'blockstates/{ident}.json');props=rows[ident]['domains'];choices={k:(list(map(str,range(6))) if k=='column' and ident=='school_trophy_gallery' else list(map(str,range(4))) if k=='row' and ident=='school_trophy_gallery' else domains[k]) for k in props}
  for values in itertools.product(*(choices[k] for k in props)):
   state=dict(zip(props,values))
   if 'variants' in statefile:
    applies=[v for k,v in statefile['variants'].items() if matches(dict(t.split('=') for t in k.split(',')),state)];assert len(applies)==1,(ident,state)
   else:
    applies=[v['apply'] for v in statefile['multipart'] if matches(v.get('when',{}),state)]
    assert applies or ident in ('dining_door_wide','bathroom_door_wide') and state['depth']=='1' and (state['open']=='false' or state['column']!=('0' if state['hinge']=='left' else '1')),(ident,state)
   for apply in applies:
    for model in resolved(apply['model']):
     limit=32 if 'framed_door_window_' in apply['model'] else 16
     minimum=-16 if limit==32 else 0
     assert model['elements'] and all(all(minimum<=e['from'][i]<e['to'][i]<=limit for i in range(3)) for e in model['elements'])
   total+=1
 assert total==604
 # Exactly nine clear lattice rectangles per 16px cell, filled by opaque tinted glass.
 for x,y in itertools.product(range(16),repeat=2):
  metal=inside([b for b,m in GATE_CLOSED if m=='metal'],(16+x+.5,y+.5,8.1));assert metal==(x in (0,5,10,15) or y in (0,5,10,15))
  assert inside([b for b,m in GATE_CLOSED if m=='glass'],(16+x+.5,y+.5,8.5))
 source=(ROOT/'tools/create_kiosk_entrance_assets.py').read_text();assert "'glass':'lsmmod:block/school_tinted_glass'" in source
 # Open moving leaf retreats into a checked cell; static upper glazing and wall do not move.
 assert DINING_CLOSED[:len(DINING_FIXED)]==DINING_OPEN[:len(DINING_FIXED)]
 for y in (1,20,39):
  assert inside([p[0] for p in DINING_CLOSED],(12,y,2)) and not inside([p[0] for p in DINING_OPEN],(12,y,2))
  assert inside([p[0] for p in DINING_OPEN],(2,y,20))
 assert max(p[0][4] for p in DINING_CLOSED if p[1] in ('bottom','top'))==40
 assert max(p[0][3] for p in DINING_CLOSED if p[1] in ('bottom','top'))==24
 # Native cutout textures define the window; physical collision remains a full door leaf.
 assert inside([b for b,m,*_ in DINING_CLOSED if m in ('bottom','top')],(12,20,2))
 assert inside([b for b,m,*_ in DINING_CLOSED if m=='window'],(12,20,2))
 assert inside([b for b,m,*_ in DINING_OPEN if m=='window'],(2,20,12))
 assert next(b for b,m,*_ in DINING_FIXED if m=='glass')==[0,40,0,32,48,8]
 for p in (A/'models/block').glob('dining_door_wide_part_*.json'):
  m=read(p)
  assert m['textures']['bottom']=='lsmmod:block/dining_door_bottom'
  assert m['textures']['top']=='lsmmod:block/dining_door_top'
  assert m['textures']['window']=='minecraft:block/glass'
  for e in m['elements']:
   for f,s in e['faces'].items():
    assert s['texture'] not in ('#glass','#window')
 # Material is light ONLY on the outer side facing the adjacent block; no interior light face.
 for p in (A/'models/block').glob('dining_door_wide_part_*.json'):
  m=read(p)
  for e in m['elements']:
   for f,s in e['faces'].items():
    if s['texture']=='#light':assert f in ('east','west') and (e['to'][0]==16 if f=='east' else e['from'][0]==0)
 # Cot extension preserves headrest and old pairs; new tail reaches the third cell.
 cot=(J/'block/InfirmaryCotBlock.java').read_text();helper=(J/'block/InfirmaryCotExtendedShapes.java').read_text()
 assert '.setValue(EXTENDED,false)' in cot and '.setValue(EXTENDED,true)' in cot and 'state.getValue(EXTENDED)?3:2' in cot
 assert 'SHAPES[0]=InfirmaryCotShapes.SHAPES[0]' in helper and 'SHAPES[2]=' in helper and 'Shapes.or' not in helper
 # Java method arguments require an explicit array creation, not a bare initializer.
 assert len(re.findall(r'ExactVoxelShapes\.pixels\(new double\[\]\[\]\s*\{',helper))==2
 assert not re.search(r'ExactVoxelShapes\.pixels\(\s*\{',helper)
 for segment in (1,2):
  model=read(A/f'models/block/infirmary_cot_extended_{segment}.json');assert model['elements']
  assert all(0<=e['from'][2]<e['to'][2]<=16 for e in model['elements'])
 # Expanded statue reserves six cells, preserves original half models and has a single-drop condition.
 statue=(J/'block/SchoolSanMartinBlock.java').read_text();assert 'extends TwoTallBlock' in statue and '.setValue(EXPANDED,false)' in statue and '.setValue(EXPANDED,true)' in statue and 'super.getCollisionShape' in statue
 loot=read(D/'lsmmod/loot_table/blocks/san_martin_de_porres.json')['pools'][0]['conditions'][1];assert loot['condition']=='minecraft:any_of' and len(loot['terms'])==2
 # Support uses virtual support faces and the exact existing collision/occlusion remains 12px.
 railing=(J/'block/SchoolWallRailingBlock.java').read_text();reg=(J/'registry/ModBlocks.java').read_text();assert 'getBlockSupportShape' in railing and 'return Shapes.block()' in railing and 'Block.box(0,0,0,16,12,16)' in railing
 assert re.search(r'"light_school_wall_railing".*?forceSolidOn\(\)',reg,re.S)
 door=(J/'block/SchoolFramedDiningDoorBlock.java').read_text();placement=door[door.index('getStateForPlacement'):door.index('private static boolean supported')]
 assert 'hit.' not in placement and 'context.getItemInHand()' in placement
 assert 'context.getClickLocation()' in placement and '.setValue(HINGE, rightHinge' in placement
 use=door[door.index('protected InteractionResult useWithoutItem'):door.index('private void notifyCells')];assert use.index('!level.isUnobstructed')<use.index('level.setBlock') and 'existing.isAir()' in use and 'level.hasChunkAt' in use
 assert 'COLLISIONS.computeIfAbsent' in door and 'SimpleBlockOutline.forState' in door
 # Dedicated translucent layer only for glass, avoiding opaque geometry sorting with the pane.
 pane=read(A/'models/block/school_trophy_gallery_glass.json');assert pane['render_type']=='minecraft:translucent' and all(set(e['faces'])<=set(('north','south')) for e in pane['elements'])
 source=(J/'block/SchoolFacilityShapes.java').read_text()
 trophies=json.loads(re.search(r'double\[\]\[\] TROPHIES = (.*?);',source)[1].replace('{','[').replace('}',']'))
 assert [max(b[i+3] for b in trophies)-min(b[i] for b in trophies) for i in range(3)]==[96,64,12]
 award_profiles=set();metals=[];heights=set()
 for col,row in itertools.product(range(6),range(4)):
  model=read(A/('models/'+read(A/'blockstates/school_trophy_gallery.json')['multipart'][row*6*4+col*4]['apply']['model'].split(':')[1]+'.json'))
  awards=[e for e in model['elements'] if any(s['texture'] in ('#gold','#silver','#bronze') for s in e['faces'].values())]
  assert awards,(col,row)
  metals.append(next(iter(awards[0]['faces'].values()))['texture'])
  heights.add(max(e['to'][1] for e in awards))
  award_profiles.add(tuple(tuple(e['from']+e['to']) for e in awards))
 assert metals.count('#gold')==18 and set(metals)=={'#gold','#silver','#bronze'}
 assert len(award_profiles)>=4 and len(heights)>=4
 assert max(b[3] for b,*_ in MICROWAVE)<=16 and max(b[4] for b,*_ in MICROWAVE)<=16
 generate();before={p:hashlib.sha256((ROOT/p).read_bytes()).digest() for p in GENERATED};generate();assert all(hashlib.sha256((ROOT/p).read_bytes()).digest()==h for p,h in before.items())
 print('OK: 604 states; dining/bathroom framed doors, shared connected transom and unsplit window, two hinges and protected swing; 6x4x0.75 gallery, 18 gold and six silver/bronze; cot/statue legacy paths, shared caches and idempotence.')
if __name__=='__main__':main()
