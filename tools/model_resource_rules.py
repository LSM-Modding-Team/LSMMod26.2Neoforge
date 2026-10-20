"""Pure per-resource rules shared by incremental preparation and release optimization."""
import json
from collections import defaultdict


def canonical(value):
 return json.dumps(value,sort_keys=True,separators=(',',':'),ensure_ascii=False)


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
