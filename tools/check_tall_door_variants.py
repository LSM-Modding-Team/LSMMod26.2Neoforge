"""Static resource and registration regression for the three tall door variants."""
import json
from create_tall_door_variants import A,ROOT,VARIANTS

def read(p):return json.loads(p.read_text())
def main():
    source=read(A/'blockstates/tall_classroom_entrance.json')['variants']
    registrations=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModBlocks.java').read_text()
    items=(ROOT/'src/main/java/net/nicomar2009/lsmmod/registry/ModItems.java').read_text()
    checked=0
    for base in VARIANTS:
        name='tall_'+base;states=read(A/f'blockstates/{name}.json')['variants']
        assert states.keys()==source.keys()
        assert f'"{name}", TallClassroomEntranceBlock::new' in registrations
        assert f'ITEMS.registerSimpleBlockItem(ModBlocks.{name.upper()})' in items
        for key,value in states.items():
            state=dict(p.split('=') for p in key.split(','))
            assert state['row'] in ('0','1','2') and state['frame_layout'] in '01234567'
            assert value['y']==source[key]['y']
            alias=read(A/f"models/{value['model'].split(':')[1]}.json")
            assert alias['parent']==source[key]['model'] and 'elements' not in alias
            for part in ('bottom','top'):
                assert alias['textures'][part]==f'lsmmod:block/{base}_{part}'
                assert (A/f'textures/block/{base}_{part}.png').is_file()
            parent=read(A/f"models/{alias['parent'].split(':')[1]}.json")
            assert parent['elements'] and alias['render_type']==parent['render_type']
            checked+=1
        item=read(A/f'models/item/{name}.json')
        assert item['parent']=='lsmmod:item/tall_classroom_entrance'
        assert read(A/f'items/{name}.json')['model']['model']=='lsmmod:item/'+name
        loot=read(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{name}.json')['pools'][0]
        condition=next(c for c in loot['conditions'] if c['condition']=='minecraft:block_state_property')
        assert condition['block']=='lsmmod:'+name and condition['properties']=={'row':'0'}
        assert loot['entries'][0]['name']=='lsmmod:'+name
        assert 'lsmmod:'+name in read(ROOT/'src/main/resources/data/minecraft/tags/block/mineable/axe.json')['values']
        for locale in ('es_es','en_us'):assert 'block.lsmmod.'+name in read(A/f'lang/{locale}.json')
    print(f'OK: {checked} tall door states; exact inherited geometry/glazing, original door textures, rotations, registrations, items and one lower-row drop.')
if __name__=='__main__':main()
