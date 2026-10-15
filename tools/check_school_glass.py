"""Static checks only: door profile, stair resources/materials and shared glass joins."""
import json
from pathlib import Path
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

def check_slab_window():
    import itertools,copy
    import numpy as np
    from create_school_projecting_window_assets import SLAB_ID,ID,PANES,parts,rotation
    from create_bathroom_statue_assets import solid_model
    from render_school_projecting_window import slab_scene,slab_item
    from render_texture_review import add,render
    from create_paintable_school_block import PAINTS
    from check_custom_paintable_blocks import visible
    from audit_block_states import audit
    row=next(r for r in audit() if r['id']==SLAB_ID)
    assert row['domains']['type']==3 and row['domains']['waterlogged']==2
    for kind,mask in [('bottom',0x33),('top',0xcc),('double',0xff)]:
        for facing,opened in itertools.product(('north','east','south','west'),(False,True)):
            elements=slab_scene(opened,facing,paint=15,kind=kind)
            slab=[e for e in elements if any(s['texture']==PAINTS[15] for s in e['faces'].values())]
            expected={(cube,face) for cube in range(8) for face in ('down','up','north','south','west','east') if visible(mask,cube,face)}
            actual={(int(e['from'][0]/8)+2*int(e['from'][1]/8)+4*int(e['from'][2]/8),next(iter(e['faces']))) for e in slab}
            assert actual==expected,(kind,facing)
            pane=[e for e in elements if not any(s['texture']==PAINTS[15] for s in e['faces'].values())]
            assert bool(pane)==(kind!='double')
            for e in pane:
                assert bool(e.get('rotation'))==opened
                assert (e['from'][1]>=8 if kind=='bottom' else e['to'][1]<=8)
                axis=2 if facing in ('north','south') else 0
                assert e['to'][axis]-e['from'][axis]==2
    # A slab boundary plus normal windows reconstruct the very same rigid 24px
    # sash, including a single shared top hinge and no extra seam frame.
    for kind,start_y in [('bottom',8),('top',0)]:
        for opened in (False,True):
            assembled=[];expected=[]
            for y in range(2):
                for x in range(2):
                    slab_cell=y==(0 if kind=='bottom' else 1)
                    phase=x+3*(y+(1 if kind=='bottom' else 0))
                    for index,pane in enumerate(parts(phase)):
                        if pane[4]+x*16!=0 or pane[5]+y*16!=start_y:continue
                        for layer in ('solid','translucent'):
                            stem=f'{SLAB_ID}/phase_{phase}_{kind}' if slab_cell else f'{ID}/phase_{phase}'
                            path=A/f'models/block/{stem}_part_{index}_{layer}_{int(opened)}.json'
                            if path.exists():add(assembled,[read(path)],(x*16,y*16,0))
            for layer,boxes in PANES.items():
                model=solid_model(boxes);model.update(render_type='minecraft:'+layer,
                    textures={'frame':'lsmmod:block/'+ID+'_frame','glass':'lsmmod:block/'+ID+'_glass'})
                for e in model['elements']:
                    if opened:e['rotation']=rotation((0,24,0,24,0,0),1)
                add(expected,[model],(0,start_y,0))
            for angle in (-65,115):
                one=np.array(render(assembled,[12,start_y+12,16],8,size=(320,360),azimuth=angle)).astype(int)
                two=np.array(render(expected,[12,start_y+12,16],8,size=(320,360),azimuth=angle)).astype(int)
                assert np.mean(np.abs(one-two))<.2,(kind,opened,angle,np.mean(np.abs(one-two)))
    icon=slab_item();assert icon and any(e['to']==[16,8,16] for e in icon)
    loot=read(D/f'lsmmod/loot_table/blocks/{SLAB_ID}.json')
    fn=loot['pools'][0]['entries'][0]['functions'][0]
    assert fn['count']==2 and fn['conditions'][0]['properties']=={'type':'double'}
    check_layout_regeneration()
    print('OK: bottom/top/double slab masks, 24px sash equivalence with normal windows, all facings, shared hinges, item and double drop; added-layout regeneration and stable fallback.')


def check_layout_regeneration():
    import tempfile,shutil,hashlib
    from create_school_projecting_window_assets import prepare_layouts,ID,layout_key,layout_parts
    original=(A/'projecting_window_layouts.json').read_bytes()
    with tempfile.TemporaryDirectory() as directory:
        assets=Path(directory)/'lsmmod';assets.mkdir()
        # Copy only the generator inputs: legacy roots, wall roots and the table.
        for ident in (ID,'school_paintable_projecting_window','school_paintable_projecting_window_slab','school_paintable_projecting_window_stairs'):
            for p in (A/'models/block'/ident).glob('*.json'):
                target=assets/p.relative_to(A);target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,target)
        shutil.copyfile(A/f'models/block/{ID}.json',assets/f'models/block/{ID}.json')
        rules=json.loads(original);added={'width':3,'height':2.5,'columns':2,'rows':1}
        rules.append(added);table=assets/'projecting_window_layouts.json';table.write_text(json.dumps(rules))
        prepare_layouts(assets)
        baked=read(assets/'projecting_window_baked_layouts.json');assert added in baked
        key=layout_key(added,0,0,0)
        root=read(assets/f'models/block/{ID}.json')
        assert any(e.get('condition',{}).get('key')==key for e in root['models'][0])
        assert read(assets/f'models/block/{ID}/layouts/{key}.json')['models']
        import math
        for start in (0,8):
            for sy in (0,8):
                area=0
                for y in range(math.ceil((sy+added['height']*16)/16)):
                    for x in range(math.ceil((start+added['width']*16)/16)):
                        cell_key=layout_key(added,start,x,y,sy)
                        value=read(assets/f'models/block/{ID}/layouts/{cell_key}.json')
                        assert value['models']
                        for group in value['models']:
                            for entry in group:
                                leaf=read(assets/('models/'+entry['model'].split(':')[1]+'.json'))
                                assert leaf['elements']
                        area+=sum((p[1]-p[0])*(p[3]-p[2]) for p in layout_parts(added,start,x,y,sy))
                assert abs(area-added['width']*added['height']*256)<1e-6
        geometry=(assets/f'models/block/{ID}/layouts/{key}_part_0_solid_0.json').read_bytes()
        legacy=assets/f'models/block/{ID}/legacy.json';before=legacy.read_bytes()
        prepare_layouts(assets);assert legacy.read_bytes()==before,'Regeneration must not wrap legacy into a self-reference'
        rules[-1]={'width':3,'height':2.5,'columns':1,'rows':2};table.write_text(json.dumps(rules));prepare_layouts(assets)
        assert rules[-1] in read(assets/'projecting_window_baked_layouts.json')
        assert (assets/f'models/block/{ID}/layouts/{key}_part_0_solid_0.json').read_bytes()!=geometry
        rules.pop();table.write_text(json.dumps(rules));prepare_layouts(assets)
        assert not (assets/f'models/block/{ID}/layouts/{key}.json').exists()
    assert (A/'projecting_window_layouts.json').read_bytes()==original


def check_stair_window():
    import itertools,math
    import numpy as np
    from create_school_projecting_window_assets import STAIR_ID,PANES,parts,rotation,layout_rules,layout_key
    from create_bathroom_statue_assets import solid_model
    from render_school_projecting_window import stair_scene,stair_mask,stair_assembly
    from render_texture_review import add,load,render
    from create_paintable_school_block import PAINTS
    from check_custom_paintable_blocks import visible
    for half,left,facing in itertools.product(('bottom','top'),(False,True),('north','east','south','west')):
        mask=stair_mask(facing,half,left);assert mask.bit_count()==6
        scene=stair_scene(facing=facing,paint=15,half=half,left=left)
        solids=[e for e in scene if any(f['texture']==PAINTS[15] for f in e['faces'].values())]
        expected={(cube,face) for cube in range(8) for face in ('down','up','north','south','west','east') if visible(mask,cube,face)}
        actual={(int(e['from'][0]/8)+2*int(e['from'][1]/8)+4*int(e['from'][2]/8),next(iter(e['faces']))) for e in solids}
        assert actual==expected,(half,left,facing)
    for half,left in itertools.product(('bottom','top'),(False,True)):
        lx,hx=(8,16) if left else (0,8);ly,hy=(8,16) if half=='bottom' else (0,8)
        profile=half+'_'+('left' if left else 'right')
        for phase in range(9):
            covered=set()
            for index,_ in enumerate(parts(phase)):
                for layer in ('solid','translucent'):
                    path=A/f'models/block/{STAIR_ID}/phase_{phase}_{profile}_part_{index}_{layer}_0.json'
                    if not path.exists():continue
                    for e in read(path)['elements']:
                        assert lx<=e['from'][0]<e['to'][0]<=hx and ly<=e['from'][1]<e['to'][1]<=hy
                        assert e['from'][2]==14 and e['to'][2]==16
                        for px,py in itertools.product(range(16),repeat=2):
                            if e['from'][0]<=px+.5<e['to'][0] and e['from'][1]<=py+.5<e['to'][1]:covered.add((px,py))
                        for surface in e['faces'].values():assert all(0<=uv<=16 for uv in surface['uv'])
            assert covered==set(itertools.product(range(lx,hx),range(ly,hy))),(profile,phase,covered)
        for opened in (False,True):
            assembled=stair_assembly(opened,half,left,paint=15)
            assembled=[e for e in assembled if all(f['texture']!=PAINTS[15] for f in e['faces'].values())]
            expected=[]
            for layer,boxes in PANES.items():
                model=solid_model(boxes);model.update(render_type='minecraft:'+layer,
                    textures={'frame':'lsmmod:block/school_projecting_window_frame','glass':'lsmmod:block/school_projecting_window_glass'})
                for e in model['elements']:
                    if opened:e['rotation']=rotation((0,24,0,24,0,0),1)
                add(expected,[model],(lx,ly,0))
            for angle in (-65,115):
                one=np.array(render(assembled,[lx+12,ly+12,16],8,size=(320,360),azimuth=angle)).astype(int)
                two=np.array(render(expected,[lx+12,ly+12,16],8,size=(320,360),azimuth=angle)).astype(int)
                assert np.mean(np.abs(one-two))<.2,(profile,opened,angle,np.mean(np.abs(one-two)))
    # Every adaptive corner points to real, clipped models, including layouts
    # with fractional block width and the top/bottom offsets introduced by slabs.
    for half,side in itertools.product(('bottom','top'),('left','right')):
        root=read(A/f'models/block/{STAIR_ID}/{half}_{side}.json')
        entries=root['models'][0];assert entries[0]['condition']['key']=='fallback'
        assert len(entries)>1
        for entry in entries[1:]:
            groups=read(A/('models/'+entry['model'].split(':')[1]+'.json'))['models'];assert groups
            for group in groups:
                for leaf in group:
                    for e in read(A/('models/'+leaf['model'].split(':')[1]+'.json'))['elements']:
                        lx,hx=(8,16) if side=='left' else (0,8);ly,hy=(8,16) if half=='bottom' else (0,8)
                        assert lx<=e['from'][0]<e['to'][0]<=hx and ly<=e['from'][1]<e['to'][1]<=hy
    icon=[];add(icon,load('lsmmod:item/'+STAIR_ID));assert icon and any(e['render_type']=='minecraft:translucent' for e in icon)
    assert read(D/f'lsmmod/loot_table/blocks/{STAIR_ID}.json')['pools'][0]['entries'][0]['name']=='lsmmod:'+STAIR_ID
    print('OK: sixteen L-profile orientations, six solid cubes with exposed 8px paints, all nine shared grid phases, adaptive corner models, whole-sash render equivalence closed/open front/back, item and drop.')


def main():
    names={'school_glass':16,'tall_classroom_entrance':384}
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
    stair=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlassStairBlock.java').read_text()
    assert 'extends StairBlock' in stair and 'super.getStateForPlacement(context)' in stair
    assert 'super.updateShape' in stair and 'return super.getShape' in stair and 'SimpleBlockOutline.forState' in stair
    joins=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlazing.java').read_text()
    assert 'a.footprint()!=b.footprint()' in joins and 'a.high()==16&&reachesBottom(neighbor,b)' in joins
    loot=read(D/'lsmmod/loot_table/blocks/tall_classroom_entrance.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'row':'0'}
    check_paintable_stairs()
    print('OK: edge-aligned 2-row door + moving third-row apron; centered handles; glass selections plus curtain overlays; paintable vanilla stairs (corners/top/waterlogging), shared glazing and single door drop.')

def check_paintable_stairs():
    from create_school_glass_stair_assets import ID,RETIRED
    from check_custom_paintable_blocks import coords,visible
    from render_school_glass import stair_scene,item_scene
    import itertools
    registry=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text()
    assert registry.count('DeferredBlock<SchoolPaintableGlassStairBlock>')==1
    for retired in RETIRED:
        assert retired not in registry
        for directory in ('blockstates','items','models/block','models/item'):
            assert not list((A/directory).glob(retired+'*.json'))
    parts=read(A/f'blockstates/{ID}.json')['multipart']
    assert parts[0]=={'apply':{'model':'lsmmod:block/school_paintable_custom_block'}}
    for half,shape,facing in itertools.product(('bottom','top'),QUARTERS,('north','east','south','west')):
        slab=0x33 if half=='bottom' else 0xcc
        state=dict(half=half,shape=shape,facing=facing,curtain='none')
        base=[p for p in parts if all(state.get(k) in v.split('|') for k,v in p.get('when',{}).items())]
        assert len(base)==2 and base[1]['apply']['y']==('north','east','south','west').index(facing)*90
        for horizontal,vertical in itertools.product(('single','left','middle','right'),repeat=2):
            state['curtain']=horizontal+'_'+vertical
            assert sum(all(state.get(k) in v.split('|') for k,v in p.get('when',{}).items()) for p in parts)==3
        # Independently retain precisely the occupied slab faces, in world coordinates.
        for mask in range(256):
            if mask&~slab:continue
            actual=stair_scene(half,shape,facing,mask,paint=12)
            paint_faces=[e for e in actual if next(iter(e['faces'].values()))['texture']=='lsmmod:block/red_street_wall']
            expected={(cube,face) for cube in range(8) for face in ('down','up','north','south','west','east') if visible(mask,cube,face)}
            found={(int(e['from'][0]/8)+2*int(e['from'][1]/8)+4*int(e['from'][2]/8),next(iter(e['faces']))) for e in paint_faces}
            assert found==expected,(state,mask,found,expected)
            assert all(e['to']==[v+8 for v in e['from']] for e in paint_faces)
        for glass_mask in range(32):
            elements=stair_scene(half,shape,facing,0,mask=glass_mask)
            assert elements
            for e in elements:
                assert all(0<=e['from'][axis]<e['to'][axis]<=16 for axis in range(3))
                assert (e['from'][1]>=8 if half=='bottom' else e['to'][1]<=8)
                assert all(0<=uv<=16 for face in e['faces'].values() for uv in face['uv'])
            if facing=='north':
                for element in elements:
                    for face,surface in element['faces'].items():
                        if surface['texture']!='lsmmod:block/school_glass':continue
                        assert not (face=='west' and element['from'][0]==0 and not glass_mask&1
                            or face=='east' and element['to'][0]==16 and not glass_mask&2
                            or face=='up' and element['to'][1]==16 and glass_mask&8
                            or face=='down' and element['from'][1]==0 and glass_mask&16),(half,shape,glass_mask,face,'internal glass cap')
            # Frame inner faces remain visible through glass, with rails removed only at joins.
            if shape=='straight' and facing=='north':
                for face,x in (('east',1),('west',15)):
                    if not glass_mask&(1 if x==1 else 2):continue
                    assert any(e['faces'].get(face,{}).get('texture')=='lsmmod:block/school_glass_frame' and (e['to'][0] if face=='east' else e['from'][0])==x for e in elements)
    item=item_scene()
    assert any(next(iter(e['faces'].values()))['texture']=='minecraft:block/light_gray_concrete' and e['from']==[0,0,0] and e['to']==[16,8,16] for e in item)
    assert any(e['render_type']=='minecraft:translucent' for e in item)
    source=(ROOT/'src/main/java/net/nicomar2009/lsmmod/block/SchoolGlassStairPaintEntity.java').read_text()
    assert 'extends SchoolPaintableCustomBlockEntity' in source and 'return false;' in source and '0xcc:0x33' in source
    assert read(D/f'lsmmod/loot_table/blocks/{ID}.json')['pools'][0]['entries'][0]['name']=='lsmmod:'+ID
    print('OK: one editable glass stair; all slab masks, corners, facings, glass joins, curtains, item and retired resources checked.')

if __name__=='__main__':
    import sys
    if '--stair-window-only' in sys.argv:
        check_stair_window()
        raise SystemExit(0)
    if '--slab-window-only' in sys.argv:
        check_slab_window()
        raise SystemExit(0)
    if '--paintable-stairs-only' in sys.argv:
        check_paintable_stairs()
        raise SystemExit(0)
    if '--projecting-only' in sys.argv:
        check_projecting_windows()
        raise SystemExit(0)
    main()
