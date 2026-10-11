"""Build-time Fusion model decisions for the nineteen blocks with >=256 original states.

Native model/OBJ files are untouched. Physical state properties remain physical;
only pavement borders and bars panel phase are selected from neighboring blocks.
"""
from collections import defaultdict
from pathlib import Path
import itertools,hashlib,json,math
TARGETS=('elementary_playground_awning','playground_awning','mixed_school_glass','mixed_school_glass_reversed',
 'supported_school_glass','supported_school_glass_reversed','awning_support','school_gate','football_basketball_goal',
 'school_entrance_pavement_slab','street_school_bars','double_school_entrance','light_school_wall_railing',
 *(f'school_entrance_pavement_slope_{i}' for i in range(1,7)))
PAVEMENT=('school_entrance_pavement', 'school_entrance_pavement_slab',*(f'school_entrance_pavement_slope_{i}' for i in range(1,7)))
GLASS=('mixed_school_glass','mixed_school_glass_reversed','supported_school_glass','supported_school_glass_reversed')
BORDER=('north','east','south','west','up','down')
def canonical(data):return json.dumps(data,sort_keys=True,separators=(',',':'),ensure_ascii=False)
def read(path):return json.loads(path.read_text(encoding='utf-8-sig'))
def write(path,data):path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,separators=(',',':'),ensure_ascii=False)+'\n',encoding='utf-8')
def key(state):return ','.join(f'{k}={v}' for k,v in sorted(state.items()))
def transform(apply):return {k:v for k,v in apply.items() if k!='model'}
def index(state,fields):
 result=0
 for field in fields:result=result*len(field['values'])+field['values'].index(state[field['name']])
 return result
def decision(models,fields,kind='state',start=0,root=True):
 if len(set(models))==1:return models[0]
 middle=len(models)//2
 left=decision(models[:middle],fields,kind,start,False);right=decision(models[middle:],fields,kind,start+middle,False)
 condition={'type':'lsmmod:model_selection','kind':kind,'keys':fields,'min':start,'max':start+middle-1}
 if root and kind!='state':condition['refresh']=True
 return {'type':'composite','models':[[{'model':left,'condition':condition},{'model':right}]]}
class Builder:
 def __init__(self,assets):self.assets=assets;self.written=set()
 def model(self,ident,body):
  data=body if isinstance(body,dict) else {'type':'composite','models':[body]}
  data=dict(data,loader='fusion:model');name=f'block/fusion_high_states/{ident}_{hashlib.sha256(canonical(data).encode()).hexdigest()[:20]}'
  path=self.assets/f'models/{name}.json';write(path,data);self.written.add(path)
  return 'lsmmod:'+name
 def selection(self,ident,entries,kind='state'):
  fields=[{'name':name,'values':sorted({state[name] for state,apply in entries})} for name in sorted(entries[0][0])]
  models=[None]*math.prod(len(f['values']) for f in fields)
  for state,apply in entries:
   offset=index(state,fields);assert models[offset] is None or models[offset]==apply['model'],(ident,state)
   models[offset]=apply['model']
  assert all(m is not None for m in models),(ident,'incomplete state table')
  return self.model(ident,decision(models,fields,kind))
 def variants(self,ident,definition):
  groups=defaultdict(list)
  for selector,apply in definition['variants'].items():groups[canonical(transform(apply))].append((dict(p.split('=',1) for p in selector.split(',') if p),apply))
  result={}
  for turns,entries in groups.items():
   common={name:values[0] for name in entries[0][0] if len(set(values:=[state[name] for state,apply in entries]))==1}
   variable=[({k:v for k,v in state.items() if k not in common},apply) for state,apply in entries]
   kind='pavement' if ident in PAVEMENT else 'state'
   if kind=='pavement':assert set(variable[0][0]) in (set(BORDER),set(BORDER)|{'type'}),(ident,variable[0][0])
   result[key(common)]=dict(json.loads(turns),model=self.selection(ident,variable,kind))
  return {'variants':result}
 def curtains(self,ident,definition):
  groups=defaultdict(list);base=[];seen=set()
  for part in definition['multipart']:
   when=part.get('when',{});apply=part['apply']
   if 'curtain' in when:
    outer={k:v for k,v in when.items() if k!='curtain'}
    groups[canonical({'when':outer,'turns':transform(apply)})].append(({'curtain':when['curtain']},apply))
   else:
    # Both water states use the exact same visual model; retain their gameplay property.
    outer={k:v for k,v in when.items() if k!='waterlogged'};entry={'when':outer,'apply':apply};signature=canonical(entry)
    if signature not in seen:base.append(entry);seen.add(signature)
  empty='lsmmod:block/fusion_high_states/empty_curtain'
  write(self.assets/'models/block/fusion_high_states/empty_curtain.json',{'parent':'minecraft:block/block','textures':{'particle':'lsmmod:block/school_curtain'},'elements':[],'render_type':'minecraft:cutout'})
  for grouping,entries in groups.items():
   descriptor=json.loads(grouping);entries.append(({'curtain':'none'},{'model':empty}))
   model=self.selection(ident,entries)
   base.append({'when':descriptor['when'],'apply':dict(descriptor['turns'],model=model)})
  return {'multipart':base}
 def bars(self,ident,definition):
  groups=defaultdict(list)
  for part in definition['multipart']:
   when=dict(part['when']);profile={'column':when.pop('column'),'row':when.pop('row')}
   groups[canonical({'when':when,'turns':transform(part['apply'])})].append((profile,part['apply']))
  result=[]
  for grouping,entries in groups.items():
   descriptor=json.loads(grouping);model=self.selection(ident,entries,'bars');part={'apply':dict(descriptor['turns'],model=model)}
   if descriptor['when']:part['when']=descriptor['when']
   result.append(part)
  return {'multipart':result}
 def multipart(self,ident,definition):
  groups=defaultdict(list)
  for part in definition['multipart']:
   entry={'model':part['apply']['model']}
   if part.get('when'):entry['condition']={'type':'lsmmod:model_properties','properties':part['when']}
   groups[canonical(transform(part['apply']))].append([entry])
  return {'multipart':[{'apply':dict(json.loads(turns),model=self.model(ident,{'type':'composite','models':series}))} for turns,series in groups.items()]}
def prepare(assets):
 builder=Builder(assets);counts={}
 for ident in (*TARGETS,'school_entrance_pavement'):
  path=assets/f'blockstates/{ident}.json';definition=read(path)
  # Owned generated output is freshly copied by optimize_model_resources; no source changes.
  if any('fusion_high_states/' in canonical(part) for part in definition.values()):raise ValueError('High-state models must be generated from original resources')
  before=len(definition.get('variants',definition.get('multipart')))
  if 'variants' in definition:result=builder.variants(ident,definition)
  elif ident in GLASS:result=builder.curtains(ident,definition)
  elif ident=='street_school_bars':result=builder.bars(ident,definition)
  else:result=builder.multipart(ident,definition)
  after=len(result.get('variants',result.get('multipart')));write(path,result);counts[ident]={'selectors_before':before,'selectors_after':after}
 return {'blocks':counts,'generated_models':len(builder.written)+1,'selectors_before':sum(c['selectors_before'] for c in counts.values()),'selectors_after':sum(c['selectors_after'] for c in counts.values())}
