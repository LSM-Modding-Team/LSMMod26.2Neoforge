"""Fully moving bathroom doors (3/4 rows) and an independent opaque school panel."""
from resource_json import resource_json
from pathlib import Path
import json,copy
from PIL import Image,ImageDraw
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/lsmmod';D=R/'src/main/resources/data'
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(resource_json(p, v, ensure_ascii=False)+'\n')
def item_loot(ident,controller=False):
 write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{ident}'}})
 conditions=[{'condition':'minecraft:survives_explosion'}]
 if controller:conditions.append({'condition':'minecraft:block_state_property','block':'lsmmod:'+ident,'properties':{'row':'0'}})
 write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':conditions,'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
def main():
 for height in (3,4):
  ident=f'tall_bathroom_door_full_{height}';states={}
  for row in range(4):
   # ROW is shared by both heights. Row=3 is unreachable when height=3.
   texture_row=min(row,height-1)
   for hinge,col in [('left',0),('right',1)]:
    for opened in (False,True):
     suffix='_open' if opened else ''
     source=json.loads((A/f'models/block/tall_classroom_entrance_{col}_{0 if row==0 else 1}{suffix}.json').read_text())
     elements=copy.deepcopy(source['elements'])
     for e in elements:
      for f,face in e['faces'].items():
       face['texture']='#panel';face.pop('cullface',None)
       # Preserve the complete bottom texture and stretch only plain upper wood.
       if row>0 and f in (('east','west') if opened else ('north','south')):
        uv=face['uv'];uv[1]=16*(height-1-texture_row)/(height-1);uv[3]=16*(height-texture_row)/(height-1)
     name=f'{ident}_{row}_{hinge}_{str(opened).lower()}'
     write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid','textures':{'panel':f'lsmmod:block/bathroom_door_{"bottom" if row==0 else "top"}','particle':'lsmmod:block/bathroom_door_bottom'},'elements':elements})
     for facing,turn in [('north',0),('east',90),('south',180),('west',270)]:
      for power in (False,True):states[f'facing={facing},hinge={hinge},open={str(opened).lower()},powered={str(power).lower()},row={row}']={'model':'lsmmod:block/'+name,'y':turn}
  write(A/f'blockstates/{ident}.json',{'variants':states})
  top=Image.open(A/'textures/block/bathroom_door_top.png').convert('RGBA').resize((16,(height-1)*16),Image.Resampling.NEAREST)
  bottom=Image.open(A/'textures/block/bathroom_door_bottom.png').convert('RGBA');icon=Image.new('RGBA',(64,64));y=(64-height*16)//2;icon.paste(top,(24,y));icon.paste(bottom,(24,y+(height-1)*16));icon.save(A/f'textures/item/{ident}.png')
  write(A/f'models/item/{ident}.json',{'parent':'minecraft:item/generated','textures':{'layer0':'lsmmod:item/'+ident}});item_loot(ident,True)
 ident='school_tinted_glass';m=json.loads((A/'models/block/school_glass.json').read_text());m['render_type']='minecraft:solid'
 m['textures']['glass']='lsmmod:block/school_tinted_glass';m['textures']['glass_edge']='lsmmod:block/school_tinted_glass'
 for e in m['elements']:
  for face in e['faces'].values():face.pop('cullface',None)
 write(A/f'models/block/{ident}.json',m)
 im=Image.new('RGBA',(16,16),(47,52,56,255));draw=ImageDraw.Draw(im)
 draw.line((3,2,3,8),fill=(55,61,65,255));draw.line((4,3,4,9),fill=(55,61,65,255));draw.line((11,10,11,14),fill=(42,47,51,255));im.save(A/f'textures/block/{ident}.png')
 write(A/f'blockstates/{ident}.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/'+ident,'y':turn} for f,turn in [('north',0),('east',90),('south',180),('west',270)]}})
 write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident});item_loot(ident)
 for locale,names in [('es_es',('Puerta completa de baño (3 bloques)','Puerta completa de baño (4 bloques)','Cristal opaco del colegio')),('en_us',('Full Bathroom Door (3 Blocks)','Full Bathroom Door (4 Blocks)','School Tinted Glass'))]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text())
  for ident,name in zip(('tall_bathroom_door_full_3','tall_bathroom_door_full_4','school_tinted_glass'),names):v['block.lsmmod.'+ident]=name
  write(p,v)
 p=D/'minecraft/tags/block/mineable/axe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:tall_bathroom_door_full_3','lsmmod:tall_bathroom_door_full_4']));write(p,v)
if __name__=='__main__':main()
