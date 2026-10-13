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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Rectangular school fixture; one controller, exact cell collisions and one item drop. */
public class SchoolRectangularBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 3);
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 5);
    private final boolean wallMounted;
    private final int height;
    private final int width;
    private final boolean operable;
    private final VoxelShape[][][][] collisions;

    protected SchoolRectangularBlock(Properties properties, double[][] closed, double[][] opened,
                                     boolean wallMounted, int width, int height, boolean operable) {
        super(properties);
        this.wallMounted = wallMounted;
        this.height = height;
        this.width = width;
        this.operable = operable;
        collisions = new VoxelShape[2][height][width][4];
        var sharedRotations = new java.util.HashMap<java.util.List<net.minecraft.world.phys.AABB>, java.util.Map<Direction,VoxelShape>>();
        for (int open = 0; open < 2; open++) {
            if (open == 1 && closed == opened) { collisions[open] = collisions[0]; continue; }
            for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
                java.util.List<net.minecraft.world.phys.AABB> volumes = new java.util.ArrayList<>();
                for (double[] b : open == 0 ? closed : opened) {
                    double x0 = Math.max(b[0], column*16), x1 = Math.min(b[3], column*16+16);
                    double y0 = Math.max(b[1], half*16), y1 = Math.min(b[4], half*16+16);
                    if (x0 < x1 && y0 < y1) volumes.add(ExactVoxelShapes.box(
                            x0-column*16, y0-half*16, b[2], x1-column*16, y1-half*16, b[5]));
                }
                var rotations = sharedRotations.computeIfAbsent(java.util.List.copyOf(volumes),
                        boxes -> Shapes.rotateHorizontal(ExactVoxelShapes.union(boxes)));
                for (Direction facing : Direction.Plane.HORIZONTAL)
                    collisions[open][half][column][(facing.get2DDataValue()+2)%4] = rotations.get(facing);
            }
        }
        BlockState initial = stateDefinition.any().setValue(FACING, Direction.NORTH);
        if(initial.hasProperty(columnProperty())) initial=initial.setValue(columnProperty(),0);
        if(initial.hasProperty(HALF)) initial=initial.setValue(HALF,DoubleBlockHalf.LOWER);
        if (initial.hasProperty(rowProperty())) initial = initial.setValue(rowProperty(), 0);
        if (initial.hasProperty(OPEN)) initial = initial.setValue(OPEN, false);
        registerDefaultState(initial);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, columnProperty());
    }

    private int half(BlockState state) {
        if (state.hasProperty(rowProperty())) return state.getValue(rowProperty());
        return state.hasProperty(HALF) && state.getValue(HALF)==DoubleBlockHalf.UPPER ? 1 : 0;
    }

    private static boolean opened(BlockState state) { return state.hasProperty(OPEN) && state.getValue(OPEN); }

    protected IntegerProperty columnProperty() { return COLUMN; }

    protected IntegerProperty rowProperty() { return ROW; }

    private int column(BlockState state) { return state.hasProperty(columnProperty()) ? state.getValue(columnProperty()) : 0; }

    private static Direction across(Direction facing) { return facing.getClockWise(); }

    private BlockPos base(BlockPos pos, BlockState state) {
        return pos.below(half(state)).relative(across(state.getValue(FACING)), -column(state));
    }

    private BlockPos cell(BlockPos base, Direction facing, int half, int column) {
        return base.above(half).relative(across(facing), column);
    }

    private BlockState part(BlockState state, int half, int column) {
        BlockState value = state.hasProperty(columnProperty()) ? state.setValue(columnProperty(), column) : state;
        if (value.hasProperty(rowProperty())) return value.setValue(rowProperty(), half);
        return value.hasProperty(HALF) ? value.setValue(HALF,half==0?DoubleBlockHalf.LOWER:DoubleBlockHalf.UPPER) : value;
    }

    private boolean matches(BlockState state, Direction facing, int half, int column) {
        return state.is(this) && state.getValue(FACING) == facing && half(state) == half && column(state) == column;
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
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            BlockPos target = cell(pos, facing, half, column);
            BlockState part = part(state, half, column);
            if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                    || !level.getWorldBorder().isWithinBounds(target) || !level.getBlockState(target).canBeReplaced(context)
                    || !level.isUnobstructed(part, target, CollisionContext.empty())
                    || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)
                        || !player.mayUseItemAt(target, context.getClickedFace(), context.getItemInHand())))) return null;
            if ((wallMounted || half == 0) && !supported(level, target, facing)) return null;
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        Direction facing = state.getValue(FACING);
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            if (half != 0 || column != 0)
                level.setBlock(cell(pos, facing, half, column), part(state, half, column), Block.UPDATE_CLIENTS);
        }
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++)
            level.updateNeighborsAt(cell(pos, facing, half, column), this);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!operable || !player.mayBuild()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockPos lower = base(pos, state);
        Direction facing = state.getValue(FACING);
        BlockState bottom = level.getBlockState(lower);
        if (!matches(bottom, facing, 0, 0)) return InteractionResult.PASS;
        boolean open = !opened(bottom);
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            BlockPos target = cell(lower, facing, half, column);
            if(!level.hasChunkAt(target) || level.isOutsideBuildHeight(target) || !level.getWorldBorder().isWithinBounds(target)) return InteractionResult.PASS;
            BlockState current = level.getBlockState(target);
            if (!matches(current, facing, half, column) || opened(current) != opened(bottom)
                    || !level.mayInteract(player, target)
                    || !player.mayUseItemAt(target, hit.getDirection(), player.getMainHandItem())
                    || !level.isUnobstructed(current.setValue(OPEN, open), target, CollisionContext.empty()))
                return InteractionResult.PASS;
        }
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            BlockPos target = cell(lower, facing, half, column);
            level.setBlock(target, level.getBlockState(target).setValue(OPEN, open), Block.UPDATE_CLIENTS);
        }
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++)
            level.updateNeighborsAt(cell(lower, facing, half, column), this);
        level.playSound(null, lower, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.6F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(base(pos,state), this, 1);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos lower = base(pos, state);
        Direction facing = state.getValue(FACING);
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            if (!level.hasChunkAt(cell(lower, facing, half, column))) {
                level.scheduleTick(pos, this, 20);
                return;
            }
        }
        boolean complete = true;
        BlockState controller = level.getBlockState(lower);
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            BlockPos target = cell(lower, facing, half, column);
            BlockState part = level.getBlockState(target);
            complete &= matches(part, facing, half, column) && opened(part) == opened(controller)
                    && (!(wallMounted || half == 0) || supported(level, target, facing));
        }
        if (!complete) dismantle(level, lower, facing, true, null);
    }

    private void dismantle(Level level, BlockPos lower, Direction facing, boolean drop, BlockPos mined) {
        if (!lower.equals(mined) && matches(level.getBlockState(lower), facing, 0, 0)) level.destroyBlock(lower, drop);
        for (int half = 0; half < height; half++) for (int column = 0; column < width; column++) {
            BlockPos target = cell(lower, facing, half, column);
            if (!target.equals(mined) && level.hasChunkAt(target) && matches(level.getBlockState(target), facing, half, column))
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
        return collisions[opened(state) ? 1 : 0][half(state)][column(state)][facing];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) { if(mirror==Mirror.NONE) return state;
        Direction facing=state.getValue(FACING),reflected=mirror.mirror(facing);
        BlockState value=state.setValue(FACING,reflected);
        if(value.hasProperty(columnProperty()) && mirror.mirror(across(facing))!=across(reflected)) value=value.setValue(columnProperty(),width-1-column(state));
        return value; }
}
