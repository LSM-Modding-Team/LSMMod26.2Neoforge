"""Generate the 5x3 double metal gate; every block texture is 16x16 pixels."""
import json
import random
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
DATA = ROOT / "src/main/resources/data"
TEXTURES = ASSETS / "textures/block"
MODELS = ASSETS / "models/block"


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def gate_art():
    rng = random.Random(7305)
    image = Image.new("RGBA", (80, 48))
    for y in range(48):
        for x in range(80):
            local = x % 40
            grain = rng.choice((-3, -2, -1, 0, 0, 1, 2))
            frame = local in (0, 1, 38, 39) or y in (0, 1, 7, 8, 46, 47)
            if frame:
                base = (54, 43, 38)
                if local in (1, 38) or y in (1, 8, 46):
                    base = (74, 59, 50)
            elif y < 7:
                # Open diamond mesh, with actual alpha holes rather than painted glass.
                if (local + y) % 6 != 0 and (local - y) % 6 != 0:
                    continue
                base = (60, 51, 44)
            else:
                # Subtle pressed-sheet panels, rails and desaturated wear.
                base = (76, 56, 48)
                if 4 <= local <= 35 and 12 <= y <= 43:
                    base = (82, 62, 53)
                if local in (3, 36) and 11 <= y <= 44 or y in (11, 26, 44) and 3 <= local <= 36:
                    base = (50, 40, 36)
                elif local in (4, 35) and 12 <= y <= 43 or y in (12, 27, 43) and 4 <= local <= 35:
                    base = (96, 75, 63)
                if y > 34 and rng.randrange(12) == 0:
                    grain += 7
                if local in (2, 37) and y in (10, 28, 44):
                    base = (113, 94, 76)
            image.putpixel((x, y), tuple(channel + grain for channel in base) + (255,))
    draw = ImageDraw.Draw(image)
    draw.line((37, 22, 37, 26), fill=(30, 28, 26, 255), width=1)
    draw.line((42, 22, 42, 26), fill=(30, 28, 26, 255), width=1)
    draw.point((38, 24), fill=(139, 124, 101, 255))
    draw.point((41, 24), fill=(139, 124, 101, 255))
    return image


def element(bounds, uv, broad_faces):
    faces = {}
    for face in ("north", "south", "east", "west", "up", "down"):
        face_uv = list(uv)
        if face in ("north", "east"):
            face_uv = [uv[2], uv[1], uv[0], uv[3]]
        faces[face] = {"texture": "#panel" if face in broad_faces else "#edge",
                       "uv": face_uv if face in broad_faces else [0, 0, 2, 16]}
    return {"from": bounds[:3], "to": bounds[3:], "faces": faces}


def model(texture, elements):
    return {"parent": "minecraft:block/block", "render_type": "minecraft:cutout",
            "textures": {"particle": "lsmmod:block/school_gate_edge", "panel": f"lsmmod:block/{texture}",
                         "edge": "lsmmod:block/school_gate_edge"}, "elements": elements}


def main():
    TEXTURES.mkdir(parents=True, exist_ok=True)
    image = gate_art()
    edge = Image.new("RGB", (16, 16), (57, 45, 39))
    draw = ImageDraw.Draw(edge)
    for x in range(0, 16, 4):
        draw.line((x, 0, x, 15), fill=(66, 52, 44))
    edge.save(TEXTURES / "school_gate_edge.png")
    write_json(MODELS / "school_gate_empty.json", {"textures": {"particle": "lsmmod:block/school_gate_edge"}, "elements": []})
    for row in range(3):
        for column in range(5):
            name = f"school_gate_closed_{column}_{row}"
            image.crop((column * 16, (2-row)*16, (column+1)*16, (3-row)*16)).save(TEXTURES / f"{name}.png")
            if column == 2:
                elements = [element([0, 0, 7, 7.875, 16, 9], [0, 0, 7.875, 16], ("north", "south")),
                            element([8.125, 0, 7, 16, 16, 9], [8.125, 0, 16, 16], ("north", "south"))]
            else:
                elements = [element([0, 0, 7, 16, 16, 9], [0, 0, 16, 16], ("north", "south"))]
            write_json(MODELS / f"{name}.json", model(name, elements))
        for column in (0, 4):
            for depth, (offset, length) in enumerate(((0, 8), (8, 16), (24, 16))):
                name = f"school_gate_open_{column}_{row}_{depth}"
                panel = Image.new("RGBA", (16, 16))
                z1 = 8 if depth == 0 else 0
                for z in range(z1, z1 + length):
                    u = offset + z-z1
                    source_x = u if column == 0 else 79-u
                    for y in range(16):
                        panel.putpixel((z, y), image.getpixel((source_x, (2-row)*16+y)))
                panel.save(TEXTURES / f"{name}.png")
                x1 = 0 if column == 0 else 14
                write_json(MODELS / f"{name}.json", model(name,
                    [element([x1, 0, z1, x1+2, 16, z1+length], [z1, 0, z1+length, 16], ("east", "west"))]))
    variants = {}
    for facing, rotation in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
        for opened in (False, True):
            for column in range(5):
                for row in range(3):
                    for depth in range(3):
                        name = "school_gate_empty"
                        if opened and column in (0, 4):
                            name = f"school_gate_open_{column}_{row}_{depth}"
                        elif not opened and depth == 0:
                            name = f"school_gate_closed_{column}_{row}"
                        key = f"column={column},depth={depth},facing={facing},open={str(opened).lower()},row={row}"
                        variants[key] = {"model": f"lsmmod:block/{name}", "y": rotation}
    write_json(ASSETS / "blockstates/school_gate.json", {"variants": variants})
    # A scaled complete gate for the inventory, using the same fifteen 16x16 textures.
    textures = {"particle": "lsmmod:block/school_gate_edge", "edge": "lsmmod:block/school_gate_edge"}
    elements = []
    for row in range(3):
        for column in range(5):
            key = f"tile_{column}_{row}"
            textures[key] = f"lsmmod:block/school_gate_closed_{column}_{row}"
            piece = element([column*3.2, 3.2+row*3.2, 7.8, (column+1)*3.2, 6.4+row*3.2, 8.2],
                            [0, 0, 16, 16], ("north", "south"))
            for face in ("north", "south"):
                piece["faces"][face]["texture"] = f"#{key}"
            elements.append(piece)
    write_json(ASSETS / "models/item/school_gate.json", {"parent": "minecraft:block/block", "render_type": "minecraft:cutout",
                                                       "textures": textures, "elements": elements})
    write_json(ASSETS / "items/school_gate.json", {"model": {"type": "minecraft:model", "model": "lsmmod:item/school_gate"}})
    write_json(DATA / "lsmmod/loot_table/blocks/school_gate.json", {
        "type": "minecraft:block", "pools": [{"rolls": 1, "conditions": [
            {"condition": "minecraft:survives_explosion"},
            {"condition": "minecraft:block_state_property", "block": "lsmmod:school_gate",
             "properties": {"column": "2", "row": "0", "depth": "0"}}],
            "entries": [{"type": "minecraft:item", "name": "lsmmod:school_gate"}]}]})
    for locale, name in (("es_es", "Portón doble del colegio"), ("en_us", "School Double Gate")):
        path = ASSETS / "lang" / f"{locale}.json"
        values = json.loads(path.read_text(encoding="utf-8"))
        values["block.lsmmod.school_gate"] = name
        write_json(path, values)
    path = DATA / "minecraft/tags/block/mineable/pickaxe.json"
    values = json.loads(path.read_text(encoding="utf-8"))
    if "lsmmod:school_gate" not in values["values"]:
        values["values"].append("lsmmod:school_gate")
    write_json(path, values)


if __name__ == "__main__":
    main()
