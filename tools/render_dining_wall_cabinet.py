"""Inspect the final cabinet block cells and item using their actual textures."""
from PIL import Image, ImageDraw
from render_texture_review import ROOT, add, block, load, render


def main():
    scene = []
    for row in range(2):
        for column in range(1):
            add(scene, block('dining_wall_cabinet', {'facing': 'north', 'column': column, 'half': 'upper' if row else 'lower'}),
                (16*column, 16*row, 0))
    item = []
    add(item, load('lsmmod:item/dining_wall_cabinet'))
    canvas = Image.new('RGB', (1680, 620), (194, 194, 194))
    for index, (label, azimuth) in enumerate((('Frente', -90), ('Frente y lateral', -60), ('Dorso y lateral', 120))):
        view = render(scene, [8, 16, 10], 13, size=(420, 620), azimuth=azimuth, elevation=12)
        canvas.paste(view, (420*index, 0))
        ImageDraw.Draw(canvas).text((420*index+18, 18), label, fill=(30, 30, 30))
    canvas.paste(render(item, [8, 8, 8], 29, size=(420, 620), azimuth=-60, elevation=12), (1260, 0))
    ImageDraw.Draw(canvas).text((1278, 18), 'Item', fill=(30, 30, 30))
    canvas.save(ROOT/'previews/dining_wall_cabinet_review.png')


if __name__ == '__main__':
    main()
