"""Lossless resource preparation: shared models, Fusion opaque geometry, compact selectors.

Source IDs and block-state properties never change. Writes only to an explicit output directory.
"""
import argparse,copy,hashlib,json,shutil
from collections import defaultdict
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def canonical(v):return json.dumps(v,sort_keys=True,separators=(',',':'),ensure_ascii=False)
def write(p,v):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(v,ensure_ascii=False,separators=(',',':'))+'\n',encoding='utf-8')
def model_identifier(path,models):return 'lsmmod:'+path.relative_to(models).with_suffix('').as_posix()
def replace(v,aliases):
 if isinstance(v,str):return aliases.get(v,v)
 if isinstance(v,list):return [replace(x,aliases) for x in v]
 if isinstance(v,dict):return {k:replace(x,aliases) for k,x in v.items()}
 return v
def compact(variants):
 current={tuple(sorted(tuple(t.split('=',1)) for t in key.split(',') if t)):value for key,value in variants.items()}
 # Properties affect game logic, but not necessarily rendering: retain all visual dependencies.
 for prop in ('powered','waterlogged'):
  if not current or any(prop not in dict(key) for key in current):continue
  domain={dict(key)[prop] for key in current};groups=defaultdict(list)
  for key,value in current.items():groups[tuple((k,v) for k,v in key if k!=prop)].append((dict(key)[prop],value))
  if all({p for p,v in items}==domain and len({canonical(v) for p,v in items})==1 for items in groups.values()):current={key:items[0][1] for key,items in groups.items()}
 return {','.join(k+'='+v for k,v in key):value for key,value in current.items()}
def prepare(output):
 output=output.resolve()
 if output in (ROOT,ROOT/'src',ROOT/'src/main/resources') or ROOT/'src' in output.parents:raise ValueError('Output cannot overwrite sources')
 if output.exists():
  owned=(output/'.lsmmod-optimized-resources').exists()
  expected=output==(ROOT/'build/generated/optimizedResources').resolve()
  empty=output.is_dir() and not any(output.iterdir())
  if not (owned or expected or empty):raise ValueError('Refusing to clear an unowned output directory')
  shutil.rmtree(output)
 output.mkdir(parents=True);(output/'.lsmmod-optimized-resources').touch()
 for base in (ROOT/'src/main/resources',ROOT/'src/generated/resources'):
  if not base.exists():continue
  for p in base.rglob('*'):
   if p.is_file() and p.suffix!='.bbmodel' and p.name!='.gitkeep' and '.cache' not in p.parts:
    dest=output/p.relative_to(base);dest.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,dest)
 assets=output/'assets/lsmmod';models=assets/'models';before=list(models.rglob('*.json'));raw_bytes=sum(p.stat().st_size for p in before)
 from repair_door_glazing_faces import prepare as repair_door_faces
 door_faces=repair_door_faces(assets)
 # Reuse parsed models through deduplication, reference repair and parent sharing.
 documents={p:json.loads(p.read_text(encoding='utf-8-sig')) for p in sorted(models.rglob('*.json'))}
 groups=defaultdict(list)
 for p,d in documents.items():groups[canonical(d)].append(p)
 aliases={}
 for paths in groups.values():
  keep=min(paths,key=lambda p:(len(str(p)),str(p)));ident=model_identifier(keep,models)
  for p in paths:
   if p!=keep:aliases[model_identifier(p,models)]=ident;p.unlink();del documents[p]
 # Replace exact duplicates in every reference, including model parents and modern item definitions.
 for p in assets.rglob('*.json'):
  d=documents[p] if p in documents else json.loads(p.read_text(encoding='utf-8-sig'));new=replace(d,aliases)
  if p in documents:documents[p]=new
  if new!=d:write(p,new)
 # Use Fusion only for opaque, explicit cuboids: transparency and OBJ rendering stay on NeoForge.
 shared=defaultdict(list)
 for p,d in documents.items():
  if models/'block' not in p.parents:continue
  if 'loader' not in d and d.get('render_type')=='minecraft:solid' and d.get('elements'):
   shared[canonical({'elements':d['elements'],'parent':d.get('parent','minecraft:block/block')})].append((p,d))
 fusion_count=0
 for geometry,items in shared.items():
  if len(items)==1:
   p,d=items[0];d['loader']='fusion:model';d['type']='base';write(p,d);fusion_count+=1
   continue
  digest=hashlib.sha256(geometry.encode()).hexdigest()[:20];ident='lsmmod:block/fusion_shared/'+digest
  write(models/f'block/fusion_shared/{digest}.json',{'loader':'fusion:model','type':'base','parent':json.loads(geometry)['parent'],'elements':json.loads(geometry)['elements']})
  for p,d in items:
   # Child textures override shared material names; inherited positions, faces and UVs are unchanged.
   d.pop('elements');d['parent']=ident;d['loader']='fusion:model';d['type']='base';write(p,d);fusion_count+=1
 variants_before=variants_after=0
 for p in (assets/'blockstates').glob('*.json'):
  d=json.loads(p.read_text(encoding='utf-8-sig'))
  if 'variants' in d:
   variants_before+=len(d['variants']);d['variants']=compact(d['variants']);variants_after+=len(d['variants']);write(p,d)
 from prepare_high_state_models import prepare as prepare_high_states
 high_states=prepare_high_states(assets)
 after=list(models.rglob('*.json'));report={'models_before':len(before),'models_after':len(after),'identical_models_shared':len(aliases),'fusion_models':fusion_count,'variants_before':variants_before,'variants_after':variants_after,'model_bytes_before':raw_bytes,'model_bytes_after':sum(p.stat().st_size for p in after),'state_properties_changed':False,'neoforge_obj_models':sum(json.loads(p.read_text(encoding='utf-8-sig')).get('loader')=='neoforge:obj' for p in after)}
 report['variants_after']=sum(len(json.loads(p.read_text(encoding='utf-8-sig')).get('variants',{})) for p in (assets/'blockstates').glob('*.json'))
 report['high_state_models']=high_states
 report['door_glazing_faces']=door_faces
 report['state_properties_changed']=True
 write(output/'lsmmod_model_optimization.json',report)
 return report,aliases
if __name__=='__main__':
 parser=argparse.ArgumentParser();parser.add_argument('--output',type=Path,required=True);args=parser.parse_args();report,_=prepare(args.output);print(json.dumps(report))
