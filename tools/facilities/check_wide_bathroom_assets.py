"""Static geometry, two-cell assembly and connected light tile regressions; no Java compilation."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_path, module_read_text
import itertools
import json
from PIL import Image
from facilities.create_wide_bathroom_assets import ROOT, ASSETS, DATA, FIXTURES, clip
from facilities.create_bathroom_statue_assets import solid_model
from facilities.check_bathroom_statue_assets import mesh


def read(path): return json.loads(module_read_text(path))


def main():
    for block_id, (closed, opened, height) in FIXTURES.items():
        variants = read(ASSETS / f'blockstates/{block_id}.json')['variants']
        assert len(variants) == (32 if height == 2 else 8)
        assert all(0<=b[0][0]<b[0][3]<=32 for b in closed)
        for is_open, parts in ((False, closed), (True, opened)) if height == 2 else ((False, closed),):
            full = solid_model(parts)
            cells = {}
            for half in range(height):
                for column in range(2):
                    half_name = 'upper' if half else 'lower'
                    name = f'{block_id}_{half_name}_{"open" if is_open else "closed"}_column_{column}'
                    model = read(ASSETS / f'models/block/{name}.json')
                    assert model == clip(full, column, half)
                    cells[column, half] = mesh(model, name)
                    for facing, angle in (('north', 0), ('east', 90), ('south', 180), ('west', 270)):
                        key = f'column={column},facing={facing}'
                        if height == 2: key += f',half={half_name},open={str(is_open).lower()}'
                        assert variants[key] == {'model': f'lsmmod:block/{name}', 'y': angle}
                        # Every rotated cell resolves to the original lower-left controller.
                        dx, dz = ((1, 0), (0, 1), (-1, 0), (0, -1))[angle//90]
                        target = (13+dx*column, 70+half, -8+dz*column)
                        assert (target[0]-dx*column, target[1]-half, target[2]-dz*column) == (13, 70, -8)
                    for element in model['elements']:
                        lo, hi = element['from'][:], element['to'][:]
                        lo[0] += column*16; hi[0] += column*16
                        lo[1] += half*16; hi[1] += half*16
                        assert any(all(b[i] <= lo[i] < hi[i] <= b[i+3]+1e-9 for i in range(3)) for b, *_ in parts)
            # Splitting a solid cannot introduce overlapping caps at x=16 or y=16.
            for (column, half), faces in cells.items():
                for axis, plane, rect in faces:
                    if axis == 0 and plane == 16 and column == 0:
                        for other_axis, other_plane, other in cells[1, half]:
                            if other_axis == axis and other_plane == 0:
                                assert min(rect[2], other[2]) <= max(rect[0], other[0]) or min(rect[3], other[3]) <= max(rect[1], other[1])
        mesh(read(ASSETS / f'models/item/{block_id}.json'), block_id+' item')
        props = {'column': '0'}
        if height == 2: props['half'] = 'lower'
        loot = read(DATA / f'lsmmod/loot_table/blocks/{block_id}.json')
        condition = next(c for c in loot['pools'][0]['conditions'] if c['condition'] == 'minecraft:block_state_property')
        assert condition['properties'] == props
        for half, column in itertools.product(range(height), range(2)):
            matching = column == 0 and half == 0
            assert matching == (all(str(column if k == 'column' else ('upper' if half else 'lower')) == v for k, v in props.items()))
        for locale in ('es_es', 'en_us'): assert f'block.lsmmod.{block_id}' in read(ASSETS / f'lang/{locale}.json')
    assert all(p[1] != 'partition' for p in FIXTURES['standalone_toilet'][0])
    inside = lambda b, point: all(b[i] < point[i] < b[i+3] for i in range(3))
    for y in (8, 24):
        point = (16, y, .5)
        assert any(inside(b, point) for b, *_ in FIXTURES['toilet'][0])
        assert not any(inside(b, point) for b, *_ in FIXTURES['toilet'][1])
    tiles = {(x,z): Image.open(module_path(ASSETS / f'textures/block/light_school_tiled_floor_{x}_{z}.png')) for x,z in itertools.product(range(3),repeat=2)}
    assert len(read(ASSETS / 'blockstates/light_school_tiled_floor.json')['variants']) == 9
    for bx,bz in itertools.product(range(-4,5),repeat=2):
        tile = tiles[bx%3,bz%3]
        for y,x in itertools.product(range(16),repeat=2):
            joint = (bx*16+x)%24 == 0 or (bz*16+y)%24 == 0
            assert (tile.getpixel((x,y)) == (173,177,182)) == joint
            assert min(tile.getpixel((x,y))) >= 173
    source = module_read_text(ROOT / 'src/main/java/net/nicomar2009/lsmmod/modules/facilities/block/WideBathroomBlock.java')
    assert 'SimpleBlockOutline.forState' in source and 'Shapes.rotateHorizontal' in source and 'ExactVoxelShapes.union' in source
    assert 'BlockEntity' not in source and 'Shapes.or' not in source
    from audit_block_states import audit
    rows = {r['id']: r for r in audit()}
    for name, (_, _, height) in FIXTURES.items(): assert rows[name]['states'] == (32 if height == 2 else 8)
    assert rows['light_school_tiled_floor']['states'] == 9
    print('OK: four two-wide fixtures, exact cell models, four rotations, hollow bowl, door passage, single controller drops; light tiles every 24px including negative coordinates.')


if __name__ == '__main__': main()
