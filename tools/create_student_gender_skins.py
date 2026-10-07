"""Generate gender variants from the existing student atlases, preserving every outfit texel.

Female atlases reserve UV (16,32) for the exact inclined bust in girl.java.
All other uniform regions retain the existing player UV layout.
"""
import argparse
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
TEXTURES = ROOT / 'src/main/resources/assets/lsmmod/textures/entity'
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--font', default='/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf')
parser.add_argument('--female-only', action='store_true', help='Only update the eleven classroom female skins')
ARGS = parser.parse_args()
FONT = ARGS.font


def mark_gender(source, gender, primary):
    image = source.copy().convert('RGBA')
    scale = image.width // 64
    draw = ImageDraw.Draw(image)
    skin = '#ffffff' if primary else '#000000'
    ink = '#000000' if primary else '#ffffff'
    # Vanilla head top: [8,0,16,8] in a 64-unit atlas.
    draw.rectangle((8 * scale, 0, 16 * scale - 1, 8 * scale - 1), fill=skin)
    font = ImageFont.truetype(FONT, round(6.5 * scale))
    label = 'F' if gender == 'female' else 'M'
    x0, y0, x1, y1 = font.getbbox(label)
    draw.text((12 * scale - (x1 - x0) / 2 - x0,
               4 * scale - (y1 - y0) / 2 - y0), label, fill=ink, font=font)
    return image


def adapt_female_bust(image):
    # New girl.java bust: UV (16,32), 8x3x4 cube. The original body and limbs retain their UVs.
    # Each face samples the corresponding existing uniform panel, including placket/badge/Mc.
    scale = image.width // 64
    source = image.copy()
    faces = [
        ((20, 32, 28, 36), (20, 20, 28, 24)),  # upper uniform / top
        ((28, 32, 36, 36), (20, 26, 28, 30)),  # lower uniform / bottom
        ((16, 36, 20, 39), (16, 23, 20, 26)),  # right side
        ((20, 36, 28, 39), (20, 23, 28, 26)),  # front
        ((28, 36, 32, 39), (28, 23, 32, 26)),  # left side
        ((32, 36, 40, 39), (32, 23, 40, 26)),  # back (inside torso)
    ]
    for target, original in faces:
        target = tuple(v * scale for v in target)
        original = tuple(v * scale for v in original)
        image.paste(source.crop(original), target)
    return image


for suffix, count in [('p', 6), ('s', 5)]:
    for grade in range(1, count + 1):
        male_path = TEXTURES / 'students' / f'placeholder_{grade}{suffix}.png'
        with Image.open(male_path) as original:
            for gender in (['female'] if ARGS.female_only else ['male', 'female']):
                output = male_path if gender == 'male' else male_path.with_stem(male_path.stem + '_female')
                variant = mark_gender(original, gender, suffix == 'p')
                if gender == 'female':
                    variant = adapt_female_bust(variant)
                variant.save(output)

if not ARGS.female_only:
    with Image.open(TEXTURES / 'placeholder_school_npc.png') as original:
        for gender in ['male', 'female']:
            mark_gender(original, gender, True).save(TEXTURES / f'placeholder_student_{gender}.png')
print('Generated 11 classroom female atlases for girl.java.' if ARGS.female_only else
      'Generated 11 female classroom variants, marked 11 male head tops, and 2 unassigned Student fallbacks.')
