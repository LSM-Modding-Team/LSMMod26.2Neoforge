"""Two-wide, three-high shelving; white desk boards and four permanent metal uprights."""
from resource_modules import module_read_text, module_write_text
import copy
import json
from create_bathroom_statue_assets import solid_model
from create_school_refrigerator_assets import A, J, D, write
from create_san_martin_display import clip
from create_facility_expansion_assets import basic

ID = 'school_shelving'
TEXTURES = {'white': 'lsmmod:block/studentsdeskleft',
            'metal': 'lsmmod:block/school_chrome', 'particle': '#metal'}


def parts():
    result = [([0, y, 3, 32, y+1, 14], 'white') for y in (4, 18, 32, 47)]
    # All four uprights stay on every module, including horizontal joins.
    for x in (.5, 30.25):
        for z in (3, 12.75):
            result.append(([x, 0, z, x+1.25, 48, z+1.25], 'metal'))
    return result


def main():
    full = solid_model(parts())
    full.update(textures=TEXTURES, render_type='minecraft:solid')
    # The desk's actual white tabletop is the top-left 16x16 region of its 64x64 atlas.
    for e in full['elements']:
        for face in e['faces'].values():
            if face['texture'] == '#white': face['uv'] = [0, 0, 4, 4]
    variants = {}
    for row in range(3):
        for column in range(2):
            local = clip(full, column, row)
            # Only the board end caps may disappear against aligned horizontal modules.
            # Uprights have no cullface; they remain visible and physically unchanged.
            for e in local['elements']:
                for side, x, key in (('west', 0, 'from'), ('east', 16, 'to')):
                    face = e['faces'].get(side)
                    if face and face['texture'] == '#white' and e[key][0] == x:
                        face['cullface'] = side
            name = f'{ID}_{column}_{row}'
            write(A/f'models/block/{name}.json', local)
            for turn, facing in enumerate(('north', 'east', 'south', 'west')):
                variants[f'column={column},facing={facing},row={row}'] = {
                    'model': 'lsmmod:block/'+name, 'y': turn*90}
    write(A/f'blockstates/{ID}.json', {'variants': variants})
    item = copy.deepcopy(full)
    for e in item['elements']:
        for key in ('from', 'to'):
            x, y, z = e[key]
            e[key] = [8+(x-16)/3, y/3, 8+(z-8.5)/3]
    write(A/f'models/item/{ID}.json', item)
    basic(ID, controller={'column': '0', 'row': '0'})
    boxes = json.dumps([box for box, *_ in parts()]).replace('[', '{').replace(']', '}')
    module_write_text(J/'modules/shelving/block/SchoolShelvingBlock.java', '''package net.nicomar2009.lsmmod.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Six-cell shelf with flush lateral boards; every module retains its four metal uprights. */
public class SchoolShelvingBlock extends WideBathroomBlock {
    private static final double[][] BOXES = '''+boxes+''';

    public SchoolShelvingBlock(Properties properties) {
        super(properties, BOXES, BOXES, false, 3, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROW);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        Direction facing = state.getValue(FACING);
        if (!adjacent.is(this) || adjacent.getValue(FACING) != facing
                || adjacent.getValue(ROW).intValue() != state.getValue(ROW).intValue()) return false;
        Direction right = facing.getClockWise();
        int column = state.getValue(COLUMN);
        // No vertical, front/back or misaligned joins. Only cull-marked board faces are hidden.
        return (side == right && adjacent.getValue(COLUMN) == 1-column)
                || (side == right.getOpposite() && adjacent.getValue(COLUMN) == 1-column);
    }
}
''')
    for filename, entry in (
        ('ModBlocks.java', '    public static final DeferredBlock<SchoolShelvingBlock> SCHOOL_SHELVING = BLOCKS.registerBlock("school_shelving", SchoolShelvingBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));'),
        ('ModItems.java', '    public static final DeferredItem<BlockItem> SCHOOL_SHELVING = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SHELVING);'),
    ):
        path = J/'registry'/filename
        text = module_read_text(path)
        if ' SCHOOL_SHELVING =' not in text:
            module_write_text(path, text.replace('    // BEGIN FACILITY EXPANSIONS', entry+'\n\n    // BEGIN FACILITY EXPANSIONS', 1))
    for locale, name in (('es_es', 'Estantería metálica con niveles blancos'),
                         ('en_us', 'Metal Shelving with White Shelves')):
        path = A/f'lang/{locale}.json'
        data = json.loads(module_read_text(path))
        data['block.lsmmod.'+ID] = name
        write(path, data)
    path = D/'minecraft/tags/block/mineable/pickaxe.json'
    data = json.loads(module_read_text(path))
    if 'lsmmod:'+ID not in data['values']:
        data['values'].append('lsmmod:'+ID)
        write(path, data)


if __name__ == '__main__': main()
