"""Crisp 64px pixel dial, all twelve readable numbers; existing OBJ/UV unchanged."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import output_path
import math
from pathlib import Path
from PIL import Image,ImageDraw

DIGITS={
'0':['111','101','101','101','111'],'1':['010','110','010','010','111'],
'2':['111','001','111','100','111'],'3':['111','001','111','001','111'],
'4':['101','101','111','001','001'],'5':['111','100','111','001','111'],
'6':['111','100','111','101','111'],'7':['111','001','010','010','010'],
'8':['111','101','111','101','111'],'9':['111','101','111','001','111']}
def create_clock_face(path):
    image=Image.new('RGB',(64,64),(213,213,203));d=ImageDraw.Draw(image)
    d.ellipse((8,8,56,56),fill=(235,234,219),outline=(147,149,137),width=1)
    for i in range(60):
        a=math.tau*i/60;r=20.5 if i%5==0 else 22
        d.line((round(32+r*math.sin(a)),round(32-r*math.cos(a)),round(32+23*math.sin(a)),round(32-23*math.cos(a))),fill=(66,68,61),width=1)
    for number in range(1,13):
        a=math.tau*number/12;text=str(number);cx=round(32+16*math.sin(a));cy=round(32-16*math.cos(a))
        left=cx-(4*len(text)-1)//2
        for i,ch in enumerate(text):
            for y,row in enumerate(DIGITS[ch]):
                for x,pixel in enumerate(row):
                    if pixel=='1':d.point((left+i*4+x,cy-2+y),fill=(43,45,41))
    d.line((32,32,23,23),fill=(38,40,36),width=3)
    d.line((32,32,45,19),fill=(38,40,36),width=2)
    d.line((32,34,35,45),fill=(126,66,54),width=1)
    d.ellipse((30,30,34,34),fill=(33,35,31));d.point((31,31),fill=(157,161,149))
    path=Path(path);path.parent.mkdir(parents=True,exist_ok=True);image.save(output_path(path))
if __name__=='__main__':
    create_clock_face(Path(__file__).resolve().parents[2]/'src/main/resources/assets/lsmmod/textures/block/school_clock_face.png')
