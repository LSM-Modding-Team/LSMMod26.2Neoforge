"""Create four passive school decorations, their resources, and matching collision boxes."""
import json
import random
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
DATA = ROOT / "src/main/resources/data"
NAMES = {
    "projector_screen": ("Pantalla de proyector", "Projector Screen"),
    "wall_projector": ("Proyector de pared", "Wall Projector"),
    "classroom_timetable": ("Horario del salón", "Classroom Timetable"),
    "emergency_backpack": ("Mochila de emergencia", "Emergency Backpack"),
}

# North-facing geometry, support at z=16. The original projector is shifted across two cells without scaling.
PROJECTOR = [
    ([2, 2, 15, 14, 14, 16], "projector_case", {}),
    ([6, 8, 7, 10, 10, 15], "projector_case", {}),
    ([5, 7, 9, 11, 8, 14], "projector_gray", {}),
    ([2, 4, 2, 14, 5, 9], "projector_case", {}),
    ([1, 5, 1, 15, 9, 10], "projector_case", {"east": "projector_vent", "west": "projector_vent"}),
    ([3, 9, 2, 13, 10, 9], "projector_case", {}),
    ([3, 6, .5, 13, 7.25, 1], "projector_optics", {}),
    ([4, 9.95, 3, 7, 10.15, 5], "projector_gray", {}),
]
TIMETABLE = [([1, 4.5, 15, 15, 11.5, 16], "paper_edge", {"north": "classroom_timetable"})]
BACKPACK = [
    ([4, 2, 11, 12, 12, 15], "backpack_red", {"north": "backpack_front"}),
    ([5, 12, 12, 11, 14, 15], "backpack_red", {}),
    ([5, 1, 12, 11, 2, 15], "backpack_red", {}),
    ([4.5, 3, 9.5, 11.5, 7, 11], "backpack_red", {"north": "backpack_pocket"}),
    ([4.5, 7, 9.5, 11.5, 7.4, 11], "backpack_zipper", {}),
    ([4, 3, 15, 5, 12, 16], "backpack_strap", {}),
    ([11, 3, 15, 12, 12, 16], "backpack_strap", {}),
    ([6, 14, 13, 7, 15, 14], "backpack_strap", {}),
    ([9, 14, 13, 10, 15, 14], "backpack_strap", {}),
    ([6, 15, 13, 10, 15.5, 14], "backpack_strap", {}),
]


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def save_texture(name, image):
    path = ASSETS / "textures/block" / f"{name}.png"
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def grain(base, seed, spread=2):
    rng = random.Random(seed)
    im = Image.new("RGB", (16, 16))
    for y in range(16):
        for x in range(16):
            offset = rng.randint(-spread, spread)
            im.putpixel((x, y), tuple(max(0, min(255, channel+offset)) for channel in base))
    return im


def textures():
    save_texture("projector_screen", Image.new("RGB", (16, 16), "#ffffff"))
    save_texture("paper_edge", Image.new("RGB", (16, 16), (238, 238, 235)))
    save_texture("projector_case", grain((232, 233, 231), 1, 1))
    save_texture("projector_gray", grain((177, 181, 179), 2, 1))
    vent = grain((209, 213, 210), 3, 1)
    draw = ImageDraw.Draw(vent)
    for y in range(2, 15, 2):
        draw.line((2, y, 13, y), fill=(76, 82, 80))
        draw.line((3, y+1, 13, y+1), fill=(163, 168, 164))
    save_texture("projector_vent", vent)
    optics = Image.new("RGB", (16, 16), (23, 26, 26))
    draw = ImageDraw.Draw(optics)
    draw.ellipse((10, 2, 15, 13), fill=(50, 66, 66))
    draw.ellipse((11, 4, 14, 11), fill=(19, 28, 30))
    draw.point((12, 5), fill=(144, 170, 166))
    save_texture("projector_optics", optics)
    save_texture("backpack_red", grain((188, 52, 31), 4))
    save_texture("backpack_strap", grain((122, 31, 23), 5))
    save_texture("backpack_zipper", grain((56, 49, 44), 6))
    front = grain((194, 56, 32), 7)
    draw = ImageDraw.Draw(front)
    draw.line((1, 1, 1, 14), fill=(150, 40, 27))
    draw.line((14, 1, 14, 14), fill=(150, 40, 27))
    draw.ellipse((5, 2, 10, 7), outline=(235, 206, 178))
    draw.rectangle((7, 3, 8, 6), fill=(241, 231, 205))
    draw.rectangle((6, 4, 9, 5), fill=(241, 231, 205))
    draw.rectangle((1, 9, 14, 11), fill=(213, 208, 188))
    draw.line((2, 8, 13, 8), fill=(48, 43, 39))
    for x in (3, 5, 8, 9, 12):
        draw.point((x, 10), fill=(83, 78, 69))
    save_texture("backpack_front", front)
    pocket = grain((194, 56, 32), 8)
    draw = ImageDraw.Draw(pocket)
    draw.rectangle((1, 1, 14, 14), outline=(146, 38, 25))
    draw.rectangle((2, 3, 13, 5), fill=(223, 214, 190))
    for x in (3, 5, 7, 10, 12):
        draw.point((x, 4), fill=(80, 72, 64))
    for y in (9, 11):
        for x in (4, 5, 7, 10, 11):
            draw.point((x, y), fill=(229, 202, 158))
    save_texture("backpack_pocket", pocket)
    # Exactly the reference table layout: header, tutorial band, 4 lessons, break,
    # 3 lessons, break, 2 lessons, exit band. No title, logos, words, or real glyphs.
    table = Image.new("RGB", (128, 64), (255, 255, 255))
    draw = ImageDraw.Draw(table)
    rows = [("header", 6), ("band", 5)] + [("lesson", 4)]*4 + [("band", 5)]
    rows += [("lesson", 4)]*3 + [("band", 5)] + [("lesson", 4)]*2 + [("band", 5)]
    boundaries = [0, 5, 18, 40, 62, 84, 106, 127]
    rng = random.Random(801)
    y = 1
    for kind, height in rows:
        color = (255, 55, 55) if kind == "header" else (211, 220, 224) if kind == "band" else (255, 255, 255)
        draw.rectangle((1, y, 126, y+height-1), fill=color)
        if kind != "band":
            for x in boundaries[1:-1]:
                draw.line((x, y, x, y+height-1), fill=(27, 27, 27))
        spans = [(40, 87)] if kind == "band" else list(zip(boundaries[:-1], boundaries[1:]))
        for left, right in spans:
            ink = (255, 255, 255) if kind == "header" else (45, 45, 45)
            for x in range(left+2, right-1):
                if rng.randrange(3): draw.point((x, y+height//2), fill=ink)
        draw.line((0, y+height-1, 127, y+height-1), fill=(27, 27, 27))
        y += height
    assert y == 63
    draw.rectangle((0, 0, 127, 63), outline=(27, 27, 27))
    save_texture("classroom_timetable", table)


def model(parts):
    names = {default for _, default, _ in parts}
    for _, _, overrides in parts: names.update(overrides.values())
    elements = []
    for bounds, default, overrides in parts:
        faces = {}
        for face in ("north", "south", "east", "west", "up", "down"):
            # North face is reversed in world x: reverse its UV to keep the artwork aligned.
            uv = [16, 0, 0, 16] if face == "north" else [0, 0, 16, 16]
            faces[face] = {"texture": "#"+overrides.get(face, default), "uv": uv}
        elements.append({"from": bounds[:3], "to": bounds[3:], "faces": faces})
    return {"parent": "minecraft:block/block", "textures": {name: f"lsmmod:block/{name}" for name in sorted(names)},
            "elements": elements}


def projector_cells():
    """Translate eight pixels to center on a two-cell span; never scale geometry or UVs."""
    full = model(PROJECTOR)
    cells = []
    for column in range(2):
        value = {"parent": full["parent"], "textures": dict(full["textures"]), "elements": []}
        for element in full["elements"]:
            lo, hi = list(element["from"]), list(element["to"])
            lo[0] += 8; hi[0] += 8
            left, right = max(lo[0],16*column), min(hi[0],16*(column+1))
            if left >= right: continue
            faces = {}
            for direction, original in element["faces"].items():
                if direction=="west" and left!=lo[0] or direction=="east" and right!=hi[0]: continue
                face = dict(original); u0,v0,u1,v1 = face["uv"]
                if direction in ("north","south","up","down"):
                    start,end = (right,left) if direction in ("north","down") else (left,right)
                    original_start,original_end = (hi[0],lo[0]) if direction in ("north","down") else (lo[0],hi[0])
                    uv = lambda x: u0+(u1-u0)*(x-original_start)/(original_end-original_start)
                    face["uv"] = [uv(start),v0,uv(end),v1]
                faces[direction]=face
            lo[0],hi[0]=left-16*column,right-16*column
            value["elements"].append({"from":lo,"to":hi,"faces":faces})
        cells.append(value)
    return cells


def java_geometry():
    lines = ["package net.nicomar2009.lsmmod.block;", "", "/** Generated by tools/create_classroom_wall_assets.py; boxes match the models. */",
             "public final class SchoolDecorationShapes {", "    private SchoolDecorationShapes() {}"]
    for name, parts in (("PROJECTOR", PROJECTOR), ("TIMETABLE", TIMETABLE), ("BACKPACK", BACKPACK)):
        lines.append(f"    public static final double[][] {name} = {{")
        lines += ["        {"+", ".join(str(v) for v in bounds)+"}," for bounds, _, _ in parts]
        lines.append("    };")
    lines.append("    public static final double[][][] PROJECTOR_CELLS = {")
    for cell in projector_cells():
        lines.append("        {")
        for e in cell["elements"]:
            lines.append("            {"+", ".join(str(v) for v in e["from"]+e["to"])+"},")
        lines.append("        },")
    lines.append("    };")
    lines.append("}")
    path = ROOT / "src/main/java/net/nicomar2009/lsmmod/block/SchoolDecorationShapes.java"
    path.write_text("\n".join(lines)+"\n", encoding="utf-8")


def main():
    textures()
    java_geometry()
    parts = {"projector_screen": [([0, 0, 15, 16, 16, 16], "projector_screen", {})],
             "wall_projector": PROJECTOR, "classroom_timetable": TIMETABLE, "emergency_backpack": BACKPACK}
    for block_id in NAMES:
        value = model(parts[block_id])
        value["textures"]["particle"] = f"lsmmod:block/{'projector_screen' if block_id == 'projector_screen' else 'backpack_red' if block_id == 'emergency_backpack' else 'paper_edge' if block_id == 'classroom_timetable' else 'projector_case'}"
        write_json(ASSETS / "models/block" / f"{block_id}.json", value)
        variants = {}
        for facing, rotation in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
            for row in range(3) if block_id == "projector_screen" else (None,):
                for column in range(4) if block_id == "projector_screen" else range(2) if block_id == "wall_projector" else (None,):
                    key = f"facing={facing}" if column is None else f"column={column},facing={facing}" if row is None else f"column={column},facing={facing},row={row}"
                    variants[key] = {"model": f"lsmmod:block/{block_id}_{column}" if block_id=="wall_projector" else f"lsmmod:block/{block_id}", "y": rotation}
        write_json(ASSETS / "blockstates" / f"{block_id}.json", {"variants": variants})
        if block_id == "wall_projector":
            for column,cell in enumerate(projector_cells()):
                cell["textures"]["particle"]="lsmmod:block/projector_case"
                write_json(ASSETS / "models/block" / f"wall_projector_{column}.json",cell)
        if block_id == "projector_screen":
            item_model = model([([0, 2, 7.875, 16, 14, 8.125], "projector_screen", {})])
        else:
            item_model = {"parent": f"lsmmod:block/{block_id}"}
        write_json(ASSETS / "models/item" / f"{block_id}.json", item_model)
        write_json(ASSETS / "items" / f"{block_id}.json", {"model": {"type": "minecraft:model", "model": f"lsmmod:item/{block_id}"}})
        conditions = [{"condition": "minecraft:survives_explosion"}]
        if block_id == "projector_screen":
            conditions.append({"condition": "minecraft:block_state_property", "block": "lsmmod:projector_screen",
                               "properties": {"column": "0", "row": "0"}})
        if block_id == "wall_projector":
            conditions.append({"condition":"minecraft:block_state_property","block":"lsmmod:wall_projector","properties":{"column":"0"}})
        write_json(DATA / "lsmmod/loot_table/blocks" / f"{block_id}.json", {
            "type": "minecraft:block", "pools": [{"rolls": 1, "conditions": conditions,
                                                  "entries": [{"type": "minecraft:item", "name": f"lsmmod:{block_id}"}]}]})
    for locale, index in (("es_es", 0), ("en_us", 1)):
        path = ASSETS / "lang" / f"{locale}.json"
        values = json.loads(path.read_text(encoding="utf-8"))
        for block_id, names in NAMES.items(): values[f"block.lsmmod.{block_id}"] = names[index]
        write_json(path, values)
    for tool, ids in (("pickaxe", ("wall_projector",)), ("axe", ("projector_screen", "classroom_timetable"))):
        path = DATA / "minecraft/tags/block/mineable" / f"{tool}.json"
        values = json.loads(path.read_text(encoding="utf-8"))
        for block_id in ids:
            if f"lsmmod:{block_id}" not in values["values"]: values["values"].append(f"lsmmod:{block_id}")
        write_json(path, values)


if __name__ == "__main__":
    main()
