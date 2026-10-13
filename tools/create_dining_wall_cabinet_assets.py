"""Empty four-door wall cabinet using existing furniture materials and cell lifecycle."""
import copy
import json
from create_school_refrigerator_assets import A, J, D, clip, write
from create_bathroom_statue_assets import solid_model
from create_facility_expansion_assets import basic

ID = 'dining_wall_cabinet'
TEXTURES = {'white': 'lsmmod:block/school_porcelain',
            'metal': 'lsmmod:block/school_chrome', 'particle': '#white'}


def parts():
    result = [([0, 0, 4, 32, 1, 16], 'white'),
              ([0, 31, 4, 32, 32, 16], 'white'),
              ([0, 1, 15, 32, 31, 16], 'white'),
              ([0, 1, 4, 1, 31, 15], 'white'),
              ([31, 1, 4, 32, 31, 15], 'white'),
              ([1, 8, 4, 31, 9, 15], 'white'),
              ([15.5, 1, 4, 16.5, 8, 15], 'white')]
    for x in (0, 8, 16, 24):
        result.append(([x+.125, 9, 4, x+7.875, 31, 5], 'white'))
    for x in (6.5, 9, 22.5, 25):
        result.extend([([x, 11, 3, x+.5, 16, 3.5], 'metal'),
                       ([x, 11, 3.5, x+.5, 11.5, 4], 'metal'),
                       ([x, 15.5, 3.5, x+.5, 16, 4], 'metal')])
    return result


def main():
    model = solid_model(parts())
    model.update(textures=TEXTURES, render_type='minecraft:solid')
    variants = {}
    item = copy.deepcopy(model)
    item['elements'] = []
    for row in range(2):
        for column in range(2):
            local = clip(model, column, row)
            name = f'{ID}_{row}_{column}'
            write(A/f'models/block/{name}.json', local)
            for turn, facing in enumerate(('north', 'east', 'south', 'west')):
                variants[f'column={column},facing={facing},half={"upper" if row else "lower"}'] = {
                    'model': 'lsmmod:block/'+name, 'y': turn*90}
            for element in copy.deepcopy(local['elements']):
                for key in ('from', 'to'):
                    x, y, z = element[key]
                    element[key] = [(x+16*column)/2, (y+16*row)/2, 8+(z-10)/2]
                item['elements'].append(element)
    write(A/f'blockstates/{ID}.json', {'variants': variants})
    write(A/f'models/item/{ID}.json', item)
    basic(ID, controller={'column': '0', 'half': 'lower'})
    boxes = json.dumps([box for box, *_ in parts()]).replace('[', '{').replace(']', '}')
    (J/'block/DiningWallCabinetBlock.java').write_text('''package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Wall-mounted four-door cabinet with two empty lower cubbies. */
public class DiningWallCabinetBlock extends WideBathroomBlock {
    private static final double[][] BOXES = '''+boxes+''';

    public DiningWallCabinetBlock(Properties properties) {
        super(properties, BOXES, BOXES, true, 2, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }
}
''')
    for filename, entry in (
        ('ModBlocks.java', '    public static final DeferredBlock<DiningWallCabinetBlock> DINING_WALL_CABINET = BLOCKS.registerBlock("dining_wall_cabinet", DiningWallCabinetBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> DINING_WALL_CABINET = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_WALL_CABINET);'),
    ):
        path = J/'registry'/filename
        text = path.read_text()
        if ' DINING_WALL_CABINET =' not in text:
            path.write_text(text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1))
    for locale, name in (('es_es', 'Armario de pared del comedor'), ('en_us', 'Dining Wall Cabinet')):
        path = A/f'lang/{locale}.json'
        data = json.loads(path.read_text())
        data['block.lsmmod.'+ID] = name
        write(path, data)
    path = D/'minecraft/tags/block/mineable/axe.json'
    data = json.loads(path.read_text())
    if 'lsmmod:'+ID not in data['values']:
        data['values'].append('lsmmod:'+ID)
        write(path, data)


if __name__ == '__main__':
    main()
