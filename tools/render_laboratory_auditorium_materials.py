"""Review actual material blocks, tile repetition, paint libraries and stage faces offline."""
from resource_modules import output_path
import json
from PIL import Image,ImageDraw
from create_laboratory_auditorium_materials import MATERIALS,A
from render_texture_review import load,add,render
from render_block_review import framing
from resource_json import preview_path

def block(ident):
    result=[];add(result,load('lsmmod:block/'+ident));return result

def main():
    cases=[]
    for ident in MATERIALS:
        cases.append((ident,block(ident)))
        surface=[]
        for x in range(2):
            for z in range(2):add(surface,load('lsmmod:block/'+ident),(16*x,0,16*z))
        cases.append((ident+' / 2x2 bloques',surface))
    for color in (13,14,15):
        painted=[];add(painted,load('lsmmod:block/school_paintable_block',predicate=lambda c:c['paint']==color))
        cases.append(('Pintura '+str(color)+' / bloque real',painted))
    canvas=Image.new('RGB',(1440,1760),(194,194,194));draw=ImageDraw.Draw(canvas)
    for i,(label,elements) in enumerate(cases):
        origin,scale=framing(elements,size=(480,400));offset=(i%3*480,i//3*440+25)
        canvas.paste(render(elements,origin,scale,size=(480,400),azimuth=-65,elevation=30,minecraft_orientation=True),offset)
        draw.text((offset[0]+8,offset[1]-18),label,fill=(25,25,25))
    canvas.save(output_path(preview_path('laboratory_auditorium_materials_review.png')))
    from render_paintable_bathroom_door import column_scene
    from render_extended_school_doors import scene,DOUBLE
    from render_school_glass import stair_scene
    cases=[('Columna / baldosas en cuadrados de 8 px',column_scene(True,paint=lambda row,face,q:13 if q%2 else 14)),
           ('Dintel / parquet pintado',scene(DOUBLE,True,paint=15)),('Escalera / parquet pintado',stair_scene(paint=15))]
    canvas=Image.new('RGB',(1440,620),(194,194,194));draw=ImageDraw.Draw(canvas)
    for i,(label,elements) in enumerate(cases):
        origin,scale=framing(elements,size=(480,580));canvas.paste(render(elements,origin,scale,size=(480,580),azimuth=-65,elevation=20,minecraft_orientation=True),(i*480,25));draw.text((i*480+8,5),label,fill=(25,25,25))
    canvas.save(output_path(preview_path('laboratory_auditorium_paints_review.png')))

if __name__=='__main__':main()
