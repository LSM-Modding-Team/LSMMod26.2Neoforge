from resource_json import resource_json
"""Retire connection properties and encode only the 17 valid curtain layouts.

Fusion chooses existing unmodified geometry by a world-dependent visual mask.
Run after the original glass/stair/curtain generators; safe to run again.
"""
from pathlib import Path
from collections import defaultdict
import json,hashlib
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/lsmmod'
IDS=('school_glass','supported_school_glass','mixed_school_glass','supported_school_glass_reversed','mixed_school_glass_reversed')
CONNECTIONS=('frame_layout','top_connected','bottom_connected')
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(resource_json(p, v, ensure_ascii=False)+'\n',encoding='utf-8')
def main():
 used=set()
 for ident in IDS:
  path=A/f'blockstates/{ident}.json';definition=json.loads(path.read_text(encoding='utf-8'));parts=definition['multipart'];groups=defaultdict(list);overlays=[]
  for part in parts:
   when=dict(part.get('when',{}));apply=part['apply']
   if 'curtain' in when:
    if 'curtain_vertical' in when:when['curtain']=when['curtain']+'_'+when.pop('curtain_vertical')
    overlays.append({'when':when,'apply':apply});continue
   if not any(k in when for k in CONNECTIONS):
    model_path=A/f"models/{apply['model'].split(':')[1]}.json"
    if 'school_glazing_dynamic/' in apply['model']:
     series=json.loads(model_path.read_text(encoding='utf-8'))['models'][0]
     digest=hashlib.sha256(json.dumps(series,sort_keys=True).encode()).hexdigest()[:16]
     name='school_glazing_dynamic/'+ident+'_'+digest
     target=A/f'models/block/{name}.json';write(target,{'loader':'fusion:model','type':'composite','models':[series]});used.add(target)
     part={'when':when,'apply':dict(apply,model='lsmmod:block/'+name)}
    overlays.append(part);continue
   mask=int(when.pop('frame_layout','3'))|(8 if when.pop('top_connected','false')=='true' else 0)|(16 if when.pop('bottom_connected','false')=='true' else 0)
   key=json.dumps(when,sort_keys=True);groups[key].append((mask,apply))
  for key,entries in groups.items():
   assert len(entries)==32 and {m for m,a in entries}==set(range(32)),(ident,key,len(entries))
   turns={(a.get('x',0),a.get('y',0),a.get('uvlock',False)) for m,a in entries};assert len(turns)==1,(ident,key,turns)
   x,y,uvlock=next(iter(turns))
   series=[{'model':a['model'],'condition':{'type':'lsmmod:glazing_mask','mask':m}} for m,a in sorted(entries)]
   digest=hashlib.sha256(json.dumps(series,sort_keys=True).encode()).hexdigest()[:16];name='school_glazing_dynamic/'+ident+'_'+digest
   used.add(A/f'models/block/{name}.json')
   write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','models':[series]})
   apply={'model':'lsmmod:block/'+name}
   if x:apply['x']=x
   if y:apply['y']=y
   if uvlock:apply['uvlock']=uvlock
   overlays.append({'when':json.loads(key),'apply':apply})
  write(path,{'multipart':overlays})
 for path in (A/'models/block/school_glazing_dynamic').glob('*.json'):
  if path not in used and any(path.stem.startswith(ident+'_') for ident in IDS):path.unlink()
if __name__=='__main__':main()
