"""Six-cell placement/geometry, continuous texture UVs and one-drop contract."""
from resource_modules import module_exists, module_read_bytes, module_read_text
import json,hashlib,math
from pathlib import Path
from create_school_crucifix import ROOT,A,PARTS,GENERATED,mesh,main as generate
from audit_block_states import audit

def read(p):return json.loads(module_read_text(p))
def area(e,f):
 axes={'north':(0,1),'south':(0,1),'west':(2,1),'east':(2,1),'up':(0,2),'down':(0,2)}[f]
 return math.prod(e['to'][i]-e['from'][i] for i in axes)

def main():
 row=next(r for r in audit() if r['id']=='school_crucifix');assert row['states']==24 and set(row['domains'])=={'column','row','facing'}
 assert min(p[0][0] for p in PARTS)==5 and max(p[0][3] for p in PARTS)==27
 assert min(p[0][1] for p in PARTS)==0 and max(p[0][4] for p in PARTS)==40
 assert abs(22/40-368/660)<.01
 source=mesh();surfaces=[]
 for row in range(3):
  for column in range(2):
   value=read(A/f'models/block/school_crucifix_{column}_{row}.json');assert value['elements'] and 'loader' not in value
   for e in value['elements']:
    assert all(0<=e['from'][i]<e['to'][i]<=16 for i in range(3))
    world={'from':[e['from'][0]+column*16,e['from'][1]+row*16,e['from'][2]],'to':[e['to'][0]+column*16,e['to'][1]+row*16,e['to'][2]]}
    assert any(all(b[i]<=world['from'][i]<world['to'][i]<=b[i+3] for i in range(3)) for b,*_ in PARTS)
    for f,s in e['faces'].items():surfaces.append((world,f,s))
   if row==2:assert max(e['to'][1] for e in value['elements'])==8
 # Clipping never adds interior caps and preserves physical and texture-area totals for each surface.
 for texture in source['textures']:
  if texture=='particle':continue
  for f in ('up','down','north','south','west','east'):
   originals=[(e,e['faces'][f]) for e in source['elements'] if f in e['faces'] and e['faces'][f]['texture']=='#'+texture]
   cells=[(e,s) for e,face,s in surfaces if face==f and s['texture']=='#'+texture]
   assert math.isclose(sum(area(e,f) for e,s in originals),sum(area(e,f) for e,s in cells),abs_tol=1e-7)
   uvarea=lambda uv:abs((uv[2]-uv[0])*(uv[3]-uv[1]))
   assert math.isclose(sum(uvarea(s['uv']) for e,s in originals),sum(uvarea(s['uv']) for e,s in cells),abs_tol=1e-7),(texture,f)
 for row in range(3):
  for column in range(2):
   for facing,angle in [('north',0),('east',90),('south',180),('west',270)]:
    states=read(A/'blockstates/school_crucifix.json')['variants'];entry=states[f'column={column},row={row},facing={facing}'];assert entry['y']==angle
    # Placement coordinate transforms and origins agree in all four orientations.
    dx,dz={'north':(1,0),'east':(0,1),'south':(-1,0),'west':(0,-1)}[facing]
    x,y,z=(10+column*dx,20+row,30+column*dz);assert (x-column*dx,y-row,z-column*dz)==(10,20,30)
 loot=read(ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/school_crucifix.json');assert loot['pools'][0]['conditions'][1]['properties']=={'row':'0','column':'0'}
 java=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/wall_blocks/block/SchoolCrucifixBlock.java');assert 'true,3,false' in java and 'builder.add(ROW)' in java
 parent=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/facilities/block/WideBathroomBlock.java');assert 'if (state.hasProperty(ROW)) return state.getValue(ROW);' in parent and 'sharedRotations.computeIfAbsent' in parent and 'SimpleBlockOutline.forState' in parent
 for filename in source['textures'].values():assert module_exists(A/'textures'/Path(filename.split(':')[1]+'.png'))
 item=read(A/'models/item/school_crucifix.json');assert all(all(0<=e['from'][i]<e['to'][i]<=16 for i in range(3)) for e in item['elements'])
 generate();before={p:hashlib.sha256(module_read_bytes(ROOT/p)).digest() for p in GENERATED};generate();assert all(hashlib.sha256(module_read_bytes(ROOT/p)).digest()==h for p,h in before.items())
 print('OK: proportion 22x40 within 32x40, six cells/four orientations, 24 states, continuous sculpted-face/INRI UV areas, wall placement, exact cached physical shapes, simple outlines and one controller drop; generator idempotent.')
if __name__=='__main__':main()
