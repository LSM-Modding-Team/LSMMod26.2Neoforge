"""Read Java property domains without compiling; fail rather than guess unknown declarations."""
import argparse,json,math,re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
J=ROOT/'src/main/java/net/nicomar2009/lsmmod'
FACING={'facing':4}
VANILLA={'Block':{},'DoorBlock':dict(FACING,half=2,hinge=2,open=2,powered=2),
 'StairBlock':dict(FACING,half=2,shape=5,waterlogged=2),'SlabBlock':{'type':3,'waterlogged':2},
 'FenceBlock':dict.fromkeys(('north','east','south','west','waterlogged'),2),
 'IronBarsBlock':dict.fromkeys(('north','east','south','west','waterlogged'),2)}
STANDARD={'HORIZONTAL_FACING':('facing',4),'DOUBLE_BLOCK_HALF':('half',2),'HALF':('half',2),
 'DOOR_HINGE':('hinge',2),'SLAB_TYPE':('type',3),'STAIRS_SHAPE':('shape',5)}
def source(cls):
 p=J/f'block/{cls}.java'
 if not p.exists() and cls.startswith('SportsGoal'):p=J/f'modules/balls/block/{cls}.java'
 if not p.exists():raise ValueError(f'Unknown source class: {cls}')
 return p.read_text(encoding='utf-8')
def parent(cls):return re.search(r'class\s+\w+\s+extends\s+(\w+)',source(cls))[1]
def property_domain(cls,field):
 if '.' in field:cls,field=field.split('.',1)
 s=source(cls)
 m=re.search(r'(?:public|protected|private)\s+static\s+final\s+(\w+(?:<[^>]+>)?)\s+'+re.escape(field)+r'\s*=\s*([^;]+);',s)
 if not m:return property_domain(parent(cls),field)
 typ,expr=m.groups()
 m=re.search(r'IntegerProperty.create\("([^"]+)",\s*(\d+),\s*(\d+)\)',expr)
 if m:return m[1],int(m[3])-int(m[2])+1
 m=re.search(r'BooleanProperty.create\("([^"]+)"\)',expr)
 if m:return m[1],2
 m=re.search(r'EnumProperty.create\("([^"]+)",\s*(\w+).class\)',expr)
 if m:
  enum=(J/f'block/{m[2]}.java').read_text();constants=re.findall(r'^\s{4}([A-Z_]+)\(',enum,re.M)
  if not constants:
   declaration=re.search(r'\benum\s+\w+(?:\s+implements\s+[\w.,\s]+)?\s*\{([^;]+);',enum)
   if declaration and re.fullmatch(r'\s*[A-Z_]+(?:\s*,\s*[A-Z_]+)*\s*',declaration[1]):
    constants=re.findall(r'[A-Z_]+',declaration[1])
  if not constants:raise ValueError(f'Unrecognized enum: {m[2]}')
  return m[1],len(constants)
 if expr.startswith('BlockStateProperties.'):
  field=expr.strip().split('.')[1]
  if field in STANDARD:return STANDARD[field]
  if typ=='BooleanProperty':return field.lower(),2
  raise ValueError(f'Unknown vanilla domain: {expr}')
 if re.fullmatch(r'\w+\.\w+',expr.strip()):return property_domain(cls,expr.strip())
 raise ValueError(f'Unrecognized property: {cls}.{field} = {expr}')
def domains(cls):
 if cls in VANILLA:return dict(VANILLA[cls])
 s=source(cls);method=re.search(r'createBlockStateDefinition\([^)]*\)\s*\{',s)
 if not method:return domains(parent(cls))
 start=method.end();depth=1;end=start
 while depth:
  if s[end]=='{':depth+=1
  if s[end]=='}':depth-=1
  end+=1
 body=s[start:end-1];out=domains(parent(cls)) if 'super.createBlockStateDefinition' in body else {}
 if 'for(Direction side:Direction.values())' in body:
  out.update(dict.fromkeys(('north','east','south','west','up','down'),2))
 for args in re.findall(r'builder.add\(([^()]*)\)',body):
  for field in args.split(','):
   key,count=property_domain(cls,field.strip())
   if key in out:raise ValueError(f'Duplicate property: {cls}.{key}')
   out[key]=count
 return out
def audit():
 reg=(J/'registry/ModBlocks.java').read_text(encoding='utf-8') + (J/'modules/balls/registry/ModuleBlocks.java').read_text(encoding='utf-8')
 entries=re.findall(r'DeferredBlock<(\w+)>\s+\w+\s*=\s*BLOCKS.registerBlock\(\s*"([^"]+)"',reg)
 if len(entries)!=len(re.findall(r'=\s*BLOCKS.registerBlock\(',reg)):raise ValueError('Unrecognized block registration')
 result=[]
 for cls,ident in entries:
  props=domains(cls);result.append({'id':ident,'class':cls,'states':math.prod(props.values()),'domains':props})
 return sorted(result,key=lambda r:(-r['states'],r['id']))
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--json',action='store_true');args=p.parse_args();rows=audit()
 if args.json:print(json.dumps({'total_states':sum(r['states'] for r in rows),'blocks':rows},indent=2))
 else:
  print(f"{len(rows)} registered blocks; {sum(r['states'] for r in rows)} states")
  for row in rows:
   if row['states']>=128:print(f"{row['states']:6} {row['id']} "+' × '.join(f'{k}:{v}' for k,v in row['domains'].items()))
