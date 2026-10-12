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
            assert image.size == (16, 16)
            if model.get('render_type') == 'minecraft:translucent':
                assert image.mode == 'RGBA' and image.getextrema()[3][1] < 255
            else:
                assert image.mode == 'RGB'
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
    from render_texture_review import load
    for layer in load('lsmmod:item/san_martin_de_porres'):
        mesh(layer, 'display item '+layer['render_type'])
    definition = read(ASSETS/'blockstates/san_martin_de_porres.json')['multipart']
    for row in range(3):
        for column in range(2):
            selected = [p['apply'] for p in definition if p['when'] == {
                'expanded': 'true', 'facing': 'north', 'column': str(column), 'row': str(row)}]
            assert len(selected) == 2
            for apply in selected:
                model = read(ASSETS/f"models/{apply['model'].split(':')[1]}.json")
                mesh(model, apply['model'])
                assert all(e['to'][1] <= (8 if row == 2 else 16) for e in model['elements'])
    from check_wide_bathroom_assets import main as check_wide
    check_wide()
    print("OK: legacy statue meshes without coplanar overlaps, clean two-half seam;")
    print("six display cells and layered item, transparent glass, top row only half a block;")
    print("complete two-wide fixture resources, floor-contact geometry and passive standalone toilet.")
    print("Static checks only: no Java compilation or Minecraft rendering.")


if __name__ == "__main__":
    main()
