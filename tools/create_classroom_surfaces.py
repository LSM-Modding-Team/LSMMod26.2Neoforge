"""Opaque painted classroom cube and short artificial turf; no connection state."""
import json,random
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod'
IDS=('classroom_white_wall','school_synthetic_grass')
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
def main():
 for ident in IDS:
  texture_name='classroom_painted_block' if ident==IDS[0] else ident
  rng=random.Random(260211);image=Image.new('RGB',(16,16))
  for y in range(16):
   for x in range(16):
    delta=rng.choice((-1,0,0,0,1)) if ident==IDS[0] else rng.choice((-7,-4,-2,0,0,2,4,7))
    base=(237,237,237) if ident==IDS[0] else (131,181,107)
    image.putpixel((x,y),tuple(v+delta for v in base))
  if ident==IDS[1]:
   # Short pale/dark fibers; no dirt band and no biome tint behind the glass.
   for x,y in [(1,1),(6,3),(12,1),(3,8),(9,10),(14,7),(1,13),(6,14),(12,13)]:
    image.putpixel((x,y),(118,168,96));image.putpixel((x,y+1),(142,190,117))
  image.save(A/f'textures/block/{texture_name}.png')
  write(A/f'models/block/{ident}.json',{'parent':'minecraft:block/cube_all','textures':{'all':f'lsmmod:block/{texture_name}'}})
  write(A/f'blockstates/{ident}.json',{'variants':{'':{'model':f'lsmmod:block/{ident}'}}})
  write(A/f'models/item/{ident}.json',{'parent':f'lsmmod:block/{ident}'})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{ident}'}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':f'lsmmod:{ident}'}]}]})
 for filename,body,anchor in [
  ('ModBlocks.java','    public static final DeferredBlock<Block> CLASSROOM_WHITE_WALL = BLOCKS.registerBlock(\n            "classroom_white_wall", Block::new,\n            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());\n    // Kept as a registry alias for saved worlds and inventory stacks.\n    public static final DeferredBlock<Block> CLASSROOM_PAINTED_BLOCK = BLOCKS.registerBlock(\n            "classroom_painted_block", Block::new,\n            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());\n    public static final DeferredBlock<Block> SCHOOL_SYNTHETIC_GRASS = BLOCKS.registerBlock(\n            "school_synthetic_grass", Block::new,\n            props -> props.strength(.8F).sound(SoundType.WOOL));','    // BEGIN MIXED SCHOOL WALLS'),
  ('ModItems.java','    public static final DeferredItem<BlockItem> CLASSROOM_WHITE_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_WHITE_WALL);\n    public static final DeferredItem<BlockItem> CLASSROOM_PAINTED_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_PAINTED_BLOCK);\n    public static final DeferredItem<BlockItem> SCHOOL_SYNTHETIC_GRASS = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SYNTHETIC_GRASS);','    // BEGIN MIXED SCHOOL WALLS')]:
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text();start='    // BEGIN CLASSROOM SURFACES';end='    // END CLASSROOM SURFACES';section=start+'\n'+body+'\n'+end+'\n\n'
  if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section.rstrip()+s[b:]
  else:a=s.index(anchor);s=s[:a]+section+s[a:]
  p.write_text(s)
 for locale,labels in [('es_es',('Pared blanca de aula','Césped sintético del colegio')),('en_us',('Classroom White Wall','School Synthetic Grass'))]:
  p=A/f'lang/{locale}.json';d=json.loads(p.read_text())
  for ident,label in zip(IDS,labels):d['block.lsmmod.'+ident]=label
  write(p,d)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(p.read_text());d['values']=list(dict.fromkeys(d['values']+['lsmmod:classroom_white_wall']));write(p,d)
if __name__=='__main__':main()
