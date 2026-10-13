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
        low=sum(c<<(i*4) for i,c in enumerate(colors[:16]))
        high=sum(c<<(i*4) for i,c in enumerate(colors[16:]))
        restored=tuple(((low if i<16 else high)>>((i if i<16 else i-16)*4))&15 for i in range(24))
        assert restored==colors
    # Existing saves omit paint_bits and retain their three-bit IDs unchanged.
    for _ in range(1000):
        old=[rng.randrange(7) for _ in range(24)]
        low=sum(c<<(i*3) for i,c in enumerate(old[:16]))
        high=sum(c<<(i*3) for i,c in enumerate(old[16:]))
        assert [((low if i<16 else high)>>((i if i<16 else i-16)*3))&7 for i in range(24)]==old
    entity=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolPaintableCustomBlockEntity.java').read_text()
    assert 'output.putInt("paint_bits",4)' in entity and 'getIntOr("paint_bits",3)==4 ? 4 : 3' in entity
    assert list(read(A/'blockstates/school_paintable_custom_block.json')['variants'])==['']
    assert list(read(A/'blockstates/school_foundation.json')['variants'])==['']
    print(f'OK: 256 occupancy masks, {len(entries)} face/color models, all exposed hit planes, 24 independent squares and {len(palettes)} packed palettes; two blocks with one state each.')

def check_fine():
    import re
    from create_custom_paintable_blocks import FINE_ID
    from render_mini_cube_paintable import scene,FULL
    from render_texture_review import load
    model=read(A/f'models/block/{FINE_ID}.json')
    assert len(model['models'])==385
    references=set()
    for group in model['models'][:-1]:
        assert len(group)==len(PAINTS)
        for paint,entry in enumerate(group):
            condition=entry['condition'];cube,face=condition['cube'],condition['face'];references.add((cube,face,paint))
            assert condition['type']=='lsmmod:painted_mini_cube' and condition['paint']==paint
            assert entry['model'].startswith('lsmmod:block/door_frame_4px/')
            leaf,=load(entry['model']);element,=leaf['elements']
            assert element['from']==[(cube&3)*4,((cube>>2)&3)*4,((cube>>4)&3)*4]
            assert element['to']==[v+4 for v in element['from']] and list(element['faces'])==[face]
            assert leaf['textures']['surface']==PAINTS[paint]
    assert len(references)==64*6*len(PAINTS)
    rng=random.Random(4416)
    masks=[FULL,0,1,1<<63]+[FULL&~(1<<cube) for cube in range(64)]+[rng.getrandbits(64) for _ in range(40)]
    for mask in masks:
        cells={(cube&3,(cube>>2)&3,(cube>>4)&3) for cube in range(64) if mask&(1<<cube)}
        expected=sum(tuple(v+d for v,d in zip(cell,step)) not in cells for cell in cells for step in STEPS.values())
        rendered=scene(mask,paint=lambda xyz,face:9 if sum(xyz)%2 else 4)
        assert len(rendered)==expected,('Missing or internal face',mask)
        for element in rendered:
            xyz=tuple(int(v/4) for v in element['from'])
            assert xyz in cells
            for face,surface in element['faces'].items():assert surface['texture']==PAINTS[9 if sum(xyz)%2 else 4]
    assert len(scene())==96 and not scene(0)
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    registration=(java/'client/GlazingFusionRegistration.java').read_text()
    entries=re.findall(r'LSMMod\.MOD_ID,\s*"([^"]+)"\),\s*(\w+\.SERIALIZER)',registration)
    assert len(entries)==registration.count('registerModelPredicate('),'Unrecognized predicate registration'
    assert len({identifier for identifier,_ in entries})==len(entries),'Duplicate Fusion predicate ID'
    assert len({serializer for _,serializer in entries})==len(entries),'Fusion rejects registering the same serializer twice'
    assert dict(entries)['painted_mini_cube']=='PaintedMiniCubePredicate.SERIALIZER'
    assert dict(entries)['painted_door_frame']=='PaintedDoorFramePredicate.SERIALIZER'
    for _,serializer in entries:
        owner=serializer.split('.')[0];source=(java/f'client/{owner}.java').read_text()
        assert re.search(r'SERIALIZER\s*=\s*new Serializer',source),f'{owner} aliases another serializer'
        assert re.search(r'getSerializer\(\)\s*\{\s*return SERIALIZER;',source),f'{owner} returns a different serializer'
    entity=(java/'block/SchoolMiniCubePaintEntity.java').read_text();frame=(java/'block/SchoolDoorFramePaintEntity.java').read_text()
    assert 'miniSize() {return 4;}' in entity and 'occupied()==0' in entity and 'level.removeBlock(worldPosition,false)' in entity
    assert 'miniSize() {return 8;}' in frame and 'return new int[]{start,start+1,start+4,start+5}' in frame
    assert 'getLongOr("removed",0)' in frame and 'private final int[] squares=new int[96]' in frame
    assert list(read(A/f'blockstates/{FINE_ID}.json')['variants'])==['']
    assert read(A/f'items/{FINE_ID}.json')['model']['model']=='lsmmod:item/'+FINE_ID
    loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{FINE_ID}.json')
    assert loot['pools'][0]['entries'][0]['name']=='lsmmod:'+FINE_ID
    print(f'OK: {len(entries)} unique Fusion predicate IDs/serializer instances; independent 64-cube block, shared geometry/all 13 materials, {len(masks)} occupancy masks including bit 63, 4px painting/interior faces; door edits at 8px and original storage preserved. Minecraft behavior untested.')

if __name__=='__main__':
    import sys
    if '--fine-only' in sys.argv:check_fine()
    else:main()
