"""Generate shared Minecraft stationery geometry and interchangeable pixel atlases."""
import json
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/lsmmod'
COLORS = {
    'white': ('#deded6', 'blanco', 'White'), 'orange': ('#e7862c', 'naranja', 'Orange'),
    'magenta': ('#bb4caf', 'magenta', 'Magenta'), 'light_blue': ('#55a8d5', 'celeste', 'Light Blue'),
    'yellow': ('#e8c73c', 'amarillo', 'Yellow'), 'lime': ('#85ba39', 'verde lima', 'Lime'),
    'pink': ('#e49bac', 'rosado', 'Pink'), 'gray': ('#555a61', 'gris', 'Gray'),
    'light_gray': ('#a6aaad', 'gris claro', 'Light Gray'), 'cyan': ('#2499a4', 'cian', 'Cyan'),
    'purple': ('#7842a2', 'morado', 'Purple'), 'blue': ('#30579d', 'azul', 'Blue'),
    'brown': ('#805333', 'marrón', 'Brown'), 'green': ('#3d7544', 'verde', 'Green'),
    'red': ('#b83b3d', 'rojo', 'Red'), 'black': ('#272b32', 'negro', 'Black'),
}

def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


def box(start, end, texture='cover', uv=(0, 0, 8, 12), overrides=None):
    faces = {f: {'texture': '#' + texture, 'uv': list(uv)}
             for f in ('north', 'south', 'east', 'west', 'up', 'down')}
    for face, value in (overrides or {}).items():
        faces[face] = {'texture': '#' + value[0], 'uv': list(value[1])}
    return {'from': start, 'to': end, 'faces': faces}

DISPLAY = {
    'gui': {'rotation': [12, -28, -12], 'scale': [.88, .88, .88]},
    'ground': {'rotation': [90, 0, 0], 'translation': [0, 1, 0], 'scale': [.55, .55, .55]},
    'fixed': {'rotation': [0, 180, 0], 'scale': [.85, .85, .85]},
    'thirdperson_righthand': {'rotation': [0, -90, 0], 'translation': [0, 1, 0], 'scale': [.65, .65, .65]},
    'thirdperson_lefthand': {'rotation': [0, 90, 0], 'translation': [0, 1, 0], 'scale': [.65, .65, .65]},
    'firstperson_righthand': {'rotation': [0, -55, -8], 'translation': [1, 1, 0], 'scale': [.72, .72, .72]},
    'firstperson_lefthand': {'rotation': [0, 55, 8], 'translation': [1, 1, 0], 'scale': [.72, .72, .72]},
}

# Atlas regions: subtly textured solid cover, darker binding, inner cardboard.
for name, (hexcolor, _, _) in COLORS.items():
    base = tuple(bytes.fromhex(hexcolor[1:]))
    image = Image.new('RGB', (128, 128))
    for y in range(128):
        for x in range(128):
            factor = 1 if x < 64 else (.72 if y < 64 else .91)
            noise = ((x * 17 + y * 31) % 7 - 3) * .65
            image.putpixel((x, y), tuple(max(0, min(255, round(v * factor + noise))) for v in base))
    path = ASSETS / f'textures/item/stationery/cover_{name}.png'
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)

image = Image.new('RGB', (128, 128), '#edeadf')
draw = ImageDraw.Draw(image)
# Horizontal leaf edges: use UV strips on all three exposed notebook edges.
for y in range(0, 64):
    draw.line((0, y, 63, y), fill='#cbc7b9' if y % 4 == 0 else '#f3f0e5')
draw.rectangle((64, 0, 127, 63), fill='#e4dfd0')
draw.rectangle((0, 64, 63, 95), fill='#f2eee1')
draw.text((7, 66), 'ARTESCO', fill='#404751')
draw.text((10, 81), 'OFICIO', fill='#606774')
draw.rectangle((64, 64, 127, 95), fill='#f2eee1')
draw.text((74, 72), 'COLLEGE', fill='#404751')
image.save(ASSETS / 'textures/item/stationery/details.png')

# Oficio proportions: 24 x 35 cm approximate; rigid thin boards, narrow folded
# hinge and internal flaps. Empty opening on the right, no band/rings/paper stack.
folder = [
    box([3.2, 1, 7.2], [12.8, 15, 7.45]),
    box([3.2, 1, 8.55], [12.8, 15, 8.8]),
    box([3.2, 1, 7.45], [3.55, 15, 8.55], uv=(8, 0, 12, 8)),
    box([3.55, 1.25, 7.45], [12.5, 3.4, 7.58], uv=(8, 8, 16, 16)),
    box([10.5, 3.4, 7.45], [12.5, 14.7, 7.58], uv=(8, 8, 16, 16)),
    box([3.55, 1, 8.8], [3.75, 15, 8.82], uv=(8, 0, 12, 8)),
    box([6.1, 2.5, 8.8], [10.6, 4.75, 8.84], 'details', (0, 8, 8, 12)),
]
# Sewn/glued college notebook: solid covers, darker bound spine, inset leaves.
notebook = [
    box([3, 1.5, 6.9], [13, 14.5, 7.15]),
    box([3, 1.5, 8.85], [13, 14.5, 9.1]),
    box([3.35, 1.75, 7.15], [12.75, 14.25, 8.85], 'details', (0, 0, 8, 8)),
    box([3, 1.5, 7.15], [3.5, 14.5, 8.85], uv=(8, 0, 12, 8)),
    box([3, 1.5, 9.1], [3.7, 14.5, 9.12], uv=(8, 0, 12, 8)),
    box([7.1, 3, 9.1], [11.6, 5.25, 9.14], 'details', (8, 8, 16, 12)),
]
for side in ['east', 'west']:
    notebook[2]['faces'][side]['rotation'] = 90

for kind, elements in [('folder', folder), ('notebook', notebook)]:
    write(ASSETS / f'models/item/{kind}.json', {
        'textures': {'cover': 'lsmmod:item/stationery/cover_blue',
                     'details': 'lsmmod:item/stationery/details', 'particle': '#cover'},
        'elements': elements, 'display': DISPLAY,
    })
    for color in COLORS:
        item = f'{color}_{kind}'
        write(ASSETS / f'models/item/{item}.json', {
            'parent': f'lsmmod:item/{kind}',
            'textures': {'cover': f'lsmmod:item/stationery/cover_{color}'},
        })
        write(ASSETS / f'items/{item}.json', {
            'model': {'type': 'minecraft:model', 'model': f'lsmmod:item/{item}'},
        })

for lang in ['es_es', 'en_us']:
    path = ASSETS / f'lang/{lang}.json'
    data = json.loads(path.read_text(encoding='utf-8'))
    for color, (_, spanish, english) in COLORS.items():
        for kind in ['folder', 'notebook']:
            data[f'item.lsmmod.{color}_{kind}'] = (
                f'Folder oficio {spanish}' if kind == 'folder' else f'Cuaderno college {spanish}'
            ) if lang == 'es_es' else (
                f'{english} Oficio Folder' if kind == 'folder' else f'{english} College Notebook'
            )
    write(path, data)
print('Generated two shared models, 32 color variants, 17 reusable atlases and translations.')
