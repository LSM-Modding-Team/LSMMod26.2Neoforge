"""Verify connected half-height transoms, single-pane rendering and material-only variants."""
import itertools
import json
from create_school_glass_assets import A, ROOT
from check_facility_expansion_assets import resolved, matches
from audit_block_states import audit


def read(path):
    return json.loads(path.read_text())


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
    main()
