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

/** One item places an 8-wide, 6-high, 3-deep goal and basketball hoop. */
public class SportsGoalBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty CELL = IntegerProperty.create("cell", 0, 143);
    private static final VoxelShape[][] COLLISION = buildShapes();

    public SportsGoalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CELL, 0));
    }

    private static VoxelShape[][] buildShapes() {
        VoxelShape[][] result = new VoxelShape[144][4];
        for (int i = 0; i < 144; i++) {
            for (int turn = 0; turn < 4; turn++) {
                VoxelShape shape = Shapes.empty();
                for (double[] source : SportsGoalShapes.CELLS[i]) {
                    double[] b = source.clone();
                    for (int t = 0; t < turn; t++) b = new double[]{16-b[5], b[1], b[0], 16-b[2], b[4], b[3]};
                    shape = Shapes.or(shape, Block.box(b[0], b[1], b[2], b[3], b[4], b[5]));
                }
                result[i][turn] = shape.optimize();
            }
        }
        return result;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, CELL); }

    private static BlockPos cell(BlockPos origin, Direction facing, int index) {
        return origin.relative(facing.getClockWise(), index % 8).above(index / 8 % 6)
                .relative(facing.getOpposite(), index / 48);
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        int index = state.getValue(CELL);
        Direction facing = state.getValue(FACING);
        return pos.relative(facing.getClockWise(), -(index % 8)).below(index / 8 % 6).relative(facing, index / 48);
    }

    private boolean matches(BlockState state, Direction facing, int index) {
        return state.is(this) && state.getValue(FACING) == facing && state.getValue(CELL) == index;
    }

    private static boolean supported(LevelReader level, BlockPos origin, Direction facing) {
        for (int index : new int[]{0, 7, 96, 103}) {
            BlockPos below = cell(origin, facing, index).below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) return false;
        }
        return true;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return supported(level, origin(pos, state), state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos origin = context.getClickedPos();
        BlockState base = defaultBlockState().setValue(FACING, facing);
        for (int index = 0; index < 144; index++) {
            BlockPos target = cell(origin, facing, index);
            if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                    || !level.getWorldBorder().isWithinBounds(target)
                    || !level.getBlockState(target).canBeReplaced(context)
                    || !level.isUnobstructed(base.setValue(CELL, index), target, CollisionContext.empty())
                    || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) return null;
        }
        return supported(level, origin, facing) ? base : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        Direction facing = state.getValue(FACING);
        for (int index : SportsGoalShapes.OCCUPIED) {
            if (index != 0) level.setBlock(cell(pos, facing, index), state.setValue(CELL, index), Block.UPDATE_CLIENTS);
        }
        for (int index : SportsGoalShapes.OCCUPIED) level.updateNeighborsAt(cell(pos, facing, index), this);
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
        for (int index : SportsGoalShapes.OCCUPIED) {
            BlockPos target = cell(origin, facing, index);
            if (!level.hasChunkAt(target)) { level.scheduleTick(pos, this, 20); return; }
            complete &= matches(level.getBlockState(target), facing, index);
        }
        if (!complete || !supported(level, origin, facing)) dismantle(level, origin, facing, true, null);
    }

    private void dismantle(Level level, BlockPos origin, Direction facing, boolean drop, BlockPos mined) {
        if (level.hasChunkAt(origin) && !origin.equals(mined) && matches(level.getBlockState(origin), facing, 0))
            level.destroyBlock(origin, drop);
        for (int index : SportsGoalShapes.OCCUPIED) {
            BlockPos target = cell(origin, facing, index);
            if (!target.equals(mined) && level.hasChunkAt(target) && matches(level.getBlockState(target), facing, index))
                level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
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
        int turn = switch (state.getValue(FACING)) { case EAST -> 1; case SOUTH -> 2; case WEST -> 3; default -> 0; };
        return COLLISION[state.getValue(CELL)][turn];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(FACING);
        Direction reflected = mirror.mirror(facing);
        int index = state.getValue(CELL);
        int column = mirror.mirror(facing.getClockWise()) == reflected.getClockWise() ? index % 8 : 7 - index % 8;
        return state.setValue(FACING, reflected).setValue(CELL, column + 8 * (index / 8 % 6) + 48 * (index / 48));
    }
}
