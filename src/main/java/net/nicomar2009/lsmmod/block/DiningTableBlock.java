package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Single-block dining table with a thick top and central pedestal. */
public class DiningTableBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 14, 0, 16, 16, 16),
            Block.box(7.25, 1.5, 7.25, 8.75, 14, 8.75),
            Block.box(1, 0, 7.5, 15, 1.5, 8.5),
            Block.box(7.5, 0, 1, 8.5, 1.5, 15));

    public DiningTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
