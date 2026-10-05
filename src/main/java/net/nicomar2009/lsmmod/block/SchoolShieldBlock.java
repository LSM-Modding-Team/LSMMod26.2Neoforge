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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A single placement creates the complete 3x3 floor crest, centered on the clicked block. */
public class SchoolShieldBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 2);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 2);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public SchoolShieldBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(COLUMN, 1).setValue(ROW, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLUMN, ROW);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();
        Player player = context.getPlayer();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                BlockPos target = tile(origin, facing, column, row);
                if (!level.getWorldBorder().isWithinBounds(target) || level.isOutsideBuildHeight(target)
                        || !level.hasChunkAt(target) || !level.getBlockState(target).canBeReplaced(context)
                        || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) {
                    return null;
                }
            }
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        Direction facing = state.getValue(FACING);
        // Placement, like a bed: the item places the center and creates all eight other tiles immediately.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (column == 1 && row == 1) continue;
                level.setBlock(tile(pos, facing, column, row), state.setValue(COLUMN, column)
                        .setValue(ROW, row), Block.UPDATE_CLIENTS);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                level.updateNeighborsAt(tile(pos, facing, column, row), this);
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static BlockPos tile(BlockPos center, Direction facing, int column, int row) {
        return center.relative(facing.getClockWise(), column - 1).relative(facing, 1 - row);
    }

    private static BlockPos center(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(FACING).getClockWise(), 1 - state.getValue(COLUMN))
                .relative(state.getValue(FACING), state.getValue(ROW) - 1);
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
            for (int column = 0; column < 3; column++) {
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
        if (matches(level.getBlockState(origin), facing, 1, 1)) level.destroyBlock(origin, drop);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
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
                    for (int column = 0; column < 3; column++) {
                        if (column == 1 && row == 1) continue;
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
