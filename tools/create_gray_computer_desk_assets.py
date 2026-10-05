"""Generate the gray 2x1x2 desk, using the interactive classroom chair's exact gray."""
import copy
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
A = ROOT / 'src/main/resources/assets/lsmmod'
def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')

def box(start, end, texture='gray', uv=None):
    return {'from':start, 'to':end, 'faces':{face:{'texture':'#'+texture, 'uv':uv or [0,0,16,16]}
            for face in ['north','south','east','west','up','down']}}

# Gray panels, recessed empty keyboard tray and a lateral open CPU cradle.
furniture = [box([0,16,0],[32,18,16]), box([1,0,1],[3,16,15]),
             box([29,0,1],[31,16,15]), box([3,2,1],[29,6,2]),
             box([3,13,4],[4,16,14]),
             box([22,13,4],[23,16,14]), box([24,2,3],[29,3,14]),
             box([24,3,3],[25,16,4]), box([24,3,13],[25,16,14])]
retracted_tray = box([3,12,4],[23,13,15])
extended_tray = box([3,12,5],[23,13,16])
equipment = [box([5,13,9],[21,13.7,15],'equipment',[0,8,8,16]),
             box([25,3,4],[28.5,11,12],'equipment',[8,0,16,8]),
             box([25,3,12],[28.5,11,12.08],'equipment',[8,8,16,16]),
             box([22,18,10],[24,18.3,13],'equipment',[8,0,16,8]),
             box([22.2,18.3,10.2],[23.8,18.8,12.8],'equipment',[8,0,16,8]),
             box([22.85,18.8,10.7],[23.15,18.9,11.3],'equipment',[0,8,8,16])]
pc = json.loads((A / 'models/block/pc.json').read_text(encoding='utf-8'))
for element in pc['elements']:
    part = copy.deepcopy(element)
    part['from'] = [6+element['from'][0]*.85, 18+element['from'][1]*.85, element['from'][2]*.85]
    part['to'] = [6+element['to'][0]*.85, 18+element['to'][1]*.85, element['to'][2]*.85]
    for face in part['faces'].values(): face['texture'] = '#pc'
    equipment.append(part)
textures = {'gray':'lsmmod:block/chair_light_gray', 'equipment':'lsmmod:block/computer_desk',
            'pc':'lsmmod:block/pc', 'particle':'lsmmod:block/chair_light_gray'}

def split(elements, column, half):
    out = []
    for element in elements:
        lower = [column*16, half*16, 0]
        upper = [column*16+16, half*16+16, 16]
        if any(element['from'][axis] >= upper[axis] or element['to'][axis] <= lower[axis] for axis in range(3)): continue
        part = copy.deepcopy(element)
        for axis in range(3):
            part['from'][axis] = max(lower[axis], part['from'][axis])-lower[axis]
            part['to'][axis] = min(upper[axis], part['to'][axis])-lower[axis]
        # Clip UVs with the geometry: otherwise the right cell repeats the screen.
        axes = {'south':(0,False,1,True), 'north':(0,True,1,True),
                'east':(2,True,1,True), 'west':(2,False,1,True),
                'up':(0,False,2,False), 'down':(0,False,2,True)}
        for face, definition in list(part['faces'].items()):
            normal = {'west':(0,False),'east':(0,True),'down':(1,False),
                      'up':(1,True),'north':(2,False),'south':(2,True)}[face]
            axis, positive = normal
            if (positive and element['to'][axis] > upper[axis]) or (not positive and element['from'][axis] < lower[axis]):
                del part['faces'][face]
                continue
            old_uv = element['faces'][face]['uv']
            clipped_uv = []
            for uv_axis in range(2):
                axis, reverse = axes[face][uv_axis*2:uv_axis*2+2]
                size = element['to'][axis]-element['from'][axis]
                start = (max(lower[axis],element['from'][axis])-element['from'][axis])/size
                end = (min(upper[axis],element['to'][axis])-element['from'][axis])/size
                if reverse: start, end = 1-end, 1-start
                origin, extent = old_uv[uv_axis], old_uv[uv_axis+2]-old_uv[uv_axis]
                clipped_uv.append((origin+extent*start,origin+extent*end))
            definition['uv'] = [clipped_uv[0][0],clipped_uv[1][0],clipped_uv[0][1],clipped_uv[1][1]]
        out.append(part)
    return out

variants = {}
java = '''package net.nicomar2009.lsmmod.block;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
/** Generated from the gray desk model geometry. */
final class GrayComputerDeskShapes {
    static final VoxelShape[][][][] SHAPES = new VoxelShape[2][2][2][4];
    static {
'''
for column in range(2):
    for half, label in [(0,'lower'),(1,'upper')]:
        for occupied in [False, True]:
            name = f'gray_computer_desk_{column}_{label}' + ('_pc' if occupied else '')
            elements = split(furniture + [extended_tray if occupied else retracted_tray] + (equipment if occupied else []), column, half)
            write(A / f'models/block/{name}.json', {'parent':'minecraft:block/block', 'textures':textures,'elements':elements})
            for facing, angle in [('south',0),('west',90),('north',180),('east',270)]:
                variants[f'column={column},facing={facing},half={label},has_pc={str(occupied).lower()}'] = {'model':f'lsmmod:block/{name}', 'y':angle}
            boxes = ['Block.box('+', '.join(str(v) for v in element['from']+element['to'])+')' for element in elements]
            java += f'        SHAPES[{column}][{half}][{int(occupied)}][0] = Shapes.or(\n                '+',\n                '.join(boxes)+');\n'
java += '''        for (int c=0; c<2; c++) for (int h=0; h<2; h++) for (int p=0; p<2; p++) {
            for (int r=1; r<4; r++) {
                VoxelShape[] result = {Shapes.empty()};
                SHAPES[c][h][p][r-1].forAllBoxes((x1,y1,z1,x2,y2,z2) ->
                        result[0] = Shapes.or(result[0], Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                SHAPES[c][h][p][r] = result[0];
            }
        }
    }
    private GrayComputerDeskShapes() {}
}
'''
(ROOT / 'src/main/java/net/nicomar2009/lsmmod/block/GrayComputerDeskShapes.java').write_text(java, encoding='utf-8')
write(A / 'blockstates/gray_computer_desk.json', {'variants':variants})
# Keep the inventory model within vanilla element bounds, centered about x=8.
item = copy.deepcopy(furniture + [retracted_tray])
for element in item:
    for corner in ['from','to']:
        element[corner][0] = element[corner][0]*.5
        element[corner][1] *= .5
        element[corner][2] = 4+element[corner][2]*.5
write(A / 'models/item/gray_computer_desk.json', {'parent':'minecraft:block/block','textures':textures,'elements':item,
    'display':{'gui':{'rotation':[25,135,0],'scale':[.85,.85,.85]},'ground':{'scale':[.4,.4,.4]}}})
write(A / 'items/gray_computer_desk.json', {'model':{'type':'minecraft:model','model':'lsmmod:item/gray_computer_desk'}})
root = {'condition':'minecraft:block_state_property','block':'lsmmod:gray_computer_desk', 'properties':{'column':'0','half':'lower'}}
occupied = {'condition':'minecraft:block_state_property','block':'lsmmod:gray_computer_desk','properties':{'has_pc':'true'}}
write(ROOT / 'src/main/resources/data/lsmmod/loot_table/blocks/gray_computer_desk.json', {'type':'minecraft:block','pools':[
    {'rolls':1,'conditions':[root,{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':'lsmmod:gray_computer_desk'}]},
    {'rolls':1,'conditions':[root,occupied],'entries':[{'type':'minecraft:item','name':'lsmmod:pc'}]}]})
for language, label in [('es_es','Escritorio gris de computación'),('en_us','Gray computer desk')]:
    path = A / f'lang/{language}.json'
    data = json.loads(path.read_text(encoding='utf-8'))
    data['block.lsmmod.gray_computer_desk'] = label
    write(path, data)
