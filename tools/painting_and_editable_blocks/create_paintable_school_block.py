"""One painted cube state, reusable face models, and texture-only tools."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, module_write_text
from resource_json import resource_json
from resource_modules import output_path, write_resource
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2];A=ROOT/'src/main/resources/assets/lsmmod'
SIDES=('down','up','north','south','west','east')
PAINTS=('minecraft:block/light_gray_concrete','lsmmod:block/light_school_wall',
        'lsmmod:block/dark_school_wall','lsmmod:block/street_school_wall','lsmmod:block/classroom_white_wall','lsmmod:block/classroom_floor','lsmmod:block/yellow_school_block','lsmmod:block/white_playground_floor','lsmmod:block/gray_playground_floor','lsmmod:block/green_playground_floor','lsmmod:block/orange_playground_floor','lsmmod:block/dark_tiled_floor','lsmmod:block/red_street_wall','lsmmod:block/school_laboratory_tile','lsmmod:block/laboratory_table_tile','lsmmod:block/auditorium_parquet')
TOOLS=('light_school','dark_school','street_school','classroom_white','classroom_floor_paint','yellow_school_paint','white_playground_floor_paint','gray_playground_floor_paint','green_playground_floor_paint','orange_playground_floor_paint','dark_tiled_floor_paint','red_street_wall_paint','school_laboratory_tile_paint','laboratory_table_tile_paint','auditorium_parquet_paint','paint_cleaner')
def write(p,d):write_resource(p,d)
def main():
 series=[]
 for face in SIDES:
  entries=[]
  for paint,texture in enumerate(PAINTS):
   name=f'paintable_faces/{face}_{paint}'
   write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid',
      'textures':{'surface':texture,'particle':'#surface'},'elements':[{'from':[0,0,0],'to':[16,16,16],
      'faces':{face:{'texture':'#surface','uv':[0,0,16,16],'cullface':face}}}]})
   entry={'model':'lsmmod:block/'+name}
   if paint:entry['condition']={'type':'lsmmod:painted_face','face':face,'paint':paint}
   entries.append(entry)
  series.append(entries[1:]+entries[:1])
 ident='school_paintable_block'
 write(A/f'models/block/{ident}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},'models':series})
 write(A/f'blockstates/{ident}.json',{'variants':{'':{'model':'lsmmod:block/'+ident}}})
 write(A/f'models/item/{ident}.json',{'parent':'minecraft:block/cube_all','textures':{'all':PAINTS[0]}})
 write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
 write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
 for item,texture in zip(TOOLS,PAINTS[1:]+('minecraft:item/brush',)):
  write(A/f'models/item/{item}.json',{'parent':'minecraft:item/generated','textures':{'layer0':texture}})
  write(A/f'items/{item}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+item}})
 block='    public static final DeferredBlock<SchoolPaintableBlock> SCHOOL_PAINTABLE_BLOCK = BLOCKS.registerBlock(\n            "school_paintable_block", SchoolPaintableBlock::new,\n            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());'
 items=['    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_BLOCK);']
 for name,color in zip(TOOLS[:-1],('LIGHT','DARK','STREET','WHITE','FLOOR','YELLOW','PLAYGROUND_WHITE','PLAYGROUND_GRAY','PLAYGROUND_GREEN','PLAYGROUND_ORANGE','DARK_TILED','RED_STREET','LABORATORY_TILE','LABORATORY_TABLE_TILE','AUDITORIUM_PARQUET')):
  items.append(f'    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> {name.upper()} = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.{name.upper()};')
 items.append('    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.PaintCleanerItem> PAINT_CLEANER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.PAINT_CLEANER;')
 for filename,body in [('ModBlocks.java',block),('ModItems.java','\n'.join(items))]:
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=module_read_text(p);start='    // BEGIN PAINTABLE SCHOOL BLOCK';end='    // END PAINTABLE SCHOOL BLOCK';section=start+'\n'+body+'\n'+end+'\n\n'
  if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section.rstrip()+s[b:]
  else:a=s.index('    // BEGIN CLASSROOM SURFACES');s=s[:a]+section+s[a:]
  module_write_text(p, s)
 for locale in ['en_us','es_es']:
  p=A/f'lang/{locale}.json';d=json.loads(module_read_text(p));d['block.lsmmod.school_paintable_block']='School Paintable Block' if locale=='en_us' else 'Bloque pintable del colegio'
  labels=('Light School Paint','Dark School Paint','Street School Paint','Classroom White Paint','Classroom Floor Paint','Yellow School Paint','White Playground Floor Paint','Gray Playground Floor Paint','Green Playground Floor Paint','Orange Playground Floor Paint','Dark Tiled Floor Paint','Red Street Wall Paint','Laboratory Tile Paint','Laboratory Table Tile Paint','Auditorium Parquet Paint','Paint Cleaner') if locale=='en_us' else ('Pintura clara del colegio','Pintura oscura del colegio','Pintura de pared de calle','Pintura blanca de aula','Pintura de suelo de aula','Pintura amarilla del colegio','Pintura de suelo claro del patio','Pintura de suelo gris del patio','Pintura de suelo verde del patio','Pintura de suelo naranja del patio','Pintura de baldosas oscuras','Pintura roja de pared de calle','Pintura de baldosas del laboratorio','Pintura de baldosas de mesa del laboratorio','Pintura de parquet del auditorio','Limpiador de pintura')
  for item,label in zip(TOOLS,labels):d['item.lsmmod.'+item]=label
  d['config.jade.plugin_lsmmod.painted_wall_name']='Painted block face: name and icon' if locale=='en_us' else 'Cara del bloque pintado: nombre e icono'
  d['message.lsmmod.paint_copied']='Six faces copied. Click another paintable block to apply once.' if locale=='en_us' else 'Se copiaron las seis caras. Haz clic en otro bloque pintable para aplicarlas una vez.'
  write(p,d)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(module_read_text(p));d['values']=list(dict.fromkeys(d['values']+['lsmmod:school_paintable_block']));write(p,d)
if __name__=='__main__':main()
