"""Static geometry/resource checks; does not compile or launch Minecraft."""
import json
from ast import literal_eval
from math import isclose
from PIL import Image
from create_sports_goal_assets import ROOT, ASSETS, PARTS, ID, coordinates, index as encode, clips

def main():
    manifest = json.loads((ROOT/'tools/sports_goal_geometry.json').read_text())
    occupied = manifest['occupied']
    assert len(occupied) == len(set(occupied)) and 0 in occupied
    assert len(manifest['cells']) == 162
    # Existing goals must still contain every required cell after a resource update.
    legacy_cells = {encode(c,r,d) for d in range(3) for r in range(4) for c in range(9)
                    if d == 2 or r == 3 or c in (0,8)}
    legacy_cells |= {encode(c,r,0) for r in (4,5) for c in (3,4,5)}
    assert set(occupied) == legacy_cells
    # Minecraft uses the generated Java shapes, rather than this preview manifest.
    source = (ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SportsGoalShapes.java').read_text()
    java_occupied = source.split('OCCUPIED = ',1)[1].split(';',1)[0]
    java_cells = source.split('CELLS = ',1)[1].split(';',1)[0]
    assert literal_eval(java_occupied.replace('{','[').replace('}',']')) == occupied
    assert literal_eval(java_cells.replace('{','[').replace('}',']')) == [
        manifest['cells'][str(index)]['collision'] for index in range(162)]
    bounds = [p[0] for p in PARTS]
    assert [min(b[a] for b in bounds) for a in range(3)] == [8, 0, 0]
    assert [max(b[a+3] for b in bounds) for a in range(3)] == [136, 96, 48]
    variants = json.loads((ASSETS/f'blockstates/{ID}.json').read_text())['variants']
    assert len(variants) == 648
    for index in range(162):
        cell = manifest['cells'][str(index)]
        assert bool(cell['collision']) == (index in occupied)
        c,r,d = coordinates(index)
        expected_collision = []
        for part in PARTS:
            clipped = clips(part[0],c,r,d)
            if clipped:
                low,high,origin = clipped
                expected_collision.append([low[a]-origin[a] for a in range(3)]
                                          + [high[a]-origin[a] for a in range(3)])
        assert cell['collision'] == expected_collision
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
            assert data['elements'] == cell['elements']
            assert variants[f'cell={index},facing={facing}']['y'] == ['north','east','south','west'].index(facing)*90
            for element in data['elements']:
                assert all(0 <= element['from'][a] < element['to'][a] <= 16 for a in range(3))
                for face in element['faces'].values():
                    texture = data['textures'][face['texture'][1:]].split(':')[1]
                    assert (ASSETS/f'textures/{texture}.png').is_file()
                    assert all(0 <= value <= 16 for value in face['uv'])
        # All four coordinate transforms invert to the controller.
        for f in [(0,-1),(1,0),(0,1),(-1,0)]:
            right = (-f[1],f[0]); pos=(right[0]*c-f[0]*d,r,right[1]*c-f[1]*d)
            assert (pos[0]-right[0]*c+f[0]*d,pos[1]-r,pos[2]-right[1]*c+f[1]*d)==(0,0,0)
    # Interior mouth at z14 is unobstructed below the crossbar (including meshes).
    for b in bounds:
        assert not (b[0]<134 and b[3]>10 and b[1]<62 and b[4]>2 and b[2]<=14<b[5])
    nets = [b for b,material,*_ in PARTS if material=='net']
    assert any(b[2]==46 and b[1]==2 and b[4]==62 for b in nets)
    assert any(b[0]==9.875 for b in nets)
    assert any(b[0]==134 for b in nets) and any(b[1]==61.875 for b in nets)
    assert all(b[4] <= 62 for b in nets)
    alpha = Image.open(ASSETS/'textures/block/sports_goal_net.png').getchannel('A')
    assert set(alpha.tobytes()) == {0,255}
    # Rectangular rear profile, tall central board flush with the goal crossbar.
    assert any(b[2]==46 and b[1]==62 and material=='frame' for b,material,*_ in PARTS)
    assert sum(b[2]==46 and b[1]==0 and b[4]==64 for b,material,*_ in PARTS if material=='frame') == 2
    board = [b for b,material,*_ in PARTS if material=='board']
    assert board == [[48,64,12,96,96,14]]
    assert Image.open(ASSETS/'textures/block/sports_goal_board_front.png').size == (48,32)
    # Board UVs cover the atlas once, continuously across its six saved cells.
    board_faces = [e for value in manifest['cells'].values() for e in value['elements']
                   if e['faces'].get('north',{}).get('texture') == '#board_front']
    for e in board_faces:
        u0,v0,u1,v1 = e['faces']['north']['uv']
        assert isclose((u0-u1)*3, e['to'][0]-e['from'][0])
        assert isclose((v1-v0)*2, e['to'][1]-e['from'][1])
    # Eight pixels of board are hidden where the back of the rim meets it.
    assert sum((e['to'][0]-e['from'][0])*(e['to'][1]-e['from'][1]) for e in board_faces) == 48*32-8
    basket = [b for b,material,*_ in PARTS if material=='basket_net']
    assert min(b[0] for b in basket if b[1]==64) > min(b[0] for b in basket if b[1]==69)
    texture = Image.open(ASSETS/'textures/block/sports_goal_basket_net.png')
    assert set(texture.getchannel('A').tobytes()) == {0,255}
    assert texture.getpixel((4,4))[3] == 255 and texture.getpixel((4,3))[3] == 0
    # Mirroring preserves the occupied-cell layout and the single controller.
    assert {encode(8-coordinates(i)[0],coordinates(i)[1],coordinates(i)[2]) for i in occupied} == set(occupied)
    loot=json.loads((ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json').read_text())
    assert loot['pools'][0]['conditions'][1]['properties']=={'cell':'0'}
    # The actual inventory definition renders the same assembled textured faces.
    definition=json.loads((ASSETS/f'items/{ID}.json').read_text())['model']
    item=json.loads((ASSETS/f"models/{definition['model'].split(':')[1]}.json").read_text())
    assert item['render_type'] == 'minecraft:cutout'
    expected_item=[]
    for index,value in manifest['cells'].items():
        c,r,d=coordinates(int(index))
        for element in value['elements']:
            e=json.loads(json.dumps(element))
            for side in ('from','to'):
                e[side]=[(e[side][0]+c*16-8)/8,1+(e[side][1]+r*16)/8,4+(e[side][2]+d*16)/8]
            expected_item.append(e)
    assert item['elements'] == expected_item
    print(f'OK: rectangular goal and tall board; {len(occupied)} legacy cells; 648 variants; collision, UVs, open entrance, cutout nets, inventory and single drop.')

if __name__=='__main__': main()
