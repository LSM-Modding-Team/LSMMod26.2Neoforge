"""Six-direction connected pavement, 3px yellow perimeter on every exposed face."""
from resource_json import resource_json
import itertools,json,random
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data'
ID='school_entrance_pavement';SIDES=('north','east','south','west','up','down')
# Face-local top/right/bottom/left following vanilla cube UV conventions.
EDGES={'north':('up','west','down','east'),'south':('up','east','down','west'),'east':('up','north','down','south'),'west':('up','south','down','north'),'up':('north','east','south','west'),'down':('south','east','north','west')}
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(resource_json(p, v, ensure_ascii=False)+'\n')
def main():
 rng=random.Random(3106);base=Image.new('RGB',(16,16))
 for y,x in itertools.product(range(16),repeat=2):
  delta=rng.choice((-3,-2,-1,0,0,1,2,3));base.putpixel((x,y),tuple(c+delta for c in (135,141,150)))
 for mask in range(16):
  tile=base.copy()
  for y,x in itertools.product(range(16),repeat=2):
   border=(not(mask&1) and y<3) or (not(mask&2) and x>=13) or (not(mask&4) and y>=13) or (not(mask&8) and x<3)
   if border:tile.putpixel((x,y),(255,211,0))
  tile.save(A/f'textures/block/{ID}_{mask}.png')
 variants={}
 for flags in itertools.product((False,True),repeat=6):
  linked=dict(zip(SIDES,flags));index=sum(int(v)<<i for i,v in enumerate(flags));textures={};faces={}
  for side in SIDES:
   mask=sum(int(linked[edge])<<i for i,edge in enumerate(EDGES[side]));textures[side]=f'lsmmod:block/{ID}_{mask}'
   faces[side]={'texture':'#'+side,'uv':[0,0,16,16],'cullface':side}
  textures['particle']=f'lsmmod:block/{ID}_15'
  name=f'{ID}_{index}';write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','textures':textures,'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':faces}]})
  variants[','.join(f'{side}={str(linked[side]).lower()}' for side in SIDES)]={'model':'lsmmod:block/'+name}
 write(A/f'blockstates/{ID}.json',{'variants':variants})
 write(A/f'models/item/{ID}.json',{'parent':f'lsmmod:block/{ID}_0'})
 write(A/f'items/{ID}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ID}})
 write(D/f'lsmmod/loot_table/blocks/{ID}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ID}]}]})
 for locale,name in [('es_es','Pavimento de la entrada del colegio'),('en_us','School Entrance Pavement')]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v['block.lsmmod.'+ID]=name;write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+ID]));write(p,v)
if __name__=='__main__':main()
