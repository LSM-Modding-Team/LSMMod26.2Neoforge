"""Render final models, slab paint squares and the taller bathroom door offline."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, output_path
import itertools,json
from PIL import Image,ImageDraw
from windows_and_enclosures.create_extended_school_doors import A,DOUBLE,BATHROOM
from create_kiosk_entrance_assets import rotated
from render_texture_review import load,add,render
from render_block_review import framing
from resource_json import preview_path


def scene(ident,opened=False,right=False,facing='north',paint=0):
    columns,rows=(3,3) if ident==DOUBLE else (2,4);elements=[];turn=('north','east','south','west').index(facing)
    entries=json.loads(module_read_text(A/f'blockstates/{ident}.json'))['multipart']
    for column,row,depth in itertools.product(range(columns),range(rows),range(2)):
        state=dict(facing=facing,open=str(opened).lower(),powered='false',hinge='right' if right else 'left',column=str(column),row=str(row),depth=str(depth))
        def predicate(condition):
            if condition['type']=='lsmmod:glazing_mask':return (1 if column==0 else 2) in condition.get('masks',[condition.get('mask')])
            face=condition['face'];q=condition['quadrant']
            x=0 if face in ('west','east') else q&1;y=0 if face in ('up','down') else q>>1
            z=q&1 if face in ('west','east') else q>>1 if face in ('up','down') else 0
            for _ in range(turn):
                if face in ('north','east','south','west'):face=('north','east','south','west')[(('north','east','south','west').index(face)+1)%4]
                x,z=1-z,x
            q=z+2*y if face in ('west','east') else x+2*z if face in ('up','down') else x+2*y
            color=paint(column,face,q) if callable(paint) else paint
            return condition['paint']==color
        for entry in entries:
            if not all(state[k] in v.split('|') for k,v in entry['when'].items()):continue
            for model in load(entry['apply']['model'],predicate=predicate):
                for _ in range(3*turn):model=rotated(model)
                offset=((column*16,row*16,depth*16),(-depth*16,row*16,column*16),(-column*16,row*16,-depth*16),(depth*16,row*16,-column*16))[turn]
                add(elements,[model],offset)
    return elements


def item(ident):
    result=[];add(result,load('lsmmod:item/'+ident));return result


def main():
    for ident in (DOUBLE,BATHROOM):
        color=(lambda column,face,q:9 if (column+q)%2 else 4) if ident==DOUBLE else 0
        cases=[('Cerrada / modelos finales',scene(ident)),('Abierta / parte superior fija',scene(ident,True,paint=color)),
               ('Dorso / UV originales',scene(ident,True,paint=color)),('Item / modelo completo',item(ident))]
        if ident==BATHROOM:cases.append(('Bisagra derecha',scene(ident,True,True)))
        for facing in ('east','south','west'):cases.append(('Orientacion '+facing,scene(ident,True,facing=facing,paint=color)))
        canvas=Image.new('RGB',(1440,1860),(194,194,194));draw=ImageDraw.Draw(canvas)
        for i,(label,elements) in enumerate(cases):
            origin,scale=framing(elements,size=(480,580));offset=(i%3*480,i//3*620+25)
            canvas.paste(render(elements,origin,scale,size=(480,580),azimuth=120 if i==2 else -65,elevation=12,minecraft_orientation=True),offset)
            draw.text((offset[0]+10,offset[1]-18),label,fill=(25,25,25))
        canvas.save(output_path(preview_path(ident+'_review.png')))

if __name__=='__main__':main()
