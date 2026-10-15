"""Full-block speaker using the tall cabinet's grille and upper horn, without a badge."""
import json
from PIL import Image
from resource_json import ROOT, resource_json

RESOURCES = ROOT / 'src/main/resources'
ASSETS = RESOURCES / 'assets/lsmmod'


def write(path, data):
    path.write_text(resource_json(path, data) + '\n', encoding='utf-8')


def main():
    # Keep the top horn and both dark cones. Remove the small gray badge from
    # the donor texture; no gray/green control unit is added at the center.
    image = Image.open(ASSETS / 'textures/block/tall_speaker_front.png').convert('RGBA')
    for y in range(30, 31):
        for x in range(7, 10):
            image.putpixel((x, y), image.getpixel((6, y)))
    image.save(ASSETS / 'textures/block/general_speaker_front.png')
    # Retain the existing cabinet's materials, UV and item transforms, expanding
    # its 12px depth to the full block instead of stretching a two-cell model.
    model = json.loads((ASSETS / 'models/block/speaker.json').read_text())
    model['elements'][0]['faces']['north'], model['elements'][0]['faces']['south'] = (
        model['elements'][0]['faces']['south'], model['elements'][0]['faces']['north'])
    model['textures']['front'] = 'lsmmod:block/general_speaker_front'
    model['elements'][0]['from'] = [0, 0, 0]
    model['elements'][0]['to'] = [16, 16, 16]
    write(ASSETS / 'models/block/general_speaker.json', model)
    write(ASSETS / 'blockstates/general_speaker.json', {'variants': {
        f'facing={facing}': {'model': 'lsmmod:block/general_speaker', 'y': rotation}
        for facing, rotation in [('north', 0), ('east', 90), ('south', 180), ('west', 270)]
    }})
    write(ASSETS / 'models/item/general_speaker.json', {'parent': 'lsmmod:block/general_speaker'})
    write(ASSETS / 'items/general_speaker.json', {'model': {
        'type': 'minecraft:model', 'model': 'lsmmod:item/general_speaker'}})
    loot = RESOURCES / 'data/lsmmod/loot_table/blocks/general_speaker.json'
    data = json.loads((loot.parent / 'speaker.json').read_text())
    data['pools'][0]['entries'][0]['name'] = 'lsmmod:general_speaker'
    write(loot, data)


if __name__ == '__main__':
    main()
