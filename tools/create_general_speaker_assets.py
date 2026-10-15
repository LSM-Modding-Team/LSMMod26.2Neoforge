"""Two-cell general speaker with a gray upper horn, based on the tall speaker."""
import copy
import json
from PIL import Image, ImageDraw
from resource_json import ROOT, resource_json

RESOURCES = ROOT / 'src/main/resources'
ASSETS = RESOURCES / 'assets/lsmmod'


def write(path, data):
    path.write_text(resource_json(path, data) + '\n', encoding='utf-8')


def main():
    front = Image.open(ASSETS / 'textures/block/tall_speaker_front.png').convert('RGBA')
    for x in range(7, 10):
        front.putpixel((x, 30), front.getpixel((6, 30)))
    front.save(ASSETS / 'textures/block/general_speaker_front.png')
    horn = Image.new('RGBA', (16, 8), (105, 120, 129, 255))
    draw = ImageDraw.Draw(horn)
    draw.rectangle((0, 0, 15, 0), fill=(151, 166, 174, 255))
    draw.rectangle((0, 7, 15, 7), fill=(56, 65, 73, 255))
    draw.rectangle((2, 2, 13, 5), fill=(55, 67, 76, 255))
    draw.rectangle((3, 3, 12, 4), fill=(15, 21, 27, 255))
    horn.save(ASSETS / 'textures/block/general_speaker_horn.png')
    models = {}
    for half in ('lower', 'upper'):
        model = json.loads((ASSETS / f'models/block/tall_speaker_{half}.json').read_text())
        model['textures'].update(front='lsmmod:block/general_speaker_front', horn='lsmmod:block/general_speaker_horn')
        body = model['elements'][0]
        body['faces']['north'], body['faces']['south'] = body['faces']['south'], body['faces']['north']
        if half == 'upper':
            model['elements'].append({'from': [1, 10, 0], 'to': [15, 16, 3], 'faces': {
                face: {'texture': '#horn', 'uv': ([0, 0, 16, 16] if face in ('north', 'south')
                      else [0, 0, 16, 2] if face == 'up' else [0, 14, 16, 16] if face == 'down' else [0, 0, 1, 16])}
                for face in ('north', 'south', 'east', 'west', 'up', 'down')}})
        write(ASSETS / f'models/block/general_speaker_{half}.json', model)
        models[half] = model
    write(ASSETS / 'blockstates/general_speaker.json', {'variants': {
        f'facing={facing},half={half}': {'model': f'lsmmod:block/general_speaker_{half}', 'y': rotation}
        for facing, rotation in [('north', 0), ('east', 90), ('south', 180), ('west', 270)]
        for half in ('lower', 'upper')
    }})
    item = copy.deepcopy(models['lower'])
    for e in models['upper']['elements']:
        e = copy.deepcopy(e)
        e['from'][1] += 16; e['to'][1] += 16
        item['elements'].append(e)
    item['display'] = {'gui': {'rotation': [30, 225, 0], 'translation': [0, -3, 0], 'scale': [.45, .45, .45]},
                       'ground': {'translation': [0, 2, 0], 'scale': [.25, .25, .25]}}
    # Keep the previous model path as the complete item model.
    write(ASSETS / 'models/block/general_speaker.json', item)
    write(ASSETS / 'models/item/general_speaker.json', {'parent': 'lsmmod:block/general_speaker'})
    write(ASSETS / 'items/general_speaker.json', {'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/general_speaker'}})
    loot = RESOURCES / 'data/lsmmod/loot_table/blocks/general_speaker.json'
    data = json.loads((loot.parent / 'tall_speaker.json').read_text())
    text = json.dumps(data).replace('lsmmod:tall_speaker', 'lsmmod:general_speaker')
    write(loot, json.loads(text))


if __name__ == '__main__':
    main()
