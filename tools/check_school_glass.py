"""Static checks only: door profile, stair resources/materials and shared glass joins."""
import json
from PIL import Image
from create_school_glass_assets import A,D,ROOT
from create_school_glass_stair_assets import QUARTERS

def read(p):return json.loads(p.read_text())

def check_projecting_windows():
    """Compare anchored cell fragments against whole panes with the inverted outward opening."""
    import copy
    import numpy as np
    from create_school_projecting_window_assets import ID, parts
    from render_school_projecting_window import scene
    from render_texture_review import add, load, render
    source=ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolProjectingWindowBlock.java'
    text=source.read_text()
    assert 'extends SchoolGlassBlock' not in text and 'SchoolCurtains.CURTAIN' not in text
    assert 'SimpleBlockOutline.forState' not in text  # Pane selection depends on the world.
    for phase in range(9):
        rectangles=parts(phase)
        assert sum((x1-x0)*(y1-y0) for x0,x1,y0,y1,_,_ in rectangles)==256
        wrapper=read(A/f'models/block/{ID}/phase_{phase}.json')
        for group in wrapper['models']:
            assert sorted(entry['condition']['pose'] for entry in group)==list(range(5))
            for entry in group:
                model=read(A/f"models/{entry['model'].split(':')[1]}.json")
                assert model['elements']
                for e in model['elements']:
                    assert all(0<=e['from'][axis]<e['to'][axis]<=16 for axis in range(3))
                    assert e['from'][2]==14 and e['to'][2]==16
                    rotation=e.get('rotation')
                    assert bool(rotation)==bool(entry['condition']['pose'])
                    if rotation: assert abs(rotation['angle'])==45 and not rotation['rescale']
                    for face in e['faces'].values():assert all(0<=v<=16 for v in face['uv'])
    reference=Image.open(ROOT/'tools/fixtures/projecting_window_tinted_reference.png').convert('RGBA')
    glass=Image.open(A/f'textures/block/{ID}_glass.png').convert('RGBA')
    assert reference.size==glass.size==(16,16)
    for y in range(16):
        for x in range(16):
            a,b=reference.getpixel((x,y)),glass.getpixel((x,y))
            assert b[:3]==tuple(max(0,v-4) for v in a[:3]) and b[3]==a[3]
    item=load('lsmmod:item/'+ID)
    # Reverse the inventory scale to obtain full panes without cell cuts.
    whole=[]
    for model in item:
        model=copy.deepcopy(model)
        for e in model['elements']:
            for key in ('from','to'):
                x,y,z=e[key];e[key]=[x*1.5,y*1.5,8+(z-8)*1.5]
        add(whole,[model])
    for poses in ({}, {(0,0):3}, {(0,0):2,(1,0):4,(0,1):3,(1,1):1}):
        expected=[]
        for ty in range(2):
            for tx in range(2):
                pose=poses.get((tx,ty),0)
                for original in whole:
                    e=copy.deepcopy(original)
                    for key in ('from','to'):
                        e[key][0]+=tx*24;e[key][1]+=ty*24
                    if pose:e['rotation']={'origin':[tx*24+(24 if pose==4 else 0),ty*24+(24 if pose==1 else 0),16],
                                          'axis':'x' if pose<=2 else 'y','angle':-45 if pose in (1,3) else 45,'rescale':False}
                    expected.append(e)
        assembled=scene(poses)
        for angle in (-65,115):
            a=np.asarray(render(assembled,[24,24,3],5,size=(320,360),azimuth=angle,elevation=12)).astype(int)
            b=np.asarray(render(expected,[24,24,3],5,size=(320,360),azimuth=angle,elevation=12)).astype(int)
            # Triangle rasterization can differ by a pixel at cuts; geometry/materials must agree.
            delta=np.max(np.abs(a-b),axis=2)
            assert np.count_nonzero(delta>3)<100 and np.mean(delta)<.12,(poses,angle,np.mean(delta))
    # Mixed and full cells must reconstruct the same complete 24px sash, without an extra frame at their seam.
    from create_school_projecting_window_assets import PAINTABLE_ID
    for side in ('left','right'):
        shift=8 if side=='left' else 0
        for pose in range(5):
            assembled=[];expected=[]
            for y in range(2):
                for x in range(2):
                    mixed=x==(0 if side=='left' else 1)
                    phase=(x+1 if side=='left' else x)+3*y
                    for index,part in enumerate(parts(phase)):
                        if y*16+part[-1]!=0:continue
                        for layer in ('solid','translucent'):
                            stem=f'{PAINTABLE_ID}/phase_{phase}_{side}_part_{index}' if mixed else f'{ID}/phase_{phase}_part_{index}'
                            path=A/f'models/block/{stem}_{layer}_{pose}.json'
                            if path.exists():add(assembled,[json.loads(path.read_text())],(x*16,y*16,0))
            for original in whole:
                e=copy.deepcopy(original)
                for key in ('from','to'):e[key][0]+=shift
                if pose:e['rotation']={'origin':[shift+(24 if pose==4 else 0),24 if pose==1 else 0,16],
                                      'axis':'x' if pose<=2 else 'y','angle':-45 if pose in (1,3) else 45,'rescale':False}
                expected.append(e)
            for angle in (-65,115):
                a=np.asarray(render(assembled,[16,12,16],7,size=(320,360),azimuth=angle,elevation=12)).astype(int)
                b=np.asarray(render(expected,[16,12,16],7,size=(320,360),azimuth=angle,elevation=12)).astype(int)
                delta=np.max(np.abs(a-b),axis=2)
                assert np.count_nonzero(delta>3)<100 and np.mean(delta)<.12,('mixed',side,pose,angle,np.mean(delta))
    check_projecting_layouts()
    print('OK: placement-anchored 24x24 panes, rigid reversed 45-degree hinges at the far edge; two-pixel thickness, preserved UVs/alpha and no curtains. Cell renders agree with whole-pane renders, front and back.')



def check_projecting_layouts():
    import math,copy
    import numpy as np
    from create_school_projecting_window_assets import ID,PAINTABLE_ID,layout_rules,layout_key,layout_parts,sized_pane_models
    from render_school_projecting_window import layout_scene
    from render_texture_review import add,render
    rules=layout_rules()
    assert rules, "The layout table must contain at least one opening"
    for rule in rules:
        width,height=rule['width']*16,rule['height']*16
        pw,ph=width/rule['columns'],height/rule['rows'];base=sized_pane_models(pw,ph)
        for start in (0,8):
            area=0;controllers=set();tiles=set()
            for y in range(int(height)//16):
                for x in range(math.ceil((start+width)/16)):
                    key=layout_key(rule,start,x,y)
                    root=read(A/f'models/block/{ID}/layouts/{key}.json')
                    for part in layout_parts(rule,start,x,y):
                        area+=(part[1]-part[0])*(part[3]-part[2]);tiles.add(part[-2:])
                        controllers.add((math.floor((part[4]+x*16)/16),math.floor((part[5]+y*16)/16)))
                    for series in root['models']:
                        assert {e['condition']['pose'] for e in series}=={0,1}
                        for entry in series:
                            leaf=read(A/f'models/{entry["model"].split(":")[1]}.json')
                            for element in leaf['elements']:
                                assert all(-1e-8<=element['from'][axis]<element['to'][axis]<=16+1e-8 for axis in range(3))
                                assert element['from'][2]==14 and element['to'][2]==16
                                if entry['condition']['pose']:
                                    assert element['rotation']['axis']=='x' and element['rotation']['angle']==-45 and element['rotation']['rescale'] is False
                                else:assert 'rotation' not in element
                    for side in ('left','right'):
                        if side=='left' and not (start==8 and x==0):continue
                        if side=='right' and not ((start+width)%16==8 and x==math.ceil((start+width)/16)-1):continue
                        half=read(A/f'models/block/{PAINTABLE_ID}/layouts/{key}_{side}.json')
                        assert half['models']==[[f'lsmmod:block/{PAINTABLE_ID}/wall_{side}'],[f'lsmmod:block/{ID}/layouts/{key}']]
            assert abs(area-width*height)<1e-6
            assert len(tiles)==len(controllers)==rule['columns']*rule['rows'], 'Two panes share an opening controller'
            # All assembled native fragments must match full, independently placed pane models.
            for opened,one in ((False,None),(True,None),(False,(0,0))):
                expected=[]
                for row in range(rule['rows']):
                    for column in range(rule['columns']):
                        ox,oy=start+column*pw,row*ph;pose=opened or one==(column,row)
                        for model in base.values():
                            model=copy.deepcopy(model)
                            if pose:
                                for element in model['elements']:element['rotation']={'origin':[0,ph,16],'axis':'x','angle':-45,'rescale':False}
                            add(expected,[model],(ox,oy,0))
                assembled=layout_scene(rule,start,opened,one,walls=False)
                for angle in (-65,115):
                    center=[start+width/2,height/2,18];scale=min(5,260/width)
                    a=np.asarray(render(assembled,center,scale,size=(360,300),azimuth=angle,elevation=12)).astype(int)
                    b=np.asarray(render(expected,center,scale,size=(360,300),azimuth=angle,elevation=12)).astype(int)
                    delta=np.max(np.abs(a-b),axis=2)
                    assert np.mean(delta)<.2 and np.count_nonzero(delta>3)<160,(rule,start,opened,one,angle,np.mean(delta))
    for ident,path in ((ID,A/f'models/block/{ID}.json'),(PAINTABLE_ID,A/f'models/block/{PAINTABLE_ID}/left.json'),(PAINTABLE_ID,A/f'models/block/{PAINTABLE_ID}/right.json')):
        first=read(path)['models'][0][0]['condition']
        assert first=={'type':'lsmmod:projecting_window_layout','key':'fallback','refresh':True}
    print(f'OK: {len(rules)} listed layouts, uniform full coverage, independent controllers, shared half panes; whole-pane renders match all cell fragments closed, open and independently opened, front/back.')

def main():
    names={'school_glass':16,'supported_school_glass':320,'mixed_school_glass':320,'supported_school_glass_reversed':320,'mixed_school_glass_reversed':320,'tall_classroom_entrance':384}
    for name,count in names.items():
        definition=read(A/f'blockstates/{name}.json')
        variants=definition.get('variants')
        if name=='tall_classroom_entrance':
            from compact_school_door_assets import expand_variants
            variants=expand_variants(definition)
        if variants is None:
            variants={','.join(f'{k}={v}' for k,v in p['when'].items()):p['apply'] for p in definition['multipart'] if 'curtain' not in p['when']}
        expanded={}
        for key,value in variants.items():
            data=read(A/('models/'+value['model'].split(':')[1]+'.json'))
            if data.get('type')=='composite':
                for entry in data['models'][0]:
                    mask=entry['condition']['mask']
                    suffix=f',frame_layout={mask&7},top_connected={str(bool(mask&8)).lower()},bottom_connected={str(bool(mask&16)).lower()}'
                    expanded[key+suffix]=dict(value,model=entry['model'])
            else:expanded[key]=value
        variants=expanded
        assert len(variants)==count*8
        for key,value in variants.items():
            state=dict(x.split('=') for x in key.split(','))
            model=read(A/('models/'+value['model'].split(':')[1]+'.json'))
            for e in model['elements']:
                assert all(0<=e['from'][a]<e['to'][a]<=16 for a in range(3))
                for f in e['faces'].values():assert f['texture'][1:] in model['textures']
            if 'school_glass' in name and name!='school_glass':
                assert state['half'] in ('top','bottom') and state['shape'] in QUARTERS
                assert state['waterlogged'] in ('true','false')
            if name=='tall_classroom_entrance' and state['row']=='2':
                assert 'tall_classroom_transom' in value['model']
    for col in range(2):
        for row in range(2):
            model=read(A/f'models/block/tall_classroom_entrance_{col}_{row}.json')
            assert len(model['elements'])==1
            assert model['elements'][0]['from']==[0,0,0] and model['elements'][0]['to']==[16,16,4]
            closed=model['elements'][0]
            opened=read(A/f'models/block/tall_classroom_entrance_{col}_{row}_open.json')['elements'][0]
            assert opened['from'][0]==(0 if col==0 else 12) and opened['to'][0]==(4 if col==0 else 16)
            # Original handle's x13.5 maps toward the center on both sides.
            for face in ('north','south'):
                uv=closed['faces'][face]['uv'];t=(13.5-uv[0])/(uv[2]-uv[0]);x=16*(1-t) if face=='north' else 16*t
                assert (x>8) if col==0 else (x<8)
    for col in range(2):
        for top in (0,1):
            for opened in (0,1):
                model=read(A/f'models/block/tall_classroom_transom_{col}_{opened}_{top}_0.json')
                apron=next(e for e in model['elements'] if e['faces'].get('north',{}).get('texture')=='#top' or any(f['texture']=='#top' for f in e['faces'].values()))
                assert apron['from']==([0,0,0] if not opened or col==0 else [12,0,0])
                assert apron['to']==([16,8,4] if not opened else [4,8,16] if col==0 else [16,8,16])
                glass=next(e for e in model['elements'] if e['faces']['north']['texture']=='#glass')
                assert glass['from'][1]==9 and glass['to'][1]==(16 if top else 15)
                assert glass['to'][2]-glass['from'][2]==8
    for name,under,side in [('supported_school_glass','light_wall','support'),('mixed_school_glass','concrete','light_wall'),('supported_school_glass_reversed','support','light_wall'),('mixed_school_glass_reversed','light_wall','concrete')]:
        model=read(A/f'models/block/{name}.json')
        assert model['textures']['light_wall']=='lsmmod:block/'+('street_school_wall' if name in ('supported_school_glass','mixed_school_glass_reversed') else 'light_school_wall')
        assert model['textures']['support']=='lsmmod:block/dark_school_wall'
        assert model['textures']['concrete']=='lsmmod:block/classroom_white_wall'
        assert any(f['texture']=='#'+under for e in model['elements'] for f in e['faces'].values())
        assert any(f['texture']=='#'+side for e in model['elements'] for f in e['faces'].values())
        for shape,quarters in QUARTERS.items():
            # Expected physical volumes from vanilla's half slab plus quarter-step arrangement.
            assert 2048+512*len(quarters)=={'straight':3072,'outer_left':2560,'outer_right':2560,'inner_left':3584,'inner_right':3584}[shape]
    for original,reversed_id in [('supported_school_glass','supported_school_glass_reversed'),('mixed_school_glass','mixed_school_glass_reversed')]:
        for half in ('bottom','top'):
            for shape in QUARTERS:
                for top in (0,1):
                    for bottom in (0,1):
                        regular=read(A/f'models/block/{original}_{half}_{shape}_{top}_{bottom}.json')
                        swapped=read(A/f'models/block/{reversed_id}_{half}_{shape}_{top}_{bottom}.json')
                        def glass_frame(m):
                            return sorted(json.dumps(e,sort_keys=True) for e in m['elements'] if any(f['texture'] in ('#glass','#frame') for f in e['faces'].values()))
                        assert glass_frame(regular)==glass_frame(swapped)
                        assert regular['textures']['glass']=='lsmmod:block/school_glass'
        # Joined straight support meets the standalone glass with the exact same physical posts.
        joined=read(A/f'models/block/{original}_bottom_straight_1_0.json')
        glass_faces=[e for e in joined['elements'] if any(f['texture']=='#glass' for f in e['faces'].values())]
        assert max(e['to'][1] for e in glass_faces)==16
        assert min(e['from'][0] for e in glass_faces)==1 and max(e['to'][0] for e in glass_faces)==15
        inverted=read(A/f'models/block/{original}_top_straight_0_1.json')
        glass_faces=[e for e in inverted['elements'] if any(f['texture']=='#glass' for f in e['faces'].values())]
        assert min(e['from'][1] for e in glass_faces)==0
    stair=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlassStairBlock.java').read_text()
    assert 'extends StairBlock' in stair and 'super.getStateForPlacement(context)' in stair
    assert 'super.updateShape' in stair and 'return super.getShape' in stair and 'SimpleBlockOutline.forState' in stair
    joins=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlazing.java').read_text()
    assert 'a.footprint()!=b.footprint()' in joins and 'a.high()==16&&reachesBottom(neighbor,b)' in joins
    loot=read(D/'lsmmod/loot_table/blocks/tall_classroom_entrance.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'row':'0'}
    print('OK: edge-aligned 2-row door + moving third-row apron; centered handles; 13440 geometry selections plus curtain overlays; four vanilla stairs (corners/top/waterlogging), material layout, shared glazing and single door drop.')

if __name__=='__main__':
    import sys
    if '--projecting-only' in sys.argv:
        check_projecting_windows()
        raise SystemExit(0)
    main()

# Regression: transparent panes must not remove the inner faces of opaque posts.
for name in ('supported_school_glass','mixed_school_glass','supported_school_glass_reversed','mixed_school_glass_reversed'):
    for half in ('bottom','top'):
        for top in (0,1):
            for bottom in (0,1):
                m=json.loads((A/f'models/block/{name}_{half}_straight_{top}_{bottom}.json').read_text())
                y0,y1=(9,15) if half=='bottom' else (1,7)
                for face,x in (('east',1),('west',15)):
                    inner=[e for e in m['elements'] if face in e['faces'] and e['faces'][face]['texture']=='#frame'
                           and (e['to'][0] if face=='east' else e['from'][0])==x]
                    assert any(e['from'][1]<=y0 and e['to'][1]>=y1 and e['from'][2]==0 and e['to'][2]==8 for e in inner),(name,half,face)
    # Normal stair, standalone pane, inverted stair: no transverse frame at either seam.
    for half,top,bottom,seam,face in (('bottom',1,0,16,'up'),('top',0,1,0,'down')):
        m=json.loads((A/f'models/block/{name}_{half}_straight_{top}_{bottom}.json').read_text())
        for e in m['elements']:
            if face in e['faces'] and e['faces'][face]['texture']=='#frame' and (e['to'][1] if face=='up' else e['from'][1])==seam:
                # Only the outer side posts can terminate at the connected boundary.
                assert e['to'][0]<=1 or e['from'][0]>=15,(name,half,e)
print('OK: visible inner frame faces retained through transparent glass in all four stairs, both halves and vertical joins.')
