"""Generate the red fabric-covered portable speaker item from the reference."""
from resource_modules import module_write_text, output_path
import json
import math
from pathlib import Path

from PIL import Image
from resource_json import resource_json

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'src/main/resources/assets/lsmmod'


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    module_write_text(path, resource_json(path, value) + '\n')


def box(lo, hi, material):
    element = {'from': lo, 'to': hi, 'faces': {
        face: {'texture': '#' + material, 'uv': [0, 0, 16, 16]}
        for face in ('north', 'south', 'east', 'west', 'up', 'down')}}
    if material == 'mesh':
        # Continuous UVs preserve the weave density across cylinder strips.
        for face in ('north', 'south'):
            element['faces'][face]['uv'] = [lo[0], 16 - hi[1], hi[0], 16 - lo[1]]
        for face in ('east', 'west'):
            element['faces'][face]['uv'] = [lo[2], 16 - hi[1], hi[2], 16 - lo[1]]
        for face in ('up', 'down'):
            element['faces'][face]['uv'] = [lo[0], lo[2], hi[0], hi[2]]
    return element


def generate():
    # Small repeating weave, matte rubber and diagonal passive-radiator ribs.
    for material in ('mesh', 'rubber', 'recess', 'radiator', 'controls'):
        image = Image.new('RGB', (32, 32))
        for y in range(32):
            for x in range(32):
                if material == 'mesh':
                    shade = ((x + (y // 2) % 2) % 4, y % 4)
                    color = {(0, 0): (255, 78, 83), (1, 1): (240, 49, 57),
                             (2, 2): (151, 12, 27), (3, 3): (174, 17, 31)}.get(shade, (211, 30, 42))
                elif material == 'rubber':
                    delta = (x * 3 + y * 7) % 5 - 2
                    color = (225 + delta, 36 + delta, 45 + delta)
                elif material == 'recess':
                    color = (125, 10, 24)
                elif material == 'radiator':
                    color = (235, 48, 56) if (x + y) % 6 < 2 else (183, 23, 35)
                else:
                    color = (161, 17, 31)
                image.putpixel((x, y), color)
        path = ASSETS / f'textures/item/portable_speaker_{material}.png'
        path.parent.mkdir(parents=True, exist_ok=True)
        image.save(output_path(path))

    elements = []
    # Horizontal cylinder approximated with half-pixel circular cross-section.
    # Each end has a raised rubber ring around a recessed, ribbed diaphragm.
    for row in range(16):
        y0 = 4 + row * .5
        half = round(math.sqrt(16 - (y0 + .25 - 8) ** 2) * 2) / 2
        elements.append(box([2, y0, 8 - half], [14, y0 + .5, 8 + half], 'mesh'))
    for left in (True, False):
        x0, x1 = (1, 2) if left else (14, 15)
        back0, back1 = (1.8, 2) if left else (14, 14.2)
        diaphragm0, diaphragm1 = (1.65, 1.8) if left else (14.2, 14.35)
        for row in range(16):
            y0 = 4 + row * .5
            dy = y0 + .25 - 8
            outer = round(math.sqrt(16 - dy * dy) * 2) / 2
            inner = round(math.sqrt(max(0, 9 - dy * dy)) * 2) / 2
            if inner == 0:
                elements.append(box([x0, y0, 8 - outer], [x1, y0 + .5, 8 + outer], 'rubber'))
            else:
                for z0, z1 in ((8 - outer, 8 - inner), (8 + inner, 8 + outer)):
                    elements.append(box([x0, y0, z0], [x1, y0 + .5, z1], 'rubber'))
                elements.append(box([back0, y0, 8 - inner], [back1, y0 + .5, 8 + inner], 'recess'))
                radius = round(math.sqrt(max(0, 6.25 - dy * dy)) * 2) / 2
                if radius:
                    elements.append(box([diaphragm0, y0, 8 - radius],
                                        [diaphragm1, y0 + .5, 8 + radius], 'radiator'))
    # Subtle raised rubber controls along the top: minus, power and plus.
    elements.extend([
        box([5, 12, 7.7], [6, 12.12, 8.0], 'controls'),
        box([7.7, 12, 7.7], [8.3, 12.12, 8.3], 'controls'),
        box([10, 12, 7.7], [11, 12.12, 8.0], 'controls'),
        box([10.35, 12, 7.35], [10.65, 12.12, 8.35], 'controls')])
    display = {
        'gui': {'rotation': [20, -30, 0], 'scale': [.95, .95, .95]},
        'ground': {'translation': [0, 3, 0], 'scale': [.5, .5, .5]},
        'fixed': {'rotation': [0, 180, 0], 'scale': [.8, .8, .8]},
        'head': {'translation': [0, 8, 0], 'scale': [.7, .7, .7]},
        'thirdperson_righthand': {'rotation': [0, 0, -15], 'translation': [0, 2, 0], 'scale': [.65, .65, .65]},
        'thirdperson_lefthand': {'rotation': [0, 0, 15], 'translation': [0, 2, 0], 'scale': [.65, .65, .65]},
        'firstperson_righthand': {'rotation': [0, -25, -10], 'translation': [1, 2, 0], 'scale': [.75, .75, .75]},
        'firstperson_lefthand': {'rotation': [0, 25, 10], 'translation': [1, 2, 0], 'scale': [.75, .75, .75]}}
    textures = {name: f'lsmmod:item/portable_speaker_{name}'
                for name in ('mesh', 'rubber', 'recess', 'radiator', 'controls')}
    textures['particle'] = textures['mesh']
    write(ASSETS / 'models/item/portable_speaker.json',
          {'credit': 'LSM Mod - red portable speaker reference', 'textures': textures,
           'elements': elements, 'display': display})
    write(ASSETS / 'items/portable_speaker.json',
          {'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/portable_speaker'}})


if __name__ == '__main__':
    generate()
