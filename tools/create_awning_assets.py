"""Generate bounded block-state cloth models, metal supports and woven blue textures."""
import json
from pathlib import Path
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'src/main/resources/assets/lsmmod'

def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(value,indent=2)+'\n')

def cube(a,b,texture,uv=(0,0,16,16)):
    return {'from':a,'to':b,'faces':{f:{'uv':list(uv),'texture':texture} for f in ('up','down','north','south','east','west')}}

(ASSETS/'textures/block').mkdir(parents=True,exist_ok=True)
cloth=Image.new('RGB',(16,16))
for y in range(16):
    for x in range(16):
        # Dominant base #385bba; quiet alternating warp/weft and a reinforced seam.
        delta=0 if (x+y)%4 else 5
        if x in (0,15):delta=-10
        cloth.putpixel((x,y),tuple(max(0,min(255,c+delta)) for c in (56,91,186)))
cloth.save(ASSETS/'textures/block/awning.png')
metal=Image.new('RGB',(16,16))
for y in range(16):
    for x in range(16):
        shade=(x*3+y//3)%5-2
        metal.putpixel((x,y),tuple(c+shade for c in (82,93,112)))
metal.save(ASSETS/'textures/block/awning_support.png')

variants={}
rotations={'south':0,'west':90,'north':180,'east':270}
for start in range(17):
    for end in range(17):
        for upper in (False,True):
            elements=[]
            offset=16 if upper else 0
            for i in range(8):
                top=2*(start+(end-start)*(i+.5)/8)-offset
                low=max(0,top-.75);high=min(16,top)
                if low>=high:continue
                piece=cube([0,low,i*2],[16,high,i*2+2],'#cloth')
                # Preserve the cloth weave's density rather than repeat one whole tile per strip.
                for face in ('up','down'):piece['faces'][face]['uv']=[0,i*2,16,i*2+2]
                elements.append(piece)
            name=f'awning/{start}_{end}_{"upper" if upper else "lower"}'
            write(ASSETS/f'models/block/{name}.json',{'textures':{'cloth':'lsmmod:block/awning','particle':'lsmmod:block/awning'},'elements':elements})
            for facing,rotation in rotations.items():
                variants[f'facing={facing},start={start},end={end},upper={str(upper).lower()}']={'model':f'lsmmod:block/{name}','y':rotation}
write(ASSETS/'blockstates/awning.json',{'variants':variants})

support_variants={}
# Four reusable profiles: left end, middle, both ends (width one), right end.
for profile in range(4):
    elements=[cube([0,10,6],[16,16,10],'#metal'),cube([6,13,0],[10,15,16],'#metal')]
    if profile in (0,2):elements.append(cube([0,9.5,5.5],[1,16,10.5],'#metal'))
    if profile in (2,3):elements.append(cube([15,9.5,5.5],[16,16,10.5],'#metal'))
    write(ASSETS/f'models/block/awning_support_{profile}.json',{'textures':{'metal':'lsmmod:block/awning_support','particle':'lsmmod:block/awning_support'},'elements':elements})
for width in range(1,17):
    for column in range(16):
        profile=2 if width==1 and column==0 else 0 if column==0 else 3 if column==width-1 else 1
        for facing,rotation in rotations.items():
            support_variants[f'facing={facing},column={column},width={width}']={'model':f'lsmmod:block/awning_support_{profile}','y':rotation}
write(ASSETS/'blockstates/awning_support.json',{'variants':support_variants})

DISPLAY={'gui':{'rotation':[25,35,0],'scale':[.8,.8,.8]},'ground':{'translation':[0,3,0],'scale':[.5,.5,.5]},'fixed':{'rotation':[0,0,0],'scale':[.7,.7,.7]},'thirdperson_righthand':{'rotation':[75,45,0],'translation':[0,2,0],'scale':[.5,.5,.5]},'thirdperson_lefthand':{'rotation':[75,-45,0],'translation':[0,2,0],'scale':[.5,.5,.5]},'firstperson_righthand':{'rotation':[0,30,0],'translation':[0,2,0],'scale':[.65,.65,.65]},'firstperson_lefthand':{'rotation':[0,-30,0],'translation':[0,2,0],'scale':[.65,.65,.65]}}
# The cloth item is folded; it expands only when attached to a pair of supports.
write(ASSETS/'models/item/awning.json',{'textures':{'cloth':'lsmmod:block/awning','particle':'lsmmod:block/awning'},'display':DISPLAY,'elements':[cube([2,5,3],[14,7,13],'#cloth'),cube([3,7,4],[13,8,12],'#cloth')]})
write(ASSETS/'models/item/awning_support.json',{'textures':{'metal':'lsmmod:block/awning_support','particle':'lsmmod:block/awning_support'},'display':DISPLAY,'elements':[cube([0,7,6],[16,10,10],'#metal'),cube([0,6,5.5],[1,11,10.5],'#metal'),cube([15,6,5.5],[16,11,10.5],'#metal')]})
for name in ('awning','awning_support'):
    write(ASSETS/f'items/{name}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{name}'}})
    # Exactly one item is returned by the structure manager; never one per constituent cell.
    write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[]})
print('Generated awning resources: cloth profiles, support models, item definitions, textures and empty per-cell loot tables.')

# Geometry is inherited from the original awning; only textures vary.
for variant, hex_color, spanish_name, english_name in json.loads((ROOT/'tools/awning_variants.json').read_text()):
    base=tuple(int(hex_color[i:i+2],16) for i in (0,2,4))
    texture=Image.new('RGB',(16,16))
    for y in range(16):
        for x in range(16):
            delta=0 if (x+y)%4 else 5
            if x in (0,15):delta=-10
            texture.putpixel((x,y),tuple(max(0,min(255,c+delta)) for c in base))
    texture.save(ASSETS/f'textures/block/{variant}.png')
    textures={'cloth':f'lsmmod:block/{variant}','particle':f'lsmmod:block/{variant}'}
    colored_variants={}
    for key,value in variants.items():
        profile=value['model'].removeprefix('lsmmod:block/awning/')
        model_name=f'{variant}/{profile}'
        write(ASSETS/f'models/block/{model_name}.json',{'parent':value['model'],'textures':textures})
        colored_variants[key]={**value,'model':f'lsmmod:block/{model_name}'}
    write(ASSETS/f'blockstates/{variant}.json',{'variants':colored_variants})
    write(ASSETS/f'models/item/{variant}.json',{'parent':'lsmmod:item/awning','textures':textures})
    write(ASSETS/f'items/{variant}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{variant}'}})
    write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{variant}.json',{'type':'minecraft:block','pools':[]})
print('Generated 18 additional awning variants with shared geometry and separate woven textures.')
