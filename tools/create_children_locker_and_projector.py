"""Two native wooden cubbies, a ceiling projector and bounded reusable resources."""
import json,random
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1];A=ROOT/'src/main/resources/assets/lsmmod';D=ROOT/'src/main/resources/data'
GENERATED=[]
SIDES=('north','south','east','west','up','down')
def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
    GENERATED.append(str(path.relative_to(ROOT)))
def save(image,name):
    path=A/f'textures/block/{name}.png';path.parent.mkdir(parents=True,exist_ok=True);image.save(path);GENERATED.append(str(path.relative_to(ROOT)))
def box(bounds,texture,overrides=None):
    faces={side:{'texture':'#'+texture,'uv':[0,0,16,16]} for side in SIDES}
    if overrides:faces.update(overrides)
    return {'from':bounds[:3],'to':bounds[3:],'faces':faces}
def main():
    rng=random.Random(261011)
    for name,base in [('children_locker_wood',(197,165,124)),('children_locker_end',(202,175,140)),('children_locker_back',(180,145,106))]:
        image=Image.new('RGB',(32,32));columns=[rng.choice((-7,-4,-2,0,0,2,4,6)) for _ in range(32)]
        for y in range(32):
            for x in range(32):
                delta=columns[x]+rng.choice((-2,0,0,0,2));image.putpixel((x,y),tuple(c+delta for c in base))
        save(image,name)
    cubby=[box([1,0,2,15,1,15],'end'),box([1,15,2,15,16,15],'end'),
           box([1,1,2,2,15,15],'wood'),box([14,1,2,15,15,15],'wood'),box([2,1,14,14,15,15],'back')]
    textures={k:'lsmmod:block/children_locker_'+k for k in ('wood','end','back')};textures['particle']=textures['wood']
    for half in ('lower','upper'):write(A/f'models/block/children_locker_{half}.json',{'parent':'minecraft:block/block','textures':textures,'elements':cubby})
    combined=cubby+[{'from':[e['from'][0],e['from'][1]+16,e['from'][2]],'to':[e['to'][0],e['to'][1]+16,e['to'][2]],'faces':e['faces']} for e in cubby]
    write(A/'models/item/children_locker.json',{'parent':'minecraft:block/block','textures':textures,'elements':combined,
          'display':{'gui':{'rotation':[20,145,0],'translation':[0,-3,0],'scale':[.42,.42,.42]},'fixed':{'scale':[.5,.5,.5]}}})
    write(A/'blockstates/children_locker.json',{'variants':{f'facing={f},half={half}':{'model':'lsmmod:block/children_locker_'+half,'y':r} for f,r in [('north',0),('east',90),('south',180),('west',270)] for half in ('lower','upper')}})
    for name,base in [('ceiling_projector_case',(58,73,73)),('ceiling_projector_mount',(43,47,48))]:
        image=Image.new('RGB',(16,16));
        for y in range(16):
            for x in range(16):
                delta=rng.choice((-3,-1,0,0,1,3));image.putpixel((x,y),tuple(c+delta for c in base))
        save(image,name)
    image=Image.new('RGB',(32,16),(41,51,53));draw=ImageDraw.Draw(image)
    draw.rectangle((0,0,31,15),outline=(89,99,96),width=1)
    draw.ellipse((2,2,13,13),fill=(19,25,28),outline=(111,124,119),width=1)
    draw.ellipse((4,4,11,11),fill=(30,74,95),outline=(14,39,55),width=1)
    draw.ellipse((5,5,8,8),fill=(70,135,157));draw.point((6,5),fill=(160,205,211))
    for x in range(18,30,2):draw.line((x,3,x,12),fill=(12,19,21),width=1)
    draw.rectangle((14,5,15,7),fill=(162,172,166));save(image,'ceiling_projector_front')
    image=Image.new('RGB',(16,16),(57,71,71));draw=ImageDraw.Draw(image)
    for y in range(3,14,2):draw.line((2,y,13,y),fill=(22,31,32),width=1)
    save(image,'ceiling_projector_vent')
    projector=[box([1,7,3,15,12,13],'case',{'north':{'texture':'#front','uv':[0,0,16,16]},'east':{'texture':'#vent','uv':[0,0,16,16]},'west':{'texture':'#vent','uv':[0,0,16,16]}}),
               box([2,12,5,3,15,11],'mount'),box([13,12,5,14,15,11],'mount'),box([2,15,5,14,16,11],'mount')]
    write(A/'models/block/ceiling_projector.json',{'parent':'minecraft:block/block','textures':{k:'lsmmod:block/ceiling_projector_'+k for k in ('case','mount','front','vent')}|{'particle':'lsmmod:block/ceiling_projector_case'},'elements':projector})
    write(A/'models/item/ceiling_projector.json',{'parent':'lsmmod:block/ceiling_projector','display':{'gui':{'rotation':[20,145,0],'scale':[.9,.9,.9]}}})
    write(A/'blockstates/ceiling_projector.json',{'variants':{f'facing={f}':{'model':'lsmmod:block/ceiling_projector','y':r} for f,r in [('north',0),('east',90),('south',180),('west',270)]}})
    for ident in ('children_locker','ceiling_projector'):
        write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
        conditions=[{'condition':'minecraft:survives_explosion'}]
        if ident=='children_locker':conditions.append({'condition':'minecraft:block_state_property','block':'lsmmod:children_locker','properties':{'half':'lower'}})
        write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':conditions,'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
    block='''    public static final DeferredBlock<ChildrenLockerBlock> CHILDREN_LOCKER = BLOCKS.registerBlock(
            "children_locker", ChildrenLockerBlock::new,
            props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<CeilingProjectorBlock> CEILING_PROJECTOR = BLOCKS.registerBlock(
            "ceiling_projector", CeilingProjectorBlock::new,
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion());'''
    items='''    public static final DeferredItem<BlockItem> CHILDREN_LOCKER = ITEMS.registerSimpleBlockItem(ModBlocks.CHILDREN_LOCKER);
    public static final DeferredItem<BlockItem> CEILING_PROJECTOR = ITEMS.registerSimpleBlockItem(ModBlocks.CEILING_PROJECTOR);'''
    for filename,body in [('ModBlocks.java',block),('ModItems.java',items)]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text();start='    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR';end='    // END CHILDREN LOCKER AND CEILING PROJECTOR';section=start+'\n'+body+'\n'+end+'\n\n'
        if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section.rstrip()+s[b:]
        else:a=s.index('    // BEGIN CUSTOM PAINTABLE BLOCKS');s=s[:a]+section+s[a:]
        p.write_text(s)
    for locale,names in [('en_us',('Children Locker','Ceiling Projector')),('es_es',('Casillero infantil','Proyector de techo'))]:
        p=A/f'lang/{locale}.json';d=json.loads(p.read_text())
        for ident,name in zip(('children_locker','ceiling_projector'),names):d['block.lsmmod.'+ident]=name
        write(p,d)
    for tool,ident in [('axe','children_locker'),('pickaxe','ceiling_projector')]:
        p=D/f'minecraft/tags/block/mineable/{tool}.json';d=json.loads(p.read_text());d['values']=list(dict.fromkeys(d['values']+['lsmmod:'+ident]));write(p,d)
if __name__=='__main__':main()
