"""One-wide two-door wall cabinet: remove a lateral module without stretching its materials."""
import copy
import json
from create_school_refrigerator_assets import A, J, D, clip, write
from create_bathroom_statue_assets import solid_model, crop_face_uv
from create_facility_expansion_assets import basic

ID = 'dining_wall_cabinet'
TEXTURES = {'white': 'lsmmod:block/school_porcelain',
            'metal': 'lsmmod:block/school_chrome', 'particle': '#white'}


def parts():
    result = [([0, 0, 4, 16, 1, 16], 'white'),
              ([0, 31, 4, 16, 32, 16], 'white'),
              ([0, 1, 15, 16, 31, 16], 'white'),
              ([0, 1, 4, 1, 31, 15], 'white'),
              ([15, 1, 4, 16, 31, 15], 'white'),
              ([1, 8, 4, 15, 9, 15], 'white')]
    for x in (0, 8):
        result.append(([x+.125, 9, 4, x+7.875, 31, 5], 'white'))
    for x in (6.5, 9):
        result.extend([([x, 11, 3, x+.5, 16, 3.5], 'metal'),
                       ([x, 11, 3.5, x+.5, 11.5, 4], 'metal'),
                       ([x, 15.5, 3.5, x+.5, 16, 4], 'metal')])
    return result


def face_uv(box, visible, face, material):
    # Retain the original 32px carcass mapping and crop off the removed module.
    # Doors, handles and the relocated right side keep their original dimensions/UVs.
    references={
        (0,0,4,16,1,16):[0,0,4,32,1,16],
        (0,31,4,16,32,16):[0,31,4,32,32,16],
        (0,1,15,16,31,16):[0,1,15,32,31,16],
        (1,8,4,15,9,15):[1,8,4,31,9,15],
    }
    reference=references.get(tuple(box),box)
    return crop_face_uv([0,0,16,16],face,reference[:3],reference[3:],visible[:3],visible[3:])


def main():
    model = solid_model(parts(), face_uv=face_uv)
    model.update(textures=TEXTURES, render_type='minecraft:solid')
    variants = {}
    item = copy.deepcopy(model)
    item['elements'] = []
    for row in range(2):
        for column in range(1):
            local = clip(model, column, row)
            name = f'{ID}_{row}_{column}'
            write(A/f'models/block/{name}.json', local)
            for turn, facing in enumerate(('north', 'east', 'south', 'west')):
                variants[f'column={column},facing={facing},half={"upper" if row else "lower"}'] = {
                    'model': 'lsmmod:block/'+name, 'y': turn*90}
            for element in copy.deepcopy(local['elements']):
                for key in ('from', 'to'):
                    x, y, z = element[key]
                    element[key] = [4+(x+16*column)/2, (y+16*row)/2, 8+(z-10)/2]
                item['elements'].append(element)
    # Preserve old column states while the removed column is retired by neighbor validation.
    for row in range(2):
        name=f'{ID}_{row}_1'
        write(A/f'models/block/{name}.json',{'parent':'minecraft:block/block','textures':TEXTURES,'elements':[]})
        for turn,facing in enumerate(('north','east','south','west')):
            variants[f'column=1,facing={facing},half={"upper" if row else "lower"}']={'model':'lsmmod:block/'+name,'y':turn*90}
    write(A/f'blockstates/{ID}.json', {'variants': variants})
    write(A/f'models/item/{ID}.json', item)
    basic(ID, controller={'column': '0', 'half': 'lower'})
    boxes = json.dumps([box for box, *_ in parts()]).replace('[', '{').replace(']', '}')
    (J/'block/DiningWallCabinetBlock.java').write_text('''package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** One-wide wall-mounted cabinet with two doors and one empty lower cubby. */
public class DiningWallCabinetBlock extends SchoolRectangularBlock {
    // Keep the serialized column domain of the former two-wide block.
    public static final IntegerProperty COLUMN = WideBathroomBlock.COLUMN;
    private static final double[][] BOXES = '''+boxes+''';

    public DiningWallCabinetBlock(Properties properties) {
        super(properties, BOXES, BOXES, true, 1, 2, false);
    }

    @Override protected IntegerProperty columnProperty() {return COLUMN;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLUMN, HALF);
    }

    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(COLUMN)==1?Shapes.empty():super.getCollisionShape(state,level,pos,context);
    }

    @Override protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return state.getValue(COLUMN)==1||super.canBeReplaced(state,context);
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(state.getValue(COLUMN)==1) {
            // The resized footprint excludes this old cell; never dismantle or duplicate-drop its remaining neighbor.
            level.setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
            return;
        }
        super.tick(state,level,pos,random);
    }

    @Override public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        return state.getValue(COLUMN)==1?state:super.playerWillDestroy(level,pos,state,player);
    }

    @Override protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));
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
