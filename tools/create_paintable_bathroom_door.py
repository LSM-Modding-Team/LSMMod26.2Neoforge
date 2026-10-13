"""Reuse the bathroom leaf; edit its frame in 8px groups while preserving legacy fine storage."""
import copy
import json
from create_facility_expansion_assets import ROOT,A,J,D,write,basic,mesh,scaled_item
from create_paintable_school_block import SIDES,PAINTS
from render_texture_review import load

ID='bathroom_door_wide_paintable'
ENTRANCE_ID='school_entrance_door_paintable'
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


def entrance_leaf(opened=False,right=False):
    from create_double_school_entrance import wood,model
    parts=[copy.deepcopy(e) for e in wood(opened) if e['to'][0]<=(4 if opened else 24)]
    if right:
        for element in parts:
            lo,hi=element['from'],element['to'];element['from']=[32-hi[0],lo[1],lo[2]];element['to']=[32-lo[0],hi[1],hi[2]]
            element['faces']={dict(west='east',east='west').get(face,face):surface for face,surface in element['faces'].items()}
            for surface in element['faces'].values():
                u0,v0,u1,v1=surface['uv']
                surface['uv']=[u0,v1,u1,v0] if surface.get('rotation',0)%180 else [u1,v0,u0,v1]
    result=model(parts);result['textures']['particle']='lsmmod:block/double_school_entrance_0_0';return result

def generate_entrance():
    from create_double_school_entrance import crop
    multipart=[];shared={}
    for opened in (False,True):
        for right in (False,True):
            whole=entrance_leaf(opened,right)
            for row in range(3):
                for column in range(2):
                    for depth in range(2):
                        elements=[];offset=(column*16,row*16,depth*16)
                        for source in whole['elements']:
                            element=crop(source,[*offset,*(v+16 for v in offset)])
                            if element:
                                for key in ('from','to'):element[key]=[v-offset[axis] for axis,v in enumerate(element[key])]
                                elements.append(element)
                        if not elements:continue
                        value=dict(whole,elements=elements);key=json.dumps(value,sort_keys=True)
                        if key not in shared:
                            name=f'{ENTRANCE_ID}_leaf_{len(shared)}';shared[key]=name;write(A/f'models/block/{name}.json',value)
                        for turn,facing in enumerate(('north','east','south','west')):
                            multipart.append({'when':dict(facing=facing,open=str(opened).lower(),hinge='right' if right else 'left',
                                row=str(row),column=str(column),depth=str(depth)),
                                'apply':{'model':'lsmmod:block/'+shared[key],'y':turn*90}})
    for turn,facing in enumerate(('north','east','south','west')):
        multipart.append({'when':{'facing':facing,'depth':'0'},'apply':{'model':f'lsmmod:block/{ID}_frame','y':turn*90}})
    write(A/f'blockstates/{ENTRANCE_ID}.json',{'multipart':multipart})
    leaf=entrance_leaf()
    for e in leaf['elements']:
        for key in ('from','to'):
            x,y,z=e[key];e[key]=[8+(x-16)/3,y/3,8+(z-8)/3]
    write(A/f'models/item/{ENTRANCE_ID}_leaf.json',leaf)
    frame=scaled_item(FRAME,{'surface':PAINTS[0],'particle':PAINTS[0]},32,48)
    write(A/f'models/item/{ENTRANCE_ID}_frame.json',frame)
    (A/f'models/item/{ENTRANCE_ID}_glass.json').unlink(missing_ok=True)
    write(A/f'models/item/{ENTRANCE_ID}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},
        'models':[[f'lsmmod:item/{ENTRANCE_ID}_{part}'] for part in ('leaf','frame')]})
    basic(ENTRANCE_ID,controller={'column':'0','row':'0','depth':'0'})
    for filename,entry in (
        ('ModBlocks.java','    public static final DeferredBlock<SchoolPaintableEntranceDoorBlock> SCHOOL_ENTRANCE_DOOR_PAINTABLE = BLOCKS.registerBlock("'+ENTRANCE_ID+'", SchoolPaintableEntranceDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java','    public static final DeferredItem<BlockItem> SCHOOL_ENTRANCE_DOOR_PAINTABLE = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE);'),
    ):
        path=J/'registry'/filename;text=path.read_text()
        if ' SCHOOL_ENTRANCE_DOOR_PAINTABLE =' not in text:path.write_text(text.replace('    // BEGIN FACILITY EXPANSIONS',entry+'\n\n    // BEGIN FACILITY EXPANSIONS',1))
    path=J/'registry/ModBlockEntities.java';text=path.read_text()
    text=text.replace('ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE.get()));','ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE.get(),ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE.get()));')
    path.write_text(text)
    for locale,label in (('es_es','Puerta del colegio con marco pintable'),('en_us','School Entrance Door with Paintable Frame')):
        path=A/f'lang/{locale}.json';data=json.loads(path.read_text());data['block.lsmmod.'+ENTRANCE_ID]=label;write(path,data)
    for tool in ('axe','pickaxe'):
        path=D/f'minecraft/tags/block/mineable/{tool}.json';data=json.loads(path.read_text())
        if 'lsmmod:'+ENTRANCE_ID not in data['values']:data['values'].append('lsmmod:'+ENTRANCE_ID)
        write(path,data)

if __name__=='__main__':
    import sys
    if '--entrance-only' in sys.argv:generate_entrance()
    else:main()
