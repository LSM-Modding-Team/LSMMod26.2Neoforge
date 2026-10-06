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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Eight-cell wooden art table with a single placement and drop. */
public class ArtTableBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 1);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 3);

    public ArtTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(COLUMN, 0).setValue(ROW, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLUMN, ROW);
    }

    private static BlockPos cell(BlockPos root, BlockState state, int column, int row) {
        return root.relative(state.getValue(FACING).getCounterClockWise(), column).relative(state.getValue(FACING), row);
    }

    private static BlockPos anchor(BlockPos pos, BlockState state) {
        return cell(pos, state, -state.getValue(COLUMN), -state.getValue(ROW));
    }

    private boolean complete(LevelReader level, BlockPos root, BlockState state) {
        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 4; row++) {
                BlockState other = level.getBlockState(cell(root, state, column, row));
                if (!other.is(this) || other.getValue(COLUMN) != column || other.getValue(ROW) != row
                        || other.getValue(FACING) != state.getValue(FACING)) return false;
            }
        }
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        Level level = context.getLevel();
        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 4; row++) {
                BlockPos pos = cell(context.getClickedPos(), state, column, row);
                if (!level.getWorldBorder().isWithinBounds(pos) || !level.getBlockState(pos).canBeReplaced(context)) return null;
            }
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) return;
        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 4; row++) {
                if (column == 0 && row == 0) continue;
                level.setBlock(cell(pos, state, column, row), state.setValue(COLUMN, column).setValue(ROW, row), Block.UPDATE_CLIENTS);
            }
        }
        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 4; row++) level.updateNeighborsAt(cell(pos, state, column, row), this);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return complete(level, anchor(pos, state), state) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            BlockPos root = anchor(pos, state);
            for (int column = 0; column < 2; column++) {
                for (int row = 0; row < 4; row++) {
                    BlockPos otherPos = cell(root, state, column, row);
                    BlockState other = level.getBlockState(otherPos);
                    if (!otherPos.equals(pos) && other.is(this) && other.getValue(COLUMN) == column
                            && other.getValue(ROW) == row && other.getValue(FACING) == state.getValue(FACING)) {
                        level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ArtTableShapes.SHAPES[state.getValue(COLUMN)][state.getValue(ROW)][state.getValue(FACING).get2DDataValue()];
    }
}
