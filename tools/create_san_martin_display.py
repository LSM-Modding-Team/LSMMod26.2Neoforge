"""Rebuild San Martin as a centered 1x2 sculpture in a white 2x2.5x1 display."""
import copy
import json
from PIL import Image, ImageDraw
from create_bathroom_statue_assets import ROOT, ASSETS as A, solid_model

ID = 'san_martin_de_porres'
GENERATED = []
COLORS = {'habit': (24, 27, 29), 'cream': (222, 207, 165),
          'skin': (112, 76, 53), 'hair': (29, 26, 23), 'gold': (174, 131, 55),
          'broom': (184, 139, 61), 'dog': (187, 140, 77), 'cat': (178, 177, 164),
          'base': (100, 63, 48)}
TEXTURES = {k: f'lsmmod:block/{ID}_{k}' for k in COLORS}
TEXTURES.update(white='lsmmod:block/school_porcelain',
                face=f'lsmmod:block/{ID}_face', glass=f'lsmmod:block/{ID}_glass',
                particle='lsmmod:block/school_porcelain')


def parts():
    # Interior sculpture bounds are exactly x=8..24, y=4..36 (1x2 blocks).
    statue = [([8, 4, 4, 24, 5, 13], 'base'),
              ([12, 5, 7, 21, 9, 12], 'cream'),
              ([12, 9, 7, 21, 25, 12], 'habit'),
              ([15, 9, 6.5, 18, 25, 7], 'cream'),
              ([11, 24, 7, 22, 28, 12], 'habit'),
              ([15, 27, 7, 18, 29, 11], 'cream'),
              ([15, 29, 7, 18, 30, 10], 'skin'),
              ([13.5, 30, 7, 19.5, 35, 12], 'skin', {'north': 'face'}),
              ([13.5, 35, 7, 19.5, 36, 12], 'hair'),
              ([13, 31, 8, 13.5, 33, 10], 'skin'),
              ([19.5, 31, 8, 20, 33, 10], 'skin'),
              ([10.5, 10, 7.5, 12, 25, 11.5], 'habit'),
              ([21, 10, 7.5, 22.5, 25, 11.5], 'habit'),
              ([11, 20, 6, 14, 23, 8], 'cream'),
              ([11.5, 21, 5, 14, 23, 6], 'skin'),
              ([18.5, 25, 5.5, 21.5, 27, 7], 'cream'),
              ([17.5, 25, 4.5, 20.5, 27, 5.5], 'skin'),
              ([14.5, 27, 6.5, 18, 27.5, 7], 'gold'),
              ([12, 8, 5, 12.5, 26, 5.5], 'broom'),
              ([11, 5, 4.5, 13.5, 8, 6.5], 'broom'),
              ([10.75, 5, 4.25, 11.25, 7, 6.75], 'broom'),
              ([13.25, 5, 4.25, 13.75, 7, 6.75], 'broom')]
    # Rosary beads follow two strands and finish with a distinct cross.
    for y in range(13, 26, 2):
        x = 15 if y >= 19 else 15.5
        statue += [([x, y, 6, x+.75, y+.75, 6.5], 'gold'),
                   ([18, y, 6, 18.75, y+.75, 6.5], 'gold')]
    statue += [([16.5, 9, 5.75, 17.25, 13, 6.5], 'gold'),
               ([15.5, 11, 5.75, 18.25, 11.75, 6.5], 'gold')]
    # Cat at left; sitting dog at right, with muzzles, ears and paws in relief.
    statue += [([8.5, 5, 7, 11, 8, 10], 'cat'),
               ([8, 7.5, 5.5, 11, 10, 8.5], 'cat'),
               ([8, 10, 6, 8.75, 11, 7], 'cat'),
               ([10.25, 10, 6, 11, 11, 7], 'cat'),
               ([8.75, 8, 5, 10.25, 8.75, 5.5], 'cream'),
               ([8.5, 5, 5.75, 9.25, 6, 7], 'cream'),
               ([10.25, 5, 5.75, 11, 6, 7], 'cream'),
               ([21, 5, 7, 24, 10, 11], 'dog'),
               ([21.25, 9, 6.75, 23.5, 13, 9.75], 'dog'),
               ([20.75, 12, 6, 23.5, 14, 9], 'cream'),
               ([20, 12, 5, 21.5, 13, 7], 'cream'),
               ([22.75, 11.5, 6, 23.75, 13.75, 8], 'dog'),
               ([21, 5, 5.75, 22, 9, 7], 'cream'),
               ([23, 5, 5.75, 24, 9, 7], 'cream')]
    for x, y, z in ((8.4, 9, 5.4), (10.3, 9, 5.4), (21.25, 13, 5.9), (20, 12.25, 4.9)):
        statue.append(([x, y, z, x+.35, y+.35, z+.1], 'hair'))
    case = [([0, 0, 0, 32, 4, 16], 'white'),
            ([0, 36, 0, 32, 40, 16], 'white'),
            ([0, 4, 0, 2, 36, 16], 'white'),
            ([30, 4, 0, 32, 36, 16], 'white'),
            ([2, 4, 14, 30, 36, 16], 'white')]
    glass = [([2, 4, 1, 30, 36, 1.25], 'glass')]
    return case + statue, glass


def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2)+'\n')
    GENERATED.append(str(path.relative_to(ROOT)))


def textures():
    for material, color in COLORS.items():
        image = Image.new('RGB', (16, 16), color)
        draw = ImageDraw.Draw(image)
        for x in (3, 8, 13):
            shade = tuple(max(0, c-8) for c in color)
            draw.line((x, 0, x, 15), fill=shade)
        image.save(A/f'textures/block/{ID}_{material}.png')
    face = Image.new('RGB', (16, 16), COLORS['skin'])
    d = ImageDraw.Draw(face)
    d.rectangle((0, 0, 15, 2), fill=COLORS['hair'])
    for x in (3, 10):
        d.rectangle((x, 5, x+2, 5), fill=(41, 30, 25))
        d.rectangle((x, 6, x+2, 7), fill=(181, 155, 123))
        d.point((x+1, 6), fill=(26, 24, 23))
    d.rectangle((7, 7, 8, 11), fill=(135, 94, 65))
    d.line((5, 13, 10, 13), fill=(72, 43, 35))
    face.save(A/f'textures/block/{ID}_face.png')
    glass = Image.new('RGBA', (16, 16), (210, 226, 229, 12))
    d = ImageDraw.Draw(glass)
    d.line((2, 2, 5, 5), fill=(237, 248, 250, 48))
    d.line((10, 10, 13, 13), fill=(237, 248, 250, 40))
    glass.save(A/f'textures/block/{ID}_glass.png')


def model(parts, layer):
    value = solid_model(parts)
    value.update(textures=TEXTURES, render_type='minecraft:'+layer)
    return value


def clip(model, column, row):
    result = copy.deepcopy(model)
    result['elements'] = []
    offset = (column*16, row*16, 0)
    axes = {'west': (0, False), 'east': (0, True), 'down': (1, False),
            'up': (1, True), 'north': (2, False), 'south': (2, True)}
    for source in model['elements']:
        a, b = source['from'], source['to']
        lo = [max(a[i], offset[i]) for i in range(3)]
        hi = [min(b[i], offset[i]+16) for i in range(3)]
        if any(lo[i] >= hi[i] for i in range(3)): continue
        e = copy.deepcopy(source)
        e['from'] = [lo[i]-offset[i] for i in range(3)]
        e['to'] = [hi[i]-offset[i] for i in range(3)]
        e['faces'] = {}
        for face, surface in source['faces'].items():
            axis, positive = axes[face]
            if (hi if positive else lo)[axis] != (b if positive else a)[axis]: continue
            u, v = (0, 1) if axis == 2 else (2, 1) if axis == 0 else (0, 2)
            uv = surface['uv']; s = copy.deepcopy(surface)
            s['uv'] = [uv[0]+(uv[2]-uv[0])*(lo[u]-a[u])/(b[u]-a[u]),
                       uv[1]+(uv[3]-uv[1])*((b[v]-hi[v]) if axis != 1 else (lo[v]-a[v]))/(b[v]-a[v]),
                       uv[0]+(uv[2]-uv[0])*(hi[u]-a[u])/(b[u]-a[u]),
                       uv[1]+(uv[3]-uv[1])*((b[v]-lo[v]) if axis != 1 else (hi[v]-a[v]))/(b[v]-a[v])]
            e['faces'][face] = s
        if e['faces']: result['elements'].append(e)
    return result


def generate():
    GENERATED.clear()
    textures()
    opaque, glass = parts()
    models = {'solid': model(opaque, 'solid'), 'translucent': model(glass, 'translucent')}
    states = []
    for layer, value in models.items():
        for row in range(3):
            for column in range(2):
                name = f'{ID}_{layer}_{column}_{row}'
                write(A/f'models/block/{name}.json', clip(value, column, row))
                for turn, facing in enumerate(('north', 'east', 'south', 'west')):
                    states.append({'when': {'expanded': 'true', 'facing': facing, 'column': str(column), 'row': str(row)},
                                   'apply': {'model': 'lsmmod:block/'+name, 'y': turn*90}})
        item = copy.deepcopy(value)
        item.update(loader='fusion:model', type='base')
        for e in item['elements']:
            for key in ('from', 'to'):
                x, y, z = e[key]
                e[key] = [8+(x-16)*.4, y*.4, 8+(z-8)*.4]
        write(A/f'models/item/{ID}_item_{layer}.json', item)
    # Preserve the original two-half world states; new placements use the display.
    for half, suffix in (('lower', 'down'), ('upper', 'up')):
        for facing, angle in (('north', 270), ('east', 0), ('south', 90), ('west', 180)):
            states.append({'when': {'expanded': 'false', 'half': half, 'facing': facing},
                           'apply': {'model': f'lsmmod:block/{ID}_{suffix}', 'y': angle}})
    write(A/f'blockstates/{ID}.json', {'multipart': states})
    write(A/f'models/item/{ID}.json', {'loader': 'fusion:model', 'type': 'composite',
          'textures': TEXTURES, 'models': [[f'lsmmod:item/{ID}_item_solid', f'lsmmod:item/{ID}_item_translucent']]})
    for path in (A/'models/block').glob(ID+'_part_*.json'): path.unlink()
    return list(GENERATED)


if __name__ == '__main__': generate()
