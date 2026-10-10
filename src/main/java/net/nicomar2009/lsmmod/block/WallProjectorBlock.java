package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

/** A wall-only placement centers the original projector across two horizontal cells. */
public class WallProjectorBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 1);

    private static final VoxelShape[][] COLLISIONS = createCollisions();

    private static VoxelShape[][] createCollisions() {
        VoxelShape[][] result = new VoxelShape[2][4];
        for (int column = 0; column < 2; column++) {
            VoxelShape shape = Shapes.empty();
            for (double[] b : SchoolDecorationShapes.PROJECTOR_CELLS[column])
                shape = Shapes.or(shape, Block.box(b[0], b[1], b[2], b[3], b[4], b[5]));
            result[column][0] = shape;
            for (int rotation = 1; rotation < 4; rotation++) {
                VoxelShape[] next = {Shapes.empty()};
                result[column][rotation-1].forAllBoxes((x1,y1,z1,x2,y2,z2) ->
                        next[0] = Shapes.or(next[0],
                                Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                result[column][rotation] = next[0];
            }
        }
        return result;
    }

    public WallProjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(COLUMN, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLUMN);
    }

    private static BlockPos cell(BlockPos origin, Direction facing, int column, int row) {
        return origin.relative(facing.getClockWise(), column).above(row);
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(FACING).getClockWise(), -state.getValue(COLUMN));
    }

    private boolean matches(BlockState state, Direction facing, int column, int row) {
        return state.is(this) && state.getValue(FACING) == facing
                && state.getValue(COLUMN) == column;
    }

    private static boolean supported(LevelReader level, BlockPos pos, Direction facing) {
        BlockPos support = pos.relative(facing.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, facing);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return supported(level, pos, state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        if (facing.getAxis().isVertical()) return null;
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos origin = context.getClickedPos();
        BlockState base = defaultBlockState().setValue(FACING, facing);
        for (int row = 0; row < 1; row++) {
            for (int column = 0; column < 2; column++) {
                BlockPos target = cell(origin, facing, column, row);
                if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                        || !level.getWorldBorder().isWithinBounds(target)
                        || !level.getBlockState(target).canBeReplaced(context)
                        || !supported(level, target, facing)
                        || !level.isUnobstructed(base.setValue(COLUMN, column), target, CollisionContext.empty())
                        || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) return null;
            }
        }
        return base;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        for (int row = 0; row < 1; row++) {
            for (int column = 0; column < 2; column++) {
                if (column == 0 && row == 0) continue;
                level.setBlock(cell(pos, state.getValue(FACING), column, row),
                        state.setValue(COLUMN, column), Block.UPDATE_CLIENTS);
            }
        }
        for (int row = 0; row < 1; row++) {
            for (int column = 0; column < 2; column++) {
                level.updateNeighborsAt(cell(pos, state.getValue(FACING), column, row), this);
            }
        }
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos origin = origin(pos, state);
        Direction facing = state.getValue(FACING);
        boolean complete = true;
        for (int row = 0; row < 1; row++) {
            for (int column = 0; column < 2; column++) {
                BlockPos target = cell(origin, facing, column, row);
                if (!level.hasChunkAt(target)) {
                    level.scheduleTick(pos, this, 20);
                    return;
                }
                complete &= matches(level.getBlockState(target), facing, column, row) && supported(level, target, facing);
            }
        }
        if (!complete) dismantle(level, origin, facing, true, null);
    }

    private void dismantle(Level level, BlockPos origin, Direction facing, boolean drop, BlockPos mined) {
        if (!origin.equals(mined) && matches(level.getBlockState(origin), facing, 0, 0)) level.destroyBlock(origin, drop);
        for (int row = 0; row < 1; row++) {
            for (int column = 0; column < 2; column++) {
                BlockPos target = cell(origin, facing, column, row);
                if (!target.equals(mined) && level.hasChunkAt(target)
                        && matches(level.getBlockState(target), facing, column, row)) {
                    level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) dismantle(level, origin(pos, state), state.getValue(FACING), !player.isCreative(), pos);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISIONS[state.getValue(COLUMN)][switch (state.getValue(FACING)) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        }];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(FACING);
        Direction reflected = mirror.mirror(facing);
        // Reflection may move the controller to the opposite bottom corner.
        int column = mirror.mirror(facing.getClockWise()) == reflected.getClockWise()
                ? state.getValue(COLUMN) : 1 - state.getValue(COLUMN);
        return state.setValue(FACING, reflected).setValue(COLUMN, column);
    }
}
