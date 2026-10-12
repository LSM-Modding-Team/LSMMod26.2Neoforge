"""One-block masonry counter: dark tiled plinth, light wall and light tiled worktop."""
import json
from PIL import Image
from create_school_yellow_border_assets import ROOT, A, D

ID = 'dining_room_counter'
J = ROOT / 'src/main/java/net/nicomar2009/lsmmod'


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(value if isinstance(value, str) else json.dumps(value, ensure_ascii=False, indent=2)+'\n')


def main():
    # Preserve the nine saved phases, but share one dark, 2px-thick top model.
    Image.new('RGB', (16,16), (22,31,37)).save(A/'textures/block/dining_room_counter.png')
    faces = {f:{'texture':'#top','uv':[0,0,16,16]} for f in ('north','south','east','west','up','down')}
    for face in ('north','south','east','west'):
        faces[face]['uv'] = [0,0,16,2]
        faces[face]['cullface'] = face
    faces['up']['cullface'] = 'up'
    write(A/f'models/block/{ID}.json', {'parent':'minecraft:block/block',
        'textures':{'top':'lsmmod:block/'+ID,'particle':'lsmmod:block/'+ID},
        'elements':[{'from':[0,14,0],'to':[16,16,16],'faces':faces}]})
    variants = {f'tile_x={x},tile_z={z}':{'model':'lsmmod:block/'+ID} for x in range(3) for z in range(3)}
    write(A/f'blockstates/{ID}.json', {'variants':variants})
    write(A/f'models/item/{ID}.json', {'parent':'lsmmod:block/'+ID})
    for path in (A/'models/block').glob(ID+'_*.json'):
        path.unlink()
    write(A / f'items/{ID}.json', {'model': {'type': 'minecraft:model', 'model': 'lsmmod:item/' + ID}})
    write(D / f'lsmmod/loot_table/blocks/{ID}.json', {'type': 'minecraft:block', 'pools': [{
        'rolls': 1, 'conditions': [{'condition': 'minecraft:survives_explosion'}],
        'entries': [{'type': 'minecraft:item', 'name': 'lsmmod:' + ID}]}]})
    for filename, line in (
        ('ModBlocks.java', '    public static final DeferredBlock<DiningRoomCounterBlock> DINING_ROOM_COUNTER = BLOCKS.registerBlock("dining_room_counter", DiningRoomCounterBlock::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> DINING_ROOM_COUNTER = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_ROOM_COUNTER);'),
    ):
        p = J / 'registry' / filename
        source = p.read_text()
        start, end = '    // BEGIN DINING COUNTER', '    // END DINING COUNTER'
        section = start + '\n' + line + '\n' + end
        if start in source:
            source = source[:source.index(start)] + section + source[source.index(end)+len(end):]
        else:
            source = source.replace('    // BEGIN FACILITY EXPANSIONS', section + '\n\n    // BEGIN FACILITY EXPANSIONS', 1)
        write(p, source)
    for locale, name in [('es_es', 'Cubierta del mostrador del comedor'), ('en_us', 'Dining Room Countertop')]:
        p = A / f'lang/{locale}.json'
        values = json.loads(p.read_text())
        values['block.lsmmod.' + ID] = name
        write(p, values)
    p = D / 'minecraft/tags/block/mineable/pickaxe.json'
    values = json.loads(p.read_text())
    values['values'] = list(dict.fromkeys(values['values'] + ['lsmmod:' + ID]))
    write(p, values)
    # All three red chair variants share this material. Read the bell's actual paint.
    bell = Image.open(A / 'textures/block/school_bell.png').convert('RGBA')
    red = max((count, color) for count, color in bell.getcolors(bell.width*bell.height)
              if color[3] and color[0] > 2*color[1] and color[0] > 2*color[2])[1]
    Image.new('RGB', (16, 16), red[:3]).save(A / 'textures/block/chair_red.png')


if __name__ == '__main__':
    main()
