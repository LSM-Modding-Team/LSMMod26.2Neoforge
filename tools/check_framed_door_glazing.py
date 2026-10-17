"""Verify connected half-height transoms, single-pane rendering and material-only variants."""
from resource_modules import module_is_file, module_read_text
import itertools
import json
from create_school_glass_assets import A, ROOT
from check_facility_expansion_assets import resolved, matches
from audit_block_states import audit


def read(path):
    return json.loads(module_read_text(path))

def check_paintable_entrance():
    import numpy as np
    from create_paintable_bathroom_door import ENTRANCE_ID,ID,entrance_leaf
    from create_double_school_entrance import wood,model
    from render_paintable_bathroom_door import scene,frame_cells,item_scene
    from render_texture_review import add,render,load
    from create_paintable_school_block import PAINTS
    original=model([e for e in wood(False) if e['to'][0]<=24])
    leaf=entrance_leaf()
    assert leaf['elements']==original['elements'] and leaf['render_type']==original['render_type']
    for key in original['textures']:assert leaf['textures'][key]==original['textures'][key] or key=='particle'
    assert max(e['to'][0] for e in leaf['elements'])==24 and max(e['to'][1] for e in leaf['elements'])==40
    states={row['id']:row for row in audit()};assert states[ENTRANCE_ID]['states']==192
    parts=read(A/f'blockstates/{ENTRANCE_ID}.json')['multipart']
    for facing,opened,hinge in itertools.product(('north','east','south','west'),('false','true'),('left','right')):
        for column,row,depth in itertools.product(range(2),range(3),range(2)):
            state=dict(facing=facing,open=opened,hinge=hinge,column=str(column),row=str(row),depth=str(depth))
            selected=[entry for entry in parts if matches(entry['when'],state)]
            assert sum(entry['apply']['model']==f'lsmmod:block/{ID}_frame' for entry in selected)==(depth==0)
            assert not any('framed_door_transom' in entry['apply']['model'] for entry in selected)
            assert all(entry['apply'].get('y',0)==('north','east','south','west').index(facing)*90 for entry in selected)
    for right in (False,True):
        cells=frame_cells(right)
        assert len(cells)==144
        header={cell for cell in cells if cell[1]>=10}
        assert header==set(itertools.product(range(8),range(10,12),range(4)))
        for opened in (False,True):
            actual=scene(opened,right,leaf_only=True,ident=ENTRANCE_ID);whole=[];add(whole,[entrance_leaf(opened,right)])
            for angle in (-90,-65,115):
                options=dict(center=[16,20,10],scale=8,size=(420,460),azimuth=angle,elevation=12,minecraft_orientation=True)
                delta=np.max(np.abs(np.asarray(render(actual,**options)).astype(int)-np.asarray(render(whole,**options)).astype(int)),axis=2)
                assert delta.mean()<.2 and np.count_nonzero(delta>3)<150,('Leaf UV/cell seam',opened,right,angle,delta.mean())
            geometry=scene(opened,right,paint=9,ident=ENTRANCE_ID)
            painted_header=[e for e in geometry if e['from'][1]>=40 and any(s['texture']==PAINTS[9] for s in e['faces'].values())]
            assert painted_header and max(e['to'][2] for e in painted_header)==16
            glass=[e for e in geometry if any(s['texture']=='lsmmod:block/school_glass' for s in e['faces'].values())]
            assert not glass
    item=item_scene(ENTRANCE_ID)
    assert any(any(s['texture']==PAINTS[0] for s in e['faces'].values()) for e in item)
    assert not any(any(s['texture']=='lsmmod:block/school_glass' for s in e['faces'].values()) for e in item)
    assert any(any('double_school_entrance_' in s['texture'] for s in e['faces'].values()) for e in item)
    loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ENTRANCE_ID}.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','row':'0','depth':'0'}
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    assert 'hasTransomGlazing() {return true;}' not in module_read_text(java/'modules/painting_and_editable_blocks/block/SchoolPaintableEntranceDoorBlock.java')
    assert 'hasTransomGlazing() { return false; }' in module_read_text(java/'modules/painting_and_editable_blocks/block/SchoolPaintableBathroomDoorBlock.java')
    assert 'ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE.get()' in module_read_text(java/'registry/ModBlockEntities.java')
    print('OK: unchanged entrance leaf/UV, 2x3 assembly and 192 states; solid paintable 32x8x16px header and shared 8px editing, no upper glass, both hinges/orientations, full item frame and single drop. Minecraft interactions remain uncompiled.')


def check_paintable_bathroom():
    import random
    from create_paintable_bathroom_door import ID,native_leaf
    from create_paintable_school_block import PAINTS,SIDES
    from render_texture_review import load,add
    from render_paintable_bathroom_door import scene,frame_cells,item_scene
    parts=read(A/f'blockstates/{ID}.json')['multipart']
    assert {row['id']:row for row in audit()}[ID]['states']==192
    original=read(A/'blockstates/bathroom_door_wide.json')['multipart']
    for facing,opened,hinge in itertools.product(('north','east','south','west'),('false','true'),('left','right')):
        for column,row,depth in itertools.product(range(2),range(3),range(2)):
            state=dict(facing=facing,open=opened,hinge=hinge,column=str(column),row=str(row),depth=str(depth))
            def leaves(entries,old=False):
                result=[]
                for entry in entries:
                    if not matches(entry['when'],state):continue
                    if entry['apply']['model'].endswith('_frame') or 'framed_door_transom' in entry['apply']['model']:continue
                    for model in load(entry['apply']['model']):
                        if old:model=native_leaf(model)
                        assert entry['apply'].get('y',0)==('north','east','south','west').index(facing)*90
                        add(result,[model])
                return result
            assert leaves(parts)==leaves(original,True),(state,'leaf geometry/UV/material changed')
            assert sum(entry['apply']['model'].endswith('_frame') for entry in parts if matches(entry['when'],state))==(depth==0)
    assert not any('transom' in entry['apply']['model'] for entry in parts)

    frame=read(A/f'models/block/{ID}_frame.json');groups=frame['models'][:-1]
    assert len(groups)==64*6 and frame['models'][-1]==[{'model':'lsmmod:block/custom_paintable_empty'}]
    faces=set()
    for group in groups:
        assert len(group)==len(PAINTS)
        first=group[0]['condition'];cube,face=first['cube'],first['face'];faces.add((cube,face))
        for paint,entry in enumerate(group):
            assert entry['condition']==dict(type='lsmmod:painted_door_frame',cube=cube,face=face,paint=paint)
            model,=load(entry['model']);element,=model['elements']
            assert element['from']==[(cube&3)*4,((cube>>2)&3)*4,((cube>>4)&3)*4]
            assert element['to']==[v+4 for v in element['from']]
            assert list(element['faces'])==[face] and all(0<=v<=16 for v in element['faces'][face]['uv'])
            assert model['textures']['surface']==PAINTS[paint]
            if paint:assert 'elements' not in read(A/f"models/{entry['model'].split(':')[1]}.json")
    assert faces==set(itertools.product(range(64),SIDES))
    for opened in (False,True):
        assert scene(opened,removed=frame_cells())==scene(opened,leaf_only=True)
    for right in (False,True):
        cells=frame_cells(right)
        assert len(cells)==144
        assert all(0<=x<8 and 0<=y<12 and 0<=z<4 for x,y,z in cells)
        assert all(y>=10 or (x<2 if right else x>=6) for x,y,z in cells)
        # Compare final selected faces with the surface area of a whole solid L-shaped frame.
        geometry=scene(right=right,paint=9)
        frame_faces=[e for e in geometry if any(f['texture']==PAINTS[9] for f in e['faces'].values())]
        # 144 cubes of 4px, fixed volume = 8*40*16 + 32*8*16.
        assert len(cells)*64==8*40*16+32*8*16
        from create_custom_paintable_blocks import STEPS
        expected_faces=sum(tuple(p+s for p,s in zip(cell,step)) not in cells for cell in cells for step in STEPS.values())
        assert len(frame_faces)==expected_faces,'Internal caps or missing frame faces'
        for cell in cells:
            group={tuple((v//2)*2+d for v,d in zip(cell,offset)) for offset in itertools.product(range(2),repeat=3)}
            carved=frame_cells(right,group);assert len(carved)==len(cells)-8 and not group&carved

    item=item_scene();assert item and all(0<=v<=16 for e in item for key in ('from','to') for v in e[key])
    loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ID}.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','row':'0','depth':'0'}
    assert loot['pools'][0]['entries'][0]['name']=='lsmmod:'+ID
    rng=random.Random(413)
    for _ in range(1000):
        colors=[rng.randrange(len(PAINTS)) for _ in range(96)]
        packed=[sum(colors[face*16+q]<<(q*4) for q in range(16)) for face in range(6)]
        assert colors==[(packed[face]>>(q*4))&15 for face in range(6) for q in range(16)]
        mask=rng.getrandbits(64);signed=mask if mask<2**63 else mask-2**64
        assert signed&((1<<64)-1)==mask
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    entity=module_read_text(java/'modules/painting_and_editable_blocks/block/SchoolDoorFramePaintEntity.java')
    block=module_read_text(java/'modules/painting_and_editable_blocks/block/SchoolPaintableBathroomDoorBlock.java')
    assert 'extends SchoolFramedDiningDoorBlock implements EntityBlock' in block
    assert 'hasTransomGlazing() { return false; }' in block and 'SimpleBlockOutline.forState' not in block
    assert 'removed|=1L<<cube' in entity and 'level.removeBlock' not in entity
    assert 'miniSize() {return 8;}' in entity and 'squareGroup(index)' in entity
    assert 'invalidateClientGeometry()' in entity and 'getLongOr("removed",0)' in entity
    print('OK: original leaf in all 192 states; 2x3 frame, 8px edit groups, all current materials, hidden seams, item/drop and unchanged fine storage for legacy paint/cuts. Java interactions remain uncompiled and untested in Minecraft.')


def check_paintable_column():
    from create_paintable_bathroom_door import COLUMN_ID
    from create_paintable_school_block import PAINTS,SIDES
    from render_texture_review import load,add
    from render_paintable_bathroom_door import column_scene,item_scene
    from create_kiosk_entrance_assets import rotated
    original=read(A/'blockstates/bathroom_door_wide.json')['multipart']
    parts=read(A/f'blockstates/{COLUMN_ID}.json')['multipart']
    assert {row['id']:row for row in audit()}[COLUMN_ID]['states']==192
    def selected(entries,state,color=0):
        result=[]
        for entry in entries:
            if not matches(entry['when'],state):continue
            def predicate(condition):
                if condition['type']=='lsmmod:glazing_mask':return 3 in condition.get('masks',[condition.get('mask')])
                assert condition['type']=='lsmmod:painted_face' and 0<=condition['quadrant']<4
                return condition['paint']==color
            for model in load(entry['apply']['model'],predicate=predicate):
                for _ in range(3*(entry['apply'].get('y',0)//90)):model=rotated(model)
                add(result,[model])
        return result
    def canonical(elements):return sorted(json.dumps(e,sort_keys=True) for e in elements)
    for facing,opened,hinge,column,row,depth in itertools.product(('north','east','south','west'),('false','true'),('left','right'),range(2),range(3),range(2)):
        state=dict(facing=facing,open=opened,hinge=hinge,column=str(column),row=str(row),depth=str(depth))
        old=selected(original,state);new=selected(parts,state)
        is_wall=lambda surface:surface['texture'] in ('lsmmod:block/classroom_white_wall','lsmmod:block/light_school_wall')
        unchanged=lambda elements:[dict(e,faces={f:v for f,v in e['faces'].items() if not is_wall(v)}) for e in elements if any(not is_wall(v) for v in e['faces'].values())]
        assert canonical(unchanged(old))==canonical([e for e in new if not any(v['texture']==PAINTS[0] for v in e['faces'].values())]),(state,'leaf or transom changed')
        # Each old column face is tiled completely without overlaps, with continuous original UVs.
        from create_bathroom_statue_assets import crop_face_uv
        for element in old:
            for face,surface in element['faces'].items():
                if not is_wall(surface):continue
                axis=0 if face in ('west','east') else 1 if face in ('up','down') else 2
                positive=face in ('east','up','south');plane=element['to' if positive else 'from'][axis]
                tiles=[e for e in new if e['faces'].get(face,{}).get('texture')==PAINTS[0] and e['to' if positive else 'from'][axis]==plane]
                tangents=[i for i in range(3) if i!=axis]
                area=lambda e:(e['to'][tangents[0]]-e['from'][tangents[0]])*(e['to'][tangents[1]]-e['from'][tangents[1]])
                assert sum(area(tile) for tile in tiles)==area(element)
                for tile in tiles:
                    assert all(element['from'][i]<=tile['from'][i]<tile['to'][i]<=element['to'][i] for i in range(3))
                    assert all(tile['to'][i]-tile['from'][i]==8 for i in tangents)
                    if facing=='north':assert tile['faces'][face]['uv']==crop_face_uv(surface['uv'],face,element['from'],element['to'],tile['from'],tile['to'])
                for first,second in itertools.combinations(tiles,2):
                    assert not all(max(first['from'][i],second['from'][i])<min(first['to'][i],second['to'][i]) for i in tangents)
        wall=[e for e in new if any(f['texture']==PAINTS[0] for f in e['faces'].values())]
        column_cell=depth==0 and column==(0 if hinge=='right' else 1)
        assert bool(wall)==column_cell,(state,'paintable cell does not match physical column')
        if wall:
            assert len(wall)==(10 if row==2 else 16) and {f for e in wall for f in e['faces']}==set(SIDES)
            for color in range(len(PAINTS)):
                painted=selected(parts,state,color)
                assert sum(any(f['texture']==PAINTS[color] for f in e['faces'].values()) for e in painted)==len(wall)
    # Distinct paints on each world direction and row survive all four rotations and hinges.
    colors={'down':1,'up':2,'north':3,'south':5,'west':6,'east':9}
    for facing,right in itertools.product(('north','east','south','west'),(False,True)):
        painted=column_scene(True,right,paint=lambda row,face,q:colors[face],facing=facing)
        column_faces=[e for e in painted if any(f['texture'] in {PAINTS[c] for c in colors.values()} for f in e['faces'].values())]
        assert len(column_faces)==42
        for element in column_faces:
            face,surface=next(iter(element['faces'].items()));assert surface['texture']==PAINTS[colors[face]],(facing,right,face)
    for facing,right in itertools.product(('north','east','south','west'),(False,True)):
        checker=column_scene(True,right,paint=lambda row,face,q:1+q+4*row,facing=facing)
        for element in checker:
            if not any(v['texture'] in PAINTS[1:] for v in element['faces'].values()):continue
            face,surface=next(iter(element['faces'].items()))
            x,y,z=[int((a+b)//16)%2 for a,b in zip(element['from'],element['to'])]
            q=z+2*y if face in ('west','east') else x+2*z if face in ('up','down') else x+2*y
            row=int(element['from'][1]//16)
            assert surface['texture']==PAINTS[1+q+4*row],(facing,right,face,q)
    item=item_scene(COLUMN_ID)
    assert any(e['render_type']=='minecraft:translucent' for e in item) and any(f['texture']==PAINTS[0] for e in item for f in e['faces'].values())
    assert all(0<=v<=16 for e in item for key in ('from','to') for v in e[key])
    loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{COLUMN_ID}.json')
    assert loot['pools'][0]['conditions'][1]['properties']=={'column':'0','row':'0','depth':'0'}
    assert loot['pools'][0]['entries'][0]['name']=='lsmmod:'+COLUMN_ID
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod'
    entity=module_read_text(java/'modules/painting_and_editable_blocks/block/SchoolBathroomColumnPaintEntity.java')
    block=module_read_text(java/'modules/painting_and_editable_blocks/block/SchoolPaintableBathroomColumnDoorBlock.java')
    assert 'extends SchoolPaintableCustomBlockEntity' in entity and 'loadWholeFaces(input)' in entity and 'else super.loadAdditional(input)' in entity
    assert 'extends SchoolFramedDiningDoorBlock implements EntityBlock' in block and 'hasTransomGlazing' not in block
    assert 'columnCell(state)?new SchoolBathroomColumnPaintEntity(pos,state):null' in block
    rectangle=module_read_text(java/'item/RectanglePaintTool.java')
    assert 'block instanceof SchoolPaintableCustomBlockEntity small' in rectangle
    assert 'end=new End(end.plane,a,a+3,b,b+3)' not in rectangle
    print('OK: 192 states preserve original geometry, leaf/UV and glass; 8x8px independent paint squares, continuous UVs and six-face legacy migration, all materials, both hinges/all orientations, complete item and single drop. Java/Minecraft remain uncompiled.')


def check_extended_doors():
    from create_extended_school_doors import DOUBLE,BATHROOM,bathroom_parts
    from render_extended_school_doors import scene,item
    from create_paintable_school_block import PAINTS
    from create_double_school_entrance import wood,model
    from render_texture_review import add,load,render
    from render_block_review import framing
    import numpy as np
    counts={entry['id']:entry['states'] for entry in audit()}
    assert counts[DOUBLE]==288 and counts[BATHROOM]==256
    assert counts['bathroom_door_wide']==192 and counts['double_school_entrance']==288
    for ident in (DOUBLE,BATHROOM):
        definition=read(A/f'blockstates/{ident}.json')['multipart']
        for entry in definition:
            path=A/f"models/{entry['apply']['model'].split(':')[1]}.json"
            assert module_is_file(path)
            for m in load(entry['apply']['model']):
                for e in m.get('elements',[]):
                    assert all(0<=e['from'][axis]<e['to'][axis]<=16 for axis in range(3)),(ident,e)
                    assert e['faces']
        loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json')['pools'][0]
        assert loot['conditions'][1]['properties']=={'column':'1' if ident==DOUBLE else '0','row':'0','depth':'0'}
        assert loot['entries'][0]['name']=='lsmmod:'+ident
        icon=item(ident);assert icon and all(0<=v<=16 for e in icon for key in ('from','to') for v in e[key])
    for opened in (False,True):
        actual=scene(DOUBLE,opened);leaves=[e for e in actual if not any(v['texture']==PAINTS[0] for v in e['faces'].values())]
        expected=[];add(expected,[model(wood(opened))])
        center,scale=framing(expected,size=(320,320))
        for angle in (-65,120):
            a=np.asarray(render(leaves,center,scale,size=(320,320),azimuth=angle,elevation=12)).astype(int)
            b=np.asarray(render(expected,center,scale,size=(320,320),azimuth=angle,elevation=12)).astype(int)
            assert np.mean(np.abs(a-b))<.15,(opened,angle,'double leaf UV or geometry changed')
        slab=[e for e in actual if any(v['texture']==PAINTS[0] for v in e['faces'].values())]
        assert len(slab)==40 and all(e['from'][1]>=40 and e['to'][1]<=48 for e in slab)
        for facing in ('north','east','south','west'):
            painted=scene(DOUBLE,opened,facing=facing,paint=lambda column,face,q:1+q)
            squares=[e for e in painted if any(v['texture'] in PAINTS[1:5] for v in e['faces'].values())]
            assert len(squares)==40
            for e in squares:
                face,surface=next(iter(e['faces'].items()));x,y,z=[int((lo+hi)//16)%2 for lo,hi in zip(e['from'],e['to'])]
                q=z+2*y if face in ('west','east') else x+2*z if face in ('up','down') else x+2*y
                assert surface['texture']==PAINTS[1+q],(facing,face,q)
    for opened,right in itertools.product((False,True),repeat=2):
        actual=scene(BATHROOM,opened,right)
        assert max(e['to'][1] for e in actual)==64 and min(e['from'][1] for e in actual)==0
        expected=bathroom_parts(opened,right)
        center,scale=framing(expected,size=(320,440))
        for angle in (-65,120):
            a=np.asarray(render(actual,center,scale,size=(320,440),azimuth=angle,elevation=12)).astype(int)
            b=np.asarray(render(expected,center,scale,size=(320,440),azimuth=angle,elevation=12)).astype(int)
            assert np.mean(np.abs(a-b))<.15,(opened,right,angle,'tall bathroom fragments differ from whole door')
        for facing,right_vector in (((0,-1),(1,0)),((1,0),(0,1)),((0,1),(-1,0)),((-1,0),(0,-1))):
            cells=[(right_vector[0]*column-facing[0]*depth,row,right_vector[1]*column-facing[1]*depth) for column,row,depth in itertools.product(range(2),range(4),range(2)) if depth==0 or opened and column==int(right)]
            assert len(cells)==len(set(cells))==(12 if opened else 8)
    java=ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
    base=module_read_text(java/'SchoolFramedDiningDoorBlock.java')
    assert 'row < 3' not in base and '.setValue(ROW,' not in base and 'state.getValue(ROW)' not in base
    assert 'hasTransomGlazing() {return false;}' in module_read_text(java/'SchoolBathroomDoor2x4Block.java')
    assert 'new double[]{0,0,0,32,64,4}' in module_read_text(java/'SchoolBathroomDoor2x4Block.java')
    assert 'rows()==4?128:0' in base and 'b[a]>=40?b[a]+16:b[a]*1.4' in base
    assert 'rows()-1,0' in base and 'public int rows() {return 3;}' in base
    assert 'return false;' in module_read_text(java/'SchoolPaintableDoubleEntranceBlock.java')
    assert 'extends SchoolPaintableCustomBlockEntity' in module_read_text(java/'SchoolEntranceSlabPaintEntity.java')
    print('OK: double leaves match original renders, 40 exposed independent slab squares and full-depth header; 2x4 bathroom fragments match complete door in both hinges/openings, unique 8/12 cells, old state domains and single drops preserved. Static/offline only.')


def main():
    wrapper = read(A/'models/block/framed_door_transom.json')
    entries = wrapper['models'][0]
    assert len(entries) == 16
    assert entries[0]['condition']['masks'][0] == 0  # Refresh shared world selection.
    assert sorted(m for e in entries for m in e['condition']['masks']) == list(range(32))
    for mask in range(32):
        model, = resolved('lsmmod:block/framed_door_transom', mask)
        assert model['type'] == 'base' and model['elements']
        assert model['textures']['glass'] == 'lsmmod:block/school_glass'
        assert model['textures']['frame'] == 'lsmmod:block/school_glass_frame'
        for e in model['elements']:
            assert all(0 <= e['from'][i] < e['to'][i] <= 16 for i in range(3))
            assert 8 <= e['from'][1] < e['to'][1] <= 16 and e['to'][2] <= 8
            for f, material in e['faces'].items():
                if material['texture'] == '#glass':
                    assert not (f == 'west' and e['from'][0] == 0 and not mask & 1)
                    assert not (f == 'east' and e['to'][0] == 16 and not mask & 2)
                    assert not (f == 'up' and e['to'][1] == 16 and mask & 8)
                if material['texture'] == '#frame' and f == 'north':
                    assert not (mask & 8 and e['from'][1] == 15 and e['to'][1] == 16)
        if not mask & 8:
            assert any(e['from'][1] == 15 and e['to'][1] == 16
                       and any(f['texture'] == '#frame' for f in e['faces'].values()) for e in model['elements'])
    rows = {r['id']:r for r in audit()}
    count = 0
    for ident, wood in [('dining_door_wide','dining_door'), ('bathroom_door_wide','bathroom_door')]:
        assert rows[ident]['states'] == 192
        parts = read(A/f'blockstates/{ident}.json')['multipart']
        for facing, opened, hinge in itertools.product(('north','east','south','west'), ('false','true'), ('left','right')):
            # Exactly one complete window model in the whole assembly, not one per cell.
            windows = []
            for column, row, depth in itertools.product(range(2),range(3),range(2)):
                state = {'facing':facing,'open':opened,'hinge':hinge,'column':str(column),'row':str(row),'depth':str(depth)}
                for part in parts:
                    if not matches(part['when'],state):
                        continue
                    model_id = part['apply']['model']
                    for model in resolved(model_id):
                        if 'framed_door_window_' in model_id:
                            windows.append((model,state))
                        else:
                            for e in model['elements']:
                                for f, material in e['faces'].items():
                                    key = material['texture']
                                    if key in ('#bottom','#top'):
                                        assert model['textures'][key[1:]] == f'lsmmod:block/{wood}_{key[1:]}'
                                    if key == '#light':
                                        assert f in ('east','west')
                                    if f == 'south' and key in ('#light','#white'):
                                        assert key == '#white'
            assert len(windows) == (1 if ident=='dining_door_wide' else 0), (ident,facing,opened,hinge)
            if windows:
                model, state = windows[0]
                assert state['row'] == '1' and state['depth'] == '0'
                assert len(model['elements']) == 1 and model['textures']['window'] == 'minecraft:block/glass'
                e = model['elements'][0]
                assert e['from'][1] == -8.5 and e['to'][1] == 19
                assert min(e['from']) >= -16 and max(e['to']) <= 32
                assert all(face['uv'] == [1,1,15,15] for face in e['faces'].values())
            state = {'facing':facing,'open':opened,'hinge':hinge,'depth':'0'}
            for column in (0,1):
                state = dict(state,row='2',column=str(column))
                assert sum('framed_door_transom' in p['apply']['model'] for p in parts if matches(p['when'],state)) == 1
            count += 1
        loot = read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{ident}.json')
        assert loot['pools'][0]['conditions'][1]['properties'] == {'column':'0','row':'0','depth':'0'}
        assert loot['pools'][0]['entries'][0]['name'] == 'lsmmod:'+ident
    java = ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
    shared = module_read_text(java/'SchoolGlazing.java')
    assert 'instanceof SchoolFramedDiningDoorBlock' in shared
    assert 'state.getValue(SchoolFramedDiningDoorBlock.DEPTH)==0' in shared
    assert 'isFramedTransom(state)||state.getBlock() instanceof TallClassroomEntranceBlock' in shared
    assert 'adjacent(level.getBlockState(start.relative(right.getOpposite())),level.getBlockState(start),facing)' in shared
    assert shared.count('length-1)),level.getBlockState(start.relative(right,length)),facing)') == 2
    assert 'SchoolGlazing.isFramedTransom(neighbor)' in module_read_text(java/'SchoolGlassBlock.java')
    door = module_read_text(java/'SchoolFramedDiningDoorBlock.java')
    assert 'SchoolGlazing.refreshHorizontal(level, cell(origin,facing,column,rows()-1,0))' in door
    assert 'SimpleBlockOutline.forState' in door and 'COLLISIONS.computeIfAbsent' in door
    print(f'OK: 32 dynamic glazing masks; 8px transom matches school-glass footprint, connected caps/top frame removed; {count} facing/open/hinge assemblies have a single dining pane or a solid bathroom leaf; native textures, restored white front, shared caches and single drops.')


if __name__ == '__main__':
    import sys
    if '--extended-only' in sys.argv:check_extended_doors()
    elif '--column-only' in sys.argv:check_paintable_column()
    elif '--paintable-bathroom-only' in sys.argv:check_paintable_bathroom()
    elif '--entrance-only' in sys.argv:check_paintable_entrance()
    else:main()
