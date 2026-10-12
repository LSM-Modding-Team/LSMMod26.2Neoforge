"""Check the 32px cylinder, circular base, neighbor cuts, face colors and railing height."""
import hashlib,itertools,json,re
from pathlib import Path
from create_dark_tiles_drain_column import ROOT,A,CIRCLE,FIXED,DRAIN,COLORS,connectors,outer_boxes,vertical,GENERATED,main as generate
from audit_block_states import audit

def read(p):return json.loads(p.read_text())
def inside(boxes,p):return any(all(b[i]<=p[i]<b[i+3] for i in range(3)) for b in boxes)
def rotate(b,side):
 for _ in range(side):b=[16-b[5],b[1],b[0],16-b[2],b[4],b[3]]
 return b

def matches(condition,base,height,color):
 if not condition:return True
 if 'base' in condition and base!=(condition['base']==1):return False
 if condition['side']<0:return base
 if 'height' in condition and height!=condition['height']:return False
 return condition['paint']<0 or height>0 and condition['paint']==color

def choose(series,base,height,color):return next((e for e in series if matches(e.get('condition'),base,height,color)),None)
def resolve(ident,trail=()):
 assert ident not in trail
 data=read(A/'models'/Path(ident.split(':')[1]+'.json'))
 parent=resolve(data['parent'],trail+(ident,)) if data.get('parent','').startswith('lsmmod:') else {}
 return {**parent,**data,'textures':{**parent.get('textures',{}),**data.get('textures',{})}}

def main():
 rows={r['id']:r['states'] for r in audit()};assert [rows[k] for k in ('dark_tiled_floor','school_drain','school_circular_column')]==[1,4,1]
 assert min(b[0] for b in CIRCLE)==min(b[2] for b in CIRCLE)==-8
 assert max(b[3] for b in CIRCLE)==max(b[5] for b in CIRCLE)==24
 assert not inside(CIRCLE,(-7.75,1,-7.75)) and inside(CIRCLE,(-7.75,1,8))
 drain=[b[0] for b in DRAIN];assert inside(drain,(12,12,8)) and not inside(drain,(4,12,8))
 for z in range(16):assert inside(drain,(4,15.5,z+.5))==(z in (0,3,6,9,12,15))
 root=read(A/'models/block/school_circular_column.json');assert len(root['models'])==34
 assert root['models'][0][-1]['model']=='lsmmod:block/school_circular_column_shaft'
 assert root['models'][1]['model']=='lsmmod:block/school_circular_column_base'
 count=0
 for q in range(4):
  for side in range(4):
   worldq=q^1 if side>=2 else q
   outer=root['models'][2+(q*4+side)*2];caps=root['models'][3+(q*4+side)*2]
   for height,base,color in itertools.product((0,12,16),(False,True),range(len(COLORS))):
    selected=choose(outer,base,height,color);assert selected and 'transform' not in selected
    mesh=resolve(selected['model']);assert mesh['loader']=='fusion:model' and mesh['type']=='base'
    physical=[rotate(b,side) for b in outer_boxes(q,height)]
    assert mesh.get('elements'), 'Fusion base must have a real geometry object; elements=[] bakes to null in 1.3.16'
    for element in mesh.get('elements',[]):
     if selected['model'].endswith('_empty'):
      assert element['faces'] and inside(FIXED,(8,8,8))
      assert all(0<element['to'][i]-element['from'][i]<=1 for i in range(3));continue
     assert any(all(b[i]<=element['from'][i]<element['to'][i]<=b[i+3] for i in range(3)) for b in physical)
     for face,surface in element['faces'].items():
      key=surface['texture'][1:];texture=root['textures'].get(key,mesh['textures'].get(key))
      expected='lsmmod:block/classroom_floor' if base and element['to'][1]<=2 else 'lsmmod:block/dark_school_wall'
      assert texture==expected
      assert all(0<=v<=16 for v in surface['uv']),surface
      assert 'cullface' not in surface or all(0<=element['from'][i]<element['to'][i]<=16 for i in (0,2))
    cap=choose(caps,base,height,color)
    if height==0:assert cap is None
    else:
     assert cap and cap['condition']['quarter']==worldq
     leaf=resolve(cap['model']);key=leaf['textures']['column_join_surface'][1:];assert root['textures'][key]==COLORS[color]
     assert max(e['to'][1] for e in leaf['elements'])==min(height,8+8*(q//2))
     assert len({next(iter(e['faces'])) for e in leaf['elements']})==1
    count+=1
 # Model/physics cut the actual neighboring quarter, not a full infinite clipping plane.
 samples=[(x+.25,y,z+.25) for x,z in itertools.product(range(-8,24,2),repeat=2) for y in (1,10,14)]
 for heights in itertools.product((0,12,16),repeat=4):
  boxes=FIXED[:]
  for side,height in enumerate(heights):
   for q in range(4):boxes.extend(rotate(b,side) for b in outer_boxes(q,height))
  for point in samples:
   x,y,z=point
   occupied=any(height>y and ((side==0 and 0<=x<16 and -16<=z<0) or (side==1 and 16<=x<32 and 0<=z<16) or (side==2 and 0<=x<16 and 16<=z<32) or (side==3 and -16<=x<0 and 0<=z<16)) for side,height in enumerate(heights))
   assert inside(boxes,point)==(inside(CIRCLE,point) and not occupied),(heights,point)
   count+=1
 # Every combination of removed front-facing half cubes keeps precisely the corresponding column sector.
 for mask in range(16):
  for side in range(4):
   boxes=FIXED[:]
   for other in range(4):
    for q in range(4):
     local=q^1 if other>=2 else q
     boxes.extend(rotate(b,other) for b in outer_boxes(local,16 if other==side and mask&(1<<q) else 0))
   for q in range(4):
    local=q^1 if side>=2 else q;b=rotate(connectors(local)[len(connectors(local))//2],side)
    point=tuple((b[i]+b[i+3])/2 for i in range(3));assert inside(boxes,point)==(not bool(mask&(1<<q)));count+=1
 # The circular base replaces exactly the lowest two pixels; it does not enlarge the collision again.
 for base in (False,True):
  mesh=resolve(choose(root['models'][0],base,0,0)['model']);assert min(e['from'][1] for e in mesh['elements'])==(2 if base else 0)
 base=resolve(root['models'][1]['model']);assert all(e['to'][1]<=2 for e in base['elements'])
 item=resolve('lsmmod:item/school_circular_column');assert all(all(0<=e['from'][i]<e['to'][i]<=16 for i in range(3)) for e in item['elements'])
 source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolCircularColumnBlock.java').read_text();assert 'SchoolDrainColumnShapes.FIXED' in source and 'SchoolDrainColumnShapes.OUTER' in source and ' ? 12 : 16' in source and 'forState' not in source
 ids=set()
 for p in (ROOT/'src/main/java/net/nicomar2009/lsmmod/compat/jade').glob('*Provider.java'):ids.update(re.findall(r'fromNamespaceAndPath\(LSMMod.MOD_ID,\s*"([a-z_]+)"\)',p.read_text()))
 for locale in ('en_us','es_es'):assert all(read(A/f'lang/{locale}.json').get('config.jade.plugin_lsmmod.'+i) for i in ids)
 generate();before={p:hashlib.sha256((ROOT/p).read_bytes()).digest() for p in GENERATED};generate();assert all(hashlib.sha256((ROOT/p).read_bytes()).digest()==h for p,h in before.items())
 print(f'OK: {count} render/profile/physical cases; diameter 32px (-8..24), circular 2px base, wall material continuity, railing cuts at 12px, preserved holes, six states, cached color-independent physics and idempotent generator.')
if __name__=='__main__':main()
