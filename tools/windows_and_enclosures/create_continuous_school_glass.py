"""Generate a continuous framed-glass variant using existing school glazing models."""
import sys
from pathlib import Path

TOOLS = Path(__file__).resolve().parents[1]
if str(TOOLS) not in sys.path:
    sys.path.insert(0, str(TOOLS))

import copy
import json
from resource_json import resource_json

ROOT = TOOLS.parent
MODULE = ROOT / 'src/main/resources/windows_and_enclosures'
ASSETS = MODULE / 'assets/lsmmod'
ID = 'school_continuous_glass'


def read(path):
    return json.loads(path.read_text(encoding='utf-8'))


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(resource_json(path, value, ensure_ascii=False) + '\n', encoding='utf-8')


def main():
    definition = read(ASSETS / 'blockstates/school_glass.json')
    panes = [part for part in definition['multipart'] if 'curtain' not in part.get('when', {})]
    source = panes[0]['apply']['model'].split(':', 1)[1]
    model = read(ASSETS / f'models/{source}.json')
    # Continuous glazing never selects the periodic middle-post bit (4).
    for index, series in enumerate(model['models']):
        selected = []
        for original in series:
            entry = copy.deepcopy(original)
            condition = entry['condition']
            masks = [mask for mask in condition.get('masks', [condition.get('mask')]) if mask is not None and not mask & 4]
            if not masks:
                continue
            condition.pop('mask', None)
            condition.pop('masks', None)
            condition['mask' if len(masks) == 1 else 'masks'] = masks[0] if len(masks) == 1 else masks
            selected.append(entry)
        model['models'][index] = selected
    write(ASSETS / f'models/block/{ID}.json', model)
    for part in panes:
        part['apply']['model'] = 'lsmmod:block/' + ID
    write(ASSETS / f'blockstates/{ID}.json', definition)
    write(ASSETS / f'models/item/{ID}.json', read(ASSETS / 'models/item/school_glass.json'))
    write(ASSETS / f'items/{ID}.json', {'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/' + ID}})
    loot = read(MODULE / 'data/lsmmod/loot_table/blocks/school_glass.json')
    for pool in loot['pools']:
        for entry in pool['entries']:
            if entry.get('name') == 'lsmmod:school_glass':
                entry['name'] = 'lsmmod:' + ID
    write(MODULE / f'data/lsmmod/loot_table/blocks/{ID}.json', loot)
    for locale, title in [('es_es', 'Cristal continuo del colegio'), ('en_us', 'Continuous School Glass')]:
        path = ASSETS / f'lang/{locale}.json'
        values = read(path)
        values['block.lsmmod.' + ID] = title
        write(path, values)


if __name__ == '__main__':
    main()
