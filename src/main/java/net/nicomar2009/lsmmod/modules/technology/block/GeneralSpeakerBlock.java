package net.nicomar2009.lsmmod.modules.technology.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Two-cell cabinet with a projecting gray upper horn, restricted to Moises' laptop. */
public class GeneralSpeakerBlock extends TallSpeakerBlock {
    private static final VoxelShape[] UPPER_SHAPES = new VoxelShape[4];
    static {
        VoxelShape shape = Shapes.or(Block.box(0, 0, 2, 16, 16, 14), Block.box(1, 10, 0, 15, 16, 3));
        Direction facing = Direction.NORTH;
        for (int i = 0; i < 4; i++) {
            UPPER_SHAPES[facing.get2DDataValue()] = shape;
            VoxelShape[] next = {Shapes.empty()};
            shape.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    next[0] = Shapes.or(next[0], Shapes.box(1-z2, y1, x1, 1-z1, y2, x2)));
            shape = next[0];
            facing = facing.getClockWise();
        }
    }
    public GeneralSpeakerBlock(Properties properties) { super(properties); }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER
                ? UPPER_SHAPES[state.getValue(FACING).get2DDataValue()]
                : super.getCollisionShape(state, level, pos, context);
    }
}
