"""Independent 24px projecting panes: two-pixel thickness, five poses, nine lattice phases."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_exists, module_path, module_read_text, module_unlink, module_write_text, output_path
import copy
import json
import math
import hashlib
from PIL import Image
from facilities.create_bathroom_statue_assets import solid_model, crop_face_uv
from resource_json import resource_json
from painting_and_editable_blocks.create_paintable_school_block import PAINTS, SIDES

# Keep resource preparation independent of manual furniture and retired door generators.
ROOT = _Path(__file__).resolve().parents[2]
A = ROOT / 'src/main/resources/assets/lsmmod'
J = ROOT / 'src/main/java/net/nicomar2009/lsmmod'
D = ROOT / 'src/main/resources/data'


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    module_write_text(path, value if isinstance(value, str) else resource_json(path, value, ensure_ascii=False) + '\n')


def basic(ident):
    write(A / f'items/{ident}.json', {
        'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/' + ident}})
    write(D / f'lsmmod/loot_table/blocks/{ident}.json', {
        'type': 'minecraft:block', 'pools': [{
            'rolls': 1, 'conditions': [{'condition': 'minecraft:survives_explosion'}],
            'entries': [{'type': 'minecraft:item', 'name': 'lsmmod:' + ident}]}]})


ID = 'school_projecting_window'
PAINTABLE_ID = 'school_paintable_projecting_window'
LAYOUT_IDS = (ID, PAINTABLE_ID, 'school_paintable_projecting_window_slab',
              'school_paintable_projecting_window_stairs')
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
            # Keep the original whole-face models as shared material parents and for the item.
            axes=(2,1) if face in ('west','east') else (0,2) if face in ('up','down') else (0,1)
            squares=[]
            for quadrant in range(4):
                lo=[low,0,0];hi=[high,16,16]
                for axis,index in zip(axes,(quadrant%2,quadrant//2)):
                    lo[axis]=max(lo[axis],index*8);hi[axis]=min(hi[axis],(index+1)*8)
                if any(a>=b for a,b in zip(lo,hi)):continue
                choices=[]
                for color,texture in enumerate(PAINTS):
                    name=f'{PAINTABLE_ID}/wall_{side}_{face}_q{quadrant}_{color}'
                    write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'base','render_type':'minecraft:solid',
                        'parent':f'lsmmod:block/{PAINTABLE_ID}/wall_{side}_{face}_{color}',
                        'elements':[{'from':lo,'to':hi,'faces':{face:{'texture':'#surface',
                        'uv':crop_face_uv(uv,face,[low,0,0],[high,16,16],lo,hi)}}}]})
                    choices.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:painted_face','face':face,
                        'paint':color,'local':True,'quadrant':quadrant}})
                squares.append(choices)
            name=f'{PAINTABLE_ID}/wall_{side}_{face}_squares'
            write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},'models':squares})
            wall_groups.append([{'model':'lsmmod:block/'+name}])
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
    rules=json.loads(module_read_text(A/'projecting_window_layouts.json'))
    seen=set()
    for rule in rules:
        w,h=rule['width']*16,rule['height']*16;c,r=rule['columns'],rule['rows']
        assert w==int(w) and h==int(h) and w%8==0 and h%8==0
        assert isinstance(c,int) and isinstance(r,int) and c>0 and r>0 and w/c>=16 and h/r>=16 and w<=1024 and h<=1024
        assert (w,h) not in seen;seen.add((w,h))
    return rules


def layout_key(rule,start,x,y,start_y=0):
    return f"w{int(rule['width']*16)}h{int(rule['height']*16)}_{start}_{x}_{y}"+(f'_v{start_y}' if start_y else '')


def layout_parts(rule,start,x,y,start_y=0):
    w,h=rule['width']*16,rule['height']*16;pw,ph=w/rule['columns'],h/rule['rows']
    result=[]
    # Include one neighboring panel at either boundary to absorb float rounding.
    # The original intersection checks below still decide which fragments exist.
    columns=range(max(0,math.floor((x*16-start)/pw)-1),
                  min(rule['columns'],math.ceil(((x+1)*16-start)/pw)+1))
    rows=range(max(0,math.floor((y*16-start_y)/ph)-1),
               min(rule['rows'],math.ceil(((y+1)*16-start_y)/ph)+1))
    for row in rows:
        for column in columns:
            ox,oy=start+column*pw-x*16,start_y+row*ph-y*16
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


def layout_models(*, read_text=module_read_text, write_model=write, clear_existing=True, share_geometry=False):
    """Generate table-driven variants in the selected resource tree, idempotently."""
    import shutil,math
    write=write_model
    shared_geometry={}
    def geometry_name(name,value):
        if not share_geometry:
            write(A/f'models/block/{name}.json',value)
            return name
        signature=json.dumps(value,sort_keys=True,separators=(',',':'),ensure_ascii=False)
        if signature not in shared_geometry:
            shared=f'{ID}/layouts/shared/'+hashlib.sha256(signature.encode('utf-8')).hexdigest()[:32]
            write(A/f'models/block/{shared}.json',value)
            shared_geometry[signature]=shared
        return shared_geometry[signature]
    slab_id='school_paintable_projecting_window_slab'
    stair_id='school_paintable_projecting_window_stairs'
    for ident in LAYOUT_IDS:
        directory=A/f'models/block/{ident}/layouts'
        if clear_existing and module_exists(directory):shutil.rmtree(directory)
    rules=layout_rules();choices=[];half_choices={'left':[],'right':[]};slab_choices={'bottom':[],'top':[]};stair_choices={kind+'_'+side:[] for kind in ('bottom','top') for side in ('left','right')}
    for side in half_choices:
        groups=json.loads(read_text(A/f'models/block/{PAINTABLE_ID}/phase_0_{side}.json'))['models'][:6]
        write(A/f'models/block/{PAINTABLE_ID}/wall_{side}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),'models':groups})
    for rule in rules:
        width,height=rule['width']*16,rule['height']*16
        base=sized_pane_models(width/rule['columns'],height/rule['rows'])
        for start in (0,8):
            cells=math.ceil((start+width)/16)
            for sy in (0,8):
                for y in range(math.ceil((sy+height)/16)):
                    for x in range(cells):
                        key=layout_key(rule,start,x,y,sy)
                        raw=layout_parts(rule,start,x,y,sy)
                        for profile in ('full','bottom','top',*stair_choices):
                            # Slabs can only bound the glass rectangle, not fill an
                            # interior cell. Avoid generating unreachable profiles.
                            kind=profile.split('_')[0]
                            if kind=='bottom' and not (sy==8 and y==0):continue
                            if kind=='top' and not ((sy+height)%16==8 and y==math.ceil((sy+height)/16)-1):continue
                            if profile in stair_choices:
                                side=profile.split('_')[1]
                                if side=='left' and not (start==8 and x==0):continue
                                if side=='right' and not ((start+width)%16==8 and x==cells-1):continue
                            if profile=='full':selected=raw
                            else:
                                low,high=(8,16) if kind=='bottom' else (0,8)
                                selected=[(p[0],p[1],max(low,p[2]),min(high,p[3]),*p[4:]) for p in raw
                                          if max(low,p[2])<min(high,p[3])]
                            if profile in stair_choices:
                                lx,hx=(8,16) if side=='left' else (0,8)
                                selected=[(max(lx,p[0]),min(hx,p[1]),*p[2:]) for p in selected if max(lx,p[0])<min(hx,p[1])]
                            if not selected:continue
                            prefix=ID if profile=='full' else stair_id if profile in stair_choices else slab_id
                            suffix='' if profile=='full' else '_'+profile
                            groups=[]
                            for index,part in enumerate(selected):
                                for layer,model in base.items():
                                    series=[]
                                    for pose in range(2):
                                        value=layout_fragment(model,part,pose)
                                        if not value['elements']:continue
                                        name=f'{prefix}/layouts/{key}{suffix}_part_{index}_{layer}_{pose}'
                                        name=geometry_name(name,value)
                                        series.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window_layout','key':key,'part':index,'pose':pose}})
                                    if series:groups.append(series)
                            name=f'{prefix}/layouts/{key}{suffix}'
                            write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':groups})
                            condition={'type':'lsmmod:projecting_window_layout','key':key}
                            if profile!='full':
                                (stair_choices if profile in stair_choices else slab_choices)[profile].append({'model':'lsmmod:block/'+name,'condition':condition});continue
                            choices.append({'model':'lsmmod:block/'+name,'condition':condition})
                            for side in half_choices:
                                if side=='left' and not (start==8 and x==0):continue
                                if side=='right' and not ((start+width)%16==8 and x==cells-1):continue
                                half_name=f'{PAINTABLE_ID}/layouts/{key}_{side}'
                                write(A/f'models/block/{half_name}.json',{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]),
                                      'models':[[f'lsmmod:block/{PAINTABLE_ID}/wall_{side}'],['lsmmod:block/'+name]]})
                                half_choices[side].append({'model':'lsmmod:block/'+half_name,'condition':condition})
    roots=[(ID,None,choices),*((PAINTABLE_ID,side,entries) for side,entries in half_choices.items()),
           *((slab_id,side,entries) for side,entries in slab_choices.items()),
           *((stair_id,side,entries) for side,entries in stair_choices.items())]
    for ident,side,entries in roots:
        source=A/f'models/block/{ident}{("/"+side) if side else ""}.json'
        legacy=f'{ident}/legacy{("_"+side) if side else ""}'
        # A build may re-run only this step on already generated resources.
        # Never replace the legacy model with a wrapper referencing itself.
        value=json.loads(read_text(source))
        first=value.get('models',[[]])[0]
        wrapped=bool(first and isinstance(first[0],dict) and first[0].get('condition',{}).get('key')=='fallback')
        if not wrapped:write(A/f'models/block/{legacy}.json',value)
        first={'model':'lsmmod:block/'+legacy,'condition':{'type':'lsmmod:projecting_window_layout','key':'fallback','refresh':True}}
        write(source,{'loader':'fusion:model','type':'composite','textures':dict(TEXTURES,particle=PAINTS[0]) if side else TEXTURES,'models':[[first]+entries]})
    write(A/'projecting_window_baked_layouts.json',rules)


def prepare_layouts(assets, *, documents=None, sizes=None, share_geometry=False):
    """Refresh table-derived models in an isolated build tree, never source files."""
    global A
    previous=A
    try:
        A=assets
        if documents is None:
            layout_models(share_geometry=share_geometry)
        else:
            def read_text(path):
                return json.dumps(documents[path]) if path.is_relative_to(assets/'models') else module_read_text(path)
            def write_model(path,value):
                if path.is_relative_to(assets/'models'):
                    documents[path]=copy.deepcopy(value)
                    if sizes is not None:
                        sizes[path]=len((json.dumps(value,ensure_ascii=False,separators=(',',':'))+'\n').encode('utf-8'))
                else:
                    write(path,value)
            layout_models(read_text=read_text,write_model=write_model,clear_existing=False,share_geometry=share_geometry)
    finally:
        A=previous


def main():
    Image.new('RGB', (16, 16), (17, 18, 19)).save(output_path(A/f'textures/block/{ID}_frame.png'))
    # Vanilla 26.2 tinted-glass extraction, retained as the reproducible color reference.
    # Source: github.com/InventivetalentDev/minecraft-assets/tree/26.2/assets/minecraft/textures/block
    glass = Image.open(module_path(ROOT/'tools/windows_and_enclosures/fixtures/projecting_window_tinted_reference.png')).convert('RGBA')
    glass.putdata([tuple(max(0, channel-4) for channel in glass.getpixel((x, y))[:3])+(glass.getpixel((x, y))[3],)
                   for y in range(glass.height) for x in range(glass.width)])
    glass.save(output_path(A/f'textures/block/{ID}_glass.png'))
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
        path = J/'registry'/filename; text = module_read_text(path)
        symbol=entry.split(' = ',1)[0].rsplit(' ',1)[-1]
        if f' {symbol} =' not in text:
            module_write_text(path, text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1))
    for locale, name in (('es_es', 'Ventana proyectante del colegio'), ('en_us', 'School Projecting Window')):
        path = A/f'lang/{locale}.json'; data = json.loads(module_read_text(path)); data['block.lsmmod.'+ID] = name
        data['block.lsmmod.'+PAINTABLE_ID]='Mitad pintable con ventana proyectante' if locale=='es_es' else 'Paintable Half with Projecting Window'
        write(path, data)
    path = D/'minecraft/tags/block/mineable/pickaxe.json'; data = json.loads(module_read_text(path))
    if 'lsmmod:'+ID not in data['values']: data['values'].append('lsmmod:'+ID)
    if 'lsmmod:'+PAINTABLE_ID not in data['values']: data['values'].append('lsmmod:'+PAINTABLE_ID)
    write(path, data)
    paintable_models()
    slab_models(adaptive=False)
    stair_models(adaptive=False)
    layout_models()


SLAB_ID='school_paintable_projecting_window_slab'


def slab_pane_models(opened=False):
    parts=[([0,8,14,1,16,16],'frame'),([15,8,14,16,16,16],'frame'),
           ([1,8,14,15,9,16],'frame'),([1,15,14,15,16,16],'frame'),([1,9,14,15,15,16],'glass')]
    layers={}
    for layer,material in (('solid','frame'),('translucent','glass')):
        source=solid_model([p for p in parts if p[1]=='frame'] if material=='frame' else parts)
        elements=[e for e in source['elements'] if next(iter(e['faces'].values()))['texture']=='#'+material]
        for element in elements:
            lo,hi=element['from'],element['to']
            for face,surface in element['faces'].items():
                surface['uv']={'north':[16-hi[0],16-hi[1],16-lo[0],16-lo[1]],'south':[lo[0],16-hi[1],hi[0],16-lo[1]],
                    'west':[lo[2],16-hi[1],hi[2],16-lo[1]],'east':[16-hi[2],16-hi[1],16-lo[2],16-lo[1]],
                    'up':[lo[0],lo[2],hi[0],hi[2]],'down':[16-hi[0],lo[2],16-lo[0],hi[2]]}[face]
            if opened:element['rotation']={'origin':[8,16,16],'axis':'x','angle':-45,'rescale':False}
        layers[layer]={'parent':'minecraft:block/block','render_type':'minecraft:'+layer,'textures':TEXTURES,'elements':elements}
    return layers


def slab_models(adaptive=True):
    multipart=[{'apply':{'model':'lsmmod:block/school_paintable_custom_block'}}]
    for kind in ('bottom','top'):
        phases=[];low,high=(8,16) if kind=='bottom' else (0,8)
        for phase in range(9):
            groups=[]
            for index,part in enumerate(parts(phase)):
                part=(part[0],part[1],max(low,part[2]),min(high,part[3]),*part[4:])
                if part[2]>=part[3]:continue
                for layer,boxes in PANES.items():
                    series=[]
                    for pose in range(5):
                        value=fragment(solid_model(boxes),part,pose)
                        if not value['elements']:continue
                        value.update(textures=TEXTURES,render_type='minecraft:'+layer)
                        name=f'{SLAB_ID}/phase_{phase}_{kind}_part_{index}_{layer}_{pose}'
                        write(A/f'models/block/{name}.json',value)
                        series.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window','phase':phase,'part':index,'pose':pose}})
                    if series:groups.append(series)
            name=f'{SLAB_ID}/phase_{phase}_{kind}'
            write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':groups})
            phases.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window','phase':phase}})
        write(A/f'models/block/{SLAB_ID}/{kind}.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':[phases]})
        for turn,facing in enumerate(('north','east','south','west')):
            multipart.append({'when':{'facing':facing,'type':kind},'apply':{'model':f'lsmmod:block/{SLAB_ID}/{kind}','y':turn*90}})
    write(A/f'blockstates/{SLAB_ID}.json',{'multipart':multipart})
    # Standalone inventory example keeps a complete small sash above its slab.
    groups=[]
    for layer,value in slab_pane_models(False).items():
        name=f'{SLAB_ID}_pane_0_{layer}'
        write(A/f'models/block/{name}.json',value);groups.append(['lsmmod:block/'+name])
    write(A/f'models/block/{SLAB_ID}_pane_0.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':groups})
    for suffix in ('','_solid','_translucent'):
        module_unlink(A/f'models/block/{SLAB_ID}_pane_1{suffix}.json', missing_ok=True)
    write(A/f'models/item/{SLAB_ID}_slab.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid',
        'textures':{'surface':PAINTS[0],'particle':'#surface'},'elements':[{'from':[0,0,0],'to':[16,8,16],
        'faces':{face:{'texture':'#surface','uv':[0,0,16,16]} for face in SIDES}}]})
    write(A/f'models/item/{SLAB_ID}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},
        'models':[['lsmmod:item/'+SLAB_ID+'_slab'],['lsmmod:block/'+SLAB_ID+'_pane_0']]})
    basic(SLAB_ID)
    path=D/f'lsmmod/loot_table/blocks/{SLAB_ID}.json';loot=json.loads(module_read_text(path))
    loot['pools'][0]['entries'][0]['functions']=[{'function':'minecraft:set_count','count':2,
        'conditions':[{'condition':'minecraft:block_state_property','block':'lsmmod:'+SLAB_ID,'properties':{'type':'double'}}]}]
    write(path,loot)
    for filename,entry in (
        ('ModBlocks.java',f'    public static final DeferredBlock<SchoolSlabProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = BLOCKS.registerBlock("{SLAB_ID}", SchoolSlabProjectingWindowBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape());'),
        ('ModItems.java','    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB);')):
        path=J/'registry'/filename;text=module_read_text(path)
        if ' SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB =' not in text:module_write_text(path, text.replace('    // BEGIN FACILITY EXPANSIONS',entry+'\n\n    // BEGIN FACILITY EXPANSIONS',1))
    path=J/'registry/ModBlockEntities.java';text=module_read_text(path)
    if ' SCHOOL_SLAB_PROJECTING_WINDOW_PAINT =' not in text:
        entry='    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowPaintEntity>> SCHOOL_SLAB_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register("school_slab_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB.get()));\n\n'
        module_write_text(path, text.replace('    // Only the LEFT half',entry+'    // Only the LEFT half',1))
    for locale,label in (('es_es','Losa pintable con ventana proyectante'),('en_us','Paintable Slab with Projecting Window')):
        path=A/f'lang/{locale}.json';data=json.loads(module_read_text(path));data['block.lsmmod.'+SLAB_ID]=label;write(path,data)
    path=D/'minecraft/tags/block/mineable/pickaxe.json';data=json.loads(module_read_text(path))
    if 'lsmmod:'+SLAB_ID not in data['values']:data['values'].append('lsmmod:'+SLAB_ID)
    write(path,data)
    for registry in ('block','item'):
        tag=D/f'minecraft/tags/{registry}/slabs.json'
        tag.parent.mkdir(parents=True,exist_ok=True)
        data=json.loads(module_read_text(tag)) if module_exists(tag) else {'values':[]}
        if 'lsmmod:'+SLAB_ID not in data['values']:data['values'].append('lsmmod:'+SLAB_ID)
        write(tag,data)
    if adaptive:layout_models()


STAIR_ID='school_paintable_projecting_window_stairs'


def stair_models(adaptive=True):
    multipart=[{'apply':{'model':'lsmmod:block/school_paintable_custom_block'}}]
    for kind in ('bottom','top'):
        for side in ('left','right'):
            profile=kind+'_'+side;phases=[]
            lx,hx=(8,16) if side=='left' else (0,8)
            ly,hy=(8,16) if kind=='bottom' else (0,8)
            for phase in range(9):
                groups=[]
                for index,p in enumerate(parts(phase)):
                    part=(max(lx,p[0]),min(hx,p[1]),max(ly,p[2]),min(hy,p[3]),*p[4:])
                    if part[0]>=part[1] or part[2]>=part[3]:continue
                    for layer,model in pane_models().items():
                        series=[]
                        for pose in range(5):
                            value=fragment(model,part,pose)
                            if not value['elements']:continue
                            name=f'{STAIR_ID}/phase_{phase}_{profile}_part_{index}_{layer}_{pose}'
                            write(A/f'models/block/{name}.json',value)
                            series.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window','phase':phase,'part':index,'pose':pose}})
                        if series:groups.append(series)
                name=f'{STAIR_ID}/phase_{phase}_{profile}'
                write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':groups})
                phases.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:projecting_window','phase':phase}})
            write(A/f'models/block/{STAIR_ID}/{profile}.json',{'loader':'fusion:model','type':'composite','textures':TEXTURES,'models':[phases]})
            for turn,facing in enumerate(('north','east','south','west')):
                multipart.append({'when':{'facing':facing,'half':kind,'wall_left':str(side=='left').lower()},
                                  'apply':{'model':f'lsmmod:block/{STAIR_ID}/{profile}','y':turn*90}})
    write(A/f'blockstates/{STAIR_ID}.json',{'multipart':multipart})
    # Fixed representative item: the same six solid cubes and an 8px framed sash.
    from facilities.create_bathroom_statue_assets import solid_model
    boxes=[([0,0,0,16,8,16],'surface'),([0,8,0,8,16,16],'surface')]
    item=solid_model(boxes);item.update(textures={'surface':PAINTS[0],'particle':PAINTS[0]},render_type='minecraft:solid')
    write(A/f'models/item/{STAIR_ID}_solid.json',item)
    source=solid_model([([8,8,14,9,16,16],'frame'),([15,8,14,16,16,16],'frame'),
                        ([9,8,14,15,9,16],'frame'),([9,15,14,15,16,16],'frame'),([9,9,14,15,15,16],'glass')])
    layers=[]
    for layer,material in [('solid','frame'),('translucent','glass')]:
        value=copy.deepcopy(source);value['elements']=[e for e in value['elements'] if next(iter(e['faces'].values()))['texture']=='#'+material]
        value.update(textures=TEXTURES,render_type='minecraft:'+layer)
        name=f'{STAIR_ID}_pane_{layer}';write(A/f'models/item/{name}.json',value);layers.append(['lsmmod:item/'+name])
    write(A/f'models/item/{STAIR_ID}.json',{'loader':'fusion:model','type':'composite','textures':{'particle':PAINTS[0]},
          'models':[['lsmmod:item/'+STAIR_ID+'_solid'],*layers]})
    basic(STAIR_ID)
    for locale,label in [('es_es','Escalera pintable con ventana proyectante'),('en_us','Paintable Stair with Projecting Window')]:
        path=A/f'lang/{locale}.json';data=json.loads(module_read_text(path));data['block.lsmmod.'+STAIR_ID]=label;write(path,data)
    path=D/'minecraft/tags/block/mineable/pickaxe.json'
    data=json.loads(module_read_text(path))
    if 'lsmmod:'+STAIR_ID not in data['values']:data['values'].append('lsmmod:'+STAIR_ID)
    write(path,data)
    if adaptive:layout_models()


if __name__ == '__main__':
    import sys
    if '--stairs-only' in sys.argv:stair_models()
    elif '--slab-only' in sys.argv:slab_models()
    else:main()
