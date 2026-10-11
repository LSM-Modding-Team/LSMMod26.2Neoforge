"""Static resource contract: exhaustive palettes, face isolation and legacy white alias.

Does not compile Java or substitute for a client/server interaction test.
"""
import itertools
import json
from pathlib import Path
from create_paintable_school_block import ROOT, A, SIDES, PAINTS, TOOLS

def read(path):
    return json.loads(path.read_text(encoding='utf-8'))

def main():
    for texture in PAINTS:
        namespace, path = texture.split(':')
        if namespace == 'lsmmod':
            assert (A/f'textures/{path}.png').is_file(), f'Missing paint texture: {texture}'
    assert PAINTS[4] == 'lsmmod:block/classroom_white_wall'
    assert PAINTS[5] == 'lsmmod:block/classroom_floor'
    from PIL import Image
    assert Image.open(A/'textures/block/classroom_white_wall.png').tobytes() == Image.open(A/'textures/block/classroom_painted_block.png').tobytes()
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
        for name in ('school_paintable_block','light_school_wall','dark_school_wall','street_school_wall','classroom_white_wall','classroom_floor'):
            assert lang['block.lsmmod.'+name]
        for item in TOOLS:
            assert lang['item.lsmmod.'+item]
    print(f'OK: {len(PAINTS)**6} palettes render exactly six non-overlapping faces; one state, all tools, translations and white legacy alias preserved.')

leaves = {f'lsmmod:block/paintable_faces/{face}_{paint}': read(A/f'models/block/paintable_faces/{face}_{paint}.json')
          for face in SIDES for paint in range(len(PAINTS))}
if __name__ == '__main__':
    main()
