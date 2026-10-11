"""Static door equivalence, generator round-trip, migration and state-count checks."""
import argparse,hashlib,json
from compact_school_door_assets import A,ROOT,IDS,PROPERTIES,main as compact,expand_variants,read
from audit_block_states import audit
J=ROOT/'src/main/java/net/nicomar2009/lsmmod'
def main(baseline=None):
    paths=[A/f'blockstates/{i}.json' for i in IDS]+list((A/'models/block/school_door_dynamic').glob('*.json'))
    before={p:hashlib.sha256(p.read_bytes()).hexdigest() for p in paths}
    compact();assert before=={p:hashlib.sha256(p.read_bytes()).hexdigest() for p in paths}
    old=read(baseline) if baseline else None
    counts={r['id']:r['states'] for r in audit()};assert sum(counts.values())==14844
    checked=0
    for ident in IDS:
        definition=read(A/f'blockstates/{ident}.json');expected=288 if ident=='double_school_entrance' else 96
        assert len(definition['variants'])==counts[ident]==expected
        legacy=expand_variants(definition);assert len(legacy)==expected*32
        if old:assert legacy==old[ident]['variants'],(ident,'geometry/transform selection changed')
        for selector,apply in definition['variants'].items():
            state=dict(p.split('=',1) for p in selector.split(','))
            assert not set(state)&set(PROPERTIES)
            assert apply['y']=={'north':0,'east':90,'south':180,'west':270}[state['facing']]
            model=read(A/f"models/{apply['model'].split(':')[1]}.json")
            if model.get('type')=='composite':
                assert state['row']=='2' and (ident!='double_school_entrance' or state['depth']=='0')
                assert len(model['models'])==1
                entries=model['models'][0];assert [e['condition']['mask'] for e in entries]==list(range(32))
                for entry in entries:
                    assert entry['condition']['type']=='lsmmod:glazing_mask'
                    assert (A/f"models/{entry['model'].split(':')[1]}.json").is_file()
            else:assert state['row']!='2' or ident=='double_school_entrance' and state['depth']!='0'
        checked+=len(legacy)
    migration=(J/'block/LegacyCurtainStates.java').read_text();assert all('lsmmod:'+i in migration for i in IDS)
    for name in ('TallClassroomEntranceBlock','DoubleSchoolEntranceBlock'):
        source=(J/f'block/{name}.java').read_text();assert all(p not in source for p in ('FRAME_LAYOUT','TOP_CONNECTED','BOTTOM_CONNECTED'))
        assert 'POWERED' in source and 'SchoolGlazing.refreshHorizontal' in source
    assert 'SchoolGlazing.isGlass(state)' in (J/'mixin/GlazingClientUpdatesMixin.java').read_text()
    print(f'OK: {checked} legacy geometry/rotation selections; 21504 -> 672 door states; 14844 current total; migration, client refresh and idempotent resources checked statically.')
if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--baseline',type=__import__('pathlib').Path);args=p.parse_args();main(args.baseline)
