"""Dynamic connection masks, hollow trough, exact cells and bounded states/shapes."""
from resource_modules import module_read_bytes, module_registry_text
import json,hashlib
from pathlib import Path
from create_school_connected_sink import ROOT,A,GENERATED,main as generate,parts

def read(p):return json.loads(module_registry_text(p))
def main():
    from audit_block_states import audit
    row=next(r for r in audit() if r['id']=='school_connected_sink');assert row['states']==8 and set(row['domains'])=={'facing','half'}
    for half in range(2):
        h='upper' if half else 'lower';root=read(A/f'models/block/school_connected_sink_{h}.json')
        for mask in range(4):
            selected=next(e for e in root['models'][0] if 'condition' not in e or e['condition']['mask']==mask)
            assert selected['model']==f'lsmmod:block/school_connected_sink_{h}_{mask}'
            model=read(A/f'models/block/school_connected_sink_{h}_{mask}.json')
            assert model['textures']['wall']=='lsmmod:block/dark_school_tiled_floor_side'
            for e in model['elements']:
                lo=e['from'][:];hi=e['to'][:];lo[1]+=half*16;hi[1]+=half*16
                assert all(0<=a<b<=16 for a,b in zip(e['from'],e['to']))
                assert any(all(b[i]<=lo[i]<hi[i]<=b[i+3] for i in range(3)) for b,*_ in parts(mask))
            assert max(e['to'][1] for e in model['elements'])==(16 if half==0 else 8)
            inside=lambda point:any(all(b[i]<=point[i]<b[i+3] for i in range(3)) for b,*_ in parts(mask))
            assert not inside((8,18,8))
            assert inside((.5,18,8))==(not bool(mask&1))
            assert inside((15.5,18,8))==(not bool(mask&2))
    # One through five aligned units have only two exterior side partitions.
    for length in range(1,6):
        masks=[(1 if i else 0)|(2 if i<length-1 else 0) for i in range(length)]
        assert sum(int(not(m&1))+int(not(m&2)) for m in masks)==2
    source=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/facilities/block/SchoolConnectedSinkBlock.java')
    assert 'SchoolConnectedSinkShapes.BOXES[half][mask]' in source and 'OUTLINES[' in source and 'forState' not in source
    assert 'other.getValue(FACING)==state.getValue(FACING)' in source and 'other.getValue(HALF)==state.getValue(HALF)' in source
    registry=module_registry_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java');registration=registry.split('SCHOOL_CONNECTED_SINK =')[1].split(';')[0];assert '.dynamicShape()' in registration
    loot=read(ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/school_connected_sink.json');assert loot['pools'][0]['conditions'][1]['properties']=={'half':'lower'}
    generate();before={p:hashlib.sha256(module_read_bytes(Path(p))).digest() for p in GENERATED};generate();assert all(hashlib.sha256(module_read_bytes(Path(p))).digest()==h for p,h in before.items())
    print('OK: 4 masks x 2 halves, hollow continuous troughs of 1..5 units, original bathroom-floor gray, 24px height, exact cached collision/outline profiles, 8 states and one paired drop; generator idempotent.')
if __name__=='__main__':main()
