"""Curtain item and multipart overlays entirely inside the empty glass/stair volume."""
import json, random
from PIL import Image, ImageDraw
from create_school_glass_assets import A, ROOT, write
from create_bathroom_statue_assets import solid_model

IDS=('school_glass','supported_school_glass','mixed_school_glass',
     'supported_school_glass_reversed','mixed_school_glass_reversed')
PARTS=('single','left','middle','right')

def mounts():
    return {'full':(0,16,0,16),'upper':(0,16,8,16),'lower':(0,16,0,8),
            'upper_inner_left':(8,16,8,16),'upper_inner_right':(0,8,8,16),
            'lower_inner_left':(8,16,0,8),'lower_inner_right':(0,8,0,8)}

def curtain_model(bounds,part,vertical="single"):
    # Full-height cells share cloth edges; rail only at the upper end.
    left,right,low,high=bounds
    pieces=[]
    bottom=low+.25 if vertical in ("single","left") or low>0 else low
    top=high-1 if vertical in ("single","right") or high<16 else high
    # Shallow alternating folds and a slim rail all stay beyond the glass's z=8 edge.
    for x in range(left,right,2):
        z=8.75 if (x//2)%2==0 else 9.5
        pieces.append(([x,bottom,z,x+2,top,z+.75],'cloth'))
    if vertical in ("single","right") or high<16:
        pieces.append(([left,high-1,8.5,right,high-.5,10.5],'rail'))
    if part in ('single','left'):pieces.append(([left,bottom,10.25,left+.5,top,10.75],'hem'))
    if part in ('single','right'):pieces.append(([right-.5,bottom,10.25,right,top,10.75],'hem'))
    result=solid_model(pieces)
    result['render_type']='minecraft:cutout'
    result['textures']={'cloth':'lsmmod:block/school_curtain','rail':'lsmmod:block/school_curtain_rail',
                        'hem':'lsmmod:block/school_curtain_hem','particle':'lsmmod:block/school_curtain'}
    for e in result['elements']:
        lo,hi=e['from'],e['to']
        # Suppress internal caps where vertically adjoining full-height cloth meets.
        if vertical not in ('single','left') and low==0 and lo[1]==0:e['faces'].pop('down',None)
        if vertical not in ('single','right') and high==16 and hi[1]==16:e['faces'].pop('up',None)
        for face,f in e['faces'].items():
            f['uv']={'north':[16-hi[0],16-hi[1],16-lo[0],16-lo[1]],
                     'south':[lo[0],16-hi[1],hi[0],16-lo[1]],
                     'east':[16-hi[2],16-hi[1],16-lo[2],16-lo[1]],
                     'west':[lo[2],16-hi[1],hi[2],16-lo[1]],
                     'up':[lo[0],lo[2],hi[0],hi[2]],
                     'down':[16-hi[0],lo[2],16-lo[0],hi[2]]}[face]
    # Minecraft rejects an element with zero faces, even if it is entirely hidden.
    result["elements"]=[e for e in result["elements"] if e["faces"]]
    return result

def base_parts(definition):
    if 'variants' in definition:
        return [{'when':dict(p.split('=') for p in key.split(',')),'apply':value}
                for key,value in definition['variants'].items()]
    return [part for part in definition['multipart'] if 'curtain' not in part.get('when',{})]

def translations():
    messages={
        'item.lsmmod.school_curtain':('Cortina del colegio','School Curtain'),
        'tooltip.lsmmod.school_curtain.select':('En cristal del colegio: clic en esquinas opuestas (máx. 4 de ancho × 8 de alto).','On school glass: click opposite corners (max. 4 wide × 8 high).'),
        'tooltip.lsmmod.school_curtain.remove':('Shift + clic: retirar; Shift + clic al aire: cancelar.','Shift-click: remove; shift-click in air: cancel.'),
        'message.lsmmod.curtain.start':('Inicio marcado (%s, %s, %s). Elige el otro extremo.','Start marked (%s, %s, %s). Choose the other end.'),
        'message.lsmmod.curtain.placed':('Cortina colocada: %s × %s bloques.','Curtain placed: %s × %s blocks.'),
        'message.lsmmod.curtain.cancelled':('Selección de cortina cancelada.','Curtain selection cancelled.'),
        'message.lsmmod.curtain.denied':('No puedes modificar todo este tramo.','You cannot modify this whole span.'),
        'message.lsmmod.curtain.unloaded':('Acércate al tramo completo antes de continuar.','Move closer to the whole span before continuing.'),
        'message.lsmmod.curtain.occupied':('Ese cristal ya tiene una cortina.','That glass already has a curtain.'),
        'message.lsmmod.curtain.line':('Selecciona un rectángulo completo de cristales con la misma orientación.','Select a complete glass rectangle with matching orientation.'),
        'message.lsmmod.curtain.length':('Una cortina puede cubrir como máximo 4 bloques de ancho y 8 de alto.','One curtain can cover at most 4 blocks wide and 8 high.'),
        'message.lsmmod.curtain.target':('Solo puedes colocar cortinas en el cristal del colegio y sus escaleras.','Curtains only attach to school glass and its stairs.'),
    }
    for locale,index in (('es_es',0),('en_us',1)):
        p=A/f'lang/{locale}.json';value=json.loads(p.read_text())
        value.update({key:labels[index] for key,labels in messages.items()});write(p,value)

def main():
    rng=random.Random(417)
    cloth=Image.new('RGB',(16,16));palette=[(174,139,104),(200,165,125),(222,190,148),(211,180,141)]
    for y in range(16):
        for x in range(16):
            color=palette[x%4];noise=rng.choice((-1,0,0,1))
            cloth.putpixel((x,y),tuple(c+noise for c in color))
    cloth.save(A/'textures/block/school_curtain.png')
    Image.new('RGB',(16,16),(221,211,194)).save(A/'textures/block/school_curtain_rail.png')
    Image.new('RGB',(16,16),(171,135,100)).save(A/'textures/block/school_curtain_hem.png')
    icon=Image.new('RGBA',(16,16),(0,0,0,0));draw=ImageDraw.Draw(icon)
    draw.rectangle((1,2,14,3),fill=(221,211,194,255))
    for x in range(2,14):draw.line((x,4,x,13-(x%3==0)),fill=(*palette[x%4],255))
    icon.save(A/'textures/item/school_curtain.png')
    write(A/'models/item/school_curtain.json',{'parent':'minecraft:item/generated','textures':{'layer0':'lsmmod:item/school_curtain'}})
    write(A/'items/school_curtain.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/school_curtain'}})
    for name,bounds in mounts().items():
        for part in PARTS:
            for vertical in PARTS:
                suffix='' if vertical=='single' else '_vertical_'+vertical
                write(A/f'models/block/school_curtain_{name}_{part}{suffix}.json',curtain_model(bounds,part,vertical))
    for block in IDS:
        path=A/f'blockstates/{block}.json';parts=base_parts(json.loads(path.read_text()))
        for facing,angle in (('north',0),('east',90),('south',180),('west',270)):
            for part in PARTS:
                for vertical in PARTS:
                    suffix='' if vertical=='single' else '_vertical_'+vertical
                    when={'facing':facing,'curtain':part,'curtain_vertical':vertical}
                    if block=='school_glass':
                        parts.append({'when':when,'apply':{'model':f'lsmmod:block/school_curtain_full_{part}{suffix}','y':angle}})
                    else:
                        for half,band in (('bottom','upper'),('top','lower')):
                            for shape,corner in (('straight|outer_left|outer_right',''),('inner_left','_inner_left'),('inner_right','_inner_right')):
                                parts.append({'when':dict(when,half=half,shape=shape),
                                              'apply':{'model':f'lsmmod:block/school_curtain_{band}{corner}_{part}{suffix}','y':angle}})
        write(path,{'multipart':parts})
    translations()

if __name__=='__main__':main()
