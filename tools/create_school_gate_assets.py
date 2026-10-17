"""Generate the 6x4 double metal gate; every block texture is 16x16 pixels."""
from resource_modules import module_read_text, module_write_text, output_path
from resource_json import preview_path
from resource_json import resource_json
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
    module_write_text(path, resource_json(path, value, ensure_ascii=False) + "\n", encoding="utf-8")


def gate_art():
    rng = random.Random(7305)
    image = Image.new("RGBA", (96, 64))
    for y in range(64):
        for x in range(96):
            local = x % 48
            grain = rng.choice((-3, -2, -1, 0, 0, 1, 2))
            frame = local in (0, 1, 46, 47) or y in (0, 1, 11, 12, 62, 63)
            if frame:
                base = (54, 43, 38)
                if local in (1, 46) or y in (1, 12, 62):
                    base = (74, 59, 50)
            elif y < 11:
                # Open diamond mesh, with actual alpha holes rather than painted glass.
                if (local + y) % 6 != 0 and (local - y) % 6 != 0:
                    continue
                base = (60, 51, 44)
            else:
                # Subtle pressed-sheet panels, rails and desaturated wear.
                base = (76, 56, 48)
                if 4 <= local <= 43 and 16 <= y <= 59:
                    base = (82, 62, 53)
                if local in (3, 44) and 15 <= y <= 60 or y in (15, 36, 60) and 3 <= local <= 44:
                    base = (50, 40, 36)
                elif local in (4, 43) and 16 <= y <= 59 or y in (16, 37, 59) and 4 <= local <= 43:
                    base = (96, 75, 63)
                if y > 46 and rng.randrange(12) == 0:
                    grain += 7
                if local in (2, 45) and y in (14, 38, 60):
                    base = (113, 94, 76)
            image.putpixel((x, y), tuple(channel + grain for channel in base) + (255,))
    draw = ImageDraw.Draw(image)
    draw.line((45, 30, 45, 35), fill=(30, 28, 26, 255), width=1)
    draw.line((50, 30, 50, 35), fill=(30, 28, 26, 255), width=1)
    draw.point((46, 33), fill=(139, 124, 101, 255))
    draw.point((49, 33), fill=(139, 124, 101, 255))
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
    (ROOT/"previews").mkdir(exist_ok=True)
    image.resize((576,384),Image.Resampling.NEAREST).save(output_path(preview_path('school_gate_6x4.png')))
    edge = Image.new("RGB", (16, 16), (57, 45, 39))
    draw = ImageDraw.Draw(edge)
    for x in range(0, 16, 4):
        draw.line((x, 0, x, 15), fill=(66, 52, 44))
    edge.save(output_path(TEXTURES / "school_gate_edge.png"))
    write_json(MODELS / "school_gate_empty.json", {"textures": {"particle": "lsmmod:block/school_gate_edge"}, "elements": []})
    for row in range(4):
        for column in range(6):
            name = f"school_gate_closed_{column}_{row}"
            image.crop((column * 16, (3-row)*16, (column+1)*16, (4-row)*16)).save(output_path(TEXTURES / f"{name}.png"))
            if column == 2:
                elements = [element([0,0,7,15.875,16,9],[0,0,15.875,16],("north","south"))]
            elif column == 3:
                elements = [element([.125,0,7,16,16,9],[.125,0,16,16],("north","south"))]
            else:
                elements = [element([0, 0, 7, 16, 16, 9], [0, 0, 16, 16], ("north", "south"))]
            write_json(MODELS / f"{name}.json", model(name, elements))
        for column in (0, 5):
            for depth, (offset, length) in enumerate(((0, 8), (8, 16), (24, 16), (40, 8))):
                name = f"school_gate_open_{column}_{row}_{depth}"
                panel = Image.new("RGBA", (16, 16))
                z1 = 8 if depth == 0 else 0
                for z in range(z1, z1 + length):
                    u = offset + z-z1
                    source_x = u if column == 0 else 95-u
                    for y in range(16):
                        panel.putpixel((z, y), image.getpixel((source_x, (3-row)*16+y)))
                panel.save(output_path(TEXTURES / f"{name}.png"))
                x1 = 0 if column == 0 else 14
                write_json(MODELS / f"{name}.json", model(name,
                    [element([x1, 0, z1, x1+2, 16, z1+length], [z1, 0, z1+length, 16], ("east", "west"))]))
    variants = {}
    for facing, rotation in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
        for opened in (False, True):
            for column in range(6):
                for row in range(4):
                    for depth in range(4):
                        name = "school_gate_empty"
                        if opened and column in (0, 5):
                            name = f"school_gate_open_{column}_{row}_{depth}"
                        elif not opened and depth == 0:
                            name = f"school_gate_closed_{column}_{row}"
                        key = f"column={column},depth={depth},facing={facing},open={str(opened).lower()},row={row}"
                        variants[key] = {"model": f"lsmmod:block/{name}", "y": rotation}
    write_json(ASSETS / "blockstates/school_gate.json", {"variants": variants})
    # A scaled complete gate for the inventory, using the same twenty-four 16x16 textures.
    textures = {"particle": "lsmmod:block/school_gate_edge", "edge": "lsmmod:block/school_gate_edge"}
    elements = []
    for row in range(4):
        for column in range(6):
            key = f"tile_{column}_{row}"
            textures[key] = f"lsmmod:block/school_gate_closed_{column}_{row}"
            piece = element([column*(8/3), (8/3)+row*(8/3), 7.8, (column+1)*(8/3), (8/3)+(row+1)*(8/3), 8.2],
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
        values = json.loads(module_read_text(path, encoding="utf-8"))
        values["block.lsmmod.school_gate"] = name
        write_json(path, values)
    path = DATA / "minecraft/tags/block/mineable/pickaxe.json"
    values = json.loads(module_read_text(path, encoding="utf-8"))
    if "lsmmod:school_gate" not in values["values"]:
        values["values"].append("lsmmod:school_gate")
    write_json(path, values)


if __name__ == "__main__":
    main()
