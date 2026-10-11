"""Validate every migrated model and blockstate against original resources, without Java."""
import json,tempfile
from pathlib import Path
from optimize_model_resources import ROOT,prepare,replace,model_identifier
A=ROOT/'src/main/resources/assets/lsmmod'
def read(p):return json.loads(p.read_text(encoding='utf-8-sig'))
def resolve(base,ident,aliases,trail=()):
 ident=aliases.get(ident,ident)
 if not ident.startswith('lsmmod:'):return {'external_parent':ident}
 assert ident not in trail, ('cycle',ident)
 p=base/'models'/f'{ident.split(":",1)[1]}.json';assert p.exists(),p
 d=read(p)
 if base==A:
  from repair_door_glazing_faces import eligible,repair
  if eligible(p):d=repair(d)
 parent=d.pop('parent',None)
 out=resolve(base,parent,aliases,trail+(ident,)) if parent else {}
 if d.get('loader')=='fusion:model' and d.get('type')=='base':d.pop('type');d.pop('loader')
 for k,v in d.items():
  if k in ('textures','display'):out[k]={**out.get(k,{}),**v}
  else:out[k]=v
 return out
def main():
 from pathlib import PureWindowsPath
 assert model_identifier(PureWindowsPath('C:/assets/models/block/nested/panel.json'),PureWindowsPath('C:/assets/models'))=='lsmmod:block/nested/panel'
 with tempfile.TemporaryDirectory() as t:
  output=Path(t)/'resources';output.mkdir();report,aliases=prepare(output);B=output/'assets/lsmmod'
  for p in (A/'models').rglob('*.json'):
   ident=model_identifier(p,A/'models')
   assert replace(resolve(A,ident,{}),aliases)==resolve(B,ident,aliases),ident
  from prepare_high_state_models import TARGETS,PAVEMENT
  from check_high_state_models import verify
  high_count=verify(output,aliases)
  count=0
  for p in (A/'blockstates').glob('*.json'):
   old=replace(read(p),aliases);new=read(B/'blockstates'/p.name)
   if p.stem in set(TARGETS)|set(PAVEMENT):continue
   if 'variants' in old:
    selectors=[(dict(x.split('=',1) for x in k.split(',') if x),v) for k,v in new['variants'].items()]
    for key,value in old['variants'].items():
     props=dict(x.split('=',1) for x in key.split(',') if x)
     matches=[v for selector,v in selectors if all(props.get(k)==v for k,v in selector.items())]
     assert matches==[value], (p.name,key)
     count+=1
   else:assert old==new,p
  for folder in ('items',):
   for p in (A/folder).rglob('*.json'):assert replace(read(p),aliases)==read(B/folder/p.relative_to(A/folder)),p
  assert report['identical_models_shared']>0 and report['fusion_models']>0 and report['neoforge_obj_models']>0
  assert report['variants_after']<report['variants_before'] and report['state_properties_changed'] and len(report['high_state_models']['blocks'])==len(TARGETS)+1
  # Dedicated-server metadata never requires the client model library.
  metadata=(ROOT/'src/main/templates/META-INF/neoforge.mods.toml').read_text(encoding='utf-8').split('modId="fusion"')[1]
  assert 'type="required"' in metadata and 'side="CLIENT"' in metadata and 'versionRange="[1.3.16]"' in metadata
  print('OK: every model preserves geometry, UV, textures and transforms except the explicit door glass-cap/cullface repair;',high_count,'high-state selections,',count,'other variant selectors and all item references preserved.')
  print(json.dumps(report))
if __name__=='__main__':main()
