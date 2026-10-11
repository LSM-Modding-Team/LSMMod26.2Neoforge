"""Validate every migrated model and blockstate against original resources, without Java."""
import json,tempfile
from pathlib import Path
from optimize_model_resources import ROOT,prepare,replace
A=ROOT/'src/main/resources/assets/lsmmod'
def read(p):return json.loads(p.read_text())
def resolve(base,ident,aliases,trail=()):
 ident=aliases.get(ident,ident)
 if not ident.startswith('lsmmod:'):return {'external_parent':ident}
 assert ident not in trail, ('cycle',ident)
 p=base/'models'/f'{ident.split(":",1)[1]}.json';assert p.exists(),p
 d=read(p);parent=d.pop('parent',None)
 out=resolve(base,parent,aliases,trail+(ident,)) if parent else {}
 if d.get('loader')=='fusion:model':assert d.pop('type')=='base';d.pop('loader')
 for k,v in d.items():
  if k in ('textures','display'):out[k]={**out.get(k,{}),**v}
  else:out[k]=v
 return out
def main():
 with tempfile.TemporaryDirectory() as t:
  output=Path(t)/'resources';report,aliases=prepare(output);B=output/'assets/lsmmod'
  for p in (A/'models').rglob('*.json'):
   ident='lsmmod:'+str(p.relative_to(A/'models').with_suffix(''))
   assert resolve(A,ident,{})==resolve(B,ident,aliases),ident
  count=0
  for p in (A/'blockstates').glob('*.json'):
   old=replace(read(p),aliases);new=read(B/'blockstates'/p.name)
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
  assert report['variants_after']<report['variants_before'] and not report['state_properties_changed']
  # Dedicated-server metadata never requires the client model library.
  metadata=(ROOT/'src/main/templates/META-INF/neoforge.mods.toml').read_text().split('modId="fusion"')[1]
  assert 'type="required"' in metadata and 'side="CLIENT"' in metadata and 'versionRange="[1.3.16]"' in metadata
  print('OK: every original model resolves to identical geometry, UV, textures and transforms; all',count,'variant selectors and item references preserved.')
  print(json.dumps(report))
if __name__=='__main__':main()
