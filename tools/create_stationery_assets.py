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
    'purple': ('#7842a2', 'morado', 'Purple'), 'blue': ('#143eae', 'azul', 'Blue'),
    'brown': ('#805333', 'marrón', 'Brown'), 'green': ('#3d7544', 'verde', 'Green'),
    'red': ('#cc252b', 'rojo', 'Red'), 'black': ('#272b32', 'negro', 'Black'),
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

# Folder grain and pressed rim; notebook satin cardstock, separate UV regions.
import random
for name, (hexcolor, _, _) in COLORS.items():
    base = tuple(bytes.fromhex(hexcolor[1:]))
    image = Image.new('RGB', (128, 128))
    rng = random.Random(72)
    for y in range(128):
        for x in range(128):
            if x < 64:
                factor = 1.0
                noise = rng.randrange(-7, 8)
            elif y < 64:
                factor = .72
                noise = rng.randrange(-3, 4)
            else:
                # Broad subtle sheen and shaded fore-edge, readable in Minecraft.
                t = (x - 64) / 63
                factor = 1.04 - .12 * t + .10 * max(0, 1 - abs(t - .18) * 6)
                noise = rng.randrange(-2, 3)
            image.putpixel((x, y), tuple(max(0, min(255, round(v * factor + noise))) for v in base))
    d = ImageDraw.Draw(image)
    dark = tuple(round(v * .7) for v in base)
    light = tuple(min(255, round(v * 1.18 + 8)) for v in base)
    d.rounded_rectangle((1, 1, 62, 94), radius=2, outline=dark, width=1)
    d.rounded_rectangle((3, 3, 60, 92), radius=2, outline=light, width=1)
    d.line((5, 4, 5, 91), fill=dark)
    d.line((64, 65, 64, 126), fill=light)
    d.line((65, 65, 65, 126), fill=dark)
    path = ASSETS / f'textures/item/stationery/cover_{name}.png'
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)

image = Image.new('RGBA', (128, 128), '#edeadf')
draw = ImageDraw.Draw(image)
# Horizontal leaf edges: use UV strips on all three exposed notebook edges.
for y in range(0, 64):
    draw.line((0, y, 63, y), fill='#cbc7b9' if y % 4 == 0 else '#f3f0e5')
draw.rectangle((64, 0, 127, 63), fill='#e4dfd0')
# Small school identification sticker inspired by the supplied reference.
# Blank writing lines and abstract brand marks are pixels, without literal names.
draw.rectangle((0, 64, 63, 95), fill='#494675')
draw.rounded_rectangle((2, 66, 61, 93), radius=5, fill='#f4f3ee')
for y in [73, 80, 87]:
    draw.line((5, y - 1, 11, y - 1), fill='#949ca4')
    for x in range(14, 58 if y < 87 else 40, 2):
        draw.point((x, y), fill='#adb8c4')
draw.polygon([(41, 85), (56, 85), (53, 87), (40, 87)], fill='#245184')
draw.polygon([(39, 89), (56, 88), (54, 90), (38, 91)], fill='#cb4051')
# Notebook reference layout: abstract printed brand motifs, no literal words.
draw.rectangle((64, 0, 127, 63), fill=(0, 0, 0, 0))
# Upper-right gold badge with dark keyline and decorative rounded strokes.
draw.rounded_rectangle((66, 3, 125, 28), radius=7, fill='#232c40')
draw.rounded_rectangle((68, 5, 123, 26), radius=6, fill='#f1d15c')
for x in [74, 85, 96, 107]:
    draw.line([(x, 18), (x+2, 10), (x+6, 10), (x+8, 15), (x+6, 21), (x+2, 21), (x, 18)], fill='#5f4f22', width=2)
draw.line((71, 29, 121, 29), fill='#f7e7a1', width=1)
# Upper-left flowing white outlined emblem, matching the reference silhouette.
wave=[(67,58),(69,39),(73,40),(78,52),(81,43),(85,45),(89,54),(94,47),(100,49),(106,54),(119,55)]
draw.line(wave, fill='#233d5a', width=7)
draw.line(wave, fill='#f6f6e7', width=5)
draw.line(wave, fill='#7bbfca', width=2)
# Lower-right notebook writing panel, separate from the folder sticker.
draw.rectangle((64, 64, 127, 95), fill='#243453')
draw.rectangle((66, 66, 125, 93), fill='#f2f2e6')
for y in [72, 79, 86]:
    draw.line((69, y-1, 75, y-1), fill='#919990')
    draw.line((77, y, 121, y), fill='#b0b7ad')
image.save(ASSETS / 'textures/item/stationery/details.png')

# Reference silhouette: width/height 10/14; compact closed rigid folder.
folder = [
    box([3, 1, 7.68], [13, 15, 7.84]),
    box([3, 1, 8.16], [13, 15, 8.32]),
    box([3, 1, 7.84], [3.3, 15, 8.16], uv=(8, 0, 12, 8)),
    box([3.3, 1.15, 7.84], [12.8, 3.3, 7.9], uv=(8, 8, 16, 16)),
    box([10.8, 3.3, 7.84], [12.8, 14.85, 7.9], uv=(8, 8, 16, 16)),
    box([8.4, 2.25, 8.32], [12.0, 4.05, 8.325], 'details', (0, 8, 8, 12)),
]
# Soft laminated covers, substantial ruled-page block and narrow glued binding.
# Front UV uses the satin card region, without any model-name lettering.
notebook = [
    box([3.18, 1.53, 7.22], [12.82, 14.47, 7.31], uv=(8, 8, 16, 16)),
    box([3.18, 1.53, 8.69], [12.82, 14.47, 8.78], uv=(8, 8, 16, 16)),
    box([3.18, 1.53, 7.31], [12.82, 14.47, 8.69], 'details', (0, 0, 8, 8)),
    box([3.18, 1.53, 7.31], [3.38, 14.47, 8.69], uv=(8, 0, 12, 8)),
    box([3.18, 1.53, 8.78], [3.5, 14.47, 8.79], uv=(8, 0, 8.5, 8)),
]
# Adjacent binding and paper volumes: the colored spine occupies the full
# left strip, including its top/bottom ends, without overlapping the leaves.
notebook[2]['from'][0] = 3.5
notebook[3]['to'][0] = 3.5
notebook[3]['faces']['east'] = {'texture': '#cover', 'uv': [8, 0, 12, 8]}
notebook[4]['faces'] = {'south': notebook[4]['faces']['south']}

# Flat printed decals share the neutral detail atlas across every cover color.
for start, end, uv in [
    ([3.8, 13.43, 8.78], [5.7, 14.1, 8.785], (8, 4, 16, 8)),
    ([10.35, 13.38, 8.78], [12.3, 14.04, 8.785], (8, 0, 16, 4)),
    ([8.8, 2.25, 8.78], [12.22, 3.62, 8.785], (8, 8, 16, 12)),
]:
    decal = box(start, end, 'details', uv)
    decal['faces'] = {'south': decal['faces']['south']}
    notebook.append(decal)

for side in ['east']:
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
