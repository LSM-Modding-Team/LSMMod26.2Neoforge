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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Two-cell fixture with a lower controller. Only the toilet has an operable cubicle door. */
public class TallBathroomBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private final boolean wallMounted;
    private final VoxelShape[][][] collisions = new VoxelShape[2][2][4];

    public TallBathroomBlock(Properties properties, double[][] closed, double[][] opened, boolean wallMounted) {
        super(properties);
        this.wallMounted = wallMounted;
        for (int open = 0; open < 2; open++) {
            for (int half = 0; half < 2; half++) {
                VoxelShape shape = Shapes.empty();
                for (double[] b : open == 0 ? closed : opened) {
                    double low = Math.max(b[1], half*16), high = Math.min(b[4], half*16+16);
                    if (low < high) shape = Shapes.or(shape, Block.box(b[0], low-half*16, b[2], b[3], high-half*16, b[5]));
                }
                collisions[open][half][0] = shape;
                for (int facing = 1; facing < 4; facing++) {
                    VoxelShape[] next = {Shapes.empty()};
                    collisions[open][half][facing-1].forAllBoxes((x1,y1,z1,x2,y2,z2) ->
                            next[0] = Shapes.or(next[0], Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                    collisions[open][half][facing] = next[0];
                }
            }
        }
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, OPEN);
    }

    private static BlockPos base(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    private boolean matches(BlockState state, Direction facing, DoubleBlockHalf half) {
        return state.is(this) && state.getValue(FACING) == facing && state.getValue(HALF) == half;
    }

    private boolean supported(LevelReader level, BlockPos pos, Direction facing) {
        BlockPos support = wallMounted ? pos.relative(facing.getOpposite()) : pos.below();
        return level.getBlockState(support).isFaceSturdy(level, support, wallMounted ? facing : Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (wallMounted ? face.getAxis().isVertical() : face != Direction.UP) return null;
        Direction facing = wallMounted ? face : context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = defaultBlockState().setValue(FACING, facing);
        for (int half = 0; half < 2; half++) {
            BlockPos target = pos.above(half);
            BlockState part = state.setValue(HALF, half == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER);
            if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                    || !level.getWorldBorder().isWithinBounds(target) || !level.getBlockState(target).canBeReplaced(context)
                    || !level.isUnobstructed(part, target, CollisionContext.empty())
                    || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) return null;
            if ((wallMounted || half == 0) && !supported(level, target, facing)) return null;
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.above(), this);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (wallMounted || !player.mayBuild()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockPos lower = base(pos, state);
        Direction facing = state.getValue(FACING);
        BlockState bottom = level.getBlockState(lower), top = level.getBlockState(lower.above());
        if (!matches(bottom, facing, DoubleBlockHalf.LOWER) || !matches(top, facing, DoubleBlockHalf.UPPER)) return InteractionResult.PASS;
        boolean open = !bottom.getValue(OPEN);
        for (int half = 0; half < 2; half++) {
            BlockPos target = lower.above(half);
            BlockState next = (half == 0 ? bottom : top).setValue(OPEN, open);
            if (!level.mayInteract(player, target) || !level.isUnobstructed(next, target, CollisionContext.empty())) return InteractionResult.PASS;
        }
        level.setBlock(lower, bottom.setValue(OPEN, open), Block.UPDATE_CLIENTS);
        level.setBlock(lower.above(), top.setValue(OPEN, open), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(lower, this);
        level.updateNeighborsAt(lower.above(), this);
        level.playSound(null, lower, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.6F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos lower = base(pos, state);
        if (!level.hasChunkAt(lower) || !level.hasChunkAt(lower.above())) {
            level.scheduleTick(pos, this, 20);
            return;
        }
        Direction facing = state.getValue(FACING);
        BlockState bottom = level.getBlockState(lower), top = level.getBlockState(lower.above());
        boolean complete = matches(bottom, facing, DoubleBlockHalf.LOWER) && matches(top, facing, DoubleBlockHalf.UPPER)
                && bottom.getValue(OPEN) == top.getValue(OPEN) && supported(level, lower, facing)
                && (!wallMounted || supported(level, lower.above(), facing));
        if (!complete) dismantle(level, lower, facing, true, null);
    }

    private void dismantle(Level level, BlockPos lower, Direction facing, boolean drop, BlockPos mined) {
        if (!lower.equals(mined) && matches(level.getBlockState(lower), facing, DoubleBlockHalf.LOWER)) level.destroyBlock(lower, drop);
        for (int half = 0; half < 2; half++) {
            BlockPos target = lower.above(half);
            if (!target.equals(mined) && matches(level.getBlockState(target), facing, half == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER))
                level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) dismantle(level, base(pos, state), state.getValue(FACING), !player.isCreative(), pos);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int facing = switch (state.getValue(FACING)) { case EAST -> 1; case SOUTH -> 2; case WEST -> 3; default -> 0; };
        return collisions[state.getValue(OPEN) ? 1 : 0][state.getValue(HALF) == DoubleBlockHalf.UPPER ? 1 : 0][facing];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
}
