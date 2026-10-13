"""16x16 warm oak parquet with staggered boards and restrained grain."""
from resource_json import preview_path
from resource_json import resource_json
import json,random
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data'
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(resource_json(p, v, ensure_ascii=False)+'\n')
def main():
 rng=random.Random(109);im=Image.new('RGB',(16,16));boards=[(184,130,73),(193,139,80),(176,122,67),(189,133,74)]
 for y in range(16):
  for x in range(16):
   board=x//4;base=boards[board];delta=rng.choice((-2,-1,0,0,1,2))
   if x%4==0:delta-=17
   elif x%4==1:delta+=5
   if y==(board*5+2)%16:delta-=15
   elif (y*3+x*7)%23==0:delta-=8
   im.putpixel((x,y),tuple(c+delta for c in base))
 im.save(A/'textures/block/school_parquet.png')
 write(A/'blockstates/school_parquet.json',{'variants':{'':{'model':'lsmmod:block/school_parquet'}}})
 write(A/'models/block/school_parquet.json',{'parent':'minecraft:block/cube_all','textures':{'all':'lsmmod:block/school_parquet'}})
 write(A/'models/item/school_parquet.json',{'parent':'lsmmod:block/school_parquet'})
 write(A/'items/school_parquet.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/school_parquet'}})
 write(D/'lsmmod/loot_table/blocks/school_parquet.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:school_parquet'}]}]})
 for locale,name in [('es_es','Parquet del colegio'),('en_us','School Parquet')]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v['block.lsmmod.school_parquet']=name;write(p,v)
 p=D/'minecraft/tags/block/mineable/axe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:school_parquet']));write(p,v)
 (ROOT/'previews').mkdir(exist_ok=True);im.resize((256,256),Image.Resampling.NEAREST).save(preview_path('school_parquet.png'))
if __name__=='__main__':main()
