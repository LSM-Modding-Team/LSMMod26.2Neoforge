"""One vanilla tiled stair and three reusable, invertible OBJ slope blocks."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
STAIR='classroom_floor_stairs'
SLOPES={'school_slope':0.0,'school_slope_raised_base':.625,'school_slope_raised_tip':-.375}

def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,indent=2,ensure_ascii=False)+'\n')
def polygon(offset):
 poly=[(0,0),(1,0),(1,1+offset),(0,offset)]
 for bound,sign in ((0,1),(1,-1)):
  out=[]
  for i,a in enumerate(poly):
   b=poly[(i+1)%len(poly)];insideA=sign*(a[1]-bound)>=0;insideB=sign*(b[1]-bound)>=0
   if insideA:out.append(a)
   if insideA!=insideB:
    ratio=(bound-a[1])/(b[1]-a[1]);out.append((a[0]+ratio*(b[0]-a[0]),bound))
  poly=out
 changed=True
 while changed and len(poly)>3:
  changed=False
  for i,p in enumerate(poly):
   a=poly[i-1];b=poly[(i+1)%len(poly)]
   cross=(p[0]-a[0])*(b[1]-p[1])-(p[1]-a[1])*(b[0]-p[0])
   if abs(cross)<1e-10:poly.pop(i);changed=True;break
 return poly

def obj(offset,top):
 poly=polygon(offset);verts=[];uvs=[];faces=[]
 def face(points):
  first=len(verts)+1
  # Ordinary profile extrusion has inward winding; Y reflection flips it again.
  indices=list(range(first,first+len(points)));faces.append(indices if top else indices[::-1])
  for x,y,z in points:
   verts.append((x,1-y if top else y,z))
   uvs.append((z,y) if all(p[0]==points[0][0] for p in points) else (x,z))
 left=[(0,y,1-t) for t,y in poly];right=[(1,y,1-t) for t,y in poly]
 for i in range(1,len(poly)-1):
  face([left[0],left[i],left[i+1]]);face([right[0],right[i+1],right[i]])
 for i,a in enumerate(poly):
  b=poly[(i+1)%len(poly)];face([(0,a[1],1-a[0]),(1,a[1],1-a[0]),(1,b[1],1-b[0]),(0,b[1],1-b[0])])
 lines=['# Reusable smooth slope; one block only.','mtllib school_slope.mtl','usemtl wall']
 lines+=['v '+' '.join(f'{n:.12g}' for n in p) for p in verts]
 lines+=['vt '+' '.join(f'{n:.12g}' for n in p) for p in uvs]
 lines+=['f '+' '.join(f'{i}/{i}' for i in f) for f in faces]
 return '\n'.join(lines)+'\n'

def register():
 block=f'    public static final DeferredBlock<SchoolTiledStairBlock> CLASSROOM_FLOOR_STAIRS = BLOCKS.registerBlock(\n            "{STAIR}", SchoolTiledStairBlock::new,\n            props -> props.strength(1.5F).sound(SoundType.STONE));\n'
 item=f'    public static final DeferredItem<BlockItem> CLASSROOM_FLOOR_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_FLOOR_STAIRS);\n'
 for name,offset in SLOPES.items():
  block+=f'\n    public static final DeferredBlock<SchoolSlopeBlock> {name.upper()} = BLOCKS.registerBlock(\n            "{name}", props -> new SchoolSlopeBlock({offset},props),\n            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());\n'
  item+=f'    public static final DeferredItem<BlockItem> {name.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{name.upper()});\n'
 for filename,content in (('ModBlocks.java',block),('ModItems.java',item)):
  p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text();begin='    // BEGIN SCHOOL RISE PIECES\n';end='    // END SCHOOL RISE PIECES\n'
  a=s.index(begin);b=s.index(end)+len(end);s=s[:a]+begin+content+end+s[b:];p.write_text(s)

def main():
 (A/'models/block/school_slope.mtl').write_text('newmtl wall\nKd 1 1 1\nd 1\nmap_Kd #wall\n')
 for suffix,parent in (('','stairs'),('_inner','inner_stairs'),('_outer','outer_stairs')):
  write(A/f'models/block/{STAIR}{suffix}.json',{'parent':'minecraft:block/'+parent,'textures':{k:'lsmmod:block/classroom_floor' for k in ('bottom','top','side')}})
 states=json.loads((ROOT/'tools/vanilla_stair_blockstates.json').read_text())
 for v in states['variants'].values():v['model']=v['model'].replace('minecraft:block/oak_stairs','lsmmod:block/'+STAIR)
 write(A/f'blockstates/{STAIR}.json',states)
 for ident,offset in SLOPES.items():
  states={}
  for top in (False,True):
   suffix='_top' if top else '';name=ident+suffix
   (A/f'models/block/{name}.obj').write_text(obj(offset,top))
   write(A/f'models/block/{name}.json',{'loader':'neoforge:obj','model':f'lsmmod:models/block/{name}.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'emissive_ambient':False,'textures':{'wall':'lsmmod:block/light_school_wall','particle':'lsmmod:block/light_school_wall'}})
   for facing,angle in (('north',0),('east',90),('south',180),('west',270)):
    states[f'facing={facing},half={"top" if top else "bottom"}']={'model':'lsmmod:block/'+name,'y':angle}
  write(A/f'blockstates/{ident}.json',{'variants':states})
 for ident in (STAIR,*SLOPES):
  write(A/f'models/item/{ident}.json',{'parent':'lsmmod:block/'+ident})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
  write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
 for locale,labels in (('es_es',('Escalera de baldosa del colegio','Pendiente del colegio','Pendiente elevada: base','Pendiente elevada: remate')),('en_us',('School Tiled Stairs','School Slope','Raised School Slope: Base','Raised School Slope: Tip'))):
  p=A/f'lang/{locale}.json';data=json.loads(p.read_text());data.update({'block.lsmmod.'+n:v for n,v in zip((STAIR,*SLOPES),labels)});write(p,data)
 for tag,names in (('mineable/pickaxe',(STAIR,*SLOPES)),('stairs',(STAIR,))):
  p=ROOT/f'src/main/resources/data/minecraft/tags/block/{tag}.json';data=json.loads(p.read_text()) if p.exists() else {'values':[]};data['values']=list(dict.fromkeys(data['values']+['lsmmod:'+n for n in names]));write(p,data)
 register()
if __name__=='__main__':main()
