"""Review actual generated-item textures and their alpha-extruded silhouette offline.

Minecraft's item/generated parent extrudes layer0 by one model unit. Resolve the
real item definition and model, then expose its front, back and alpha boundary
through the shared software renderer. This is not a Minecraft screenshot.
"""
import json
from PIL import Image, ImageDraw
from create_music_disc_assets import ASSETS, DESIGNS
from resource_json import preview_path
import render_texture_review as renderer


def generated_item(ident):
    definition = json.loads((ASSETS / f'items/{ident}.json').read_text())['model']
    assert definition['type'] == 'minecraft:model'
    model = json.loads((ASSETS / ('models/' + definition['model'].split(':')[1] + '.json')).read_text())
    assert model['parent'] == 'minecraft:item/generated'
    texture = model['textures']['layer0']
    image = Image.open(ASSETS / ('textures/' + texture.split(':')[1] + '.png')).convert('RGBA')
    assert image.size == (16, 16)
    scene = []
    def opaque(x, y):
        return 0 <= x < 16 and 0 <= y < 16 and image.getpixel((x, y))[3] != 0
    for y in range(16):
        for x in range(16):
            if not opaque(x, y):
                continue
            # Per-texel front/back quads give the same nearest-neighbor sampling
            # as a full generated face; side quads only exist at alpha boundaries.
            faces = {'north': {'texture': texture, 'uv': [x + 1, y, x, y + 1]},
                     'south': {'texture': texture, 'uv': [x, y, x + 1, y + 1]}}
            for face, neighbor in [('west', (x-1, y)), ('east', (x+1, y)),
                                   ('up', (x, y-1)), ('down', (x, y+1))]:
                if not opaque(*neighbor):
                    faces[face] = {'texture': texture, 'uv': [x+.5, y+.5, x+.5, y+.5]}
            scene.append({'from': [x, 15-y, 7.5], 'to': [x+1, 16-y, 8.5],
                          'faces': faces, 'render_type': 'minecraft:cutout'})
    return scene, image


def main():
    ids = [*DESIGNS, 'lsm_anthem', 'lsm_march']
    cell_w, cell_h = 240, 160
    sheet = Image.new('RGB', (cell_w*4, cell_h*len(ids)+60), (225, 225, 225))
    draw = ImageDraw.Draw(sheet)
    labels = ['Inventario / layer0', 'Frente', 'Frente y borde', 'Dorso y borde']
    for column, label in enumerate(labels):
        draw.text((column*cell_w+10, 8), label, fill='black')
    for row, ident in enumerate(ids):
        scene, image = generated_item(ident)
        top = 30 + row*cell_h
        # GUI renders item/generated as the flat sprite, retaining its alpha.
        background = Image.new('RGBA', (cell_w, cell_h-20), (185, 185, 185, 255))
        enlarged = image.resize((144, 144), Image.Resampling.NEAREST)
        background.alpha_composite(enlarged, ((cell_w-144)//2, -2))
        sheet.paste(background.convert('RGB'), (0, top+20))
        for column, angle in enumerate((90, 55, -125), start=1):
            view = renderer.render(scene, (8, 8, 8), 11, size=(cell_w, cell_h-20),
                                   azimuth=angle, elevation=0 if column==1 else 12,
                                   minecraft_orientation=True)
            sheet.paste(view, (column*cell_w, top+20))
        draw.text((10, top+3), ident, fill='black')
    draw.text((10, sheet.height-20), 'Render externo del item/generated, UV y alfa; no es una captura de Minecraft.', fill='black')
    target = preview_path('music_discs_review.png')
    sheet.save(target)
    print(target)


if __name__ == '__main__':
    main()
