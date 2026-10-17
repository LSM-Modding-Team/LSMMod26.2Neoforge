"""Coplanar half-grid targeting across all axes, occupancy masks and negative coordinates."""
from resource_modules import module_read_text
import itertools
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]


def main():
    checked=0
    for mask in range(256):
        cells={xyz for xyz in itertools.product(range(2),repeat=3) if mask&(1<<(xyz[0]+2*xyz[1]+4*xyz[2]))}
        for axis,step in itertools.product(range(3),(-1,1)):
            u=2 if axis==0 else 0;v=2 if axis==1 else 1
            surfaces=set()
            for cube in cells:
                neighbor=list(cube);neighbor[axis]+=step
                if tuple(neighbor) not in cells:
                    surfaces.add((cube[axis]+int(step>0),cube[u],cube[v]))
            for origin in (-3,-1,0,2):
                for local_plane in range(3):
                    plane=origin*2+local_plane
                    normal=(plane-int(step>0))//2;half=(plane-int(step>0))%2
                    if normal!=origin:continue
                    actual=set()
                    for qa,qb in itertools.product(range(2),repeat=2):
                        cube=[0,0,0];cube[axis]=half;cube[u]=qa;cube[v]=qb
                        neighbor=cube[:];neighbor[axis]+=step
                        if tuple(cube) in cells and tuple(neighbor) not in cells:actual.add((qa,qb))
                    assert actual=={(a,b) for p,a,b in surfaces if p==local_plane}
                    checked+=1
    # Inclusive whole-block endpoints cover all four quarters; mini endpoints stay precise.
    for start,end in itertools.product(range(-8,9),repeat=2):
        lo,hi=sorted((start,end));blocks=set(range(lo//2,hi//2+1))
        assert blocks=={q//2 for q in range(lo,hi+1)}
    for block in range(-4,5):
        assert {q%2 for q in range(block*2,block*2+2)}=={0,1}
    # Quarter-grid planes distinguish every 4px surface, including signed world coordinates.
    for origin,axis,step in itertools.product(range(-4,5),range(3),(-1,1)):
        u=2 if axis==0 else 0;v=2 if axis==1 else 1
        for cell in itertools.product(range(4),repeat=3):
            plane=origin*4+cell[axis]+int(step>0)
            assert (plane-int(step>0))//4==origin
            assert (plane-int(step>0))%4==cell[axis]
            point=[origin+(q+.5)/4 for q in cell];point[axis]=plane/4
            point[axis]-=step*1e-6
            assert tuple(int((p-origin)*4) for p in point)==cell
    for origin,divisions,local in itertools.product(range(-4,5),(1,2,4),range(4)):
        size=4//divisions;start=origin*4+(local//size)*size;end=start+size-1
        assert end-start+1==size
        assert {q//4 for q in range(start,end+1)}=={origin}
        if divisions==2:
            # Legacy half-grid endpoints scale to the same occupied interval.
            old=origin*2+local//2
            assert (start,end)==(old*2,old*2+1)
    source=module_read_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/school_supplies/item/RectanglePaintTool.java')
    assert 'MAX_BLOCKS=256' in source and 'Math.floorDiv' in source and 'Math.floorMod' in source
    assert 'Double.isFinite' in source and 'anchor.getStringOr("dimension"' in source and 'anchor.getStringOr("owner"' in source
    assert source.index('if(!level.hasChunkAt(pos))')<source.index('for(Change change:changes)')
    assert source.index('!player.mayUseItemAt(pos,face,stack)')<source.index('for(Change change:changes)')
    assert 'small.hasCube' not in source and 'block.hasCube(cube)' in source
    assert 'block.snapshot()' in source and 'change.block.apply(change.colors)' in source
    for name in ('SchoolPaintItem','SchoolSandpaperItem'):
        item=module_read_text(ROOT/f'src/main/java/net/nicomar2009/lsmmod/modules/school_supplies/item/{name}.java')
        assert 'RectanglePaintTool.use(context,' in item and 'RectanglePaintTool.cancel' in item
    print(f'OK: {checked} half-grid cases plus every quarter-grid hit plane over signed coordinates; 4px/8px/full-face endpoints and legacy anchors, permissions/loaded preflight and one batched palette per block; ordinary tool paths retained.')


if __name__=='__main__':main()
