"""Fifteen grouped two-material cubes: one native cube model and sixteen rotations each."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
PAIRS=(('light','dark'),('light','street'),('dark','street'))
SIDES=('north','east','south','west','up','down')
def write(path,data):
 path.parent.mkdir(parents=True,exist_ok=True)
 path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
def ids():return [(f'{a}_{b}_school_wall_{n}',a,b,n) for a,b in PAIRS for n in range(1,6)]
def main():
 blocks=[];items=[]
 for ident,a,b,n in ids():
  grouped={'north'}|({'east'} if min(n,6-n)>=2 else set())|({'up'} if n==3 else set())
  primary=set(SIDES)-grouped if n>3 else grouped
  textures={'first':f'lsmmod:block/{a}_school_wall','second':f'lsmmod:block/{b}_school_wall',
            'particle':'#first',**{side:'#first' if side in primary else '#second' for side in SIDES}}
  write(A/f'models/block/{ident}.json',{'parent':'minecraft:block/cube','render_type':'minecraft:solid','textures':textures})
  write(A/f'blockstates/{ident}.json',{'variants':{f'orientation={x*4+y}':
        {'model':f'lsmmod:block/{ident}','x':90*x,'y':90*y} for x in range(4) for y in range(4)}})
  write(A/f'models/item/{ident}.json',{'parent':f'lsmmod:block/{ident}'})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{ident}'}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[
      {'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],
       'entries':[{'type':'minecraft:item','name':f'lsmmod:{ident}'}]}]})
  blocks.append(f'    public static final DeferredBlock<MixedSchoolWallBlock> {ident.upper()} = BLOCKS.registerBlock(\n'
                f'            "{ident}", props -> new MixedSchoolWallBlock({n}, props),\n'
                '            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());')
  items.append(f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});')
 for filename,lines,anchor in [('ModBlocks.java',blocks,'    public static final DeferredBlock<'),('ModItems.java',items,'    // BEGIN SCHOOL RAILING SLOPES')]:
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text()
  start='    // BEGIN MIXED SCHOOL WALLS';end='    // END MIXED SCHOOL WALLS'
  section=start+'\n'+'\n'.join(lines)+'\n'+end+'\n\n'
  if start in s:
   a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section.rstrip()+s[b:]
  else:a=s.index(anchor);s=s[:a]+section+s[a:]
  p.write_text(s)
 names={'en_us':{'light':'Light','dark':'Dark','street':'Street'},'es_es':{'light':'Clara','dark':'Oscura','street':'Calle'}}
 for locale,colors in names.items():
  p=A/f'lang/{locale}.json';d=json.loads(p.read_text())
  for ident,a,b,n in ids():d['block.lsmmod.'+ident]=(f'{colors[a]}/{colors[b]} School Wall ({n}/{6-n} faces)' if locale=='en_us' else f'Pared {colors[a]}/{colors[b]} ({n}/{6-n} caras)')
  write(p,d)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(p.read_text());d['values']=list(dict.fromkeys(d['values']+[f'lsmmod:{ident}' for ident,*_ in ids()]));write(p,d)
if __name__=='__main__':main()
