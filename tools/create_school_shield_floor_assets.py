"""Retexture only the shield's background with classroom floor, preserving its exact pixels."""
import base64
import io
import json
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/lsmmod"
BLOCKS = ASSETS / "textures/block"
EDITABLE = ROOT / "newresources/escudo/bbmodels"
FOREGROUND = {(255, 255, 255, 255), (112, 21, 36, 255), (232, 177, 23, 255), (0, 0, 0, 255)}
LAYOUT = ((6, 4, 7), (3, 1, 2), (8, 5, 9))


def embedded_png(image):
    data = io.BytesIO()
    image.save(data, format="PNG")
    return "data:image/png;base64," + base64.b64encode(data.getvalue()).decode("ascii")


def main():
    floor = Image.open(BLOCKS / "classroom_floor.png").convert("RGBA")
    assert floor.size == (16, 16)
    # Shield top faces retain their original UV rotation=180. Pre-rotate the background
    # so their rendered grout lands on the same pixel rows as the surrounding floor.
    background = floor.transpose(Image.Transpose.ROTATE_180)
    tiles = {}
    for number in range(1, 10):
        path = BLOCKS / f"lsm{number}.png"
        original = Image.open(path).convert("RGBA")
        assert original.size == (16, 16)
        result = background.copy()
        for y in range(16):
            for x in range(16):
                pixel = original.getpixel((x, y))
                if pixel in FOREGROUND:
                    result.putpixel((x, y), pixel)
        result.save(path)
        tiles[number] = result
        # Keep the supplied Blockbench projects usable without restoring the old material.
        editable = EDITABLE / f"lsm{number}.bbmodel"
        data = json.loads(editable.read_text(encoding="utf-8"))
        for texture in data["textures"]:
            if texture["id"] == "0":
                texture.update(name="classroom_floor.png", folder="block", namespace="lsmmod",
                               source=embedded_png(floor), saved=False)
                texture.pop("path", None)
                texture.pop("relative_path", None)
            elif texture["id"] == "1":
                texture.update(source=embedded_png(result), namespace="lsmmod", saved=False)
        editable.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    # Preserve the existing item layout and emblem orientation; its pixels match the PNGs.
    item = Image.new("RGBA", (48, 48))
    for row, numbers in enumerate(LAYOUT):
        for column, number in enumerate(numbers):
            item.paste(tiles[number], (16*column, 16*row))
    item.save(ASSETS / "textures/item/school_shield.png")


if __name__ == "__main__":
    main()
