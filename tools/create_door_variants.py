"""Regenerate registered door assets and previews from the existing classroom door."""
import json
from pathlib import Path
import numpy as np
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'src/main/resources/assets/lsmmod'
DEST = SOURCE

def save_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + '\n')

def original():
    result = Image.new('RGBA', (16, 32))
    for part, y in [('top', 0), ('bottom', 16)]:
        result.paste(Image.open(SOURCE / f'textures/block/classroom_door_{part}.png'), (0, y))
    return result

def solid_wood():
    im = original()
    source = im.copy()
    # Restore only glass/frame texels, using matching columns from clean wood
    # below. This preserves the darker vertical grain and all surrounding texels.
    for y in range(4, 12):
        for x in range(4, 12):
            r, g, b, alpha = source.getpixel((x, y))
            if alpha == 0 or r >= 160 or g >= 100:
                im.putpixel((x, y), source.getpixel((x, y + 16)))
    return im

def dining_texture():
    im = solid_wood()
    d = ImageDraw.Draw(im)
    d.rectangle((4, 3, 11, 26), fill=(176, 181, 174, 255))
    d.line((11, 4, 11, 26), fill=(143, 152, 145, 255))
    d.line((5, 26, 11, 26), fill=(143, 152, 145, 255))
    d.rectangle((5, 4, 10, 25), fill=(0, 0, 0, 0))
    # Sparse cutout reflections match the classroom door's clear glass treatment.
    for x, y, color in [(5, 5, (166, 198, 205, 255)),
                         (6, 6, (199, 223, 226, 255)),
                         (7, 6, (199, 223, 226, 255)),
                         (8, 7, (199, 223, 226, 255)),
                         (9, 8, (166, 198, 205, 255)),
                         (10, 9, (166, 198, 205, 255)),
                         (5, 20, (123, 163, 175, 255)),
                         (6, 21, (199, 223, 226, 255))]:
        im.putpixel((x, y), color)
    return im

def teachers_texture():
    im = solid_wood().resize((128, 256), Image.Resampling.NEAREST)
    d = ImageDraw.Draw(im)
    d.rectangle((25, 26, 103, 69), fill=(98, 39, 21, 255))
    d.rectangle((24, 24, 102, 67), fill=(248, 245, 233, 255))
    d.rectangle((28, 28, 98, 63), fill=(0, 204, 204, 255))
    font = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 9)
    for text, y in [('SALA DE', 30), ('PROFESORES', 46)]:
        d.text((63, y), text, anchor='mt', font=font, fill=(255, 255, 255, 255))
    return im

def write_assets(name, full):
    width, height = full.size
    for part, y in [('top', 0), ('bottom', height // 2)]:
        path = DEST / f'textures/block/{name}_{part}.png'
        path.parent.mkdir(parents=True, exist_ok=True)
        full.crop((0, y, width, y + height // 2)).save(path)
    for path in sorted((SOURCE / 'models/block').glob('classroom_door_*.json')):
        data = json.loads(path.read_text())
        data['textures'] = {key: value.replace('classroom_door', name)
                            for key, value in data['textures'].items()}
        save_json(DEST / 'models/block' / path.name.replace('classroom_door', name), data)
    data = json.loads((SOURCE / 'blockstates/classroom_door.json').read_text())
    for variant in data['variants'].values():
        variant['model'] = variant['model'].replace('classroom_door', name)
    save_json(DEST / f'blockstates/{name}.json', data)
    save_json(DEST / f'models/item/{name}.json', {
        'parent': 'minecraft:item/generated', 'textures': {'layer0': f'lsmmod:item/{name}'}})
    save_json(DEST / f'items/{name}.json', {
        'model': {'type': 'minecraft:model', 'model': f'lsmmod:item/{name}'}})
    path = DEST / f'textures/item/{name}.png'
    path.parent.mkdir(parents=True, exist_ok=True)
    icon = Image.new('RGBA', (width * 2, height))
    icon.paste(full, (width // 2, 0))
    icon.save(path)

def preview(full, width=440, height=760):
    # Orthographic textured preview of the same 16 x 32 x 3 vanilla door slab.
    canvas = np.full((height, width, 3), (235, 239, 242), dtype=np.uint8)
    texture = np.array(full)
    scale = 19
    origin = np.array([65., 665.])
    px = np.array([scale, 0.7])
    py = np.array([0., -scale])
    pz = np.array([9., -5.])
    def project(x, y, z):
        return origin + px*x + py*y + pz*z
    def face(points, uv, shade):
        points = np.array(points)
        uv = np.array(uv)
        for indices in [(0, 1, 2), (0, 2, 3)]:
            tri = points[list(indices)]
            tex = uv[list(indices)]
            lo = np.maximum(np.floor(tri.min(axis=0)).astype(int), 0)
            hi = np.minimum(np.ceil(tri.max(axis=0)).astype(int), [width-1, height-1])
            xx, yy = np.meshgrid(np.arange(lo[0], hi[0]+1)+.5, np.arange(lo[1], hi[1]+1)+.5)
            a, b, c = tri
            den = (b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
            u = ((b[1]-c[1])*(xx-c[0])+(c[0]-b[0])*(yy-c[1]))/den
            v = ((c[1]-a[1])*(xx-c[0])+(a[0]-c[0])*(yy-c[1]))/den
            w = 1-u-v
            coords = u[...,None]*tex[0]+v[...,None]*tex[1]+w[...,None]*tex[2]
            tx = np.clip((coords[...,0]*full.width).astype(int), 0, full.width-1)
            ty = np.clip((coords[...,1]*full.height).astype(int), 0, full.height-1)
            rgba = texture[ty, tx]
            mask = (u >= 0)&(v >= 0)&(w >= 0)&(rgba[...,3] > 0)
            target = canvas[lo[1]:hi[1]+1, lo[0]:hi[0]+1]
            target[mask] = np.clip(rgba[...,:3][mask]*shade, 0, 255).astype(np.uint8)
    face([project(16,0,0),project(16,32,0),project(16,32,3),project(16,0,3)],
         [(0,1),(0,0),(.1875,0),(.1875,1)], .68)
    face([project(0,32,0),project(0,32,3),project(16,32,3),project(16,32,0)],
         [(0,0),(0,.09375),(1,.09375),(1,0)], 1.08)
    face([project(0,0,0),project(0,32,0),project(16,32,0),project(16,0,0)],
         [(0,1),(0,0),(1,0),(1,1)], 1.)
    return Image.fromarray(canvas)

def create_bathroom():
    texture = solid_wood()
    write_assets('bathroom_door', texture)
    rendered = preview(texture)
    font = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 20)
    ImageDraw.Draw(rendered).text((220, 710), 'BAÑOS', anchor='mt', font=font, fill=(39, 49, 58))
    rendered.save(ROOT / 'newresources/bathroom_door_render.png')

def main():
    variants = [('dining_door', 'COMEDOR', dining_texture()),
                ('teachers_office_door', 'SALA DE PROFESORES', teachers_texture())]
    sheet = Image.new('RGB', (880, 760), (235, 239, 242))
    font = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 20)
    for i, (name, title, texture) in enumerate(variants):
        write_assets(name, texture)
        rendered = preview(texture)
        ImageDraw.Draw(rendered).text((220, 710), title, anchor='mt', font=font, fill=(39, 49, 58))
        rendered.save(ROOT / f'newresources/{name}_render.png')
        sheet.paste(rendered, (i*440, 0))
    sheet.save(ROOT / 'newresources/door_variants_render.png')
    create_bathroom()

if __name__ == '__main__':
    main()
