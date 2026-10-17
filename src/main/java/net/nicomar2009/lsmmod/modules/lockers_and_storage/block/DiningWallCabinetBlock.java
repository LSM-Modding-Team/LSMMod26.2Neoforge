package net.nicomar2009.lsmmod.modules.lockers_and_storage.block;
import net.nicomar2009.lsmmod.modules.facilities.block.WideBathroomBlock;
import net.nicomar2009.lsmmod.block.SchoolRectangularBlock;

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
    private static final double[][] BOXES = {{0, 0, 4, 16, 1, 16}, {0, 31, 4, 16, 32, 16}, {0, 1, 15, 16, 31, 16}, {0, 1, 4, 1, 31, 15}, {15, 1, 4, 16, 31, 15}, {1, 8, 4, 15, 9, 15}, {0.125, 9, 4, 7.875, 31, 5}, {8.125, 9, 4, 15.875, 31, 5}, {6.5, 11, 3, 7.0, 16, 3.5}, {6.5, 11, 3.5, 7.0, 11.5, 4}, {6.5, 15.5, 3.5, 7.0, 16, 4}, {9, 11, 3, 9.5, 16, 3.5}, {9, 11, 3.5, 9.5, 11.5, 4}, {9, 15.5, 3.5, 9.5, 16, 4}};

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
