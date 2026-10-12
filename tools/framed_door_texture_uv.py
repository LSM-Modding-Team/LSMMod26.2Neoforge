"""Map the small door's two 16px texture halves onto one 24x40px leaf."""
import json
from create_school_glass_assets import A


def leaf_face(element, face, material, column=0, row=0, depth=0, opened=False, right=False):
    lo = [v+base for v,base in zip(element['from'],(column*16,row*16,depth*16))]
    hi = [v+base for v,base in zip(element['to'],(column*16,row*16,depth*16))]
    if right:
        lo[0],hi[0] = 32-hi[0],32-lo[0]
        face = {'east':'west','west':'east'}.get(face,face)
    if opened:
        lo = [lo[2],lo[1],lo[0]]
        hi = [hi[2],hi[1],hi[0]]
        face = {'west':'north','east':'south','north':'west','south':'east'}.get(face,face)
    # Both halves scale equally: 16 -> 20px, not 16 -> 16/24px.
    end = 20 if material['texture']=='#bottom' else 40
    v0,v1 = (end-hi[1])*16/20,(end-lo[1])*16/20
    u0,u1 = lo[0]*16/24,hi[0]*16/24
    if face in ('north','south'):
        material['uv'] = [u1,v0,u0,v1] if face=='north' else [u0,v0,u1,v1]
    elif face in ('east','west'):
        # Native door thickness uses the first three columns, not a whole face.
        t0,t1 = lo[2]*3/4,hi[2]*3/4
        material['uv'] = [t1,v0,t0,v1] if face=='west' else [t0,v0,t1,v1]
    else:
        t0,t1 = lo[2]*3/4,hi[2]*3/4
        material['uv'] = [u0,t0,u1,t1] if face=='up' else [u1,13+t0,u0,13+t1]


def fix_item(write):
    path = A/'models/item/dining_door_wide_item_solid.json'
    model = json.loads(path.read_text())
    model['render_type'] = 'minecraft:cutout'
    for element in model['elements']:
        original = {'from':[(element['from'][0]-8)*3+16,element['from'][1]*3,(element['from'][2]-8)*3+8],
                    'to':[(element['to'][0]-8)*3+16,element['to'][1]*3,(element['to'][2]-8)*3+8]}
        for face, material in element['faces'].items():
            if material['texture'] in ('#bottom','#top'):
                leaf_face(original,face,material)
                material['uv'] = [round(v,10) for v in material['uv']]
    write(path,model)
