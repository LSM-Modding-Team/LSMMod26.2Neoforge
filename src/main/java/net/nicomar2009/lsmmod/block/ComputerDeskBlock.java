package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.nicomar2009.lsmmod.registry.ModItems;

/** One fixed PC slot, persisted in block state and shared by both vertical halves. */
public class ComputerDeskBlock extends TwoTallBlock {
    public static final BooleanProperty HAS_PC = BooleanProperty.create("has_pc");

    public ComputerDeskBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.SOUTH).setValue(HAS_PC, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_PC);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ComputerDeskShapes.SHAPES[state.getValue(HALF) == DoubleBlockHalf.UPPER ? 1 : 0]
                [state.getValue(HAS_PC) ? 1 : 0][state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(ModItems.PC.get())) return InteractionResult.FAIL;
        if (state.getValue(HAS_PC)) return InteractionResult.SUCCESS;
        if (!level.isClientSide()) {
            if (!setOccupied(level, pos, state, true)) return InteractionResult.FAIL;
            if (!player.isCreative()) stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown() || !state.getValue(HAS_PC)) return InteractionResult.PASS;
        if (!level.isClientSide() && setOccupied(level, pos, state, false)) {
            ItemStack pc = new ItemStack(ModItems.PC.get());
            if (!player.getInventory().add(pc)) player.drop(pc, false);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean setOccupied(Level level, BlockPos pos, BlockState state, boolean occupied) {
        BlockPos lower = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState bottom = level.getBlockState(lower);
        BlockState top = level.getBlockState(lower.above());
        if (!bottom.is(this) || !top.is(this)
                || bottom.getValue(HALF) != DoubleBlockHalf.LOWER
                || top.getValue(HALF) != DoubleBlockHalf.UPPER) return false;
        level.setBlock(lower, bottom.setValue(HAS_PC, occupied), Block.UPDATE_CLIENTS);
        level.setBlock(lower.above(), top.setValue(HAS_PC, occupied), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                   Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        BlockState result = super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
        if (result.is(this) && direction == (state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN)
                && neighborState.is(this)) {
            return result.setValue(HAS_PC, neighborState.getValue(HAS_PC));
        }
        return result;
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
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
