"""Generate six metre-ruler item models, engraved pixel atlases and vanilla-tier recipes."""
from pathlib import Path
import json
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/lsmmod'
TIERS=json.loads((ROOT/'tools/ruler_tiers.json').read_text())
PALETTES={'wooden':((191,145,77),(41,31,22)), 'stone':((142,145,147),(30,32,34)), 'golden':((232,188,61),(67,46,16)), 'iron':((206,214,219),(36,44,52)), 'diamond':((66,205,193),(15,56,67)), 'netherite':((73,65,70),(218,207,180))}
MATERIALS={'wooden':'#minecraft:planks','stone':'#minecraft:stone_tool_materials','golden':'minecraft:gold_ingot','iron':'minecraft:iron_ingot','diamond':'minecraft:diamond'}
def write(path,value):
 path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(value,indent=2)+'\n')
def cube(a,b,uv):
 return {'from':a,'to':b,'faces':{f:{'texture':'#atlas','uv':uv} for f in ['north','south','east','west','up','down']}}
DISPLAY={
 'gui':{'rotation':[12,-20,-35],'scale':[.43,.43,.43]},
 'ground':{'translation':[0,4,0],'scale':[.35,.35,.35]},
 'fixed':{'rotation':[0,180,0],'scale':[.43,.43,.43]},
 'thirdperson_righthand':{'rotation':[0,-90,0],'translation':[0,1,0],'scale':[.5,.5,.5]},
 'thirdperson_lefthand':{'rotation':[0,90,0],'translation':[0,1,0],'scale':[.5,.5,.5]},
 'firstperson_righthand':{'rotation':[0,-75,-15],'translation':[1,2,0],'scale':[.55,.55,.55]},
 'firstperson_lefthand':{'rotation':[0,75,15],'translation':[1,2,0],'scale':[.55,.55,.55]}}
for name,material,durability,damage,enchantability in TIERS:
 base,ink=PALETTES[name]
 image=Image.new('RGB',(128,128));draw=ImageDraw.Draw(image)
 for y in range(128):
  for x in range(128):
   # Wood has long grain; metal/mineral finishes remain pixelated and quiet.
   delta=((x//2*7+y//8*3)%7-3) if name=='wooden' else ((x*3+y//3)%5-2)
   image.putpixel((x,y),tuple(max(0,min(255,c+delta)) for c in base))
 # Front region x=0..31, reverse x=32..63. 0–100 graduation, both long edges.
 for n in range(101):
  y=2+round(n*123/100)
  length=8 if n%10==0 else 5 if n%5==0 else 3
  for offset in (0,32):
   draw.line((offset,y,offset+length,y),fill=ink)
   draw.line((offset+31-length,y,offset+31,y),fill=ink)
   if n%10==0:
    label=str(n);draw.text((offset+10,min(119,y)),label,font=ImageFont.load_default(size=8),fill=ink)
 # Unmarked material tiles for the central raised handle and slim sides.
 for y in range(32,64):
  for x in range(64,96):image.putpixel((x,y),base)
 path=ASSETS/f'textures/item/{name}_ruler.png';path.parent.mkdir(parents=True,exist_ok=True);image.save(path)
 board=cube([6.5,-8,7.5],[9.5,24,8.25],[8,4,12,8])
 board['faces']['north']['uv']=[0,0,4,16]
 board['faces']['south']['uv']=[4,0,8,16]
 # Central bridge handle, raised on two feet, like the reference photo.
 elements=[board,cube([7,4,6.25],[9,5,7.5],[8,4,12,8]),cube([7,11,6.25],[9,12,7.5],[8,4,12,8]),cube([7,4,5.75],[9,12,6.25],[8,4,12,8])]
 write(ASSETS/f'models/item/{name}_ruler.json',{'textures':{'atlas':f'lsmmod:item/{name}_ruler','particle':f'lsmmod:item/{name}_ruler'},'elements':elements,'display':DISPLAY})
 write(ASSETS/f'items/{name}_ruler.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{name}_ruler'}})
 recipe=ROOT/f'src/main/resources/data/lsmmod/recipe/{name}_ruler.json'
 if name=='netherite':
  write(recipe,{'type':'minecraft:smithing_transform','template':'minecraft:netherite_upgrade_smithing_template','base':'lsmmod:diamond_ruler','addition':'#minecraft:netherite_tool_materials','result':{'id':'lsmmod:netherite_ruler'}})
 else:
  write(recipe,{'type':'minecraft:crafting_shaped','category':'equipment','pattern':['M','M','S'],'key':{'M':MATERIALS[name],'S':'minecraft:stick'},'result':{'id':f'lsmmod:{name}_ruler','count':1}})
write(ROOT/'src/main/resources/data/minecraft/tags/item/swords.json',{'replace':False,'values':[f'lsmmod:{tier[0]}_ruler' for tier in TIERS]})
print('Generated six ruler models, 128x128 pixel textures, definitions, recipes and sword tag.')
