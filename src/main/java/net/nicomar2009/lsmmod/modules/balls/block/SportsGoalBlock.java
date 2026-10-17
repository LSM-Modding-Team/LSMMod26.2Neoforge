package net.nicomar2009.lsmmod.modules.balls.block;

import net.nicomar2009.lsmmod.block.ExactVoxelShapes;
import net.nicomar2009.lsmmod.block.SharedChairRotations;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;
import net.nicomar2009.lsmmod.modules.balls.block.SportsGoalShapes;

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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.nicomar2009.lsmmod.modules.balls.rules.FootballRules;

/** One item places an 8-wide, 6-high, 3-deep goal and basketball hoop. */
public class SportsGoalBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty CELL = IntegerProperty.create("cell", 0, 161);
    private static final VoxelShape[][] COLLISION = buildShapes(SportsGoalShapes.CELLS);
    private static final VoxelShape[][] NET_COLLISION = buildShapes(SportsGoalShapes.NET_CELLS);
    private static final VoxelShape[][] RIGID_COLLISION = buildShapes(SportsGoalShapes.RIGID_CELLS);

    public SportsGoalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CELL, 0));
    }

    private static VoxelShape[][] buildShapes(double[][][] cells) {
        VoxelShape[][] result = new VoxelShape[162][4];
        for (int i = 0; i < 162; i++) {
            result[i] = SharedChairRotations.horizontal(ExactVoxelShapes.pixels(cells[i]));
        }
        return result;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, CELL); }

    // Existing cell IDs 0..143 retain their original coordinates; extra column is appended.
    private static int column(int index) { return index<144 ? index%8 : 8; }
    private static int row(int index) { return index<144 ? index/8%6 : (index-144)%6; }
    private static int depth(int index) { return index<144 ? index/48 : (index-144)/6; }
    private static int encode(int column,int row,int depth) { return column<8 ? column+8*row+48*depth : 144+row+6*depth; }
    private static BlockPos cell(BlockPos origin, Direction facing, int index) {
        return origin.relative(facing.getClockWise(), column(index)).above(row(index))
                .relative(facing.getOpposite(), depth(index));
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        int index = state.getValue(CELL);
        Direction facing = state.getValue(FACING);
        return pos.relative(facing.getClockWise(), -(column(index))).below(row(index)).relative(facing, depth(index));
    }

    private boolean matches(BlockState state, Direction facing, int index) {
        return state.is(this) && state.getValue(FACING) == facing && state.getValue(CELL) == index;
    }

    private static boolean supported(LevelReader level, BlockPos origin, Direction facing) {
        for (int index : new int[]{0, 144, 96, 156}) {
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
        for (int index = 0; index < 162; index++) {
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

    public enum FootballContact { NONE, FRAME, NET }

    public static boolean touchesFootballNet(Level level, AABB ball, Vec3 requested, boolean blockedX, boolean blockedY, boolean blockedZ) {
        return footballContact(level, ball, requested, blockedX, blockedY, blockedZ) == FootballContact.NET;
    }

    /** Shares the real contact geometry between rebound physics and impact audio. */
    public static FootballContact footballContact(Level level, AABB ball, Vec3 requested, boolean blockedX, boolean blockedY, boolean blockedZ) {
        FootballRules.Bounds bounds = new FootballRules.Bounds(ball.minX, ball.minY, ball.minZ, ball.maxX, ball.maxY, ball.maxZ);
        double[] motion = {requested.x, requested.y, requested.z};
        boolean[] blocked = {blockedX, blockedY, blockedZ};
        boolean net = false;
        for (int axis = 0; axis < 3; axis++) {
            if (!blocked[axis] || Math.abs(motion[axis]) < 0.000001) continue;
            var patch = FootballRules.contactPatch(bounds, axis, motion[axis]);
            int material = contactMaterial(level, new AABB(patch.x0(), patch.y0(), patch.z0(), patch.x1(), patch.y1(), patch.z1()));
            if (material == 0) {
                // A stepped/sloped net may first touch a corner, outside the central patch.
                var face = FootballRules.contactFace(bounds, axis, motion[axis]);
                material = contactMaterial(level, new AABB(face.x0(), face.y0(), face.z0(), face.x1(), face.y1(), face.z1()));
            }
            if (material < 0) return FootballContact.FRAME;
            net |= material > 0;
        }
        return net ? FootballContact.NET : FootballContact.NONE;
    }

    private static int contactMaterial(Level level, AABB impact) {
        boolean net = false;
        // Native boxes span -16..32 pixels: their owning block may be one cell away.
        for (int x = (int) Math.floor(impact.minX) - 1; x <= Math.floor(impact.maxX) + 1; x++)
            for (int y = (int) Math.floor(impact.minY) - 1; y <= Math.floor(impact.maxY) + 1; y++)
                for (int z = (int) Math.floor(impact.minZ) - 1; z <= Math.floor(impact.maxZ) + 1; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.hasChunkAt(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof SportsGoalBlock)) continue;
                    int index = state.getValue(CELL);
                    int turn = switch (state.getValue(FACING)) { case EAST -> 1; case SOUTH -> 2; case WEST -> 3; default -> 0; };
                    VoxelShape contact = Shapes.create(impact.move(-x, -y, -z));
                    if (Shapes.joinIsNotEmpty(contact, RIGID_COLLISION[index][turn], BooleanOp.AND)) return -1;
                    net |= Shapes.joinIsNotEmpty(contact, NET_COLLISION[index][turn], BooleanOp.AND);
                }
        return net ? 1 : 0;
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
        int column = mirror.mirror(facing.getClockWise()) == reflected.getClockWise() ? column(index) : 8 - column(index);
        return state.setValue(FACING, reflected).setValue(CELL, encode(column,row(index),depth(index)));
    }
}
