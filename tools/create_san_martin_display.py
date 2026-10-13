"""Rebuild San Martin as a centered 1x2 sculpture in a white 2x2.5x1 display."""
from resource_json import resource_json
import copy
import json
from PIL import Image, ImageDraw
from create_bathroom_statue_assets import ROOT, ASSETS as A, solid_model, crop_face_uv

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


def sculpture():
    # Author the entire north-facing figure in world pixels, before any cell cuts.
    # The base remains 16px wide; the slimmer head is about one seventh of its height.
    statue = [
        ([8, 4, 4, 24, 4.75, 13], 'base'),
        ([8.5, 4.75, 4.5, 23.5, 5, 12.5], 'base'),
        # White underhabit with separate folds and a dark, slightly flared mantle.
        ([12, 5, 7, 20, 10, 11.75], 'cream'),
        ([12.5, 5, 6.75, 14, 9, 7], 'cream'),
        ([15.25, 5, 6.5, 16.75, 9, 7], 'cream'),
        ([18, 5, 6.75, 19.5, 9, 7], 'cream'),
        ([11.25, 9, 6.75, 20.75, 18, 12], 'habit'),
        ([11.5, 18, 7, 20.5, 24, 11.75], 'habit'),
        ([12, 24, 7.25, 20, 27.5, 11.5], 'habit'),
        ([13, 27.5, 7.5, 19, 28.5, 11.25], 'habit'),
        # Thin cream edging belongs to the mantle, not a wide central stripe.
        ([11.25, 9, 6.5, 11.5, 18, 6.75], 'cream'),
        ([20.5, 9, 6.5, 20.75, 18, 6.75], 'cream'),
        ([11.5, 18, 6.75, 11.75, 24, 7], 'cream'),
        ([20.25, 18, 6.75, 20.5, 24, 7], 'cream'),
        ([11.25, 8.75, 6.5, 20.75, 9, 12], 'cream'),
        # Side drapery and sleeves form continuous stepped volumes.
        ([10.5, 10, 7.5, 11.25, 20, 11.75], 'habit'),
        ([20.75, 10, 7.5, 21.5, 20, 11.75], 'habit'),
        ([10.75, 20, 7, 12, 25.5, 11.5], 'habit'),
        ([20, 20, 7, 21.25, 26, 11.5], 'habit'),
        ([11.5, 25.5, 7.25, 13, 27.5, 11.5], 'habit'),
        ([19, 26, 7.25, 20.5, 27.5, 11.5], 'habit'),
        # Lower hand wraps around the broom; the other rests against the chest.
        ([11, 19.5, 6, 13.75, 22.5, 8], 'habit'),
        ([11.5, 20.25, 5.5, 13.5, 22.25, 6.25], 'cream'),
        ([11.75, 21, 4.75, 13.25, 22.25, 5.5], 'skin'),
        ([12, 20.5, 4.75, 12.75, 21, 5.5], 'skin'),
        ([18.5, 24, 6, 21, 26, 8], 'habit'),
        ([18, 25, 5.5, 20.25, 27, 6.5], 'habit'),
        ([17.75, 25.5, 5.25, 18.75, 27.25, 6], 'cream'),
        ([16.5, 26, 4.75, 17.75, 27.25, 5.75], 'skin'),
        ([16.25, 25.75, 5, 16.75, 26.5, 5.75], 'skin'),
        # Collar, neck and bevels avoid a broad cubic head and jutting chin.
        ([14, 27.75, 7, 18, 29, 10.75], 'cream'),
        ([14.5, 29, 7.75, 17.5, 30.25, 10.25], 'skin'),
        ([14.5, 30, 7.25, 17.5, 30.5, 10.5], 'skin'),
        ([14, 30.5, 7, 18, 34.75, 10.75], 'skin', {'north': 'face'}),
        ([14.25, 34.75, 7.25, 17.75, 35.5, 10.5], 'hair'),
        ([14, 33.75, 10.5, 18, 34.75, 10.75], 'hair'),
        ([14, 34, 7, 14.5, 34.75, 10.5], 'hair'),
        ([17.5, 34, 7, 18, 34.75, 10.5], 'hair'),
        ([13.75, 31.75, 8, 14, 33, 9], 'skin'),
        ([18, 31.75, 8, 18.25, 33, 9], 'skin'),
        # A small crucifix held at the chest; fine rosary ends above the hem.
        ([16.25, 26.5, 4.5, 16.625, 29.75, 4.75], 'gold'),
        ([15.25, 28.25, 4.5, 17.625, 28.625, 4.75], 'gold'),
        ([15.875, 10.5, 6.125, 16.25, 13, 6.5], 'gold'),
        ([15.125, 11.875, 6.125, 17, 12.25, 6.5], 'gold'),
        # One continuous wooden handle and tiered straw, with a binding.
        ([12.125, 7.25, 4.5, 12.5, 23, 4.875], 'base'),
        ([11.625, 7, 4.125, 13, 8.25, 5.75], 'broom'),
        ([11.375, 6, 4, 13.25, 7, 6], 'broom'),
        ([11.125, 5, 3.875, 13.5, 6, 6.125], 'broom'),
        ([11.5, 6.75, 3.875, 13.125, 7, 4.125], 'gold'),
        # Small seated cat at the left: paws, muzzle, ears and curling tail.
        ([8.75, 5, 7.25, 11, 7, 10], 'cat'),
        ([9.25, 7, 6.75, 11, 8.5, 8.75], 'cat'),
        ([9.25, 8.5, 7, 9.75, 9, 7.75], 'cat'),
        ([10.5, 8.5, 7, 11, 9, 7.75], 'cat'),
        ([9.5, 7.125, 6.375, 10.75, 7.75, 6.75], 'cream'),
        ([9.25, 5, 6.5, 9.75, 6.5, 7.5], 'cream'),
        ([10.25, 5, 6.5, 10.75, 6.5, 7.5], 'cream'),
        ([8.5, 5, 8, 8.75, 6.25, 10.5], 'cat'),
        ([8.5, 5, 10.25, 10.5, 5.5, 10.5], 'cat'),
        # Dog at the right: white chest, hanging ears and a projecting muzzle.
        ([21, 5, 7, 23.5, 8, 10.5], 'dog'),
        ([21.25, 7.75, 7, 23.25, 10.25, 9.5], 'dog'),
        ([21, 10, 6.5, 23.25, 11.5, 9], 'dog'),
        ([21.5, 8, 6.75, 22.75, 10, 7], 'cream'),
        ([21.25, 10, 5.75, 22.75, 10.75, 6.5], 'cream'),
        ([20.75, 9.5, 7, 21.25, 11.25, 8.5], 'base'),
        ([23.25, 9.5, 7, 23.75, 11.25, 8.5], 'base'),
        ([21, 5, 6.5, 21.625, 8, 7.25], 'cream'),
        ([22.75, 5, 6.5, 23.375, 8, 7.25], 'cream'),
        ([23.5, 5, 8.5, 23.75, 7, 10.5], 'dog'),
        # The shared bowl sits on the pedestal, in front of the robe.
        ([14.5, 5, 4.5, 17.5, 5.25, 6], 'cream'),
        ([14.5, 5.25, 4.5, 17.5, 5.5, 4.75], 'cream'),
        ([14.5, 5.25, 5.75, 17.5, 5.5, 6], 'cream'),
        ([14.5, 5.25, 4.75, 14.75, 5.5, 5.75], 'cream'),
        ([17.25, 5.25, 4.75, 17.5, 5.5, 5.75], 'cream'),
    ]
    for y in range(14, 27):
        spread = min(1.5, (y-13)*.125)
        for x in (16-spread, 16+spread):
            # Beads attach to thin strands, instead of floating gold buttons.
            statue.append(([x-.0625, y-.5, 6.375, x+.0625, y+.5, 6.5], 'base'))
            if y % 2 == 0:
                statue.append(([x-.1875, y-.1875, 6.125, x+.1875, y+.1875, 6.5], 'base'))
    statue.append(([15.9375, 13, 6.375, 16.0625, 14, 6.5], 'base'))
    for x, y, z in ((9.5, 8, 6.625), (10.5, 8, 6.625),
                    (21.375, 10.875, 6.375), (22.625, 10.875, 6.375)):
        statue.append(([x, y, z, x+.25, y+.25, z+.125], 'hair'))
    statue += [([10, 7.375, 6.25, 10.25, 7.625, 6.375], 'base'),
               ([21.75, 10.25, 5.5, 22.25, 10.625, 5.75], 'hair')]
    return statue


def parts():
    case = [([0, 0, 0, 32, 4, 16], 'white'),
            ([0, 36, 0, 32, 40, 16], 'white'),
            ([0, 4, 0, 2, 36, 16], 'white'),
            ([30, 4, 0, 32, 36, 16], 'white'),
            ([2, 4, 14, 30, 36, 16], 'white')]
    glass = [([2, 4, 1, 30, 36, 1.25], 'glass')]
    return case + sculpture(), glass


def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(resource_json(path, data, ensure_ascii=False)+'\n')
    GENERATED.append(str(path.relative_to(ROOT)))


def textures():
    for material, color in COLORS.items():
        # One complete 16x32 fabric texture covers the whole robe vertically.
        height = 32 if material in ('habit', 'cream') else 16
        image = Image.new('RGB', (16, height), color)
        for y in range(height):
            for x in range(16):
                delta = (-5, -2, 1, 2, 0, -1, 0, 1)[x % 8]
                if material == 'broom': delta = -14 if x % 3 == 0 else 2
                if material not in ('habit', 'cream', 'broom', 'base'): delta //= 3
                image.putpixel((x, y), tuple(max(0, min(255, c+delta)) for c in color))
        image.save(A/f'textures/block/{ID}_{material}.png')
    face = Image.new('RGB', (16, 16), COLORS['skin'])
    d = ImageDraw.Draw(face)
    d.rectangle((0, 0, 15, 1), fill=COLORS['hair'])
    d.rectangle((0, 2, 1, 14), fill=(100, 67, 46))
    d.rectangle((14, 2, 15, 14), fill=(100, 67, 46))
    for x in (3, 10):
        d.line((x, 5, x+2, 5), fill=(48, 33, 26))
        d.line((x, 6, x+2, 6), fill=(161, 139, 111))
        d.point((x+1, 6), fill=(27, 23, 20))
    d.rectangle((7, 7, 8, 10), fill=(129, 86, 58))
    d.line((7, 11, 9, 11), fill=(89, 57, 41))
    d.line((6, 13, 10, 13), fill=(81, 49, 39))
    d.line((6, 14, 10, 14), fill=(120, 81, 56))
    face.save(A/f'textures/block/{ID}_face.png')
    glass = Image.new('RGBA', (16, 16), (210, 226, 229, 12))
    d = ImageDraw.Draw(glass)
    d.line((2, 2, 5, 5), fill=(237, 248, 250, 48))
    d.line((10, 10, 13, 13), fill=(237, 248, 250, 40))
    glass.save(A/f'textures/block/{ID}_glass.png')


def face_uv(original, exposed, face, material):
    if material in ('habit', 'cream'):
        # Global projection prevents folds from restarting at box/occlusion boundaries.
        low, high = exposed[:3], exposed[3:]
        if face in ('north', 'south'):
            u0, u1 = (24-high[0], 24-low[0]) if face == 'north' else (low[0]-8, high[0]-8)
        elif face in ('east', 'west'):
            u0, u1 = (16-high[2], 16-low[2]) if face == 'east' else (low[2], high[2])
        else:
            u0, u1 = (24-high[0], 24-low[0]) if face == 'down' else (low[0]-8, high[0]-8)
        v0, v1 = ((36-high[1])/2, (36-low[1])/2) if face not in ('up', 'down') else (low[2]/2, high[2]/2)
        return [u0, v0, u1, v1]
    # Occlusion can divide the original face too: crop it instead of repainting it.
    return crop_face_uv([0, 0, 16, 16], face, original[:3], original[3:], exposed[:3], exposed[3:])


def model(parts, layer):
    value = solid_model(parts, face_uv=face_uv)
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
            s = copy.deepcopy(surface)
            s['uv'] = crop_face_uv(surface['uv'], face, a, b, lo, hi)
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
