"""Generate the school monitor's code-native model and pixel texture."""
import json
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/lsmmod'

def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')

image = Image.new('RGB', (64, 64), '#35383c')
draw = ImageDraw.Draw(image)
# Plastic, screen, rear ventilation and front bezel quadrants.
for y in range(32):
    for x in range(32):
        image.putpixel((x, y), (55, 58, 62) if (x + y) % 9 else (61, 64, 68))
draw.rectangle((32, 0, 63, 31), fill='#172b3b')
for y in range(2, 29):
    draw.line((34, y, 61, y), fill=(22, 61 + y * 2, 84 + y * 2))
draw.polygon([(34, 27), (45, 10), (58, 27)], fill='#37748c')
draw.polygon([(45, 27), (56, 16), (61, 27)], fill='#6198aa')
draw.rectangle((34, 29, 61, 30), fill='#1c3545')
draw.rectangle((0, 32, 31, 63), fill='#303337')
for y in [37, 40, 43, 54, 57]:
    draw.line((5, y, 26, y), fill='#171a1d')
draw.rectangle((10, 47, 22, 51), fill='#74777a')
draw.rectangle((12, 48, 20, 50), fill='#a7a9aa')
draw.rectangle((32, 32, 63, 63), fill='#292c30')
draw.line((35, 36, 60, 36), fill='#484b50')
draw.rectangle((44, 53, 50, 54), fill='#888d92')
draw.rectangle((57, 55, 58, 56), fill='#85b98a')
image.save(ASSETS / 'textures/block/pc.png')

def box(start, end, faces=None):
    result = {side: {'texture': '#atlas', 'uv': [0, 0, 8, 8]}
              for side in ['north', 'south', 'east', 'west', 'up', 'down']}
    for side, uv in (faces or {}).items():
        result[side]['uv'] = uv
    return {'from': start, 'to': end, 'faces': result}

elements = [
    box([4, 0, 5], [12, 1, 12]),
    box([6.75, 1, 7], [9.25, 5, 9]),
    box([1, 4, 6], [15, 14, 8.5], {'south': [8, 8, 16, 16]}),
    # Stepped rear housing gives a modest, older LCD monitor its depth.
    box([2, 5, 5], [14, 13, 6], {'north': [0, 8, 8, 16]}),
    box([4, 6, 4.5], [12, 12, 5], {'north': [0, 8, 8, 16]}),
    box([2.1, 5.7, 8.5], [13.9, 12.9, 8.55], {'south': [8, 0, 16, 8]}),
]
write(ASSETS / 'models/block/pc.json', {
    'parent': 'minecraft:block/block',
    'textures': {'atlas': 'lsmmod:block/pc', 'particle': 'lsmmod:block/pc'},
    'elements': elements,
    'display': {
        'gui': {'rotation': [20, 135, 0], 'translation': [0, 0, 0], 'scale': [.85, .85, .85]},
        'ground': {'translation': [0, 2, 0], 'scale': [.5, .5, .5]},
        'fixed': {'rotation': [0, 180, 0], 'scale': [.75, .75, .75]}}})
write(ASSETS / 'blockstates/pc.json', {'variants': {
    f'facing={direction}': {'model': 'lsmmod:block/pc', 'y': angle}
    for direction, angle in [('south', 0), ('west', 90), ('north', 180), ('east', 270)]}})
write(ASSETS / 'items/pc.json', {'model': {'type': 'minecraft:model', 'model': 'lsmmod:block/pc'}})
write(ROOT / 'src/main/resources/data/lsmmod/loot_table/blocks/pc.json', {
    'type': 'minecraft:block', 'pools': [{'rolls': 1,
    'conditions': [{'condition': 'minecraft:survives_explosion'}],
    'entries': [{'type': 'minecraft:item', 'name': 'lsmmod:pc'}]}]})
for language, label in [('es_es', 'PC escolar'), ('en_us', 'School PC')]:
    path = ASSETS / f'lang/{language}.json'
    data = json.loads(path.read_text(encoding='utf-8'))
    data['block.lsmmod.pc'] = label
    write(path, data)

source = ROOT / 'src/main/java/net/nicomar2009/lsmmod/block'
java = (source / 'LaptopBlock.java').read_text(encoding='utf-8')
java = java.replace('LaptopBlock', 'MonitorBlock').replace(
    'Open decorative laptop. No inventory, sound or interaction behavior yet.',
    'Decorative school PC represented only by its monitor, stand and base.')
java = java.replace(
    'Shapes.or(Block.box(1, 0, 3, 15, 1, 13), Block.box(1, 1, 3, 15, 10, 4))',
    'Shapes.or(Block.box(4, 0, 5, 12, 1, 12), Block.box(6.75, 1, 7, 9.25, 5, 9),\n'
    '                Block.box(1, 4, 6, 15, 14, 8.55), Block.box(2, 5, 5, 14, 13, 6),\n'
    '                Block.box(4, 6, 4.5, 12, 12, 5))')
(source / 'MonitorBlock.java').write_text(java, encoding='utf-8')
