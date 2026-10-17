"""One glass stair with an editable 8px slab and shared glass/curtain geometry."""
from resource_modules import module_glob, module_read_text, module_rglob, module_unlink, module_write_text
import itertools,json,re
from pathlib import Path
from create_school_glass_assets import ROOT,A,D,T,write,item_and_loot
from create_horizontal_glazing_assets import glazing
from create_bathroom_statue_assets import solid_model
from create_paintable_school_block import PAINTS

ID='school_paintable_glass_stairs'
RETIRED=('supported_school_glass','mixed_school_glass','supported_school_glass_reversed','mixed_school_glass_reversed',
 'white_dark_school_glass','white_street_school_glass','dark_white_school_glass','dark_street_school_glass',
 'light_white_school_glass','light_dark_school_glass','light_street_school_glass','street_light_school_glass')
QUARTERS={'straight':[(0,0),(1,0)],'outer_left':[(0,0)],'outer_right':[(1,0)],
 'inner_left':[(0,0),(1,0),(0,1)],'inner_right':[(0,0),(1,0),(1,1)]}

def glass_parts(half,shape,mask):
 low,high=(8,16) if half=='bottom' else (0,8)
 bottom,top=bool(mask&16),bool(mask&8)
 if shape=='straight':return repair_glass_caps(glazing(mask&7,low,high,bottom,top,[]),mask)
 parts=[]
 for zrow in (0,1):
  xs=[x for x,z in QUARTERS[shape] if z==zrow]
  if not xs:continue
  x0,x1=min(xs)*8,(max(xs)+1)*8
  parts.extend([([x0,low,zrow*8,x0+1,high,zrow*8+8],'frame'),([x1-1,low,zrow*8,x1,high,zrow*8+8],'frame')])
  if not bottom:parts.append(([x0+1,low,zrow*8,x1-1,low+1,zrow*8+8],'frame'))
  if not top:parts.append(([x0+1,high-1,zrow*8,x1-1,high,zrow*8+8],'frame'))
  parts.append(([x0+1,low if bottom else low+1,zrow*8,x1-1,high if top else high-1,zrow*8+8],'glass'))
 opaque=solid_model([p for p in parts if p[1]!='glass'])['elements']
 transparent=[e for e in solid_model(parts)['elements'] if next(iter(e['faces'].values()))['texture']=='#glass']
 for e in opaque+transparent:
  lo,hi=e['from'],e['to']
  for face,s in e['faces'].items():
   s['uv']={'north':[16-hi[0],16-hi[1],16-lo[0],16-lo[1]],'south':[lo[0],16-hi[1],hi[0],16-lo[1]],
    'east':[16-hi[2],16-hi[1],16-lo[2],16-lo[1]],'west':[lo[2],16-hi[1],hi[2],16-lo[1]],
    'up':[lo[0],lo[2],hi[0],hi[2]],'down':[16-hi[0],lo[2],16-lo[0],hi[2]]}[face]
 return repair_glass_caps(opaque+transparent,mask)

def repair_glass_caps(elements,mask):
 # Whole-block side culling is disabled to keep carved slab faces visible. Remove
 # only the transparent caps at real glazing joins, retaining opaque frame faces.
 result=[]
 for element in elements:
  for face,surface in list(element['faces'].items()):
   if surface['texture']!='#glass':continue
   if (face=='west' and element['from'][0]==0 and not mask&1
       or face=='east' and element['to'][0]==16 and not mask&2
       or face=='up' and element['to'][1]==16 and mask&8
       or face=='down' and element['from'][1]==0 and mask&16):
    del element['faces'][face]
  if element['faces']:result.append(element)
 return result

def retire():
 j=ROOT/'src/main/java/net/nicomar2009/lsmmod'
 for name in RETIRED:
  for directory in (A/'blockstates',A/'items',A/'models/block',A/'models/item',D/'lsmmod/loot_table/blocks'):
   for path in module_glob(directory, name+'*.json'):module_unlink(path)
  for path in module_glob(A/'models/block/school_glazing_dynamic', name+'*.json'):module_unlink(path)
  for filename in ('ModBlocks.java','ModItems.java'):
   path=j/'registry'/filename;s=module_read_text(path)
   s=re.sub(r'    public static final Deferred(?:Block|Item)<[^\n]+> '+name.upper()+r'\s*=.*?;\n', '',s,flags=re.S)
   module_write_text(path, s)
 for locale in ('es_es','en_us'):
  path=A/f'lang/{locale}.json';data=json.loads(module_read_text(path))
  for name in RETIRED:data.pop('block.lsmmod.'+name,None)
  write(path,data)
 for path in module_rglob(D/'minecraft/tags/block', '*.json'):
  data=json.loads(module_read_text(path));values=data.get('values',[])
  filtered=[v for v in values if not isinstance(v,str) or v.removeprefix('lsmmod:') not in RETIRED]
  if filtered!=values:data['values']=filtered;write(path,data)
 for path in module_glob(A/'textures/block', 'school_stair_glass_*.png'):module_unlink(path)

def main():
 import shutil
 shutil.rmtree(A/f'models/block/{ID}',ignore_errors=True)
 retire();multipart=[{'apply':{'model':'lsmmod:block/school_paintable_custom_block'}}];shared={}
 for half,shape in itertools.product(('bottom','top'),QUARTERS):
  selectors=[]
  for mask in range(32):
   groups=[];elements=glass_parts(half,shape,mask)
   for layer,material in (('solid','frame'),('translucent','glass')):
    subset=[e for e in elements if next(iter(e['faces'].values()))['texture']=='#'+material]
    value={'parent':'minecraft:block/block','render_type':'minecraft:'+layer,'textures':dict(T,particle=T['frame']),'elements':subset}
    if not subset:continue
    key=json.dumps(value,sort_keys=True)
    if key not in shared:
     name=ID+'/parts/'+str(len(shared));shared[key]='lsmmod:block/'+name;write(A/f'models/block/{name}.json',value)
    groups.append([shared[key]])
   name=f'{ID}/{half}_{shape}_{mask}'
   write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':T['frame']},'models':groups})
   selectors.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:glazing_mask','mask':mask}})
  name=f'{ID}/{half}_{shape}'
  write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':T['frame']},'models':[selectors]})
  for turn,facing in enumerate(('north','east','south','west')):
   multipart.append({'when':dict(half=half,shape=shape,facing=facing),'apply':{'model':'lsmmod:block/'+name,'y':turn*90}})
   band='upper' if half=='bottom' else 'lower'
   corner='_inner_left' if shape=='inner_left' else '_inner_right' if shape=='inner_right' else ''
   for horizontal,vertical in itertools.product(('single','left','middle','right'),repeat=2):
    suffix='' if vertical=='single' else '_vertical_'+vertical
    multipart.append({'when':dict(half=half,shape=shape,facing=facing,curtain=horizontal+'_'+vertical),
     'apply':{'model':f'lsmmod:block/school_curtain_{band}{corner}_{horizontal}{suffix}','y':turn*90}})
 write(A/f'blockstates/{ID}.json',{'multipart':multipart})
 slab={'parent':'minecraft:block/block','render_type':'minecraft:solid','textures':{'surface':PAINTS[0],'particle':PAINTS[0]},
  'elements':[{'from':[0,0,0],'to':[16,8,16],'faces':{face:{'texture':'#surface','uv':uv} for face,uv in
   {'north':[16,8,0,16],'south':[0,8,16,16],'east':[16,8,0,16],'west':[0,8,16,16],'up':[0,0,16,16],'down':[16,0,0,16]}.items()}}]}
 write(A/f'models/item/{ID}_slab.json',slab)
 write(A/f'models/item/{ID}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},
  'models':[[f'lsmmod:item/{ID}_slab'],[f'lsmmod:block/{ID}/bottom_straight_3']]})
 item_and_loot(ID)
 j=ROOT/'src/main/java/net/nicomar2009/lsmmod'
 for filename,entry in (
  ('ModBlocks.java',f'    public static final DeferredBlock<SchoolPaintableGlassStairBlock> SCHOOL_PAINTABLE_GLASS_STAIRS = BLOCKS.registerBlock("{ID}", SchoolPaintableGlassStairBlock::new, props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().dynamicShape());'),
  ('ModItems.java','    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_GLASS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS);')):
  path=j/'registry'/filename;s=module_read_text(path)
  if ' SCHOOL_PAINTABLE_GLASS_STAIRS =' not in s:s=s.replace('    // BEGIN FACILITY EXPANSIONS',entry+'\n\n    // BEGIN FACILITY EXPANSIONS',1)
  module_write_text(path, s)
 path=j/'registry/ModBlockEntities.java';s=module_read_text(path)
 if ' SCHOOL_GLASS_STAIR_PAINT =' not in s:
  entry='    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolGlassStairPaintEntity>> SCHOOL_GLASS_STAIR_PAINT = BLOCK_ENTITIES.register("school_glass_stair_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolGlassStairPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS.get()));\n\n'
  s=s.replace('    // Only the LEFT half',entry+'    // Only the LEFT half',1);module_write_text(path, s)
 for locale,label in (('es_es','Escalera con cristal y base pintable'),('en_us','Glass Stairs with Paintable Base')):
  path=A/f'lang/{locale}.json';d=json.loads(module_read_text(path));d['block.lsmmod.'+ID]=label;write(path,d)
 path=D/'minecraft/tags/block/mineable/pickaxe.json';d=json.loads(module_read_text(path))
 if 'lsmmod:'+ID not in d['values']:d['values'].append('lsmmod:'+ID)
 write(path,d)

if __name__=='__main__':main()
