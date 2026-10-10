"""Three-column tall school entrance, with two 24-pixel leaves and fixed school glazing."""
import copy,itertools,json
from pathlib import Path
from PIL import Image,ImageDraw
from create_horizontal_glazing_assets import glazing
from create_school_glass_assets import A,ROOT,write,T
NAME='double_school_entrance'
# Ordered local face axes match Minecraft UV orientation.
AXES={'north':((0,-1),(1,-1)),'south':((0,1),(1,-1)),
      'west':((2,1),(1,-1)),'east':((2,-1),(1,-1)),
      'up':((0,1),(2,1)),'down':((0,-1),(2,1))}
NORMAL={'west':(0,False),'east':(0,True),'down':(1,False),'up':(1,True),'north':(2,False),'south':(2,True)}

def crop(e,box,caps=False):
    lo=e['from'];hi=e['to'];nl=[max(lo[i],box[i]) for i in range(3)];nh=[min(hi[i],box[i+3]) for i in range(3)]
    if any(nl[i]>=nh[i] for i in range(3)):return None
    out={'from':nl,'to':nh,'faces':{}}
    for face,original in e['faces'].items():
        axis,positive=NORMAL[face]
        if (nh if positive else nl)[axis]!=(hi if positive else lo)[axis]:
            if caps and axis==0:out['faces'][face]={'texture':original['texture'],'uv':[1,1,2,2]}
            continue
        f=copy.deepcopy(original);f.pop('cullface',None)
        ranges=[]
        for a,sign in AXES[face]:
            p,q=(nl[a],nh[a]) if sign==1 else (nh[a],nl[a])
            start,end=(lo[a],hi[a]) if sign==1 else (hi[a],lo[a])
            ranges.append(((p-start)/(end-start),(q-start)/(end-start)))
        # UV rotation changes which physical axis supplies the texture coordinates.
        rot=f.get('rotation',0)
        u,v=ranges
        if rot==90:u,v=(1-v[1],1-v[0]),u
        elif rot==180:u,v=(1-u[1],1-u[0]),(1-v[1],1-v[0])
        elif rot==270:u,v=v,(1-u[1],1-u[0])
        u0,v0,u1,v1=f['uv'];f['uv']=[u0+(u1-u0)*u[0],v0+(v1-v0)*v[0],u0+(u1-u0)*u[1],v0+(v1-v0)*v[1]]
        out['faces'][face]=f
    return out

def art():
    # Original wood, circular glazing and handle palette; no photo decorations.
    bottom=Image.open(A/'textures/block/classroom_door_bottom.png').convert('RGBA')
    top=Image.open(A/'textures/block/classroom_door_top.png').convert('RGBA')
    for row in range(3):
        for col in range(3):
            image=(bottom if row==0 else top).copy()
            if row==0:
                # Remove the repeated handle from each original panel.
                for y in range(16):
                    for x in range(16):
                        if x in (12,13) and y<=5:image.putpixel((x,y),bottom.getpixel((11,y)))
            if row==1 and col in (0,2):
                # Move only the circular glazing/frame; restore the original wood underneath.
                window={(x,y):top.getpixel((x,y)) for y in range(16) for x in range(16)
                        if top.getpixel((x,y))[1]>100 or top.getpixel((x,y))[3]<255}
                for x,y in window:
                    image.putpixel((x,y),bottom.getpixel((11 if x in (12,13) and y<=5 else x,y)))
                offset=4 if col==0 else -4
                for (x,y),color in window.items():image.putpixel((x+offset,y-2),color)
            if row==1 and col==1:
                for y in range(16):
                    for x in range(16):image.putpixel((x,y),bottom.getpixel((min(x,3),y)))
            if row==2:
                for y in range(16):
                    for x in range(16):image.putpixel((x,y),bottom.getpixel((min(x,3),y)))
            if col==1:
                draw=ImageDraw.Draw(image)
                draw.line((7,0,7,15),fill=(79,34,19,255));draw.line((8,0,8,15),fill=(114,48,24,255))
                if row==0:
                    draw.rectangle((5,0,6,5),fill=(180,180,163,255));draw.rectangle((9,0,10,5),fill=(180,180,163,255))
                    draw.point((6,0),fill=(220,216,192,255));draw.point((9,0),fill=(220,216,192,255))
            image.save(A/f'textures/block/{NAME}_{col}_{row}.png')

def wood(opened):
    result=[]
    for row in range(3):
        for col in range(3):
            source=json.loads((A/f'models/block/tall_classroom_entrance_0_{row}.json').read_text())
            for original in source['elements']:
                if not any(f['texture'] in ('#bottom','#top') for f in original['faces'].values()):continue
                e=copy.deepcopy(original)
                for k in ('from','to'):e[k][0]+=16*col;e[k][1]+=16*row
                for f in e['faces'].values():f['texture']=f'#wood_{col}_{row}'
                if row==2:
                    # Keep the center seam on the same physical pixels as both lower rows.
                    e['faces']['north']['uv']=[16,8,0,16]
                    e['faces']['south']['uv']=[0,8,16,16]
                    for face in ('east','west','up','down'):e['faces'][face]['uv']=[1,1,2,2]
                for leaf in (0,1):
                    cut=crop(e,[24*leaf,0,0,24*(leaf+1),48,16],True)
                    if cut is None:continue
                    if opened:
                        lo,hi=cut['from'],cut['to']
                        if leaf==0:
                            cut['from']=[4-hi[2],lo[1],lo[0]];cut['to']=[4-lo[2],hi[1],hi[0]]
                            # Rotate about the outer hinge at (2,2); the other hinge is (46,2).
                            mapping={'north':'east','south':'west','east':'south','west':'north','up':'up','down':'down'}
                        else:
                            cut['from']=[44+lo[2],lo[1],48-hi[0]];cut['to']=[44+hi[2],hi[1],48-lo[0]]
                            mapping={'north':'west','south':'east','east':'north','west':'south','up':'up','down':'down'}
                        cut['faces']={mapping[f]:v for f,v in cut['faces'].items()}
                        for face in ('up','down'):
                            if face in cut['faces']:cut['faces'][face]['rotation']=90 if leaf==0 else 270
                        # Side faces preserve their original face texels through the hinge transform.
                    result.append(cut)
    return result

def model(parts):
    return {'parent':'minecraft:block/block','render_type':'minecraft:translucent',
            'textures':dict(T,**{f'wood_{c}_{r}':f'lsmmod:block/{NAME}_{c}_{r}' for c in range(3) for r in range(3)}), 'elements':parts}

def cell_model(col,row,depth,opened,layout=3,top=False,bottom=False):
    pieces=wood(opened)
    if row==2 and depth==0:
        frame=glazing(layout,8,16,bottom,top,[] if opened else [([0,0,0,16,8,4],'occluder')])
        for e in frame:
            for k in ('from','to'):e[k][0]+=16*col;e[k][1]+=32
            pieces.append(e)
    result=[]
    for e in pieces:
        cropped=crop(e,[16*col,16*row,16*depth,16*(col+1),16*(row+1),16*(depth+1)])
        if cropped:
            for k in ('from','to'):
                cropped[k]=[cropped[k][0]-16*col,cropped[k][1]-16*row,cropped[k][2]-16*depth]
            result.append(cropped)
    return model(result)

def main():
    art();states={};generated={}
    for col,row,depth,opened,power,top,bottom,layout in itertools.product(range(3),range(3),range(2),(False,True),(False,True),(False,True),(False,True),range(8)):
        glass=row==2 and depth==0
        suffix=f'{col}_{row}_{depth}_{int(opened)}'+(f'_{layout}_{int(top)}_{int(bottom)}' if glass else '')
        path=f'{NAME}_{suffix}'
        if path not in generated:
            generated[path]=cell_model(col,row,depth,opened,layout,top,bottom)
            write(A/f'models/block/{path}.json',generated[path])
        for facing,angle in (('north',0),('east',90),('south',180),('west',270)):
            key=f'bottom_connected={str(bottom).lower()},column={col},depth={depth},facing={facing},frame_layout={layout},open={str(opened).lower()},powered={str(power).lower()},row={row},top_connected={str(top).lower()}'
            states[key]={'model':'lsmmod:block/'+path,'y':angle}
    write(A/f'blockstates/{NAME}.json',{'variants':states})
    full=[]
    for col in range(3):
        for row in range(3):
            layout=(1,4,2)[col] if row==2 else 3
            for e in cell_model(col,row,0,False,layout)['elements']:
                for k in ('from','to'):e[k]=[(e[k][0]+16*col)/3,(e[k][1]+16*row)/3,e[k][2]/3+5]
                full.append(e)
    write(A/f'models/item/{NAME}.json',model(full))
    write(A/f'items/{NAME}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+NAME}})
    write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{NAME}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'},{'condition':'minecraft:block_state_property','block':'lsmmod:'+NAME,'properties':{'row':'0','column':'1','depth':'0'}}],'entries':[{'type':'minecraft:item','name':'lsmmod:'+NAME}]}]})
    for locale,label in (('es_es','Puerta doble grande del colegio'),('en_us','Large Double School Door')):
        p=A/f'lang/{locale}.json';data=json.loads(p.read_text());data['block.lsmmod.'+NAME]=label;write(p,data)
    p=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/axe.json';data=json.loads(p.read_text());data['values']=list(dict.fromkeys(data['values']+['lsmmod:'+NAME]));write(p,data)
if __name__=='__main__':main()
