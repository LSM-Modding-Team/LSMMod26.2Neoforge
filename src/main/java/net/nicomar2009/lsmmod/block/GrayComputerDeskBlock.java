package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Four-cell desk with one shared PC slot, anchored at the lower left cell. */
public class GrayComputerDeskBlock extends ComputerDeskBlock {
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 1);

    public GrayComputerDeskBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLUMN, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLUMN);
    }

    private static Direction width(BlockState state) {
        return state.getValue(FACING).getCounterClockWise();
    }

    private static BlockPos anchor(BlockPos pos, BlockState state) {
        return pos.relative(width(state), -state.getValue(COLUMN))
                .below(state.getValue(HALF) == DoubleBlockHalf.UPPER ? 1 : 0);
    }

    private boolean complete(LevelReader level, BlockPos root, BlockState state) {
        for (int column = 0; column < 2; column++) {
            for (int half = 0; half < 2; half++) {
                BlockState cell = level.getBlockState(root.relative(width(state), column).above(half));
                if (!cell.is(this) || cell.getValue(COLUMN) != column
                        || cell.getValue(HALF) != (half == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER)
                        || cell.getValue(FACING) != state.getValue(FACING)) return false;
            }
        }
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        BlockPos root = context.getClickedPos();
        Level level = context.getLevel();
        for (int column = 0; column < 2; column++) {
            for (int half = 0; half < 2; half++) {
                BlockPos cell = root.relative(width(state), column).above(half);
                if (cell.getY() >= level.getMaxY() || !level.getWorldBorder().isWithinBounds(cell)
                        || !level.getBlockState(cell).canBeReplaced(context)) return null;
            }
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) return;
        for (int column = 0; column < 2; column++) {
            for (int half = 0; half < 2; half++) {
                if (column == 0 && half == 0) continue;
                level.setBlock(pos.relative(width(state), column).above(half), state.setValue(COLUMN, column)
                        .setValue(HALF, half == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER), Block.UPDATE_CLIENTS);
            }
        }
        notifyCells(level, pos, state);
    }

    private void notifyCells(Level level, BlockPos root, BlockState state) {
        for (int column = 0; column < 2; column++) {
            for (int half = 0; half < 2; half++) {
                level.updateNeighborsAt(root.relative(width(state), column).above(half), this);
            }
        }
    }

    @Override
    protected boolean setOccupied(Level level, BlockPos pos, BlockState state, boolean occupied) {
        BlockPos root = anchor(pos, state);
        if (!complete(level, root, state)) return false;
        for (int column = 0; column < 2; column++) {
            for (int half = 0; half < 2; half++) {
                BlockPos cell = root.relative(width(state), column).above(half);
                level.setBlock(cell, level.getBlockState(cell).setValue(HAS_PC, occupied), Block.UPDATE_CLIENTS);
            }
        }
        notifyCells(level, root, state);
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                   Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return complete(level, anchor(pos, state), state) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            BlockPos root = anchor(pos, state);
            for (int column = 0; column < 2; column++) {
                for (int half = 0; half < 2; half++) {
                    BlockPos cell = root.relative(width(state), column).above(half);
                    if (!cell.equals(pos) && level.getBlockState(cell).is(this)) {
                        level.setBlock(cell, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return GrayComputerDeskShapes.SHAPES[state.getValue(COLUMN)]
                [state.getValue(HALF) == DoubleBlockHalf.UPPER ? 1 : 0]
                [state.getValue(HAS_PC) ? 1 : 0][state.getValue(FACING).get2DDataValue()];
    }
}
