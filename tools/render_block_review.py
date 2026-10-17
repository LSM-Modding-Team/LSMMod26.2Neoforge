"""Review native, unrotated block cells and the actual item definition. Requires Pillow/numpy.

python tools/render_block_review.py school_microwave
python tools/render_block_review.py school_refrigerator --grid 2 3 1
For non-column/row/depth assemblies, OBJ, rotated parts or dynamic states use the family renderer.
"""
from resource_modules import module_read_text, output_path
import argparse
import itertools
import json
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw
import render_texture_review as renderer


def framing(scene, size=(420, 620)):
    points = np.array([e[key] for e in scene for key in ('from', 'to')])
    low, high = points.min(axis=0), points.max(axis=0)
    # Fit both the frontal and oblique views, including geometry outside its cell.
    width = high[0]-low[0]+high[2]-low[2]
    height = high[1]-low[1]+.25*width
    return (low+high)/2, min((size[0]-60)/max(width, 1), (size[1]-100)/max(height, 1))


def validate(models):
    if any(m.get('loader') not in (None, 'fusion:model') for m in models):
        raise ValueError('Custom/OBJ loaders require the family renderer.')
    if not any(m.get('elements') for m in models):
        raise ValueError('No native elements selected; use the family renderer for OBJ/dynamic models.')
    if any(e.get('rotation') for m in models for e in m.get('elements', [])):
        raise ValueError('Rotated elements require the family renderer; this tool handles axis-aligned models.')


def review(ident, grid, state, output):
    scene = []
    for column, row, depth in itertools.product(*(range(n) for n in grid)):
        cell = dict(state, column=str(column), row=str(row), depth=str(depth))
        try:
            models = renderer.block(ident, cell)
        except AssertionError as error:
            raise ValueError('Select the base orientation with x=0,y=0; otherwise use the family renderer.') from error
        validate(models)
        renderer.add(scene, models, (16*column, 16*row, 16*depth))
    definition = json.loads(module_read_text(renderer.A/f'items/{ident}.json'))['model']
    if definition['type'] != 'minecraft:model':
        raise ValueError('Dynamic item definitions require the family renderer.')
    models = renderer.load(definition['model'])
    validate(models)
    item = []
    renderer.add(item, models)
    center, scale = framing(scene)
    item_center, item_scale = framing(item)
    canvas = Image.new('RGB', (1680, 650), (194, 194, 194))
    views = [('Frente', scene, center, scale, -90),
             ('Frente y lateral', scene, center, scale, -60),
             ('Dorso y lateral', scene, center, scale, 120),
             ('Item / definicion real', item, item_center, item_scale, -60)]
    draw = ImageDraw.Draw(canvas)
    for index, (label, geometry, origin, zoom, azimuth) in enumerate(views):
        canvas.paste(renderer.render(geometry, origin, zoom, size=(420, 620),
                                     azimuth=azimuth, elevation=12), (index*420, 25))
        draw.text((index*420+12, 8), label, fill=(30, 30, 30))
    draw.text((12, 635), ident+' / render externo; no es una captura de Minecraft.', fill=(30, 30, 30))
    output.parent.mkdir(parents=True, exist_ok=True)
    canvas.save(output_path(output))
    print(output)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('id', help='Block ID without lsmmod:')
    parser.add_argument('--grid', type=int, nargs=3, default=(1, 1, 1), metavar=('WIDTH', 'HEIGHT', 'DEPTH'))
    parser.add_argument('--state', nargs='*', default=['facing=north'], metavar='KEY=VALUE')
    parser.add_argument('--vanilla-dir', type=Path, default=renderer.VANILLA)
    parser.add_argument('--output', type=Path, help='Default: previews/<id>_review.png')
    args = parser.parse_args()
    if any(n < 1 for n in args.grid):
        parser.error('--grid dimensions must be positive')
    try:
        state = dict(pair.split('=', 1) for pair in args.state)
        renderer.VANILLA = args.vanilla_dir
        review(args.id, args.grid, state, args.output or renderer.ROOT/f'previews/{args.id}_review.png')
    except (ValueError, KeyError, FileNotFoundError, AttributeError, TypeError) as error:
        parser.error(str(error))


if __name__ == '__main__':
    main()
