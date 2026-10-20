"""Review both speaker cells, upper horn, real item and all four facing variants."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, output_path
import json
from PIL import Image, ImageDraw
from resource_json import preview_path
import render_texture_review as renderer


def main():
    scene = []
    for half, y in [('lower', 0), ('upper', 16)]:
        renderer.add(scene, renderer.block('general_speaker', {'facing': 'north', 'half': half}), (0, y, 0))
    definition = json.loads(module_read_text(renderer.A / 'items/general_speaker.json'))['model']
    item = []
    renderer.add(item, renderer.load(definition['model']))
    views = [('Frente', scene, -90), ('Frente y lateral', scene, -60),
             ('Dorso y lateral', scene, 120), ('Item / modelo real', item, -60)]
    canvas = Image.new('RGB', (1680, 610), (210, 210, 210))
    draw = ImageDraw.Draw(canvas)
    for index, (label, geometry, angle) in enumerate(views):
        view = renderer.render(geometry, (8,16,8), 14, size=(420,560), azimuth=angle, elevation=12)
        canvas.paste(view, (index*420,25))
        draw.text((index*420+10,8), label, fill='black')
    draw.text((12,590), 'Render externo de dos celdas; no es una captura de Minecraft.', fill='black')
    target = preview_path('general_speaker_review.png'); canvas.save(output_path(target)); print(target)
    # Rotate the selected geometry as blockstates do to check all physical fronts.
    rotations = Image.new('RGB', (1440,560), (210,210,210)); draw = ImageDraw.Draw(rotations)
    variants = json.loads(module_read_text(renderer.A/'blockstates/general_speaker.json'))['variants']
    for index, facing in enumerate(('north','east','south','west')):
        parts=[]
        for half,y in [('lower',0),('upper',16)]:
            variant=variants[f'facing={facing},half={half}']
            chunk=[]; renderer.add(chunk,renderer.load(variant['model']),(0,y,0))
            for e in chunk:
                e['rotation']={'origin':[8,16,8],'axis':'y','angle':-variant['y']}
            parts.extend(chunk)
        view=renderer.render(parts,(8,16,8),13,size=(360,520),azimuth=-60,elevation=12)
        rotations.paste(view,(index*360,25));draw.text((index*360+12,8),facing,fill='black')
    target=preview_path('general_speaker_rotations.png');rotations.save(output_path(target));print(target)


if __name__ == '__main__':
    main()
