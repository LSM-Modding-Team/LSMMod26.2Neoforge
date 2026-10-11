package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A single placement creates the complete six-block inclined wooden handrail. */
public class SchoolWoodenHandrailBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 5);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 2);

    public SchoolWoodenHandrailBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(COLUMN, 0).setValue(ROW, 0).setValue(RIGHT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLUMN, ROW, RIGHT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();
        Player player = context.getPlayer();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 6; column++) {
                if (!occupied(column, row)) continue;
                BlockPos target = tile(origin, facing, column, row);
                if (!level.getWorldBorder().isWithinBounds(target) || level.isOutsideBuildHeight(target)
                        || !level.hasChunkAt(target) || !level.getBlockState(target).canBeReplaced(context)
                        || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) {
                    return null;
                }
            }
        }
        Direction right = facing.getClockWise();
        BlockPos rightPos = origin.relative(right), leftPos = origin.relative(right.getOpposite());
        boolean onRight;
        if (hasRamp(level, rightPos)) onRight = true;
        else if (hasRamp(level, leftPos)) onRight = false;
        else {
            double x = context.getClickLocation().x - origin.getX() - 0.5;
            double z = context.getClickLocation().z - origin.getZ() - 0.5;
            onRight = x * right.getStepX() + z * right.getStepZ() >= 0;
        }
        return defaultBlockState().setValue(FACING, facing).setValue(RIGHT, onRight);
    }

    private static boolean hasRamp(Level level, BlockPos pos) {
        for (int dy = -2; dy <= 1; dy++) {
            BlockPos candidate = pos.above(dy);
            if (level.hasChunkAt(candidate) && level.getBlockState(candidate).getBlock() instanceof EntrancePavementSlopeBlock) return true;
        }
        return false;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        Direction facing = state.getValue(FACING);
        // One item creates all occupied cells, with one controller and one drop.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 6; column++) {
                if (!occupied(column, row) || (column == 0 && row == 0)) continue;
                level.setBlock(tile(pos, facing, column, row), state.setValue(COLUMN, column)
                        .setValue(ROW, row), Block.UPDATE_CLIENTS);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 6; column++) {
                if (occupied(column, row)) level.updateNeighborsAt(tile(pos, facing, column, row), this);
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int column = state.getValue(COLUMN), row = state.getValue(ROW);
        Direction facing = state.getValue(FACING);
        double edge = state.getValue(RIGHT) ? 14 : 0;
        VoxelShape shape = net.minecraft.world.phys.shapes.Shapes.empty();
        for (int i = 0; i < 16; i++) {
            double low = 0;
            double high = Math.min(16, column * 4 + (i + 1) / 4.0 + 16 - row * 16);
            if (high <= low) continue;
            VoxelShape slice = switch (facing) {
                case NORTH -> Block.box(edge, low, 15-i, edge+2, high, 16-i);
                case SOUTH -> Block.box(14-edge, low, i, 16-edge, high, i+1);
                case EAST -> Block.box(i, low, edge, i+1, high, edge+2);
                default -> Block.box(15-i, low, 14-edge, 16-i, high, 16-edge);
            };
            shape = net.minecraft.world.phys.shapes.Shapes.or(shape, slice);
        }
        return shape;
    }

    private static boolean occupied(int column, int row) {
        return row * 16 < column * 4 + 20;
    }

    private static BlockPos tile(BlockPos center, Direction facing, int column, int row) {
        return center.relative(facing, column).above(row);
    }

    private static BlockPos center(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(FACING), -state.getValue(COLUMN)).below(state.getValue(ROW));
    }

    private boolean matches(BlockState candidate, Direction facing, int column, int row) {
        return candidate.is(this) && candidate.getValue(FACING) == facing
                && candidate.getValue(COLUMN) == column && candidate.getValue(ROW) == row;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos origin = center(pos, state);
        Direction facing = state.getValue(FACING);
        boolean complete = true;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 6; column++) {
                if (!occupied(column, row)) continue;
                BlockPos target = tile(origin, facing, column, row);
                if (!level.hasChunkAt(target)) {
                    level.scheduleTick(pos, this, 20);
                    return;
                }
                complete &= matches(level.getBlockState(target), facing, column, row);
            }
        }
        if (!complete) dismantle(level, origin, facing, true);
    }

    private void dismantle(Level level, BlockPos origin, Direction facing, boolean drop) {
        // Only the controller has a loot drop. Clear it first, then only matching satellite tiles.
        if (matches(level.getBlockState(origin), facing, 0, 0)) level.destroyBlock(origin, drop);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 6; column++) {
                if (!occupied(column, row)) continue;
                BlockPos target = tile(origin, facing, column, row);
                if (matches(level.getBlockState(target), facing, column, row)) {
                    level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos origin = center(pos, state);
            // When the mined tile is the controller, vanilla handles its single drop.
            if (!origin.equals(pos)) dismantle(level, origin, state.getValue(FACING), !player.isCreative());
            else {
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < 6; column++) {
                        if (!occupied(column, row) || (column == 0 && row == 0)) continue;
                        BlockPos target = tile(origin, state.getValue(FACING), column, row);
                        if (matches(level.getBlockState(target), state.getValue(FACING), column, row)) {
                            level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                        }
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
