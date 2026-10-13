package net.nicomar2009.lsmmod.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Six-cell shelf with flush lateral boards; every module retains its four metal uprights. */
public class SchoolShelvingBlock extends WideBathroomBlock {
    private static final double[][] BOXES = {{0, 4, 3, 32, 5, 14}, {0, 18, 3, 32, 19, 14}, {0, 32, 3, 32, 33, 14}, {0, 47, 3, 32, 48, 14}, {0.5, 0, 3, 1.75, 48, 4.25}, {0.5, 0, 12.75, 1.75, 48, 14.0}, {30.25, 0, 3, 31.5, 48, 4.25}, {30.25, 0, 12.75, 31.5, 48, 14.0}};

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
