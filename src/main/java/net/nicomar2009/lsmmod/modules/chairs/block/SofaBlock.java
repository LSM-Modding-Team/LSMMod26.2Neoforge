package net.nicomar2009.lsmmod.modules.chairs.block;
import net.nicomar2009.lsmmod.block.SharedChairRotations;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nicomar2009.lsmmod.modules.chairs.entity.SeatEntity;

/** Armless upholstered modules; FACING points toward the sitter's view, away from the back. */
public class SofaBlock extends ChairBlock {
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    private static final VoxelShape[][] COLLISIONS = new VoxelShape[StairsShape.values().length][];

    static {
        for (StairsShape shape : StairsShape.values()) {
            COLLISIONS[shape.ordinal()] = SharedChairRotations.horizontal(
                    ExactVoxelShapes.pixels(SofaShapes.BOXES[shape.ordinal()]));
        }
    }

    public SofaBlock(Properties properties) {
        super(properties, COLLISIONS[StairsShape.STRAIGHT.ordinal()][0]);
        registerDefaultState(defaultBlockState().setValue(SHAPE, StairsShape.STRAIGHT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(SHAPE, corner(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        return side.getAxis().isHorizontal() ? state.setValue(SHAPE, corner(state, level, pos)) : state;
    }

    private static boolean joins(BlockState state, BlockState other) {
        return other.is(state.getBlock());
    }

    private static BlockState neighbor(LevelReader level, BlockPos pos) {
        return level.hasChunkAt(pos) ? level.getBlockState(pos) : Blocks.AIR.defaultBlockState();
    }

    private static boolean different(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        BlockState other = neighbor(level, pos.relative(side));
        return !joins(state, other) || other.getValue(FACING) != state.getValue(FACING);
    }

    /** Stair-style corner guards keep aligned rows straight at T junctions. */
    private static StairsShape corner(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockState behind = neighbor(level, pos.relative(facing.getOpposite()));
        if (joins(state, behind)) {
            Direction turn = behind.getValue(FACING);
            if (turn.getAxis() != facing.getAxis() && different(state, level, pos, turn)) {
                return turn.getOpposite() == facing.getCounterClockWise()
                        ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
            }
        }
        BlockState front = neighbor(level, pos.relative(facing));
        if (joins(state, front)) {
            Direction turn = front.getValue(FACING);
            if (turn.getAxis() != facing.getAxis() && different(state, level, pos, turn.getOpposite())) {
                return turn.getOpposite() == facing.getCounterClockWise()
                        ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
            }
        }
        return StairsShape.STRAIGHT;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) return state;
        StairsShape shape = switch (state.getValue(SHAPE)) {
            case INNER_LEFT -> StairsShape.INNER_RIGHT;
            case INNER_RIGHT -> StairsShape.INNER_LEFT;
            case OUTER_LEFT -> StairsShape.OUTER_RIGHT;
            case OUTER_RIGHT -> StairsShape.OUTER_LEFT;
            default -> StairsShape.STRAIGHT;
        };
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING))).setValue(SHAPE, shape);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISIONS[state.getValue(SHAPE).ordinal()][state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
        if (!level.getBlockState(pos).is(this)) {
            for (SeatEntity seat : level.getEntitiesOfClass(SeatEntity.class, new AABB(pos))) seat.discard();
        }
    }
}
