"""Y-inverted copies of both wall slope families, with no metal and a Y=0 start."""
import json
from create_school_railing_slopes import ROOT,A,pieces as originals,obj,write

def pieces():
 for ident,col,row,low,rise,post in originals():
  yield ident.replace('school_railing_slope_','school_inverted_slope_'),col,-row,.75-low,rise

def inverted_obj(original_low,rise):
 source=obj(original_low,rise,False,False);vertices=[];uv=[];faces=[];material=None
 for line in source.splitlines():
  f=line.split()
  if not f:continue
  if f[0]=='v':vertices.append(tuple(map(float,f[1:])))
  elif f[0]=='vt':uv.append(tuple(map(float,f[1:])))
  elif f[0]=='usemtl':material=f[1]
  elif f[0]=='f' and material=='wall':faces.append(f[1:][::-1])
 # Only the wall's first 24 vertices/UVs are retained. Reflection reverses winding.
 out=['# Wall only: inclined profile reflected; flat top extended from 12 px to 16 px.','mtllib school_inverted_slopes.mtl','usemtl wall']
 out+=['v '+' '.join(f'{v:.12g}' for v in (x,1.0 if y==0 else .75-y,z)) for x,y,z in vertices[:24]]
 out+=['vt '+' '.join(f'{v:.12g}' for v in p) for p in uv[:24]]
 out+=['f '+' '.join(f) for f in faces]
 return '\n'.join(out)+'\n'

def main():
 (A/'models/block/school_inverted_slopes.mtl').write_text('newmtl wall\nKd 1 1 1\nd 1\nmap_Kd #wall\n')
 blocks='    // BEGIN SCHOOL INVERTED SLOPES\n';items=blocks
 for ident,col,row,low,rise in pieces():
  (A/f'models/block/{ident}.obj').write_text(inverted_obj(.75-low,rise))
  write(A/f'models/block/{ident}.json',{'loader':'neoforge:obj','model':f'lsmmod:models/block/{ident}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'wall':'lsmmod:block/light_school_wall','particle':'lsmmod:block/light_school_wall'}})
  write(A/f'blockstates/{ident}.json',{'variants':{f'facing={face}':{'model':'lsmmod:block/'+ident,'y':turn} for face,turn in (('north',0),('east',90),('south',180),('west',270))}})
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
  blocks+=f'    public static final DeferredBlock<SchoolInvertedSlopeBlock> {ident.upper()} = BLOCKS.registerBlock(\n            "{ident}", props -> new SchoolInvertedSlopeBlock({low:.15g},{rise:.15g},props),\n            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());\n'
  items+=f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});\n'
 for filename,section in (('ModBlocks.java',blocks),('ModItems.java',items)):
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text();begin='    // BEGIN SCHOOL INVERTED SLOPES';end='    // END SCHOOL INVERTED SLOPES';section+=end+'\n'
  if begin in s:a=s.index(begin);b=s.index(end)+len(end)+1;s=s[:a]+section+s[b:]
  else:anchor=s.index('    public static final DeferredBlock<' if filename=='ModBlocks.java' else '    public static final DeferredItem<');s=s[:anchor]+section+'\n'+s[anchor:]
  p.write_text(s)
 for locale in ('es_es','en_us'):
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text())
  for ident,*_ in pieces():
   family='interrumpida' if '_split_' in ident else 'continua';number=ident.rsplit('_',1)[1]
   v['block.lsmmod.'+ident]=f'Pendiente invertida sin baranda: {family} {number}' if locale=='es_es' else f'Inverted unrailed slope: {"split" if "_split_" in ident else "flight"} {number}'
  write(p,v)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+x[0] for x in pieces()]));write(p,v)
 write(ROOT/'tools/school_inverted_slope_layout.json',{'start_y':0,'transform':'inclined profile Ynew = 0.75 - Yoriginal; flat top Y=1','flat_top_y':1,'pieces':[{'id':ident,'column':col,'row':row,'lower_start':low,'rise':rise} for ident,col,row,low,rise in pieces()]})
if __name__=='__main__':main()
