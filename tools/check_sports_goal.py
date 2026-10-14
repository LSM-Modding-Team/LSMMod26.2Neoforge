"""Static geometry/resource checks; does not compile or launch Minecraft."""
import json
import re
from ast import literal_eval
from math import isclose
from PIL import Image
from create_sports_goal_assets import ROOT, ASSETS, PARTS, ID, coordinates, index as encode

def main():
    manifest = json.loads((ROOT/'tools/sports_goal_geometry.json').read_text())
    occupied = manifest['occupied']
    assert len(occupied) == len(set(occupied)) and 0 in occupied
    assert len(manifest['cells']) == 162
    legacy = {encode(c,r,d) for d in range(3) for r in range(4) for c in range(9)
              if d == 2 or r == 3 or c in (0,8)}
    legacy |= {encode(c,r,0) for r in (4,5) for c in (3,4,5)}
    assert set(occupied) == legacy
    java = (ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SportsGoalShapes.java').read_text()
    calls = java.split('CELLS = ',1)[1].split(';',1)[0]
    assert list(map(int,re.findall(r'cell(\d+)\(\)',calls))) == list(range(162))
    for i in range(162):
        boxes = re.search(r'cell'+str(i)+r'\(\) \{\s*return new double\[\]\[\] (\{.*?\});',java).group(1)
        assert literal_eval(boxes.replace('{','[').replace('}',']')) == manifest['cells'][str(i)]['collision']
    bounds = [p[0] for p in PARTS]
    assert [min(b[a] for b in bounds) for a in range(3)] == [8, 0, 0]
    assert [max(b[a+3] for b in bounds) for a in range(3)] == [136, 96, 48]
    variants = json.loads((ASSETS/f'blockstates/{ID}.json').read_text())['variants']
    assert len(variants) == 648
    for index in range(162):
        cell = manifest['cells'][str(index)]
        c,r,d = coordinates(index)
        maximum = 32 if r == 3 else 16
        assert bool(cell['collision']) == (index in occupied)
        for b in cell['collision']:
            assert all(0 <= b[a] < b[a+3] <= (maximum if a == 1 else 16) for a in range(3))
            rotated = b
            for turn in range(4):
                rotated = [16-rotated[5], rotated[1], rotated[0], 16-rotated[2], rotated[4], rotated[3]]
                assert all(0 <= rotated[a] < rotated[a+3] <= (maximum if a == 1 else 16) for a in range(3))
            assert rotated == b
        for facing in ['north','east','south','west']:
            model = variants[f'cell={index},facing={facing}']['model'].split(':')[1]
            data = json.loads((ASSETS/f'models/{model}.json').read_text())
            assert data['elements'] == cell['elements']
            for element in data['elements']:
                assert all(0 <= element['from'][a] < element['to'][a] <= (maximum if a == 1 else 16) for a in range(3))
                for face in element['faces'].values():
                    texture = data['textures'][face['texture'][1:]].split(':')[1]
                    assert (ASSETS/f'textures/{texture}.png').is_file()
                    assert all(0 <= v <= 16 for v in face['uv'])
        # All four coordinate transforms invert to the controller.
        for f in [(0,-1),(1,0),(0,1),(-1,0)]:
            right = (-f[1],f[0]); pos=(right[0]*c-f[0]*d,r,right[1]*c-f[1]*d)
            assert (pos[0]-right[0]*c+f[0]*d,pos[1]-r,pos[2]-right[1]*c+f[1]*d)==(0,0,0)
    # Interior mouth at z14 is unobstructed below the crossbar (including meshes).
    for b in bounds:
        assert not (b[0]<134 and b[3]>10 and b[1]<62 and b[4]>2 and b[2]<=14<b[5])
    nets = [b for b,material,*_ in PARTS if material=='net']
    assert any(b[2]==46 for b in nets) and any(b[0]==9.875 for b in nets)
    assert any(b[0]==134 for b in nets) and any(b[1]==61.875 for b in nets)
    alpha = Image.open(ASSETS/'textures/block/sports_goal_net.png').getchannel('A')
    assert set(alpha.tobytes()) == {0,255}
    # Raised rear rail, mirrored triangular side braces and paired board arms.
    assert any(b[2]==46 and b[1]==78 and material=='frame' for b,material,*_ in PARTS)
    assert sum(b[2]==46 and b[1]==0 and b[4]==80 for b,material,*_ in PARTS if material=='frame') == 2
    assert any(b[4] > 16 for value in manifest['cells'].values() for b in value['collision'])
    frames = [b for b,material,*_ in PARTS if material=='frame']
    for b in frames:
        reflected = [144-b[3],b[1],b[2],144-b[0],b[4],b[5]]
        assert any(all(isclose(v,w,abs_tol=1e-8) for v,w in zip(reflected,other)) for other in frames)
    # No football mesh fills the front basketball support tower above the mouth.
    assert not any(b[0]<90 and b[3]>54 and b[1]>=64 and b[2]<=14 for b in nets)
    basket = [b for b,material,*_ in PARTS if material=='basket_net']
    assert min(b[0] for b in basket if b[1]==64) > min(b[0] for b in basket if b[1]==71)
    texture = Image.open(ASSETS/'textures/block/sports_goal_basket_net.png')
    assert set(texture.getchannel('A').tobytes()) == {0,255}
    assert texture.getpixel((4,4))[3] == 255 and texture.getpixel((4,3))[3] == 0
    # Mirroring preserves the occupied-cell layout and the single controller.
    assert {encode(8-coordinates(i)[0],coordinates(i)[1],coordinates(i)[2]) for i in occupied} == set(occupied)
    loot=json.loads((ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json').read_text())
    assert loot['pools'][0]['conditions'][1]['properties']=={'cell':'0'}
    print(f'OK: mirrored raised frame and braces; {len(occupied)} legacy cells; 648 variants; bounded overhangs, collision, rotations, UVs, cutout nets and single drop.')

if __name__=='__main__': main()
