"""Muted cream/gray playground concrete with subtle vanilla pixel grain and reusable paints."""
import json,random
from PIL import Image
from create_paintable_school_block import ROOT,A,write

def main():
    names={'white_playground_floor':(231,228,218),'gray_playground_floor':(119,118,114)}
    blocks=[];items=[]
    for name,base in names.items():
        rng=random.Random(26101155);image=Image.new('RGB',(16,16))
        for y in range(16):
            for x in range(16):
                delta=rng.choice((-3,-2,-1,0,0,0,1,2,3));image.putpixel((x,y),tuple(c+delta for c in base))
        image.save(A/f'textures/block/{name}.png')
        write(A/f'blockstates/{name}.json',{'variants':{'':{'model':'lsmmod:block/'+name}}})
        write(A/f'models/block/{name}.json',{'parent':'minecraft:block/cube_all','textures':{'all':'lsmmod:block/'+name}})
        write(A/f'models/item/{name}.json',{'parent':'lsmmod:block/'+name})
        write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+name}})
        write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+name}]}]})
        blocks.append(f'    public static final DeferredBlock<Block> {name.upper()} = BLOCKS.registerBlock("{name}", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());')
        items.append(f'    public static final DeferredItem<BlockItem> {name.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{name.upper()});')
        for locale,label in [('es_es','Suelo '+('claro' if name.startswith('white') else 'gris')+' del patio'),('en_us',('White' if name.startswith('white') else 'Gray')+' Playground Floor')]:
            p=A/f'lang/{locale}.json';d=json.loads(p.read_text());d['block.lsmmod.'+name]=label;write(p,d)
    for file,body in [('ModBlocks.java','\n'.join(blocks)),('ModItems.java','\n'.join(items))]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/file;s=p.read_text();start='    // BEGIN PLAYGROUND FLOORS';end='    // END PLAYGROUND FLOORS';section=start+'\n'+body+'\n'+end
        if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
        else:s=s.replace('    // BEGIN CLASSROOM SURFACES',section+'\n\n    // BEGIN CLASSROOM SURFACES',1)
        p.write_text(s)
    p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';d=json.loads(p.read_text());d['values']=list(dict.fromkeys(d['values']+['lsmmod:'+n for n in names]));write(p,d)
    from create_paintable_school_block import main as paints
    from create_custom_paintable_blocks import main as custom
    paints();custom()
if __name__=='__main__':main()
