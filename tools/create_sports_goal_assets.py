"""Generate a 8x6x3 school football goal with basketball board, hoop and real cutout nets."""
from resource_modules import module_exists, module_path, module_read_text, module_write_text, output_path
from resource_json import resource_json
import json
from pathlib import Path

from PIL import Image, ImageDraw

from create_bathroom_statue_assets import solid_model

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/balls/assets/lsmmod"
DATA = ROOT / "src/main/resources/balls/data"
ID = "football_basketball_goal"
WIDTH, HEIGHT, DEPTH = 9, 6, 3
SHIFT_X = 8
# Five-sided football net profile traced from the enlarged Paint sketch:
# short roof, inclined upper back, vertical lower back, base and front mouth.
GOAL = [
    ([0,0,14,2,48,16], "frame"), ([126,0,14,128,48,16], "frame"),
    ([0,46,14,128,48,16], "frame"),
    ([0,0,46,2,24,48], "frame"), ([126,0,46,128,24,48], "frame"),
    ([0,46,30,128,48,32], "frame"),
    ([0,46,16,2,48,30], "frame"), ([126,46,16,128,48,30], "frame"),
    ([0,0,16,2,2,46], "frame"), ([126,0,16,128,2,46], "frame"),
    ([2,0,46,126,2,48], "frame"),
    ([2,2,46,126,24,46.125], "net"),
    ([2,45.875,16,126,46,30], "net"),
]


def brace(start, end, thickness=2):
    """Continuous subpixel stair tubes, with matching model and collision bounds."""
    count = int(max(abs(end[a]-start[a]) for a in range(3))*2)
    result = []
    for step in range(count):
        a = [start[i]+(end[i]-start[i])*step/count for i in range(3)]
        b = [start[i]+(end[i]-start[i])*(step+1)/count for i in range(3)]
        result.append(([min(a[i],b[i])-thickness/2 for i in range(3)]
                       + [max(a[i],b[i])+thickness/2 for i in range(3)], "frame"))
    return result


for x in (1,127):
    GOAL.extend(brace((x,23,47), (x,47,31)))
for y in range(24,46,2):
    z = 46-(y-24)*2/3
    GOAL.extend([([2,y,z,126,y+2,z+0.125], "net"),
                 ([2,y+1.875,z-1,126,y+2,z+0.125], "net")])
for y in range(2,46,2):
    back = 46 if y < 24 else 46-(y-24)*2/3
    GOAL.extend([([1.875,y,16,2,y+2,back], "net"),
                 ([126,y,16,126.125,y+2,back], "net")])
# The basketball support is separate from the football net. Each side has a
# vertical lower section, an inclined shoulder and a horizontal top rail.
TOWER = []
for x in (46,80):
    TOWER.extend([
        ([x,48,14,x+2,92,16], "frame"),
        ([x,48,30,x+2,78,32], "frame"),
        ([x,90,22,x+2,92,24], "frame"),
        ([x,90,16,x+2,92,22], "frame"),
        ([x,48,16,x+2,50,30], "frame"),
        ([x,76,16,x+2,78,30], "frame"),
    ])
    TOWER.extend(brace((x+1,77,31), (x+1,91,23)))
TOWER.extend([
    ([48,76,30,80,78,32], "frame"),
    ([48,90,22,80,92,24], "frame"),
    ([48,90,14,80,92,16], "frame"),
])
PARTS = GOAL + TOWER + [
    ([32,64,12,96,96,14], "board", {"north": "board_front"}),
    ([59,72,0,69,73.5,1.5], "rim"), ([59,72,10.5,69,73.5,12], "rim"),
    ([56,72,3,57.5,73.5,9], "rim"), ([70.5,72,3,72,73.5,9], "rim"),
    ([57.5,72,1.5,59,73.5,3], "rim"), ([69,72,1.5,70.5,73.5,3], "rim"),
    ([57.5,72,9,59,73.5,10.5], "rim"), ([69,72,9,70.5,73.5,10.5], "rim"),
    ([62,72,12,66,73.5,14], "rim"),
]
# A tapered hanging basket, open below; its own diamond-weave texture follows the height.
for y in range(64, 72):
    inset = (71-y)*0.375
    left, right = 57.5+inset, 70.5-inset
    front, back = 1.5+inset*0.75, 10.5-inset*0.75
    PARTS.extend([([left,y,front,right,y+1,front+0.125], "basket_net"),
                  ([left,y,back-0.125,right,y+1,back], "basket_net"),
                  ([left,y,front+0.125,left+0.125,y+1,back-0.125], "basket_net"),
                  ([right-0.125,y,front+0.125,right,y+1,back-0.125], "basket_net")])



for part in PARTS:
    part[0][0] += SHIFT_X
    part[0][3] += SHIFT_X


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    module_write_text(path, resource_json(path, value, ensure_ascii=False)+"\n", encoding="utf-8")


def index(column, row, depth):
    return column+8*row+48*depth if column<8 else 144+row+6*depth


def coordinates(cell):
    return (cell%8,cell//8%6,cell//48) if cell<144 else (8,(cell-144)%6,(cell-144)//6)


# Logical assembly cells keep their saved coordinates even when a thinner frame
# leaves a cell empty. Adjacent native overhangs place geometry at its true height.
OCCUPIED = [index(c,r,d) for d in range(DEPTH) for r in range(HEIGHT) for c in range(WIDTH)
            if (r < 4 and (d == 2 or r == 3 or c in (0,8)))
            or (d == 0 and r in (4,5) and c in (3,4,5))]
OWNERS = {}
for d in range(DEPTH):
    for r in range(HEIGHT):
        for c in range(WIDTH):
            cell = index(c,r,d)
            def distance(other):
                oc,orr,od = coordinates(other)
                return (abs(oc-c)+abs(orr-r)+abs(od-d),abs(oc-c),abs(od-d),abs(orr-r))
            OWNERS[cell] = cell if cell in OCCUPIED else min(OCCUPIED,key=distance)


def clips(bounds, column, row, depth):
    origin = [16*column,16*row,16*depth]
    low = [max(bounds[a],origin[a]) for a in range(3)]
    high = [min(bounds[a+3],origin[a]+16) for a in range(3)]
    return (low,high,origin) if all(a < b for a,b in zip(low,high)) else None


def uv_for(direction, low, high, origin, material):
    uv_origin = [int(value//16)*16 for value in low]
    local_low = [low[a]-uv_origin[a] for a in range(3)]
    local_high = [high[a]-uv_origin[a] for a in range(3)]
    if material == "board_front":
        return [(high[0]-32-SHIFT_X)/4, (96-high[1])/2, (low[0]-32-SHIFT_X)/4, (96-low[1])/2]
    if material == "basket_net":
        if direction in ("north", "south"):
            return [(high[0]-57.5-SHIFT_X)*16/13, (72-high[1])*2, (low[0]-57.5-SHIFT_X)*16/13, (72-low[1])*2]
        if direction in ("east", "west"):
            return [(high[2]-1.5)*16/9, (72-high[1])*2, (low[2]-1.5)*16/9, (72-low[1])*2]
    if direction == "north": return [local_high[0], 16-local_high[1], local_low[0], 16-local_low[1]]
    if direction == "south": return [local_low[0], 16-local_high[1], local_high[0], 16-local_low[1]]
    if direction == "east": return [local_high[2], 16-local_high[1], local_low[2], 16-local_low[1]]
    if direction == "west": return [local_low[2], 16-local_high[1], local_high[2], 16-local_low[1]]
    return [local_low[0], local_low[2], local_high[0], local_high[2]]


def textures():
    p = ASSETS / "textures/block"; p.mkdir(parents=True, exist_ok=True)
    for name, color in (("frame", (242, 242, 235)), ("board", (248, 248, 242)), ("rim", (178, 57, 27))):
        Image.new("RGB", (16, 16), color).save(output_path(p/f"sports_goal_{name}.png"))
    board = Image.new("RGB", (64, 32), (248, 248, 242)); draw = ImageDraw.Draw(board)
    draw.rectangle((0, 0, 63, 31), outline=(35, 37, 35), width=1)
    draw.rectangle((22, 12, 41, 24), outline=(35, 37, 35), width=1)
    board.save(output_path(p/"sports_goal_board_front.png"))
    # Half-pixel rope on an 8px mesh, lighter and thinner than the metal tubing.
    net = Image.new("RGBA", (32, 32)); draw = ImageDraw.Draw(net)
    for i in (0, 16):
        draw.line((i, 0, i, 31), fill=(239, 240, 231, 255))
        draw.line((0, i, 31, i), fill=(239, 240, 231, 255))
    net.save(output_path(p/"sports_goal_net.png"))
    basket = Image.new("RGBA", (16,16)); draw = ImageDraw.Draw(basket)
    for offset in range(-24,32,8):
        draw.line((offset,0,offset+16,16), fill=(246,244,229,255))
        draw.line((offset,0,offset-16,16), fill=(214,215,203,255))
    for y in (0,15): draw.line((0,y,15,y), fill=(246,244,229,255))
    basket.save(output_path(p/"sports_goal_basket_net.png"))
    # Restrained paint wear, leaving large clean regions as in vanilla textures.
    for name,base in (("frame",(242,242,235)), ("rim",(178,57,27))):
        image=Image.open(module_path(p/f"sports_goal_{name}.png")); draw=ImageDraw.Draw(image)
        for x,y in ((2,3),(10,7),(6,12)):
            draw.line((x,y,x+1,y), fill=tuple(v-9 for v in base))
        image.save(output_path(p/f"sports_goal_{name}.png"))



def write_shapes(all_cells, occupied):
    # Separate per-cell factories keep the detailed trusses out of one large
    # class initializer while preserving the shared CELLS/OCCUPIED contract.
    java = ["package net.nicomar2009.lsmmod.modules.balls.block;", "",
            "/** Generated by tools/create_sports_goal_assets.py. */",
            "public final class SportsGoalShapes {", "    private SportsGoalShapes() {}",
            "    public static final int[] OCCUPIED = {"+", ".join(map(str,occupied))+"};",
            "    public static final double[][][] CELLS = {"]
    java += ["        cell"+str(cell)+"()," for cell in range(WIDTH*HEIGHT*DEPTH)]
    java.append("    };")
    # Material-specific physics uses the exact same clipped/owned geometry,
    # including net fragments that overhang into an adjacent saved cell.
    materials = {name: {cell: [] for cell in range(WIDTH*HEIGHT*DEPTH)} for name in ('net', 'rigid')}
    for d in range(DEPTH):
        for r in range(HEIGHT):
            for c in range(WIDTH):
                owner = OWNERS[index(c,r,d)]
                oc,orr,od = coordinates(owner)
                origin = [16*oc,16*orr,16*od]
                for bounds,material,*_ in PARTS:
                    clipped = clips(bounds,c,r,d)
                    if clipped:
                        low,high,_ = clipped
                        box = [low[a]-origin[a] for a in range(3)]+[high[a]-origin[a] for a in range(3)]
                        materials['net' if material == 'net' else 'rigid'][owner].append(box)
    for name in materials:
        java.append('    public static final double[][][] '+name.upper()+'_CELLS = {')
        java += ['        '+name+str(cell)+'(),' for cell in range(WIDTH*HEIGHT*DEPTH)]
        java.append('    };')
    for cell in range(WIDTH*HEIGHT*DEPTH):
        boxes = ", ".join("{"+", ".join(map(str,b))+"}" for b in all_cells[cell]['collision'])
        java += ["    private static double[][] cell"+str(cell)+"() {",
                 "        return new double[][] {"+boxes+"};", "    }"]
    for name,cells in materials.items():
        for cell,values in cells.items():
            boxes = ', '.join('{'+', '.join(map(str,b))+'}' for b in values)
            java += ['    private static double[][] '+name+str(cell)+'() {',
                     '        return new double[][] {'+boxes+'};', '    }']
    java.append("}")
    module_write_text(ROOT/'src/main/java/net/nicomar2009/lsmmod/modules/balls/block/SportsGoalShapes.java', "\n".join(java)+"\n")


def main():
    textures()
    exterior = solid_model(PARTS)
    # Tile overhanging faces without stretching their UVs or adding internal caps.
    tiled = exterior['elements']
    cuts_by_axis = [('west','east'),('down','up'),('north','south')]
    for axis,(negative,positive) in enumerate(cuts_by_axis):
        fragments = []
        for element in tiled:
            low,high = element['from'][axis],element['to'][axis]
            cuts = [low]+[v for v in range(16,144,16) if low < v < high]+[high]
            for start,end in zip(cuts,cuts[1:]):
                e = json.loads(json.dumps(element))
                e['from'][axis],e['to'][axis] = start,end
                if start != low: e['faces'].pop(negative,None)
                if end != high: e['faces'].pop(positive,None)
                if e['faces']: fragments.append(e)
        tiled = fragments
    exterior['elements'] = tiled
    mappings = {m: f"lsmmod:block/sports_goal_{m}" for m in ("frame", "board", "board_front", "rim", "net", "basket_net")}
    mappings["particle"] = mappings["frame"]
    occupied = OCCUPIED
    all_cells = {cell: {'collision': [], 'elements': []} for cell in range(WIDTH*HEIGHT*DEPTH)}
    normals = {"west": (0, False), "east": (0, True), "down": (1, False), "up": (1, True), "north": (2, False), "south": (2, True)}
    for depth in range(DEPTH):
        for row in range(HEIGHT):
            for column in range(WIDTH):
                owner = OWNERS[index(column,row,depth)]
                oc,orr,od = coordinates(owner)
                origin = [16*oc,16*orr,16*od]
                for part in PARTS:
                    clipped = clips(part[0],column,row,depth)
                    if clipped:
                        low,high,_ = clipped
                        all_cells[owner]['collision'].append([low[a]-origin[a] for a in range(3)]+[high[a]-origin[a] for a in range(3)])
                for source in exterior['elements']:
                    bounds = source['from']+source['to']
                    clipped = clips(bounds,column,row,depth)
                    if not clipped: continue
                    low,high,_ = clipped
                    faces = {}
                    for face,data in source['faces'].items():
                        axis,positive = normals[face]
                        if (high[axis] if positive else low[axis]) != (bounds[axis+3] if positive else bounds[axis]): continue
                        material = data['texture'][1:]
                        faces[face] = {'texture': '#'+material, 'uv': uv_for(face,low,high,origin,material)}
                    if faces:
                        all_cells[owner]['elements'].append({'from': [low[a]-origin[a] for a in range(3)],
                                                            'to': [high[a]-origin[a] for a in range(3)], 'faces': faces})
    for cell in occupied:
        write_json(ASSETS / f"models/block/{ID}_{cell}.json", {'parent': 'minecraft:block/block',
            'render_type': 'minecraft:cutout', 'textures': mappings, 'elements': all_cells[cell]['elements']})
    write_json(ASSETS / f"models/block/{ID}_empty.json", {"textures": mappings, "elements": []})
    variants = {}
    for cell in range(WIDTH*HEIGHT*DEPTH):
        name = f"{ID}_{cell}" if cell in occupied else f"{ID}_empty"
        for facing, angle in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
            variants[f"cell={cell},facing={facing}"] = {"model": f"lsmmod:block/{name}", "y": angle}
    write_json(ASSETS / f"blockstates/{ID}.json", {"variants": variants})
    item_elements = []
    for cell, value in all_cells.items():
        column, row, depth = coordinates(cell)
        for element in value["elements"]:
            e = json.loads(json.dumps(element))
            for side in ("from", "to"):
                e[side] = [(e[side][0]+column*16-SHIFT_X)/8, 1+(e[side][1]+row*16)/8, 4+(e[side][2]+depth*16)/8]
            item_elements.append(e)
    write_json(ASSETS / f"models/item/{ID}.json", {"parent": "minecraft:block/block", "render_type": "minecraft:cutout", "textures": mappings, "elements": item_elements})
    write_json(ASSETS / f"items/{ID}.json", {"model": {"type": "minecraft:model", "model": f"lsmmod:item/{ID}"}})
    write_json(DATA / f"lsmmod/loot_table/blocks/{ID}.json", {"type": "minecraft:block", "pools": [{"rolls": 1,
        "conditions": [{"condition": "minecraft:survives_explosion"}, {"condition": "minecraft:block_state_property", "block": f"lsmmod:{ID}", "properties": {"cell": "0"}}],
        "entries": [{"type": "minecraft:item", "name": f"lsmmod:{ID}"}]}]})
    write_shapes(all_cells, occupied)
    # Collision manifest permits static model/collision tests and external previews.
    write_json(ROOT / "tools/sports_goal_geometry.json", {"occupied": occupied, "cells": all_cells})
    for locale, name in (("es_es", "Arco de fútbol con canasta de básquet"), ("en_us", "Football Goal with Basketball Hoop")):
        p = ASSETS / f"lang/{locale}.json"; value = json.loads(module_read_text(p)) if module_exists(p) else {'replace': False, 'values': []}; value[f"block.lsmmod.{ID}"] = name; write_json(p, value)
    p = DATA / "minecraft/tags/block/mineable/pickaxe.json"; value = json.loads(module_read_text(p)) if module_exists(p) else {'replace': False, 'values': []}
    if f"lsmmod:{ID}" not in value["values"]: value["values"].append(f"lsmmod:{ID}")
    write_json(p, value)


if __name__ == "__main__": main()
