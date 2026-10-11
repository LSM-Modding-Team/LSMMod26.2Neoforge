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
    state = read(A/'blockstates/school_paintable_block.json')
    assert list(state['variants']) == [''], 'Paint must not multiply block states'
    model = read(A/'models/block/school_paintable_block.json')
    assert model['loader'] == 'fusion:model' and len(model['models']) == 6
    for colors in itertools.product(range(5), repeat=6):
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
        for name in ('school_paintable_block','light_school_wall','dark_school_wall','street_school_wall','classroom_white_wall'):
            assert lang['block.lsmmod.'+name]
        for item in TOOLS:
            assert lang['item.lsmmod.'+item]
    print('OK: 15625 palettes render exactly six non-overlapping faces; one state, all tools, translations and white legacy alias preserved.')

leaves = {f'lsmmod:block/paintable_faces/{face}_{paint}': read(A/f'models/block/paintable_faces/{face}_{paint}.json')
          for face in SIDES for paint in range(5)}
if __name__ == '__main__':
    main()
