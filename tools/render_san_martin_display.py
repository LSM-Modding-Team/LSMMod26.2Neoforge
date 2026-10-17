"""Review actual display cell models and item, with their final UVs and alpha layers."""
from resource_modules import output_path
from resource_json import preview_path
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
    canvas.save(output_path(preview_path('san_martin_display_review.png')))
    # Inspect the actual cell surfaces behind the case, including the robe's back.
    sculpture = [e for e in scene if all(s['texture'] not in (
        'lsmmod:block/school_porcelain', 'lsmmod:block/san_martin_de_porres_glass')
        for s in e['faces'].values())]
    canvas = Image.new('RGB', (1680, 600))
    for i, (label, azimuth) in enumerate((('Estatua / frente', -90),
                         ('Frente y lateral', -55), ('Lateral opuesto', -135), ('Dorso', 100))):
        canvas.paste(render(sculpture, [16, 20, 8], 15, size=(420, 600),
                           azimuth=azimuth, elevation=8), (i*420, 0))
        ImageDraw.Draw(canvas).text((i*420+15, 15), label, fill=(25, 25, 25))
    ImageDraw.Draw(canvas).text((15, 580), 'Render externo de las superficies finales; vitrina oculta para revisar el detalle.', fill=(25, 25, 25))
    canvas.save(output_path(preview_path('san_martin_sculpture_review.png')))


if __name__ == '__main__': main()
