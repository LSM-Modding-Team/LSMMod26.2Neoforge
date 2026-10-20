"""World-aligned 1.5-block bathroom tiles, using a light grey porcelain finish."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, output_path
import json,random,itertools
from PIL import Image
from building_blocks.create_school_yellow_border_assets import ROOT,A,D,write

def main():
 rng=random.Random(731);plain=Image.new('RGB',(16,16))
 for y,x in itertools.product(range(16),repeat=2):
  delta=rng.choice((-2,-1,0,0,1,2));plain.putpixel((x,y),tuple(c+delta for c in (215,218,222)))
 plain.save(output_path(A/'textures/block/light_school_tiled_floor_side.png'));variants={}
 for sx,sz in itertools.product(range(3),repeat=2):
  im=plain.copy()
  for y,x in itertools.product(range(16),repeat=2):
   u=(sx*16+x)%24;v=(sz*16+y)%24
   if u==0 or v==0:im.putpixel((x,y),(173,177,182))
   elif u==1 or v==1:im.putpixel((x,y),(202,205,209))
  name=f'light_school_tiled_floor_{sx}_{sz}';im.save(output_path(A/f'textures/block/{name}.png'))
  faces={face:{'texture':'#side','uv':[0,0,16,16],'cullface':face} for face in ('north','south','east','west')}
  faces['up']={'texture':'#top','uv':[0,0,16,16],'cullface':'up'}
  faces['down']={'texture':'#side','uv':[0,0,16,16],'cullface':'down'}
  write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','textures':{'top':'lsmmod:block/'+name,'side':'lsmmod:block/light_school_tiled_floor_side','particle':'lsmmod:block/light_school_tiled_floor_side'},'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':faces}]})
  variants[f'tile_x={sx},tile_z={sz}']={'model':'lsmmod:block/'+name}
 write(A/'blockstates/light_school_tiled_floor.json',{'variants':variants})
 write(A/'models/item/light_school_tiled_floor.json',{'parent':'lsmmod:block/light_school_tiled_floor_0_0'})
 write(A/'items/light_school_tiled_floor.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/light_school_tiled_floor'}})
 write(D/'lsmmod/loot_table/blocks/light_school_tiled_floor.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:light_school_tiled_floor'}]}]})
 for locale,name in [('es_es','Suelo de baldosas claras del colegio'),('en_us','Light School Tiled Floor')]:
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p));v['block.lsmmod.light_school_tiled_floor']=name;write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:light_school_tiled_floor']));write(p,v)
if __name__=='__main__':main()
