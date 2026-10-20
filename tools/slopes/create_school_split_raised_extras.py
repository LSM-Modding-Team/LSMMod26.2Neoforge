"""Fill split columns 2..4 with elevated inverted slopes only."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, module_write_text
import json,math
from slopes.create_school_inverted_slopes import ROOT,A,inverted_obj
from slopes.create_school_raised_inverted_slopes import shift_obj,OFFSET
from slopes.create_school_railing_slopes import write

def pieces():
 for number,col in enumerate(range(2,5),1):
  row=-math.ceil(5*col/7)
  lower=-5*col/7-row
  top=math.ceil(lower+OFFSET)
  yield f'school_inverted_slope_split_raised_extra_{number}',col,row,lower,5/7,top

def main():
 blocks='    // BEGIN SCHOOL SPLIT RAISED EXTRAS\n';items=blocks
 template='school_inverted_slope_split_raised_1'
 for ident,col,row,lower,rise,top in pieces():
  module_write_text(A/f'models/block/{ident}.obj', shift_obj(inverted_obj(.75-lower,rise),top))
  model=json.loads(module_read_text(A/f'models/block/{template}.json'));model['model']=f'lsmmod:models/block/{ident}.obj';write(A/f'models/block/{ident}.json',model)
  states=json.loads(module_read_text(A/f'blockstates/{template}.json'))
  for value in states['variants'].values():value['model']='lsmmod:block/'+ident
  write(A/f'blockstates/{ident}.json',states)
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
  blocks+=f'    public static final DeferredBlock<SchoolInvertedSlopeBlock> {ident.upper()} = BLOCKS.registerBlock(\n            "{ident}", props -> new SchoolInvertedSlopeBlock({lower:.15g},{rise:.15g},0.5,{top:.1f},props),\n            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());\n'
  items+=f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});\n'
 for name,section in (('ModBlocks.java',blocks),('ModItems.java',items)):
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/name;s=module_read_text(p);begin='    // BEGIN SCHOOL SPLIT RAISED EXTRAS';end='    // END SCHOOL SPLIT RAISED EXTRAS';section+=end+'\n'
  if begin in s:a=s.index(begin);b=s.index(end)+len(end)+1;s=s[:a]+section+s[b:]
  else:anchor=s.index('    // BEGIN SCHOOL RAISED INVERTED SLOPES');s=s[:anchor]+section+'\n'+s[anchor:]
  module_write_text(p, s)
 for locale in ('es_es','en_us'):
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p))
  for ident,*_ in pieces():
   number=ident.rsplit('_',1)[1];v['block.lsmmod.'+ident]=f'Pendiente invertida elevada: split extra {number}' if locale=='es_es' else f'Raised Inverted Split Slope: Extra {number}'
  write(p,v)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+x[0] for x in pieces()]));write(p,v)
 write(ROOT/'tools/slopes/school_split_raised_extra_layout.json',{'vertical_offset':OFFSET,'rise_per_column':5/7,'pieces':[{'id':ident,'column':col,'row':row,'lower_start':lower,'rise':rise,'flat_top_y':top} for ident,col,row,lower,rise,top in pieces()]})
if __name__=='__main__':main()
