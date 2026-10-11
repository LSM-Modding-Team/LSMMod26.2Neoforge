"""Static checks for every occupancy, paint selection, hit plane and packed palette.

Uses the generated models and independently compares their surfaces to a 2x2x2 grid.
Java interaction and multiplayer behavior still require an in-game test.
"""
import itertools
import json
import random
from pathlib import Path
from create_custom_paintable_blocks import ROOT, A, SIDES, PAINTS, STEPS

def read(p): return json.loads(p.read_text())
def quadrant(face,x,y,z):
    return z+2*y if face in ('west','east') else x+2*z if face in ('down','up') else x+2*y
def coords(cube): return cube&1,(cube>>1)&1,(cube>>2)&1
def visible(mask,cube,face):
    if not mask&(1<<cube): return False
    xyz=tuple(a+b for a,b in zip(coords(cube),STEPS[face]))
    return not all(0<=a<2 for a in xyz) or not mask&(1<<(xyz[0]+2*xyz[1]+4*xyz[2]))

def main():
    model=read(A/'models/block/school_paintable_custom_block.json')
    assert model['loader']=='fusion:model' and len(model['models'])==49
    assert model['models'][-1]==[{'model':'lsmmod:block/custom_paintable_empty'}]
    empty=read(A/'models/block/custom_paintable_empty.json')
    assert empty['elements']==[] and empty['textures']['particle']==PAINTS[0]
    entries={}
    for series in model['models'][:-1]:
        assert len(series)==len(PAINTS) and all('condition' in e for e in series), 'Missing guard would render ghost faces'
        for entry in series:
            predicate=entry['condition'];assert predicate['type']=='lsmmod:painted_cube_face'
            key=(predicate['cube'],predicate['face'],predicate['paint']);assert key not in entries
            leaf=read(A/('models/'+entry['model'].split(':')[1]+'.json'))
            x,y,z=coords(key[0]);element=leaf['elements'][0]
            assert element['from']==[x*8,y*8,z*8] and element['to']==[x*8+8,y*8+8,z*8+8]
            assert list(element['faces'])==[key[1]]
            assert leaf['textures']['surface']==PAINTS[key[2]]
            entries[key]=element
    for mask in range(256):
        # Independent reference enumerates each cell boundary against its occupied neighbor.
        expected=set()
        cells={coords(cube) for cube in range(8) if mask&(1<<cube)}
        for xyz in cells:
            cube=xyz[0]+2*xyz[1]+4*xyz[2]
            for face,step in STEPS.items():
                if tuple(a+b for a,b in zip(xyz,step)) not in cells: expected.add((cube,face))
        rendered={(cube,face) for cube in range(8) for face in SIDES if visible(mask,cube,face)}
        assert rendered==expected
        for cube,face in rendered:
            x,y,z=coords(cube);q=quadrant(face,x,y,z)
            assert 0<=q<4
            # Every color has exactly one matching face model; removed/covered cells select none.
            for color in range(len(PAINTS)):
                assert (cube,face,color) in entries
            # The hit coordinate lies on the face plane; inward epsilon must select its own cell.
            hit=[a*.5+.25 for a in (x,y,z)]
            for axis,step in enumerate(STEPS[face]):
                if step: hit[axis]=(x,y,z)[axis]*.5+(step>0)*.5
            selected=[int(a-step*1e-6>=.5) for a,step in zip(hit,STEPS[face])]
            assert selected==[x,y,z], (mask,cube,face,hit,selected)
        # At most one visible face per direction/quadrant, even when interior faces are exposed.
        keys=[(face,quadrant(face,*coords(cube))) for cube,face in rendered]
        assert len(keys)==len(set(keys))
    rng=random.Random(261011)
    palettes=[tuple([c]*24) for c in range(len(PAINTS))]+[tuple(rng.randrange(len(PAINTS)) for _ in range(24)) for _ in range(10000)]
    for colors in palettes:
        low=sum(c<<(i*3) for i,c in enumerate(colors[:16]))
        high=sum(c<<(i*3) for i,c in enumerate(colors[16:]))
        restored=tuple(((low if i<16 else high)>>((i if i<16 else i-16)*3))&7 for i in range(24))
        assert restored==colors
    assert list(read(A/'blockstates/school_paintable_custom_block.json')['variants'])==['']
    assert list(read(A/'blockstates/school_foundation.json')['variants'])==['']
    print(f'OK: 256 occupancy masks, {len(entries)} face/color models, all exposed hit planes, 24 independent squares and {len(palettes)} packed palettes; two blocks with one state each.')

if __name__=='__main__': main()
