"""Inspect the real two-half bucket models, materials and inventory item offline."""
from PIL import Image, ImageDraw
from render_texture_review import ROOT, add, block, load, render


def main():
    scene = []
    for row, half in enumerate(('lower', 'upper')):
        add(scene, block('nido_storage_bucket', {'facing': 'north', 'half': half}), (0, 16*row, 0))
    item = []; add(item, load('lsmmod:item/nido_storage_bucket'))
    canvas = Image.new('RGB', (1680, 620))
    for index, (label, azimuth) in enumerate((('Frente / hitbox 1 x 1.5', -90),
                               ('Tapa blanca y asas', -55), ('Dorso y lateral', 120))):
        canvas.paste(render(scene, [8, 12, 8], 20, size=(420, 620), azimuth=azimuth, elevation=20), (420*index, 0))
        ImageDraw.Draw(canvas).text((420*index+15, 15), label, fill=(25, 25, 25))
    canvas.paste(render(item, [8, 8, 8], 29, size=(420, 620), azimuth=-55, elevation=20), (1260, 0))
    ImageDraw.Draw(canvas).text((1275, 15), 'nido_storage_bucket / item', fill=(25, 25, 25))
    canvas.save(ROOT/'previews/nido_storage_bucket_review.png')


if __name__ == '__main__': main()
