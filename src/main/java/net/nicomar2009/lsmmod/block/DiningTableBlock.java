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
            Block.box(7.25, 1.75, 7.25, 8.75, 14, 8.75),
            Block.box(7.2929, 0.5, 7.6464, 8.3536, 1.75, 8.7071),
            Block.box(7.6464, 0.5, 7.2929, 8.7071, 1.75, 8.3536),
            Block.box(2.6967, 0.5, 12.2426, 3.7574, 1.75, 13.3033),
            Block.box(3.0503, 0.5, 11.8891, 4.1109, 1.75, 12.9497),
            Block.box(3.4038, 0.5, 11.5355, 4.4645, 1.75, 12.5962),
            Block.box(3.7574, 0.5, 11.182, 4.818, 1.75, 12.2426),
            Block.box(4.1109, 0.5, 10.8284, 5.1716, 1.75, 11.8891),
            Block.box(4.4645, 0.5, 10.4749, 5.5251, 1.75, 11.5355),
            Block.box(4.818, 0.5, 10.1213, 5.8787, 1.75, 11.182),
            Block.box(5.1716, 0.5, 9.7678, 6.2322, 1.75, 10.8284),
            Block.box(5.5251, 0.5, 9.4142, 6.5858, 1.75, 10.4749),
            Block.box(5.8787, 0.5, 9.0607, 6.9393, 1.75, 10.1213),
            Block.box(6.2322, 0.5, 8.7071, 7.2929, 1.75, 9.7678),
            Block.box(6.5858, 0.5, 8.3536, 7.6464, 1.75, 9.4142),
            Block.box(6.9393, 0.5, 8, 8, 1.75, 9.0607),
            Block.box(8, 0.5, 6.9393, 9.0607, 1.75, 8),
            Block.box(8.3536, 0.5, 6.5858, 9.4142, 1.75, 7.6464),
            Block.box(8.7071, 0.5, 6.2322, 9.7678, 1.75, 7.2929),
            Block.box(9.0607, 0.5, 5.8787, 10.1213, 1.75, 6.9393),
            Block.box(9.4142, 0.5, 5.5251, 10.4749, 1.75, 6.5858),
            Block.box(9.7678, 0.5, 5.1716, 10.8284, 1.75, 6.2322),
            Block.box(10.1213, 0.5, 4.818, 11.182, 1.75, 5.8787),
            Block.box(10.4749, 0.5, 4.4645, 11.5355, 1.75, 5.5251),
            Block.box(10.8284, 0.5, 4.1109, 11.8891, 1.75, 5.1716),
            Block.box(11.182, 0.5, 3.7574, 12.2426, 1.75, 4.818),
            Block.box(11.5355, 0.5, 3.4038, 12.5962, 1.75, 4.4645),
            Block.box(11.8891, 0.5, 3.0503, 12.9497, 1.75, 4.1109),
            Block.box(12.2426, 0.5, 2.6967, 13.3033, 1.75, 3.7574),
            Block.box(2.6967, 0.5, 3.0503, 7.6464, 1.75, 8),
            Block.box(3.0503, 0.5, 2.6967, 8, 1.75, 7.6464),
            Block.box(8, 0.5, 8.3536, 12.9497, 1.75, 13.3033),
            Block.box(8.3536, 0.5, 8, 13.3033, 1.75, 12.9497),
            Block.box(2.6967, 0, 12.2426, 3.7574, 0.5, 13.3033),
            Block.box(3.0503, 0, 11.8891, 4.1109, 0.5, 12.9497),
            Block.box(11.8891, 0, 3.0503, 12.9497, 0.5, 4.1109),
            Block.box(12.2426, 0, 2.6967, 13.3033, 0.5, 3.7574),
            Block.box(2.6967, 0, 3.0503, 3.7574, 0.5, 4.1109),
            Block.box(3.0503, 0, 2.6967, 4.1109, 0.5, 3.7574),
            Block.box(11.8891, 0, 12.2426, 12.9497, 0.5, 13.3033),
            Block.box(12.2426, 0, 11.8891, 13.3033, 0.5, 12.9497));

    public DiningTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
