"""External source-model preview; not an in-game screenshot."""
from PIL import Image,ImageDraw
from create_double_school_entrance import ROOT,cell_model
from render_school_curtain import render

def main():
    canvas=Image.new('RGB',(960,600),(112,130,128))
    for index,opened in enumerate((False,True)):
        pieces=[];textures={}
        for col in range(3):
            for row in range(3):
                for depth in range(2):
                    value=cell_model(col,row,depth,opened,(1,4,2)[col])
                    textures.update(value['textures'])
                    for e in value['elements']:
                        for k in ('from','to'):e[k]=[e[k][0]+16*col,e[k][1]+16*row,e[k][2]+16*depth]
                        pieces.append(e)
        canvas.paste(render({'elements':pieces,'textures':textures},[24,24,10],8),(480*index,0))
    draw=ImageDraw.Draw(canvas)
    draw.text((20,25),'Puerta 3 x 3: cerrada; hojas de 24 px',fill='white')
    draw.text((500,25),'Abierta: cristal superior fijo',fill='white')
    canvas.save(ROOT/'previews/double_school_entrance.png')
if __name__=='__main__':main()
