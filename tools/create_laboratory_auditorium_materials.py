"""Two tiles per axis, narrow auditorium parquet, and a wooden stage with dark wool top."""
import json,math,random
from PIL import Image
from create_school_glass_assets import ROOT,A,D,write
from create_facility_expansion_assets import J,basic
MATERIALS=('school_laboratory_tile','laboratory_table_tile','auditorium_parquet','auditorium_stage')
LABELS=(('Baldosas del laboratorio','Laboratory Tiles'),('Baldosas de mesa del laboratorio','Laboratory Table Tiles'),('Parquet del auditorio','Auditorium Parquet'),('Estrado del auditorio','Auditorium Stage'))


def tiles(table=False):
    rng=random.Random(1406 if table else 1405);image=Image.new('RGB',(32,32))
    base=(220,220,211) if table else (183,173,151);joint=(112,111,101) if table else (86,78,64)
    variations=[0,2,-2,1] if table else [0,3,-3,1]
    for y in range(32):
        for x in range(32):
            if x%16==0 or y%16==0:color=joint
            else:
                grain=rng.choice((-1,0,0,0,1)) if table else rng.choice((-6,-3,-1,0,0,1,3,5))
                cloud=0 if table else int(2*math.sin(x*.53+y*.37))
                delta=variations[x//16+2*(y//16)]+grain+cloud
                color=tuple(c+delta for c in base)
            image.putpixel((x,y),color)
    return image


def parquet():
    rng=random.Random(1407);image=Image.new('RGB',(32,32))
    boards=((121,61,29),(171,94,42),(144,71,31),(187,110,51),(114,53,24),(157,79,32),(178,101,45),(134,63,27))
    for y in range(32):
        for x in range(32):
            board=x//4;delta=rng.choice((-5,-3,-1,0,0,1,3,4))
            delta+=int(3*math.sin(y*.49+board*2))
            if x%4==0:delta-=17
            elif x%4==1:delta+=5
            if y==(board*7+11)%32:delta-=19
            image.putpixel((x,y),tuple(max(0,c+delta) for c in boards[board]))
    return image


def stage():
    rng=random.Random(1408);wood=Image.new('RGB',(32,32));top=Image.new('RGB',(32,32))
    tones=((112,54,30),(128,63,34),(117,55,31),(133,68,38))
    for y in range(32):
        for x in range(32):
            delta=rng.choice((-3,-1,0,0,1,3))+int(3*math.sin(x*.42+y*.09))
            if y%8==0:delta-=18
            elif y%8==1:delta+=5
            wood.putpixel((x,y),tuple(max(0,c+delta) for c in tones[y//8]))
            fiber=((x//2+y//2)%2)*3+((x+2*y)%5==0)*3
            shade=32+fiber+rng.choice((-3,-1,0,1,3))
            top.putpixel((x,y),(shade,shade,shade+1))
    return wood,top


def main():
    textures=[tiles(),tiles(True),parquet()];wood,top=stage();textures.append(wood)
    for ident,image in zip(MATERIALS,textures):image.save(A/f'textures/block/{ident}.png')
    top.save(A/'textures/block/auditorium_stage_top.png')
    for ident,labels in zip(MATERIALS,LABELS):
        surface='lsmmod:block/'+ident
        model={'parent':'minecraft:block/block','render_type':'minecraft:solid','textures':{'surface':surface,'particle':surface},
            'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':{face:{'texture':'#surface','uv':[0,0,16,16],'cullface':face} for face in ('down','up','north','south','west','east')}}]}
        if ident=='auditorium_stage':
            model['textures']['top']='lsmmod:block/auditorium_stage_top';model['elements'][0]['faces']['up']['texture']='#top'
        write(A/f'models/block/{ident}.json',model);write(A/f'blockstates/{ident}.json',{'variants':{'':{'model':'lsmmod:block/'+ident}}})
        write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident});basic(ident)
        symbol=ident.upper();wooden=ident in ('auditorium_parquet','auditorium_stage')
        entry=f'    public static final DeferredBlock<Block> {symbol} = BLOCKS.registerBlock("{ident}", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.{"WOOD" if wooden else "STONE"}));'
        item=f'    public static final DeferredItem<BlockItem> {symbol} = ITEMS.registerSimpleBlockItem(ModBlocks.{symbol});'
        for filename,line in (('ModBlocks.java',entry),('ModItems.java',item)):
            p=J/'registry'/filename;s=p.read_text()
            if ' '+symbol+' =' not in s:p.write_text(s.replace('    // BEGIN FACILITY EXPANSIONS',line+'\n\n    // BEGIN FACILITY EXPANSIONS',1))
        for locale,label in zip(('es_es','en_us'),labels):
            p=A/f'lang/{locale}.json';data=json.loads(p.read_text());data['block.lsmmod.'+ident]=label;write(p,data)
        p=D/f'minecraft/tags/block/mineable/{"axe" if wooden else "pickaxe"}.json';data=json.loads(p.read_text())
        if 'lsmmod:'+ident not in data['values']:data['values'].append('lsmmod:'+ident)
        write(p,data)

    from create_paintable_school_block import main as paints
    from extend_school_paint_palette import main as extend
    paints();extend()

if __name__=='__main__':main()
