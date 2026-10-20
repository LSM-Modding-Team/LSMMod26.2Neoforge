"""Review the actual shared 4px geometry, paint selectors, cut surfaces and inventory model."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import output_path
from resource_json import preview_path
import itertools
from PIL import Image,ImageDraw
from painting_and_editable_blocks.create_custom_paintable_blocks import ROOT,FINE_ID,STEPS
from render_texture_review import load,add,render

FULL=(1<<64)-1

def scene(mask=FULL,paint=0):
    def selected(condition):
        cube=condition['cube'];xyz=(cube&3,(cube>>2)&3,(cube>>4)&3)
        neighbor=tuple(v+d for v,d in zip(xyz,STEPS[condition['face']]))
        hidden=all(0<=v<4 for v in neighbor) and mask&(1<<(neighbor[0]+4*neighbor[1]+16*neighbor[2]))
        color=paint(xyz,condition['face']) if callable(paint) else paint
        return bool(mask&(1<<cube)) and not hidden and condition['paint']==color
    result=[];add(result,load('lsmmod:block/'+FINE_ID,predicate=selected));return result

def review():
    tunnel=FULL
    for z in range(4):tunnel&=~(1<<(1+4+16*z))
    carved=FULL
    for x,y,z in itertools.product(range(2),range(2),range(2)):carved&=~(1<<(x+4*y+16*z))
    item=[];add(item,load('lsmmod:item/'+FINE_ID))
    cases=(('Bloque completo / 64 minicubos',scene()),('Cuadrados de 4x4 px',scene(paint=lambda xyz,face:9 if sum(xyz)%2 else 4)),
           ('Recorte con martillo / superficies interiores',scene(carved,9)),('Hueco de 4x4 px atravesando el bloque',scene(tunnel,4)),
           ('Ultimo minicubo / bit 63',scene(1<<63,12)),('Item / modelo real',item))
    canvas=Image.new('RGB',(1440,1000),(194,194,194));draw=ImageDraw.Draw(canvas)
    for index,(label,elements) in enumerate(cases):
        offset=(index%3*480,index//3*500+25)
        canvas.paste(render(elements,[8,8,8],18,size=(480,450),azimuth=-65,elevation=20,minecraft_orientation=True),offset)
        draw.text((offset[0]+10,offset[1]-18),label,fill=(25,25,25))
    draw.text((10,980),'Render externo de modelos finales; sin compilar ni ejecutar Minecraft.',fill=(25,25,25))
    canvas.save(output_path(preview_path(f'{FINE_ID}_review.png')))

if __name__=='__main__':review()
