"""Generate passive sanitary fixtures and a solid, non-overlapping San Martin statue."""
import json
import random
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
DATA = ROOT / "src/main/resources/data"

TOILET = [
    ([0, 0, 1, 1, 16, 15], "partition"), ([15, 0, 1, 16, 16, 15], "partition"),
    ([5, 0, 5, 11, 1, 11], "porcelain"), ([6, 1, 6, 10, 5, 10], "porcelain"),
    ([4, 5, 3, 12, 5.75, 11.5], "porcelain"),
    ([3.5, 5.75, 3, 5, 8, 11], "porcelain"), ([11, 5.75, 3, 12.5, 8, 11], "porcelain"),
    ([5, 5.75, 2.5, 11, 8, 4], "porcelain"), ([5, 5.75, 10, 11, 8, 11.5], "porcelain"),
    ([3.5, 8, 2.5, 5, 9, 11.5], "seat"), ([11, 8, 2.5, 12.5, 9, 11.5], "seat"),
    ([5, 8, 2.5, 11, 9, 4], "seat"), ([5, 8, 10, 11, 9, 11.5], "seat"),
    ([4, 5, 12, 12, 13, 15], "porcelain"), ([3.75, 13, 11.75, 12.25, 13.5, 15.25], "seat"),
    ([10, 10.5, 11.75, 11.25, 11.25, 12], "chrome"),
]
URINAL = [
    ([1, 2, 9, 2, 13, 16], "partition"), ([14, 2, 9, 15, 13, 16], "partition"),
    ([5, 0, 11, 11, 1, 16], "porcelain"), ([5, 1, 12, 11, 3, 16], "porcelain"),
    ([4, 3, 15, 12, 14, 16], "porcelain"),
    ([4, 4, 11, 5, 14, 15], "porcelain"), ([11, 4, 11, 12, 14, 15], "porcelain"),
    ([5, 13, 11, 11, 14, 15], "porcelain"),
    ([4, 2, 9, 12, 3, 15], "porcelain"),
    ([4, 3, 9, 5, 5, 15], "porcelain"), ([11, 3, 9, 12, 5, 15], "porcelain"),
    ([5, 3, 9, 11, 4, 10], "porcelain"),
    ([7, 3, 12, 9, 3.125, 14], "drain"),
    ([7.5, 14, 15, 8.5, 15.5, 16], "chrome"),
    ([7, 14.5, 14.5, 9, 15, 15], "chrome"),
]

# Statue keeps the existing east-facing 8x16 footprint and two-block placement/collision.
STATUE = [
    ([0, 0, 0, 8, 2, 16], "pedestal"), ([.5, 2, .5, 7.5, 2.5, 15.5], "grass"),
    ([2, 2.5, 4.5, 6, 8, 11.5], "cream"),
    ([1.5, 7, 3.5, 6.5, 18, 12.5], "robe"),
    ([2, 18, 4, 6, 23, 12], "robe"),
    ([2, 18, 2.5, 5.5, 23, 4], "robe"), ([2, 18, 12, 5.5, 23, 13.5], "robe"),
    ([2.5, 21, 4.5, 5.5, 24, 11.5], "cream"),
    ([3, 23.5, 6, 5.5, 25, 10], "skin"),
    ([2, 25, 5.5, 6.5, 29.5, 10.5], "skin", {"east": "face"}),
    ([2, 29.5, 5.5, 6.5, 30.5, 10.5], "hair"),
    ([2, 27.5, 5.5, 2.5, 29.5, 10.5], "hair"),
    ([2.5, 27.5, 5.25, 5.5, 29.5, 5.5], "hair"),
    ([2.5, 27.5, 10.5, 5.5, 29.5, 10.75], "hair"),
    ([5.5, 18.5, 3.75, 6.5, 20.5, 5.5], "cream"),
    ([6.5, 18.75, 4, 7, 20, 5.25], "skin"),
    ([5.5, 21, 9, 6.5, 23, 11], "cream"),
    ([6.5, 21.25, 9, 7, 22.5, 10.75], "skin"),
    ([6.5, 8, 3, 6.875, 21, 3.375], "wood"),
    ([6, 2.5, 2.25, 7.5, 6, 4.25], "broom"),
    ([6.5, 23, 5.5, 6.75, 23.375, 10.5], "gold"),
    ([6.5, 7.5, 8, 6.875, 10.5, 8.375], "gold"),
    ([6.5, 9, 7.25, 6.875, 9.375, 9.125], "gold"),
    # Cat and dog remain small sculpted figures at the saint's feet.
    ([3.75, 2.5, 1, 6.5, 4, 4], "cat"), ([5.25, 4, 1.5, 7, 5.5, 3.5], "cat"),
    ([5.75, 5.5, 1.5, 6.5, 6, 2.25], "cat"), ([5.75, 5.5, 2.75, 6.5, 6, 3.5], "cat"),
    ([7, 4.5, 2, 7.125, 4.75, 2.5], "hair"),
    ([3.75, 2.5, 12, 6, 6, 14.5], "dog"), ([5.5, 6, 11.75, 7, 7.5, 14.75], "dog"),
    ([5.5, 5.5, 11.5, 6, 7, 12], "wood"), ([5.5, 5.5, 14.5, 6, 7, 15], "wood"),
    ([7, 6.5, 12.5, 7.375, 7, 14], "hair"),
    ([6, 2.5, 6.5, 7.5, 2.875, 9.5], "cream"),
]
for y in (11, 12.5, 14, 15.5, 17, 18.5, 20, 21.5):
    STATUE.append(([6.5, y, 8.125, 6.875, y+.375, 8.5], "wood"))
for z in (3.5, 12.125):
    STATUE.append(([6.5, 7.5, z, 6.625, 18, z+.375], "gold"))


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2)+"\n", encoding="utf-8")


def textures():
    colors = {"partition": (153, 158, 161), "porcelain": (239, 240, 236), "seat": (249, 249, 245),
              "chrome": (174, 185, 187), "drain": (82, 91, 94), "pedestal": (92, 39, 43),
              "grass": (89, 104, 45), "cream": (232, 218, 173), "robe": (17, 24, 31),
              "skin": (109, 73, 47), "face": (109, 73, 47), "hair": (26, 23, 20),
              "gold": (185, 144, 54), "wood": (114, 75, 35), "broom": (191, 156, 77),
              "cat": (184, 179, 152), "dog": (216, 169, 97)}
    for material, base in colors.items():
        rng = random.Random(782)
        im = Image.new("RGB", (16, 16))
        for y in range(16):
            for x in range(16):
                delta = rng.choice((-2, -1, 0, 0, 0, 1, 2))
                if material in ("robe", "cream") and x % 4 == 0: delta -= 3
                if material == "broom" and x % 3 == 0: delta -= 15
                im.putpixel((x, y), tuple(max(0, min(255, c+delta)) for c in base))
        if material == "face":
            draw = ImageDraw.Draw(im)
            draw.rectangle((0, 0, 15, 2), fill=(30, 26, 22))
            draw.line((3, 5, 6, 5), fill=(45, 31, 23)); draw.line((10, 5, 13, 5), fill=(45, 31, 23))
            draw.point((5, 6), fill=(215, 203, 169)); draw.point((11, 6), fill=(215, 203, 169))
            draw.rectangle((7, 7, 8, 10), fill=(131, 88, 54))
            draw.line((6, 12, 10, 12), fill=(67, 42, 32))
        if material == "drain":
            draw = ImageDraw.Draw(im)
            for x in (3, 6, 9, 12): draw.line((x, 2, x, 13), fill=(32, 41, 44))
        path = ASSETS / f"textures/block/school_{material}.png"
        path.parent.mkdir(parents=True, exist_ok=True)
        im.save(path)
        if material == "robe":
            for half in ("up", "down"): im.save(ASSETS / f"textures/block/san_martin_de_porres_{half}.png")


def solid_model(parts, half=None):
    """Clip all hidden/coplanar faces. Each remaining face belongs to one positive-volume box."""
    elements = []
    materials = set()
    faces = {"west": (0, -1), "east": (0, 1), "down": (1, -1), "up": (1, 1), "north": (2, -1), "south": (2, 1)}
    for index, part in enumerate(parts):
        b, default = part[:2]
        overrides = part[2] if len(part) == 3 else {}
        for face, (axis, sign) in faces.items():
            plane = b[axis+3] if sign > 0 else b[axis]
            axes = [a for a in range(3) if a != axis]
            a, c = axes
            masks = []
            for j, other in enumerate(parts):
                if j == index: continue
                o = other[0]
                outward = o[axis] <= plane <= o[axis+3] and (o[axis+3] > plane if sign > 0 else o[axis] < plane)
                same = (o[axis+3] == plane if sign > 0 else o[axis] == plane) and j > index
                if not (outward or same): continue
                rectangle = (max(b[a], o[a]), max(b[c], o[c]), min(b[a+3], o[a+3]), min(b[c+3], o[c+3]))
                if rectangle[0] < rectangle[2] and rectangle[1] < rectangle[3]: masks.append(rectangle)
            us = sorted({b[a], b[a+3]} | {v for m in masks for v in (m[0], m[2])})
            vs = sorted({b[c], b[c+3]} | {v for m in masks for v in (m[1], m[3])})
            # Merge adjacent exposed cells into rectangles, reducing unnecessary quads.
            rectangles, active = [], {}
            for v0, v1 in zip(vs, vs[1:]):
                spans, start = [], None
                for u0, u1 in zip(us, us[1:]):
                    u, v = (u0+u1)/2, (v0+v1)/2
                    covered = any(m[0] < u < m[2] and m[1] < v < m[3] for m in masks)
                    if not covered and start is None: start = u0
                    if covered and start is not None: spans.append((start, u0)); start = None
                if start is not None: spans.append((start, us[-1]))
                next_active = {}
                for span in spans:
                    rectangle = active.pop(span, [span[0], span[1], v0, v1])
                    rectangle[3] = v1
                    next_active[span] = rectangle
                rectangles.extend(active.values())
                active = next_active
            rectangles.extend(active.values())
            for u0, u1, v0, v1 in rectangles:
                box = list(b); box[a], box[a+3], box[c], box[c+3] = u0, u1, v0, v1
                material = overrides.get(face, default); materials.add(material)
                elements.append({"from": box[:3], "to": box[3:], "faces": {
                    face: {"texture": "#"+material, "uv": [0, 0, 16, 16]}}})
    textures = {m: f"lsmmod:block/school_{m}" for m in sorted(materials)}
    if half and "robe" in textures: textures["robe"] = f"lsmmod:block/san_martin_de_porres_{half}"
    textures["particle"] = "lsmmod:block/school_pedestal" if half else "lsmmod:block/school_porcelain"
    return {"parent": "minecraft:block/block", "textures": textures, "elements": elements}


def main():
    textures()
    for half, minimum, maximum in (("down", 0, 16), ("up", 16, 32)):
        value = solid_model(STATUE, half)
        clipped = []
        for e in value["elements"]:
            low, high = e["from"][1], e["to"][1]
            if high <= minimum or low >= maximum: continue
            # Preserve only actual exterior faces; no new caps at the shared y=16 seam.
            if low < minimum: e["faces"].pop("down", None)
            if high > maximum: e["faces"].pop("up", None)
            e["from"][1] = max(minimum, low)-minimum
            e["to"][1] = min(maximum, high)-minimum
            if e["faces"]: clipped.append(e)
        value["elements"] = clipped
        write_json(ASSETS / f"models/block/san_martin_de_porres_{half}.json", value)
    item = solid_model(STATUE)
    for e in item["elements"]:
        e["from"] = [6+e["from"][0]/2, e["from"][1]/2, 4+e["from"][2]/2]
        e["to"] = [6+e["to"][0]/2, e["to"][1]/2, 4+e["to"][2]/2]
    write_json(ASSETS / "models/item/san_martin_de_porres.json", item)
    write_json(ASSETS / "items/san_martin_de_porres.json", {"model": {"type": "minecraft:model", "model": "lsmmod:item/san_martin_de_porres"}})
    java = ["package net.nicomar2009.lsmmod.block;", "", "/** Generated by tools/create_bathroom_statue_assets.py. */", "public final class BathroomShapes {", "    private BathroomShapes() {}"]
    for block_id, parts, es, en in (("toilet", TOILET, "Inodoro con separadores", "Toilet with Partitions"),
                                   ("men_urinal", URINAL, "Urinario de varones", "Men's Urinal")):
        write_json(ASSETS / f"models/block/{block_id}.json", solid_model(parts))
        write_json(ASSETS / f"blockstates/{block_id}.json", {"variants": {
            f"facing={f}": {"model": f"lsmmod:block/{block_id}", "y": angle}
            for f, angle in (("north", 0), ("east", 90), ("south", 180), ("west", 270))}})
        write_json(ASSETS / f"models/item/{block_id}.json", {"parent": f"lsmmod:block/{block_id}"})
        write_json(ASSETS / f"items/{block_id}.json", {"model": {"type": "minecraft:model", "model": f"lsmmod:item/{block_id}"}})
        write_json(DATA / f"lsmmod/loot_table/blocks/{block_id}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [{"type": "minecraft:item", "name": f"lsmmod:{block_id}"}]}]})
        name = "TOILET" if block_id == "toilet" else "URINAL"
        java.append(f"    public static final double[][] {name} = {{")
        java += ["        {"+", ".join(str(v) for v in p[0])+"}," for p in parts]
        java.append("    };")
        for locale, text in (("es_es", es), ("en_us", en)):
            path = ASSETS / f"lang/{locale}.json"; values = json.loads(path.read_text()); values[f"block.lsmmod.{block_id}"] = text; write_json(path, values)
    java.append("}")
    (ROOT / "src/main/java/net/nicomar2009/lsmmod/block/BathroomShapes.java").write_text("\n".join(java)+"\n")
    path = DATA / "minecraft/tags/block/mineable/pickaxe.json"; values = json.loads(path.read_text())
    for name in ("toilet", "men_urinal"):
        if f"lsmmod:{name}" not in values["values"]: values["values"].append(f"lsmmod:{name}")
    write_json(path, values)
    # Keep the corrected two-block fixtures when regenerating the original asset family.
    from create_tall_bathroom_assets import main as rebuild_tall_fixtures
    rebuild_tall_fixtures()


if __name__ == "__main__":
    main()
