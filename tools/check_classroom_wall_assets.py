"""Static resource/geometry checks for the four wall decorations. Never compiles Java."""
import itertools
import json
import re
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
IDS = ("projector_screen", "wall_projector", "classroom_timetable", "emergency_backpack")


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def rotate(x, z, angle):
    return {0: (x, z), 90: (16-z, x), 180: (16-x, 16-z), 270: (z, 16-x)}[angle]


def main():
    for block_id in IDS:
        model = read(ASSETS / f"models/block/{block_id}.json")
        for element in model["elements"]:
            assert all(0 <= lo < hi <= 16 for lo, hi in zip(element["from"], element["to"]))
            for face in element["faces"].values():
                assert all(0 <= v <= 16 for v in face["uv"])
                texture = model["textures"][face["texture"][1:]].split(":")[1]
                Image.open(ASSETS / f"textures/{texture}.png").verify()
        variants = read(ASSETS / f"blockstates/{block_id}.json")["variants"]
        assert len(variants) == (48 if block_id == "projector_screen" else 8 if block_id == "wall_projector" else 4)
        for key, variant in variants.items():
            state = dict(part.split("=") for part in key.split(","))
            assert variant["y"] == {"north": 0, "east": 90, "south": 180, "west": 270}[state["facing"]]
            assert variant["model"] == (f"lsmmod:block/{block_id}_{state['column']}" if block_id=="wall_projector" else f"lsmmod:block/{block_id}")
        read(ASSETS / f"models/item/{block_id}.json")
        assert read(ASSETS / f"items/{block_id}.json")["model"]["model"] == f"lsmmod:item/{block_id}"
        for language in ("en_us", "es_es"):
            assert f"block.lsmmod.{block_id}" in read(ASSETS / f"lang/{language}.json")
        loot = read(ROOT / f"src/main/resources/data/lsmmod/loot_table/blocks/{block_id}.json")
        assert loot["pools"][0]["entries"][0]["name"] == f"lsmmod:{block_id}"
    white = Image.open(ASSETS / "textures/block/projector_screen.png").convert("RGB")
    assert white.size == (16, 16) and white.tobytes() == bytes([255]) * (16*16*3)
    # The twelve tiles touch the supporting wall, cover exactly 64x48 pixels,
    # and remain one pixel thick in every direction without overlapping each other.
    for angle in (0, 90, 180, 270):
        vertices = []
        for column, row in itertools.product(range(4), range(3)):
            for x, y, z in itertools.product((column*16, (column+1)*16), (row*16, (row+1)*16), (15, 16)):
                x, z = rotate(x, z, angle)
                vertices.append((x, y, z))
        spans = [max(p[a] for p in vertices)-min(p[a] for p in vertices) for a in range(3)]
        assert spans == ([64, 48, 1] if angle in (0, 180) else [1, 48, 64])
    screen_loot = read(ROOT / "src/main/resources/data/lsmmod/loot_table/blocks/projector_screen.json")
    assert next(c for c in screen_loot["pools"][0]["conditions"] if c["condition"] == "minecraft:block_state_property")["properties"] == {"column": "0", "row": "0"}
    # Check source/model agreement to catch invisible collision pieces and mismatched rotations.
    geometry = (ROOT / "src/main/java/net/nicomar2009/lsmmod/block/SchoolDecorationShapes.java").read_text()
    for name, block_id in (("PROJECTOR", "wall_projector"), ("TIMETABLE", "classroom_timetable"), ("BACKPACK", "emergency_backpack")):
        section = re.search(name+r" = \{(.*?)\n    \};", geometry, re.S).group(1)
        boxes = [[float(v) for v in group.split(",")] for group in re.findall(r"\{([^{}]+)\}", section)]
        models = read(ASSETS / f"models/block/{block_id}.json")["elements"]
        assert boxes == [e["from"]+e["to"] for e in models]
        for angle in (0, 90, 180, 270):
            for b in boxes:
                points = [rotate(x, z, angle) for x, z in itertools.product((b[0], b[3]), (b[2], b[5]))]
                assert all(0 <= v <= 16 for p in points for v in p)
    # The projector is translated, not enlarged; splitting preserves each solid's volume.
    from create_classroom_wall_assets import PROJECTOR
    cells=[read(ASSETS/f"models/block/wall_projector_{c}.json") for c in range(2)]
    volume=lambda lo,hi: (hi[0]-lo[0])*(hi[1]-lo[1])*(hi[2]-lo[2])
    assert abs(sum(volume(e["from"],e["to"]) for m in cells for e in m["elements"])
               -sum(volume(b[:3],b[3:]) for b,_,_ in PROJECTOR))<1e-9
    points=[]
    for column,m in enumerate(cells):
        section=re.search(r"PROJECTOR_CELLS = \{(.*?)\n    \};",geometry,re.S).group(1)
        groups=re.findall(r"\{([^{}]+)\}",section)
        all_boxes=[[float(v) for v in g.split(',')] for g in groups]
        for e in m["elements"]:
            assert e["from"]+e["to"] in all_boxes
            assert all(0<=lo<hi<=16 for lo,hi in zip(e["from"],e["to"]))
            for face,uv in e['faces'].items():
                assert all(0<=v<=16 for v in uv['uv'])
                # No new cap across the cell seam.
                assert not (face=='east' and column==0 and e['to'][0]==16)
                assert not (face=='west' and column==1 and e['from'][0]==0)
            points.extend((x+16*column,y,z) for x,y,z in itertools.product(*zip(e['from'],e['to'])))
    assert all_boxes==[e['from']+e['to'] for m in cells for e in m['elements']]
    for angle in (0,90,180,270):
        for column,m in enumerate(cells):
            for e in m['elements']:
                assert all(0<=v<=16 for x,z in itertools.product((e['from'][0],e['to'][0]),(e['from'][2],e['to'][2])) for v in rotate(x,z,angle))
    assert min(p[0] for p in points)==9 and max(p[0] for p in points)==23
    assert (min(p[0] for p in points)+max(p[0] for p in points))/2==16
    loot=read(ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/wall_projector.json')
    assert next(c for c in loot['pools'][0]['conditions'] if c['condition']=='minecraft:block_state_property')['properties']=={'column':'0'}
    source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/WallProjectorBlock.java').read_text()
    assert 'SimpleBlockOutline.forState' in source and 'PROJECTOR_CELLS[column]' in source
    assert 'column < 2' in source and 'column < 4' not in source
    assert 'supported(level, target, facing)' in source and 'Block.UPDATE_SUPPRESS_DROPS' in source
    screen=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/ProjectorScreenBlock.java').read_text()
    assert 'IntegerProperty.create("row", 0, 2)' in screen and 'row < 2' not in screen
    timetable = read(ASSETS / "models/block/classroom_timetable.json")["elements"][0]
    assert [hi-lo for lo, hi in zip(timetable["from"], timetable["to"])] == [14, 7, 1]
    print("OK: four complete resource families, pure white screen 64x48x1 px in four directions,")
    print("single screen/projector drops, unchanged projector pixel dimensions, sub-block timetable, and collision/model agreement for all decorations.")
    print("Static verification only; no Java compilation or Minecraft execution.")


if __name__ == "__main__":
    main()
