"""Original double entrance with an 8px paintable slab, and a four-row bathroom door."""
from resource_modules import module_glob, module_read_text, module_unlink, module_write_text
import copy,itertools,json,shutil
from create_school_glass_assets import ROOT,A,D,write
from create_facility_expansion_assets import J,basic
from create_double_school_entrance import wood,model,crop
from create_paintable_school_block import PAINTS,SIDES
from create_bathroom_statue_assets import crop_face_uv
from render_texture_review import load,add
DOUBLE='double_school_entrance_paintable_slab'
BATHROOM='bathroom_door_wide_2x4'


def native(elements,layer='solid',textures=None):
    return {'parent':'minecraft:block/block','render_type':'minecraft:'+layer,'textures':textures or {'particle':PAINTS[0]},'elements':elements}


def clipped(elements,column,row,depth):
    result=[];offset=(column*16,row*16,depth*16)
    for element in elements:
        cut=crop(element,[*offset,*[v+16 for v in offset]])
        if cut and cut['faces']:
            for key in ('from','to'):cut[key]=[v-offset[i] for i,v in enumerate(cut[key])]
            result.append(cut)
    return result


def item(ident,elements,width,height):
    for obsolete in module_glob(A/'models/item', ident+'_*.json'):module_unlink(obsolete)
    groups=[];scale=max(width,height)/16
    for layer in sorted({e.get('render_type','minecraft:solid') for e in elements}):
        parts=[]
        for original in elements:
            if original.get('render_type','minecraft:solid')!=layer:continue
            element=copy.deepcopy(original);element.pop('render_type',None)
            for key in ('from','to'):element[key]=[8+(element[key][0]-width/2)/scale,element[key][1]/scale,8+(element[key][2]-8)/scale]
            parts.append(element)
        name=f'{ident}_{layer.split(":")[1]}'
        write(A/f'models/item/{name}.json',native(parts,layer.split(':')[1]));groups.append(['lsmmod:item/'+name])
    write(A/f'models/item/{ident}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},'models':groups})


def double():
    multipart=[]
    for opened in (False,True):
        source=wood(opened)
        for column,row,depth in itertools.product(range(3),range(3),range(2)):
            parts=clipped(source,column,row,depth)
            if not parts:continue
            name=f'{DOUBLE}/leaf_{column}_{row}_{depth}_{int(opened)}'
            write(A/f'models/block/{name}.json',model(parts))
            for turn,facing in enumerate(('north','east','south','west')):
                multipart.append({'when':dict(facing=facing,open=str(opened).lower(),column=str(column),row=str(row),depth=str(depth)),
                    'apply':{'model':'lsmmod:block/'+name,'y':turn*90}})
    for column in range(3):
        groups=[]
        for face in SIDES:
            if face=='west' and column!=0 or face=='east' and column!=2:continue
            axes=(2,1) if face in ('west','east') else (0,2) if face in ('up','down') else (0,1)
            for q in range(4):
                lo=[0,8,0];hi=[16,16,16]
                for axis,index in zip(axes,(q%2,q//2)):
                    lo[axis]=max(lo[axis],index*8);hi[axis]=min(hi[axis],(index+1)*8)
                if any(a>=b for a,b in zip(lo,hi)):continue
                parent=f'{DOUBLE}/slab_{column}_{face}_{q}'
                uv=crop_face_uv([0,0,16,16],face,[0,8,0],[16,16,16],lo,hi)
                write(A/f'models/block/{parent}.json',native([{'from':lo,'to':hi,'faces':{face:{'texture':'#surface','uv':uv}}}],textures={'surface':PAINTS[0],'particle':'#surface'}))
                entries=[]
                for color,texture in enumerate(PAINTS):
                    name=parent
                    if color:
                        name=parent+'_'+str(color);write(A/f'models/block/{name}.json',{'parent':'lsmmod:block/'+parent,'textures':{'surface':texture}})
                    entries.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:painted_face','face':face,'paint':color,'quadrant':q,'local':True}})
                groups.append(entries)
        name=f'{DOUBLE}/slab_{column}'
        write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},'models':groups})
        for turn,facing in enumerate(('north','east','south','west')):
            multipart.append({'when':dict(facing=facing,column=str(column),row='2',depth='0'),
                'apply':{'model':'lsmmod:block/'+name,'y':turn*90}})
    write(A/f'blockstates/{DOUBLE}.json',{'multipart':multipart})
    elements=[];add(elements,[model(wood(False))])
    slab={'from':[0,40,0],'to':[48,48,16],'faces':{face:{'texture':PAINTS[0],'uv':[0,0,16,16]} for face in SIDES}}
    elements.append(dict(slab,render_type='minecraft:solid'));item(DOUBLE,elements,48,48)
    basic(DOUBLE,controller={'column':'1','row':'0','depth':'0'})


def bathroom_parts(opened=False,right=False):
    entries=json.loads(module_read_text(A/'blockstates/bathroom_door_wide.json'))['multipart'];elements=[]
    for entry in entries:
        when=entry['when']
        if 'framed_door_transom' in entry['apply']['model']:continue
        if when.get('facing')!='north' or when.get('open')!=str(opened).lower() or when.get('hinge')!=('right' if right else 'left'):continue
        offset=(int(when['column'])*16,int(when['row'])*16,int(when['depth'])*16)
        parts=[];add(parts,load(entry['apply']['model']),offset)
        for element in parts:
            element['faces']={face:surface for face,surface in element['faces'].items() if surface['texture'] in ('lsmmod:block/bathroom_door_bottom','lsmmod:block/bathroom_door_top')}
            if element['faces']:elements.append(element)
    for element in elements:
        for key in ('from','to'):
            element[key][1]*=64/40
            if opened:element[key][2]*=32/24
            else:element[key][0]=(element[key][0]-(8 if right else 0))*32/24
    return elements


def bathroom():
    multipart=[]

    for opened,right in itertools.product((False,True),repeat=2):
        source=bathroom_parts(opened,right)
        for column,row,depth in itertools.product(range(2),range(4),range(2)):
            groups=[]
            for layer in ('minecraft:solid','minecraft:cutout','minecraft:translucent'):
                parts=clipped([e for e in source if e['render_type']==layer],column,row,depth)
                if not parts:continue
                name=f'{BATHROOM}/{int(opened)}_{int(right)}_{column}_{row}_{depth}_{layer.split(":")[1]}'
                write(A/f'models/block/{name}.json',native(parts,layer.split(':')[1],{'particle':'lsmmod:block/bathroom_door_bottom'}));groups.append(['lsmmod:block/'+name])
            if not groups:continue
            name=f'{BATHROOM}/{int(opened)}_{int(right)}_{column}_{row}_{depth}'
            write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','models':groups})
            for turn,facing in enumerate(('north','east','south','west')):
                multipart.append({'when':dict(facing=facing,open=str(opened).lower(),hinge='right' if right else 'left',column=str(column),row=str(row),depth=str(depth)),
                    'apply':{'model':'lsmmod:block/'+name,'y':turn*90}})
    write(A/f'blockstates/{BATHROOM}.json',{'multipart':multipart})
    elements=bathroom_parts()
    item(BATHROOM,elements,32,64);basic(BATHROOM,controller={'column':'0','row':'0','depth':'0'})


def main():
    for ident in (DOUBLE,BATHROOM):shutil.rmtree(A/f'models/block/{ident}',ignore_errors=True)
    double();bathroom()
    for ident,cls,label_es,label_en in (
        (DOUBLE,'SchoolPaintableDoubleEntranceBlock','Puerta doble del colegio con losa pintable','Double School Entrance with Paintable Slab'),
        (BATHROOM,'SchoolBathroomDoor2x4Block','Puerta ancha de baño de 2×4','Wide Bathroom Door 2×4')):
        symbol=ident.upper()
        for filename,entry in (
            ('ModBlocks.java',f'    public static final DeferredBlock<{cls}> {symbol} = BLOCKS.registerBlock("{ident}", {cls}::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));'),
            ('ModItems.java',f'    public static final DeferredItem<BlockItem> {symbol} = ITEMS.registerSimpleBlockItem(ModBlocks.{symbol});')):
            p=J/'registry'/filename;s=module_read_text(p)
            if ' '+symbol+' =' not in s:module_write_text(p, s.replace('    // BEGIN FACILITY EXPANSIONS',entry+'\n\n    // BEGIN FACILITY EXPANSIONS',1))
        for locale,label in (('es_es',label_es),('en_us',label_en)):
            p=A/f'lang/{locale}.json';data=json.loads(module_read_text(p));data['block.lsmmod.'+ident]=label;write(p,data)
        for tool in (('axe','pickaxe') if ident==DOUBLE else ('axe',)):
            p=D/f'minecraft/tags/block/mineable/{tool}.json';data=json.loads(module_read_text(p))
            if 'lsmmod:'+ident not in data['values']:data['values'].append('lsmmod:'+ident)
            write(p,data)
    p=J/'registry/ModBlockEntities.java';s=module_read_text(p)
    if ' SCHOOL_ENTRANCE_SLAB_PAINT =' not in s:
        entry='    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolEntranceSlabPaintEntity>> SCHOOL_ENTRANCE_SLAB_PAINT = BLOCK_ENTITIES.register("school_entrance_slab_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolEntranceSlabPaintEntity::new, ModBlocks.DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB.get()));\n\n'
        module_write_text(p, s.replace('    // Only the LEFT half',entry+'    // Only the LEFT half',1))

if __name__=='__main__':main()
