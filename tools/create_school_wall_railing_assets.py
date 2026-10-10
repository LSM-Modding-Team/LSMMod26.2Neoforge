"""Generate the lowered wall and connected railing, using existing textures."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
ID='light_school_wall_railing'
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n')
def element(lo,hi,texture):
 return {'from':lo,'to':hi,'faces':{side:{'texture':'#'+texture,'uv':[0,0,16,16]} for side in ('north','south','east','west','up','down')}}
def model(name,elements):write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','textures':{'wall':'lsmmod:block/light_school_wall','metal':'lsmmod:block/school_gate_edge','particle':'lsmmod:block/light_school_wall'},'elements':elements})
def main():
 model(ID,[element([0,0,0],[16,12,16],'wall')])
 model(ID+'_post',[element([7,12,7],[9,18,9],'metal')])
 model(ID+'_arm',[element([7,18,0],[9,20,7],'metal')])
 model(ID+'_center',[element([7,18,7],[9,20,9],'metal')])
 model(ID+'_isolated',[element([7,18,0],[9,20,16],'metal')])
 model(ID+'_inventory',[element([0,0,0],[16,12,16],'wall'),element([7,12,7],[9,18,9],'metal'),element([7,18,0],[9,20,16],'metal')])
 multipart=[{'apply':{'model':'lsmmod:block/'+ID}},{'when':{'post':'true'},'apply':{'model':'lsmmod:block/'+ID+'_post'}}]
 multipart.append({'when':{'OR':[{d:'true'} for d in ('north','east','south','west')]},'apply':{'model':'lsmmod:block/'+ID+'_center'}})
 for direction,angle in (('north',0),('east',90),('south',180),('west',270)):
  multipart.append({'when':{direction:'true'},'apply':{'model':'lsmmod:block/'+ID+'_arm','y':angle,'uvlock':True}})
 for direction,angle in (('north',0),('east',90),('south',180),('west',270)):
  multipart.append({'when':{**{d:'false' for d in ('north','east','south','west')},'facing':direction},'apply':{'model':'lsmmod:block/'+ID+'_isolated','y':angle,'uvlock':True}})
 write(A/f'blockstates/{ID}.json',{'multipart':multipart})
 write(A/f'models/item/{ID}.json',{'parent':'lsmmod:block/'+ID+'_inventory'})
 write(A/f'items/{ID}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ID}})
 write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ID}]}]})
 for locale,label in (('es_es','Muro escolar claro con baranda'),('en_us','Light School Wall with Railing')):
  p=A/f'lang/{locale}.json';data=json.loads(p.read_text());data['block.lsmmod.'+ID]=label;write(p,data)
 for tag in ('fences','mineable/pickaxe'):
  p=ROOT/f'src/main/resources/data/minecraft/tags/block/{tag}.json';data=json.loads(p.read_text()) if p.exists() else {'values':[]};data['values']=list(dict.fromkeys(data['values']+['lsmmod:'+ID]));write(p,data)
if __name__=='__main__':main()
