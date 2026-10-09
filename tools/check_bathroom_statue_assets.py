"""Check statue z-fighting regressions and sanitary fixture resources without Java compilation."""
import json
import re
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
NORMALS = {"west": (0, False), "east": (0, True), "down": (1, False),
           "up": (1, True), "north": (2, False), "south": (2, True)}


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def mesh(model, name):
    faces = []
    for element in model["elements"]:
        low, high = element["from"], element["to"]
        assert all(0 <= a < b <= 16 for a, b in zip(low, high)), (name, "zero-volume or out-of-cell box")
        for direction, face in element["faces"].items():
            axis, positive = NORMALS[direction]
            plane = high[axis] if positive else low[axis]
            p, q = [a for a in range(3) if a != axis]
            rectangle = (low[p], low[q], high[p], high[q])
            for other_axis, other_plane, other in faces:
                if axis == other_axis and plane == other_plane:
                    overlap = min(rectangle[2], other[2])-max(rectangle[0], other[0])
                    overlap2 = min(rectangle[3], other[3])-max(rectangle[1], other[1])
                    assert overlap <= 1e-9 or overlap2 <= 1e-9, (name, "coplanar overlapping faces")
            faces.append((axis, plane, rectangle))
            texture = model["textures"][face["texture"][1:]].split(":")[1]
            image = Image.open(ASSETS / f"textures/{texture}.png")
            assert image.size == (16, 16) and image.mode == "RGB"
    return faces


def main():
    halves = {}
    for half in ("down", "up"):
        name = f"san_martin_de_porres_{half}"
        halves[half] = mesh(read(ASSETS / f"models/block/{name}.json"), name)
    # No new caps may cover the shared seam where the two robe pieces meet.
    lower_caps = [rect for axis, plane, rect in halves["down"] if axis == 1 and plane == 16]
    upper_caps = [rect for axis, plane, rect in halves["up"] if axis == 1 and plane == 0]
    for a in lower_caps:
        for b in upper_caps:
            assert min(a[2], b[2])-max(a[0], b[0]) <= 0 or min(a[3], b[3])-max(a[1], b[1]) <= 0
    mesh(read(ASSETS / "models/item/san_martin_de_porres.json"), "statue item")
    source = (ROOT / "src/main/java/net/nicomar2009/lsmmod/block/BathroomShapes.java").read_text()
    fixture_boxes = {}
    tall_source = (ROOT / "src/main/java/net/nicomar2009/lsmmod/block/TallBathroomShapes.java").read_text()
    for block_id, constant in (("toilet", "TOILET_CLOSED"), ("men_urinal", "URINAL"), ("adult_men_urinal", "ADULT_URINAL")):
        tall = block_id != "men_urinal"
        models = [(read(ASSETS / f"models/block/{block_id}_{half}_closed.json"), offset)
                  for half, offset in (("lower", 0), ("upper", 16))] if tall else [(read(ASSETS / f"models/block/{block_id}.json"), 0)]
        for model, _ in models: mesh(model, block_id)
        if tall:
            for half in ("lower", "upper"):
                mesh(read(ASSETS / f"models/block/{block_id}_{half}_open.json"), block_id+" open")
        section = re.search(constant+r" = \{(.*?)\n    \};", tall_source if tall else source, re.S).group(1)
        boxes = [[float(v) for v in values.split(",")] for values in re.findall(r"\{([^{}]+)\}", section)]
        fixture_boxes[block_id] = boxes
        assert min(b[1] for b in boxes) == 0
        for model, offset in models:
            for element in model["elements"]:
                low, high = list(element["from"]), list(element["to"])
                low[1] += offset; high[1] += offset
                assert any(all(b[i] <= low[i] and high[i] <= b[i+3]+1e-9 for i in range(3)) for b in boxes)
        variants = read(ASSETS / f"blockstates/{block_id}.json")["variants"]
        assert len(variants) == (16 if tall else 4)
        for facing, rotation in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
            if tall:
                for half in ("lower", "upper"):
                    for opened in ("false", "true"):
                        assert variants[f"facing={facing},half={half},open={opened}"]["y"] == rotation
            else:
                assert variants[f"facing={facing}"]["y"] == rotation
        for locale in ("en_us", "es_es"):
            assert f"block.lsmmod.{block_id}" in read(ASSETS / f"lang/{locale}.json")
        assert read(ASSETS / f"items/{block_id}.json")["model"]["model"] == f"lsmmod:item/{block_id}"
        loot = read(ROOT / f"src/main/resources/data/lsmmod/loot_table/blocks/{block_id}.json")
        assert loot["pools"][0]["entries"][0]["name"] == f"lsmmod:{block_id}"
    toilet_panels = fixture_boxes["toilet"][:2]
    urinal_panels = fixture_boxes["men_urinal"][:2]
    assert all(b[3]-b[0] == 1 for b in toilet_panels+urinal_panels)
    assert all(u[4]-u[1] < t[4]-t[1] and u[5]-u[2] < t[5]-t[2] for t, u in zip(toilet_panels, urinal_panels))
    assert all(b[4]-b[1] == 32 for b in toilet_panels)
    assert max(b[4] for b in fixture_boxes["adult_men_urinal"]) == 32
    open_section = re.search(r"TOILET_OPEN = \{(.*?)\n    \};", tall_source, re.S).group(1)
    opened = [[float(v) for v in values.split(",")] for values in re.findall(r"\{([^{}]+)\}", open_section)]
    for y in (8, 24):
        point = (8, y, .5)
        inside = lambda b: all(b[i] < point[i] < b[i+3] for i in range(3))
        assert any(inside(b) for b in fixture_boxes["toilet"])
        assert not any(inside(b) for b in opened)
    for block_id in ("toilet", "adult_men_urinal"):
        loot = read(ROOT / f"src/main/resources/data/lsmmod/loot_table/blocks/{block_id}.json")
        assert next(c for c in loot["pools"][0]["conditions"] if c["condition"] == "minecraft:block_state_property")["properties"] == {"half": "lower"}
    print("OK: solid statue meshes/item without coplanar overlaps, clean two-half seam,")
    print("complete fixture resources, floor-contact geometry and 1px partitions (smaller on urinal).")
    print("Static checks only: no Java compilation or Minecraft rendering.")


if __name__ == "__main__":
    main()
