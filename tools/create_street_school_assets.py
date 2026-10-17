"""Vanilla-resolution plaster and a 4x3 dark chain-link bars panel."""
from resource_modules import module_read_text, module_write_text, output_path
from resource_json import preview_path
from resource_json import resource_json
import json,random
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data'
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);module_write_text(p, resource_json(p, v, ensure_ascii=False)+'\n')
def cube(a,b,uv):return {'from':a,'to':b,'faces':{f:{'texture':'#mesh','uv':[uv[2],uv[1],uv[0],uv[3]] if f in ('north','east') else uv} for f in ('north','south','east','west','up','down')}}
def main():
 rng=random.Random(1613);wall=Image.new('RGB',(16,16))
 for y in range(16):
  for x in range(16):
   delta=rng.choice((-3,-2,-1,0,0,1,2));wall.putpixel((x,y),tuple(c+delta for c in (195,183,158)))
 wall.save(output_path(A/'textures/block/street_school_wall.png'))
 write(A/'models/block/street_school_wall.json',{'parent':'minecraft:block/cube_all','textures':{'all':'lsmmod:block/street_school_wall'}})
 write(A/'blockstates/street_school_wall.json',{'variants':{'':{'model':'lsmmod:block/street_school_wall'}}})
 mesh=Image.new('RGBA',(64,48));draw=ImageDraw.Draw(mesh)
 for y in range(48):
  for x in range(64):
   if (x+y)%8==0 or (x-y)%8==0:mesh.putpixel((x,y),(72,67,57,255))
 draw.rectangle((0,0,63,47),outline=(46,43,38,255),width=2)
 draw.line((2,2,2,45),fill=(90,82,67,255));draw.line((61,2,61,45),fill=(79,74,63,255))
 draw.line((2,2,61,2),fill=(99,90,74,255));draw.line((2,45,61,45),fill=(79,74,63,255))
 parts=[]
 for col in range(4):
  for row in range(3):
   name=f'street_school_bars_{col}_{row}';mesh.crop((col*16,(2-row)*16,(col+1)*16,(3-row)*16)).save(output_path(A/f'textures/block/{name}.png'))
   boxes={'center':([7,0,7],[9,16,9],[7,0,9,16]),'east':([8,0,7],[16,16,9],[8,0,16,16]),'west':([0,0,7],[8,16,9],[0,0,8,16]),'north':([7,0,0],[9,16,8],[0,0,8,16]),'south':([7,0,8],[9,16,16],[8,0,16,16])}
   for side,(a,b,uv) in boxes.items():
    model=f'{name}_{side}';write(A/f'models/block/{model}.json',{'parent':'minecraft:block/block','render_type':'minecraft:cutout','textures':{'mesh':'lsmmod:block/'+name,'particle':'lsmmod:block/'+name},'elements':[cube(a,b,uv)]})
    condition={'column':str(col),'row':str(row)}
    if side!='center':condition[side]='true'
    parts.append({'when':condition,'apply':{'model':'lsmmod:block/'+model}})
 write(A/'blockstates/street_school_bars.json',{'multipart':parts})
 for ident in ('street_school_wall','street_school_bars'):
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident} if ident.endswith('wall') else {'parent':'minecraft:item/generated','textures':{'layer0':'lsmmod:block/street_school_bars_0_1'}})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
 for locale,wall_name,bars_name in [('es_es','Pared exterior del colegio','Reja de malla del colegio'),('en_us','Street School Wall','School Chain-Link Bars')]:
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p));v['block.lsmmod.street_school_wall']=wall_name;v['block.lsmmod.street_school_bars']=bars_name;write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:street_school_wall','lsmmod:street_school_bars']));write(p,v)
 (ROOT/'previews').mkdir(exist_ok=True);mesh.resize((512,384),Image.Resampling.NEAREST).save(output_path(preview_path('street_school_bars_4x3.png')))
if __name__=='__main__':main()
