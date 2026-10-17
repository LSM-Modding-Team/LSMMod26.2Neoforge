"""Three shared raised underside pieces: 48px run, lower edge y=8..56px."""
from resource_modules import module_read_text, module_write_text
from resource_json import resource_json
import json
from pathlib import Path
from create_school_inverted_slopes import inverted_obj
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';J=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry';D=ROOT/'src/main/resources/data'
IDS=[f'school_inverted_slope_short_raised_{i}' for i in range(1,4)]
GENERATED=[]
def write(path,value):
 path.parent.mkdir(parents=True,exist_ok=True)
 module_write_text(path, resource_json(path, value, ensure_ascii=False)+'\n' if not isinstance(value,str) else value)
 GENERATED.append(str(path.relative_to(ROOT)))
def main():
 # Native north: back underside .5b, front underside 1.5b; roof 2b.
 mesh=inverted_obj(.75,-.5)
 lines=[]
 for line in mesh.splitlines():
  fields=line.split()
  if fields and fields[0]=='v':
   x,y,z=map(float,fields[1:]);y=2 if y==1 else y*2+.5
   line=f'v {x:.12g} {y:.12g} {z:.12g}'
  if line.startswith('mtllib '):line='mtllib school_short_raised_slopes.mtl'
  lines.append(line)
 write(A/'models/block/school_short_raised_slopes.obj','\n'.join(lines)+'\n')
 write(A/'models/block/school_short_raised_slopes.mtl','newmtl wall\nKd 1 1 1\nd 1\nmap_Kd #wall\n')
 write(A/'models/block/school_short_raised_slopes.json',{'loader':'neoforge:obj','model':'lsmmod:models/block/school_short_raised_slopes.obj','automatic_culling':False,'flip_v':True,'shade_quads':True,'textures':{'wall':'lsmmod:block/classroom_white_wall','particle':'lsmmod:block/classroom_white_wall'}})
 for ident in IDS:
  write(A/f'models/block/{ident}.json',{'parent':'lsmmod:block/school_short_raised_slopes'})
  write(A/f'blockstates/{ident}.json',{'variants':{f'facing={face}':{'model':'lsmmod:block/'+ident,'y':turn*90} for turn,face in enumerate(('north','east','south','west'))}})
  write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:block/'+ident}})
  write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
 for filename in ('ModBlocks.java','ModItems.java'):
  p=J/filename;s=module_read_text(p);begin='    // BEGIN SHORT RAISED SLOPES';end='    // END SHORT RAISED SLOPES'
  if filename=='ModBlocks.java':body='\n'.join(f'    public static final DeferredBlock<SchoolInvertedSlopeBlock> {i.upper()} = BLOCKS.registerBlock("{i}", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());' for i in IDS)
  else:body='\n'.join(f'    public static final DeferredItem<BlockItem> {i.upper()} = ITEMS.registerSimpleBlockItem(ModBlocks.{i.upper()});' for i in IDS)
  section=begin+'\n'+body+'\n'+end
  if begin in s:a=s.index(begin);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
  else:s=s.replace('    // BEGIN SCHOOL INVERTED SLOPES',section+'\n\n    // BEGIN SCHOOL INVERTED SLOPES',1)
  write(p,s)
 for locale in ('en_us','es_es'):
  p=A/f'lang/{locale}.json';v=json.loads(module_read_text(p))
  for n,ident in enumerate(IDS,1):v['block.lsmmod.'+ident]=(f'Short raised inverted slope {n}' if locale=='en_us' else f'Pendiente invertida elevada corta {n}')
  write(p,v)
 p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(module_read_text(p));v['values']=list(dict.fromkeys(v['values']+['lsmmod:'+i for i in IDS]));write(p,v)
 write(ROOT/'tools/school_short_raised_slope_layout.json',{'length_pixels':48,'height_pixels':64,'underside_start_pixels':8,'underside_end_pixels':56,'placements':{face:[{'id':ident,'offset':[dx*n,n,dz*n]} for n,ident in enumerate(IDS)] for face,dx,dz in [('north',0,-1),('east',1,0),('south',0,1),('west',-1,0)]}})
 module_write_text(Path('/tmp/short-slopes-generated.json'), json.dumps(GENERATED))
if __name__=='__main__':main()
