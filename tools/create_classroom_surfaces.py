"""Opaque painted classroom/street cubes and short artificial turf; no connection state."""
from resource_modules import module_read_text, module_write_text, output_path
from resource_json import resource_json
import json,random
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod'
IDS=('classroom_white_wall','school_synthetic_grass','red_street_wall')
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);module_write_text(p, resource_json(p, d, ensure_ascii=False)+'\n',encoding='utf-8')
def main():
 for ident in IDS:
  texture_name=ident
  rng=random.Random(260211);image=Image.new('RGB',(16,16))
  for y in range(16):
   for x in range(16):
    if ident==IDS[0]:base=(237,237,237);delta=rng.choice((-1,0,0,0,1))
    elif ident==IDS[1]:base=(83,126,62);delta=rng.choice((-7,-4,-2,0,0,2,4,7))
    else:
     # Muted red plaster matching the supplied color swatch, with subtle pixel grain.
     base=(174,95,95);delta=rng.choice((-3,-2,-1,0,0,0,1,2,3))
    image.putpixel((x,y),tuple(v+delta for v in base))
  if ident==IDS[1]:
   # Short pale/dark fibers; no dirt band and no biome tint behind the glass.
   for x,y in [(1,1),(6,3),(12,1),(3,8),(9,10),(14,7),(1,13),(6,14),(12,13)]:
    image.putpixel((x,y),(70,113,51));image.putpixel((x,y+1),(94,135,72))
  image.save(output_path(A/f'textures/block/{texture_name}.png'))
  if ident==IDS[0]: image.save(output_path(A/'textures/block/classroom_painted_block.png')) # Legacy texture alias.
  write(A/f'models/block/{ident}.json',{'parent':'minecraft:block/cube_all','textures':{'all':f'lsmmod:block/{texture_name}'}})
  write(A/f'blockstates/{ident}.json',{'variants':{'':{'model':f'lsmmod:block/{ident}'}}})
  write(A/f'models/item/{ident}.json',{'parent':f'lsmmod:block/{ident}'})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{ident}'}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':f'lsmmod:{ident}'}]}]})
 for filename,body,anchor in [
  ('ModBlocks.java','    public static final DeferredBlock<Block> CLASSROOM_WHITE_WALL = BLOCKS.registerBlock(\n            "classroom_white_wall", Block::new,\n            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());\n    // Kept as a registry alias for saved worlds and inventory stacks.\n    public static final DeferredBlock<Block> CLASSROOM_PAINTED_BLOCK = BLOCKS.registerBlock(\n            "classroom_painted_block", Block::new,\n            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());\n    public static final DeferredBlock<Block> SCHOOL_SYNTHETIC_GRASS = BLOCKS.registerBlock(\n            "school_synthetic_grass", Block::new,\n            props -> props.strength(.8F).sound(SoundType.MOSS));','    // BEGIN MIXED SCHOOL WALLS'),
  ('ModItems.java','    public static final DeferredItem<BlockItem> CLASSROOM_WHITE_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_WHITE_WALL);\n    public static final DeferredItem<BlockItem> CLASSROOM_PAINTED_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_PAINTED_BLOCK);\n    public static final DeferredItem<BlockItem> SCHOOL_SYNTHETIC_GRASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SYNTHETIC_GRASS);','    // BEGIN MIXED SCHOOL WALLS')]:
  if filename=='ModBlocks.java':body+='\n    public static final DeferredBlock<Block> RED_STREET_WALL = BLOCKS.registerBlock(\n            "red_street_wall", Block::new,\n            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());'
  else:body+='\n    public static final DeferredItem<BlockItem> RED_STREET_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.RED_STREET_WALL);'
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=module_read_text(p);start='    // BEGIN CLASSROOM SURFACES';end='    // END CLASSROOM SURFACES';section=start+'\n'+body+'\n'+end+'\n\n'
  if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section.rstrip()+s[b:]
  else:a=s.index(anchor);s=s[:a]+section+s[a:]
  module_write_text(p, s)
 for locale,labels in [('es_es',('Pared blanca de aula','Césped sintético del colegio','Pared roja de la calle')),('en_us',('Classroom White Wall','School Synthetic Grass','Red Street Wall'))]:
  p=A/f'lang/{locale}.json';d=json.loads(module_read_text(p))
  for ident,label in zip(IDS,labels):d['block.lsmmod.'+ident]=label
  write(p,d)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(module_read_text(p));d['values']=list(dict.fromkeys(d['values']+['lsmmod:classroom_white_wall','lsmmod:red_street_wall']));write(p,d)
if __name__=='__main__':main()
