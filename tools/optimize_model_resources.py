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
 # Most tables have neither property. Avoid constructing a second state table.
 if not variants or not {'powered','waterlogged'} & {part.split('=',1)[0] for part in next(iter(variants)).split(',')}:
  return variants
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
 from resource_modules import compose
 from windows_and_enclosures.create_school_projecting_window_assets import prepare_layouts,LAYOUT_IDS
 # These directories are fully regenerated next; copying them only to delete
 # them again needlessly touches thousands of source and destination files.
 compose(output,skip_directories={Path(f'assets/lsmmod/models/block/{ident}/layouts') for ident in LAYOUT_IDS})
 assets=output/'assets/lsmmod'
 prepare_layouts(assets)
 models=assets/'models';before=sorted(models.rglob('*.json'));raw_bytes=sum(p.stat().st_size for p in before)
 from windows_and_enclosures.repair_door_glazing_faces import prepare as repair_door_faces
 door_faces=repair_door_faces(assets)
 # Reuse parsed models through deduplication, reference repair and parent sharing.
 documents={p:json.loads(p.read_text(encoding='utf-8-sig')) for p in before}
 groups=defaultdict(list)
 for p,d in documents.items():groups[canonical(d)].append(p)
 aliases={}
 for paths in groups.values():
  keep=min(paths,key=lambda p:(len(str(p)),str(p)));ident=model_identifier(keep,models)
  for p in paths:
   if p!=keep:aliases[model_identifier(p,models)]=ident;p.unlink();del documents[p]
 del groups
 dirty=set()
 states={}
 dirty_states=set()
 # Replace exact duplicates in every reference, including model parents and modern item definitions.
 for p in assets.rglob('*.json'):
  d=documents[p] if p in documents else json.loads(p.read_text(encoding='utf-8-sig'));new=replace(d,aliases)
  if p.parent==assets/'blockstates':
   states[p]=new
   if new!=d:dirty_states.add(p)
  if p in documents:
   documents[p]=new
   if new!=d:dirty.add(p)
  elif new!=d and p not in states:write(p,new)
 # Use Fusion only for opaque, explicit cuboids: transparency and OBJ rendering stay on NeoForge.
 shared=defaultdict(list)
 for p,d in documents.items():
  if models/'block' not in p.parents:continue
  if 'loader' not in d and d.get('render_type')=='minecraft:solid' and d.get('elements'):
   shared[canonical({'elements':d['elements'],'parent':d.get('parent','minecraft:block/block')})].append((p,d))
 fusion_count=0
 for geometry,items in shared.items():
  if len(items)==1:
   p,d=items[0];d['loader']='fusion:model';d['type']='base';dirty.add(p);fusion_count+=1
   continue
  digest=hashlib.sha256(geometry.encode()).hexdigest()[:20];ident='lsmmod:block/fusion_shared/'+digest
  shape=json.loads(geometry);path=models/f'block/fusion_shared/{digest}.json'
  documents[path]={'loader':'fusion:model','type':'base','parent':shape['parent'],'elements':shape['elements']};dirty.add(path)
  for p,d in items:
   # Child textures override shared material names; inherited positions, faces and UVs are unchanged.
   d.pop('elements');d['parent']=ident;d['loader']='fusion:model';d['type']='base';dirty.add(p);fusion_count+=1
 # A model may need both alias replacement and geometry sharing; serialize once.
 for p in sorted(dirty):write(p,documents[p])
 obj_count=sum(d.get('loader')=='neoforge:obj' for d in documents.values())
 del shared,documents,dirty
 variants_before=variants_after=0
 for p,d in states.items():
  if 'variants' in d:
   variants_before+=len(d['variants']);variants=compact(d['variants']);variants_after+=len(variants)
   if variants!=d['variants']:dirty_states.add(p)
   d['variants']=variants
  if p in dirty_states:write(p,d)
 from prepare_high_state_models import prepare as prepare_high_states
 high_states=prepare_high_states(assets)
 # High-state preparation creates Fusion models only. Its selector counts let
 # the report reuse parsed tables instead of reopening every model and state.
 variants_after+=sum(counts['selectors_after']-counts['selectors_before']
                     for ident,counts in high_states['blocks'].items()
                     if 'variants' in states[assets/f'blockstates/{ident}.json'])
 after=list(models.rglob('*.json'));report={'models_before':len(before),'models_after':len(after),'identical_models_shared':len(aliases),'fusion_models':fusion_count,'variants_before':variants_before,'variants_after':variants_after,'model_bytes_before':raw_bytes,'model_bytes_after':sum(p.stat().st_size for p in after),'state_properties_changed':False,'neoforge_obj_models':obj_count}
 report['high_state_models']=high_states
 report['door_glazing_faces']=door_faces
 report['state_properties_changed']=True
 write(output/'lsmmod_model_optimization.json',report)
 return report,aliases
if __name__=='__main__':
 parser=argparse.ArgumentParser();parser.add_argument('--output',type=Path,required=True);args=parser.parse_args();report,_=prepare(args.output);print(json.dumps(report))
