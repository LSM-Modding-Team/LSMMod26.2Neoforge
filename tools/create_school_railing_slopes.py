"""Four interrupted and six independently placed smooth railing slopes."""
import json,math
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod'
FLIGHT_RISE=4
def flight_height(column):return FLIGHT_RISE*min(1,max(0,(column-.5)/5))
def flight_halves(column):return flight_height(column+.5)-flight_height(column),flight_height(column+1)-flight_height(column+.5)
def pieces():
 for sequence,i in enumerate((0,1,5,6),1):
  row=math.ceil(5*i/7);yield f'school_railing_slope_split_{sequence}',i,row,.75+5*i/7-row,5/7,i%2==0
 for i in range(6):
  h=flight_height(i);row=math.floor(h+.75)
  yield f'school_railing_slope_flight_{i+1}',i,row,.75+h-row,flight_height(i+1)-h,i%2==0

def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,indent=2,ensure_ascii=False)+'\n')
def obj(low,rise,post,band=False):
 vertices=[];uv=[];faces=[]
 def prism(x0,x1,t0,t1,b0,b1,h0,h1,material):
  # Counterclockwise travel/height profile, extruded in X.
  p=[(t0,b0),(t1,b1),(t1,h1),(t0,h0)]
  left=[(x0,y,1-t) for t,y in p];right=[(x1,y,1-t) for t,y in p]
  polys=[left,right[::-1]]
  for i,(t,y) in enumerate(p):
   u,v=p[(i+1)%4];polys.append([(x0,y,1-t),(x1,y,1-t),(x1,v,1-u),(x0,v,1-u)])
  for points in polys:
   points=points[::-1];indices=[]
   for x,y,z in points:
    vertices.append((x,y,z))
    if all(q[0]==points[0][0] for q in points):
     travel=1-z;ratio=(travel-t0)/(t1-t0)
     bottom=b0+(b1-b0)*ratio;top=h0+(h1-h0)*ratio
     uv.append((ratio,(y-bottom)/(top-bottom)))
    else:uv.append((x,z))
    indices.append(len(vertices))
   faces.append((material,indices))
 prism(0,1,0,1,low-.75 if band else 0,low+rise-.75 if band else 0,low,low+rise,'wall')
 # Parallel inclined square handrail, continuous at each shared endpoint.
 prism(7/16,9/16,0,1,low+6/16,low+rise+6/16,low+8/16,low+rise+8/16,'metal')
 if post:
  # Exact inclined contact under the rail, no gap or intersection with its surface.
  prism(7/16,9/16,7/16,9/16,low+rise*7/16,low+rise*9/16,low+rise*7/16+6/16,low+rise*9/16+6/16,'metal')
 out=['mtllib school_railing_slopes.mtl']
 out+=['v '+' '.join(f'{n:.12g}' for n in p) for p in vertices];out+=['vt '+' '.join(f'{n:.12g}' for n in p) for p in uv]
 for material,indices in faces:out+=['usemtl '+material,'f '+' '.join(f'{i}/{i}' for i in indices)]
 return '\n'.join(out)+'\n'
def flight_obj(low,first,second,post):
 vertices=[];uvs=[];faces=[]
 def height(t):return low+(2*t*first if t<=.5 else first+2*(t-.5)*second)
 def beam(x0,x1,t0,t1,bottom,top,material):
  b0,b1=bottom(t0),bottom(t1);h0,h1=top(t0),top(t1)
  profile=[(t0,b0),(t1,b1),(t1,h1),(t0,h0)]
  left=[(x0,y,1-t) for t,y in profile];right=[(x1,y,1-t) for t,y in profile]
  polys=[left,right[::-1]]
  for i,(t,y) in enumerate(profile):
   # Omit internal end faces at the mid-piece join.
   if i==1 and t1==.5 or i==3 and t0==.5:continue
   u,v=profile[(i+1)%4];polys.append([(x0,y,1-t),(x1,y,1-t),(x1,v,1-u),(x0,v,1-u)])
  for points in polys:
   indices=[]
   for x,y,z in points[::-1]:
    travel=1-z;vertices.append((x,y,z));indices.append(len(vertices))
    if all(p[0]==points[0][0] for p in points):
     u=travel;v=(y-bottom(travel))/(top(travel)-bottom(travel))
    else:u=x;v=travel
    uvs.append((min(1,max(0,u)),min(1,max(0,v))))
   faces.append((material,indices))
 for t0,t1 in ((0,.5),(.5,1)):
  beam(0,1,t0,t1,lambda t:height(t)-.75,height,'wall')
  beam(7/16,9/16,t0,t1,lambda t:height(t)+6/16,lambda t:height(t)+8/16,'metal')
 if post:
  for t0,t1 in ((7/16,.5),(.5,9/16)):
   beam(7/16,9/16,t0,t1,height,lambda t:height(t)+6/16,'metal')
 out=['# Rebuilt flight: level end transitions and a constant-thickness diagonal band.','mtllib school_railing_slopes.mtl']
 out+=['v '+' '.join(f'{n:.12g}' for n in p) for p in vertices]
 out+=['vt '+' '.join(f'{n:.12g}' for n in p) for p in uvs]
 for material,indices in faces:out+=['usemtl '+material,'f '+' '.join(f'{i}/{i}' for i in indices)]
 return '\n'.join(out)+'\n'

def main():
 (A/'models/block/school_railing_slopes.mtl').write_text('newmtl wall\nKd 1 1 1\nd 1\nmap_Kd #wall\nnewmtl metal\nKd 1 1 1\nd 1\nmap_Kd #metal\n')
 registrations='    // BEGIN SCHOOL RAILING SLOPES\n';items=registrations
 for ident,column,row,low,rise,post in pieces():
  band='_flight_' in ident
  halves=flight_halves(column) if band else (rise/2,rise/2)
  (A/f'models/block/{ident}.obj').write_text(flight_obj(low,*halves,post) if band else obj(low,rise,post,False))
  write(A/f'models/block/{ident}.json',{'loader':'neoforge:obj','model':f'lsmmod:models/block/{ident}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'wall':'lsmmod:block/light_school_wall','metal':'lsmmod:block/school_gate_edge','particle':'lsmmod:block/light_school_wall'}})
  write(A/f'blockstates/{ident}.json',{'variants':{f'facing={face}':{'model':'lsmmod:block/'+ident,'y':angle} for face,angle in (('north',0),('east',90),('south',180),('west',270))}})
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
  registrations+=f'    public static final DeferredBlock<SchoolRailingSlopeBlock> {ident.upper()} = BLOCKS.registerBlock(\n            "{ident}", props -> new SchoolRailingSlopeBlock({low:.15g},{halves[0]:.15g},{halves[1]:.15g},{str(post).lower()},{str(band).lower()},props),\n            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());\n'
  items+=f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});\n'
 for name,section in (('ModBlocks.java',registrations),('ModItems.java',items)):
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/name;s=p.read_text();begin='    // BEGIN SCHOOL RAILING SLOPES';end='    // END SCHOOL RAILING SLOPES';section+=end+'\n'
  if begin in s:a=s.index(begin);b=s.index(end)+len(end)+1;s=s[:a]+section+s[b:]
  else:anchor=s.index('    public static final DeferredBlock<' if name=='ModBlocks.java' else '    public static final DeferredItem<');s=s[:anchor]+section+'\n'+s[anchor:]
  p.write_text(s)
 for locale in ('es_es','en_us'):
  p=A/f'lang/{locale}.json';v=json.loads(p.read_text())
  for ident,*_ in pieces():
   family='tramo interrumpido' if '_split_' in ident else 'tramo de 6 bloques y 5 niveles';number=ident.rsplit('_',1)[1]
   v['block.lsmmod.'+ident]=f'Pendiente con baranda: {family} {number}' if locale=='es_es' else f'Railing slope: {"split" if "_split_" in ident else "6 blocks / 5 levels"} {number}'
  write(p,v)
 p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+x[0] for x in pieces()]));write(p,v)
 write(ROOT/'tools/school_railing_slope_layout.json',{'split':{'length':7,'rise':5,'pillar_columns':[2,3,4]},'flight':{'length':6,'rise':FLIGHT_RISE,'wall_thickness':.75,'level_end_length':.5},'pieces':[{'id':ident,'column':col,'row':row,'low':low,'rise':rise,'post':post,'half_rises':list(flight_halves(col)) if '_flight_' in ident else [rise/2,rise/2]} for ident,col,row,low,rise,post in pieces()]})
if __name__=='__main__':main()
