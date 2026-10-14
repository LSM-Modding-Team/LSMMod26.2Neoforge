"""Review the saved goal cells and actual inventory model; no Minecraft launch."""
import json

from PIL import Image, ImageDraw

from create_sports_goal_assets import ASSETS, ID, coordinates
from render_block_review import framing
import render_texture_review as renderer
from resource_json import preview_path


def main():
    scene = []
    for cell in range(162):
        column, row, depth = coordinates(cell)
        renderer.add(scene, renderer.block(ID, {"facing": "north", "cell": cell}),
                     (16 * column, 16 * row, 16 * depth))
    definition = json.loads((ASSETS / f"items/{ID}.json").read_text())["model"]
    item = []
    renderer.add(item, renderer.load(definition["model"]))
    size = (600, 540)
    center, scale = framing(scene, size)
    item_center, item_scale = framing(item, size)
    views = [("Frente", scene, center, scale, -90),
             ("Frente y lateral", scene, center, scale, -55),
             ("Lateral", scene, center, scale, 0),
             ("Dorso y lateral", scene, center, scale, 120),
             ("Dorso", scene, center, scale, 90),
             ("Item / modelo real", item, item_center, item_scale, -55)]
    canvas = Image.new("RGB", (1800, 1140), (52, 66, 72))
    draw = ImageDraw.Draw(canvas)
    for index, (label, geometry, origin, zoom, azimuth) in enumerate(views):
        x, y = index % 3 * 600, index // 3 * 565
        rendered = renderer.render(geometry, origin, zoom*2, size=(size[0]*2,size[1]*2),
                                   azimuth=azimuth, elevation=10, background=(52,66,72))
        canvas.paste(rendered.resize(size, Image.Resampling.LANCZOS), (x, y + 25))
        draw.text((x + 12, y + 8), label, fill=(232, 236, 239))
    draw.text((12, 1128), "Render externo; no es una captura de Minecraft.", fill=(232, 236, 239))
    output = preview_path(f"{ID}_review.png")
    canvas.save(output)
    print(output)


if __name__ == "__main__":
    main()
