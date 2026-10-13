"""Inspect actual pane fragments, rigid 45-degree hinges, shared seams and the inventory item."""
import json
from PIL import Image, ImageDraw
from create_school_projecting_window_assets import A, ROOT, ID, PAINTABLE_ID, parts
from render_texture_review import add, render, load


def scene(poses, width=3, height=3):
    result = []
    for y in range(height):
        for x in range(width):
            phase = x%3+3*(y%3)
            for index, part in enumerate(parts(phase)):
                *_, ox, oy = part
                tile = ((x*16+ox)//24, (y*16+oy)//24)
                pose = poses.get(tile, 0)
                for layer in ('solid', 'translucent'):
                    path = A/f'models/block/{ID}/phase_{phase}_part_{index}_{layer}_{pose}.json'
                    if path.exists(): add(result, [json.loads(path.read_text())], (16*x, 16*y, 0))
    return result


def main():
    closed = scene({})
    # A 3x3-block opening contains exactly four 24x24-pixel panes.
    independent = scene({(0, 0): 3})
    all_edges = scene({(0, 0): 2, (1, 0): 4, (0, 1): 3, (1, 1): 1})
    item = []; add(item, load('lsmmod:item/'+ID))
    canvas = Image.new('RGB', (1680, 640))
    cases = [('1.5 x 1.5 / inicio desde el primer bloque', closed, [24, 24, 16], 8, -80),
             ('Abre alejandose del jugador', independent, [24, 24, 18], 8, -65),
             ('Bisagras en los cuatro bordes', all_edges, [24, 24, 18], 8, -65),
             ('Item / marco negro y vidrio tintado', item, [8, 8, 8], 25, -65)]
    for index, (label, elements, center, scale, azimuth) in enumerate(cases):
        canvas.paste(render(elements, center, scale, size=(420, 640), azimuth=azimuth, elevation=12), (420*index, 0))
        ImageDraw.Draw(canvas).text((420*index+10, 15), label, fill=(25, 25, 25))
    canvas.save(ROOT/'previews/school_projecting_window_review.png')
    reverse = Image.new('RGB', (840, 640))
    for index, (elements, label) in enumerate(((closed, 'Dorso cerrado'), (all_edges, 'Dorso abierto / bisagras independientes'))):
        reverse.paste(render(elements, [24, 24, 18], 8, size=(420, 640), azimuth=115, elevation=12), (420*index, 0))
        ImageDraw.Draw(reverse).text((420*index+10, 15), label, fill=(25, 25, 25))
    reverse.save(ROOT/'previews/school_projecting_window_reverse_review.png')
    half = Image.new('RGB',(1680,640))
    for index,(side,pose,color,label) in enumerate((('left',0,0,'Mitad pintable / ventana al fondo'),
                  ('left',3,0,'Solo la ventana se mueve'),('left',0,4,'Pintura blanca / cristal intacto'),
                  ('right',4,2,'Mitad derecha pintada / abierta'))):
        definition=json.loads((A/f'models/block/{PAINTABLE_ID}/{side}_{pose}.json').read_text())
        components=[]
        for series in definition['models']:
            if isinstance(series[0],str):components.extend(json.loads((A/f'models/{name.split(":")[1]}.json').read_text()) for name in series)
            else:
                selected=next(e for e in series if 'condition' not in e or e['condition']['paint']==color)
                components.append(json.loads((A/f'models/{selected["model"].split(":")[1]}.json').read_text()))
        geometry=[];add(geometry,components)
        half.paste(render(geometry,[8,8,14],25,size=(420,640),azimuth=-65,elevation=18),(420*index,0))
        ImageDraw.Draw(half).text((420*index+10,15),label,fill=(25,25,25))
    half.save(ROOT/'previews/school_paintable_projecting_window_review.png')


if __name__ == '__main__':
    import argparse
    from pathlib import Path
    import render_texture_review as renderer
    parser=argparse.ArgumentParser()
    parser.add_argument('--vanilla-dir',type=Path,default=renderer.VANILLA)
    renderer.VANILLA=parser.parse_args().vanilla_dir
    main()
