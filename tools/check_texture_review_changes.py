"""Check source-texture sampling, invisible cell caps, opaque joins and reviewed assets."""
import hashlib
import itertools
import json
from PIL import Image
from render_texture_review import ROOT, A, door, load
from create_connected_tinted_glass import ID, main as generate
from audit_block_states import audit


def main():
    samples = 0
    for ident,original in [('dining_door_wide','dining_door'),('bathroom_door_wide','bathroom_door')]:
        source = {half:Image.open(A/f'textures/block/{original}_{half}.png').convert('RGBA') for half in ('bottom','top')}
        for right in (False,True):
            scene = door(ident,False,right)
            wood = [e for e in scene if any(f['texture'] in ('lsmmod:block/'+original+'_bottom','lsmmod:block/'+original+'_top') for f in e['faces'].values())]
            for face in ('north','south'):
                for ix,iy in itertools.product(range(24),range(40)):
                    x,y = (8 if right else 0)+ix+.5,iy+.5
                    candidates = [e for e in wood if face in e['faces'] and e['from'][0]<=x<e['to'][0] and e['from'][1]<=y<e['to'][1]]
                    assert len(candidates)==1,(ident,right,face,x,y)
                    e = candidates[0];material = e['faces'][face]
                    lo,hi = e['from'],e['to'];u0,v0,u1,v1 = material['uv']
                    t = (hi[0]-x)/(hi[0]-lo[0]) if face=='north' else (x-lo[0])/(hi[0]-lo[0])
                    u = u0+(u1-u0)*t;v = v0+(v1-v0)*(hi[1]-y)/(hi[1]-lo[1])
                    actual = Image.open(A/f"textures/{material['texture'].split(':')[1]}.png").convert('RGBA').getpixel((min(15,int(u)),min(15,int(v))))
                    half,end = ('bottom',20) if y<20 else ('top',40)
                    expected = source[half].getpixel((min(15,int((32-x if right else x)*16/24)),min(15,int((end-y)*16/20))))
                    assert actual==expected,(ident,right,face,x,y,actual,expected)
                    samples += 1
            # There must be no opaque cap inside the transparent window at cell joins.
            for opened in (False,True):
                for e in door(ident,opened,right):
                    for face,material in e['faces'].items():
                        if material['texture'] not in ('lsmmod:block/'+original+'_bottom','lsmmod:block/'+original+'_top'):
                            continue
                        axis,positive = {'west':(0,False),'east':(0,True),'down':(1,False),'up':(1,True),'north':(2,False),'south':(2,True)}[face]
                        plane = e['to' if positive else 'from'][axis]
                        assert not (axis==1 and plane in (16,32))
                        assert not (axis==(2 if opened else 0) and plane==16)
    rows = {r['id']:r for r in audit()}
    assert rows[ID]['states']==rows['school_glass']['states']==68
    assert rows['school_tinted_glass']['states']==4
    wrapper = json.loads((A/f'models/block/{ID}.json').read_text())
    assert {e['condition']['mask'] for e in wrapper['models'][0]}==set(range(32))
    for mask in range(32):
        model, = load('lsmmod:block/'+ID,mask)
        assert model['elements'] and model['render_type']=='minecraft:solid'
        assert model['textures']['glass']=='lsmmod:block/school_tinted_glass'
        assert all(0<=e['from'][i]<e['to'][i]<=16 for e in model['elements'] for i in range(3))
    tinted = Image.open(A/'textures/block/school_tinted_glass.png').convert('RGBA')
    assert tinted.getextrema()[3]==(255,255)
    wood = Image.open(A/'textures/block/school_wood.png').convert('RGB')
    assert max(wood.getcolors(256))[1]==(150,108,62)
    models = list((A/'models/block').glob(ID+'*.json'))
    before = {p:hashlib.sha256(p.read_bytes()).digest() for p in models}
    generate()
    assert all(hashlib.sha256(p.read_bytes()).digest()==h for p,h in before.items())
    for name in ('wide_doors_texture_review','wide_doors_right_hinge_review','wide_doors_back_review',
                 'wide_doors_inventory_review','wide_doors_original_comparison','tinted_glass_and_counter_review','school_wood_texture_review'):
        p = ROOT/f'previews/{name}.png'
        assert p.exists()
        im = Image.open(p);assert im.width>=420 and im.height>=500
    print(f'OK: {samples} source-texture samples match both small doors, front/back and both hinges; no invented wood caps; 32 opaque tinted joins, 68 curtain-compatible states, original independent glass retained; light school wood, renders saved and generator idempotent.')


if __name__=='__main__':
    main()
