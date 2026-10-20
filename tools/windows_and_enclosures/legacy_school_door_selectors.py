"""Read legacy door selectors for historical generators; no resource optimization."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[2];A=ROOT/'src/main/resources/assets/lsmmod'
PROPERTIES=('frame_layout','top_connected','bottom_connected')
def read(p):return json.loads(module_read_text(p, encoding='utf-8'))
def key(state):return ','.join(f'{k}={v}' for k,v in sorted(state.items()))
def expand_variants(definition):
    """Recover all legacy selectors for generators and equivalence checks, never runtime states."""
    expanded={}
    for selector,apply in definition['variants'].items():
        state=dict(p.split('=',1) for p in selector.split(','))
        if any(p in state for p in PROPERTIES):expanded[selector]=apply;continue
        model=read(A/f"models/{apply['model'].split(':')[1]}.json")
        entries=model['models'][0] if model.get('loader')=='fusion:model' and model.get('type')=='composite' else [
            {'model':apply['model'],'condition':{'mask':mask}} for mask in range(32)]
        for entry in entries:
            mask=entry['condition']['mask']
            legacy=dict(state,frame_layout=str(mask&7),top_connected=str(bool(mask&8)).lower(),bottom_connected=str(bool(mask&16)).lower())
            expanded[key(legacy)]=dict(apply,model=entry['model'])
    return expanded
