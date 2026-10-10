"""Exact +8px duplicates of the six inverted flight pieces; original resources untouched."""
import json
from create_school_inverted_slopes import ROOT,A,pieces as originals
from create_school_railing_slopes import write
OFFSET=.5

def pieces():
 for source,col,row,low,rise in originals():
  if '_flight_' not in source:continue
  yield source.replace('_flight_','_flight_raised_'),source,col,row,low,rise

def shift_obj(text):
 out=[]
 for line in text.splitlines():
  f=line.split()
  if f and f[0]=='v':
   x,y,z=map(float,f[1:]);line='v '+' '.join(f'{v:.12g}' for v in (x,y+OFFSET,z))
  out.append(line)
 return '\n'.join(out)+'\n'

def main():
 blocks='    // BEGIN SCHOOL RAISED INVERTED SLOPES\n';items=blocks
 for ident,source,col,row,low,rise in pieces():
  (A/f'models/block/{ident}.obj').write_text(shift_obj((A/f'models/block/{source}.obj').read_text()))
  model=json.loads((A/f'models/block/{source}.json').read_text());model['model']=f'lsmmod:models/block/{ident}.obj';write(A/f'models/block/{ident}.json',model)
  states=json.loads((A/f'blockstates/{source}.json').read_text())
  for value in states['variants'].values():value['model']='lsmmod:block/'+ident
  write(A/f'blockstates/{ident}.json',states)
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
  blocks+=f'    public static final DeferredBlock<SchoolInvertedSlopeBlock> {ident.upper()} = BLOCKS.registerBlock(\n            "{ident}", props -> new SchoolInvertedSlopeBlock({low:.15g},{rise:.15g},0.5,props),\n            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());\n'
  items+=f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});\n'
 for name,section in (('ModBlocks.java',blocks),('ModItems.java',items)):
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/name;s=p.read_text();begin='    // BEGIN SCHOOL RAISED INVERTED SLOPES';end='    // END SCHOOL RAISED INVERTED SLOPES';section+=end+'\n'
  if begin in s:a=s.index(begin);b=s.index(end)+len(end)+1;s=s[:a]+section+s[b:]
  else:anchor=s.index('    public static final DeferredBlock<' if name=='ModBlocks.java' else '    public static final DeferredItem<');s=s[:anchor]+section+'\n'+s[anchor:]
  p.write_text(s)
 for locale in ('es_es','en_us'):
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text())
  for ident,*_ in pieces():
   number=ident.rsplit('_',1)[1];v['block.lsmmod.'+ident]=f'Pendiente invertida elevada 8 px: {number}' if locale=='es_es' else f'Raised Inverted Flight Slope (+8 px): {number}'
  write(p,v)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+x[0] for x in pieces()]));write(p,v)
 write(ROOT/'tools/school_raised_inverted_slope_layout.json',{'vertical_offset':OFFSET,'pixels':8,'pieces':[{'id':ident,'source':source,'column':col,'row':row} for ident,source,col,row,low,rise in pieces()]})
if __name__=='__main__':main()
