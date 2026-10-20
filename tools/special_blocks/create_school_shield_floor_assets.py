"""Generate a vanilla-resolution 4x4 crest slab from a deterministic pixel drawing."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_path, module_write_text, output_path
from resource_json import preview_path
from resource_json import resource_json
import json
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[2]
ASSETS=ROOT/'src/main/resources/assets/lsmmod'
BLOCKS=ASSETS/'textures/block'

def write(path,value):module_write_text(path, resource_json(path, value, ensure_ascii=False)+'\n')

def crest():
 floor=Image.open(module_path(BLOCKS/'classroom_floor.png')).convert('RGBA')
 canvas=Image.new('RGBA',(64,64))
 for row in range(4):
  for col in range(4):canvas.paste(floor,(col*16,row*16))
 d=ImageDraw.Draw(canvas)
 # Keep a golden outer border and the tiled floor visible around the shield.
 outline=[(5,3),(58,3),(58,47),(32,61),(5,47)]
 inside=[(7,5),(56,5),(56,46),(32,58),(7,46)]
 d.polygon(outline,fill=(197,148,42,255));d.line(outline+[outline[0]],fill=(231,182,63,255),width=1)
 d.polygon(inside,fill=(169,39,36,255))
 # Small coherent variations, rather than gradients or photographic antialiasing.
 for y in range(6,47):
  for x in range(8,56):
   if (x*17+y*11)%37==0:d.point((x,y),fill=(177,43,39,255))
 # Stylised white shield/S following the reference below the yellow header.
 white=(235,231,217,255);shadow=(206,202,190,255);red=(169,39,36,255)
 rim=[(10,14),(20,14),(27,11),(32,7),(38,12),(46,14),(54,14),(52,21),(48,26),(41,29),(47,34),(51,40),(52,45),(43,46),(37,49),(32,54),(25,49),(18,47),(10,46),(16,39),(23,35),(18,31),(14,25)]
 d.polygon(rim,fill=white)
 cut=[(15,18),(22,17),(28,14),(32,11),(37,15),(44,18),(48,18),(44,23),(37,26),(31,23),(28,28),(38,33),(43,38),(46,42),(39,43),(32,49),(25,44),(18,43),(25,39),(31,41),(36,37),(29,32),(21,28),(18,23)]
 d.polygon(cut,fill=red)
 # Broad diagonal ribbon and its upper return form the interlocking S motif.
 d.polygon([(20,20),(26,18),(27,22),(33,26),(41,31),(44,37),(38,39),(32,34),(24,29)],fill=white)
 d.polygon([(31,18),(38,21),(47,21),(42,25),(36,26),(29,23)],fill=white)
 d.line([(11,46),(18,46),(25,48),(32,53)],fill=shadow,width=1)
 # A compact black M, placed diagonally on the ribbon like the supplied crest.
 d.line([(27,31),(29,26),(30,31),(34,28),(32,34)],fill=(30,28,26,255),width=2)
 return canvas

def main():
 canvas=crest();states={}
 for row in range(4):
  for col in range(4):
   name=f'school_shield_{row}_{col}'
   canvas.crop((col*16,row*16,(col+1)*16,(row+1)*16)).save(output_path(BLOCKS/f'{name}.png'))
   faces={side:{'texture':'#floor','uv':[0,0,16,8] if side in ('north','south','east','west') else [0,0,16,16],'cullface':side} for side in ('north','south','east','west','down')}
   faces['up']={'texture':'#crest','uv':[0,0,16,16]}
   write(ASSETS/f'models/block/{name}.json',{'parent':'minecraft:block/block','textures':{'floor':'lsmmod:block/classroom_floor','crest':'lsmmod:block/'+name,'particle':'lsmmod:block/classroom_floor'},'elements':[{'from':[0,0,0],'to':[16,8,16],'faces':faces}]})
   for facing,turn in [('north',0),('east',90),('south',180),('west',270)]:
    states[f'column={col},facing={facing},row={row}']={'model':'lsmmod:block/'+name,'y':turn}
 write(ASSETS/'blockstates/school_shield.json',{'variants':states})
 canvas.save(output_path(ASSETS/'textures/item/school_shield.png'))
 (ROOT/'previews').mkdir(exist_ok=True)
 canvas.resize((512,512),Image.Resampling.NEAREST).save(output_path(preview_path('school_shield_4x4.png')))
if __name__=='__main__':main()
