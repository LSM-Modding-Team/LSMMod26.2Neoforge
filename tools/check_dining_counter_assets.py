"""Check counter surfaces, tile continuity, renamed floor resources and shared chair paint."""
import hashlib
import itertools
import json
import re
from PIL import Image
from audit_block_states import audit
from create_dining_counter_assets import ROOT, A, D, J, ID, main as generate


def read(path):
    return json.loads(path.read_text())


def main():
    rows = {r['id']: r for r in audit()}
    floors = ('dark_school_tiled_floor', 'light_school_tiled_floor')
    for ident in (*floors, ID):
        assert rows[ident]['states'] == 9
        assert len(read(A/f'blockstates/{ident}.json')['variants']) == 9
        assert read(D/f'lsmmod/loot_table/blocks/{ident}.json')['pools'][0]['entries'][0]['name'] == 'lsmmod:'+ident
    for old in ('bathroom_floor', 'light_bathroom_floor'):
        assert old not in rows
        assert not list(A.rglob(old+'*.json')) and not list(A.rglob(old+'*.png'))
        new = floors[old.startswith('light_')]
        for filename in ('ModBlocks.java', 'ModItems.java'):
            source = (J/'registry'/filename).read_text()
            assert re.search(r'addAlias\([^;]*"'+old+r'"\),\s*'+new.upper()+r'\.getId\(\)\)', source)
    # Every saved tile phase now shares exactly the same thin upper surface.
    states = read(A/f'blockstates/{ID}.json')['variants']
    assert {v['model'] for v in states.values()} == {'lsmmod:block/'+ID}
    model = read(A/f'models/block/{ID}.json')
    assert len(model['elements']) == 1
    element = model['elements'][0]
    assert element['from'] == [0,14,0] and element['to'] == [16,16,16]
    assert set(element['faces']) == {'north','south','east','west','up','down'}
    assert 'cullface' not in element['faces']['down']
    assert model['textures']['top'] == 'lsmmod:block/'+ID
    assert Image.open(A/f'textures/block/{ID}.png').getcolors(256) == [(256,(22,31,37))]
    counter = (J/'block/DiningRoomCounterBlock.java').read_text()
    assert 'extends BathroomFloorBlock' in counter and 'Block.box(0,14,0,16,16,16)' in counter
    assert counter.count('return TOP;') == 2
    assert 'lsmmod:'+ID in read(D/'minecraft/tags/block/mineable/pickaxe.json')['values']
    for locale in ('en_us', 'es_es'):
        assert all('block.lsmmod.'+ident in read(A/f'lang/{locale}.json') for ident in (*floors, ID))
    bell = Image.open(A/'textures/block/school_bell.png').convert('RGBA')
    paint = max((n,c) for n,c in bell.getcolors(bell.width*bell.height) if c[3] and c[0]>2*c[1] and c[0]>2*c[2])[1][:3]
    assert Image.open(A/'textures/block/chair_red.png').convert('RGB').getcolors(256) == [(256,paint)]
    for chair in ('hallchair', 'plasticchair_red', 'plasticchair_red_arms'):
        assert read(A/f'models/block/{chair}.json')['textures']['body'] == 'lsmmod:block/chair_red'
    paths = [A/f'models/block/{ID}.json',A/f'textures/block/{ID}.png'] + [
        A/f'blockstates/{ID}.json', A/f'models/item/{ID}.json', A/f'items/{ID}.json',
        A/'textures/block/chair_red.png', A/'lang/es_es.json', A/'lang/en_us.json',
        D/f'lsmmod/loot_table/blocks/{ID}.json', D/'minecraft/tags/block/mineable/pickaxe.json',
        J/'registry/ModBlocks.java', J/'registry/ModItems.java']
    before = {p:hashlib.sha256(p.read_bytes()).digest() for p in paths}
    generate()
    assert all(hashlib.sha256(p.read_bytes()).digest() == h for p,h in before.items())
    print('OK: counter contains only Y14..16px with matching collision and teachers-desk RGB22/31/37; nine saved phases share one model; renamed floors and block/item aliases; all red chairs match bell RGB 112/21/36; generator idempotent.')


if __name__ == '__main__':
    main()
