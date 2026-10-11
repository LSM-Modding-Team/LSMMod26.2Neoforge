"""Static round wall clock/fan and manually switched circular ceiling lamp."""
import json,math
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data'
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,ensure_ascii=False,indent=2)+'\n')
UV={'metal':(.1,.1),'blade':(.35,.1),'rim':(.6,.1),'body':(.85,.1),'hub':(.1,.6),'on':(.35,.6),'off':(.6,.6)}
class Mesh:
 def __init__(self):self.lines=['mtllib school_decorations.mtl'];self.index=0
 def face(self,pts,material):
  for i in range(1,len(pts)-1):
   ids=[]
   for x,y,z in (pts[0],pts[i],pts[i+1]):
    self.index+=1;ids.append(self.index);self.lines.append(f'v {x/16:.12g} {y/16:.12g} {z/16:.12g}')
    u,v=(x/16,y/16) if material=='clock' else (UV[material][0],1-UV[material][1]);self.lines.append(f'vt {u:.12g} {v:.12g}')
   self.lines.extend(['usemtl '+material,'f '+' '.join(f'{j}/{j}' for j in ids)])
 def prism(self,poly,start,end,material,axis='z',front=None):
  point=lambda p,d:(p[0],p[1],d) if axis=='z' else (p[0],d,16-p[1])
  self.face([point(p,start) for p in poly[::-1]],material)
  self.face([point(p,end) for p in poly],front or material)
  for a,b in zip(poly,poly[1:]+poly[:1]):self.face([point(a,start),point(b,start),point(b,end),point(a,end)],material)
 def disk(self,cx,cy,r,start,end,material,axis='z',front=None):
  self.prism([(cx+r*math.cos(i*math.tau/32),cy+r*math.sin(i*math.tau/32)) for i in range(32)],start,end,material,axis,front)
 def ring(self,cx,cy,r,thickness,z,material):
  for i in range(32):
   a=i*math.tau/32;b=(i+1)*math.tau/32
   poly=[(cx+(r-thickness)*math.cos(a),cy+(r-thickness)*math.sin(a)),(cx+r*math.cos(a),cy+r*math.sin(a)),(cx+r*math.cos(b),cy+r*math.sin(b)),(cx+(r-thickness)*math.cos(b),cy+(r-thickness)*math.sin(b))]
   self.prism(poly,z,z+.25,material)
 def box(self,x0,y0,z0,x1,y1,z1,material):self.prism([(x0,y0),(x1,y0),(x1,y1),(x0,y1)],z0,z1,material)
 def save(self,name):(A/f'models/block/{name}.obj').write_text('\n'.join(self.lines)+'\n')
def model(name,emission=False):
 v={'loader':'neoforge:obj','model':f'lsmmod:models/block/{name}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'surface':'lsmmod:block/school_decoration_surface','clock_face':'lsmmod:block/school_clock_face','particle':'lsmmod:block/school_decoration_surface'}}
 if emission:v['shade_quads']=False
 return v

def main():
 atlas=Image.new('RGB',(16,16));draw=ImageDraw.Draw(atlas)
 for (x,y),color in { (0,0):(95,98,94),(4,0):(144,147,143),(8,0):(168,168,157),(12,0):(219,221,215),(0,8):(67,71,69),(4,8):(240,242,237),(8,8):(155,160,155)}.items():draw.rectangle((x,y,x+3,y+7),fill=color)
 atlas.save(A/'textures/block/school_decoration_surface.png')
 clock=Image.new('RGB',(16,16),(200,201,187));draw=ImageDraw.Draw(clock)
 for i in range(12):
  a=math.tau*i/12;draw.point((round(8+6*math.sin(a)),round(8-6*math.cos(a))),fill=(66,68,61))
 digits={'1':['01','01','01'],'2':['11','01','10'],'3':['11','01','11'],'6':['10','11','11'],'9':['11','11','01']}
 for text,x,y in [('12',6,2),('3',12,7),('6',7,12),('9',2,7)]:
  for k,ch in enumerate(text):
   for yy,row in enumerate(digits[ch]):
    for xx,pixel in enumerate(row):
     if pixel=='1':draw.point((x+k*2+xx,y+yy),fill=(63,65,58))
 draw.line((8,8,11,5),fill=(32,34,29));draw.line((8,8,6,6),fill=(32,34,29));draw.point((8,8),fill=(21,23,20))
 clock.save(A/'textures/block/school_clock_face.png')
 (A/'models/block/school_decorations.mtl').write_text(''.join(f'newmtl {mat}\nKd 1 1 1\nd 1\nmap_Kd #surface\n' for mat in UV)+'newmtl clock\nKd 1 1 1\nd 1\nmap_Kd #clock_face\n')
 m=Mesh();m.disk(8,8,7,0,1.75,'rim');m.ring(8,8,7,.65,1.75,'rim');m.disk(8,8,6.35,1.75,2,'rim',front='clock');m.save('school_clock')
 m=Mesh();m.box(6,1,0,10,5,1,'metal');m.box(7,3,1,9,8,3,'metal');m.disk(8,8,2,1,4,'hub')
 blade=[(8,8),(9,9),(10,12),(9,14),(7,14),(6.5,12),(7,10)]
 for i in range(3):
  a=i*math.tau/3;poly=[(8+(x-8)*math.cos(a)-(y-8)*math.sin(a),8+(x-8)*math.sin(a)+(y-8)*math.cos(a)) for x,y in blade];m.prism(poly,3.8,4.3,'blade')
 for r in (6.5,5,3.5,2):m.ring(8,8,r,.2,5.5,'metal')
 m.ring(8,8,6.5,.3,3,'metal')
 for i in range(8):
  a=i*math.tau/8;dx,dy=math.cos(a),math.sin(a);px,py=-dy*.08,dx*.08
  m.prism([(8-px,8-py),(8+6.5*dx-px,8+6.5*dy-py),(8+6.5*dx+px,8+6.5*dy+py),(8+px,8+py)],5.55,5.75,'metal')
 m.disk(8,8,1.2,4.3,6,'hub');m.save('school_fan')
 for lit in (False,True):
  name='ceiling_light_'+('on' if lit else 'off');m=Mesh();m.disk(8,8,6.5,14.2,16,'body',axis='y');m.disk(8,8,6,13.5,14.2,'on' if lit else 'off',axis='y');m.save(name);write(A/f'models/block/{name}.json',model(name,lit))
 for name in ('school_clock','school_fan'):
  write(A/f'models/block/{name}.json',model(name));write(A/f'blockstates/{name}.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/'+name,'y':turn} for f,turn in [('north',0),('east',90),('south',180),('west',270)]}})
 write(A/'blockstates/ceiling_light.json',{'variants':{'lit=true':{'model':'lsmmod:block/ceiling_light_on'},'lit=false':{'model':'lsmmod:block/ceiling_light_off'}}})
 for ident in ('school_clock','school_fan','ceiling_light'):
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+('ceiling_light_on' if ident=='ceiling_light' else ident),'display':{'gui':{'rotation':[20,160,0] if ident!='ceiling_light' else [70,20,0],'scale':[.9,.9,.9]}}})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
 for locale,names in [('es_es',('Reloj del colegio','Ventilador del colegio','Lámpara de techo')),('en_us',('School Clock','School Fan','Ceiling Light'))]:
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text())
  for ident,name in zip(('school_clock','school_fan','ceiling_light'),names):v['block.lsmmod.'+ident]=name
  write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:school_clock','lsmmod:school_fan','lsmmod:ceiling_light']));write(p,v)
if __name__=='__main__':main()
