"""Reference-based closed display refrigerator; shared furniture lifecycle and render layers."""
from resource_modules import module_read_text, module_write_text, output_path
from resource_json import resource_json
import copy
import json
from pathlib import Path

from PIL import Image, ImageDraw
from create_bathroom_statue_assets import solid_model
from create_facility_expansion_assets import basic, layered_item

ROOT = Path(__file__).resolve().parents[1]
A = ROOT / 'src/main/resources/assets/lsmmod'
J = ROOT / 'src/main/java/net/nicomar2009/lsmmod'
D = ROOT / 'src/main/resources/data'
ID = 'school_refrigerator'
TEXTURES = {
    'black': 'lsmmod:block/school_entrance_black_metal',
    'white': 'lsmmod:block/school_porcelain',
    'wire': 'lsmmod:block/school_chrome',
    'glass': 'lsmmod:block/school_refrigerator_glass',
    'particle': 'lsmmod:block/school_entrance_black_metal',
}


def parts():
    opaque = [
        ([0, 0, 1, 32, 3, 16], 'black'),
        ([0, 41, 1, 32, 48, 16], 'black'),
        ([0, 3, 1, 1.5, 41, 16], 'black'),
        ([30.5, 3, 1, 32, 41, 16], 'black'),
        ([1.5, 3, 14.5, 30.5, 41, 16], 'black', {'north': 'white'}),
        ([1.5, 3, 3, 2, 41, 14.5], 'white'),
        ([30, 3, 3, 30.5, 41, 14.5], 'white'),
        ([2, 3, 3, 30, 3.5, 14.5], 'white'),
        ([2, 40.5, 3, 30, 41, 14.5], 'white'),
        ([1, 42, .875, 31, 47, 1], 'white'),
    ]
    # Two complete dark door frames and central handles, without a middle shelf post.
    for x in (0, 16):
        opaque.extend([
            ([x, 3, 1, x+1.5, 41, 3], 'black'),
            ([x+14.5, 3, 1, x+16, 41, 3], 'black'),
            ([x+1.5, 3, 1, x+14.5, 4.5, 3], 'black'),
            ([x+1.5, 39.5, 1, x+14.5, 41, 3], 'black'),
        ])
    for x in (13.5, 17.75):
        opaque.extend([
            ([x, 18, 0, x+.75, 28, .75], 'black'),
            ([x, 18, .75, x+.75, 19, 1], 'black'),
            ([x, 27, .75, x+.75, 28, 1], 'black'),
        ])
    # Four open wire shelves: geometry, rather than opaque painted rectangles.
    for y in (9, 19, 29, 38):
        for z in (3.5, 13.5):
            opaque.append(([2, y, z, 30, y+.375, z+.375], 'wire'))
        for x in (2, 29.625):
            opaque.append(([x, y, 3.875, x+.375, y+.375, 13.5], 'wire'))
        for x in range(3, 30, 2):
            opaque.append(([x, y, 3.875, x+.25, y+.25, 13.5], 'wire'))
    glass = [([x+1.5, 4.5, 1.25, x+14.5, 39.5, 1.5], 'glass') for x in (0, 16)]
    return opaque, glass


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    module_write_text(path, resource_json(path, value, ensure_ascii=False)+'\n', encoding='utf-8')


def mesh(parts, layer):
    model = solid_model(parts)
    model.update(textures=TEXTURES, render_type='minecraft:'+layer)
    # World-space UVs retain pixel density and stay continuous across cell cuts.
    for e in model['elements']:
        for face, surface in e['faces'].items():
            lo, hi = e['from'], e['to']
            if surface['texture'] == '#glass':
                surface['uv'] = [lo[0] % 16, (48-hi[1])/3, hi[0] % 16, (48-lo[1])/3]
            elif face in ('north', 'south'):
                surface['uv'] = [lo[0] % 16, 16-hi[1] % 16, lo[0] % 16+hi[0]-lo[0], 16-hi[1] % 16+hi[1]-lo[1]]
            else:
                axes = (2, 1) if face in ('east', 'west') else (0, 2)
                u, v = axes
                surface['uv'] = [0, 0, hi[u]-lo[u], hi[v]-lo[v]]
    return model


def clip(model, column, row):
    result = copy.deepcopy(model)
    result['elements'] = []
    axes = {'west': (0, 0), 'east': (0, 1), 'down': (1, 0), 'up': (1, 1), 'north': (2, 0), 'south': (2, 1)}
    offset = [16*column, 16*row, 0]
    for source in model['elements']:
        lo = [max(source['from'][i], offset[i]) for i in range(3)]
        hi = [min(source['to'][i], offset[i]+16) for i in range(3)]
        if any(a >= b for a, b in zip(lo, hi)):
            continue
        e = copy.deepcopy(source)
        e['from'] = [lo[i]-offset[i] for i in range(3)]
        e['to'] = [hi[i]-offset[i] for i in range(3)]
        e['faces'] = {}
        for face, original in source['faces'].items():
            axis, positive = axes[face]
            # Preserve only the original surface; cuts never acquire internal caps.
            if (hi if positive else lo)[axis] != source['to' if positive else 'from'][axis]:
                continue
            surface = copy.deepcopy(original)
            u, v = (0, 1) if axis == 2 else (2, 1) if axis == 0 else (0, 2)
            uv = original['uv']
            a, b = source['from'], source['to']
            # These UVs use top-to-bottom V on vertical faces.
            surface['uv'] = [
                uv[0]+(uv[2]-uv[0])*(lo[u]-a[u])/(b[u]-a[u]),
                uv[1]+(uv[3]-uv[1])*((b[v]-hi[v]) if axis != 1 else (lo[v]-a[v]))/(b[v]-a[v]),
                uv[0]+(uv[2]-uv[0])*(hi[u]-a[u])/(b[u]-a[u]),
                uv[1]+(uv[3]-uv[1])*((b[v]-lo[v]) if axis != 1 else (hi[v]-a[v]))/(b[v]-a[v]),
            ]
            if surface['texture'] != '#glass':
                local_lo, local_hi = e['from'], e['to']
                surface['uv'] = [local_lo[u], 16-local_hi[v] if axis != 1 else local_lo[v],
                                 local_hi[u], 16-local_lo[v] if axis != 1 else local_hi[v]]
            e['faces'][face] = surface
        if e['faces']:
            result['elements'].append(e)
    return result


def main():
    glass = Image.new('RGBA', (16, 48), (184, 207, 203, 20))
    draw = ImageDraw.Draw(glass)
    for start, end in (((1, 4), (8, 25)), ((2, 4), (9, 25)), ((10, 27), (15, 43))):
        draw.line((start, end), fill=(228, 241, 238, 50), width=1)
    glass.save(output_path(A/f'textures/block/{ID}_glass.png'))
    opaque, transparent = parts()
    models = {'solid': mesh(opaque, 'solid'), 'translucent': mesh(transparent, 'translucent')}
    multipart = []
    for layer, model in models.items():
        shared = {}
        for row in range(3):
            for column in range(2):
                local = clip(model, column, row)
                key = json.dumps(local, sort_keys=True)
                if key not in shared:
                    name = f'{ID}_{layer}_{len(shared)}'
                    shared[key] = name
                    write(A/f'models/block/{name}.json', local)
                for turn, facing in enumerate(('north', 'east', 'south', 'west')):
                    multipart.append({'when': {'column': str(column), 'row': str(row), 'facing': facing},
                                      'apply': {'model': 'lsmmod:block/'+shared[key], 'y': turn*90}})
    write(A/f'blockstates/{ID}.json', {'multipart': multipart})
    # Use the existing layered item helper, then apply the exact world model UVs.
    layered_item(ID, [('solid', opaque), ('translucent', transparent)], TEXTURES, 32, 48)
    for layer, model in models.items():
        item = copy.deepcopy(model)
        item['elements'] = []
        for row in range(3):
            for column in range(2):
                for e in clip(model, column, row)['elements']:
                    for key in ('from', 'to'):
                        e[key][0] += column*16
                        e[key][1] += row*16
                    item['elements'].append(e)
        item.update(loader='fusion:model', type='base')
        for e in item['elements']:
            for key in ('from', 'to'):
                x, y, z = e[key]
                e[key] = [8+(x-16)/3, y/3, 8+(z-8)/3]
        write(A/f'models/item/{ID}_item_{layer}.json', item)
    basic(ID, controller={'column': '0', 'row': '0'})
    for file, entry in (
        ('ModBlocks.java', '    public static final DeferredBlock<SchoolRefrigeratorBlock> SCHOOL_REFRIGERATOR = BLOCKS.registerBlock("school_refrigerator", SchoolRefrigeratorBlock::new, props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> SCHOOL_REFRIGERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_REFRIGERATOR);'),
    ):
        path = J/'registry'/file
        text = module_read_text(path)
        if ' SCHOOL_REFRIGERATOR =' not in text:
            text = text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1)
            module_write_text(path, text)
    for locale, name in (('es_es', 'Refrigeradora del comedor'), ('en_us', 'Dining Room Refrigerator')):
        path = A/f'lang/{locale}.json'
        data = json.loads(module_read_text(path))
        data['block.lsmmod.'+ID] = name
        write(path, data)
    path = D/'minecraft/tags/block/mineable/pickaxe.json'
    data = json.loads(module_read_text(path))
    if 'lsmmod:'+ID not in data['values']:
        data['values'].append('lsmmod:'+ID)
        write(path, data)


if __name__ == '__main__':
    main()
