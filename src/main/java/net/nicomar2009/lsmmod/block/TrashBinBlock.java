package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Decorative floor bins: no inventory, moving lid, menu, or trash processing. */
public class TrashBinBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final VoxelShape[] collisions = new VoxelShape[4];

    public TrashBinBlock(Properties properties, double[][] boxes) {
        super(properties);
        VoxelShape shape = Shapes.empty();
        for (double[] b : boxes) shape = Shapes.or(shape, Block.box(b[0], b[1], b[2], b[3], b[4], b[5]));
        collisions[0] = shape;
        for (int i = 1; i < 4; i++) {
            VoxelShape[] next = {Shapes.empty()};
            collisions[i-1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    next[0] = Shapes.or(next[0], Shapes.box(1-z2, y1, x1, 1-z1, y2, x2)));
            collisions[i] = next[0];
        }
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() != Direction.UP) return null;
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = switch (state.getValue(FACING)) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return collisions[index];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
