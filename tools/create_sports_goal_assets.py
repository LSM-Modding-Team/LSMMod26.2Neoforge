"""Generate a 8x6x3 school football goal with basketball board, hoop and real cutout nets."""
from resource_json import resource_json
import json
from pathlib import Path

from PIL import Image, ImageDraw

from create_bathroom_statue_assets import solid_model

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
DATA = ROOT / "src/main/resources/data"
ID = "football_basketball_goal"
WIDTH, HEIGHT, DEPTH = 9, 6, 3
SHIFT_X = 8
# The Paint trace defines a raised rear rail and triangular side/board supports.
# The existing front mouth, cell IDs and +8px placement offset remain intact.
GOAL = [
    ([0, 0, 14, 2, 64, 16], "frame"), ([126, 0, 14, 128, 64, 16], "frame"),
    ([0, 62, 14, 128, 64, 16], "frame"),
    ([0, 0, 46, 2, 80, 48], "frame"), ([126, 0, 46, 128, 80, 48], "frame"),
    ([2, 78, 46, 126, 80, 48], "frame"),
    ([0, 0, 16, 2, 2, 46], "frame"), ([126, 0, 16, 128, 2, 46], "frame"),
    ([2, 0, 46, 126, 2, 48], "frame"),
    ([2, 2, 46, 126, 78, 46.125], "net"),
]


def brace(start, end, thickness=1.5):
    """Continuous subpixel stair tubes, clipped using the same geometry as collision."""
    count = int(max(abs(end[a]-start[a]) for a in range(3))*2)
    result = []
    for step in range(count):
        a = [start[i]+(end[i]-start[i])*step/count for i in range(3)]
        b = [start[i]+(end[i]-start[i])*(step+1)/count for i in range(3)]
        result.append(([min(a[i],b[i])-thickness/2 for i in range(3)]
                       + [max(a[i],b[i])+thickness/2 for i in range(3)], "frame"))
    return result


# Both side trusses rise backwards from the front posts to the elevated rear rail.
for x in (1, 127):
    GOAL.extend(brace((x,63,16), (x,79,46), 2))
    GOAL.extend(brace((x,42,16), (x,78,46), 1.5))
# Two inclined arms carry the basketball assembly forward from that rear rail.
for x in (47, 81):
    GOAL.extend(brace((x,79,46), (x,77,16), 1.5))
# Football mesh follows the inclined roof, with a full-height rear and side walls.
for z in range(16, 46):
    roof = 62+(z-16)*16/30
    next_roof = 62+(z+1-16)*16/30
    GOAL.append(([2,roof-0.125,z,126,next_roof,z+1], "net"))
    GOAL.extend([([1.875,2,z,2,roof,z+1], "net"),
                 ([126,2,z,126.125,roof,z+1], "net")])
PARTS = GOAL + [
    # Solid board mounting rails; no net in the basketball support tower.
    ([46, 64, 14, 48, 92, 16], "frame"), ([80, 64, 14, 82, 92, 16], "frame"),
    ([48, 90, 14, 80, 92, 16], "frame"),
    ([40, 72, 12, 88, 96, 14], "board", {"north": "board_front"}),
    ([59, 72, 0, 69, 73.5, 1.5], "rim"), ([59, 72, 10.5, 69, 73.5, 12], "rim"),
    ([56, 72, 3, 57.5, 73.5, 9], "rim"), ([70.5, 72, 3, 72, 73.5, 9], "rim"),
    ([57.5, 72, 1.5, 59, 73.5, 3], "rim"), ([69, 72, 1.5, 70.5, 73.5, 3], "rim"),
    ([57.5, 72, 9, 59, 73.5, 10.5], "rim"), ([69, 72, 9, 70.5, 73.5, 10.5], "rim"),
    ([62, 72, 12, 66, 73.5, 14], "rim"),
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
    path.write_text(resource_json(path, value, ensure_ascii=False)+"\n", encoding="utf-8")


def index(column, row, depth):
    return column+8*row+48*depth if column<8 else 144+row+6*depth


def coordinates(cell):
    return (cell%8,cell//8%6,cell//48) if cell<144 else (8,(cell-144)%6,(cell-144)//6)


def clips(bounds, column, row, depth, raised=False):
    # An <=1-block upward overhang stays owned by the existing top goal cell.
    # Native models permit coordinates up to 32px; saved goals keep all 72 cells.
    if raised and row >= 4:
        return None
    origin = [16*column, 16*row, 16*depth]
    low = [max(bounds[a], origin[a]) for a in range(3)]
    limits = [16,32 if raised and row == 3 else 16,16]
    high = [min(bounds[a+3], origin[a]+limits[a]) for a in range(3)]
    return (low, high, origin) if all(a < b for a, b in zip(low, high)) else None


def uv_for(direction, low, high, origin, material):
    uv_origin = list(origin)
    uv_origin[1] = int(low[1]//16)*16
    local_low = [low[a]-uv_origin[a] for a in range(3)]
    local_high = [high[a]-uv_origin[a] for a in range(3)]
    if material == "board_front":
        return [(high[0]-40-SHIFT_X)/3, (96-high[1])*2/3, (low[0]-40-SHIFT_X)/3, (96-low[1])*2/3]
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
        Image.new("RGB", (16, 16), color).save(p/f"sports_goal_{name}.png")
    board = Image.new("RGB", (48, 24), (248, 248, 242)); draw = ImageDraw.Draw(board)
    draw.rectangle((0, 0, 47, 23), outline=(35, 37, 35), width=1)
    draw.rectangle((15, 11, 32, 21), outline=(35, 37, 35), width=1)
    board.save(p/"sports_goal_board_front.png")
    # Half-pixel rope on an 8px mesh, lighter and thinner than the metal tubing.
    net = Image.new("RGBA", (32, 32)); draw = ImageDraw.Draw(net)
    for i in (0, 16):
        draw.line((i, 0, i, 31), fill=(239, 240, 231, 255))
        draw.line((0, i, 31, i), fill=(239, 240, 231, 255))
    net.save(p/"sports_goal_net.png")
    basket = Image.new("RGBA", (16,16)); draw = ImageDraw.Draw(basket)
    for offset in range(-24,32,8):
        draw.line((offset,0,offset+16,16), fill=(246,244,229,255))
        draw.line((offset,0,offset-16,16), fill=(214,215,203,255))
    for y in (0,15): draw.line((0,y,15,y), fill=(246,244,229,255))
    basket.save(p/"sports_goal_basket_net.png")
    # Restrained paint wear, leaving large clean regions as in vanilla textures.
    for name,base in (("frame",(242,242,235)), ("rim",(178,57,27))):
        image=Image.open(p/f"sports_goal_{name}.png"); draw=ImageDraw.Draw(image)
        for x,y in ((2,3),(10,7),(6,12)):
            draw.line((x,y,x+1,y), fill=tuple(v-9 for v in base))
        image.save(p/f"sports_goal_{name}.png")



def write_shapes(all_cells, occupied):
    # Separate per-cell factories keep the detailed trusses out of one large
    # class initializer while preserving the shared CELLS/OCCUPIED contract.
    java = ["package net.nicomar2009.lsmmod.block;", "",
            "/** Generated by tools/create_sports_goal_assets.py. */",
            "public final class SportsGoalShapes {", "    private SportsGoalShapes() {}",
            "    public static final int[] OCCUPIED = {"+", ".join(map(str,occupied))+"};",
            "    public static final double[][][] CELLS = {"]
    java += ["        cell"+str(cell)+"()," for cell in range(WIDTH*HEIGHT*DEPTH)]
    java.append("    };")
    for cell in range(WIDTH*HEIGHT*DEPTH):
        boxes = ", ".join("{"+", ".join(map(str,b))+"}" for b in all_cells[cell]['collision'])
        java += ["    private static double[][] cell"+str(cell)+"() {",
                 "        return new double[][] {"+boxes+"};", "    }"]
    java.append("}")
    (ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SportsGoalShapes.java').write_text("\n".join(java)+"\n")


def main():
    textures()
    exterior = solid_model(PARTS)
    tiled = []
    for element in exterior["elements"]:
        low,high = element["from"],element["to"]
        cuts = [low[1]]+[y for y in range(16,96,16) if low[1] < y < high[1]]+[high[1]]
        for y0,y1 in zip(cuts,cuts[1:]):
            e = json.loads(json.dumps(element))
            e["from"][1],e["to"][1] = y0,y1
            if y0 != low[1]: e["faces"].pop("down",None)
            if y1 != high[1]: e["faces"].pop("up",None)
            if e["faces"]: tiled.append(e)
    # Classify ownership once, rather than repeating containment for all 162 cells.
    exterior["elements"] = []
    for element in tiled:
        bounds = element['from']+element['to']
        material = next(iter(element['faces'].values()))['texture'][1:]
        raised = material in ('frame','net') and any(
            all(p[0][a] <= bounds[a] and bounds[a+3] <= p[0][a+3] for a in range(3))
            for p in GOAL)
        exterior['elements'].append((element,raised))
    mappings = {m: f"lsmmod:block/sports_goal_{m}" for m in ("frame", "board", "board_front", "rim", "net", "basket_net")}
    mappings["particle"] = mappings["frame"]
    all_cells, occupied = {}, []
    normals = {"west": (0, False), "east": (0, True), "down": (1, False), "up": (1, True), "north": (2, False), "south": (2, True)}
    for depth in range(DEPTH):
        for row in range(HEIGHT):
            for column in range(WIDTH):
                cell = index(column, row, depth)
                collision = []
                for part in PARTS:
                    clipped = clips(part[0], column, row, depth, raised=part in GOAL)
                    if clipped:
                        low, high, origin = clipped
                        collision.append([low[a]-origin[a] for a in range(3)]+[high[a]-origin[a] for a in range(3)])
                elements = []
                for source, raised in exterior["elements"]:
                    bounds = source["from"]+source["to"]
                    clipped = clips(bounds, column, row, depth, raised=raised)
                    if not clipped: continue
                    low, high, origin = clipped
                    faces = {}
                    for face, data in source["faces"].items():
                        axis, positive = normals[face]
                        if (high[axis] if positive else low[axis]) != (bounds[axis+3] if positive else bounds[axis]): continue
                        material = data["texture"][1:]
                        faces[face] = {"texture": "#"+material, "uv": uv_for(face, low, high, origin, material)}
                    if faces:
                        elements.append({"from": [low[a]-origin[a] for a in range(3)], "to": [high[a]-origin[a] for a in range(3)], "faces": faces})
                all_cells[cell] = {"collision": collision, "elements": elements}
                if collision:
                    occupied.append(cell)
                    write_json(ASSETS / f"models/block/{ID}_{cell}.json", {"parent": "minecraft:block/block", "render_type": "minecraft:cutout", "textures": mappings, "elements": elements})
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
        p = ASSETS / f"lang/{locale}.json"; value = json.loads(p.read_text()); value[f"block.lsmmod.{ID}"] = name; write_json(p, value)
    p = DATA / "minecraft/tags/block/mineable/pickaxe.json"; value = json.loads(p.read_text())
    if f"lsmmod:{ID}" not in value["values"]: value["values"].append(f"lsmmod:{ID}")
    write_json(p, value)


if __name__ == "__main__": main()
