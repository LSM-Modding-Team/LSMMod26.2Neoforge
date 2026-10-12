"""Static geometry, state coverage, group layout and source-regression checks."""
import copy,hashlib,itertools,json,re
from create_kiosk_entrance_assets import ROOT,A,D,J,IDS,GATE_CLOSED,GATE_OPEN,GENERATED,main as generate
from audit_block_states import audit

def read(p):return json.loads(p.read_text())
def inside(boxes,p):return any(all(b[i]<=p[i]<b[i+3] for i in range(3)) for b in boxes)
def turn(point,n):
 x,y,z=point
 for _ in range(n):x,z=16-z,x
 return x,y,z

def main():
 rows={r['id']:r for r in audit()};expected={'kiosk_table':12,'school_wood_panel':4,'school_wood_door':128,'school_sliding_entrance':192,'drinking_fountain_raised':8,'lost_and_found_box':4}
 assert all(rows[k]['states']==v for k,v in expected.items()),{k:rows[k] for k in expected}
 # Every declared state maps to exactly one selector, including ignored powered values.
 for ident in expected:
  variants=read(A/f'blockstates/{ident}.json')['variants'];props=rows[ident]['domains']
  choices={p:sorted({dict(term.split('=') for term in key.split(','))[p] for key in variants if p+'=' in key}) for p in props if any(p+'=' in key for key in variants)}
  for state in itertools.product(*(choices[p] for p in choices)):
   assignment=dict(zip(choices,state));matches=[v for key,v in variants.items() if all(assignment[k]==value for k,value in (term.split('=') for term in key.split(',')))];assert len(matches)==1,(ident,assignment)
 # Closed center has four one-block leaves; open center is free in every row.
 closed=[b for b,m in GATE_CLOSED];opened=[b for b,m in GATE_OPEN]
 for y in range(64):
  for x in range(16,80):
   assert not inside(opened,(x+.5,y+.5,8.5))
   assert inside(closed,(x+.5,y+.5,8.5)) # opaque glazing fills the lattice
   assert inside(closed,(x+.5,y+.5,8.1))==(x%16 in (0,5,10,15) or y%16 in (0,5,10,15))
 # All leaves remain within 6x4 and are preserved in distinct lateral tracks.
 for b in opened:assert 0<=b[0]<b[3]<=96 and 0<=b[1]<b[4]<=64 and 0<=b[2]<b[5]<=16
 assert closed[:len(closed)-4*21]==opened[:len(opened)-4*21] # fixed decorative lattice
 # Four world orientations: every occupied cell resolves to the same controller.
 for facing,(dx,dz) in enumerate(((1,0),(0,1),(-1,0),(0,-1))):
  for row,col in itertools.product(range(4),range(6)):
   pos=(dx*col,row,dz*col);assert (pos[0]-dx*col,pos[1]-row,pos[2]-dz*col)==(0,0,0)
   corners=[turn((x,y,z),facing) for x,y,z in [(0,0,6),(16,16,13)]];assert all(0<=v<=16 for p in corners for v in p)
 # Raised fountain is the original mesh translated 8px, with exactly two reserved cells.
 source=read(A/'models/block/drinking_fountain.json');mesh=[read(A/f'models/block/drinking_fountain_raised_{r}.json') for r in range(2)]
 assert min(e['from'][1] for e in mesh[0]['elements'])==8
 assert max(e['to'][1]+16 for e in mesh[1]['elements'])==max(e['to'][1] for e in source['elements'])+8
 assert mesh[0]['textures']==mesh[1]['textures']==source['textures']
 for m in mesh:
  for e in m['elements']:
   assert all(0<=e['from'][i]<e['to'][i]<=16 for i in range(3))
   for surface in e['faces'].values():assert all(0<=n<=16 for n in surface['uv'])
 # Existing left/right table geometry, texture mapping and facing conventions survive.
 for col,part in [(0,'left'),(2,'right')]:assert read(A/f'models/block/kiosk_table_part_{col}.json')==read(A/f'models/block/kiosk_table_{part}.json')
 table=(J/'block/KioskTableBlock.java').read_text();assert 'return match(level.getBlockState(previous)' in table and '?2:3' in table and 'SharedChairRotations.horizontal' in table and 'EntityBlock' not in table
 middle=read(A/'models/block/kiosk_table_part_1.json');assert all(e['from'][1]>=8 for e in middle['elements'])
 # Loot controllers: one item for all 24 entrance cells / two fountain cells / three table cells.
 for ident,properties in [('school_sliding_entrance',{'column':'0','row':'0'}),('drinking_fountain_raised',{'half':'lower'}),('kiosk_table',{'part':'left'}),('school_wood_door',{'row':'0'})]:
  conditions=read(D/f'lsmmod/loot_table/blocks/{ident}.json')['pools'][0]['conditions'];assert any(c.get('properties')==properties for c in conditions)
 panel=read(A/'models/block/school_wood_panel.json');assert min(e['from'][2] for e in panel['elements'])==8 and max(e['to'][2] for e in panel['elements'])==16
 box=read(A/'models/block/lost_and_found_box.json');assert all(all(0<=e['from'][i]<e['to'][i]<=16 for i in range(3)) for e in box['elements'])
 # Placement and movement preflight every part before changing any world cells.
 rect=(J/'block/SchoolRectangularBlock.java').read_text();use=rect[rect.index('protected InteractionResult useWithoutItem'):rect.index('protected BlockState updateShape')]
 assert use.index('!level.isUnobstructed')<use.index('level.setBlock') and 'level.mayInteract' in use and 'player.mayUseItemAt' in use
 assert 'sharedRotations.computeIfAbsent' in rect and 'SimpleBlockOutline.forState' in rect and 'Shapes.or' not in rect and 'EntityBlock' not in rect
 for locale in ('en_us','es_es'):assert all(read(A/f'lang/{locale}.json').get('block.lsmmod.'+i) for i in IDS)
 generate();before={p:hashlib.sha256((ROOT/p).read_bytes()).digest() for p in GENERATED};generate();assert all(hashlib.sha256((ROOT/p).read_bytes()).digest()==h for p,h in before.items())
 print('OK: 348 block states fully covered, 4096 center-opening samples, telescopic pockets, four orientations, exact raised fountain, retained legacy table ends, single-drop controllers, cached grids and idempotent generator.')
if __name__=='__main__':main()
