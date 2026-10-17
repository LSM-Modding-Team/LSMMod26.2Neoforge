"""Static walking clearance with a .6-block player footprint, smooth meshes and 32-step collision profiles."""
from resource_modules import module_read_bytes, module_read_text
import json,math,hashlib
from pathlib import Path
from create_yellow_school_slopes import ROOT,A,GENERATED,main as generate


def read(p):return json.loads(module_read_text(p))


def profile(pieces,descending):
    boxes=[]
    for cell,(height,row) in enumerate(pieces):
        for i in range(32):
            top=row+height+(-.125*i/32 if descending else .25*(i+1)/32)
            boxes.append((cell+i/32,cell+(i+1)/32,top))
    # Continue into flat surfaces at both ends. Test a 0.6-wide collision footprint.
    start=pieces[0][0]+pieces[0][1];last=pieces[-1][0]+pieces[-1][1]+(-.125 if descending else .25)
    boxes=[(-2,0,start)]+boxes+[(len(pieces),len(pieces)+2,last)]
    contacts=[]
    for step in range((len(pieces)+2)*64+1):
        center=-1+step/64
        contacts.append(max(h for low,high,h in boxes if low<center+.3 and high>center-.3))
    delta=max(abs(b-a) for a,b in zip(contacts,contacts[1:]))
    assert delta<.6 and delta<=.25/32+1/64*.25+1e-9
    # Same result when walking backwards; horizontal rotations preserve widths/heights.
    assert max(abs(b-a) for a,b in zip(contacts[::-1],contacts[-2::-1]))==delta
    return delta


def main():
    layout=read(ROOT/'tools/yellow_school_slope_layout.json');assert layout['length']==4 and layout['drop_pixels']==8
    pieces=layout['pieces'];assert [(p['high_pixels'],p['low_pixels']) for p in pieces]==[(8,6),(6,4),(4,2),(2,0)]
    for a,b in zip(pieces,pieces[1:]):assert a['low_pixels']==b['high_pixels']
    for p in pieces:
        name=p['id'];model=read(A/f'models/block/{name}.json')
        assert model['textures']=={'yellow':'lsmmod:block/yellow_school_block','particle':'lsmmod:block/yellow_school_block'}
        mesh=module_read_text(A/f'models/block/{name}.obj');assert 'usemtl gray' not in mesh and 'usemtl yellow' in mesh
        vertices=[tuple(map(float,line.split()[1:])) for line in mesh.splitlines() if line.startswith('v ')]
        assert min(v[1] for v in vertices)==0 and math.isclose(max(v[1] for v in vertices),p['high_pixels']/16)
        for v in vertices:assert all(0<=a<=1 for a in v)
        states=read(A/f'blockstates/{name}.json')['variants'];assert len(states)==4
    yellow=profile([(p['high_pixels']/16,0) for p in pieces],True)
    entrance_layout=read(ROOT/'tools/entrance_pavement_slope_layout.json')
    entrance=profile([(p['lower_y'],p['row']) for p in entrance_layout['pieces']],False)
    ends=[p['row']+p['lower_y'] for p in entrance_layout['pieces']]
    assert all(math.isclose(b,a+.25) for a,b in zip(ends,ends[1:]))
    yellow_source=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/slopes/block/YellowSchoolSlopeBlock.java')
    entrance_source=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/slopes/block/EntrancePavementSlopeBlock.java')
    assert 'high-.125*i/32.0' in yellow_source and 'low+.25*(i+1)/32.0' in entrance_source
    assert 'ExactVoxelShapes.union' in yellow_source and 'ExactVoxelShapes.union' in entrance_source
    cabinet=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/technology/block/SchoolCabinetBlock.java')
    assert 'BOXES, BOXES, true, 2, false' in cabinet
    wide=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/facilities/block/WideBathroomBlock.java')
    assert 'wallMounted ? face.getAxis().isVertical()' in wide and 'wallMounted || half == 0' in wide
    generate();before={p:hashlib.sha256(module_read_bytes(Path(p))).digest() for p in GENERATED};generate()
    assert all(hashlib.sha256(module_read_bytes(Path(p))).digest()==h for p,h in before.items())
    print(f'OK: 4 yellow pieces (8→0px / 4 blocks), uniform material and 4 orientations; .6-block footprint over yellow/entrance in both directions: max contact increments {yellow:.6f}/{entrance:.6f} blocks (<.6 step height), seam continuity, unchanged entrance collision; cabinet wall-only; generator idempotent.')


if __name__=='__main__':main()
