"""Review the actual bathroom leaf, editable frame, palette selection and carved surfaces."""
import itertools
import json
from PIL import Image,ImageDraw
from create_paintable_bathroom_door import ROOT,A,ID
from create_custom_paintable_blocks import STEPS
from create_kiosk_entrance_assets import rotated
from render_texture_review import add,load,render
from render_block_review import framing


def frame_cells(right=False,removed=()):
    return {(x,y,z) for x,y,z in itertools.product(range(8),range(12),range(4))
            if (y>=10 or (x<2 if right else x>=6)) and (x,y,z) not in removed}


def scene(opened=False,right=False,paint=0,removed=(),facing='north',leaf_only=False):
    cells=frame_cells(right,removed);result=[]
    definition=json.loads((A/f'blockstates/{ID}.json').read_text())['multipart']
    turn=('north','east','south','west').index(facing)
    for column,row,depth in itertools.product(range(2),range(3),range(2)):
        state={'facing':facing,'column':str(column),'row':str(row),'depth':str(depth),
               'open':str(opened).lower(),'hinge':'right' if right else 'left'}
        def predicate(condition):
            cube=condition['cube'];xyz=((cube&3)+4*column,((cube>>2)&3)+4*row,((cube>>4)&3)+4*depth)
            neighbor=tuple(v+step for v,step in zip(xyz,STEPS[condition['face']]))
            selected=paint(xyz,condition['face']) if callable(paint) else paint
            return xyz in cells and neighbor not in cells and condition['paint']==selected
        for entry in definition:
            if not all(state[key] in value.split('|') for key,value in entry['when'].items()):continue
            if leaf_only and entry['apply']['model'].endswith('_frame'):continue
            for model in load(entry['apply']['model'],predicate=predicate):
                for _ in range(3*turn):model=rotated(model)
                offset=((column*16,row*16,depth*16),(-depth*16,row*16,column*16),
                        (-column*16,row*16,-depth*16),(depth*16,row*16,-column*16))[turn]
                add(result,[model],offset)
    return result


def item_scene():
    item=[];definition=json.loads((A/f'items/{ID}.json').read_text())['model']
    add(item,load(definition['model']));return item


def review():
    cases=[('Cerrada / marco sin pintar',scene()),('Abierta / misma hoja de bano',scene(True)),
           ('Bisagra derecha / marco verde pintado',scene(False,True,9)),
           ('Marco verde / cubos retirados de 4 px',scene(True,False,9,[(6,5,0),(7,5,0),(6,6,0),(7,6,0)])),
           ('Dorso / marco fijo',scene(False,False,9)),('Item / definicion real',item_scene())]
    for facing in ('east','south','west'):
        cases.append(('Orientacion '+facing,scene(True,False,9,facing=facing)))
    canvas=Image.new('RGB',(1440,1860),(194,194,194));draw=ImageDraw.Draw(canvas)
    for index,(label,elements) in enumerate(cases):
        origin,scale=framing(elements,size=(480,580));angle=120 if index==4 else -65
        offset=(index%3*480,index//3*620+25)
        canvas.paste(render(elements,origin,scale,size=(480,580),azimuth=angle,elevation=12,minecraft_orientation=True),offset)
        draw.text((offset[0]+10,offset[1]-18),label,fill=(25,25,25))
    draw.text((10,1840),'Render externo de modelos finales; no es una prueba en Minecraft.',fill=(25,25,25))
    canvas.save(ROOT/f'previews/{ID}_review.png')
    palette=Image.new('RGB',(1680,2340),(194,194,194));draw=ImageDraw.Draw(palette)
    for paint in range(13):
        elements=scene(paint=paint);origin,scale=framing(elements,size=(420,520));offset=(paint%4*420,paint//4*580+25)
        palette.paste(render(elements,origin,scale,size=(420,520),azimuth=-65,elevation=12,minecraft_orientation=True),offset)
        draw.text((offset[0]+10,offset[1]-18),f'Pintura {paint} / material real',fill=(25,25,25))
    palette.save(ROOT/f'previews/{ID}_palette_review.png')
    editing=Image.new('RGB',(1440,620),(194,194,194));draw=ImageDraw.Draw(editing)
    cases=(('Cuadrados independientes de 4x4 px',scene(paint=lambda xyz,face:9 if sum(xyz)%2 else 4)),
           ('Marco retirado / hoja conservada',scene(removed=frame_cells())),
           ('Hoja abierta / marco retirado',scene(True,removed=frame_cells())))
    for index,(label,elements) in enumerate(cases):
        origin,scale=framing(elements,size=(480,580));offset=(index*480,25)
        editing.paste(render(elements,origin,scale,size=(480,580),azimuth=-65,elevation=12,minecraft_orientation=True),offset)
        draw.text((offset[0]+10,8),label,fill=(25,25,25))
    editing.save(ROOT/f'previews/{ID}_editing_review.png')


if __name__=='__main__':review()
