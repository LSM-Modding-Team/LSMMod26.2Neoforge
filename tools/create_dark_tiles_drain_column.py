"""Low-state dark tiles, open drain, and shared per-quadrant circular-column connections."""
import json, math, random, copy
from pathlib import Path
from PIL import Image
from create_bathroom_statue_assets import solid_model
ROOT=Path(__file__).resolve().parents[1]; A=ROOT/'src/main/resources/assets/lsmmod'; D=ROOT/'src/main/resources/data'; J=ROOT/'src/main/java/net/nicomar2009/lsmmod'; GENERATED=[]
from create_paintable_school_block import PAINTS
COLORS=PAINTS
DRAIN=[([0,0,0,16,8,16],'gray'),([8,8,0,16,16,16],'gray',{'up':'tile'})]+[([0,15,z,8,16,z+1],'metal') for z in (0,3,6,9,12,15)]
# A pixel-friendly circular cross-section sampled at half-pixel intervals. Model and physics use exactly the same strips.
CIRCLE=[]
for i in range(24):
 x=2+i*.5; edge=8-math.sqrt(36-(x+.25-8)**2); edge=round(edge*2)/2
 CIRCLE.append([x,0,edge,x+.5,16,16-edge])
def connectors(q):
 return [[b[0],8*(q//2),0,b[3],8+8*(q//2),b[2]] for b in CIRCLE if (b[0]<8)==(q%2==0) and b[2]>0]
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
def basic(id,root):
 write(A/f'items/{id}.json',{'model':{'type':'minecraft:model','model':'lsmmod:block/'+root}})
 write(D/f'lsmmod/loot_table/blocks/{id}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+id}]}]})
def main():
 im=Image.open(A/'textures/block/classroom_floor.png').convert('RGB'); vals=[sum(c)/3 for c in im.getdata()]; average=sum(vals)/len(vals)
 im.putdata([tuple(max(0,min(255,round(101+(v-average)*.7)+offset)) for offset in (0,1,2)) for v in vals]);p=A/'textures/block/dark_tiled_floor.png';im.save(p);GENERATED.append(str(p.relative_to(ROOT)))
 rng=random.Random(239);im=Image.new('RGB',(16,16));im.putdata([tuple(c+rng.choice((-2,-1,0,1,2)) for c in (43,46,49)) for _ in range(256)]);p=A/'textures/block/school_drain_metal.png';im.save(p);GENERATED.append(str(p.relative_to(ROOT)))
 write(A/'models/block/dark_tiled_floor.json',{'parent':'minecraft:block/cube_all','textures':{'all':'lsmmod:block/dark_tiled_floor'}});write(A/'blockstates/dark_tiled_floor.json',{'variants':{'':{'model':'lsmmod:block/dark_tiled_floor'}}});basic('dark_tiled_floor','dark_tiled_floor')
 write(A/'models/block/school_drain.json',model(DRAIN,{'gray':'lsmmod:block/bathroom_floor_side','tile':'lsmmod:block/dark_tiled_floor','metal':'lsmmod:block/school_drain_metal','particle':'lsmmod:block/dark_tiled_floor'}));write(A/'blockstates/school_drain.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/school_drain','y':i*90} for i,f in enumerate(('north','east','south','west'))}});basic('school_drain','school_drain')
 models=[{'model':'lsmmod:block/school_circular_column_shaft'},{'model':'lsmmod:block/school_circular_column_base','condition':{'type':'lsmmod:column_part','side':-1,'quarter':0,'paint':0}}]
 write(A/'models/block/school_circular_column_shaft.json',explicit_materials(model([(b,'shaft') for b in CIRCLE],{'shaft':'lsmmod:block/dark_school_wall','particle':'lsmmod:block/dark_school_wall'})))
 # Base top surface is clipped against the shaft, so there are no overlapping cap faces.
 base=model([([0,0,0,16,2,16],'base')]+[(b,'shaft') for b in CIRCLE],{'base':'lsmmod:block/classroom_floor','shaft':'lsmmod:block/dark_school_wall','particle':'lsmmod:block/classroom_floor'})
 # Retain only base-material surfaces; shaft is shared independently.
 base['elements']=[e for e in base['elements'] if next(iter(e['faces'].values()))['texture']=='#base'];write(A/'models/block/school_circular_column_base.json',explicit_materials(base))
 for q in range(4):
  join=model([(b,'wall') for b in connectors(q)],{'wall':'lsmmod:block/light_school_wall','particle':'lsmmod:block/light_school_wall'})
  for e in join['elements']:
   for f in ('up','down'):
    if f in e['faces']:e['faces'][f].pop('cullface',None)
  write(A/f'models/block/school_circular_column_join_{q}.json',join)
  for color,texture in enumerate(COLORS):
   name=f'school_circular_column_join_{q}_{color}';leaf=copy.deepcopy(join);leaf['textures']={'wall':texture,'particle':texture};write(A/f'models/block/{name}.json',explicit_materials(leaf))
   for side in range(4):
    worldq=(q^1) if side>=2 else q
    models.append({'model':'lsmmod:block/'+name,'transform':[{'type':'rotate','axis':'y','angle':-side*90,'origin':[8,8,8]}],'condition':{'type':'lsmmod:column_part','side':side,'quarter':worldq,'paint':color}})
 groups={}
 for entry in models[2:]:
  c=entry['condition'];groups.setdefault((c['side'],c['quarter']),[]).append(entry)
 models=models[:2]+[sorted(series,key=lambda e:e['condition']['paint']==0) for series in groups.values()]
 write(A/'models/block/school_circular_column.json',{'loader':'fusion:model','type':'composite','textures':{'shaft':'lsmmod:block/dark_school_wall','base':'lsmmod:block/classroom_floor','particle':'lsmmod:block/dark_school_wall',**{'column_'+t.replace(':','_').replace('/','_'):t for t in COLORS}},'models':models})
 write(A/'blockstates/school_circular_column.json',{'variants':{'':{'model':'lsmmod:block/school_circular_column'}}})
 item=model([([0,0,0,16,2,16],'base')]+[(b,'shaft') for b in CIRCLE],{'base':'lsmmod:block/classroom_floor','shaft':'lsmmod:block/dark_school_wall','particle':'lsmmod:block/dark_school_wall'});write(A/'models/item/school_circular_column.json',explicit_materials(item));basic('school_circular_column','school_circular_column');write(A/'items/school_circular_column.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/school_circular_column'}})
 java='package net.nicomar2009.lsmmod.block;\n\n/** Generated mesh-identical physical boxes. */\npublic final class SchoolDrainColumnShapes {\n private SchoolDrainColumnShapes() {}\n'
 for name,boxes in [('DRAIN',[b[0] for b in DRAIN]),('SHAFT',CIRCLE)]:java+=' public static final double[][] '+name+' = '+json.dumps(boxes).replace('[','{').replace(']','}')+';\n'
 java+=' public static final double[][][] JOINS = '+json.dumps([connectors(q) for q in range(4)]).replace('[','{').replace(']','}')+';\n}\n';p=J/'block/SchoolDrainColumnShapes.java';p.write_text(java);GENERATED.append(str(p.relative_to(ROOT)))
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
