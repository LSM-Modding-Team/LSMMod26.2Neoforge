"""Generate the eleven numbered classroom egg icons from the shared palette manifest."""
import argparse,json
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1]
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('--font',default='/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf')
font=ImageFont.truetype(parser.parse_args().font,36)
for entry in json.loads((ROOT/'tools/student_classroom_eggs.json').read_text()):
 im=Image.new('RGBA',(64,64));d=ImageDraw.Draw(im)
 outline=[(27,3),(37,3),(45,9),(51,21),(55,38),(52,51),(44,59),(20,59),(12,51),(9,38),(13,21),(19,9)]
 d.polygon(outline,fill=entry['background'])
 d.line(outline+[outline[0]],fill=entry['numeral'],width=2)
 # Small shell marks leave the central number unobstructed.
 for x,y in [(22,13),(42,15),(16,38),(46,46)]:d.rectangle((x,y,x+2,y+2),fill=entry['numeral'])
 label=str(entry['grade']);b=font.getbbox(label)
 d.text(((64-(b[2]-b[0]))//2-b[0],(64-(b[3]-b[1]))//2-b[1]+2),label,font=font,fill=entry['numeral'])
 path=ROOT/'src/main/resources/assets/lsmmod/textures/item'/f'{entry["id"]}.png'
 im.save(path)
print('Generated 11 classroom egg icons, 64x64 RGBA.')
