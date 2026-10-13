"""Reuse the bathroom leaf; edit its frame in 8px groups while preserving legacy fine storage."""
import copy
import json
from create_facility_expansion_assets import ROOT,A,J,D,write,basic,mesh,scaled_item
from create_paintable_school_block import SIDES,PAINTS
from render_texture_review import load

ID='bathroom_door_wide_paintable'
FRAME=[([24,0,0,32,40,16],'surface'),([0,40,0,32,48,16],'surface')]


def uv(face,x,y,z):
    return {'down':[x,12-z,x+4,16-z],'up':[x,z,x+4,z+4],
            'north':[12-x,12-y,16-x,16-y],'south':[x,12-y,x+4,16-y],
            'west':[z,12-y,z+4,16-y],'east':[12-z,12-y,16-z,16-y]}[face]


def native_leaf(model):
    result=copy.deepcopy(model)
    result['elements']=[e for e in result.get('elements',[]) if any(f['texture'] in ('#bottom','#top') for f in e['faces'].values())]
    for element in result['elements']:
        element['faces']={face:material for face,material in element['faces'].items() if material['texture'] in ('#bottom','#top')}
    return result


def main():
    # Each 4px face has a single geometry parent; all colors reuse the existing material palette.
    # These are one shared library for every frame cell, hinge, and orientation, not per-state copies.
    series=[]
    for cube in range(64):
        x,y,z=(cube&3)*4,((cube>>2)&3)*4,((cube>>4)&3)*4
        for face in SIDES:
            parent=f'door_frame_4px/{cube}_{face}'
            write(A/f'models/block/{parent}.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid',
                'textures':{'surface':PAINTS[0],'particle':'#surface'},'elements':[{'from':[x,y,z],'to':[x+4,y+4,z+4],
                'faces':{face:{'texture':'#surface','uv':uv(face,x,y,z)}}}]})
            entries=[]
            for paint,texture in enumerate(PAINTS):
                name=parent+f'_{paint}'
                if paint:
                    write(A/f'models/block/{name}.json',{'parent':'lsmmod:block/'+parent,'textures':{'surface':texture}})
                else:name=parent
                entries.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:painted_door_frame',
                    'cube':cube,'face':face,'paint':paint}})
            series.append(entries)
    series.append([{'model':'lsmmod:block/custom_paintable_empty'}])
    write(A/f'models/block/{ID}_frame.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},'models':series})

    multipart=[];shared={}
    for entry in json.loads((A/'blockstates/bathroom_door_wide.json').read_text())['multipart']:
        if 'framed_door_transom' in entry['apply']['model']:continue
        model=native_leaf(load(entry['apply']['model'])[0])
        if not model['elements']:continue
        key=json.dumps(model,sort_keys=True)
        if key not in shared:
            name=f'{ID}_leaf_{len(shared)}';shared[key]=name;write(A/f'models/block/{name}.json',model)
        new=copy.deepcopy(entry);new['apply']['model']='lsmmod:block/'+shared[key];multipart.append(new)
    for turn,facing in enumerate(('north','east','south','west')):
        multipart.append({'when':{'facing':facing,'depth':'0'},'apply':{'model':f'lsmmod:block/{ID}_frame','y':turn*90}})
    write(A/f'blockstates/{ID}.json',{'multipart':multipart})
    item=native_leaf(load('lsmmod:item/bathroom_door_wide_item_solid')[0])
    frame=scaled_item(FRAME,{'surface':PAINTS[0],'particle':PAINTS[0]},32,48)
    item['textures']['surface']=PAINTS[0];item['elements'].extend(frame['elements'])
    write(A/f'models/item/{ID}.json',item)
    basic(ID,controller={'column':'0','row':'0','depth':'0'})

    additions=(
        ('ModBlocks.java','    public static final DeferredBlock<SchoolPaintableBathroomDoorBlock> BATHROOM_DOOR_WIDE_PAINTABLE = BLOCKS.registerBlock("'+ID+'", SchoolPaintableBathroomDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java','    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_PAINTABLE = ITEMS.registerSimpleBlockItem(ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE);'),
    )
    for filename,entry in additions:
        path=J/'registry'/filename;text=path.read_text()
        if ' BATHROOM_DOOR_WIDE_PAINTABLE =' not in text:path.write_text(text.replace('    // BEGIN FACILITY EXPANSIONS',entry+'\n\n    // BEGIN FACILITY EXPANSIONS',1))
    path=J/'registry/ModBlockEntities.java';text=path.read_text()
    if ' SCHOOL_DOOR_FRAME_PAINT =' not in text:
        entry='''    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolDoorFramePaintEntity>> SCHOOL_DOOR_FRAME_PAINT = BLOCK_ENTITIES.register(
            "school_door_frame_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolDoorFramePaintEntity::new,
                    ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE.get()));

'''
        path.write_text(text.replace('    // Only the LEFT half',entry+'    // Only the LEFT half',1))
    for locale,label in (('es_es','Puerta ancha de baño con marco pintable'),('en_us','Wide Bathroom Door with Paintable Frame')):
        path=A/f'lang/{locale}.json';data=json.loads(path.read_text());data['block.lsmmod.'+ID]=label;write(path,data)
    for tool in ('axe','pickaxe'):
        path=D/f'minecraft/tags/block/mineable/{tool}.json';data=json.loads(path.read_text())
        if 'lsmmod:'+ID not in data['values']:data['values'].append('lsmmod:'+ID)
        write(path,data)


if __name__=='__main__':main()
