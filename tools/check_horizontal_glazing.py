"""Static regression for parity spacing and transparent frame faces; no Java execution."""
import json
from pathlib import Path
from create_school_glass_assets import A, ROOT

def layouts(length):
    spacing=32 if length%2==0 else 24
    return [(1 if i==0 or 16*i%spacing==0 else 0)
            |(2 if i==length-1 or (16*i+16)%spacing==0 else 0)
            |(4 if (16*i+8)%spacing==0 else 0) for i in range(length)]

def posts(model,y):
    return {(e['from'][0],e['to'][0]) for e in model['elements']
            if e['from'][1]<y<e['to'][1]
            and any(f['texture']=='#frame' for side,f in e['faces'].items() if side in ('north','south'))}

def main():
    sources=['school_glass_0_0']
    for name in ('supported_school_glass','mixed_school_glass','supported_school_glass_reversed','mixed_school_glass_reversed'):
        sources += [f'{name}_{half}_straight_{t}_{b}' for half in ('bottom','top') for t in (0,1) for b in (0,1)]
    sources += [f'tall_classroom_transom_{h}_{o}_{t}_{b}' for h in (0,1) for o in (0,1) for t in (0,1) for b in (0,1)]
    for stem in sources:
        base=json.loads((A/f'models/block/{stem}.json').read_text())
        preserved=lambda m:[e for e in m['elements'] if all(f['texture'] not in ('#glass','#frame') for f in e['faces'].values())]
        y=4 if '_top_straight_' in stem else 11 if stem!='school_glass_0_0' else 8
        for layout in range(8):
            target=stem if layout==3 else f'{stem}_horizontal_{layout}'
            m=json.loads((A/f'models/block/{target}.json').read_text())
            assert preserved(m)==preserved(base),(stem,layout,'support or door changed')
            expected=set()
            if layout&1:expected.add((0,1))
            if layout&2:expected.add((15,16))
            if layout&4:expected.add((7,9))
            assert posts(m,y)==expected,(stem,layout,posts(m,y))
            # Both faces towards each pane survive: the transparent pane cannot occlude the frame.
            for left,right in expected:
                for side,x in [('west',left),('east',right)]:
                    if x in (0,16):continue
                    assert any(e['from'][1]<y<e['to'][1] and side in e['faces']
                               and e['faces'][side]['texture']=='#frame'
                               and (e['to'][0] if side=='east' else e['from'][0])==x for e in m['elements']),(stem,layout,side,x)
    for length in range(1,18):
        centers=set()
        for i,layout in enumerate(layouts(length)):
            if layout&1:centers.add(16*i)
            if layout&2:centers.add(16*(i+1))
            if layout&4:centers.add(16*i+8)
        expected={0,16*length}|set(range(32 if length%2==0 else 24,16*length,32 if length%2==0 else 24))
        assert centers==expected,(length,centers,expected)
    # Adding/removing the final block changes the distant beginning of the row as well.
    assert layouts(4)[1]!=layouts(5)[1] and layouts(5)[1]!=layouts(6)[1]
    java=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlazing.java').read_text()
    assert 'length % 2 == 0 ? 32 : 24' in java and 'index < length' in java
    assert 'level.hasChunkAt' in java and 'Block.UPDATE_CLIENTS' in java
    for file in ('SchoolGlassBlock.java','SchoolGlassStairBlock.java','TallClassroomEntranceBlock.java'):
        s=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block'/file).read_text()
        assert 'SchoolGlazing.FRAME_LAYOUT' in s and 'SchoolGlazing.refreshHorizontal' in s
        assert 'SchoolGlazing.mirrorLayout' in s and 'SimpleBlockOutline.forState' in s
    print('OK: 1–17 blocks, even/odd spacing, distant parity changes, all four stairs, door open/closed, preserved supports and visible inner frame faces.')

if __name__=='__main__':main()
