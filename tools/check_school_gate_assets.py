"""Check gate resources and the complete closed/open geometry without compiling Java."""
import itertools
import json
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def rotate(x, z, degrees):
    return {0: (x, z), 90: (16-z, x), 180: (16-x, 16-z), 270: (z, 16-x)}[degrees]


def main():
    variants = read(ASSETS / "blockstates/school_gate.json")["variants"]
    assert len(variants) == 360
    for key, variant in variants.items():
        state = dict(piece.split("=") for piece in key.split(","))
        column, row, depth = (int(state[p]) for p in ("column", "row", "depth"))
        opened = state["open"] == "true"
        model = read(ASSETS / "models" / (variant["model"].split(":")[1] + ".json"))
        visible = (opened and column in (0, 4)) or (not opened and depth == 0)
        assert bool(model["elements"]) == visible, key
        for element in model["elements"]:
            assert all(0 <= a < b <= 16 for a, b in zip(element["from"], element["to"])), key
            for face in element["faces"].values():
                texture_id = model["textures"][face["texture"][1:]]
                image = Image.open(ASSETS / "textures" / (texture_id.split(":")[1] + ".png"))
                assert image.size == (16, 16)
                assert all(0 <= v <= 16 for v in face["uv"])
        if not visible:
            continue
        # Model bounds must match the selection/collision envelope specified by the block.
        low = [min(e["from"][axis] for e in model["elements"]) for axis in range(3)]
        high = [max(e["to"][axis] for e in model["elements"]) for axis in range(3)]
        expected = ([0 if column == 0 else 14, 0, 8 if depth == 0 else 0],
                    [2 if column == 0 else 16, 16, 16]) if opened else ([0, 0, 7], [16, 16, 9])
        assert (low, high) == expected, key
    for opened in (False, True):
        for rotation in (0, 90, 180, 270):
            vertices = []
            for column, row, depth in itertools.product(range(5), range(3), range(3)):
                if depth and (not opened or column not in (0, 4)):
                    continue
                if opened and column not in (0, 4):
                    continue
                name = f"school_gate_open_{column}_{row}_{depth}" if opened else f"school_gate_closed_{column}_{row}"
                model = read(ASSETS / f"models/block/{name}.json")
                # Combine cells before rotating: this also checks tile/model orientation consistency.
                for element in model["elements"]:
                    for x, y, z in itertools.product(*zip(element["from"], element["to"])):
                        x, z = rotate(x+16*column, z+16*depth, rotation)
                        vertices.append((x, y+16*row, z))
            spans = [max(v[a] for v in vertices)-min(v[a] for v in vertices) for a in range(3)]
            expected = [80, 48, 40] if opened else [80, 48, 2]
            if rotation in (90, 270):
                expected = [expected[2], expected[1], expected[0]]
            assert spans == expected
    # Verify that opening preserves each leaf's artwork, including partial end cells.
    for row in range(3):
        original = Image.new("RGBA", (80, 16))
        for column in range(5):
            original.paste(Image.open(ASSETS / f"textures/block/school_gate_closed_{column}_{row}.png"), (16*column, 0))
        for column in (0, 4):
            for depth, (offset, length) in enumerate(((0, 8), (8, 16), (24, 16))):
                strip = Image.open(ASSETS / f"textures/block/school_gate_open_{column}_{row}_{depth}.png")
                start = 8 if depth == 0 else 0
                for u in range(length):
                    x = offset+u if column == 0 else 79-offset-u
                    for y in range(16):
                        assert strip.getpixel((start+u, y)) == original.getpixel((x, y))
    loot = read(ROOT / "src/main/resources/data/lsmmod/loot_table/blocks/school_gate.json")
    conditions = loot["pools"][0]["conditions"]
    controller = next(c for c in conditions if c["condition"] == "minecraft:block_state_property")
    assert controller["properties"] == {"column": "2", "row": "0", "depth": "0"}
    floor = Image.open(ASSETS / "textures/block/classroom_floor.png")
    assert floor.size == (16, 16)
    # Four tile interiors divided by grout only at x/y = 0 and 8.
    for y in range(16):
        for x in range(16):
            grout = floor.getpixel((x, y))[0] < 180
            assert grout == (x in (0, 8) or y in (0, 8))
    print("OK: 360 states, 16x16 textures, 5x3 geometry and two open leaves in four directions,")
    print("preserved leaf artwork, one controller drop, and floor with four tiles. No Java compilation.")


if __name__ == "__main__":
    main()
