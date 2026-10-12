"""Low-state dark tiles, open drain, and shared per-quadrant circular-column connections."""
import json, math, random, copy
from pathlib import Path
from PIL import Image
from create_bathroom_statue_assets import solid_model
ROOT=Path(__file__).resolve().parents[1]; A=ROOT/'src/main/resources/assets/lsmmod'; D=ROOT/'src/main/resources/data'; J=ROOT/'src/main/java/net/nicomar2009/lsmmod'; GENERATED=[]
from create_paintable_school_block import PAINTS
COLORS=PAINTS
DRAIN=[([0,0,0,16,8,16],'gray'),([8,8,0,16,16,16],'gray',{'up':'tile'})]+[([0,15,z,8,16,z+1],'metal') for z in (0,3,6,9,12,15)]
# Diameter 32px, centered on (8,8), extending eight pixels into each neighboring cell.
CIRCLE=[]
for i in range(64):
 x=-8+i*.5;edge=round((8-math.sqrt(256-(x+.25-8)**2))*2)/2
 CIRCLE.append([x,0,edge,x+.5,16,16-edge])
FIXED=[];ALL=[];OUTER=[[],[]]
for b in CIRCLE:
 for low,high in zip((-8,0,8,16),(0,8,16,24)):
  z0,z1=max(b[2],low),min(b[5],high)
  if z0>=z1:continue
  cell=[b[0],0,z0,b[3],16,z1];ALL.append(cell)
  cardinal=(0<=b[0]<16 and (z1<=0 or z0>=16)) or (0<=z0<16 and (b[3]<=0 or b[0]>=16))
  if not cardinal:FIXED.append(cell)
  elif z1<=0:OUTER[int(b[0]>=8)].append(cell)

def connectors(q):
 # North-facing overhang; rotations share this section in all four directions.
 return [[b[0],8*(q//2),b[2],b[3],8+8*(q//2),b[5]] for b in OUTER[q%2]]
def outer_boxes(q,height):
 return [[b[0],max(b[1],height),b[2],b[3],b[4],b[5]] for b in connectors(q) if b[4]>height]
def vertical(boxes,low,high):
 return [[b[0],max(b[1],low),b[2],b[3],min(b[4],high),b[5]] for b in boxes if max(b[1],low)<min(b[4],high)]
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n');GENERATED.append(str(p.relative_to(ROOT)))
def model(parts,textures):
 v=solid_model(parts);v['textures']=textures;v['render_type']='minecraft:solid'
 for e in v['elements']:
  for f,s in e['faces'].items():
   axis={'west':0,'east':0,'down':1,'up':1,'north':2,'south':2}[f]
   if (f in ('west','down','north') and e['from'][axis]==0) or (f in ('east','up','south') and e['to'][axis]==16):s['cullface']=f
 return v
def explicit_materials(value):
 v=copy.deepcopy(value)
 for e in v.get('elements',[]):
  for surface in e['faces'].values():
   texture=surface['texture']
   while texture.startswith('#'):texture=v['textures'][texture[1:]]
   key='column_'+texture.replace(':','_').replace('/','_')
   v['textures'][key]=texture;surface['texture']='#'+key
 return v
def fusion_base(value):
 v=copy.deepcopy(value);v['loader']='fusion:model';v['type']='base';return v

def oriented_model(value,turns):
 v=copy.deepcopy(value);horizontal={'north':'east','east':'south','south':'west','west':'north'}
 for _ in range(turns):
  for e in v['elements']:
   a,b=e['from'],e['to'];e['from']=[16-b[2],a[1],a[0]];e['to']=[16-a[2],b[1],b[0]]
   faces={}
   for face,surface in e['faces'].items():
    if 'cullface' in surface:surface['cullface']=horizontal.get(surface['cullface'],surface['cullface'])
    if face in ('up','down'):surface['rotation']=(surface.get('rotation',0)+(90 if face=='up' else 270))%360
    faces[horizontal.get(face,face)]=surface
   e['faces']=faces
 return v

def basic(id,root):
 write(A/f'items/{id}.json',{'model':{'type':'minecraft:model','model':'lsmmod:block/'+root}})
 write(D/f'lsmmod/loot_table/blocks/{id}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+id}]}]})
def main():
 im=Image.open(A/'textures/block/classroom_floor.png').convert('RGB'); vals=[sum(c)/3 for c in im.getdata()]; average=sum(vals)/len(vals)
 im.putdata([tuple(max(0,min(255,round(101+(v-average)*.7)+offset)) for offset in (0,1,2)) for v in vals]);p=A/'textures/block/dark_tiled_floor.png';im.save(p);GENERATED.append(str(p.relative_to(ROOT)))
 rng=random.Random(239);im=Image.new('RGB',(16,16));im.putdata([tuple(c+rng.choice((-2,-1,0,1,2)) for c in (43,46,49)) for _ in range(256)]);p=A/'textures/block/school_drain_metal.png';im.save(p);GENERATED.append(str(p.relative_to(ROOT)))
 write(A/'models/block/dark_tiled_floor.json',{'parent':'minecraft:block/cube_all','textures':{'all':'lsmmod:block/dark_tiled_floor'}});write(A/'blockstates/dark_tiled_floor.json',{'variants':{'':{'model':'lsmmod:block/dark_tiled_floor'}}});basic('dark_tiled_floor','dark_tiled_floor')
 write(A/'models/block/school_drain.json',model(DRAIN,{'gray':'lsmmod:block/bathroom_floor_side','tile':'lsmmod:block/dark_tiled_floor','metal':'lsmmod:block/school_drain_metal','particle':'lsmmod:block/dark_tiled_floor'}));write(A/'blockstates/school_drain.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/school_drain','y':i*90} for i,f in enumerate(('north','east','south','west'))}});basic('school_drain','school_drain')
 models=[]
 textures={'shaft':'lsmmod:block/dark_school_wall','base':'lsmmod:block/classroom_floor','particle':'lsmmod:block/dark_school_wall'}
 def column_mesh(parts,fixed=False):
  value=model(parts,textures)
  for element in value['elements']:
   for face in list(element['faces']):
    surface=element['faces'][face];surface.pop('cullface',None)
    axis={'north':2,'south':2,'west':0,'east':0,'up':1,'down':1}[face]
    lo,hi=element['from'],element['to'];plane=lo[axis] if face in ('north','west','down') else hi[axis]
    # Remove caps where the fixed cylinder meets a conditional cardinal sector.
    if fixed and ((face in ('north','south') and plane==(0 if face=='north' else 16) and 0<=lo[0]<hi[0]<=16) or (face in ('west','east') and plane==(0 if face=='west' else 16) and 0<=lo[2]<hi[2]<=16)):
     del element['faces'][face];continue
    if face in ('up','down') and plane in (0,16) and 0<=lo[0]<hi[0]<=16 and 0<=lo[2]<hi[2]<=16:surface['cullface']=face
    # World-aligned pixels instead of stretching an entire texture over each circular strip.
    ox=math.floor(lo[0]/16)*16;oz=math.floor(lo[2]/16)*16
    x0,x1=lo[0]-ox,hi[0]-ox;z0,z1=lo[2]-oz,hi[2]-oz
    surface['uv']={'north':[16-x1,16-hi[1],16-x0,16-lo[1]],'south':[x0,16-hi[1],x1,16-lo[1]],'west':[z0,16-hi[1],z1,16-lo[1]],'east':[16-z1,16-hi[1],16-z0,16-lo[1]],'up':[x0,z0,x1,z1],'down':[x0,16-z1,x1,16-z0]}[face]
   # An element may consist only of a removed internal cap.
  value['elements']=[e for e in value['elements'] if e['faces']]
  return fusion_base(explicit_materials(value))
 for base in (False,True):
  name='school_circular_column_shaft'+('_raised' if base else '')
  write(A/f'models/block/{name}.json',column_mesh([(b,'shaft') for b in vertical(FIXED,2 if base else 0,16)],True))
 models.append([{'model':'lsmmod:block/school_circular_column_shaft_raised','condition':{'type':'lsmmod:column_part','side':-1,'quarter':0,'paint':0}},{'model':'lsmmod:block/school_circular_column_shaft'}])
 write(A/'models/block/school_circular_column_base.json',column_mesh([(b,'base') for b in vertical(FIXED,0,2)],True))
 models.append({'model':'lsmmod:block/school_circular_column_base','condition':{'type':'lsmmod:column_part','side':-1,'quarter':0,'paint':0}})
 write(A/'models/block/school_circular_column_empty.json',{'loader':'fusion:model','type':'base','textures':{'particle':textures['particle']},'elements':[]})
 for q in range(4):
  for side,direction in enumerate(('north','east','south','west')):
   suffix='' if side==0 else '_'+direction;worldq=q^1 if side>=2 else q
   series=[]
   for height in (16,12):
    if height==16 or q<2:ident='school_circular_column_empty'
    else:
     ident=f'school_circular_column_outer_{q}{suffix}_railing'
     write(A/f'models/block/{ident}.json',oriented_model(column_mesh([(b,'shaft') for b in outer_boxes(q,12)]),side))
    series.append({'model':'lsmmod:block/'+ident,'condition':{'type':'lsmmod:column_part','side':side,'quarter':worldq,'paint':-1,'height':height}})
   for base in ((True,False) if q<2 else (False,)):
    name=f'school_circular_column_outer_{q}{suffix}'+('_base' if base else '')
    parts=[(b,'shaft') for b in vertical(connectors(q),2 if base else 0,16)]+([(b,'base') for b in vertical(connectors(q),0,2)] if base else [])
    write(A/f'models/block/{name}.json',oriented_model(column_mesh(parts),side))
    entry={'model':'lsmmod:block/'+name}
    if base:entry['condition']={'type':'lsmmod:column_part','side':side,'quarter':worldq,'paint':-1,'height':0,'base':1}
    series.append(entry)
   models.append(series)
   # The cut plane adopts the material of the neighboring face; no railing is copied.
   caps=[]
   for height in ((16,12) if q>=2 else (16,)):
    name=f'school_circular_column_join_{q}{suffix}'+('_railing' if height==12 else '')
    b=[8*(q%2),8*(q//2),0,8+8*(q%2),min(8+8*(q//2),height),.001]
    cap=column_mesh([(b,'shaft')]);cap['elements']=[e for e in cap['elements'] if 'north' in e['faces']]
    for element in cap['elements']:
     surface=element['faces']['north'];surface['texture']='#column_join_surface';surface['cullface']='north'
    cap['textures']['column_join_surface']='lsmmod:block/light_school_wall'
    write(A/f'models/block/{name}.json',oriented_model(cap,side))
    for color,texture in enumerate(COLORS):
     child=f'school_circular_column_join_{q}_{color}{suffix}'+('_railing' if height==12 else '')
     write(A/f'models/block/{child}.json',{'loader':'fusion:model','type':'base','parent':'lsmmod:block/'+name,'textures':{'column_join_surface':'#column_'+texture.replace(':','_').replace('/','_'),'particle':texture}})
     condition={'type':'lsmmod:column_part','side':side,'quarter':worldq,'paint':color}
     if q>=2:condition['height']=height
     caps.append({'model':'lsmmod:block/'+child,'condition':condition})
   models.append(caps)
 write(A/'models/block/school_circular_column.json',{'loader':'fusion:model','type':'composite','textures':{**textures,**{'column_'+t.replace(':','_').replace('/','_'):t for t in COLORS}},'models':models})
 write(A/'blockstates/school_circular_column.json',{'variants':{'':{'model':'lsmmod:block/school_circular_column'}}})
 # Inventory uses a single complete cylinder scaled to fit a normal block icon.
 item=column_mesh([(b,'shaft') for b in vertical(ALL,2,16)]+[(b,'base') for b in vertical(ALL,0,2)])
 for element in item['elements']:
  for key in ('from','to'):element[key]=[8+(element[key][0]-8)/2,element[key][1],8+(element[key][2]-8)/2]
 write(A/'models/item/school_circular_column.json',item);basic('school_circular_column','school_circular_column');write(A/'items/school_circular_column.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/school_circular_column'}})
 java='package net.nicomar2009.lsmmod.block;\n\n/** Generated mesh-identical physical boxes. */\npublic final class SchoolDrainColumnShapes {\n private SchoolDrainColumnShapes() {}\n'
 for name,boxes in [('DRAIN',[b[0] for b in DRAIN]),('SHAFT',CIRCLE),('FIXED',FIXED)]:java+=' public static final double[][] '+name+' = '+json.dumps(boxes).replace('[','{').replace(']','}')+';\n'
 java+=' public static final double[][][][] OUTER = '+json.dumps([[outer_boxes(q,height) for height in (0,12,16)] for q in range(4)]).replace('[','{').replace(']','}')+';\n}\n';p=J/'block/SchoolDrainColumnShapes.java';p.write_text(java);GENERATED.append(str(p.relative_to(ROOT)))
 registrations='''    public static final DeferredBlock<Block> DARK_TILED_FLOOR = BLOCKS.registerBlock("dark_tiled_floor", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<SchoolDrainBlock> SCHOOL_DRAIN = BLOCKS.registerBlock("school_drain", SchoolDrainBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<SchoolCircularColumnBlock> SCHOOL_CIRCULAR_COLUMN = BLOCKS.registerBlock("school_circular_column", SchoolCircularColumnBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape().requiresCorrectToolForDrops());'''
 for filename,body in [('ModBlocks.java',registrations),('ModItems.java','\n'.join(f'    public static final DeferredItem<BlockItem> {id.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{id.upper()});' for id in ('dark_tiled_floor','school_drain','school_circular_column')))]:
  p=J/'registry'/filename;s=p.read_text();a='    // BEGIN DARK TILE DRAIN COLUMN';z='    // END DARK TILE DRAIN COLUMN';section=a+'\n'+body+'\n'+z
  if a in s:start=s.index(a);end=s.index(z)+len(z);s=s[:start]+section+s[end:]
  else:s=s.replace('    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR',section+'\n\n    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR',1)
  p.write_text(s);GENERATED.append(str(p.relative_to(ROOT)))
 for locale,names in [('es_es',('Baldosas oscuras','Desagüe del colegio','Columna circular del colegio')),('en_us',('Dark Tiled Floor','School Drain','School Circular Column'))]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v.update({f'block.lsmmod.{id}':n for id,n in zip(('dark_tiled_floor','school_drain','school_circular_column'),names)});write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+id for id in ('dark_tiled_floor','school_drain','school_circular_column')]));write(p,v)
 Path('/tmp/drain-generated.json').write_text(json.dumps(GENERATED))
if __name__=='__main__':main()
