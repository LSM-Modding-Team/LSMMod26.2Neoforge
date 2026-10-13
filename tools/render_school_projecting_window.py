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



def layout_scene(rule,start=0,opened=False,one=None,paint=4,walls=True):
    import math
    from create_school_projecting_window_assets import layout_key,layout_parts
    result=[];width,height=rule['width']*16,rule['height']*16
    for y in range(int(height)//16):
        for x in range(math.ceil((start+width)/16)):
            key=layout_key(rule,start,x,y)
            if walls and (start==8 and x==0 or (start+width)%16==8 and x==math.ceil((start+width)/16)-1):
                side='left' if x==0 and start==8 else 'right'
                for face in ('north','south','east','west','up','down'):
                    add(result,load(f'lsmmod:block/{PAINTABLE_ID}/wall_{side}_{face}_{paint}'),(x*16,y*16,0))
            for index,part in enumerate(layout_parts(rule,start,x,y)):
                pose=int(opened or one==(part[-2],part[-1]))
                for layer in ('solid','translucent'):
                    path=A/f'models/block/{ID}/layouts/{key}_part_{index}_{layer}_{pose}.json'
                    if path.exists():add(result,[json.loads(path.read_text())],(x*16,y*16,0))
    return result


def review_layouts():
    from create_school_projecting_window_assets import layout_rules
    rules=layout_rules();canvas=Image.new('RGB',(1800,1500))
    for index,rule in enumerate(rules):
        start=8 if index%2==0 else 0
        for opened in (False,True):
            column=index%3;row=index//3*2+int(opened)
            geometry=layout_scene(rule,start,opened=opened)
            image=render(geometry,[rule['width']*8+start,rule['height']*8,18],min(460/(rule['width']*16+16),250/(rule['height']*16+16)),size=(600,375),azimuth=-65 if not opened else 115,elevation=12)
            canvas.paste(image,(column*600,row*375))
            label=f"{rule['width']}x{rule['height']} bloques -> {rule['columns']}x{rule['rows']} paneles / {'abierto, dorso' if opened else 'cerrado, frente'}"
            ImageDraw.Draw(canvas).text((column*600+10,row*375+10),label,fill=(25,25,25))
    canvas.save(ROOT/'previews/school_projecting_window_layouts_review.png')

def main():
    closed = scene({})
    # A 3x3-block opening contains exactly four 24x24-pixel panes.
    independent = scene({(0, 0): 1})
    all_edges = scene({(0, 0): 1, (1, 0): 1, (0, 1): 1, (1, 1): 1})
    item = []; add(item, load('lsmmod:item/'+ID))
    canvas = Image.new('RGB', (1680, 640))
    cases = [('1.5 x 1.5 / inicio desde el primer bloque', closed, [24, 24, 16], 8, -80),
             ('Abre alejandose del jugador', independent, [24, 24, 18], 8, -65),
             ('Todos los cuadrados se elevan', all_edges, [24, 24, 18], 8, -65),
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
    cases=(('left',0,0,'Panel compartido / cerrado'),('left',1,4,'Panel compartido / abierto'),
           ('right',1,2,'Mitad derecha / panel compartido'),('item',0,0,'Item / mitad fija'))
    for index,(side,pose,color,label) in enumerate(cases):
        geometry=[]
        if side=='item':add(geometry,load('lsmmod:item/'+PAINTABLE_ID))
        else:
            for y in range(3):
                for x in range(2):
                    mixed=x==(0 if side=='left' else 1)
                    phase=((x+1)%3 if side=='left' else x%3)+3*(y%3)
                    if mixed:
                        root=json.loads((A/f'models/block/{PAINTABLE_ID}/phase_{phase}_{side}.json').read_text())
                        for group in root['models']:
                            selected=next((e for e in group if e.get('condition',{}).get('paint')==color or e.get('condition',{}).get('pose')==pose),group[-1])
                            add(geometry,load(selected['model']),(16*x,16*y,0))
                    else:
                        for part_index,part in enumerate(parts(phase)):
                            for layer in ('solid','translucent'):
                                path=A/f'models/block/{ID}/phase_{phase}_part_{part_index}_{layer}_{pose}.json'
                                if path.exists():add(geometry,[json.loads(path.read_text())],(16*x,16*y,0))
        half.paste(render(geometry,[8,8,14] if side=='item' else [16,24,18],25 if side=='item' else 9,size=(420,640),azimuth=-65,elevation=18),(420*index,0))
        ImageDraw.Draw(half).text((420*index+10,15),label,fill=(25,25,25))
    half.save(ROOT/'previews/school_paintable_projecting_window_review.png')
    review_layouts()


if __name__ == '__main__':
    import argparse
    from pathlib import Path
    import render_texture_review as renderer
    parser=argparse.ArgumentParser()
    parser.add_argument('--vanilla-dir',type=Path,default=renderer.VANILLA)
    renderer.VANILLA=parser.parse_args().vanilla_dir
    main()
