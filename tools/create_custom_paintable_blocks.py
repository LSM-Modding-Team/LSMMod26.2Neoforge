"""Bounded Fusion face models for an editable eight-cell cube, tools and foundation."""
from resource_modules import module_read_text, module_write_text, output_path
from resource_json import resource_json
from resource_modules import output_path, write_resource
import json
import random
from pathlib import Path
from PIL import Image, ImageDraw
from create_paintable_school_block import ROOT, A, SIDES, PAINTS

STEPS={'down':(0,-1,0),'up':(0,1,0),'north':(0,0,-1),'south':(0,0,1),'west':(-1,0,0),'east':(1,0,0)}
GENERATED=[]
def write(p,value):
    destination=p
    write_resource(p,value)
    GENERATED.append(str(destination.relative_to(ROOT)))

def uv(face,x,y,z):
    # Each square is a quarter of the existing texture, without repeating or stretching it.
    return {'down':[x,8-z,x+8,16-z], 'up':[x,z,x+8,z+8],
            'north':[8-x,8-y,16-x,16-y], 'south':[x,8-y,x+8,16-y],
            'west':[z,8-y,z+8,16-y], 'east':[8-z,8-y,16-z,16-y]}[face]

def main():
    series=[]
    for cube in range(8):
        x,y,z=(cube&1)*8,((cube>>1)&1)*8,((cube>>2)&1)*8
        for face in SIDES:
            dx,dy,dz=STEPS[face]
            outside=not(0<=x//8+dx<2 and 0<=y//8+dy<2 and 0<=z//8+dz<2)
            entries=[]
            for paint,texture in enumerate(PAINTS):
                name=f'custom_paintable_faces/{cube}_{face}_{paint}'
                surface={'texture':'#surface','uv':uv(face,x,y,z)}
                if outside: surface['cullface']=face
                write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid',
                      'textures':{'surface':texture,'particle':'#surface'},'elements':[{'from':[x,y,z],
                      'to':[x+8,y+8,z+8],'faces':{face:surface}}]})
                entries.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:painted_cube_face',
                              'cube':cube,'face':face,'paint':paint}})
            series.append(entries)
    # Fusion requires an unconditional default for context-free particles. An empty model
    # supplies the gray particle without drawing any removed/hidden cube geometry.
    write(A/'models/block/custom_paintable_empty.json',{'parent':'minecraft:block/block',
          'textures':{'particle':PAINTS[0]},'elements':[]})
    series.append([{'model':'lsmmod:block/custom_paintable_empty'}])
    ident='school_paintable_custom_block'
    write(A/f'models/block/{ident}.json',{'loader':'fusion:model','type':'composite',
            'textures':{'particle':PAINTS[0]},'models':series})
    write(A/f'models/item/{ident}.json',{'parent':'minecraft:block/cube_all','textures':{'all':PAINTS[0]}})
    ident='school_foundation'
    write(A/f'models/block/{ident}.json',{'parent':'minecraft:block/cube_all','textures':{'all':'lsmmod:block/school_foundation'}})
    write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
    for ident in ('school_foundation','school_paintable_custom_block'):
        write(A/f'blockstates/{ident}.json',{'variants':{'':{'model':'lsmmod:block/'+ident}}})
        write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
        write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block',
              'pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],
                        'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
    for ident in ('school_sandpaper','school_hammer'):
        write(A/f'models/item/{ident}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'lsmmod:item/'+ident}})
        write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
    rng=random.Random(261011)
    image=Image.new('RGB',(16,16))
    for y in range(16):
        for x in range(16):
            delta=rng.choice((-5,-3,-1,0,0,1,3,5)); image.putpixel((x,y),tuple(c+delta for c in (76,80,83)))
    save_texture(image,A/'textures/block/school_foundation.png')
    image=Image.new('RGBA',(16,16));d=ImageDraw.Draw(image)
    d.polygon([(3,2),(12,2),(14,12),(5,14),(1,5)],fill=(157,107,60),outline=(92,62,39))
    for x,y in [(4,4),(8,3),(10,6),(4,8),(8,9),(11,11),(6,12),(3,6)]: d.point((x,y),fill=(191,144,84))
    save_texture(image,A/'textures/item/school_sandpaper.png')
    image=Image.new('RGBA',(16,16));d=ImageDraw.Draw(image)
    d.line([(5,13),(10,5)],fill=(80,51,29),width=4);d.line([(5,13),(10,5)],fill=(144,99,54),width=2)
    d.polygon([(6,2),(10,1),(14,4),(13,7),(9,6),(5,4)],fill=(69,74,80),outline=(40,43,48))
    d.line([(7,2),(10,2),(13,4)],fill=(160,165,169),width=1)
    save_texture(image,A/'textures/item/school_hammer.png')
    blocks='''    public static final DeferredBlock<Block> SCHOOL_FOUNDATION = BLOCKS.registerBlock(
            "school_foundation", Block::new,
            props -> props.strength(3.0F, 8.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<SchoolPaintableCustomBlock> SCHOOL_PAINTABLE_CUSTOM_BLOCK = BLOCKS.registerBlock(
            "school_paintable_custom_block", SchoolPaintableCustomBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().dynamicShape().noOcclusion());'''
    items='''    public static final DeferredItem<BlockItem> SCHOOL_FOUNDATION = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_FOUNDATION);
    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_CUSTOM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_CUSTOM_BLOCK);
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolSandpaperItem> SCHOOL_SANDPAPER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_SANDPAPER;
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolHammerItem> SCHOOL_HAMMER = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.SCHOOL_HAMMER;'''
    for filename,body in [('ModBlocks.java',blocks),('ModItems.java',items)]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=module_read_text(p)
        start='    // BEGIN CUSTOM PAINTABLE BLOCKS';end='    // END CUSTOM PAINTABLE BLOCKS';section=start+'\n'+body+'\n'+end+'\n\n'
        if start in s:
            a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section.rstrip()+s[b:]
        else:
            a=s.index('    // BEGIN PAINTABLE SCHOOL BLOCK');s=s[:a]+section+s[a:]
        module_write_text(p, s)
    for locale,labels in [('en_us',('School Foundation','Custom Paintable School Block','School Sandpaper','School Hammer')),
                          ('es_es',('Cimientos del colegio','Bloque pintable personalizado del colegio','Lija del colegio','Martillo del colegio'))]:
        p=A/f'lang/{locale}.json';data=json.loads(module_read_text(p))
        for key,label in zip(('block.lsmmod.school_foundation','block.lsmmod.school_paintable_custom_block',
                              'item.lsmmod.school_sandpaper','item.lsmmod.school_hammer'),labels):data[key]=label
        write(p,data)
    p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';data=json.loads(module_read_text(p))
    data['values']=list(dict.fromkeys(data['values']+['lsmmod:school_foundation','lsmmod:school_paintable_custom_block']));write(p,data)

def save_texture(image,p):
    p.parent.mkdir(parents=True,exist_ok=True);p=output_path(p);p.parent.mkdir(parents=True,exist_ok=True);image.save(output_path(p));GENERATED.append(str(p.relative_to(ROOT)))

FINE_ID='school_paintable_mini_cube_block'

def generate_fine():
    import copy
    from create_facility_expansion_assets import J,D,basic
    model=copy.deepcopy(json.loads(module_read_text(A/'models/block/bathroom_door_wide_paintable_frame.json')))
    for group in model['models']:
        for entry in group:
            if 'condition' in entry:entry['condition']['type']='lsmmod:painted_mini_cube'
    write(A/f'models/block/{FINE_ID}.json',model)
    write(A/f'blockstates/{FINE_ID}.json',{'variants':{'':{'model':'lsmmod:block/'+FINE_ID}}})
    write(A/f'models/item/{FINE_ID}.json',{'parent':'minecraft:block/block','textures':{'surface':PAINTS[0],'particle':'#surface'},
        'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':{face:{'texture':'#surface','uv':[0,0,16,16]} for face in SIDES}}]})
    basic(FINE_ID)
    for filename,entry in (
        ('ModBlocks.java','    public static final DeferredBlock<SchoolMiniCubePaintableBlock> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = BLOCKS.registerBlock("'+FINE_ID+'", SchoolMiniCubePaintableBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops().dynamicShape().noOcclusion());'),
        ('ModItems.java','    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK);'),
    ):
        path=J/'registry'/filename;text=module_read_text(path)
        if ' SCHOOL_PAINTABLE_MINI_CUBE_BLOCK =' not in text:module_write_text(path, text.replace('    // BEGIN CUSTOM PAINTABLE BLOCKS',entry+'\n\n    // BEGIN CUSTOM PAINTABLE BLOCKS',1))
    path=J/'registry/ModBlockEntities.java';text=module_read_text(path)
    if ' SCHOOL_MINI_CUBE_PAINT =' not in text:
        entry='''    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintEntity>> SCHOOL_MINI_CUBE_PAINT = BLOCK_ENTITIES.register(
            "school_mini_cube_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK.get()));

'''
        module_write_text(path, text.replace('    // Only the LEFT half',entry+'    // Only the LEFT half',1))
    for locale,label in (('es_es','Bloque pintable de minicubos de 4 px'),('en_us','Paintable 4px Mini Cube Block')):
        path=A/f'lang/{locale}.json';data=json.loads(module_read_text(path));data['block.lsmmod.'+FINE_ID]=label;write(path,data)
    path=D/'minecraft/tags/block/mineable/pickaxe.json';data=json.loads(module_read_text(path))
    if 'lsmmod:'+FINE_ID not in data['values']:data['values'].append('lsmmod:'+FINE_ID)
    write(path,data)

if __name__=='__main__':
    import sys
    if '--fine-only' in sys.argv:generate_fine()
    else:main()
