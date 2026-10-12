"""Review actual display cell models and item, with their final UVs and alpha layers."""
from PIL import Image, ImageDraw
from render_texture_review import ROOT, add, block, load, render


def main():
    scene = []
    for row in range(3):
        for column in range(2):
            add(scene, block('san_martin_de_porres', {'facing': 'north', 'column': column,
                      'row': row, 'expanded': 'true'}), (16*column, 16*row, 0))
    item = []; add(item, load('lsmmod:item/san_martin_de_porres'))
    canvas = Image.new('RGB', (1680, 600))
    for i, (label, azimuth) in enumerate((('Frente / estatua 1 x 2', -90),
                         ('Caja blanca 2 x 2.5 x 1', -65), ('Dorso y lateral', 120))):
        canvas.paste(render(scene, [16, 20, 8], 12, size=(420, 600), azimuth=azimuth, elevation=6), (i*420, 0))
        ImageDraw.Draw(canvas).text((i*420+15, 15), label, fill=(25, 25, 25))
    canvas.paste(render(item, [8, 8, 8], 29, size=(420, 600), azimuth=-70, elevation=8), (1260, 0))
    ImageDraw.Draw(canvas).text((1275, 15), 'Item / cristal transparente', fill=(25, 25, 25))
    canvas.save(ROOT/'previews/san_martin_display_review.png')


if __name__ == '__main__': main()
