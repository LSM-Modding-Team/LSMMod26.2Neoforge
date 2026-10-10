"""Horizontal glazing layouts: change only visible glass/frame, never supports or doors."""
import json
from create_school_glass_assets import A, T, write, model
from create_bathroom_statue_assets import solid_model

IDS=('school_glass','supported_school_glass','mixed_school_glass',
     'supported_school_glass_reversed','mixed_school_glass_reversed','tall_classroom_entrance')

def glazing(layout, low, high, bottom, top, occluders):
    posts=[]
    if layout & 1:posts.append((0,1))
    if layout & 2:posts.append((15,16))
    if layout & 4:posts.append((7,9))
    posts.sort()
    edges=[0]+[v for pair in posts for v in pair]+[16]
    panes=[(edges[i],edges[i+1]) for i in range(0,len(edges)-1,2) if edges[i]<edges[i+1]]
    parts=[([left,low,0,right,high,8],'frame') for left,right in posts]
    if not bottom:parts.append(([0,low,0,16,low+1,8],'frame'))
    if not top:parts.append(([0,high-1,0,16,high,8],'frame'))
    parts += [([left,low if bottom else low+1,0,right,high if top else high-1,8],'glass') for left,right in panes]
    opaque=[e for e in solid_model([p for p in parts if p[1]!='glass']+occluders)['elements']
            if next(iter(e['faces'].values()))['texture']=='#frame']
    glass=[e for e in solid_model(parts+occluders)['elements']
           if next(iter(e['faces'].values()))['texture']=='#glass']
    for e in opaque+glass:
        lo,hi=e['from'],e['to']
        for face,f in e['faces'].items():
            f['uv']={'north':[16-hi[0],16-hi[1],16-lo[0],16-lo[1]],
                     'south':[lo[0],16-hi[1],hi[0],16-lo[1]],
                     'east':[16-hi[2],16-hi[1],16-lo[2],16-lo[1]],
                     'west':[lo[2],16-hi[1],hi[2],16-lo[1]],
                     'up':[lo[0],lo[2],hi[0],hi[2]],
                     'down':[16-hi[0],lo[2],16-lo[0],hi[2]]}[face]
            axis={'west':0,'east':0,'down':1,'up':1,'north':2,'south':2}[face]
            positive=face in ('east','up','south')
            if (hi if positive else lo)[axis]==(16 if positive else 0):f['cullface']=face
    return opaque+glass

def main():
    generated=set()
    for name in IDS:
        path=A/f'blockstates/{name}.json'
        definition=json.loads(path.read_text())
        if 'variants' in definition:original=definition['variants']
        else:
            original={','.join(f'{k}={v}' for k,v in sorted(p['when'].items())):p['apply']
                      for p in definition['multipart'] if 'curtain' not in p.get('when',{})}
        variants={}
        for key,value in original.items():
            state=dict(p.split('=') for p in key.split(','))
            if 'frame_layout' in state and state.pop('frame_layout')!='3':continue
            eligible=name=='school_glass' or state.get('shape')=='straight' or state.get('row')=='2'
            stem=value['model'].split(':')[1].removeprefix('block/')
            for layout in range(8):
                target=stem if layout==3 or not eligible else f'{stem}_horizontal_{layout}'
                if target!=stem and target not in generated:
                    old=json.loads((A/f'models/block/{stem}.json').read_text())
                    preserved=[e for e in old['elements'] if all(f['texture'] not in ('#glass','#frame') for f in e['faces'].values())]
                    bottom=state['bottom_connected']=='true';top=state['top_connected']=='true'
                    if name=='school_glass':low,high,occluders=0,16,[]
                    elif name=='tall_classroom_entrance':
                        low,high=8,16
                        apron=next(e for e in preserved if any(f['texture']=='#top' for f in e['faces'].values()))
                        occluders=[(apron['from']+apron['to'],'occluder')]
                    elif state['half']=='bottom':low,high,occluders=8,16,[([0,0,0,16,8,16],'occluder')]
                    else:low,high,occluders=0,8,[([0,8,0,16,16,16],'occluder')]
                    result=model(preserved+glazing(layout,low,high,bottom,top,occluders))
                    result['textures']=old['textures']
                    write(A/f'models/block/{target}.json',result);generated.add(target)
                states=dict(state,frame_layout=str(layout))
                variants[','.join(f'{k}={v}' for k,v in sorted(states.items()))]=dict(value,model=f'lsmmod:block/{target}')
        write(path,{'variants':variants})

if __name__=='__main__':main()
