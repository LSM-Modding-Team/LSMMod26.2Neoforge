"""Independent 24px projecting panes: two-pixel thickness, five poses, nine lattice phases."""
import copy
import json
from PIL import Image
from create_bathroom_statue_assets import solid_model, crop_face_uv
from create_school_refrigerator_assets import A, J, D, ROOT, write
from create_facility_expansion_assets import basic

ID = 'school_projecting_window'
TEXTURES = {'frame': f'lsmmod:block/{ID}_frame', 'glass': f'lsmmod:block/{ID}_glass', 'particle': '#frame'}
AXES = (((0, 16, 0),), ((0, 8, -16), (8, 16, 8)), ((0, 16, -8),))
PANES = {'solid': [([0, 0, 7, 1, 24, 9], 'frame'), ([23, 0, 7, 24, 24, 9], 'frame'),
                   ([1, 0, 7, 23, 1, 9], 'frame'), ([1, 23, 7, 23, 24, 9], 'frame')],
         'translucent': [([1, 1, 7, 23, 23, 9], 'glass')]}


def parts(phase):
    return [(x0, x1, y0, y1, ox, oy) for y0, y1, oy in AXES[phase//3] for x0, x1, ox in AXES[phase%3]]


def rotation(part, pose):
    *_, ox, oy = part
    return {'origin': [ox+(24 if pose == 4 else 0), oy+(24 if pose == 1 else 0), 7],
            'axis': 'x' if pose <= 2 else 'y', 'angle': 45 if pose in (1, 3) else -45, 'rescale': False}


def pane_models():
    result = {}
    for layer, components in PANES.items():
        model = solid_model(components)
        model.update(textures=TEXTURES, render_type='minecraft:'+layer)
        result[layer] = model
    return result


def fragment(model, part, pose):
    x0, x1, y0, y1, ox, oy = part
    result = copy.deepcopy(model); result['elements'] = []
    axes = {'west': (0, False), 'east': (0, True), 'down': (1, False),
            'up': (1, True), 'north': (2, False), 'south': (2, True)}
    for original in model['elements']:
        e = copy.deepcopy(original)
        for key in ('from', 'to'):
            e[key][0] += ox; e[key][1] += oy
        a, b = e['from'], e['to']
        lo = [max(a[0], x0), max(a[1], y0), a[2]]
        hi = [min(b[0], x1), min(b[1], y1), b[2]]
        if any(lo[i] >= hi[i] for i in range(3)): continue
        surfaces = {}
        for face, surface in e['faces'].items():
            axis, positive = axes[face]
            if (hi if positive else lo)[axis] != (b if positive else a)[axis]: continue
            cropped = copy.deepcopy(surface)
            cropped['uv'] = crop_face_uv(surface['uv'], face, a, b, lo, hi)
            surfaces[face] = cropped
        if not surfaces: continue
        e.update({'from': lo, 'to': hi, 'faces': surfaces})
        if pose: e['rotation'] = rotation(part, pose)
        result['elements'].append(e)
    result.update(loader='fusion:model', type='base')
    return result


def main():
    Image.new('RGB', (16, 16), (17, 18, 19)).save(A/f'textures/block/{ID}_frame.png')
    # Vanilla 26.2 tinted-glass extraction, retained as the reproducible color reference.
    # Source: github.com/InventivetalentDev/minecraft-assets/tree/26.2/assets/minecraft/textures/block
    glass = Image.open(ROOT/'newresources/projecting_window_tinted_reference.png').convert('RGBA')
    glass.putdata([tuple(max(0, channel-4) for channel in glass.getpixel((x, y))[:3])+(glass.getpixel((x, y))[3],)
                   for y in range(glass.height) for x in range(glass.width)])
    glass.save(A/f'textures/block/{ID}_glass.png')
    base = pane_models(); roots = []
    for phase in range(9):
        groups = []
        for index, part in enumerate(parts(phase)):
            for layer, model in base.items():
                choices = []
                for pose in range(5):
                    value = fragment(model, part, pose)
                    # Some interior cell fragments contain glass only. Omit absent frame layers.
                    if not value['elements']: continue
                    name = f'{ID}/phase_{phase}_part_{index}_{layer}_{pose}'
                    write(A/f'models/block/{name}.json', value)
                    choices.append({'model': 'lsmmod:block/'+name,
                                    'condition': {'type': 'lsmmod:projecting_window', 'phase': phase,
                                                  'part': index, 'pose': pose}})
                if choices: groups.append(choices)
        name = f'{ID}/phase_{phase}'
        write(A/f'models/block/{name}.json', {'loader': 'fusion:model', 'type': 'composite', 'textures': TEXTURES, 'models': groups})
        roots.append({'model': 'lsmmod:block/'+name,
                      'condition': {'type': 'lsmmod:projecting_window', 'phase': phase}})
    write(A/f'models/block/{ID}.json', {'loader': 'fusion:model', 'type': 'composite', 'textures': TEXTURES, 'models': [roots]})
    write(A/f'blockstates/{ID}.json', {'variants': {f'facing={facing}': {
          'model': 'lsmmod:block/'+ID, 'y': turn*90} for turn, facing in enumerate(('north', 'east', 'south', 'west'))}})
    for layer, value in base.items():
        item = copy.deepcopy(value); item.update(loader='fusion:model', type='base')
        for e in item['elements']:
            for key in ('from', 'to'):
                x, y, z = e[key]; e[key] = [x*2/3, y*2/3, 8+(z-8)*2/3]
        write(A/f'models/item/{ID}_{layer}.json', item)
    write(A/f'models/item/{ID}.json', {'loader': 'fusion:model', 'type': 'composite', 'textures': TEXTURES,
          'models': [[f'lsmmod:item/{ID}_solid', f'lsmmod:item/{ID}_translucent']]})
    basic(ID)
    for filename, entry in (
        ('ModBlocks.java', '    public static final DeferredBlock<SchoolProjectingWindowBlock> SCHOOL_PROJECTING_WINDOW = BLOCKS.registerBlock("school_projecting_window", SchoolProjectingWindowBlock::new, props -> props.strength(0.5F).sound(SoundType.GLASS).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> SCHOOL_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_PROJECTING_WINDOW);'),
    ):
        path = J/'registry'/filename; text = path.read_text()
        if ' SCHOOL_PROJECTING_WINDOW =' not in text:
            path.write_text(text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1))
    for locale, name in (('es_es', 'Ventana proyectante del colegio'), ('en_us', 'School Projecting Window')):
        path = A/f'lang/{locale}.json'; data = json.loads(path.read_text()); data['block.lsmmod.'+ID] = name; write(path, data)
    path = D/'minecraft/tags/block/mineable/pickaxe.json'; data = json.loads(path.read_text())
    if 'lsmmod:'+ID not in data['values']: data['values'].append('lsmmod:'+ID)
    write(path, data)


if __name__ == '__main__': main()
