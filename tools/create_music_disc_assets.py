"""Generate seven school record labels using the existing 16px vinyl silhouette."""
from resource_modules import module_path, module_write_text, output_path
from PIL import Image, ImageColor
from resource_json import ROOT, resource_json

ASSETS = ROOT / 'src/main/resources/music_discs/assets/lsmmod'
# Label pixels are deliberately restricted to the existing printed center.
LABEL = ((6, 6), (7, 6), (8, 6), (5, 7), (6, 7), (8, 7), (9, 7), (6, 8), (7, 8), (8, 8))
# Each label has both its own palette and a different motif, not only a tint.
DESIGNS = {
    'qapackachampa': ('#af7724', '#ffdb63', '#f3f0cc', 'ABABABBABA'),
    'cofradia_san_miguel': ('#53419a', '#b9a1ef', '#f2d878', 'ACAACAAACA'),
    'carnaval_huaccana': ('#176a75', '#66dcd1', '#fae4aa', 'ABBACBCABB'),
    'carnaval_huanupampa': ('#1c713d', '#83d05d', '#f4e38f', 'ABCABACBCA'),
    'carnaval_macari': ('#a63358', '#f18fbc', '#ffeac1', 'ABCCABACBA'),
    'formacion': ('#25598f', '#6ab7ed', '#f5da85', 'ACACCCCAAC'),
    'himnoperu': ('#b92532', '#f2efdf', '#e7b749', 'ABAABBABAA'),
}


def main():
    original = Image.open(module_path(ASSETS / 'textures/item/lsm_anthem.png')).convert('RGBA')
    for ident, (*colors, motif) in DESIGNS.items():
        image = original.copy()
        palette = dict(zip('ABC', colors))
        for xy, code in zip(LABEL, motif):
            image.putpixel(xy, ImageColor.getrgb(palette[code]) + (255,))
        image.save(output_path(ASSETS / f'textures/item/{ident}.png'))
        resources = {
            f'models/item/{ident}.json': {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'lsmmod:item/{ident}'}},
            f'items/{ident}.json': {'model': {'type': 'minecraft:model', 'model': f'lsmmod:item/{ident}'}},
        }
        for filename, data in resources.items():
            path = ASSETS / filename
            module_write_text(path, resource_json(path, data) + '\n', encoding='utf-8')
    print('Generated seven unique labels, models and item definitions.')


if __name__ == '__main__':
    main()
