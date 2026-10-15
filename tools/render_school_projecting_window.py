"""Inspect actual pane fragments, rigid 45-degree hinges, shared seams and the inventory item."""
from resource_json import preview_path
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
            mixed=(start==8 and x==0 or (start+width)%16==8 and x==math.ceil((start+width)/16)-1)
            side='left' if x==0 and start==8 else 'right'
            cell_parts=layout_parts(rule,start,x,y)
            def selected(condition):
                kind=condition['type']
                if kind=='lsmmod:painted_face':return condition['paint']==(paint(condition) if callable(paint) else paint)
                if kind=='lsmmod:projecting_window_layout':
                    if condition['key']!=key:return False
                    if 'part' not in condition:return True
                    part=cell_parts[condition['part']]
                    return condition['pose']==int(opened or one==(part[-2],part[-1]))
                return False
            # Resolve the actual root and every nested Fusion group, including the painted wall + glass composition.
            ident=f'lsmmod:block/{PAINTABLE_ID}/{side}' if mixed else 'lsmmod:block/'+ID
            models=load(ident,predicate=selected)
            if not walls:models=[model for model in models if 'glass' in model.get('textures',{})]
            add(result,models,(x*16,y*16,0))
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
    canvas.save(preview_path('school_projecting_window_layouts_review.png'))

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
    canvas.save(preview_path('school_projecting_window_review.png'))
    reverse = Image.new('RGB', (840, 640))
    for index, (elements, label) in enumerate(((closed, 'Dorso cerrado'), (all_edges, 'Dorso abierto / bisagras independientes'))):
        reverse.paste(render(elements, [24, 24, 18], 8, size=(420, 640), azimuth=115, elevation=12), (420*index, 0))
        ImageDraw.Draw(reverse).text((420*index+10, 15), label, fill=(25, 25, 25))
    reverse.save(preview_path('school_projecting_window_reverse_review.png'))
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
                        def selected(condition):
                            return condition.get('paint')==color if condition['type']=='lsmmod:painted_face' else condition.get('pose')==pose
                        add(geometry,load(f'lsmmod:block/{PAINTABLE_ID}/phase_{phase}_{side}',predicate=selected),(16*x,16*y,0))
                    else:
                        for part_index,part in enumerate(parts(phase)):
                            for layer in ('solid','translucent'):
                                path=A/f'models/block/{ID}/phase_{phase}_part_{part_index}_{layer}_{pose}.json'
                                if path.exists():add(geometry,[json.loads(path.read_text())],(16*x,16*y,0))
        half.paste(render(geometry,[8,8,14] if side=='item' else [16,24,18],25 if side=='item' else 9,size=(420,640),azimuth=-65,elevation=18),(420*index,0))
        ImageDraw.Draw(half).text((420*index+10,15),label,fill=(25,25,25))
    half.save(preview_path('school_paintable_projecting_window_review.png'))
    review_layouts()
    review_squares()

def review_squares():
    from create_school_projecting_window_assets import layout_rules
    rule=layout_rules()[1]
    checker=lambda condition:4 if (condition['quadrant']%2+condition['quadrant']//2)%2==0 else 9
    canvas=Image.new('RGB',(1800,900),(194,194,194));draw=ImageDraw.Draw(canvas)
    wall=[];add(wall,load(f'lsmmod:block/{PAINTABLE_ID}/wall_left',predicate=lambda c:c['paint']==checker(c)))
    cases=(('Izquierda / cuadrados 8x8 / cerrada',layout_scene(rule,8,paint=checker),-65),
           ('Izquierda / cuadrados 8x8 / abierta',layout_scene(rule,8,opened=True,paint=checker),-65),
           ('Dorso / cuatro cuadrados en el lateral',layout_scene(rule,8,paint=checker),115),
           ('Derecha / cuadrados 8x8',layout_scene(rule,0,paint=checker),-65),
           ('Corte interior / cuatro cuadrados 8x8',wall,-25))
    for index,(label,elements,angle) in enumerate(cases):
        from render_block_review import framing
        origin,zoom=framing(elements,size=(600,420));offset=(index%3*600,index//3*450+25)
        canvas.paste(render(elements,origin,zoom,size=(600,420),azimuth=angle,elevation=18,minecraft_orientation=True),offset)
        draw.text((offset[0]+10,offset[1]-18),label,fill=(25,25,25))
    item=[];add(item,load('lsmmod:item/'+PAINTABLE_ID))
    canvas.paste(render(item,[8,8,8],20,size=(600,420),azimuth=-65,elevation=18,minecraft_orientation=True),(1200,475))
    draw.text((1210,457),'Item / modelo real',fill=(25,25,25))
    canvas.save(preview_path('school_paintable_projecting_window_squares_review.png'))


def slab_scene(opened=False,facing='north',paint=0,kind='bottom',phase=None):
    from create_school_projecting_window_assets import SLAB_ID
    from create_custom_paintable_blocks import STEPS
    from create_kiosk_entrance_assets import rotated
    phase=(3 if kind=='bottom' else 0) if phase is None else phase
    mask={'bottom':0x33,'top':0xcc,'double':0xff}[kind]
    state=dict(facing=facing,open=str(opened).lower(),type=kind);elements=[]
    def predicate(condition):
        if condition['type']=='lsmmod:projecting_window_layout':return condition['key']=='fallback'
        if condition['type']=='lsmmod:projecting_window':
            return condition['phase']==phase and ('pose' not in condition or condition['pose']==int(opened))
        cube=condition['cube'];xyz=(cube&1,(cube>>1)&1,(cube>>2)&1)
        neighbor=tuple(a+b for a,b in zip(xyz,STEPS[condition['face']]))
        covered=all(0<=a<2 for a in neighbor) and (mask&(1<<(neighbor[0]+2*neighbor[1]+4*neighbor[2])))
        color=paint(xyz,condition['face']) if callable(paint) else paint
        return bool(mask&(1<<cube)) and not covered and condition['paint']==color
    for entry in json.loads((A/f'blockstates/{SLAB_ID}.json').read_text())['multipart']:
        if not all(state[k] in value.split('|') for k,value in entry.get('when',{}).items()):continue
        apply=entry['apply']
        for model in load(apply['model'],predicate=predicate):
            for _ in range(3*(apply.get('y',0)//90)):
                model=rotated(model)
                for element in model.get('elements',[]):
                    rotation=element.get('rotation')
                    if not rotation:continue
                    x,y,z=rotation['origin'];rotation['origin']=[z,y,16-x]
                    if rotation['axis']=='x':rotation['axis']='z';rotation['angle']=-rotation['angle']
                    elif rotation['axis']=='z':rotation['axis']='x'
            add(elements,[model])
    return elements


def slab_item():
    from create_school_projecting_window_assets import SLAB_ID
    elements=[];add(elements,load('lsmmod:item/'+SLAB_ID));return elements


def slab_assembly(opened=False,kind='bottom',paint=0):
    """Two complete 24px sashes: one slab boundary row and one full glass row."""
    from create_school_projecting_window_assets import ID
    elements=[]
    for y in range(2):
        for x in range(3):
            is_slab=y==(0 if kind=='bottom' else 1)
            phase=x+3*(y+(1 if kind=='bottom' else 0))
            if is_slab:
                chunk=slab_scene(opened,paint=paint,kind=kind,phase=phase)
                for e in chunk:
                    for key in ('from','to'):
                        e[key][0]+=16*x;e[key][1]+=16*y
                    if 'rotation' in e:
                        e['rotation']['origin'][0]+=16*x;e['rotation']['origin'][1]+=16*y
                elements.extend(chunk)
            else:
                predicate=lambda c:c.get('pose')==int(opened)
                add(elements,load(f'lsmmod:block/{ID}/phase_{phase}',predicate=predicate),(16*x,16*y,0))
    return elements


def review_slab():
    from create_school_projecting_window_assets import SLAB_ID
    from render_block_review import framing
    paint=lambda xyz,face:13 if (xyz[0]+xyz[2])%2 else 14
    cases=[('Inferior / hoja compartida',slab_scene()),('Dos hojas completas / abiertas 45 grados',slab_assembly(True,paint=paint)),
           ('Superior / hoja inferior cerrada',slab_scene(paint=paint,kind='top')),('Doble / bloque completo',slab_scene(kind='double',paint=paint)),
           ('Item / losa y ventana',slab_item())]
    for facing in ('east','south','west'):cases.append(('Orientacion '+facing,slab_scene(False,facing,paint)))
    canvas=Image.new('RGB',(1440,1560),(194,194,194));draw=ImageDraw.Draw(canvas)
    for i,(label,elements) in enumerate(cases):
        center,scale=framing(elements,size=(480,490));offset=(i%3*480,i//3*520+25)
        canvas.paste(render(elements,center,scale*.85,size=(480,490),azimuth=120 if i==3 else -65,elevation=20,minecraft_orientation=True),offset)
        draw.text((offset[0]+8,offset[1]-18),label,fill=(25,25,25))
    canvas.save(preview_path(SLAB_ID+'_review.png'))


if __name__ == '__main__':
    import argparse
    from pathlib import Path
    import render_texture_review as renderer
    parser=argparse.ArgumentParser()
    parser.add_argument('--vanilla-dir',type=Path,default=renderer.VANILLA)
    parser.add_argument('--slab-only',action='store_true')
    args=parser.parse_args();renderer.VANILLA=args.vanilla_dir
    if args.slab_only:review_slab()
    else:main()
