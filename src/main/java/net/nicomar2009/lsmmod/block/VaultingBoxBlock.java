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

/** Four-cell gymnastics vaulting box with a single placement and drop. */
public class VaultingBoxBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty SEGMENT = IntegerProperty.create("segment", 0, 1);
    public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 1);

    public VaultingBoxBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(SEGMENT, 0).setValue(LAYER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SEGMENT, LAYER);
    }

    private static BlockPos cell(BlockPos root, BlockState state, int segment, int layer) {
        return root.relative(state.getValue(FACING), segment).above(layer);
    }

    private static BlockPos anchor(BlockPos pos, BlockState state) {
        return cell(pos, state, -state.getValue(SEGMENT), -state.getValue(LAYER));
    }

    private boolean complete(LevelReader level, BlockPos root, BlockState state) {
        for (int segment = 0; segment < 2; segment++) {
            for (int layer = 0; layer < 2; layer++) {
                BlockState other = level.getBlockState(cell(root, state, segment, layer));
                if (!other.is(this) || other.getValue(SEGMENT) != segment || other.getValue(LAYER) != layer
                        || other.getValue(FACING) != state.getValue(FACING)) return false;
            }
        }
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        Level level = context.getLevel();
        for (int segment = 0; segment < 2; segment++) {
            for (int layer = 0; layer < 2; layer++) {
                BlockPos pos = cell(context.getClickedPos(), state, segment, layer);
                if (pos.getY() >= level.getMaxY() || !level.getWorldBorder().isWithinBounds(pos) || !level.getBlockState(pos).canBeReplaced(context)) return null;
            }
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) return;
        for (int segment = 0; segment < 2; segment++) {
            for (int layer = 0; layer < 2; layer++) {
                if (segment == 0 && layer == 0) continue;
                level.setBlock(cell(pos, state, segment, layer), state.setValue(SEGMENT, segment).setValue(LAYER, layer), Block.UPDATE_CLIENTS);
            }
        }
        for (int segment = 0; segment < 2; segment++) {
            for (int layer = 0; layer < 2; layer++) level.updateNeighborsAt(cell(pos, state, segment, layer), this);
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
            for (int segment = 0; segment < 2; segment++) {
                for (int layer = 0; layer < 2; layer++) {
                    BlockPos otherPos = cell(root, state, segment, layer);
                    BlockState other = level.getBlockState(otherPos);
                    if (!otherPos.equals(pos) && other.is(this) && other.getValue(SEGMENT) == segment
                            && other.getValue(LAYER) == layer && other.getValue(FACING) == state.getValue(FACING)) {
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
        return VaultingBoxShapes.SHAPES[state.getValue(SEGMENT)][state.getValue(LAYER)][state.getValue(FACING).get2DDataValue()];
    }
}
