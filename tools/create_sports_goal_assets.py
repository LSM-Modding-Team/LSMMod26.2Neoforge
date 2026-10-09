"""Generate a 8x6x3 school football goal with basketball board, hoop and real cutout nets."""
import json
from pathlib import Path

from PIL import Image, ImageDraw

from create_bathroom_statue_assets import solid_model

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
DATA = ROOT / "src/main/resources/data"
ID = "football_basketball_goal"
WIDTH, HEIGHT, DEPTH = 8, 6, 3
# North-facing mouth; the rear shoulders slope from z30 at the top to z46 below.
PARTS = [
    ([0, 0, 14, 2, 64, 16], "frame"), ([126, 0, 14, 128, 64, 16], "frame"),
    ([0, 62, 14, 128, 64, 16], "frame"),
    ([0, 0, 46, 2, 32, 48], "frame"), ([126, 0, 46, 128, 32, 48], "frame"),
    ([0, 62, 30, 128, 64, 32], "frame"),
    ([0, 62, 16, 2, 64, 30], "frame"), ([126, 62, 16, 128, 64, 30], "frame"),
    ([0, 0, 16, 2, 2, 46], "frame"), ([126, 0, 16, 128, 2, 46], "frame"),
    ([2, 0, 46, 126, 2, 48], "frame"),
    ([2, 2, 46, 126, 32, 46.125], "net"),
    ([2, 61.875, 16, 126, 62, 30], "net"),
    ([46, 64, 14, 48, 78, 16], "frame"), ([80, 64, 14, 82, 78, 16], "frame"),
    ([48, 76, 14, 80, 78, 16], "frame"),
    ([48, 64, 14, 80, 72, 14.125], "net"),
    ([40, 72, 12, 88, 96, 14], "board", {"north": "board_front"}),
    ([59, 72, 0, 69, 73.5, 1.5], "rim"), ([59, 72, 10.5, 69, 73.5, 12], "rim"),
    ([56, 72, 3, 57.5, 73.5, 9], "rim"), ([70.5, 72, 3, 72, 73.5, 9], "rim"),
    ([57.5, 72, 1.5, 59, 73.5, 3], "rim"), ([69, 72, 1.5, 70.5, 73.5, 3], "rim"),
    ([57.5, 72, 9, 59, 73.5, 10.5], "rim"), ([69, 72, 9, 70.5, 73.5, 10.5], "rim"),
    ([62, 72, 12, 66, 73.5, 14], "rim"),
]
# Pixel steps produce the sloped rear without rotating elements across block boundaries.
for y in range(32, 64, 2):
    z = 46 - (y - 32) / 2
    for x in (0, 126):
        PARTS.append(([x, y, z-1, x+2, y+2, z+2], "frame"))
    if y < 62:
        PARTS.append(([2, y, z, 126, y+2, z+0.125], "net"))
        PARTS.append(([2, y+1.875, z-1, 126, y+2, z+0.125], "net"))
for y in range(2, 62, 2):
    back = 46 if y < 32 else 46 - (y-32)/2
    PARTS.extend([([1.875, y, 16, 2, y+2, back], "net"),
                  ([126, y, 16, 126.125, y+2, back], "net")])
# A tapered hanging basket, open below; its own diamond-weave texture follows the height.
for y in range(64, 72):
    inset = (71-y)*0.375
    left, right = 57.5+inset, 70.5-inset
    front, back = 1.5+inset*0.75, 10.5-inset*0.75
    PARTS.extend([([left,y,front,right,y+1,front+0.125], "basket_net"),
                  ([left,y,back-0.125,right,y+1,back], "basket_net"),
                  ([left,y,front+0.125,left+0.125,y+1,back-0.125], "basket_net"),
                  ([right-0.125,y,front+0.125,right,y+1,back-0.125], "basket_net")])



def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2)+"\n", encoding="utf-8")


def index(column, row, depth):
    return column+WIDTH*row+WIDTH*HEIGHT*depth


def clips(bounds, column, row, depth):
    origin = [16*column, 16*row, 16*depth]
    low = [max(bounds[a], origin[a]) for a in range(3)]
    high = [min(bounds[a+3], origin[a]+16) for a in range(3)]
    return (low, high, origin) if all(a < b for a, b in zip(low, high)) else None


def uv_for(direction, low, high, origin, material):
    local_low = [low[a]-origin[a] for a in range(3)]
    local_high = [high[a]-origin[a] for a in range(3)]
    if material == "board_front":
        return [(high[0]-40)/3, (96-high[1])*2/3, (low[0]-40)/3, (96-low[1])*2/3]
    if material == "basket_net":
        if direction in ("north", "south"):
            return [(high[0]-57.5)*16/13, (72-high[1])*2, (low[0]-57.5)*16/13, (72-low[1])*2]
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
    net = Image.new("RGBA", (16, 16)); draw = ImageDraw.Draw(net)
    for i in (0, 8):
        draw.line((i, 0, i, 15), fill=(217, 219, 209, 255))
        draw.line((0, i, 15, i), fill=(217, 219, 209, 255))
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



def main():
    textures()
    exterior = solid_model(PARTS)
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
                    clipped = clips(part[0], column, row, depth)
                    if clipped:
                        low, high, origin = clipped
                        collision.append([low[a]-origin[a] for a in range(3)]+[high[a]-origin[a] for a in range(3)])
                elements = []
                for source in exterior["elements"]:
                    bounds = source["from"]+source["to"]
                    clipped = clips(bounds, column, row, depth)
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
        column, row, depth = cell%WIDTH, (cell//WIDTH)%HEIGHT, cell//(WIDTH*HEIGHT)
        for element in value["elements"]:
            e = json.loads(json.dumps(element))
            for side in ("from", "to"):
                e[side] = [(e[side][0]+column*16)/WIDTH, 1+(e[side][1]+row*16)/WIDTH, 4+(e[side][2]+depth*16)/WIDTH]
            item_elements.append(e)
    write_json(ASSETS / f"models/item/{ID}.json", {"parent": "minecraft:block/block", "render_type": "minecraft:cutout", "textures": mappings, "elements": item_elements})
    write_json(ASSETS / f"items/{ID}.json", {"model": {"type": "minecraft:model", "model": f"lsmmod:item/{ID}"}})
    write_json(DATA / f"lsmmod/loot_table/blocks/{ID}.json", {"type": "minecraft:block", "pools": [{"rolls": 1,
        "conditions": [{"condition": "minecraft:survives_explosion"}, {"condition": "minecraft:block_state_property", "block": f"lsmmod:{ID}", "properties": {"cell": "0"}}],
        "entries": [{"type": "minecraft:item", "name": f"lsmmod:{ID}"}]}]})
    java = ["package net.nicomar2009.lsmmod.block;", "", "/** Generated by tools/create_sports_goal_assets.py. */", "public final class SportsGoalShapes {", "    private SportsGoalShapes() {}",
            "    public static final int[] OCCUPIED = {"+", ".join(map(str, occupied))+"};", "    public static final double[][][] CELLS = {"]
    for cell in range(WIDTH*HEIGHT*DEPTH):
        java.append("        {"+", ".join("{"+", ".join(map(str, b))+"}" for b in all_cells[cell]["collision"])+"},")
    java += ["    };", "}"]
    (ROOT / "src/main/java/net/nicomar2009/lsmmod/block/SportsGoalShapes.java").write_text("\n".join(java)+"\n")
    # Collision manifest permits static model/collision tests and external previews.
    write_json(ROOT / "tools/sports_goal_geometry.json", {"occupied": occupied, "cells": all_cells})
    for locale, name in (("es_es", "Arco de fútbol con canasta de básquet"), ("en_us", "Football Goal with Basketball Hoop")):
        p = ASSETS / f"lang/{locale}.json"; value = json.loads(p.read_text()); value[f"block.lsmmod.{ID}"] = name; write_json(p, value)
    p = DATA / "minecraft/tags/block/mineable/pickaxe.json"; value = json.loads(p.read_text())
    if f"lsmmod:{ID}" not in value["values"]: value["values"].append(f"lsmmod:{ID}")
    write_json(p, value)


if __name__ == "__main__": main()
