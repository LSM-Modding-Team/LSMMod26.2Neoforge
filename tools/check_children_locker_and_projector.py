"""Static geometry/resource checks; no Java build or Minecraft execution."""
import json
from PIL import Image
from create_children_locker_and_projector import ROOT,A,D
def read(path):return json.loads(path.read_text())
def solid(model,point):return any(all(a<=v<=b for a,v,b in zip(e['from'],point,e['to'])) for e in model['elements'])
def main():
    for half in ('lower','upper'):
        model=read(A/f'models/block/children_locker_{half}.json')
        assert len(model['elements'])==5
        assert not solid(model,(8,8,8)), 'The cubby must be open inside'
        assert solid(model,(8,8,14.5)) and solid(model,(1.5,8,8)) and solid(model,(8,.5,8))
        for e in model['elements']:assert all(0<=a<b<=16 for a,b in zip(e['from'],e['to']))
    states=read(A/'blockstates/children_locker.json')['variants'];assert len(states)==8
    assert {s['y'] for s in states.values()}=={0,90,180,270}
    item=read(A/'models/item/children_locker.json');assert len(item['elements'])==10
    assert min(e['from'][1] for e in item['elements'])==0 and max(e['to'][1] for e in item['elements'])==32
    loot=read(D/'lsmmod/loot_table/blocks/children_locker.json')
    assert any(c.get('properties')=={'half':'lower'} for c in loot['pools'][0]['conditions'])
    projector=read(A/'models/block/ceiling_projector.json');assert len(projector['elements'])==4
    assert max(e['to'][1] for e in projector['elements'])==16 and min(e['from'][1] for e in projector['elements'])==7
    assert len(read(A/'blockstates/ceiling_projector.json')['variants'])==4
    for model in [projector,item]:
        for texture in model['textures'].values():assert (A/('textures/'+texture.split(':')[1]+'.png')).is_file()
    clock=Image.open(A/'textures/block/school_clock_face.png');assert clock.size==(64,64) and clock.mode=='RGB'
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
    for name in ('ChildrenLockerBlock','CeilingProjectorBlock'):
        source=(java/f'{name}.java').read_text()
        assert 'EntityBlock' not in source and 'scheduleTick' not in source
        assert 'Shapes.rotateHorizontal' in source and 'SimpleBlockOutline.forState' in source
    assert (A/'blockstates/wall_projector.json').is_file()
    print('OK: two open 16px cubbies / 32px item, eight orientations/halves, lower-only loot; four ceiling orientations, ceiling mount, resources present and 64px clock dial; no entities/tickers.')
if __name__=='__main__':main()
