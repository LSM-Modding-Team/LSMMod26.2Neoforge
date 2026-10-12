"""Shared connected transoms and a single unsplit vanilla-glass window per leaf."""
import copy
import json
from create_school_glass_assets import A, cube
from create_horizontal_glazing_assets import glazing


def add_visual_parts(multipart, textures, write):
    textures = dict(textures, frame='lsmmod:block/school_glass_frame')
    entries = []
    for mask in range(16):
        parts = glazing(mask & 7, 8, 16, False, bool(mask & 8), [])
        # Remove only connected glass caps; whole-block culling would also erase
        # the exposed side-wall faces below this half-height transom.
        for element in parts:
            for face, material in list(element['faces'].items()):
                if material['texture'] != '#glass':
                    continue
                if (face=='west' and element['from'][0]==0 and not mask & 1
                        or face=='east' and element['to'][0]==16 and not mask & 2
                        or face=='up' and element['to'][1]==16 and mask & 8):
                    del element['faces'][face]
        parts = [e for e in parts if e['faces']]
        name = f'framed_door_transom_{mask}'
        write(A/f'models/block/{name}.json', {'loader':'fusion:model', 'type':'base',
              'parent':'minecraft:block/block', 'textures':textures,
              'render_type':'minecraft:translucent', 'elements':parts})
        entries.append({'model':'lsmmod:block/'+name,
                        'condition':{'type':'lsmmod:glazing_mask','masks':[mask, mask | 16]}})
    write(A/'models/block/framed_door_transom.json', {'loader':'fusion:model',
          'type':'composite', 'textures':textures, 'models':[entries]})
    for turn, facing in enumerate(('north','east','south','west')):
        # These fixed panels are identical for either hinge, open or closed.
        multipart.append({'when':{'facing':facing,'row':'2','depth':'0'},
                          'apply':{'model':'lsmmod:block/framed_door_transom','y':turn*90}})
    for opened in (False, True):
        for right in (False, True):
            bounds = [7.5,7.5,1,16.5,35,3]
            if opened:
                bounds = [1,7.5,7.5,3,35,16.5]
            if right:
                bounds[0], bounds[3] = 32-bounds[3], 32-bounds[0]
            column = 1 if opened and right else 0
            bounds[0] -= column*16
            bounds[3] -= column*16
            bounds[1] -= 16
            bounds[4] -= 16
            # One element, one complete UV rectangle: no cell seam or UV shrink gap.
            name = f'framed_door_window_{int(opened)}_{int(right)}'
            pane=cube(bounds,'window')
            for face in pane['faces'].values():face['uv']=[1,1,15,15]
            write(A/f'models/block/{name}.json', {'parent':'minecraft:block/block',
                  'render_type':'minecraft:cutout', 'textures':{'window':'minecraft:block/glass',
                  'particle':'minecraft:block/glass'}, 'elements':[pane]})
            for turn, facing in enumerate(('north','east','south','west')):
                multipart.append({'when':{'facing':facing,'open':str(opened).lower(),
                    'hinge':'right' if right else 'left','row':'1','column':str(column),'depth':'0'},
                    'apply':{'model':'lsmmod:block/'+name,'y':turn*90}})


def bathroom_variant(multipart, textures, write):
    overrides = {'bottom':'lsmmod:block/bathroom_door_bottom',
                 'top':'lsmmod:block/bathroom_door_top',
                 'particle':'lsmmod:block/bathroom_door_bottom'}
    result = [copy.deepcopy(entry) for entry in multipart if 'framed_door_window_' not in entry['apply']['model']]
    for entry in result:
        name = entry['apply']['model'].split(':')[1]
        if name.startswith('block/dining_door_wide_part_'):
            new = name.replace('dining_door_wide','bathroom_door_wide')
            write(A/f'models/{new}.json', {'parent':'lsmmod:'+name,'textures':overrides})
            entry['apply']['model'] = 'lsmmod:'+new
    write(A/'blockstates/bathroom_door_wide.json', {'multipart':result})
    layers = []
    for layer in ('solid','translucent'):
        name = f'bathroom_door_wide_item_{layer}'
        write(A/f'models/item/{name}.json', {'loader':'fusion:model','type':'base',
              'parent':f'lsmmod:item/dining_door_wide_item_{layer}','textures':overrides})
        layers.append('lsmmod:item/'+name)
    (A/'models/item/bathroom_door_wide_item_cutout.json').unlink(missing_ok=True)
    write(A/'models/item/bathroom_door_wide.json', {'loader':'fusion:model',
          'type':'composite','textures':dict(textures,**overrides),'models':[layers]})


def item_transom(write):
    path = A/'models/item/dining_door_wide_item_translucent.json'
    model = json.loads(path.read_text())
    model['textures']['frame'] = 'lsmmod:block/school_glass_frame'
    model['elements'] = []
    for column, mask in enumerate((1,2)):
        parts = json.loads((A/f'models/block/framed_door_transom_{mask}.json').read_text())['elements']
        for part in copy.deepcopy(parts):
            for key in ('from','to'):
                x,y,z = part[key]
                part[key] = [8+(x+column*16-16)/3, (y+32)/3, 8+(z-8)/3]
            for face in part['faces'].values():
                face.pop('cullface',None)
            model['elements'].append(part)
    write(path,model)
