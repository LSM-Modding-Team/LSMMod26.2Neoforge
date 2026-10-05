"""Build the two-block school computer workstation and matching collision shapes."""
import copy
import json
import math
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
A = ROOT / 'src/main/resources/assets/lsmmod'
def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')

im = Image.new('RGB', (64, 64), '#bc8858')
d = ImageDraw.Draw(im)
for y in range(32):
    for x in range(32):
        shade = (x * 3 + y // 3) % 13
        im.putpixel((x, y), (183 + shade, 127 + shade, 77 + shade))
for y in [5, 16, 27]:
    d.line((0, y, 31, y), fill='#a86f40')
d.rectangle((32, 0, 63, 31), fill='#292d30')
for x in [34, 61]:
    d.line((x, 1, x, 30), fill='#42494d')
d.rectangle((0, 32, 31, 63), fill='#282c30')
for row in range(5):
    for col in range(10):
        x, y = 2 + col * 3, 35 + row * 4
        d.rectangle((x, y, x+1, y+2), fill='#92989e')
d.rectangle((8, 56, 23, 59), fill='#9ca2a7')
d.rectangle((32, 32, 63, 63), fill='#272c30')
d.rectangle((35, 36, 59, 40), fill='#14171a', outline='#60666b')
d.rectangle((35, 44, 42, 47), fill='#101315')
d.rectangle((54, 44, 57, 47), fill='#76b887')
for x in range(36, 61, 3):
    d.line((x, 52, x, 59), fill='#121619')
im.save(A / 'textures/block/computer_desk.png')

def box(start, end, texture='wood', overrides=None):
    uv = {'wood': [0,0,8,8], 'paint': [8,0,16,8], 'keys': [0,8,8,16], 'cpu': [8,8,16,16]}[texture]
    faces = {f: {'texture': '#desk', 'uv': uv} for f in ['north','south','east','west','up','down']}
    for face, coords in (overrides or {}).items(): faces[face]['uv'] = coords
    return {'from': start, 'to': end, 'faces': faces}

# Wooden side panels support the desktop; their front edges are painted black.
# The narrow rear header is one pixel high and two deep, rather than a roof.
furniture = [box([1,13,0],[15,28,1]), box([0,28,0],[16,29,2]),
             box([1,16,1],[15,17,12]),
             box([1,5,1],[15,8,2]),
             box([1,12.4,2],[1.2,13.4,12],'paint'),
             box([14.8,12.4,2],[15,13.4,12],'paint')]
# Approximate a quarter-circle at the upper front of each wooden side wall.
# Thin adjacent strips stay compatible with vanilla cuboid models and collisions.
for x in [0, 15]:
    furniture.append(box([x,0,1],[x+1,24,12], overrides={'south':[8,0,16,8]}))
    furniture.append(box([x,24,1],[x+1,28,8], overrides={'up':[8,0,16,8]}))
    for step in range(16):
        start = 8 + step*.25
        end = start + .25
        height = 24 + math.sqrt(max(0, 16-(end-8)**2))
        if height > 24:
            furniture.append(box([x,24,start],[x+1,height,end],
                                 overrides={'up':[8,0,16,8], 'south':[8,0,16,8]}))
retracted_tray = box([1.25,12.6,2],[14.75,13.2,9])
extended_tray = box([1.25,12.6,7],[14.75,13.2,15.5])
equipment = [box([2,13.2,10],[14,13.85,14.5],'keys'),
             box([10.5,17,3],[14.5,19.4,8],'paint'),
             box([10.5,17,8],[14.5,19.4,8.08],'cpu'),
             # Small mouse on the desktop, with a raised body and scroll wheel.
             box([6.8,17,9.1],[8.4,17.25,11.3],'paint'),
             box([7,17.25,9.3],[8.2,17.65,11.1],'paint'),
             box([7.48,17.65,9.65],[7.72,17.75,10.05],'keys')]
# Reuse the existing PC monitor at a reduced size, preserving its texture UVs.
pc = json.loads((A / 'models/block/pc.json').read_text(encoding='utf-8'))
for element in pc['elements']:
    part = copy.deepcopy(element)
    part['from'] = [1 + element['from'][0]*.62, 17 + element['from'][1]*.62, 1 + element['from'][2]*.62]
    part['to'] = [1 + element['to'][0]*.62, 17 + element['to'][1]*.62, 1 + element['to'][2]*.62]
    for face in part['faces'].values(): face['texture'] = '#pc'
    equipment.append(part)

def hide_internal_faces(elements):
    """Remove faces completely buried in neighboring geometry, including joins."""
    elements = copy.deepcopy(elements)
    for element in elements:
        for face, axis, positive in [('west',0,False),('east',0,True),('down',1,False),
                                     ('up',1,True),('north',2,False),('south',2,True)]:
            plane = element['to' if positive else 'from'][axis]
            other_axes = [i for i in range(3) if i != axis]
            for other in elements:
                if other is element: continue
                covers = all(other['from'][i] <= element['from'][i] and other['to'][i] >= element['to'][i] for i in other_axes)
                buried = (other['from'][axis] <= plane < other['to'][axis]) if positive else (other['from'][axis] < plane <= other['to'][axis])
                if covers and buried:
                    element['faces'].pop(face, None)
                    break
    return elements

def split(elements, half):
    out = []
    lo, hi = half * 16, (half + 1) * 16
    for element in elements:
        if element['from'][1] >= hi or element['to'][1] <= lo: continue
        part = copy.deepcopy(element)
        part['from'][1] = max(lo, part['from'][1]) - lo
        part['to'][1] = min(hi, part['to'][1]) - lo
        if element['from'][1] < lo: part['faces'].pop('down', None)
        if element['to'][1] > hi: part['faces'].pop('up', None)
        out.append(part)
    return out

variants = {}
shapes = []
for half, label in [(0,'lower'), (1,'upper')]:
    shape_states = []
    for occupied in [False, True]:
        name = f'computer_desk_{label}' + ('_pc' if occupied else '')
        full_model = hide_internal_faces(furniture + [extended_tray if occupied else retracted_tray] + (equipment if occupied else []))
        elements = split(full_model, half)
        shape_states.append(elements)
        write(A / f'models/block/{name}.json', {
            'parent': 'minecraft:block/block',
            'textures': {'desk':'lsmmod:block/computer_desk', 'pc':'lsmmod:block/pc', 'particle':'lsmmod:block/computer_desk'},
            'elements': elements})
        for facing, angle in [('south',0),('west',90),('north',180),('east',270)]:
            variants[f'facing={facing},half={label},has_pc={str(occupied).lower()}'] = {'model':f'lsmmod:block/{name}', 'y':angle}
    shapes.append(shape_states)
write(A / 'blockstates/computer_desk.json', {'variants':variants})
write(A / 'models/item/computer_desk.json', {
    'parent':'minecraft:block/block',
    'textures':{'desk':'lsmmod:block/computer_desk', 'particle':'lsmmod:block/computer_desk'},
    'elements':hide_internal_faces(furniture + [retracted_tray]),
    'display':{'gui':{'rotation':[20,135,0], 'translation':[0,-5,0], 'scale':[.45,.45,.45]},
               'ground':{'translation':[0,1,0], 'scale':[.25,.25,.25]},
               'fixed':{'translation':[0,-5,0], 'scale':[.4,.4,.4]}}})
write(A / 'items/computer_desk.json', {'model':{'type':'minecraft:model','model':'lsmmod:item/computer_desk'}})

lower = {'condition':'minecraft:block_state_property','block':'lsmmod:computer_desk','properties':{'half':'lower'}}
occupied = {'condition':'minecraft:block_state_property','block':'lsmmod:computer_desk','properties':{'has_pc':'true'}}
write(ROOT / 'src/main/resources/data/lsmmod/loot_table/blocks/computer_desk.json', {
    'type':'minecraft:block', 'pools':[
        {'rolls':1, 'conditions':[lower, {'condition':'minecraft:survives_explosion'}],
         'entries':[{'type':'minecraft:item','name':'lsmmod:computer_desk'}]},
        {'rolls':1, 'conditions':[lower, occupied], 'entries':[{'type':'minecraft:item','name':'lsmmod:pc'}]}]})
for lang, label in [('es_es','Mesa de computación'),('en_us','Computer desk')]:
    path = A / f'lang/{lang}.json'
    data = json.loads(path.read_text(encoding='utf-8'))
    data['block.lsmmod.computer_desk'] = label
    write(path, data)

java = '''package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Generated from the same geometry as the desk models. */
final class ComputerDeskShapes {
    static final VoxelShape[][][] SHAPES = new VoxelShape[2][2][4];
    static {
'''
for half in range(2):
    for occupied in range(2):
        boxes = ['Block.box(' + ', '.join(str(v) for v in e['from'] + e['to']) + ')' for e in shapes[half][occupied]]
        java += f'        SHAPES[{half}][{occupied}][0] = Shapes.or(\n                ' + ',\n                '.join(boxes) + ');\n'
java += '''        for (int half = 0; half < 2; half++) {
            for (int occupied = 0; occupied < 2; occupied++) {
                for (int rotation = 1; rotation < 4; rotation++) {
                    VoxelShape[] result = {Shapes.empty()};
                    SHAPES[half][occupied][rotation - 1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                            result[0] = Shapes.or(result[0], Shapes.box(1-z2, y1, x1, 1-z1, y2, x2)));
                    SHAPES[half][occupied][rotation] = result[0];
                }
            }
        }
    }
    private ComputerDeskShapes() {}
}
'''
(ROOT / 'src/main/java/net/nicomar2009/lsmmod/block/ComputerDeskShapes.java').write_text(java, encoding='utf-8')
