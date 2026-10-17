"""Smooth 6-piece entrance ramp and vanilla slabs with geometric 3px perimeter."""
from resource_modules import module_read_text, module_write_text
import itertools,json,math
from create_school_yellow_border_assets import ROOT,A,D,SIDES,write

# Clip convex face polygons by distance to each unconnected perimeter edge.
def clip(poly,fn,inside):
 out=[]
 for a,b in zip(poly,poly[1:]+poly[:1]):
  da,db=fn(a),fn(b);ka=(da>=-1e-10) if inside else (da<=1e-10);kb=(db>=-1e-10) if inside else (db<=1e-10)
  if ka:out.append(a)
  if ka!=kb:
   q=da/(da-db);out.append(tuple(x+(y-x)*q for x,y in zip(a,b)))
 return out

def obj(low,rise,base,linked):
 # t increases toward north. Flat bottom; top profile is low+rise*t.
 h=lambda p:low+rise*(1-p[2])
 faces=[
  ([(0,base,1),(1,base,1),(1,low,1),(0,low,1)], [('west',lambda p:p[0]),('east',lambda p:1-p[0]),('down',lambda p:p[1]-base),('up',lambda p:h(p)-p[1])]),
  ([(1,base,0),(0,base,0),(0,low+rise,0),(1,low+rise,0)],[('west',lambda p:p[0]),('east',lambda p:1-p[0]),('down',lambda p:p[1]-base),('up',lambda p:h(p)-p[1])]),
  ([(0,base,0),(0,base,1),(0,low,1),(0,low+rise,0)],[('north',lambda p:p[2]),('south',lambda p:1-p[2]),('down',lambda p:p[1]-base),('up',lambda p:h(p)-p[1])]),
  ([(1,base,1),(1,base,0),(1,low+rise,0),(1,low,1)],[('north',lambda p:p[2]),('south',lambda p:1-p[2]),('down',lambda p:p[1]-base),('up',lambda p:h(p)-p[1])]),
  ([(0,low,1),(1,low,1),(1,low+rise,0),(0,low+rise,0)],[('west',lambda p:p[0]),('east',lambda p:1-p[0]),('north',lambda p:p[2]*math.sqrt(1+rise*rise)),('south',lambda p:(1-p[2])*math.sqrt(1+rise*rise))]),
  ([(0,base,0),(1,base,0),(1,base,1),(0,base,1)],[('west',lambda p:p[0]),('east',lambda p:1-p[0]),('north',lambda p:p[2]),('south',lambda p:1-p[2])])]
 out=['mtllib school_entrance_pavement_shapes.mtl'];index=0
 for poly,edges in faces:
  regions=[];gray=poly
  for side,dist in edges:
   if linked[side]:continue
   fn=lambda p,dist=dist:dist(p)-3/16
   yellow=clip(gray,fn,False)
   if len(yellow)>=3:regions.append((yellow,'yellow'))
   gray=clip(gray,fn,True)
   if len(gray)<3:break
  if len(gray)>=3:regions.append((gray,'gray'))
  for poly,material in regions:
   # OBJ quads are fan-triangulated explicitly for clipped polygons of any length.
   for i in range(1,len(poly)-1):
    a,b,c=poly[0],poly[i],poly[i+1]
    ab=[v-u for u,v in zip(a,b)];ac=[v-u for u,v in zip(a,c)]
    cross=[ab[1]*ac[2]-ab[2]*ac[1],ab[2]*ac[0]-ab[0]*ac[2],ab[0]*ac[1]-ab[1]*ac[0]]
    if sum(v*v for v in cross)<1e-20:continue
    ids=[]
    for x,y,z in (a,b,c):
     index+=1;out.append(f'v {x:.12g} {y:.12g} {z:.12g}');out.append('vt 0.01 0.01' if material=='yellow' else f'vt {x:.12g} {z:.12g}');ids.append(index)
    out+=['usemtl '+material,'f '+' '.join(f'{i}/{i}' for i in ids)]
 return '\n'.join(out)+'\n'

def model(name):return {'loader':'neoforge:obj','model':f'lsmmod:models/block/{name}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'gray':'lsmmod:block/school_entrance_pavement_15','yellow':'lsmmod:block/school_entrance_pavement_0','particle':'lsmmod:block/school_entrance_pavement_15'}}

def main():
 # Plain slot names work in vanilla TextureSlots and Fusion 1.3.16's adapter.
 # Its getMaterial override does not strip the optional leading #.
 module_write_text(A/'models/block/school_entrance_pavement_shapes.mtl', 'newmtl gray\nKd 1 1 1\nd 1\nmap_Kd gray\nnewmtl yellow\nKd 1 1 1\nd 1\nmap_Kd yellow\n')
 blocks='    // BEGIN ENTRANCE PAVEMENT SHAPES\n';items=blocks
 families=[(f'school_entrance_pavement_slope_{i+1}',low,.25,0) for i,low in enumerate((.5,.75,0,.25,.5,.75))]+[('school_entrance_pavement_slab',.5,0,0)]
 for ident,low,rise,base in families:
  slope='_slope_' in ident;states={}
  for flags in itertools.product((False,True),repeat=6):
   linked=dict(zip(SIDES,flags));mask=sum(int(v)<<i for i,v in enumerate(flags));key=','.join(f'{s}={str(linked[s]).lower()}' for s in SIDES)
   for kind in (('slope',) if slope else ('bottom','top','double')):
    name=f'{ident}_{kind}_{mask}'
    if kind=='double':ref=f'school_entrance_pavement_{mask}'
    else:
     ref=name;lo=low if slope or kind=='bottom' else 1;bottom=0 if slope or kind=='bottom' else .5
     module_write_text(A/f'models/block/{name}.obj', obj(lo,rise,bottom,linked));write(A/f'models/block/{name}.json',model(name))
    if slope:
     # Flags in models are local; rotate both model and connection key into world space.
     horizontal=['north','east','south','west']
     for facing,turn in [('north',0),('east',1),('south',2),('west',3)]:
      world={**linked,**{horizontal[(i+turn)%4]:linked[s] for i,s in enumerate(horizontal)}}
      worldkey=','.join(f'{s}={str(world[s]).lower()}' for s in SIDES)
      states[worldkey+',facing='+facing]={'model':'lsmmod:block/'+ref,'y':turn*90}
    else:states[key+',type='+kind]={'model':'lsmmod:block/'+ref}
  write(A/f'blockstates/{ident}.json',{'variants':states})
  write(A/f'models/item/{ident}.json',{'parent':f'lsmmod:block/{ident}_{"slope" if slope else "bottom"}_0'})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  entry={'type':'minecraft:item','name':'lsmmod:'+ident}
  if not slope:entry['functions']=[{'function':'minecraft:set_count','count':2,'conditions':[{'condition':'minecraft:block_state_property','block':'lsmmod:'+ident,'properties':{'type':'double'}}]}]
  write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[entry]}]})
  cls='EntrancePavementSlopeBlock' if slope else 'EntrancePavementSlabBlock';factory=f'props -> new {cls}({low},props)' if slope else cls+'::new'
  blocks+=f'    public static final DeferredBlock<{cls}> {ident.upper()} = BLOCKS.registerBlock("{ident}", {factory}, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());\n'
  items+=f'    public static final DeferredItem<BlockItem> {ident.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{ident.upper()});\n'
 for name,section in [('ModBlocks.java',blocks),('ModItems.java',items)]:
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/name;s=module_read_text(p);begin='    // BEGIN ENTRANCE PAVEMENT SHAPES';end='    // END ENTRANCE PAVEMENT SHAPES';section+=end+'\n'
  if begin in s:a=s.index(begin);b=s.index(end)+len(end)+1;s=s[:a]+section+s[b:]
  else:a=s.index('    public static final DeferredBlock<' if name=='ModBlocks.java' else '    public static final DeferredItem<');s=s[:a]+section+'\n'+s[a:]
  module_write_text(p, s)
 for locale in ('es_es','en_us'):
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p))
  for ident,*_ in families:v['block.lsmmod.'+ident]=('Losa del pavimento de entrada' if '_slab' in ident else 'Pendiente del pavimento de entrada '+ident[-1]) if locale=='es_es' else ('Entrance Pavement Slab' if '_slab' in ident else 'Entrance Pavement Slope '+ident[-1])
  write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+x[0] for x in families]));write(p,v)
 write(ROOT/'tools/entrance_pavement_slope_layout.json',{'length':6,'width':6,'start_y_pixels':8,'end_y_pixels':32,'rise_pixels':24,'pieces':[{'id':ident,'column':i,'row':row,'lower_y':low,'rise':.25} for i,(ident,low,row) in enumerate([(f'school_entrance_pavement_slope_{i+1}',lo,row) for i,(lo,row) in enumerate(zip((.5,.75,0,.25,.5,.75),(0,0,1,1,1,1)))])]})
if __name__=='__main__':main()
