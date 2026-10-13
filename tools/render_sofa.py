"""Review modular sofa corners, connected assemblies, four orientations and the real item."""
import json

from PIL import Image, ImageDraw
from create_kiosk_entrance_assets import rotated
from render_block_review import framing
from render_texture_review import A, ROOT, add, load, render


def module(scene, facing, shape='straight', x=0, z=0):
    variants = json.loads((A/'blockstates/sofa.json').read_text())['variants']
    apply = variants[f'facing={facing},shape={shape}']
    for model in load(apply['model']):
        # Existing helper rotates by 270 degrees, including actual face UV orientation.
        for _ in range((-apply.get('y', 0)//90) % 4):
            model = rotated(model)
        add(scene, [model], (x*16, 0, z*16))


def panel(canvas, index, label, scene, columns=3, azimuth=60, elevation=22, size=(500, 380)):
    origin, scale = framing(scene, size)
    x, y = index % columns*size[0], index // columns*size[1]
    canvas.paste(render(scene, origin, scale*1.1, size=size, azimuth=azimuth, elevation=elevation), (x, y))
    ImageDraw.Draw(canvas).text((x+12, y+10), label, fill=(30, 30, 30))


def main():
    canvas = Image.new('RGB', (1500, 1140), (194, 194, 194))
    row = []
    for x in range(3):
        module(row, 'south', x=x)
    panel(canvas, 0, 'Tres modulos conectados / frente', row)
    panel(canvas, 1, 'Tres modulos / dorso', row, azimuth=-120)
    item_definition = json.loads((A/'items/sofa.json').read_text())['model']
    item = []
    add(item, load(item_definition['model']))
    panel(canvas, 2, 'Item / modelo y materiales reales', item)
    for index, shape in enumerate(('inner_left', 'inner_right', 'outer_left', 'outer_right'), 3):
        scene = []
        module(scene, 'south', shape)
        panel(canvas, index, shape, scene)
    inside = []
    module(inside, 'south', 'inner_right')
    for offset in (1, 2):
        module(inside, 'south', x=offset)
        module(inside, 'east', z=offset)
    panel(canvas, 7, 'Conexion en L / esquina interior', inside)
    outside = []
    module(outside, 'south', 'outer_right')
    for offset in (-1, -2):
        module(outside, 'south', x=offset)
        module(outside, 'east', z=offset)
    panel(canvas, 8, 'Conexion en L / esquina exterior', outside, azimuth=135)
    target = ROOT/'previews/sofa_connections_review.png'
    canvas.save(target)
    print(target)
    # Use the actual front of the SOUTH-native chair rather than the generic NORTH labels.
    canvas.crop((0, 0, 1500, 380)).save(ROOT/'previews/sofa_review.png')

    orientations = Image.new('RGB', (1500, 5*320), (194, 194, 194))
    for row, shape in enumerate(('straight', 'inner_left', 'inner_right', 'outer_left', 'outer_right')):
        for column, facing in enumerate(('south', 'west', 'north', 'east')):
            scene = []
            module(scene, facing, shape)
            panel(orientations, row*4+column, shape+' / '+facing, scene,
                  columns=4, size=(375, 320), azimuth=60)
    target = ROOT/'previews/sofa_orientations_review.png'
    orientations.save(target)
    print(target)


if __name__ == '__main__':
    main()
