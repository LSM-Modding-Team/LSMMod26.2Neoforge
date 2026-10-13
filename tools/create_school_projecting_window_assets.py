"""Independent 24px projecting panes: two-pixel thickness, five poses, nine lattice phases."""
import copy
import json
from PIL import Image
from create_bathroom_statue_assets import solid_model, crop_face_uv
from create_school_refrigerator_assets import A, J, D, ROOT, write
from create_facility_expansion_assets import basic
from create_paintable_school_block import PAINTS, SIDES

ID = 'school_projecting_window'
PAINTABLE_ID = 'school_paintable_projecting_window'
TEXTURES = {'frame': f'lsmmod:block/{ID}_frame', 'glass': f'lsmmod:block/{ID}_glass', 'particle': '#frame'}
AXES = (((0, 16, 0),), ((0, 8, -16), (8, 16, 8)), ((0, 16, -8),))
PANES = {'solid': [([0, 0, 14, 1, 24, 16], 'frame'), ([23, 0, 14, 24, 24, 16], 'frame'),
                   ([1, 0, 14, 23, 1, 16], 'frame'), ([1, 23, 14, 23, 24, 16], 'frame')],
         'translucent': [([1, 1, 14, 23, 23, 16], 'glass')]}


def parts(phase):
    return [(x0, x1, y0, y1, ox, oy) for y0, y1, oy in AXES[phase//3] for x0, x1, ox in AXES[phase%3]]


def rotation(part, pose):
    *_, ox, oy = part
    return {'origin': [ox+(24 if pose == 4 else 0), oy+(24 if pose == 1 else 0), 16],
            'axis': 'x' if pose <= 2 else 'y', 'angle': -45 if pose in (1, 3) else 45, 'rescale': False}


def paintable_models():
    variants={}
    for left_wall in (True,False):
        side='left' if left_wall else 'right';low,high=(0,8) if left_wall else (8,16);wall_groups=[]
        for face in SIDES:
            series=[]
            for color,texture in enumerate(PAINTS):
                name=f'{PAINTABLE_ID}/wall_{side}_{face}_{color}'
                box=[low,0,0,high,16,16]
                uv=[low,0,high,16] if face in ('north','south','up','down') else [0,0,16,16]
                value={'parent':'minecraft:block/block','loader':'fusion:model','type':'base','render_type':'minecraft:solid',
                       'textures':{'surface':texture,'particle':texture},
                       'elements':[{'from':box[:3],'to':box[3:],'faces':{face:{'texture':'#surface','uv':uv}}}]}
                write(A/f'models/block/{name}.json',value)
                entry={'model':'lsmmod:block/'+name}
                if color:entry['condition']={'type':'lsmmod:painted_face','face':face,'paint':color,'local':True}
                series.append(entry)
            wall_groups.append(series[1:]+series[:1])
        roots=[]
        for phase in range(9):
            groups=[]
            for index,part in enumerate(parts(phase)):
                x0,x1,y0,y1,ox,oy=part
                cropped=(max(x0,8 if left_wall else 0),min(x1,16 if left_wall else 8),y0,y1,ox,oy)
                if cropped[0]>=cropped[1]:continue
                for layer,model in pane_models().items():
                    choices=[]
                    for pose in range(5):
                        value=fragment(model,cropped,pose)
                        if not value['elements']:continue
                        name=f'{PAINTABLE_ID}/phase_{phase}_{side}_part_{index}_{layer}_{pose}'
                        write(A/f'models/block/{name}.json',value)
                        choices.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window','phase':phase,'part':index,'pose':pose}})
                    if choices:groups.append(choices)
            name=f'{PAINTABLE_ID}/phase_{phase}_{side}'
            write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),'models':wall_groups+groups})
            roots.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window','phase':phase}})
        name=f'{PAINTABLE_ID}/{side}'
        write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),'models':[roots]})
        for turn,facing in enumerate(('north','east','south','west')):
            variants[f'facing={facing},wall_left={str(left_wall).lower()}']={'model':'lsmmod:block/'+name,'y':turn*90}
    write(A/f'blockstates/{PAINTABLE_ID}.json',{'variants':variants})
    # Item preview is a closed section of the shared grid, with the unpainted fixed wall.
    item_models=[f'lsmmod:block/{PAINTABLE_ID}/wall_left_{face}_0' for face in SIDES]
    for layer,model in pane_models().items():
        name=f'{PAINTABLE_ID}_{layer}'
        write(A/f'models/item/{name}.json',fragment(model,(8,16,0,16,8,0),0))
        item_models.append('lsmmod:item/'+name)
    write(A/f'models/item/{PAINTABLE_ID}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),'models':[[name] for name in item_models]})
    basic(PAINTABLE_ID)


def pane_models():
    result = {}
    for layer, components in PANES.items():
        model = solid_model(components)
        model.update(textures=TEXTURES, render_type='minecraft:'+layer)
        result[layer] = model
    return result


def fragment(model, part, pose):
    x0, x1, y0, y1, ox, oy = part
    result = copy.deepcopy(model); result['elements'] = []
    axes = {'west': (0, False), 'east': (0, True), 'down': (1, False),
            'up': (1, True), 'north': (2, False), 'south': (2, True)}
    for original in model['elements']:
        e = copy.deepcopy(original)
        for key in ('from', 'to'):
            e[key][0] += ox; e[key][1] += oy
        a, b = e['from'], e['to']
        lo = [max(a[0], x0), max(a[1], y0), a[2]]
        hi = [min(b[0], x1), min(b[1], y1), b[2]]
        if any(lo[i] >= hi[i] for i in range(3)): continue
        surfaces = {}
        for face, surface in e['faces'].items():
            axis, positive = axes[face]
            if abs((hi if positive else lo)[axis]-(b if positive else a)[axis])>1e-8: continue
            cropped = copy.deepcopy(surface)
            cropped['uv'] = crop_face_uv(surface['uv'], face, a, b, lo, hi)
            surfaces[face] = cropped
        if not surfaces: continue
        e.update({'from': lo, 'to': hi, 'faces': surfaces})
        if pose: e['rotation'] = rotation(part, pose)
        result['elements'].append(e)
    result.update(loader='fusion:model', type='base')
    return result



def layout_rules():
    rules=json.loads((A/'projecting_window_layouts.json').read_text())
    seen=set()
    for rule in rules:
        w,h=rule['width']*16,rule['height']*16;c,r=rule['columns'],rule['rows']
        assert w==int(w) and h==int(h) and w%8==0 and h%16==0
        assert isinstance(c,int) and isinstance(r,int) and c>0 and r>0 and w/c>=16 and h/r>=16 and w<=1024 and h<=1024
        assert (w,h) not in seen;seen.add((w,h))
    return rules


def layout_key(rule,start,x,y):
    return f"w{int(rule['width']*16)}h{int(rule['height']*16)}_{start}_{x}_{y}"


def layout_parts(rule,start,x,y):
    w,h=rule['width']*16,rule['height']*16;pw,ph=w/rule['columns'],h/rule['rows']
    result=[]
    for row in range(rule['rows']):
        for column in range(rule['columns']):
            ox,oy=start+column*pw-x*16,row*ph-y*16
            lo,hi=max(0,start-x*16,ox),min(16,start+w-x*16,ox+pw)
            bottom,top=max(0,oy),min(16,oy+ph)
            if lo<hi-1e-8 and bottom<top-1e-8:result.append((lo,hi,bottom,top,ox,oy,pw,ph,column,row))
    return result


def sized_pane_models(width,height):
    components={'solid':[([0,0,14,1,height,16],'frame'),([width-1,0,14,width,height,16],'frame'),
                         ([1,0,14,width-1,1,16],'frame'),([1,height-1,14,width-1,height,16],'frame')],
                'translucent':[([1,1,14,width-1,height-1,16],'glass')]}
    result={}
    for layer,boxes in components.items():
        model=solid_model(boxes);model.update(textures=TEXTURES,render_type='minecraft:'+layer)
        result[layer]=model
    return result


def layout_fragment(model,part,pose):
    value=fragment(model,part[:6],0)
    if pose:
        for e in value['elements']:e['rotation']={'origin':[part[4],part[5]+part[7],16],'axis':'x','angle':-45,'rescale':False}
    return value


def layout_models():
    # Rebuild this generated family so deleted table entries leave no obsolete adaptive models.
    import shutil,math
    for ident in (ID,PAINTABLE_ID):
        directory=A/f'models/block/{ident}/layouts'
        if directory.exists():shutil.rmtree(directory)
    rules=layout_rules();choices=[];half_choices={'left':[],'right':[]}
    for side in half_choices:
        groups=json.loads((A/f'models/block/{PAINTABLE_ID}/phase_0_{side}.json').read_text())['models'][:6]
        write(A/f'models/block/{PAINTABLE_ID}/wall_{side}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),'models':groups})
    for rule in rules:
        width,height=rule['width']*16,rule['height']*16
        base=sized_pane_models(width/rule['columns'],height/rule['rows'])
        for start in (0,8):
            cells=math.ceil((start+width)/16)
            for y in range(int(height)//16):
                for x in range(cells):
                    key=layout_key(rule,start,x,y);groups=[]
                    for index,part in enumerate(layout_parts(rule,start,x,y)):
                        for layer,model in base.items():
                            series=[]
                            for pose in range(2):
                                value=layout_fragment(model,part,pose)
                                if not value['elements']:continue
                                name=f'{ID}/layouts/{key}_part_{index}_{layer}_{pose}'
                                write(A/f'models/block/{name}.json',value)
                                series.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window_layout','key':key,'part':index,'pose':pose}})
                            if series:groups.append(series)
                    name=f'{ID}/layouts/{key}'
                    write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':groups})
                    condition={'type':'lsmmod:projecting_window_layout','key':key}
                    choices.append({'model':'lsmmod:block/'+name,'condition':condition})
                    for side in half_choices:
                        if side=='left' and not (start==8 and x==0):continue
                        if side=='right' and not ((start+width)%16==8 and x==cells-1):continue
                        half_name=f'{PAINTABLE_ID}/layouts/{key}_{side}'
                        write(A/f'models/block/{half_name}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),
                              'models':[[f'lsmmod:block/{PAINTABLE_ID}/wall_{side}'],['lsmmod:block/'+name]]})
                        half_choices[side].append({'model':'lsmmod:block/'+half_name,'condition':condition})
    # The first predicate scans the supplied snapshot once, including whenever a neighboring cell changes.
    for ident,side,entries in ((ID,None,choices),*( (PAINTABLE_ID,side,entries) for side,entries in half_choices.items())):
        source=A/f'models/block/{ident}{("/"+side) if side else ""}.json'
        legacy=f'{ident}/legacy{("_"+side) if side else ""}'
        write(A/f'models/block/{legacy}.json',json.loads(source.read_text()))
        first={'model':'lsmmod:block/'+legacy,'condition':{'type':'lsmmod:projecting_window_layout','key':'fallback','refresh':True}}
        write(source,{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]) if side else TEXTURES,'models':[[first]+entries]})

def main():
    Image.new('RGB', (16, 16), (17, 18, 19)).save(A/f'textures/block/{ID}_frame.png')
    # Vanilla 26.2 tinted-glass extraction, retained as the reproducible color reference.
    # Source: github.com/InventivetalentDev/minecraft-assets/tree/26.2/assets/minecraft/textures/block
    glass = Image.open(ROOT/'newresources/projecting_window_tinted_reference.png').convert('RGBA')
    glass.putdata([tuple(max(0, channel-4) for channel in glass.getpixel((x, y))[:3])+(glass.getpixel((x, y))[3],)
                   for y in range(glass.height) for x in range(glass.width)])
    glass.save(A/f'textures/block/{ID}_glass.png')
    base = pane_models(); roots = []
    for phase in range(9):
        groups = []
        for index, part in enumerate(parts(phase)):
            for layer, model in base.items():
                choices = []
                for pose in range(5):
                    value = fragment(model, part, pose)
                    # Some interior cell fragments contain glass only. Omit absent frame layers.
                    if not value['elements']: continue
                    name = f'{ID}/phase_{phase}_part_{index}_{layer}_{pose}'
                    write(A/f'models/block/{name}.json', value)
                    choices.append({'model': 'lsmmod:block/'+name,
                                    'condition': {'type': 'lsmmod:projecting_window', 'phase': phase,
                                                  'part': index, 'pose': pose}})
                if choices: groups.append(choices)
        name = f'{ID}/phase_{phase}'
        write(A/f'models/block/{name}.json', {'loader': 'fusion:model', 'type': 'composite', 'textures': TEXTURES, 'models': groups})
        roots.append({'model': 'lsmmod:block/'+name,
                      'condition': {'type': 'lsmmod:projecting_window', 'phase': phase}})
    write(A/f'models/block/{ID}.json', {'loader': 'fusion:model', 'type': 'composite', 'textures': TEXTURES, 'models': [roots]})
    write(A/f'blockstates/{ID}.json', {'variants': {f'facing={facing}': {
          'model': 'lsmmod:block/'+ID, 'y': turn*90} for turn, facing in enumerate(('north', 'east', 'south', 'west'))}})
    for layer, value in base.items():
        item = copy.deepcopy(value); item.update(loader='fusion:model', type='base')
        for e in item['elements']:
            for key in ('from', 'to'):
                x, y, z = e[key]; e[key] = [x*2/3, y*2/3, 8+(z-8)*2/3]
        write(A/f'models/item/{ID}_{layer}.json', item)
    write(A/f'models/item/{ID}.json', {'loader': 'fusion:model', 'type': 'composite', 'textures': TEXTURES,
          'models': [[f'lsmmod:item/{ID}_solid'], [f'lsmmod:item/{ID}_translucent']]})
    basic(ID)
    for filename, entry in (
        ('ModBlocks.java', '    public static final DeferredBlock<SchoolProjectingWindowBlock> SCHOOL_PROJECTING_WINDOW = BLOCKS.registerBlock("school_projecting_window", SchoolProjectingWindowBlock::new, props -> props.strength(0.5F).sound(SoundType.GLASS).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> SCHOOL_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PROJECTING_WINDOW);'),
        ('ModBlocks.java', '    public static final DeferredBlock<SchoolPaintableProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW = BLOCKS.registerBlock("school_paintable_projecting_window", SchoolPaintableProjectingWindowBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW);'),
    ):
        path = J/'registry'/filename; text = path.read_text()
        symbol=entry.split(' = ',1)[0].rsplit(' ',1)[-1]
        if f' {symbol} =' not in text:
            path.write_text(text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1))
    for locale, name in (('es_es', 'Ventana proyectante del colegio'), ('en_us', 'School Projecting Window')):
        path = A/f'lang/{locale}.json'; data = json.loads(path.read_text()); data['block.lsmmod.'+ID] = name
        data['block.lsmmod.'+PAINTABLE_ID]='Mitad pintable con ventana proyectante' if locale=='es_es' else 'Paintable Half with Projecting Window'
        write(path, data)
    path = D/'minecraft/tags/block/mineable/pickaxe.json'; data = json.loads(path.read_text())
    if 'lsmmod:'+ID not in data['values']: data['values'].append('lsmmod:'+ID)
    if 'lsmmod:'+PAINTABLE_ID not in data['values']: data['values'].append('lsmmod:'+PAINTABLE_ID)
    write(path, data)
    paintable_models()
    layout_models()


if __name__ == '__main__': main()
