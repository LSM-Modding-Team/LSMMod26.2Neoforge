"""Dimensions, front UV continuity, support surface and physical cell/resource checks."""
from resource_modules import module_exists, module_path, module_read_bytes, module_read_text
import json,hashlib
from pathlib import Path
from PIL import Image
from create_school_cabinet_and_router import ROOT,A,GENERATED,main as generate


def read(p):return json.loads(module_read_text(p))


def main():
    from audit_block_states import audit
    rows={r['id']:r for r in audit()}
    assert rows['school_cabinet']['states']==16 and set(rows['school_cabinet']['domains'])=={'facing','half','column'}
    assert rows['school_router']['states']==4
    state=read(A/'blockstates/school_cabinet.json')['variants'];assert len(state)==16
    pixels=set()
    for half in range(2):
        for column in range(2):
            name=f'school_cabinet_{"upper" if half else "lower"}_{column}'
            model=read(A/f'models/block/{name}.json');assert len(model['elements'])==1
            e=model['elements'][0];assert e['from']==[0,0,0] and e['to']==[16,16,16]
            assert set(e['faces'])=={'north','south','west','east','up','down'}
            for face,surface in e['faces'].items():assert surface['cullface']==face
            uv=e['faces']['north']['uv']
            for x in range(int(uv[0]*2),int(uv[2]*2)):
                for y in range(int(uv[1]*2),int(uv[3]*2)):
                    assert (x,y) not in pixels;pixels.add((x,y))
            for facing,angle in [('north',0),('east',90),('south',180),('west',270)]:
                assert state[f'column={column},facing={facing},half={"upper" if half else "lower"}']=={'model':'lsmmod:block/'+name,'y':angle}
    assert pixels=={(x,y) for x in range(32) for y in range(32)}
    assert Image.open(module_path(A/'textures/block/school_cabinet_front.png')).size==(32,32)
    cabinet=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/technology/block/SchoolCabinetBlock.java')
    assert '{{0, 0, 0, 32, 32, 16}}' in cabinet and 'true, 2, false' in cabinet and 'builder.add(HALF)' in cabinet
    wide=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/facilities/block/WideBathroomBlock.java')
    assert 'initial.hasProperty(OPEN)' in wide and 'initial.hasProperty(HALF)' in wide
    assert 'sharedRotations.computeIfAbsent' in wide and 'SimpleBlockOutline.forState' in wide
    router=read(A/'models/block/school_router.json')
    boxes=[[3,0,4,13,3,12],[4,3,10,5,12,11],[11,3,10,12,12,11]]
    for e in router['elements']:
        assert all(0<=a<b<=16 for a,b in zip(e['from'],e['to']))
        assert any(all(box[i]<=e['from'][i]<e['to'][i]<=box[i+3] for i in range(3)) for box in boxes)
        for f in e['faces'].values():
            texture=router['textures'][f['texture'][1:]].split(':')[1];assert module_exists(A/f'textures/{texture}.png')
    source=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/technology/block/SchoolRouterBlock.java')
    assert 'isFaceSturdy(level,support,Direction.UP)' in source and 'ExactVoxelShapes.pixels' in source and 'SimpleBlockOutline.forState' in source
    for name in ('school_cabinet','school_router'):
        loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{name}.json')
        assert loot['pools'][0]['entries']==[{'type':'minecraft:item','name':'lsmmod:'+name}]
        if name=='school_cabinet':assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','half':'lower'}
        for locale in ('es_es','en_us'):assert read(A/f'lang/{locale}.json')['block.lsmmod.'+name]
        assert read(A/f'items/{name}.json')['model']['model']=='lsmmod:item/'+name
    generate();before={p:hashlib.sha256(module_read_bytes(Path(p))).digest() for p in GENERATED};generate()
    assert all(hashlib.sha256(module_read_bytes(Path(p))).digest()==h for p,h in before.items())
    print('OK: cabinet 32x32x16px, 16 orientations/cells with closed doors, continuous front UVs, full top support and one controller drop; router exact three-box shape, support check and four cached rotations; generator idempotent.')


if __name__=='__main__':main()
