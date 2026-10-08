"""Generate three opaque 16x16 vanilla cube surface assets (no build required)."""
import json
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
DATA = ROOT / "src/main/resources/data"
SURFACES = {
    "classroom_floor": ((188, 184, 172), "Piso de baldosas del salón", "Classroom Tile Floor"),
    "light_school_wall": ((202, 201, 187), "Pared clara del colegio", "Light School Wall"),
    "dark_school_wall": ((133, 125, 115), "Pared oscura del colegio", "Dark School Wall"),
}


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def texture(block_id, base):
    rng = random.Random(5054)
    image = Image.new("RGB", (16, 16))
    tile_count = 2 if block_id == "classroom_floor" else 4
    tile_shades = [[rng.choice((-2, -1, 0, 1, 2)) for _ in range(tile_count)] for _ in range(tile_count)]
    for y in range(16):
        for x in range(16):
            shade = rng.choice((-2, -1, 0, 0, 0, 1, 2))
            if block_id == "classroom_floor":
                # One-pixel grout every eight pixels: four tiles, seamless between cubes.
                shade += tile_shades[y // 8][x // 8]
                if x % 8 == 0 or y % 8 == 0:
                    shade = -15 + rng.choice((-1, 0, 1))
                elif x % 8 == 1 or y % 8 == 1:
                    shade += 2
            image.putpixel((x, y), tuple(channel + shade for channel in base))
    path = ASSETS / "textures/block" / f"{block_id}.png"
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def main():
    for block_id, (base, _, _) in SURFACES.items():
        texture(block_id, base)
        write_json(ASSETS / "blockstates" / f"{block_id}.json",
                   {"variants": {"": {"model": f"lsmmod:block/{block_id}"}}})
        write_json(ASSETS / "models/block" / f"{block_id}.json",
                   {"parent": "minecraft:block/cube_all", "textures": {"all": f"lsmmod:block/{block_id}"}})
        write_json(ASSETS / "models/item" / f"{block_id}.json",
                   {"parent": f"lsmmod:block/{block_id}"})
        write_json(ASSETS / "items" / f"{block_id}.json",
                   {"model": {"type": "minecraft:model", "model": f"lsmmod:item/{block_id}"}})
        write_json(DATA / "lsmmod/loot_table/blocks" / f"{block_id}.json", {
            "type": "minecraft:block", "random_sequence": f"lsmmod:blocks/{block_id}",
            "pools": [{"rolls": 1, "conditions": [{"condition": "minecraft:survives_explosion"}],
                       "entries": [{"type": "minecraft:item", "name": f"lsmmod:{block_id}"}]}]})
    for locale, index in (("es_es", 1), ("en_us", 2)):
        path = ASSETS / "lang" / f"{locale}.json"
        values = json.loads(path.read_text(encoding="utf-8"))
        for block_id, names in SURFACES.items():
            values[f"block.lsmmod.{block_id}"] = names[index]
        write_json(path, values)
    path = DATA / "minecraft/tags/block/mineable/pickaxe.json"
    values = json.loads(path.read_text(encoding="utf-8"))
    for block_id in SURFACES:
        entry = f"lsmmod:{block_id}"
        if entry not in values["values"]:
            values["values"].append(entry)
    write_json(path, values)


if __name__ == "__main__":
    main()
