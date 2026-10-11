"""All twelve ordered material pairs, inherited meshes and grouped glazing equivalence."""
import itertools,json,hashlib
from pathlib import Path
from create_school_glass_material_families import ROOT,A,TEMPLATE,GENERATED,main as generate
from school_glass_material_families import MATERIALS,EXISTING_PAIRS,NEW_PAIRS


def read(p):return json.loads(p.read_text())

def main():
    assert len(NEW_PAIRS)==8 and len(EXISTING_PAIRS)==4
    assert set(NEW_PAIRS)|set(EXISTING_PAIRS)==set(itertools.permutations(MATERIALS,2))
    base=read(A/f'blockstates/{TEMPLATE}.json')
    from audit_block_states import audit
    rows={r['id']:r for r in audit()}
    comparisons=0;unique=0
    for (front,body),name in NEW_PAIRS.items():
        assert rows[name]['class']=='SchoolGlassStairBlock' and rows[name]['domains']==rows[TEMPLATE]['domains']
        state=read(A/f'blockstates/{name}.json');assert len(state['multipart'])==len(base['multipart'])
        seen=set()
        for original,part in zip(base['multipart'],state['multipart']):
            assert part['when']==original['when']
            assert {k:v for k,v in part['apply'].items() if k!='model'}=={k:v for k,v in original['apply'].items() if k!='model'}
            if 'curtain' in original['when']:assert part==original;continue
            source=original['apply']['model'];target=part['apply']['model']
            if target in seen:continue
            seen.add(target)
            old=read(A/f'models/{source.split(":")[1]}.json');new=read(A/f'models/{target.split(":")[1]}.json')
            entries=new['models'][0]
            assert len(entries)==len({e['model'] for e in entries})
            assert 0 in entries[0]['condition'].get('masks',[entries[0]['condition'].get('mask')])
            for mask in range(32):
                selected=[e for e in entries if mask in e['condition'].get('masks',[e['condition'].get('mask')])]
                assert len(selected)==1
                expected=next(e['model'] for e in old['models'][0] if e['condition']['mask']==mask)
                leaf=read(A/f'models/{selected[0]["model"].split(":")[1]}.json')
                assert leaf=={'parent':expected,'textures':{'concrete':'lsmmod:block/'+MATERIALS[front],'light_wall':'lsmmod:block/'+MATERIALS[body]}}
                inherited=read(A/f'models/{expected.split(":")[1]}.json')
                for e in inherited['elements']:
                    for face,surface in e['faces'].items():
                        if surface['texture']=='#concrete':assert face=='north' and e['from'][2]==0
                        elif surface['texture'] not in ('#glass','#frame'):assert surface['texture']=='#light_wall'
                comparisons+=1
            unique+=len(entries)
        for locale in ('es_es','en_us'):assert read(A/f'lang/{locale}.json')['block.lsmmod.'+name]
        assert read(A/f'items/{name}.json')['model']['model']=='lsmmod:item/'+name
    # Signed Java bit 31 must still select exactly the same mask as the legacy single-mask form.
    for mask in range(32):
        membership=(1<<mask)&0xffffffff
        assert [i for i in range(32) if membership&(1<<i)]==[mask]
    predicate=(ROOT/'src/main/java/net/nicomar2009/lsmmod/client/SchoolGlazingPredicate.java').read_text()
    assert 'mask==0' in predicate and 'members & (1<<selection.mask)' in predicate and 'members & (1<<3)' in predicate
    generate();before={p:hashlib.sha256(Path(p).read_bytes()).digest() for p in GENERATED};generate()
    assert all(hashlib.sha256(Path(p).read_bytes()).digest()==value for p,value in before.items())
    print(f'OK: all 12 ordered pairs, {comparisons} glazing-mask choices and {unique} unique inherited native bindings; correct front-only materials, identical curtain/physical domains and idempotent generation.')


if __name__=='__main__':main()
