"""Static joins, narrow sanitary material widths and 24px sink collision/resource coverage."""
from resource_modules import module_read_bytes, module_read_text
import json,sys,hashlib
from pathlib import Path
from facilities.create_school_sink import ROOT,A,PARTS,main as generate,GENERATED
from facilities.create_wide_bathroom_assets import widened
from facilities.create_bathroom_statue_assets import TOILET,URINAL


def read(p):return json.loads(module_read_text(p))


def main():
    locker=read(A/'models/block/children_locker_lower.json')
    boxes=[e['from']+e['to'] for e in locker['elements']]
    assert min(b[0] for b in boxes)==0 and max(b[3] for b in boxes)==16
    assert boxes[2][3]-boxes[2][0]==boxes[3][3]-boxes[3][0]==.5
    assert boxes[3][3]==boxes[2][0]+16
    for e in locker['elements']:
        if e['from'][0]==0:assert e['faces']['west']['cullface']=='west'
        if e['to'][0]==16:assert e['faces']['east']['cullface']=='east'
    source=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/lockers_and_storage/block/ChildrenLockerBlock.java')
    assert 'neighbor.getValue(HALF)==state.getValue(HALF)' in source and 'side.getAxis()!=state.getValue(FACING).getAxis()' in source
    assert 'builder.add(FACING,HALF)' in source and 'BooleanProperty' not in source
    for original in (TOILET,URINAL):
        new=widened(original)
        for old,part in zip(original,new):
            b,material=old[:2];actual=part[0]
            scale=.875 if material in ('porcelain','seat','drain') else 1
            assert abs((actual[3]-actual[0])-(b[3]-b[0])*2*scale)<1e-9
            assert actual[1]==b[1] and actual[2]==b[2] and actual[4:]==b[4:]
    assert max(b[4] for b,*_ in PARTS)==24 and min(b[1] for b,*_ in PARTS)==0
    assert not any(all(b[i]<=v<b[i+3] for i,v in enumerate((8,20,8))) for b,*_ in PARTS), 'Basin must remain hollow'
    for half in range(2):
        model=read(A/f'models/block/school_sink_{"upper" if half else "lower"}.json')
        assert model['textures']['porcelain']=='lsmmod:block/school_porcelain'
        for e in model['elements']:
            lo=e['from'][:];hi=e['to'][:];lo[1]+=half*16;hi[1]+=half*16
            assert any(all(b[i]<=lo[i]<hi[i]<=b[i+3] for i in range(3)) for b,*_ in PARTS)
        assert max(e['to'][1] for e in model['elements'])==(16 if half==0 else 8)
    from audit_block_states import audit
    rows={r['id']:r for r in audit()};assert rows['children_locker']['states']==rows['school_sink']['states']==8
    loot=read(ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/school_sink.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'half':'lower'}
    generate();before={p:hashlib.sha256(module_read_bytes(Path(p))).digest() for p in GENERATED};generate()
    assert all(hashlib.sha256(module_read_bytes(Path(p))).digest()==h for p,h in before.items())
    print('OK: lockers touch horizontally with 1px shared seam/no extra states; sanitary bodies narrowed 12.5% while partitions unchanged; sink 24px, hollow basin, exact physical cells, original porcelain and one drop; generator idempotent.')


if __name__=='__main__':main()
