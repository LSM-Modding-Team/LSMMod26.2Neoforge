"""Six-block wooden handrail: 24px rise, two rails, slim balusters, no tall post."""
from resource_json import resource_json
from pathlib import Path
import json, random, re
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]; A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data'
def write(p,v):
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(resource_json(p, v, ensure_ascii=False)+'\n')
def clip(poly,limit,keep_above):
 out=[]
 for a,b in zip(poly,poly[1:]+poly[:1]):
  ia=(a[1]>=limit) if keep_above else (a[1]<=limit);ib=(b[1]>=limit) if keep_above else (b[1]<=limit)
  if ia:out.append(a)
  if ia!=ib:
   t=(limit-a[1])/(b[1]-a[1]);out.append(tuple(a[k]+t*(b[k]-a[k]) for k in range(3)))
 return out
class Mesh:
 def __init__(self,edge):self.edge=edge;self.lines=['mtllib school_wooden_handrail.mtl'];self.n=0
 def face(self,poly,row):
  poly=clip(clip(poly,row*16,True),(row+1)*16,False)
  for i in range(1,len(poly)-1):
   ps=[poly[0],poly[i],poly[i+1]];a,b,c=ps
   cross=[(b[1]-a[1])*(c[2]-a[2])-(b[2]-a[2])*(c[1]-a[1]),(b[2]-a[2])*(c[0]-a[0])-(b[0]-a[0])*(c[2]-a[2]),(b[0]-a[0])*(c[1]-a[1])-(b[1]-a[1])*(c[0]-a[0])]
   if sum(t*t for t in cross)<1e-12:continue
   ids=[]
   for x,y,z in ps:
    self.n+=1;ids.append(self.n);self.lines.extend([f'v {x/16} {max(0,min(1,(y-row*16)/16))} {z/16}',f'vt {z/16} {max(0,min(1,(y-row*16)/16))}'])
   self.lines.extend(['usemtl wood','f '+' '.join(f'{j}/{j}' for j in ids)])
 def beam(self,poly,row):
  lo=[(self.edge,y,z) for y,z in poly];hi=[(self.edge+2,y,z) for y,z in poly]
  self.face(lo[::-1],row);self.face(hi,row)
  for i in range(4):self.face([lo[i],lo[(i+1)%4],hi[(i+1)%4],hi[i]],row)
def main():
 for p in (A/'models/block').glob('school_wooden_handrail_*'):
  if re.fullmatch(r'school_wooden_handrail_[0-5]_[0-2]\.(json|obj)',p.name):p.unlink()
 rng=random.Random(240);im=Image.new('RGB',(16,16))
 for x in range(16):
  grain=rng.randrange(-5,6)
  for y in range(16):
   n=grain+rng.randrange(-2,3);im.putpixel((x,y),(68+n,43+n,28+n))
 im.save(A/'textures/block/school_wooden_handrail.png')
 (A/'models/block/school_wooden_handrail.mtl').write_text('newmtl wood\nKd 1 1 1\nd 1\nmap_Kd #wood\n')
 states={}
 for side,edge in [('right',14),('left',0)]:
  for col in range(6):
   base=col*4
   for row in range(3):
    if not row*16<base+20:continue
    mesh=Mesh(edge)
    for h in (8,16):mesh.beam([(base+h-2,16),(base+4+h-2,0),(base+4+h,0),(base+h,16)],row)
    # Every baluster starts at the common ground plane of the first block.
    mesh.beam([(0,9),(0,7),(base+2.25+14,7),(base+1.75+14,9)],row)
    name=f'school_wooden_handrail_{side}_{col}_{row}'
    (A/f'models/block/{name}.obj').write_text('\n'.join(mesh.lines)+'\n')
    write(A/f'models/block/{name}.json',{'loader':'neoforge:obj','model':f'lsmmod:models/block/{name}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'wood':'lsmmod:block/school_wooden_handrail','particle':'lsmmod:block/school_wooden_handrail'}})
    for f,y in [('north',0),('east',90),('south',180),('west',270)]:states[f'facing={f},column={col},row={row},right={str(side=="right").lower()}']={'model':'lsmmod:block/'+name,'y':y}
 # Unreachable state combinations have a harmless empty model, so every state resolves.
 write(A/'models/block/school_wooden_handrail_empty.json',{'textures':{'particle':'lsmmod:block/school_wooden_handrail'},'elements':[]})
 for col in range(6):
  for row in range(3):
   for f in ('north','east','south','west'):
    for side in ('true','false'):states.setdefault(f'facing={f},column={col},row={row},right={side}',{'model':'lsmmod:block/school_wooden_handrail_empty'})
 write(A/'blockstates/school_wooden_handrail.json',{'variants':states})
 write(A/'models/item/school_wooden_handrail.json',{'parent':'lsmmod:block/school_wooden_handrail_right_0_0','display':{'gui':{'rotation':[25,135,0],'scale':[.9,.9,.9]}}})
 write(A/'items/school_wooden_handrail.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/school_wooden_handrail'}})
 write(D/'lsmmod/loot_table/blocks/school_wooden_handrail.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'},{'condition':'minecraft:block_state_property','block':'lsmmod:school_wooden_handrail','properties':{'column':'0','row':'0'}}],'entries':[{'type':'minecraft:item','name':'lsmmod:school_wooden_handrail'}]}]})
 for locale,name in [('es_es','Pasamanos de madera de entrada'),('en_us','Entrance Wooden Handrail')]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v['block.lsmmod.school_wooden_handrail']=name;write(p,v)
 p=D/'minecraft/tags/block/mineable/axe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:school_wooden_handrail']));write(p,v)
if __name__=='__main__':main()
