"""Generate the two code-native pixel-art laptop models and texture atlases."""
import json
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/lsmmod'

def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')

for name, luxury in [('laptop', False), ('moises_laptop', True)]:
    image = Image.new('RGB', (64, 64), '#d6ad39' if luxury else '#898d96')
    draw = ImageDraw.Draw(image)
    # Four atlas quadrants: metal, keyboard deck, display, lid.
    for y in range(32):
        for x in range(32):
            image.putpixel((x, y), ((224, 183, 65) if (x+y)%7 else (244, 210, 107)) if luxury
                           else ((145, 149, 158) if (x+y)%7 else (160, 164, 173)))
    draw.rectangle((32, 0, 63, 31), fill='#edca62' if luxury else '#b6bac2')
    for row in range(4):
        for col in range(10):
            x, y = 34 + col*3, 3 + row*3
            draw.rectangle((x, y, x+1, y+1), fill='#258ea3' if luxury else '#333740')
    draw.rectangle((41, 16, 55, 18), fill='#258ea3' if luxury else '#333740')
    draw.rectangle((43, 21, 53, 28), fill='#66d6e3' if luxury else '#8a909a', outline='#c59628' if luxury else '#747b86')
    draw.rectangle((0, 32, 31, 63), fill='#145f77' if luxury else '#17243b')
    for y in range(34, 60):
        draw.line((2, y, 29, y), fill=(20, 95+(y-34)*3, 135+(y-34)*3))
    draw.polygon([(3, 55), (16, 39), (28, 55)], fill='#52cbdc' if luxury else '#3873a3')
    draw.polygon([(12, 54), (24, 42), (29, 54)], fill='#b8f4f3' if luxury else '#70a5bb')
    draw.rectangle((2, 60, 29, 61), fill='#082c40')
    draw.rectangle((32, 32, 63, 63), fill='#d8ae38' if luxury else '#666c75')
    if luxury:
        draw.polygon([(48, 41), (55, 47), (48, 56), (41, 47)], fill='#73dfeb', outline='#d5ffff')
        draw.line((41, 47, 55, 47), fill='#2395b5')
        draw.line((48, 41, 46, 47, 48, 56), fill='#d5ffff')
    else:
        draw.ellipse((43, 43, 53, 53), outline='#b8bdc5', width=1)
        draw.line((46, 46, 46, 50), fill='#cbd0d8')
        draw.line((50, 46, 50, 50), fill='#cbd0d8')
    image.save(ASSETS / f'textures/block/{name}.png')

    metal, deck, screen, lid = [0, 0, 8, 8], [8, 0, 16, 8], [0, 8, 8, 16], [8, 8, 16, 16]
    def box(start, end, overrides=None):
        faces = {face: {'texture': '#atlas', 'uv': metal} for face in ['north','south','east','west','up','down']}
        for face, uv in (overrides or {}).items():
            faces[face]['uv'] = uv
        return {'from': start, 'to': end, 'faces': faces}
    elements = [box([1, 0, 3], [15, 1, 13], {'up': deck}),
                box([1, 1, 3], [15, 10, 4], {'north': lid}),
                box([2, 2, 4], [14, 9, 4.05], {'south': screen}),
                box([2, 1, 3.8], [4, 1.4, 4.4]),
                box([12, 1, 3.8], [14, 1.4, 4.4])]
    # Small camera in the upper bezel and a power button above the keyboard.
    elements += [box([7.8, 9.35, 4], [8.2, 9.65, 4.06], {'south': [8.5, .75, 9, 1.25]}),
                 box([13.1, 1, 4.8], [13.8, 1.08, 5.2], {'up': [10.75, 5.25, 11.25, 5.75]})]
    write(ASSETS / f'models/block/{name}.json', {
        'parent': 'minecraft:block/block', 'textures': {'atlas': f'lsmmod:block/{name}', 'particle': f'lsmmod:block/{name}'},
        'elements': elements, 'display': {
            'gui': {'rotation': [25, 135, 0], 'translation': [0, 2, 0], 'scale': [.85, .85, .85]},
            'ground': {'translation': [0, 3, 0], 'scale': [.5, .5, .5]},
            'fixed': {'rotation': [0, 180, 0], 'translation': [0, 2, 0], 'scale': [.75, .75, .75]}}})
    write(ASSETS / f'blockstates/{name}.json', {'variants': {
        f'facing={direction}': {'model': f'lsmmod:block/{name}', 'y': angle}
        for direction, angle in [('south', 0), ('west', 90), ('north', 180), ('east', 270)]}})
    write(ASSETS / f'items/{name}.json', {'model': {'type': 'minecraft:model', 'model': f'lsmmod:block/{name}'}})
    write(ROOT / f'src/main/resources/data/lsmmod/loot_table/blocks/{name}.json', {
        'type': 'minecraft:block', 'pools': [{'rolls': 1, 'conditions': [{'condition': 'minecraft:survives_explosion'}],
        'entries': [{'type': 'minecraft:item', 'name': f'lsmmod:{name}'}]}]})

for language in ['es_es', 'en_us']:
    path = ASSETS / f'lang/{language}.json'
    data = json.loads(path.read_text(encoding='utf-8'))
    data.update({'block.lsmmod.laptop': 'Laptop', 'block.lsmmod.moises_laptop': 'Laptop de Moisés' if language == 'es_es' else "Moisés\u2019 Laptop"})
    write(path, data)
