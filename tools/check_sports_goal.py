"""Static geometry/resource checks; does not compile or launch Minecraft."""
import json
from pathlib import Path
from PIL import Image
from create_sports_goal_assets import ROOT, ASSETS, PARTS, ID

def main():
    manifest = json.loads((ROOT/'tools/sports_goal_geometry.json').read_text())
    occupied = manifest['occupied']
    assert len(occupied) == len(set(occupied)) and 0 in occupied
    assert len(manifest['cells']) == 144
    bounds = [p[0] for p in PARTS]
    assert [min(b[a] for b in bounds) for a in range(3)] == [0, 0, 0]
    assert [max(b[a+3] for b in bounds) for a in range(3)] == [128, 96, 48]
    variants = json.loads((ASSETS/f'blockstates/{ID}.json').read_text())['variants']
    assert len(variants) == 576
    for index in range(144):
        cell = manifest['cells'][str(index)]
        assert bool(cell['collision']) == (index in occupied)
        for b in cell['collision']:
            assert all(0 <= b[a] < b[a+3] <= 16 for a in range(3))
            rotated = b
            for turn in range(4):
                rotated = [16-rotated[5], rotated[1], rotated[0], 16-rotated[2], rotated[4], rotated[3]]
                assert all(0 <= rotated[a] < rotated[a+3] <= 16 for a in range(3))
            assert rotated == b
        for facing in ['north','east','south','west']:
            model = variants[f'cell={index},facing={facing}']['model'].split(':')[1]
            data = json.loads((ASSETS/f'models/{model}.json').read_text())
            for element in data['elements']:
                assert all(0 <= element['from'][a] < element['to'][a] <= 16 for a in range(3))
                for face in element['faces'].values():
                    texture = data['textures'][face['texture'][1:]].split(':')[1]
                    assert (ASSETS/f'textures/{texture}.png').is_file()
        # All four coordinate transforms invert to the controller.
        c,r,d = index%8,index//8%6,index//48
        for f in [(0,-1),(1,0),(0,1),(-1,0)]:
            right = (-f[1],f[0]); pos=(right[0]*c-f[0]*d,r,right[1]*c-f[1]*d)
            assert (pos[0]-right[0]*c+f[0]*d,pos[1]-r,pos[2]-right[1]*c+f[1]*d)==(0,0,0)
    # Interior mouth at z14 is unobstructed below the crossbar (including meshes).
    for b in bounds:
        assert not (b[0]<126 and b[3]>2 and b[1]<62 and b[4]>2 and b[2]<=14<b[5])
    nets = [b for b,material,*_ in PARTS if material=='net']
    assert any(b[2]==46 for b in nets) and any(b[0]==1.875 for b in nets)
    assert any(b[0]==126 for b in nets) and any(b[1]==61.875 for b in nets)
    alpha = Image.open(ASSETS/'textures/block/sports_goal_net.png').getchannel('A')
    assert set(alpha.tobytes()) == {0,255}
    # Rear crossbar retreats to z30; lower rear frame remains at z46.
    assert any(b[2]==30 and b[1]==62 and material=='frame' for b,material,*_ in PARTS)
    assert any(b[2]==46 and b[1]==0 and b[4]==32 for b,material,*_ in PARTS)
    assert not any(b[2]==46 and b[4]==64 and material=='frame' for b,material,*_ in PARTS)
    basket = [b for b,material,*_ in PARTS if material=='basket_net']
    assert min(b[0] for b in basket if b[1]==64) > min(b[0] for b in basket if b[1]==71)
    texture = Image.open(ASSETS/'textures/block/sports_goal_basket_net.png')
    assert set(texture.getchannel('A').tobytes()) == {0,255}
    assert texture.getpixel((4,4))[3] == 255 and texture.getpixel((4,3))[3] == 0
    # Mirroring preserves the occupied-cell layout and the single controller.
    assert {7-i%8+8*(i//8%6)+48*(i//48) for i in occupied} == set(occupied)
    loot=json.loads((ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json').read_text())
    assert loot['pools'][0]['conditions'][1]['properties']=={'cell':'0'}
    print(f'OK: 8x6x3; {len(occupied)} occupied cells; 576 variants; rotations, open entrance, mesh alpha and single drop.')

if __name__=='__main__': main()
