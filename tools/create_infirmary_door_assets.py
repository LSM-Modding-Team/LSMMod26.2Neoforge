"""Material variants and perpendicular connected rear-half glazing; reuse normal door placement."""
import copy
import json
from create_school_glass_assets import A, cube
from create_bathroom_statue_assets import solid_model
from framed_door_texture_uv import leaf_face

INFIRMARY_FIXED=[([24,0,0,32,16,16],'white',{'north':'front','east':'light'}),
                 ([24,16,0,32,40,8],'white',{'north':'front','east':'light'}),
                 ([0,40,0,32,48,8],'glass'),([24,16,8,32,48,16],'glass')]
LEAF=[([0,0,0,24,20,4],'bottom'),([0,20,0,24,40,4],'top')]
INFIRMARY_CLOSED=INFIRMARY_FIXED+LEAF
INFIRMARY_OPEN=INFIRMARY_FIXED+[([b[2],b[1],b[0],b[5],b[4],b[3]],m) for b,m in LEAF]


def side_elements(mask,right):
    start,end=(0,8) if right else (8,16)
    posts=[]
    if mask&1:posts.append((start,start+1))
    if mask&2:posts.append((end-1,end))
    parts=[([a,0,0,b,16,8],'frame') for a,b in posts]
    if not mask&16:parts.append(([start,0,0,end,1,8],'frame'))
    if not mask&8:parts.append(([start,15,0,end,16,8],'frame'))
    edges=[start]+[v for ab in posts for v in ab]+[end]
    parts += [([edges[i],0 if mask&16 else 1,0,edges[i+1],16 if mask&8 else 15,8],'glass')
              for i in range(0,len(edges)-1,2) if edges[i]<edges[i+1]]
    elements=solid_model(parts)['elements']
    for e in elements:
        lo,hi=e['from'],e['to']
        for face,material in list(e['faces'].items()):
            if material['texture']=='#glass' and (face=='west' and lo[0]==start and not mask&1
                    or face=='east' and hi[0]==end and not mask&2
                    or face=='up' and hi[1]==16 and mask&8 or face=='down' and lo[1]==0 and mask&16):
                del e['faces'][face];continue
            material['uv']={'north':[16-hi[0],16-hi[1],16-lo[0],16-lo[1]],
                 'south':[lo[0],16-hi[1],hi[0],16-lo[1]],'east':[16-hi[2],16-hi[1],16-lo[2],16-lo[1]],
                 'west':[lo[2],16-hi[1],hi[2],16-lo[1]],'up':[lo[0],lo[2],hi[0],hi[2]],
                 'down':[16-hi[0],lo[2],16-lo[0],hi[2]]}[face]
        # Native half-width pane rotated into its perpendicular side, preserving UVs.
        remap=({'north':'east','east':'south','south':'west','west':'north'} if not right
               else {'north':'west','west':'south','south':'east','east':'north'})
        e['from']=([16-hi[2],lo[1],lo[0]] if not right else [lo[2],lo[1],16-hi[0]])
        e['to']=([16-lo[2],hi[1],hi[0]] if not right else [hi[2],hi[1],16-lo[0]])
        e['faces']={remap.get(f,f):v for f,v in e['faces'].items()}
        for face in ('up','down'):
            if face in e['faces']:e['faces'][face]['rotation']=90 if not right else 270
    return [e for e in elements if e['faces']]


def generate(multipart,textures,write,clipped,mesh,layered_item,basic):
    fronttex=dict(textures,front='lsmmod:block/light_school_wall')
    # The wall uses light material only on its front; all other wall faces are white.
    light=copy.deepcopy(multipart);changed={}
    for entry in light:
        name=entry['apply']['model'].split(':')[1]
        if not name.startswith('block/dining_door_wide_part_'):continue
        model=json.loads((A/f'models/{name}.json').read_text())
        found=False
        for e in model.get('elements',[]):
            for material in e['faces'].values():
                if material['texture']=='#light':material['texture']='#white';found=True
            face=e['faces'].get('north')
            if face and face['texture']=='#white':face['texture']='#front';found=True
        if found:
            model['textures']['front']=fronttex['front'];new=name.replace('dining_door_wide','dining_door_wide_light')
            if new not in changed:write(A/f'models/{new}.json',model);changed[new]=True
            entry['apply']['model']='lsmmod:'+new
    write(A/'blockstates/dining_door_wide_light.json',{'multipart':light})
    solid=json.loads((A/'models/item/dining_door_wide_item_solid.json').read_text())
    solid['textures']['front']=fronttex['front']
    for e in solid['elements']:
        for material in e['faces'].values():
            if material['texture']=='#light':material['texture']='#white'
        f=e['faces'].get('north')
        if f and f['texture']=='#white':f['texture']='#front'
    write(A/'models/item/dining_door_wide_light_item_solid.json',solid)
    write(A/'models/item/dining_door_wide_light.json',{'loader':'fusion:model','type':'composite','textures':fronttex,
          'models':[['lsmmod:item/dining_door_wide_light_item_solid','lsmmod:item/dining_door_wide_item_translucent','lsmmod:item/dining_door_wide_item_cutout']]})
    basic('dining_door_wide_light',controller={'column':'0','row':'0','depth':'0'})
    tex=dict(fronttex,bottom='lsmmod:block/bathroom_door_bottom',top='lsmmod:block/bathroom_door_top',
             glass='lsmmod:block/school_tinted_glass',frame='lsmmod:block/school_glass_frame',particle='lsmmod:block/bathroom_door_bottom')
    state=[];shared={}
    for opened in (False,True):
        for right in (False,True):
            for row in range(3):
                for col in range(2):
                    for depth in range(2):
                        parts=clipped(INFIRMARY_OPEN if opened else INFIRMARY_CLOSED,col,row,depth,right)
                        for layer,materials in [('solid',('white',)),('cutout',('bottom','top'))]:
                            value=mesh([p for p in parts if p[1] in materials],tex);value['render_type']='minecraft:'+layer
                            for e in value['elements']:
                                for face,s in list(e['faces'].items()):
                                    if s['texture'] not in ('#bottom','#top'):s['uv']=[0,0,16,16];continue
                                    axis,pos={'west':(0,False),'east':(0,True),'down':(1,False),'up':(1,True),'north':(2,False),'south':(2,True)}[face]
                                    plane=e['to' if pos else 'from'][axis]+(col*16,row*16,depth*16)[axis]
                                    if axis==1 and plane in (16,32) or axis==(2 if opened else 0) and plane==16:
                                        del e['faces'][face];continue
                                    leaf_face(e,face,s,col,row,depth,opened,right)
                            value['elements']=[e for e in value['elements'] if e['faces']]
                            if not value['elements']:continue
                            key=json.dumps(value,sort_keys=True)
                            if key not in shared:
                                shared[key]=f'infirmary_door_part_{len(shared)}';write(A/f'models/block/{shared[key]}.json',value)
                            for turn,facing in enumerate(('north','east','south','west')):
                                state.append({'when':{'facing':facing,'open':str(opened).lower(),'hinge':'right' if right else 'left',
                                               'row':str(row),'column':str(col),'depth':str(depth)},
                                              'apply':{'model':'lsmmod:block/'+shared[key],'y':turn*90}})
    # Tinted material inherits the exact existing normal-glass connected transom geometry.
    entries=[]
    for mask in range(16):
        name=f'infirmary_door_transom_{mask}'
        write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'base','parent':f'lsmmod:block/framed_door_transom_{mask}',
              'textures':tex,'render_type':'minecraft:solid'})
        entries.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:glazing_mask','masks':[mask,mask|16]}})
    write(A/'models/block/infirmary_door_transom.json',{'loader':'fusion:model','type':'composite','textures':tex,'models':[entries]})
    for turn,facing in enumerate(('north','east','south','west')):
        state.append({'when':{'facing':facing,'row':'2','depth':'0'},'apply':{'model':'lsmmod:block/infirmary_door_transom','y':turn*90}})
    for right in (False,True):
        entries=[]
        # Middle-post bit cannot create a second edge on an eight-pixel-wide pane.
        for mask in range(32):
            if mask&4:continue
            name=f'infirmary_door_side_{int(right)}_{mask}'
            write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'base','parent':'minecraft:block/block',
                  'textures':tex,'render_type':'minecraft:solid','elements':side_elements(mask,right)})
            entries.append({'model':'lsmmod:block/'+name,'condition':{'type':'lsmmod:glazing_mask','masks':[mask,mask|4],'channel':'side'}})
        name=f'infirmary_door_side_{int(right)}'
        write(A/f'models/block/{name}.json',{'loader':'fusion:model','type':'composite','textures':tex,'models':[entries]})
        for turn,facing in enumerate(('north','east','south','west')):
            state.append({'when':{'facing':facing,'row':'1|2','column':'0' if right else '1','hinge':'right' if right else 'left','depth':'0'},
                          'apply':{'model':'lsmmod:block/'+name,'y':turn*90}})
    write(A/'blockstates/infirmary_door.json',{'multipart':state})
    layered_item('infirmary_door',[('solid',[p for p in INFIRMARY_CLOSED if p[1]=='white']),('cutout',LEAF)],tex,32,48)
    path=A/'models/item/infirmary_door_item_cutout.json';item=json.loads(path.read_text())
    for e in item['elements']:
        original={k:[(e[k][0]-8)*3+16,e[k][1]*3,(e[k][2]-8)*3+8] for k in ('from','to')}
        for face,material in e['faces'].items():leaf_face(original,face,material)
    write(path,item)
    glazed=[]
    for col,mask in enumerate((1,2)):
        elements=json.loads((A/f'models/block/framed_door_transom_{mask}.json').read_text())['elements']
        glazed += [(e,(16*col,32,0)) for e in elements]
    for row,mask in ((1,11),(2,19)):
        glazed += [(e,(16,16*row,0)) for e in side_elements(mask,False)]
    elements=[]
    for e,offset in glazed:
        e=copy.deepcopy(e)
        for k in ('from','to'):
            x,y,z=[v+offset[i] for i,v in enumerate(e[k])];e[k]=[8+(x-16)/3,y/3,8+(z-8)/3]
        for material in e['faces'].values():material.pop('cullface',None)
        elements.append(e)
    write(A/'models/item/infirmary_door_item_glass.json',{'loader':'fusion:model','type':'base','textures':tex,'render_type':'minecraft:solid','elements':elements})
    item=json.loads((A/'models/item/infirmary_door.json').read_text());item['models'][0].append('lsmmod:item/infirmary_door_item_glass');write(A/'models/item/infirmary_door.json',item)
    basic('infirmary_door',controller={'column':'0','row':'0','depth':'0'})
