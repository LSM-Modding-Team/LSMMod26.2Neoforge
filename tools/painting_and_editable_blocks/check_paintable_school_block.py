"""Static resource contract: exhaustive palettes, face isolation and legacy white alias.

Does not compile Java or substitute for a client/server interaction test.
"""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_is_file, module_path, module_registry_text, module_rglob
import itertools
import json
from pathlib import Path
from painting_and_editable_blocks.create_paintable_school_block import ROOT, SIDES, PAINTS, TOOLS
from resource_modules import source_assets
A = source_assets()

def read(path):
    return json.loads(module_registry_text(path, encoding='utf-8'))

def check_projecting_half():
    """Paint palettes must affect only the fixed half, across both profiles and all hinge poses."""
    from windows_and_enclosures.create_school_projecting_window_assets import PAINTABLE_ID, parts, rotation
    from render_texture_review import load
    native={}
    for side in ('left','right'):
        white_low,white_high=(0,8) if side=='left' else (8,16)
        previous=None
        for phase in range(9):
            root=read(A/f'models/block/{PAINTABLE_ID}/phase_{phase}_{side}.json')
            fixed=root['models'][:6]
            assert previous is None or fixed==previous, 'Opening the window changes painted wall models'
            previous=fixed
            for face,group in zip(SIDES,fixed):
                wrapper=read(A/f'models/{group[0]["model"].split(":")[1]}.json')
                assert len(group)==1 and 'condition' not in group[0]
                assert len(wrapper['models'])==(4 if face in ('east','west') else 2)
                area=0
                for squares in wrapper['models']:
                    assert len(squares)==len(PAINTS)
                    for paint,chosen in enumerate(squares):
                        condition=chosen['condition'];q=condition['quadrant']
                        assert condition=={'type':'lsmmod:painted_face','face':face,'paint':paint,'local':True,'quadrant':q}
                        leaf,=load(chosen['model']);box,=leaf['elements']
                        assert white_low<=box['from'][0]<box['to'][0]<=white_high and len(box['faces'])==1
                        assert 'rotation' not in box and face in box['faces'] and leaf['textures']['surface']==PAINTS[paint]
                        axes=(2,1) if face in ('east','west') else (0,2) if face in ('up','down') else (0,1)
                        assert all(box['to'][a]-box['from'][a]==8 for a in axes)
                        assert q==int(box['from'][axes[0]]//8)+2*int(box['from'][axes[1]]//8)
                        native[side,face,q,paint]=leaf
                    area+=64
                assert area==(256 if face in ('east','west') else 128)
            for group in root['models'][6:]:
                assert {e['condition']['pose'] for e in group}==set(range(5))
                for entry in group:
                    condition=entry['condition'];pose=condition['pose'];part=parts(phase)[condition['part']]
                    pane=read(A/f'models/{entry["model"].split(":")[1]}.json')
                    assert pane['elements']
                    for e in pane['elements']:
                        assert e['from'][2]==14 and e['to'][2]==16
                        assert (e['from'][0]>=8 if side=='left' else e['to'][0]<=8)
                        assert bool(e.get('rotation'))==bool(pose)
                        if pose:assert e['rotation']==rotation(part,pose), 'Half pane has a separate hinge'
    assert len(native)==2*16*len(PAINTS)
    # Geometry-based hit centers must map to the same saved world-space quadrants in every facing.
    clockwise=dict(north='east',east='south',south='west',west='north',up='up',down='down')
    def q_for(face,xyz):
        x,y,z=xyz;return z+2*y if face in ('east','west') else x+2*z if face in ('up','down') else x+2*y
    for (side,face,q,paint),model in native.items():
        if paint!=0:continue
        element,=model['elements'];center=[(a+b)/32 for a,b in zip(element['from'],element['to'])]
        bits=[0,0,0]
        axes=(2,1) if face in ('east','west') else (0,2) if face in ('up','down') else (0,1)
        bits[axes[0]]=q%2;bits[axes[1]]=q//2;target=face
        for turn in range(4):
            assert q_for(target,[int(v>=.5) for v in center])==q_for(target,bits),(side,face,q,turn)
            center=[1-center[2],center[1],center[0]];bits=[1-bits[2],bits[1],bits[0]];target=clockwise[target]
    # Fragment UVs should reproduce the previous whole-face texture, front/back and oblique.
    import numpy as np
    from render_texture_review import add,render
    for side in ('left','right'):
        split=[];whole=[]
        add(split,load(f'lsmmod:block/{PAINTABLE_ID}/wall_{side}',predicate=lambda c:c['paint']==12))
        for face in SIDES:add(whole,load(f'lsmmod:block/{PAINTABLE_ID}/wall_{side}_{face}_12'))
        for angle in (-90,-65,115):
            options=dict(center=[8,8,8],scale=12,size=(320,320),azimuth=angle,elevation=20)
            difference=np.abs(np.asarray(render(split,**options)).astype(int)-np.asarray(render(whole,**options)).astype(int))
            assert difference.max()<=1,('Changed UV or square seam',side,angle,difference.max())
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    entity=module_registry_text(java/'modules/painting_and_editable_blocks/block/SchoolProjectingWindowPaintEntity.java')
    assert 'extends SchoolPaintableCustomBlockEntity' in entity and 'loadWholeFaces(input)' in entity
    assert 'getIntOr("paint_bits",0)==0' in entity and 'removeCube(Direction face,Vec3 hit) {return false;}' in entity
    variants=read(A/f'blockstates/{PAINTABLE_ID}.json')['variants'];assert len(variants)==8
    turns=dict(north=0,east=90,south=180,west=270)
    for key,apply in variants.items():
        fields=dict(value.split('=') for value in key.split(','));assert apply['y']==turns[fields['facing']]
        side='left' if fields['wall_left']=='true' else 'right'
        assert apply['model']==f'lsmmod:block/{PAINTABLE_ID}/{side}'
    from render_texture_review import load
    for ident in ('school_projecting_window',PAINTABLE_ID):
        models=load('lsmmod:item/'+ident)
        assert any(model.get('render_type')=='minecraft:translucent' and model.get('elements') for model in models), 'Fusion drops the glass from the inventory model'
        assert any(model.get('render_type')=='minecraft:solid' and model.get('elements') for model in models)
        if ident==PAINTABLE_ID:
            painted=[model for model in models if 'surface' in model.get('textures',{})]
            assert {face for model in painted for element in model['elements'] for face in element['faces']}==set(SIDES), 'Fusion drops painted wall faces from the item'
    print(f'OK: {len(PAINTS)} paints on 8x8 squares across all six fixed half-block faces; both wall sides, four orientations and five pane poses, whole-face legacy import and item layers; glass/frame geometry remains isolated from painting.')

def check_laboratory_auditorium():
    from PIL import Image
    from building_blocks.create_laboratory_auditorium_materials import MATERIALS
    from render_texture_review import load
    for ident in MATERIALS:
        image=Image.open(module_path(A/f'textures/block/{ident}.png'));assert image.mode=='RGB' and image.size==(32,32)
        models=load('lsmmod:block/'+ident);assert len(models)==1
        box,=models[0]['elements'];assert box['from']==[0,0,0] and box['to']==[16,16,16] and set(box['faces'])==set(SIDES)
        assert read(A/f'blockstates/{ident}.json')['variants']=={'':{'model':'lsmmod:block/'+ident}}
        assert read(A/f'items/{ident}.json')['model']['model']=='lsmmod:item/'+ident
        loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json')
        assert loot['pools'][0]['entries'][0]['name']=='lsmmod:'+ident
    for ident,joint,target in (('school_laboratory_tile',(86,78,64),(183,173,151)),('laboratory_table_tile',(112,111,101),(220,220,211))):
        image=Image.open(module_path(A/f'textures/block/{ident}.png'))
        for y in range(32):
            for x in range(32):assert (image.getpixel((x,y))==joint)==(x%16==0 or y%16==0)
        pixels=[image.getpixel((x,y)) for x in range(32) for y in range(32) if x%16 and y%16]
        assert all(abs(sum(p[c] for p in pixels)/len(pixels)-target[c])<3 for c in range(3))
    top=Image.open(module_path(A/'textures/block/auditorium_stage_top.png'));assert top.mode=='RGB' and top.size==(32,32)
    assert 28<sum(sum(top.getpixel((x,y)))/3 for x in range(32) for y in range(32))/1024<42
    model=load('lsmmod:block/auditorium_stage')[0]
    assert model['elements'][0]['faces']['up']['texture']=='#top' and model['textures']['top']=='lsmmod:block/auditorium_stage_top'
    parquet=Image.open(module_path(A/'textures/block/auditorium_parquet.png'))
    means=[sum(parquet.getpixel((x,y))[0] for x in range(i*4,(i+1)*4) for y in range(32))/128 for i in range(8)]
    assert max(means)-min(means)>50
    assert PAINTS[13:]==tuple('lsmmod:block/'+name for name in MATERIALS[:3]) and len(PAINTS)==16
    for path in module_rglob(A/'models/block', '*.json'):
        model=read(path)
        for group in model.get('models',[]):
            colors={e['condition']['paint'] for e in group if isinstance(e,dict) and isinstance(e.get('condition',{}).get('paint'),int) and e['condition']['paint']>=0}
            if colors and max(colors)>0:assert {13,14,15}<=colors,(path,'new paints missing')
    registry=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModItems.java')
    for ident in MATERIALS[:3]:assert ident+'_paint' in registry
    assert 'auditorium_stage_paint' not in registry
    print('OK: four opaque block/item/drop sets; two tile rows/columns and reference colors, narrow parquet grain, dark wool only on stage top; stable paint IDs 13–15 fit four-bit storage and every active paint library includes them.')


def main():
    for texture in PAINTS:
        namespace, path = texture.split(':')
        if namespace == 'lsmmod':
            assert module_is_file(A/f'textures/{path}.png'), f'Missing paint texture: {texture}'
    assert PAINTS[4] == 'lsmmod:block/classroom_white_wall'
    assert PAINTS[5] == 'lsmmod:block/classroom_floor'
    assert PAINTS[6] == 'lsmmod:block/yellow_school_block'
    from PIL import Image
    assert Image.open(module_path(A/'textures/block/classroom_white_wall.png')).tobytes() == Image.open(module_path(A/'textures/block/classroom_painted_block.png')).tobytes()
    yellow=Image.open(module_path(A/'textures/block/yellow_school_block.png'))
    border=Image.open(module_path(A/'textures/block/school_entrance_pavement_0.png')).getpixel((0,0))
    assert yellow.mode=='RGB' and yellow.size==(16,16)
    assert border==(255,211,0) and {yellow.getpixel((x,y)) for x in range(16) for y in range(16)}=={border}
    enum=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/painting_and_editable_blocks/block/SchoolPaint.java')
    import re
    assert re.findall(r'^    ([A-Z_]+)\(',enum,re.M)==['UNPAINTED','LIGHT','DARK','STREET','WHITE','FLOOR','YELLOW','PLAYGROUND_WHITE','PLAYGROUND_GRAY','PLAYGROUND_GREEN','PLAYGROUND_ORANGE','DARK_TILED','RED_STREET','LABORATORY_TILE','LABORATORY_TABLE_TILE','AUDITORIUM_PARQUET']
    assert read(A/'blockstates/yellow_school_block.json')['variants']=={'':{'model':'lsmmod:block/yellow_school_block'}}
    assert read(A/'models/block/yellow_school_block.json')['parent']=='minecraft:block/cube_all'
    assert 'case YELLOW -> ModItems.YELLOW_SCHOOL_PAINT.get();' in module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/school_supplies/item/SchoolSandpaperItem.java')
    assert 'case YELLOW -> ModBlocks.YELLOW_SCHOOL_BLOCK.get();' in module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/painting_and_editable_blocks/compat/jade/PaintedWallNameProvider.java')
    assert 'id.equals("yellow_school_block")' in module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModCreativeTabs.java')
    grass=Image.open(module_path(A/'textures/block/school_synthetic_grass.png'))
    assert grass.mode == 'RGB' and grass.size == (16,16)
    averages=tuple(sum(pixel[c] for pixel in (grass.getpixel((x,y)) for y in range(16) for x in range(16)))/256 for c in range(3))
    assert all(abs(actual-target)<2 for actual,target in zip(averages,(83,126,62)))
    blocks=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java')
    grass_registration=blocks.split('SCHOOL_SYNTHETIC_GRASS =',1)[1].split(';',1)[0]
    assert 'SoundType.MOSS' in grass_registration
    state = read(A/'blockstates/school_paintable_block.json')
    assert list(state['variants']) == [''], 'Paint must not multiply block states'
    model = read(A/'models/block/school_paintable_block.json')
    assert model['loader'] == 'fusion:model' and len(model['models']) == 6
    # Prove the Cartesian product by independent face selectors rather than enumerating millions of combinations.
    for face,series in zip(SIDES,model['models']):
        assert len(series)==len(PAINTS)
        assert all(entry['condition']=={'type':'lsmmod:painted_face','face':face,'paint':paint}
                   for paint,entry in enumerate(series[:-1],1))
        assert 'condition' not in series[-1]
    palettes=[(color,)*6 for color in range(len(PAINTS))]
    palettes += [tuple(color if i==face else 0 for i in range(6)) for face in range(6) for color in range(len(PAINTS))]
    for colors in palettes:
        found = {}
        for series in model['models']:
            selected = next(entry for entry in series if 'condition' not in entry or
                            colors[SIDES.index(entry['condition']['face'])] == entry['condition']['paint'])
            leaf = leaves[selected['model']]
            for face, surface in leaf['elements'][0]['faces'].items():
                assert face not in found, 'Duplicate face causes overlapping geometry'
                assert surface['cullface'] == face and surface['uv'] == [0,0,16,16]
                found[face] = leaf['textures'][surface['texture'][1:]]
        assert found == dict(zip(SIDES, (PAINTS[color] for color in colors)))
    java = ROOT/'src/main/java/net/nicomar2009/lsmmod'
    blocks = module_registry_text(java/'registry/ModBlocks.java')
    items = module_registry_text(java/'registry/ModItems.java')
    assert '"classroom_white_wall"' in blocks and '"classroom_painted_block"' in blocks
    assert 'CLASSROOM_WHITE_WALL' in items and 'CLASSROOM_PAINTED_BLOCK' in items
    white = read(A/'models/block/classroom_white_wall.json')
    assert white['textures']['all'] == PAINTS[4]
    for item in TOOLS:
        assert item.upper() in items
        assert read(A/f'items/{item}.json')['model']['model'] == f'lsmmod:item/{item}'
    for locale in ('en_us','es_es'):
        lang = read(A/f'lang/{locale}.json')
        assert lang['config.jade.plugin_lsmmod.painted_wall_name']
        for name in ('school_paintable_block','light_school_wall','dark_school_wall','street_school_wall','classroom_white_wall','classroom_floor'):
            assert lang['block.lsmmod.'+name]
        for item in TOOLS:
            assert lang['item.lsmmod.'+item]
    print(f'OK: independent face selectors cover all {len(PAINTS)**6} palettes; {len(palettes)} isolation/uniform examples render exactly six non-overlapping faces; one state, tools, translations and white legacy alias preserved.')

leaves = {f'lsmmod:block/paintable_faces/{face}_{paint}': read(A/f'models/block/paintable_faces/{face}_{paint}.json')
          for face in SIDES for paint in range(len(PAINTS))}
if __name__ == '__main__':
    import sys
    if '--materials-only' in sys.argv:check_laboratory_auditorium()
    elif '--projecting-half-only' in sys.argv:check_projecting_half()
    else:main()
