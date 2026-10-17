"""Full tiled cube with a 4px parquet strip on top, using existing textures without recoloring."""
from resource_modules import module_read_text, module_write_text
from resource_json import resource_json
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
ID='tiled_parquet_floor'
GENERATED=[]

def write(path, value):
    path.parent.mkdir(parents=True,exist_ok=True)
    module_write_text(path, resource_json(path, value, ensure_ascii=False)+'\n',encoding='utf-8')
    GENERATED.append(str(path.relative_to(ROOT)))


def main():
    faces={face:{'texture':'#tile','uv':[0,0,16,16],'cullface':face} for face in ('down','north','south','west','east')}
    model={'parent':'minecraft:block/block', 'textures':{'tile':'lsmmod:block/classroom_floor','wood':'lsmmod:block/school_parquet','particle':'#tile'},
           'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':faces},
                       {'from':[0,0,0],'to':[12,16,16],'faces':{'up':{'texture':'#tile','uv':[0,0,12,16],'cullface':'up'}}},
                       {'from':[12,0,0],'to':[16,16,16],'faces':{'up':{'texture':'#wood','uv':[12,0,16,16],'cullface':'up'}}}]}
    write(A/f'models/block/{ID}.json',model)
    write(A/f'blockstates/{ID}.json',{'variants':{f'facing={direction}':{'model':f'lsmmod:block/{ID}','y':rotation}
          for direction,rotation in (('east',0),('south',90),('west',180),('north',270))}})
    write(A/f'models/item/{ID}.json',{'parent':f'lsmmod:block/{ID}'})
    write(A/f'items/{ID}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{ID}'}})
    write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json',{'type':'minecraft:block','pools':[{'rolls':1,
          'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ID}]}]})
    block='''    public static final DeferredBlock<FloorTransitionBlock> TILED_PARQUET_FLOOR = BLOCKS.registerBlock(
            "tiled_parquet_floor", FloorTransitionBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());'''
    item='    public static final DeferredItem<BlockItem> TILED_PARQUET_FLOOR = ITEMS.registerSimpleBlockItem(ModBlocks.TILED_PARQUET_FLOOR);'
    for file,body in [('ModBlocks.java',block),('ModItems.java',item)]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/file;s=module_read_text(p)
        start='    // BEGIN TILED PARQUET FLOOR';end='    // END TILED PARQUET FLOOR'
        section=start+'\n'+body+'\n'+end
        if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
        else:marker='    // BEGIN CLASSROOM SURFACES';s=s.replace(marker,section+'\n\n'+marker,1)
        module_write_text(p, s);GENERATED.append(str(p.relative_to(ROOT)))
    for locale,name in [('es_es','Suelo de baldosas con borde de parquet'),('en_us','Tiled Floor with Parquet Border')]:
        p=A/f'lang/{locale}.json';d=json.loads(module_read_text(p));d['block.lsmmod.'+ID]=name;write(p,d)
    p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(module_read_text(p))
    d['values']=list(dict.fromkeys(d['values']+['lsmmod:'+ID]));write(p,d)


if __name__=='__main__':main()
