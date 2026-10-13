"""Closed 2x2x1 cabinet and independent decorative desktop router; bounded native models."""
from resource_json import resource_json
import json,random
from PIL import Image,ImageDraw
from create_children_locker_and_projector import ROOT,A,D
from create_bathroom_statue_assets import solid_model
GENERATED=[]

def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True);path.write_text(resource_json(path, value, ensure_ascii=False)+'\n',encoding='utf-8');GENERATED.append(str(path.relative_to(ROOT)))

def save(image,name):
    path=A/f'textures/block/{name}.png';image.save(path);GENERATED.append(str(path.relative_to(ROOT)))


def main():
    rng=random.Random(26101124)
    body=Image.new('RGB',(16,16))
    for y in range(16):
        for x in range(16):
            delta=rng.choice((-2,-1,0,0,1,2))+(1 if x%5==0 else 0)
            body.putpixel((x,y),tuple(c+delta for c in (45,38,34)))
    save(body,'school_cabinet_body')
    front=Image.new('RGB',(32,32),(44,36,31));draw=ImageDraw.Draw(front)
    draw.rectangle((0,0,31,31),outline=(27,24,22),width=2)
    for x0,x1 in ((2,14),(17,29)):
        draw.rectangle((x0,2,x1,29),fill=(48,40,34),outline=(39,33,29))
        for x in range(x0+1,x1,4):draw.line((x,3,x,28),fill=(50,42,35))
    draw.rectangle((15,2,16,29),fill=(22,21,20))
    for x in (13,18):
        draw.line((x,17,x,21),fill=(134,133,125));draw.line((x+1,17,x+1,21),fill=(75,76,72))
    draw.rectangle((27,3,29,4),fill=(123,124,116))
    save(front,'school_cabinet_front')
    textures={'body':'lsmmod:block/school_cabinet_body','front':'lsmmod:block/school_cabinet_front','particle':'#body'}
    variants={}
    for half in range(2):
        for column in range(2):
            name=f'school_cabinet_{"upper" if half else "lower"}_{column}'
            faces={f:{'texture':'#body','uv':[0,0,16,16],'cullface':f} for f in ('north','south','west','east','up','down')}
            faces['north']={'texture':'#front','uv':[16-(column+1)*8,(1-half)*8,16-column*8,(2-half)*8],'cullface':'north'}
            write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid','textures':textures,'elements':[{'from':[0,0,0],'to':[16,16,16],'faces':faces}]})
            for facing,angle in [('north',0),('east',90),('south',180),('west',270)]:
                variants[f'column={column},facing={facing},half={"upper" if half else "lower"}']={'model':'lsmmod:block/'+name,'y':angle}
    write(A/'blockstates/school_cabinet.json',{'variants':variants})
    item_faces={f:{'texture':'#body','uv':[0,0,16,16]} for f in ('north','south','west','east','up','down')};item_faces['north']['texture']='#front'
    write(A/'models/item/school_cabinet.json',{'parent':'minecraft:block/block','render_type':'minecraft:solid','textures':textures,'elements':[{'from':[0,0,4],'to':[16,16,12],'faces':item_faces}]})
    for name,color in [('case',(37,39,40)),('antenna',(27,28,29))]:
        image=Image.new('RGB',(16,16),color);draw=ImageDraw.Draw(image)
        draw.line((1,1,14,1),fill=tuple(c+9 for c in color));save(image,'school_router_'+name)
    image=Image.new('RGB',(16,4),(34,36,37));draw=ImageDraw.Draw(image)
    for x in (2,4,6):draw.point((x,1),fill=(99,159,73))
    for x in (9,11,13):draw.line((x,1,x,2),fill=(17,19,20))
    save(image,'school_router_front')
    image=Image.new('RGB',(16,16),(41,43,44));draw=ImageDraw.Draw(image)
    for y in range(4,13,2):draw.line((3,y,12,y),fill=(25,27,28))
    draw.rectangle((6,1,9,2),fill=(119,121,117));save(image,'school_router_top')
    parts=[([3,0,4,13,3,12],'router_case',{'north':'router_front','up':'router_top'}),
           ([4,3,10,5,12,11],'router_antenna'),([11,3,10,12,12,11],'router_antenna')]
    model=solid_model(parts);model['render_type']='minecraft:solid';model['textures']['particle']='lsmmod:block/school_router_case'
    write(A/'models/block/school_router.json',model)
    write(A/'models/item/school_router.json',{'parent':'lsmmod:block/school_router'})
    write(A/'blockstates/school_router.json',{'variants':{f'facing={side}':{'model':'lsmmod:block/school_router','y':angle} for side,angle in [('north',0),('east',90),('south',180),('west',270)]}})
    for ident in ('school_cabinet','school_router'):
        write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
        conditions=[{'condition':'minecraft:survives_explosion'}]
        if ident=='school_cabinet':conditions.append({'condition':'minecraft:block_state_property','block':'lsmmod:'+ident,'properties':{'column':'0','half':'lower'}})
        write(D/f'lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':conditions,'entries':[{'type':'minecraft:item','name':'lsmmod:'+ident}]}]})
    blocks='''    public static final DeferredBlock<SchoolCabinetBlock> SCHOOL_CABINET = BLOCKS.registerBlock(
            "school_cabinet", SchoolCabinetBlock::new,
            props -> props.strength(2.0F).sound(SoundType.WOOD).pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolRouterBlock> SCHOOL_ROUTER = BLOCKS.registerBlock(
            "school_router", SchoolRouterBlock::new,
            props -> props.strength(0.5F).sound(SoundType.METAL).noOcclusion());'''
    items='''    public static final DeferredItem<BlockItem> SCHOOL_CABINET = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CABINET);
    public static final DeferredItem<BlockItem> SCHOOL_ROUTER = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ROUTER);'''
    for filename,body in [('ModBlocks.java',blocks),('ModItems.java',items)]:
        p=ROOT/'src/main/java/net/nicomar2009/lsmmod/registry'/filename;s=p.read_text();start='    // BEGIN SCHOOL CABINET AND ROUTER';end='    // END SCHOOL CABINET AND ROUTER';section=start+'\n'+body+'\n'+end
        if start in s:a=s.index(start);b=s.index(end)+len(end);s=s[:a]+section+s[b:]
        else:marker='    // BEGIN CLASSROOM SURFACES';s=s.replace(marker,section+'\n\n'+marker,1)
        p.write_text(s);GENERATED.append(str(p.relative_to(ROOT)))
    for locale,names in [('es_es',{'school_cabinet':'Armario del colegio','school_router':'Router del colegio'}),('en_us',{'school_cabinet':'School Cabinet','school_router':'School Router'})]:
        p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v.update({'block.lsmmod.'+k:n for k,n in names.items()});write(p,v)
    p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text());v['values']=list(dict.fromkeys(v['values']+['lsmmod:school_cabinet','lsmmod:school_router']));write(p,v)


if __name__=='__main__':main()
