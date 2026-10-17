"""Remove saved visual connection properties, selecting unchanged door models through Fusion."""
from resource_modules import module_glob, module_read_text, module_unlink, module_write_text
from resource_json import resource_json
from collections import defaultdict
from pathlib import Path
import hashlib,json
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod'
IDS=('tall_classroom_entrance','tall_bathroom_door','tall_teachers_office_door','tall_dining_door','double_school_entrance')
PROPERTIES=('frame_layout','top_connected','bottom_connected')
def read(p):return json.loads(module_read_text(p, encoding='utf-8'))
def write(p,data):p.parent.mkdir(parents=True,exist_ok=True);module_write_text(p, resource_json(p, data, ensure_ascii=False)+'\n',encoding='utf-8')
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

def main(ids=IDS):
    for ident in ids:
        path=A/f'blockstates/{ident}.json';definition=read(path);groups=defaultdict(list);variants={};used=set()
        for selector,apply in definition['variants'].items():
            state=dict(p.split('=',1) for p in selector.split(','))
            if not any(p in state for p in PROPERTIES):
                variants[selector]=apply
                if 'school_door_dynamic/' in apply['model']:used.add(A/f"models/{apply['model'].split(':')[1]}.json")
                continue
            mask=int(state.pop('frame_layout'))|(8 if state.pop('top_connected')=='true' else 0)|(16 if state.pop('bottom_connected')=='true' else 0)
            groups[key(state)].append((mask,apply))
        for selector,entries in groups.items():
            assert len(entries)==32 and {m for m,a in entries}==set(range(32)),(ident,selector)
            transforms=[{k:v for k,v in a.items() if k!='model'} for m,a in entries]
            assert all(t==transforms[0] for t in transforms),(ident,selector,'transform')
            if all(a==entries[0][1] for m,a in entries):variants[selector]=entries[0][1];continue
            series=[{'model':a['model'],'condition':{'type':'lsmmod:glazing_mask','mask':m}} for m,a in sorted(entries)]
            digest=hashlib.sha256(json.dumps(series,sort_keys=True).encode()).hexdigest()[:16]
            name='school_door_dynamic/'+ident+'_'+digest;target=A/f'models/block/{name}.json';used.add(target)
            write(target,{'loader':'fusion:model','type':'composite','models':[series]})
            variants[selector]=dict(transforms[0],model='lsmmod:block/'+name)
        write(path,{'variants':variants})
        for target in module_glob(A/'models/block/school_door_dynamic', ident+'_*.json'):
            if target not in used:module_unlink(target)
if __name__=='__main__':main()
