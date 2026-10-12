"""World-aligned 1.5-block bathroom tiles, using the previous dark pavement grey."""
import json,random,itertools
from PIL import Image
from create_school_yellow_border_assets import ROOT,A,D,write

def main():
 rng=random.Random(731);plain=Image.new('RGB',(16,16))
 for y,x in itertools.product(range(16),repeat=2):
  delta=rng.choice((-2,-1,0,0,1,2));plain.putpixel((x,y),tuple(c+delta for c in (73,74,72)))
 plain.save(A/'textures/block/dark_school_tiled_floor_side.png');variants={}
 for sx,sz in itertools.product(range(3),repeat=2):
  im=plain.copy()
  for y,x in itertools.product(range(16),repeat=2):
   u=(sx*16+x)%24;v=(sz*16+y)%24
   if u==0 or v==0:im.putpixel((x,y),(113,114,110))
   elif u==1 or v==1:im.putpixel((x,y),(79,80,78))
  name=f'dark_school_tiled_floor_{sx}_{sz}';im.save(A/f'textures/block/{name}.png')
  faces={face:{'texture':'#side','uv':[0,0,16,16],'cullface':face} for face in ('north','south','east','west')}
  faces['up']={'texture':'#top','uv':[0,0,16,16],'cullface':'up'}
  faces['down']={'texture':'#side','uv':[0,0,16,16],'cullface':'down'}
  write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','textures':{'top':'lsmmod:block/'+name,'side':'lsmmod:block/dark_school_tiled_floor_side','particle':'lsmmod:block/dark_school_tiled_floor_side'},'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':faces}]})
  variants[f'tile_x={sx},tile_z={sz}']={'model':'lsmmod:block/'+name}
 write(A/'blockstates/dark_school_tiled_floor.json',{'variants':variants})
 write(A/'models/item/dark_school_tiled_floor.json',{'parent':'lsmmod:block/dark_school_tiled_floor_0_0'})
 write(A/'items/dark_school_tiled_floor.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/dark_school_tiled_floor'}})
 write(D/'lsmmod/loot_table/blocks/dark_school_tiled_floor.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:dark_school_tiled_floor'}]}]})
 for locale,name in [('es_es','Suelo de baldosas oscuras del colegio'),('en_us','Dark School Tiled Floor')]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v['block.lsmmod.dark_school_tiled_floor']=name;write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:dark_school_tiled_floor']));write(p,v)
 from create_light_school_tiled_floor_assets import main as rebuild_light
 rebuild_light()
if __name__=='__main__':main()
