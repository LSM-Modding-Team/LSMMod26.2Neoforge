"""Static geometry/resource checks; does not compile or launch Minecraft."""
import json
import re
from ast import literal_eval
from math import isclose
from PIL import Image
from create_sports_goal_assets import ROOT, ASSETS, PARTS, ID, OWNERS, clips, coordinates, index as encode

def main():
    manifest = json.loads((ROOT/'tools/sports_goal_geometry.json').read_text())
    occupied = manifest['occupied']
    assert len(occupied) == len(set(occupied)) and 0 in occupied
    assert len(manifest['cells']) == 162
    legacy = {encode(c,r,d) for d in range(3) for r in range(4) for c in range(9)
              if d == 2 or r == 3 or c in (0,8)}
    legacy |= {encode(c,r,0) for r in (4,5) for c in (3,4,5)}
    assert set(occupied) == legacy
    java = (ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/balls/block/SportsGoalShapes.java').read_text()
    saved = java.split('OCCUPIED = ',1)[1].split(';',1)[0]
    assert literal_eval(saved.replace('{','[').replace('}',']')) == occupied
    calls = java.split('CELLS = ',1)[1].split(';',1)[0]
    assert list(map(int,re.findall(r'cell(\d+)\(\)',calls))) == list(range(162))
    for i in range(162):
        boxes = re.search(r'cell'+str(i)+r'\(\) \{\s*return new double\[\]\[\] (\{.*?\});',java).group(1)
        assert literal_eval(boxes.replace('{','[').replace('}',']')) == manifest['cells'][str(i)]['collision']
    material_cells = {kind: {i: [] for i in range(162)} for kind in ('net','rigid')}
    for d in range(3):
        for r in range(6):
            for c in range(9):
                owner = OWNERS[encode(c,r,d)]
                oc,orr,od = coordinates(owner)
                origin = [16*oc,16*orr,16*od]
                for bounds,material,*_ in PARTS:
                    part = clips(bounds,c,r,d)
                    if part:
                        low,high,_ = part
                        material_cells['net' if material == 'net' else 'rigid'][owner].append(
                            [low[a]-origin[a] for a in range(3)]+[high[a]-origin[a] for a in range(3)])
    for kind,cells in material_cells.items():
        for i,expected in cells.items():
            boxes = re.search(kind+str(i)+r'\(\) \{\s*return new double\[\]\[\] (\{.*?\});',java).group(1)
            assert literal_eval(boxes.replace('{','[').replace('}',']')) == expected
    bounds = [p[0] for p in PARTS]
    assert [min(b[a] for b in bounds) for a in range(3)] == [8, 0, 0]
    assert [max(b[a+3] for b in bounds) for a in range(3)] == [136, 96, 48]
    variants = json.loads((ASSETS/f'blockstates/{ID}.json').read_text())['variants']
    assert len(variants) == 648
    for index in range(162):
        cell = manifest['cells'][str(index)]
        c,r,d = coordinates(index)
        assert not cell['collision'] or index in occupied
        for b in cell['collision']:
            assert all(-16 <= b[a] < b[a+3] <= 32 for a in range(3))
            rotated = b
            for turn in range(4):
                rotated = [16-rotated[5], rotated[1], rotated[0], 16-rotated[2], rotated[4], rotated[3]]
                assert all(-16 <= rotated[a] < rotated[a+3] <= 32 for a in range(3))
            assert rotated == b
        for facing in ['north','east','south','west']:
            model = variants[f'cell={index},facing={facing}']['model'].split(':')[1]
            data = json.loads((ASSETS/f'models/{model}.json').read_text())
            assert data['elements'] == cell['elements']
            for element in data['elements']:
                assert all(-16 <= element['from'][a] < element['to'][a] <= 32 for a in range(3))
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
        assert not (b[0]<134 and b[3]>10 and b[1]<46 and b[4]>2 and b[2]<=14<b[5])
    nets = [b for b,material,*_ in PARTS if material=='net']
    assert any(b[2]==46 for b in nets) and any(b[0]==9.875 for b in nets)
    assert any(b[0]==134 for b in nets) and any(b[1]==45.875 for b in nets)
    alpha = Image.open(ASSETS/'textures/block/sports_goal_net.png').getchannel('A')
    assert set(alpha.tobytes()) == {0,255}
    # Five-sided net: lower vertical back, sloping upper back and short flat roof.
    assert any(b[2]==30 and b[1]==46 and material=='frame' for b,material,*_ in PARTS)
    assert sum(b[2]==46 and b[1]==0 and b[4]==24 for b,material,*_ in PARTS if material=='frame') == 2
    assert all(b[4] <= 46 for b in nets)
    assert not any(b[3]-b[0]>100 and b[1]>=48 for b,material,*_ in PARTS if material=='frame')
    world_boxes = []
    for cell,value in manifest['cells'].items():
        c,r,d = coordinates(int(cell))
        origin = [16*c,16*r,16*d]
        world_boxes += [[b[a]+origin[a%3] for a in range(6)] for b in value['collision']]
    def solid_at(point):
        return any(all(b[a] <= point[a] <= b[a+3] for a in range(3)) for b in world_boxes)
    # Ownership may move geometry into adjacent saved cells without losing a part.
    for b in bounds:
        assert solid_at(tuple((b[a]+b[a+3])/2 for a in range(3)))
    assert not solid_at((72,40.5,46.05))  # A rectangular rear wall must be absent.
    assert solid_at((72,40.5,35.35))     # The real mesh recedes towards the mouth.
    assert not solid_at((9.95,40.5,44))  # Cut-off upper-back corner in each side net.
    assert solid_at((9.95,40.5,34))
    # Gameplay material probes: the mouth stays open, while back/side/roof mesh
    # catches a ball and the posts/crossbar remain rigid in all orientations.
    world_materials = {kind: [] for kind in material_cells}
    for kind,cells in material_cells.items():
        for cell,boxes in cells.items():
            c,r,d = coordinates(cell)
            origin = [16*c,16*r,16*d]
            world_materials[kind] += [[b[a]+origin[a%3] for a in range(6)] for b in boxes]
    probes = [((72,12,46.0625),'net'), ((72,40.5,35.35),'net'),
              ((9.9375,12,32),'net'), ((134.0625,12,32),'net'),
              ((72,45.9375,22),'net'), ((9,12,15),'rigid'),
              ((72,47,15),'rigid'), ((72,20,14),'open')]
    def rotate_point(point):
        return (16-point[2],point[1],point[0])
    def rotate_box(box):
        return [16-box[5],box[1],box[0],16-box[2],box[4],box[3]]
    for turn in range(4):
        for point,expected in probes:
            found = 'open'
            for kind in ('net','rigid'):
                for box in world_materials[kind]:
                    rotated = box
                    for _ in range(turn): rotated = rotate_box(rotated)
                    if all(rotated[a] <= point[a] <= rotated[a+3] for a in range(3)):
                        found = kind; break
            assert found == expected,(turn,point,found,expected)
        probes = [(rotate_point(point),kind) for point,kind in probes]
    frames = [b for b,material,*_ in PARTS if material=='frame']
    for b in frames:
        reflected = [144-b[3],b[1],b[2],144-b[0],b[4],b[5]]
        assert any(all(isclose(v,w,abs_tol=1e-8) for v,w in zip(reflected,other)) for other in frames)
    # No football mesh fills the front basketball support tower above the mouth.
    assert not any(b[0]<90 and b[3]>54 and b[1]>=48 and b[2]<=14 for b in nets)
    basket = [b for b,material,*_ in PARTS if material=='basket_net']
    assert min(b[0] for b in basket if b[1]==64) > min(b[0] for b in basket if b[1]==71)
    texture = Image.open(ASSETS/'textures/block/sports_goal_basket_net.png')
    assert set(texture.getchannel('A').tobytes()) == {0,255}
    assert texture.getpixel((4,4))[3] == 255 and texture.getpixel((4,3))[3] == 0
    # Mirroring preserves the occupied-cell layout and the single controller.
    assert {encode(8-coordinates(i)[0],coordinates(i)[1],coordinates(i)[2]) for i in occupied} == set(occupied)
    loot=json.loads((ROOT/f'src/main/resources/balls/data/lsmmod/loot_table/blocks/{ID}.json').read_text())
    assert loot['pools'][0]['conditions'][1]['properties']=={'cell':'0'}
    print(f'OK: five-sided net, rigid frame and open-mouth material probes in 4 orientations; {len(occupied)} saved logical cells; 648 variants; collision, native bounds, rotations, UVs, cutout nets and single drop.')

if __name__=='__main__': main()
