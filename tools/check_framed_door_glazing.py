"""Verify connected half-height transoms, single-pane rendering and material-only variants."""
import itertools
import json
from create_school_glass_assets import A, ROOT
from check_facility_expansion_assets import resolved, matches
from audit_block_states import audit


def read(path):
    return json.loads(path.read_text())

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
    assert 'hasTransomGlazing() {return true;}' not in (java/'block/SchoolPaintableEntranceDoorBlock.java').read_text()
    assert 'hasTransomGlazing() { return false; }' in (java/'block/SchoolPaintableBathroomDoorBlock.java').read_text()
    assert 'ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE.get()' in (java/'registry/ModBlockEntities.java').read_text()
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
    entity=(java/'block/SchoolDoorFramePaintEntity.java').read_text()
    block=(java/'block/SchoolPaintableBathroomDoorBlock.java').read_text()
    assert 'extends SchoolFramedDiningDoorBlock implements EntityBlock' in block
    assert 'hasTransomGlazing() { return false; }' in block and 'SimpleBlockOutline.forState' not in block
    assert 'removed|=1L<<cube' in entity and 'level.removeBlock' not in entity
    assert 'miniSize() {return 8;}' in entity and 'squareGroup(index)' in entity
    assert 'invalidateClientGeometry()' in entity and 'getLongOr("removed",0)' in entity
    print('OK: original leaf in all 192 states; 2x3 frame, 8px edit groups, all 13 materials, hidden seams, item/drop and unchanged fine storage for legacy paint/cuts. Java interactions remain uncompiled and untested in Minecraft.')


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
    shared = (java/'SchoolGlazing.java').read_text()
    assert 'instanceof SchoolFramedDiningDoorBlock' in shared
    assert 'state.getValue(SchoolFramedDiningDoorBlock.DEPTH)==0' in shared
    assert 'isFramedTransom(state)||state.getBlock() instanceof TallClassroomEntranceBlock' in shared
    assert 'adjacent(level.getBlockState(start.relative(right.getOpposite())),level.getBlockState(start),facing)' in shared
    assert shared.count('length-1)),level.getBlockState(start.relative(right,length)),facing)') == 2
    assert 'SchoolGlazing.isFramedTransom(neighbor)' in (java/'SchoolGlassBlock.java').read_text()
    door = (java/'SchoolFramedDiningDoorBlock.java').read_text()
    assert 'SchoolGlazing.refreshHorizontal(level, cell(origin,facing,column,2,0))' in door
    assert 'SimpleBlockOutline.forState' in door and 'COLLISIONS.computeIfAbsent' in door
    print(f'OK: 32 dynamic glazing masks; 8px transom matches school-glass footprint, connected caps/top frame removed; {count} facing/open/hinge assemblies have a single dining pane or a solid bathroom leaf; native textures, restored white front, shared caches and single drops.')


if __name__ == '__main__':
    import sys
    if '--paintable-bathroom-only' in sys.argv:check_paintable_bathroom()
    elif '--entrance-only' in sys.argv:check_paintable_entrance()
    else:main()
