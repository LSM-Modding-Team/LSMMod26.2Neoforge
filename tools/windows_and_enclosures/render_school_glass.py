"""Review actual glass stairs, paint selections, corners and carved surfaces offline."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, output_path
import itertools,json
from PIL import Image,ImageDraw
from resource_json import preview_path
from windows_and_enclosures.create_school_glass_assets import A
from painting_and_editable_blocks.create_school_glass_stair_assets import ID,QUARTERS
from painting_and_editable_blocks.create_custom_paintable_blocks import STEPS
from create_kiosk_entrance_assets import rotated
from render_texture_review import add,load,render,door
from render_block_review import framing


def stair_scene(half='bottom',shape='straight',facing='north',occupied=None,paint=0,mask=3,curtain='none'):
    occupied=(0x33 if half=='bottom' else 0xcc) if occupied is None else occupied
    state=dict(half=half,shape=shape,facing=facing,curtain=curtain);scene=[]
    def predicate(condition):
        if condition['type']=='lsmmod:glazing_mask':return condition['mask']==mask
        cube=condition['cube'];xyz=(cube&1,(cube>>1)&1,(cube>>2)&1)
        neighbor=tuple(a+b for a,b in zip(xyz,STEPS[condition['face']]))
        internal=all(0<=v<2 for v in neighbor)
        covered=internal and occupied&(1<<(neighbor[0]+2*neighbor[1]+4*neighbor[2]))
        color=paint(xyz,condition['face']) if callable(paint) else paint
        return bool(occupied&(1<<cube)) and not covered and condition['paint']==color
    definition=json.loads(module_read_text(A/f'blockstates/{ID}.json'))
    for entry in definition['multipart']:
        if not all(state.get(k) in v.split('|') for k,v in entry.get('when',{}).items()):continue
        apply=entry['apply']
        for model in load(apply['model'],predicate=predicate):
            for _ in range(3*(apply.get('y',0)//90)):model=rotated(model)
            add(scene,[model])
    return scene


def item_scene():
    result=[];definition=json.loads(module_read_text(A/f'items/{ID}.json'))
    add(result,load(definition['model']['model']));return result


def main():
    cases=[('Base sin pintar',stair_scene()),('Cuadrados de 8x8 px',stair_scene(paint=lambda xyz,face:9 if sum(xyz)%2 else 4)),
           ('Martillo / hueco de 8 px',stair_scene(occupied=0x32,paint=9)),('Sin base / cristal conservado',stair_scene(occupied=0)),
           ('Invertida / base superior',stair_scene(half='top',paint=9)),('Item / modelo real',item_scene())]
    for shape in tuple(QUARTERS)[1:]:cases.append((shape,stair_scene(shape=shape,paint=4)))
    for facing in ('east','south','west'):cases.append(('Orientacion '+facing,stair_scene(facing=facing,paint=9)))
    cases.extend([('Cortina / base recortada',stair_scene(occupied=0x32,curtain='single_single',paint=9)),
                  ('Cortina / esquina interior',stair_scene(shape='inner_left',curtain='single_single',paint=9))])
    canvas=Image.new('RGB',(1200,2000),(194,194,194));draw=ImageDraw.Draw(canvas)
    for i,(label,elements) in enumerate(cases):
        origin,scale=framing(elements,size=(400,370));offset=(i%3*400,i//3*400+25)
        canvas.paste(render(elements,origin,scale,size=(400,370),azimuth=-65,elevation=22,minecraft_orientation=True),offset)
        draw.text((offset[0]+8,offset[1]-20),label,fill=(25,25,25))
    canvas.save(output_path(preview_path(ID+'_review.png')))
    # Actual joined glazing, with both normal and inverted stairs and standalone panes.
    joined=[]
    for row,(half,mask) in enumerate((('bottom',11),(None,27),('top',19))):
        if half:add(joined,[{'elements':stair_scene(half=half,paint=9,mask=mask)}],(0,row*16,0))
        else:
            panes=load(json.loads(module_read_text(A/'blockstates/school_glass.json'))['multipart'][-4]['apply']['model'],mask=27)
            for model in panes:
                for element in model.get('elements',[]):
                    for face in ('up','down'):
                        if element['faces'].get(face,{}).get('cullface')==face:element['faces'].pop(face)
            add(joined,panes,(0,row*16,0))
    origin,scale=framing(joined,size=(480,700))
    render(joined,origin,scale,size=(480,700),azimuth=-65,elevation=12,minecraft_orientation=True).save(output_path(preview_path(ID+'_joins_review.png')))

    from windows_and_enclosures.check_horizontal_glazing import layouts
    panels=[]
    for half in ('bottom','top'):
        row=[]
        for column,mask in enumerate(layouts(4)):
            add(row,[{'elements':stair_scene(half=half,mask=mask,paint=4)}],(column*16,0,0))
        panels.append(row)
    review=Image.new('RGB',(1000,700),(194,194,194));labels=ImageDraw.Draw(review)
    for index,elements in enumerate(panels):
        origin,scale=framing(elements,size=(1000,320))
        review.paste(render(elements,origin,scale,size=(1000,320),azimuth=-65,elevation=20,minecraft_orientation=True),(0,index*350+25))
        labels.text((10,index*350+5),'Fila conectada / '+('normal' if index==0 else 'invertida'),fill=(25,25,25))
    review.save(output_path(preview_path(ID+'_horizontal_review.png')))

if __name__=='__main__':main()
