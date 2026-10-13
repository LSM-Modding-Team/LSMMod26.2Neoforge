"""Review real sign cells, front/back/orientations/item and the newly painted red surface."""
import json
from PIL import Image,ImageDraw
from create_school_name_sign import ROOT,A,ID,TEXTURES
from render_texture_review import add,block,load,render


def scene(wall=True):
    result=[]
    if wall:
        for y in range(3):
            for x in range(10):
                models=load('lsmmod:block/school_paintable_block',predicate=lambda condition:condition.get('paint')==12)
                add(result,models,(x*16,y*16,16))
    for row in range(3):
        for column in range(10):
            add(result,block(ID,{'facing':'north','row':str(row),'column':str(column)}),(column*16,row*16,0))
    return result


def main():
    painted=scene();letters=scene(False);item=[];add(item,load('lsmmod:item/'+ID))
    canvas=Image.new('RGB',(1800,1050),(194,194,194))
    cases=(('Frente / texto exacto sobre pintura roja',painted,[80,24,16],8.5,-90,0,(1800,420),(0,0)),
           ('Relieve de 1 pixel / lateral',painted,[80,24,16],5,-65,8,(900,420),(0,420)),
           ('Item / proporciones 10x3',item,[8,8,8],42,-90,0,(900,420),(900,420)))
    for label,elements,center,scale,azimuth,elevation,size,offset in cases:
        canvas.paste(render(elements,center,scale,size=size,azimuth=azimuth,elevation=elevation,minecraft_orientation=True),offset)
        ImageDraw.Draw(canvas).text((offset[0]+12,offset[1]+10),label,fill=(25,25,25))
    painted_cube=[];add(painted_cube,load('lsmmod:block/school_paintable_block',predicate=lambda condition:condition.get('paint')==12))
    canvas.paste(render(painted_cube,[8,8,8],8,size=(350,200),minecraft_orientation=True),(0,850))
    ImageDraw.Draw(canvas).text((360,900),'red_street_wall_paint / material real en el bloque pintable',fill=(25,25,25))
    definition=json.loads((A/'items/red_street_wall_paint.json').read_text())['model']
    tool=json.loads((A/f"models/{definition['model'].split(':')[1]}.json").read_text())
    assert tool['parent']=='minecraft:item/generated'
    # This fully opaque 16px sprite generates a flat square item; read its material from the real definition.
    paint_item=[]
    sprite={'textures':{'surface':tool['textures']['layer0']},'elements':[{'from':[0,0,7.5],'to':[16,16,8.5],
            'faces':{face:{'texture':'#surface','uv':[0,0,16,16]} for face in ('north','south','up','down','east','west')}}]}
    add(paint_item,[sprite])
    canvas.paste(render(paint_item,[8,8,8],8,size=(300,200),azimuth=-90,elevation=0,minecraft_orientation=True),(1150,850))
    ImageDraw.Draw(canvas).text((1460,900),'Item de pintura / sprite real',fill=(25,25,25))
    canvas.save(ROOT/'previews/school_name_sign_review.png')
    reverse=Image.new('RGB',(1800,520),(194,194,194))
    for index,angle in enumerate((90,0,180)):
        reverse.paste(render(letters,[80,24,15.5],3.4,size=(600,520),azimuth=angle,elevation=12,minecraft_orientation=True),(600*index,0))
        ImageDraw.Draw(reverse).text((600*index+12,10),('Dorso / letras sin panel','Orientacion lateral este','Orientacion lateral oeste')[index],fill=(25,25,25))
    reverse.save(ROOT/'previews/school_name_sign_reverse_review.png')


if __name__=='__main__':main()
