"""Generate connected school glazing and one three-block-tall operable classroom door."""
import json
from pathlib import Path
from PIL import Image, ImageDraw
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
D=ROOT/'src/main/resources/data'

def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(value,ensure_ascii=False,indent=2)+'\n')

def cube(bounds,material,uv=None,mirror=False):
    faces={f:{'texture':'#'+material,'uv': uv or [0,0,16,16]} for f in ['north','south','east','west','up','down']}
    if mirror:
        for f in ('north','south'):faces[f]['uv']=[faces[f]['uv'][2],faces[f]['uv'][1],faces[f]['uv'][0],faces[f]['uv'][3]]
    return {'from':bounds[:3],'to':bounds[3:],'faces':faces}

T={'glass':'lsmmod:block/school_glass','support':'lsmmod:block/dark_school_wall','light_wall':'lsmmod:block/light_school_wall','concrete':'minecraft:block/white_concrete',
   'transom':'lsmmod:block/school_transom_glass','glass_edge':'lsmmod:block/school_glass_edge',
   'frame':'lsmmod:block/school_glass_frame','bottom':'lsmmod:block/classroom_door_bottom',
   'top':'lsmmod:block/classroom_door_top','particle':'lsmmod:block/school_glass_frame'}

PART_IDS={(0,0):'tall_classroom_entrance',(1,0):'tall_classroom_entrance_right_bottom',
          (0,1):'tall_classroom_entrance_left_middle',(1,1):'tall_classroom_entrance_right_middle',
          (0,2):'tall_classroom_entrance_left_top',(1,2):'tall_classroom_entrance_right_top'}

def model(elements):return {'parent':'minecraft:block/block','render_type':'minecraft:translucent','textures':T,'elements':elements}

def textures():
    p=A/'textures/block';p.mkdir(parents=True,exist_ok=True)
    glass=Image.new('RGBA',(16,16),(209,219,216,90));draw=ImageDraw.Draw(glass)
    # Discreet pale streaks recall the irregular old school glazing, with clear sight through it.
    draw.line([(3,2),(4,5),(3,9),(4,13)],fill=(241,242,228,140))
    draw.line([(11,1),(10,4),(11,7)],fill=(242,243,233,132))
    draw.line([(7,11),(8,13),(8,15)],fill=(191,204,201,120))
    glass.save(p/'school_glass.png')
    Image.new('RGBA',(16,16),(207,217,214,100)).save(p/'school_glass_edge.png')
    # Native 16x4 pixels keep the shallow transom reflections in proportion.
    transom=Image.new('RGBA',(16,4),(214,224,221,82));draw=ImageDraw.Draw(transom)
    draw.line((3,1,5,1),fill=(243,244,234,136))
    draw.line((11,2,12,2),fill=(236,239,229,112))
    transom.save(p/'school_transom_glass.png')
    support=Image.new('RGB',(16,16),(203,200,181));draw=ImageDraw.Draw(support)
    for x,y in [(2,2),(11,4),(5,9),(12,13)]:draw.rectangle((x,y,x+1,y+1),fill=(198,196,179))
    support.save(p/'school_glass_support.png')
    frame=Image.new('RGB',(16,16),(58,43,35));draw=ImageDraw.Draw(frame)
    for x in (2,9,14):draw.line((x,0,x,15),fill=(67,49,39))
    draw.line((0,1,15,1),fill=(85,64,49));frame.save(p/'school_glass_frame.png')

def transom_parts(top,bottom):
    parts=[cube([1,8 if bottom else 9,0,15,16 if top else 15,8],'glass'),
           cube([0,8,0,1,16,8],'frame'),cube([15,8,0,16,16,8],'frame')]
    if not bottom:parts.append(cube([1,8,0,15,9,8],'frame'))
    if not top:parts.append(cube([1,15,0,15,16,8],'frame'))
    for e in parts:
        if e['to'][1]==16:e['faces']['up']['cullface']='up'
    return parts

def main():
    textures()
    for name,sill in [('school_glass',False),('supported_school_glass',True),('mixed_school_glass',True)]:
        variants={}
        for top in (False,True):
            for bottom in (False,True):
                y=8 if sill else 0
                low=y if bottom else y+1
                high=16 if top else 15
                parts=[cube([1,low,0,15,high,8],'glass'),
                       cube([0,y,0,1,16,8],'frame'),cube([15,y,0,16,16,8],'frame')]
                if not bottom:parts.append(cube([1,y,0,15,y+1,8],'frame'))
                if not top:parts.append(cube([1,15,0,15,16,8],'frame'))
                if name=='mixed_school_glass':
                    parts.extend([cube([0,0,0,16,8,8],'concrete'),cube([0,0,8,16,8,16],'light_wall')])
                    del parts[-2]['faces']['south'];del parts[-1]['faces']['north']
                elif sill:parts.append(cube([0,0,0,16,8,16],'support'))
                # Only boundary faces can be culled, never the top of the step.
                for e in parts:
                    for f,axis,positive in [('west',0,False),('east',0,True),('down',1,False),('up',1,True)]:
                        if e['to' if positive else 'from'][axis]==(16 if positive else 0):
                            e['faces'][f]['cullface']=f
                suffix=f'{name}_{int(top)}_{int(bottom)}'
                write(A/f'models/block/{suffix}.json',model(parts))
                for f,r in [('north',0),('east',90),('south',180),('west',270)]:
                    variants[f'bottom_connected={str(bottom).lower()},facing={f},top_connected={str(top).lower()}']={'model':f'lsmmod:block/{suffix}','y':r}
                if not top and not bottom:
                    write(A/f'models/block/{name}.json',model(parts))
        write(A/f'blockstates/{name}.json',{'variants':variants})
        write(A/f'models/item/{name}.json',{'parent':f'lsmmod:block/{name}'})
        item_and_loot(name)
    variants={};full=[]
    for row in range(3):
        for column in range(2):
            if row < 2:
                parts=[cube([0,0,0,16,16,4],'bottom' if row==0 else 'top')]
                for part in parts:
                    for face,reverse in [('north',column==0),('south',column==1)]:
                        uv=part['faces'][face]['uv']
                        if reverse:part['faces'][face]['uv']=[uv[2],uv[1],uv[0],uv[3]]
                    for face in ('up','down','east','west'):part['faces'][face]['uv']=[1,1,2,2]
            else:
                # Exactly the same framed glass as the standalone block, fixed above the moving leaf.
                parts=[cube([0,0,0,16,8,4],'top',[0,12,16,16])] + transom_parts(False,False)
            name=f'tall_classroom_entrance_{column}_{row}'
            write(A/f'models/block/{name}.json',model(parts))
            opened=[]
            remap={'north':'west','south':'east','east':'north','west':'south','up':'up','down':'down'} if column==0 else {'north':'east','south':'west','east':'south','west':'north','up':'up','down':'down'}
            for part in parts:
                if row==2 and next(iter(part['faces'].values()))['texture']!='#top':
                    opened.append(json.loads(json.dumps(part)));continue
                e=json.loads(json.dumps(part));lo,hi=part['from'],part['to']
                if column==0:e['from']=[lo[2],lo[1],16-hi[0]];e['to']=[hi[2],hi[1],16-lo[0]]
                else:e['from']=[16-hi[2],lo[1],lo[0]];e['to']=[16-lo[2],hi[1],hi[0]]
                e['faces']={remap[f]:data for f,data in e['faces'].items()}
                for f in ('up','down'):e['faces'][f]['rotation']=90 if column==0 else 270
                opened.append(e)
            write(A/f'models/block/{name}_open.json',model(opened))
    for top in (False,True):
        for bottom in (False,True):
            for col in range(2):
                for opened in (False,True):
                    apron=cube([0,0,0,16,8,4],'top',[0,12,16,16])
                    for face in ('up','down','east','west'):apron['faces'][face]['uv']=[1,1,2,2]
                    if opened:
                        apron['from']=[0,0,0] if col==0 else [12,0,0]
                        apron['to']=[4,8,16] if col==0 else [16,8,16]
                        mapping={'north':'west','south':'east','east':'north','west':'south','up':'up','down':'down'} if col==0 else {'north':'east','south':'west','east':'south','west':'north','up':'up','down':'down'}
                        apron['faces']={mapping[f]:v for f,v in apron['faces'].items()}
                    write(A/f'models/block/tall_classroom_transom_{col}_{int(opened)}_{int(top)}_{int(bottom)}.json',model([apron]+transom_parts(top,bottom)))
    variants={}
    for row in range(3):
        for hinge,col in [('left',0),('right',1)]:
            for opened in (False,True):
                for powered in (False,True):
                    for top in (False,True):
                        for bottom in (False,True):
                            for facing,angle in [('north',0),('east',90),('south',180),('west',270)]:
                                name=f'tall_classroom_transom_{col}_{int(opened)}_{int(top)}_{int(bottom)}' if row==2 else f'tall_classroom_entrance_{col}_{row}'+('_open' if opened else '')
                                variants[f'bottom_connected={str(bottom).lower()},facing={facing},hinge={hinge},open={str(opened).lower()},powered={str(powered).lower()},row={row},top_connected={str(top).lower()}']={'model':f'lsmmod:block/{name}','y':angle}
    write(A/'blockstates/tall_classroom_entrance.json',{'variants':variants})
    # The item depicts one complete door, rather than a single row or a double door.
    full=[]
    for row in range(3):
        m=json.loads((A/f'models/block/tall_classroom_entrance_0_{row}.json').read_text())
        for part in m['elements']:
            e=json.loads(json.dumps(part))
            for k in ('from','to'):e[k]=[5+e[k][0]/3,(e[k][1]+16*row)/3,5+e[k][2]/3]
            full.append(e)
    write(A/'models/item/tall_classroom_entrance.json',model(full))
    item_and_loot('tall_classroom_entrance',{'row':'0'})
    # Former component IDs are no longer exposed as blocks or items.
    for old in PART_IDS.values():
        if old=='tall_classroom_entrance':continue
        for path in [A/f'blockstates/{old}.json',A/f'items/{old}.json',A/f'models/item/{old}.json',D/f'lsmmod/loot_table/blocks/{old}.json']:
            path.unlink(missing_ok=True)
    names={'school_glass':('Cristal del colegio','School Glass'),
           'supported_school_glass':('Cristal con alféizar de pared oscura','School Glass with Dark Wall Sill'),
           'mixed_school_glass':('Cristal con muro claro y concreto blanco','School Glass with Light Wall and White Concrete'),
           'tall_classroom_entrance':('Puerta alta de aula','Tall Classroom Door')}
    for locale,index in [('es_es',0),('en_us',1)]:
        p=A/f'lang/{locale}.json';v=json.loads(p.read_text())
        for old in PART_IDS.values():
            if old!='tall_classroom_entrance':v.pop('block.lsmmod.'+old,None)
        for name,n in names.items():v['block.lsmmod.'+name]=n[index]
        write(p,v)
    for tag,ids in [('pickaxe',['supported_school_glass','mixed_school_glass']),('axe',['tall_classroom_entrance'])]:
        p=D/f'minecraft/tags/block/mineable/{tag}.json';v=json.loads(p.read_text())
        v['values']=[x for x in v['values'] if x.removeprefix('lsmmod:') not in set(PART_IDS.values())-{'tall_classroom_entrance'}]
        for name in ids:
            if 'lsmmod:'+name not in v['values']:v['values'].append('lsmmod:'+name)
        write(p,v)

def item_and_loot(name,properties=None):
    write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':f'lsmmod:item/{name}'}})
    conditions=[{'condition':'minecraft:survives_explosion'}]
    if properties:conditions.append({'condition':'minecraft:block_state_property','block':'lsmmod:'+name,'properties':properties})
    write(D/f'lsmmod/loot_table/blocks/{name}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':conditions,'entries':[{'type':'minecraft:item','name':'lsmmod:'+name}]}]})

if __name__=='__main__':
    main()
    from create_school_glass_stair_assets import main as stair_main
    stair_main()
    from create_horizontal_glazing_assets import main as horizontal_main
    horizontal_main()
    from create_school_curtain_assets import main as curtain_main
    curtain_main()
    from create_tall_door_variants import main as tall_variants_main
    tall_variants_main()
    from create_double_school_entrance import main as double_entrance_main
    double_entrance_main()
