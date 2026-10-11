"""Exhaustive prepared-model equivalence for the >=256-state families, without Java."""
import itertools,json,math
from collections import Counter,defaultdict
from pathlib import Path
from prepare_high_state_models import TARGETS,PAVEMENT,BORDER
from audit_block_states import audit
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod'
DOMAINS={'facing':['north','east','south','west'],'half':['bottom','top'],'shape':['straight','inner_left','inner_right','outer_left','outer_right'],
 'curtain':['none']+[a+'_'+b for a,b in itertools.product(('single','left','middle','right'),repeat=2)],'type':['bottom','top','double']}
def read(path):return json.loads(path.read_text(encoding='utf-8-sig'))
def when_matches(when,state):
 for name,value in when.items():
  if name=='OR':
   if not any(when_matches(p,state) for p in value):return False
  elif name=='AND':
   if not all(when_matches(p,state) for p in value):return False
  elif state.get(name) not in value.split('|'):return False
 return True
def predicate(condition,state):
 kind=condition['type']
 if kind=='lsmmod:model_properties':return when_matches(condition['properties'],state)
 assert kind=='lsmmod:model_selection',kind
 value=0
 for field in condition['keys']:
  value=value*len(field['values'])+field['values'].index(state[field['name']])
 return any(first<=value<=last for first,last in condition.get('ranges',[[condition['min'],condition['max']]]))
def normalized(apply):return (apply['model'],apply.get('x',0),apply.get('y',0),apply.get('uvlock',False))
class Selector:
 def __init__(self,definition,assets=None):
  self.definition=definition;self.assets=assets;self.models={};self.groups=defaultdict(dict)
  if 'variants' in definition:
   for selector,apply in definition['variants'].items():
    props=dict(p.split('=',1) for p in selector.split(',') if p);keys=tuple(sorted(props))
    values=tuple(props[k] for k in keys);assert values not in self.groups[keys];self.groups[keys][values]=apply
 def model(self,model,state,turns):
  if isinstance(model,str):
   if model.endswith('/empty_curtain'):return []
   if 'block/fusion_high_states/' not in model:return [normalized(dict(turns,model=model))]
   if model not in self.models:self.models[model]=read(self.assets/f'models/{model.split(":")[1]}.json')
   model=self.models[model]
  assert model['type']=='composite';selected=[]
  for series in model['models']:
   entries=series if isinstance(series,list) else [series]
   for entry in entries:
    if isinstance(entry,str):entry={'model':entry}
    if 'condition' not in entry or predicate(entry['condition'],state):
     selected+=self.model(entry['model'],state,turns);break
  return selected
 def select(self,state):
  if self.groups:
   parts=[group.get(tuple(state.get(k) for k in keys)) for keys,group in self.groups.items()];parts=[p for p in parts if p is not None]
   assert len(parts)==1,(state,len(parts))
  else:parts=[p['apply'] for p in self.definition['multipart'] if when_matches(p.get('when',{}),state)]
  if self.assets is None:return Counter(normalized(p) for p in parts)
  return Counter(item for part in parts for item in self.model(part['model'],state,{k:v for k,v in part.items() if k!='model'}))
def state_domains(row):
 domains={}
 bools={'open','powered','waterlogged','upper','post','north','east','south','west','up','down'}
 for name,count in row['domains'].items():
  if name in DOMAINS:values=DOMAINS[name]
  elif name in bools:values=['false','true']
  else:values=[str(i) for i in range(1 if name=='width' else 0,count+(1 if name=='width' else 0))]
  assert len(values)==count,(row,name,count);domains[name]=values
 if row['id'] in PAVEMENT:domains.update({name:['false','true'] for name in BORDER})
 if row['id']=='street_school_bars':domains.update({'column':[str(i) for i in range(4)],'row':[str(i) for i in range(3)]})
 return domains
def verify(output,aliases):
 from optimize_model_resources import replace
 B=output/'assets/lsmmod';rows={r['id']:r for r in audit()};checked=0
 # Fusion eagerly bakes all conditional leaves: identical native IDs must not
 # occur twice in one prepared root, even when runtime conditions differ.
 def leaves(model):
  if isinstance(model,str):return [model]
  return [leaf for series in model['models']
          for entry in (series if isinstance(series,list) else [series])
          for leaf in leaves(entry if isinstance(entry,str) else entry['model'])]
 for path in (B/'models/block/fusion_high_states').glob('*.json'):
  model=read(path)
  if model.get('type')=='composite':
   native=leaves(model);assert len(native)==len(set(native)),(path,'repeated baked geometry')

 for ident in (*TARGETS,'school_entrance_pavement'):
  source=Selector(replace(read(A/f'blockstates/{ident}.json'),aliases));prepared=Selector(read(B/f'blockstates/{ident}.json'),B)
  if ident in PAVEMENT:
   # These roots own the atlas materials used by native OBJ MTL references.
   for application in prepared.definition['variants'].values():
    wrapper=read(B/f'models/{application["model"].split(":")[1]}.json')
    textures=wrapper['textures']
    assert textures=={'gray':'lsmmod:block/school_entrance_pavement_15',
                      'yellow':'lsmmod:block/school_entrance_pavement_0',
                      'particle':'lsmmod:block/school_entrance_pavement_15'}
    for texture in textures.values():
     assert (B/f'textures/{texture.split(":")[1]}.png').is_file(),texture
  domains=state_domains(rows[ident]);keys=list(domains)
  for values in itertools.product(*(domains[k] for k in keys)):
   state=dict(zip(keys,values));expected=source.select(state);actual=prepared.select(state)
   assert expected==actual,(ident,state,expected,actual)
   checked+=1
 # Source geometry and all physical ownership/shape fields remain present for these families.
 J=ROOT/'src/main/java/net/nicomar2009/lsmmod'
 for cls,fields in {'AwningBlock':('START','END','UPPER'),'AwningSupportBlock':('COLUMN','WIDTH'),
                   'SchoolGateBlock':('COLUMN','ROW','DEPTH','OPEN'),'SportsGoalBlock':('CELL',),
                   'SchoolWallRailingBlock':('POST','FACING')}.items():
  java=(J/f'block/{cls}.java').read_text();assert all(f in java for f in fields)
 helper=(J/'block/EntrancePavementConnections.java').read_text()
 assert 'dy=-2;dy<=2' in helper and 'Math.abs(edge(state,side,0)' in helper
 assert 'if(join)mask|=1<<i' in helper and 'hasChunkAt' in helper
 bars=(J/'block/StreetSchoolBarsBlock.java').read_text();assert '(column%4)*3+row%3' in bars and 'REFRESHES' in bars and 'WeakHashMap' in bars
 assert 'COLUMN' not in bars and 'ROW' not in bars
 migration=(J/'block/LegacyCurtainStates.java').read_text()
 assert all('lsmmod:'+ident in migration for ident in PAVEMENT) and 'lsmmod:street_school_bars' in migration
 assert sum(r['states'] for r in rows.values() if r['class']!='MixedSchoolWallBlock')==14844
 return checked
if __name__=='__main__':
 import argparse,tempfile
 from optimize_model_resources import prepare
 p=argparse.ArgumentParser();p.add_argument('--output',type=Path);args=p.parse_args()
 if args.output:
  # Recreate aliases deterministically while preparing the specified owned output.
  report,aliases=prepare(args.output);count=verify(args.output,aliases)
 else:
  with tempfile.TemporaryDirectory() as t:
   output=Path(t)/'resources';report,aliases=prepare(output);count=verify(output,aliases)
 print(f'OK: {count} state/visual-profile combinations select exactly the same native geometry and transforms; 19 target blocks covered, physical ownership preserved; original families retain 14844 states.')
 print(json.dumps(report))
