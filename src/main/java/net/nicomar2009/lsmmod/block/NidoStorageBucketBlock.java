package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Decorative two-cell bucket; full 1x1 footprint and 1.5 block selection/collision height. */
public class NidoStorageBucketBlock extends SchoolRectangularBlock {
    private static final double[][] BOXES = {{0, 0, 0, 16, 24, 16}};

    public NidoStorageBucketBlock(Properties properties) {
        super(properties, BOXES, BOXES, false, 1, 2, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }
}
