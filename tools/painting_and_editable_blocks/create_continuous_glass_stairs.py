"""Continuous-glazing variant; reuse the existing paint palettes, base and curtains."""
import sys
from pathlib import Path
TOOLS = Path(__file__).resolve().parents[1]
if str(TOOLS) not in sys.path:
    sys.path.insert(0, str(TOOLS))
import copy
import hashlib
import itertools
import json
from resource_json import resource_json
from painting_and_editable_blocks.create_school_glass_stair_assets import glass_parts, QUARTERS, T

ROOT = TOOLS.parent
MODULE = ROOT / 'src/main/resources/painting_and_editable_blocks'
ASSETS = MODULE / 'assets/lsmmod'
SOURCE = 'school_paintable_glass_stairs'
ID = 'school_paintable_continuous_glass_stairs'


def read(path):
    return json.loads(path.read_text(encoding='utf-8'))


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(resource_json(path, value, ensure_ascii=False) + '\n', encoding='utf-8')


def main():
    shared = {}
    for half, shape in itertools.product(('bottom', 'top'), QUARTERS):
        selectors = []
        for mask in (value for value in range(32) if not value & 4):
            groups = []
            elements = glass_parts(half, shape, mask, continuous=True)
            for layer, material in (('solid', 'frame'), ('translucent', 'glass')):
                subset = [e for e in elements if next(iter(e['faces'].values()))['texture'] == '#' + material]
                if not subset:
                    continue
                value = {'parent': 'minecraft:block/block', 'loader': 'fusion:model', 'type': 'base',
                         'render_type': 'minecraft:' + layer, 'textures': dict(T, particle=T['frame']), 'elements': subset}
                signature = json.dumps(value, sort_keys=True, separators=(',', ':'))
                if signature not in shared:
                    part = ID + '/parts/' + hashlib.sha256(signature.encode('utf-8')).hexdigest()[:24]
                    shared[signature] = 'lsmmod:block/' + part
                    write(ASSETS / f'models/block/{part}.json', value)
                groups.append([shared[signature]])
            name = f'{ID}/{half}_{shape}_{mask}'
            write(ASSETS / f'models/block/{name}.json', {'loader': 'fusion:model', 'type': 'composite',
                  'textures': {'particle': T['frame']}, 'models': groups})
            selectors.append({'model': 'lsmmod:block/' + name, 'condition': {'type': 'lsmmod:glazing_mask', 'mask': mask}})
        write(ASSETS / f'models/block/{ID}/{half}_{shape}.json', {'loader': 'fusion:model', 'type': 'composite',
              'textures': {'particle': T['frame']}, 'models': [selectors]})
    state = read(ASSETS / f'blockstates/{SOURCE}.json')
    for part in state['multipart']:
        model = part['apply']['model']
        if model.startswith('lsmmod:block/' + SOURCE + '/'):
            part['apply']['model'] = model.replace(SOURCE, ID, 1)
    write(ASSETS / f'blockstates/{ID}.json', state)
    item = read(ASSETS / f'models/item/{SOURCE}.json')
    item['models'][1] = [f'lsmmod:block/{ID}/bottom_straight_3']
    write(ASSETS / f'models/item/{ID}.json', item)
    write(ASSETS / f'items/{ID}.json', {'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/' + ID}})
    loot = read(MODULE / f'data/lsmmod/loot_table/blocks/{SOURCE}.json')
    for pool in loot['pools']:
        for entry in pool['entries']:
            if entry.get('name') == 'lsmmod:' + SOURCE:
                entry['name'] = 'lsmmod:' + ID
    write(MODULE / f'data/lsmmod/loot_table/blocks/{ID}.json', loot)
    for locale, label in [('es_es', 'Escalera pintable con cristal continuo'), ('en_us', 'Paintable Continuous Glass Stairs')]:
        path = ASSETS / f'lang/{locale}.json'
        data = read(path)
        data['block.lsmmod.' + ID] = label
        write(path, data)
    path = MODULE / 'data/minecraft/tags/block/mineable/pickaxe.json'
    tag = read(path)
    if 'lsmmod:' + ID not in tag['values']:
        tag['values'].append('lsmmod:' + ID)
    write(path, tag)


if __name__ == '__main__':
    main()
