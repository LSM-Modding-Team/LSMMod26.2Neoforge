"""Inspect the final refrigerator block cells and layered item using their actual textures."""
from resource_modules import output_path
from resource_json import preview_path
from PIL import Image, ImageDraw
from render_texture_review import ROOT, add, block, load, render


def main():
    scene = []
    for row in range(3):
        for column in range(2):
            add(scene, block('school_refrigerator', {'facing': 'north', 'column': column, 'row': row}),
                (16*column, 16*row, 0))
    item = []
    add(item, load('lsmmod:item/school_refrigerator'))
    canvas = Image.new('RGB', (1680, 620), (194, 194, 194))
    for index, (label, azimuth) in enumerate((('Frente', -90), ('Frente y lateral', -60), ('Dorso y lateral', 120))):
        view = render(scene, [16, 24, 8], 10, size=(420, 620), azimuth=azimuth, elevation=12)
        canvas.paste(view, (420*index, 0))
        ImageDraw.Draw(canvas).text((420*index+18, 18), label, fill=(30, 30, 30))
    canvas.paste(render(item, [8, 8, 8], 29, size=(420, 620), azimuth=-60, elevation=12), (1260, 0))
    ImageDraw.Draw(canvas).text((1278, 18), 'Item / capas solid y translucent', fill=(30, 30, 30))
    canvas.save(output_path(preview_path('school_refrigerator_review.png')))


if __name__ == '__main__':
    main()
