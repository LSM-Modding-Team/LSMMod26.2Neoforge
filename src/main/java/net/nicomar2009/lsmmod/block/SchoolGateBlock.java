package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A 5x3 double gate, controlled by its bottom center cell. Both leaves swing together. */
public class SchoolGateBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 4);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 2);
    public static final IntegerProperty DEPTH = IntegerProperty.create("depth", 0, 2);

    public SchoolGateBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false).setValue(COLUMN, 2).setValue(ROW, 0).setValue(DEPTH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, COLUMN, ROW, DEPTH);
    }

    private static BlockPos cell(BlockPos origin, Direction facing, int column, int row, int depth) {
        return origin.relative(facing.getClockWise(), column - 2).above(row)
                .relative(facing.getOpposite(), depth);
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        return pos.relative(facing.getClockWise(), 2 - state.getValue(COLUMN))
                .below(state.getValue(ROW)).relative(facing, state.getValue(DEPTH));
    }

    private boolean matches(BlockState state, Direction facing, int column, int row, int depth) {
        return state.is(this) && state.getValue(FACING) == facing && state.getValue(COLUMN) == column
                && state.getValue(ROW) == row && state.getValue(DEPTH) == depth;
    }

    private static boolean occupied(int column, int depth, boolean open) {
        // Keep the original plane as empty controller cells while the leaves are open.
        return depth == 0 || (open && (column == 0 || column == 4));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        Player player = context.getPlayer();
        BlockState base = defaultBlockState().setValue(FACING, facing);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                BlockPos target = cell(origin, facing, column, row, 0);
                BlockState part = base.setValue(COLUMN, column).setValue(ROW, row);
                if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                        || !level.getWorldBorder().isWithinBounds(target)
                        || !level.getBlockState(target).canBeReplaced(context)
                        || !level.isUnobstructed(part, target, CollisionContext.empty())
                        || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) return null;
            }
        }
        return supported(level, origin, facing) ? base : null;
    }

    private static boolean supported(LevelReader level, BlockPos origin, Direction facing) {
        for (int column : new int[]{0, 4}) {
            BlockPos below = cell(origin, facing, column, 0, 0).below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) return false;
        }
        return true;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                if (column == 2 && row == 0) continue;
                level.setBlock(cell(pos, state.getValue(FACING), column, row, 0),
                        state.setValue(COLUMN, column).setValue(ROW, row), Block.UPDATE_CLIENTS);
            }
        }
        notifyCells(level, pos, state.getValue(FACING));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!player.mayBuild()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockPos origin = origin(pos, state);
        Direction facing = state.getValue(FACING);
        BlockState controller = level.getBlockState(origin);
        if (!matches(controller, facing, 2, 0, 0)) return InteractionResult.PASS;
        boolean open = !controller.getValue(OPEN);
        // Validate the entire operation before moving either leaf. A blocked swing changes nothing.
        for (int depth = 0; depth < 3; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 5; column++) {
                    if (!occupied(column, depth, open || controller.getValue(OPEN))) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                            || !level.getWorldBorder().isWithinBounds(target)
                            || !level.mayInteract(player, target)) return InteractionResult.PASS;
                    BlockState existing = level.getBlockState(target);
                    if (occupied(column, depth, controller.getValue(OPEN))) {
                        if (!matches(existing, facing, column, row, depth)
                                || existing.getValue(OPEN) != controller.getValue(OPEN)) return InteractionResult.PASS;
                    } else if (!existing.isAir()) return InteractionResult.PASS;
                    if (occupied(column, depth, open)) {
                        BlockState next = controller.setValue(OPEN, open).setValue(COLUMN, column)
                                .setValue(ROW, row).setValue(DEPTH, depth);
                        if (!level.isUnobstructed(next, target, CollisionContext.empty())) return InteractionResult.PASS;
                    }
                }
            }
        }
        for (int depth = 0; depth < 3; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 5; column++) {
                    if (!occupied(column, depth, open || controller.getValue(OPEN))) continue;
                    BlockState next = occupied(column, depth, open)
                            ? controller.setValue(OPEN, open).setValue(COLUMN, column).setValue(ROW, row).setValue(DEPTH, depth)
                            : Blocks.AIR.defaultBlockState();
                    level.setBlock(cell(origin, facing, column, row, depth), next,
                            Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        notifyCells(level, origin, facing);
        level.playSound(null, origin, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private void notifyCells(Level level, BlockPos origin, Direction facing) {
        for (int depth = 0; depth < 3; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 5; column++) {
                    if (depth != 0 && column != 0 && column != 4) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (level.hasChunkAt(target)) level.updateNeighborsAt(target, this);
                }
            }
        }
        level.scheduleTick(origin, this, 1);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int column = state.getValue(COLUMN);
        int depth = state.getValue(DEPTH);
        if (!occupied(column, depth, state.getValue(OPEN))) return Shapes.empty();
        double x1 = 0, z1 = 7, x2 = 16, z2 = 9;
        if (state.getValue(OPEN)) {
            if (column != 0 && column != 4) return Shapes.empty();
            x1 = column == 0 ? 0 : 14;
            x2 = x1 + 2;
            z1 = depth == 0 ? 8 : 0;
            z2 = 16;
        }
        return switch (state.getValue(FACING)) {
            case EAST -> Block.box(16-z2, 0, x1, 16-z1, 16, x2);
            case SOUTH -> Block.box(16-x2, 0, 16-z2, 16-x1, 16, 16-z1);
            case WEST -> Block.box(z1, 0, 16-x2, z2, 16, 16-x1);
            default -> Block.box(x1, 0, z1, x2, 16, z2);
        };
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
        for (int depth = 0; depth < 3; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 5; column++) {
                    if (!occupied(column, depth, state.getValue(OPEN))) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (!level.hasChunkAt(target)) {
                        level.scheduleTick(pos, this, 20);
                        return;
                    }
                    BlockState part = level.getBlockState(target);
                    complete &= matches(part, facing, column, row, depth)
                            && part.getValue(OPEN) == state.getValue(OPEN);
                }
            }
        }
        if (!complete || !supported(level, origin, facing)) dismantle(level, origin, facing, true, null);
    }

    private void dismantle(Level level, BlockPos origin, Direction facing, boolean drop, BlockPos mined) {
        if (!origin.equals(mined) && matches(level.getBlockState(origin), facing, 2, 0, 0)) {
            level.destroyBlock(origin, drop);
        }
        for (int depth = 0; depth < 3; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 5; column++) {
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (target.equals(mined)) continue;
                    if (level.hasChunkAt(target) && matches(level.getBlockState(target), facing, column, row, depth)) {
                        level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                    }
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
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        // Reflect both the direction and the cell's local coordinate, keeping the controller consistent.
        Direction facing = state.getValue(FACING);
        Direction reflected = mirror.mirror(facing);
        Direction right = facing.getClockWise();
        int column = mirror.mirror(right) == reflected.getClockWise()
                ? state.getValue(COLUMN) : 4 - state.getValue(COLUMN);
        return state.setValue(FACING, reflected).setValue(COLUMN, column);
    }
}
