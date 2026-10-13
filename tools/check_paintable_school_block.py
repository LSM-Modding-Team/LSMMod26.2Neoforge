"""Static resource contract: exhaustive palettes, face isolation and legacy white alias.

Does not compile Java or substitute for a client/server interaction test.
"""
import itertools
import json
from pathlib import Path
from create_paintable_school_block import ROOT, A, SIDES, PAINTS, TOOLS

def read(path):
    return json.loads(path.read_text(encoding='utf-8'))

def check_projecting_half():
    """Paint palettes must affect only the fixed half, across both profiles and all hinge poses."""
    from create_school_projecting_window_assets import PAINTABLE_ID, parts, rotation
    native={}
    for side in ('left','right'):
        white_low,white_high=(0,8) if side=='left' else (8,16)
        previous=None
        for phase in range(9):
            root=read(A/f'models/block/{PAINTABLE_ID}/phase_{phase}_{side}.json')
            fixed=root['models'][:6]
            assert previous is None or fixed==previous, 'Opening the window changes painted wall models'
            previous=fixed
            for group in fixed:
                assert len(group)==len(PAINTS)
                for paint in range(len(PAINTS)):
                    chosen=next(e for e in group if 'condition' not in e or e['condition']['paint']==paint)
                    leaf=read(A/f'models/{chosen["model"].split(":")[1]}.json')
                    box,=leaf['elements'];assert box['from']==[white_low,0,0] and box['to']==[white_high,16,16]
                    assert 'rotation' not in box and len(box['faces'])==1
                    face,=box['faces'];assert leaf['textures']['surface']==PAINTS[paint]
                    if paint:assert chosen['condition']['face']==face and chosen['condition']['local'] is True
                    native[side,face,paint]=leaf
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
    assert len(native)==2*6*len(PAINTS)
    variants=read(A/f'blockstates/{PAINTABLE_ID}.json')['variants'];assert len(variants)==8
    turns=dict(north=0,east=90,south=180,west=270)
    for key,apply in variants.items():
        fields=dict(value.split('=') for value in key.split(','));assert apply['y']==turns[fields['facing']]
        side='left' if fields['wall_left']=='true' else 'right'
        assert apply['model']==f'lsmmod:block/{PAINTABLE_ID}/{side}'
    print('OK: twelve paint colors on all six fixed half-block faces; both wall sides, four orientations and five pane poses; glass/frame materials are isolated from the palette.')

def main():
    for texture in PAINTS:
        namespace, path = texture.split(':')
        if namespace == 'lsmmod':
            assert (A/f'textures/{path}.png').is_file(), f'Missing paint texture: {texture}'
    assert PAINTS[4] == 'lsmmod:block/classroom_white_wall'
    assert PAINTS[5] == 'lsmmod:block/classroom_floor'
    assert PAINTS[6] == 'lsmmod:block/yellow_school_block'
    from PIL import Image
    assert Image.open(A/'textures/block/classroom_white_wall.png').tobytes() == Image.open(A/'textures/block/classroom_painted_block.png').tobytes()
    yellow=Image.open(A/'textures/block/yellow_school_block.png')
    border=Image.open(A/'textures/block/school_entrance_pavement_0.png').getpixel((0,0))
    assert yellow.mode=='RGB' and yellow.size==(16,16)
    assert border==(255,211,0) and {yellow.getpixel((x,y)) for x in range(16) for y in range(16)}=={border}
    enum=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolPaint.java').read_text()
    import re
    assert re.findall(r'^    ([A-Z_]+)\(',enum,re.M)==['UNPAINTED','LIGHT','DARK','STREET','WHITE','FLOOR','YELLOW','PLAYGROUND_WHITE','PLAYGROUND_GRAY','PLAYGROUND_GREEN','PLAYGROUND_ORANGE','DARK_TILED']
    assert read(A/'blockstates/yellow_school_block.json')['variants']=={'':{'model':'lsmmod:block/yellow_school_block'}}
    assert read(A/'models/block/yellow_school_block.json')['parent']=='minecraft:block/cube_all'
    assert 'case YELLOW -> ModItems.YELLOW_SCHOOL_PAINT.get();' in (ROOT/'src/main/java/net/nicomar2009/lsmmod/item/SchoolSandpaperItem.java').read_text()
    assert 'case YELLOW -> ModBlocks.YELLOW_SCHOOL_BLOCK.get();' in (ROOT/'src/main/java/net/nicomar2009/lsmmod/compat/jade/PaintedWallNameProvider.java').read_text()
    assert 'id.equals("yellow_school_block")' in (ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModCreativeTabs.java').read_text()
    grass=Image.open(A/'textures/block/school_synthetic_grass.png')
    assert grass.mode == 'RGB' and grass.size == (16,16)
    averages=tuple(sum(pixel[c] for pixel in (grass.getpixel((x,y)) for y in range(16) for x in range(16)))/256 for c in range(3))
    assert all(abs(actual-target)<2 for actual,target in zip(averages,(83,126,62)))
    blocks=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text()
    grass_registration=blocks.split('SCHOOL_SYNTHETIC_GRASS =',1)[1].split(';',1)[0]
    assert 'SoundType.MOSS' in grass_registration
    state = read(A/'blockstates/school_paintable_block.json')
    assert list(state['variants']) == [''], 'Paint must not multiply block states'
    model = read(A/'models/block/school_paintable_block.json')
    assert model['loader'] == 'fusion:model' and len(model['models']) == 6
    for colors in itertools.product(range(len(PAINTS)), repeat=6):
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
    blocks = (java/'registry/ModBlocks.java').read_text()
    items = (java/'registry/ModItems.java').read_text()
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
    print(f'OK: {len(PAINTS)**6} palettes render exactly six non-overlapping faces; one state, all tools, translations and white legacy alias preserved.')

leaves = {f'lsmmod:block/paintable_faces/{face}_{paint}': read(A/f'models/block/paintable_faces/{face}_{paint}.json')
          for face in SIDES for paint in range(len(PAINTS))}
if __name__ == '__main__':
    import sys
    if '--projecting-half-only' in sys.argv:check_projecting_half()
    else:main()
