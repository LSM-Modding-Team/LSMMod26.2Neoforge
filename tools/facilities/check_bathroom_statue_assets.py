"""Check statue z-fighting regressions and sanitary fixture resources without Java compilation."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_path, module_read_text
import json
import re
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
NORMALS = {"west": (0, False), "east": (0, True), "down": (1, False),
           "up": (1, True), "north": (2, False), "south": (2, True)}


def read(path):
    return json.loads(module_read_text(path, encoding="utf-8"))


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
            image = Image.open(module_path(ASSETS / f"textures/{texture}.png"))
            expected_size = (16, 32) if texture in (
                'block/san_martin_de_porres_habit', 'block/san_martin_de_porres_cream') else (16, 16)
            assert image.size == expected_size
            assert len(face['uv']) == 4 and all(0 <= v <= 16 for v in face['uv']), (name, face)
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
    from render_texture_review import add, block, load, render
    import numpy as np
    for layer in load('lsmmod:item/san_martin_de_porres'):
        mesh(layer, 'display item '+layer['render_type'])
    definition = read(ASSETS/'blockstates/san_martin_de_porres.json')['multipart']
    assembled = []
    for row in range(3):
        for column in range(2):
            selected = [p['apply'] for p in definition if p['when'] == {
                'expanded': 'true', 'facing': 'north', 'column': str(column), 'row': str(row)}]
            assert len(selected) == 2
            for apply in selected:
                model = read(ASSETS/f"models/{apply['model'].split(':')[1]}.json")
                mesh(model, apply['model'])
                assert all(e['to'][1] <= (8 if row == 2 else 16) for e in model['elements'])
                add(assembled, [model], (column*16, row*16, 0))
    complete = []
    from special_blocks.create_san_martin_display import parts, model
    for layer, geometry in zip(('solid', 'translucent'), parts()):
        add(complete, [model(geometry, layer)])
    # Compare the rendered appearance, not the clipping implementation. This catches
    # reversed U, restarted folds, glass alpha changes and artificial seam caps.
    for azimuth in (-90, -55, -135, 60, 135):
        kwargs = dict(size=(320, 440), azimuth=azimuth, elevation=12)
        # Non-integral zoom avoids samples exactly on triangle/texel edges.
        expected = np.array(render(complete, [16, 20, 8], 8.73, **kwargs)).astype(int)
        actual = np.array(render(assembled, [16, 20, 8], 8.73, **kwargs)).astype(int)
        assert np.max(np.abs(actual-expected)) <= 1, ('complete/cell appearance differs', azimuth)
    from facilities.check_wide_bathroom_assets import main as check_wide
    check_wide()
    print("OK: legacy statue meshes without coplanar overlaps, clean two-half seam;")
    print("six display cells visually match the complete model from five angles, continuous UVs;")
    print("layered item, transparent glass, top row only half a block;")
    print("complete two-wide fixture resources, floor-contact geometry and passive standalone toilet.")
    print("Static checks only: no Java compilation or Minecraft rendering.")


if __name__ == "__main__":
    main()
