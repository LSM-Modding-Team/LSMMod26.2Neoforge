package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

/** Decorative drinking fountain mounted on a sturdy wall. */
public class DrinkingFountainBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[] SHAPES = new VoxelShape[4];

    static {
        SHAPES[0] = Shapes.or(
                Block.box(3, 0, 0, 13, 8, 7),
                Block.box(2, 8, 0, 14, 10, 8),
                Block.box(2.5, 10, 1.5, 13.5, 10.5, 8.5),
                Block.box(1, 10, 0, 2.5, 12, 8.5),
                Block.box(13.5, 10, 0, 15, 12, 8.5),
                Block.box(2.5, 10, 0, 13.5, 12, 1.5),
                Block.box(2.5, 10, 8.5, 13.5, 12, 10),
                Block.box(2.5, 10.5, 1.5, 3.25, 11, 8.5),
                Block.box(12.75, 10.5, 1.5, 13.5, 11, 8.5),
                Block.box(3.25, 10.5, 7.75, 12.75, 11, 8.5),
                Block.box(3.25, 10.5, 1.5, 12.75, 11, 2.25),
                Block.box(11, 10.5, 6.5, 12.5, 13.5, 8),
                Block.box(10.5, 13, 6.5, 12, 14, 7.5),
                Block.box(12.5, 10.5, 7.5, 13, 14.5, 8.5),
                Block.box(11.5, 14, 7.5, 13, 14.5, 8.5));
        for (int i = 1; i < 4; i++) {
            VoxelShape[] rotated = {Shapes.empty()};
            SHAPES[i - 1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    rotated[0] = Shapes.or(rotated[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
            SHAPES[i] = rotated[0];
        }
    }

    public DrinkingFountainBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (face.getAxis().isVertical()) return null;
        BlockState state = defaultBlockState().setValue(FACING, face);
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos support = pos.relative(facing.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, facing);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == state.getValue(FACING).getOpposite()) ticks.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }
}
