"""Generate the reference school's white combined goal, board and cutout nets."""
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
# Keep the 8-block frame and saved 9x6x3 cell grid, including its +8px offset.
# The photo has a rectangular tubular goal and a tall board directly above its
# crossbar, without a second mesh-filled frame between the goal and the board.
BOARD = [40, 64, 12, 88, 96, 14]
RIM_Y = 70
PARTS = [
    ([0, 0, 14, 2, 64, 16], "frame"), ([126, 0, 14, 128, 64, 16], "frame"),
    ([0, 62, 14, 128, 64, 16], "frame"),
    ([0, 0, 46, 2, 64, 48], "frame"), ([126, 0, 46, 128, 64, 48], "frame"),
    ([2, 62, 46, 126, 64, 48], "frame"),
    ([0, 62, 16, 2, 64, 46], "frame"), ([126, 62, 16, 128, 64, 46], "frame"),
    ([0, 0, 16, 2, 2, 46], "frame"), ([126, 0, 16, 128, 2, 46], "frame"),
    ([2, 0, 46, 126, 2, 48], "frame"),
    ([2, 2, 46, 126, 62, 46.125], "net"),
    ([2, 61.875, 16, 126, 62, 46], "net"),
    ([1.875, 2, 16, 2, 62, 46], "net"),
    ([126, 2, 16, 126.125, 62, 46], "net"),
    # White mounting rails sit behind the board, joined to the goal crossbar.
    ([46, 64, 14, 48, 94, 16], "frame"), ([80, 64, 14, 82, 94, 16], "frame"),
    ([48, 92, 14, 80, 94, 16], "frame"),
    (BOARD.copy(), "board", {"north": "board_front"}),
    # Equal width/depth octagonal hoop, rather than the previous elongated rim.
    ([61, RIM_Y, 0, 67, RIM_Y+1, 1], "rim"),
    ([61, RIM_Y, 11, 67, RIM_Y+1, 12], "rim"),
    ([58, RIM_Y, 3, 59, RIM_Y+1, 9], "rim"),
    ([69, RIM_Y, 3, 70, RIM_Y+1, 9], "rim"),
    ([62, RIM_Y, 12, 66, RIM_Y+1, 14], "rim"),
]
# Pixel stairs close all four round-hoop corners without rotated cell fragments.
for step in range(3):
    left, right = 58+step, 69-step
    for x in (left, right):
        for z in (2-step, 9+step):
            PARTS.append(([x, RIM_Y, z, x+1, RIM_Y+1, z+1], "rim"))
# A tapered, open-bottom basket hangs just above the goal's crossbar.
for y in range(RIM_Y-6, RIM_Y):
    inset = (RIM_Y-1-y)*0.375
    left, right = 59+inset, 69-inset
    front, back = 1+inset, 11-inset
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


def clips(bounds, column, row, depth):
    origin = [16*column, 16*row, 16*depth]
    low = [max(bounds[a], origin[a]) for a in range(3)]
    high = [min(bounds[a+3], origin[a]+16) for a in range(3)]
    return (low, high, origin) if all(a < b for a, b in zip(low, high)) else None


def uv_for(direction, low, high, origin, material):
    local_low = [low[a]-origin[a] for a in range(3)]
    local_high = [high[a]-origin[a] for a in range(3)]
    if material == "board_front":
        return [(high[0]-BOARD[0]-SHIFT_X)/3, (BOARD[4]-high[1])/2,
                (low[0]-BOARD[0]-SHIFT_X)/3, (BOARD[4]-low[1])/2]
    if material == "basket_net":
        if direction in ("north", "south"):
            return [(high[0]-59-SHIFT_X)*16/10, (RIM_Y-high[1])*16/6,
                    (low[0]-59-SHIFT_X)*16/10, (RIM_Y-low[1])*16/6]
        if direction in ("east", "west"):
            return [(high[2]-1)*16/10, (RIM_Y-high[1])*16/6,
                    (low[2]-1)*16/10, (RIM_Y-low[1])*16/6]
    if direction == "north": return [local_high[0], 16-local_high[1], local_low[0], 16-local_low[1]]
    if direction == "south": return [local_low[0], 16-local_high[1], local_high[0], 16-local_low[1]]
    if direction == "east": return [local_high[2], 16-local_high[1], local_low[2], 16-local_low[1]]
    if direction == "west": return [local_low[2], 16-local_high[1], local_high[2], 16-local_low[1]]
    return [local_low[0], local_low[2], local_high[0], local_high[2]]


def textures():
    p = ASSETS / "textures/block"; p.mkdir(parents=True, exist_ok=True)
    for name, color in (("frame", (242, 242, 235)), ("board", (248, 248, 242)), ("rim", (178, 57, 27))):
        Image.new("RGB", (16, 16), color).save(p/f"sports_goal_{name}.png")
    board = Image.new("RGB", (48, 32), (248, 248, 242)); draw = ImageDraw.Draw(board)
    draw.rectangle((0, 0, 47, 31), outline=(65, 68, 64), width=1)
    draw.rectangle((15, 15, 32, 25), outline=(65, 68, 64), width=1)
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
