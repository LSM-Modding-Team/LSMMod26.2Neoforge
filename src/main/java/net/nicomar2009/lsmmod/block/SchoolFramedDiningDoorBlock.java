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

/** A 2x3 framed door: 1.5x2.5 moving wood, fixed glass and side wall. */
public class SchoolFramedDiningDoorBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 1);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 2);
    public static final IntegerProperty DEPTH = IntegerProperty.create("depth", 0, 1);

    public static final EnumProperty<net.minecraft.world.level.block.state.properties.DoorHingeSide> HINGE=BlockStateProperties.DOOR_HINGE;

    public SchoolFramedDiningDoorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(HINGE,net.minecraft.world.level.block.state.properties.DoorHingeSide.LEFT).setValue(OPEN, false).setValue(COLUMN, 0).setValue(ROW, 0).setValue(DEPTH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, COLUMN, ROW, DEPTH, HINGE);
    }

    private static BlockPos cell(BlockPos origin, Direction facing, int column, int row, int depth) {
        return origin.relative(facing.getClockWise(), column).above(row)
                .relative(facing.getOpposite(), depth);
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        return pos.relative(facing.getClockWise(), -state.getValue(COLUMN))
                .below(state.getValue(ROW)).relative(facing, state.getValue(DEPTH));
    }

    private boolean matches(BlockState state, Direction facing, int column, int row, int depth) {
        return state.is(this) && state.getValue(FACING) == facing && state.getValue(COLUMN) == column
                && state.getValue(ROW) == row && state.getValue(DEPTH) == depth;
    }

    private static boolean occupied(int column, int depth, boolean open, boolean right) {
        // Keep the original plane as empty controller cells while the leaves are open.
        return depth == 0 || (open && column == (right?1:0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        Player player = context.getPlayer();
        BlockState base = defaultBlockState().setValue(FACING, facing);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 2; column++) {
                BlockPos target = cell(origin, facing, column, row, 0);
                BlockState part = base.setValue(COLUMN, column).setValue(ROW, row);
                if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                        || !level.getWorldBorder().isWithinBounds(target)
                        || !level.getBlockState(target).canBeReplaced(context)
                        || !level.isUnobstructed(part, target, CollisionContext.empty())
                        || (player != null && (!player.mayBuild() || !level.mayInteract(player, target) || !player.mayUseItemAt(target,context.getClickedFace(),context.getItemInHand())))) return null;
            }
        }
        return supported(level, origin, facing) ? base : null;
    }

    private static boolean supported(LevelReader level, BlockPos origin, Direction facing) {
        for (int column : new int[]{0, 1}) {
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
            for (int column = 0; column < 2; column++) {
                if (column == 0 && row == 0) continue;
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
        if (!matches(controller, facing, 0, 0, 0)) return InteractionResult.PASS;
        boolean open = !controller.getValue(OPEN);
        // Validate the entire operation before moving either leaf. A blocked swing changes nothing.
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 2; column++) {
                    if (!occupied(column, depth, open || controller.getValue(OPEN),right(controller))) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                            || !level.getWorldBorder().isWithinBounds(target)
                            || !level.mayInteract(player, target) || !player.mayUseItemAt(target,hit.getDirection(),player.getMainHandItem())) return InteractionResult.PASS;
                    BlockState existing = level.getBlockState(target);
                    if (occupied(column, depth, controller.getValue(OPEN),right(controller))) {
                        if (!matches(existing, facing, column, row, depth)
                                || existing.getValue(OPEN) != controller.getValue(OPEN) || existing.getValue(HINGE)!=controller.getValue(HINGE)) return InteractionResult.PASS;
                    } else if (!existing.isAir()) return InteractionResult.PASS;
                    if (occupied(column, depth, open,right(controller))) {
                        BlockState next = controller.setValue(OPEN, open).setValue(COLUMN, column)
                                .setValue(ROW, row).setValue(DEPTH, depth);
                        if (!level.isUnobstructed(next, target, CollisionContext.empty())) return InteractionResult.PASS;
                    }
                }
            }
        }
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 2; column++) {
                    if (!occupied(column, depth, open || controller.getValue(OPEN),right(controller))) continue;
                    BlockState next = occupied(column, depth, open,right(controller))
                            ? controller.setValue(OPEN, open).setValue(COLUMN, column).setValue(ROW, row).setValue(DEPTH, depth)
                            : Blocks.AIR.defaultBlockState();
                    level.setBlock(cell(origin, facing, column, row, depth), next,
                            Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        notifyCells(level, origin, facing);
        level.playSound(null, origin, open ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private void notifyCells(Level level, BlockPos origin, Direction facing) {
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 2; column++) {
                    
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

    private static boolean right(BlockState state) {return state.getValue(HINGE)==net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT;}
    private static final java.util.Map<Integer,VoxelShape[]> COLLISIONS=new java.util.concurrent.ConcurrentHashMap<>();
    private static VoxelShape[] physical(int key) {
        boolean open=(key&1)!=0,right=(key&2)!=0;
        int col=(key>>2)&1,row=(key>>3)&3,depth=(key>>5)&1;
        var boxes=new java.util.ArrayList<double[]>();
        for(double[] source:open?SchoolFacilityShapes.DINING_OPEN:SchoolFacilityShapes.DINING_CLOSED) {
            double[] b=source.clone();if(right){b[0]=32-source[3];b[3]=32-source[0];}
            double x0=Math.max(b[0],col*16),x1=Math.min(b[3],col*16+16),y0=Math.max(b[1],row*16),y1=Math.min(b[4],row*16+16),z0=Math.max(b[2],depth*16),z1=Math.min(b[5],depth*16+16);
            if(x0<x1&&y0<y1&&z0<z1) boxes.add(new double[]{x0-col*16,y0-row*16,z0-depth*16,x1-col*16,y1-row*16,z1-depth*16});
        }
        VoxelShape[] result=new VoxelShape[4];result[0]=ExactVoxelShapes.pixels(boxes.toArray(double[][]::new));
        for(int i=1;i<4;i++)result[i]=Shapes.rotate(result[i-1],com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90);
        return result;
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        int key=(state.getValue(OPEN)?1:0)|(right(state)?2:0)|(state.getValue(COLUMN)<<2)|(state.getValue(ROW)<<3)|(state.getValue(DEPTH)<<5);
        int turn=switch(state.getValue(FACING)){case EAST->1;case SOUTH->2;case WEST->3;default->0;};
        return COLLISIONS.computeIfAbsent(key,SchoolFramedDiningDoorBlock::physical)[turn];
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(origin(pos,state), this, 1);
        return state;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos origin = origin(pos, state);
        Direction facing = state.getValue(FACING);
        boolean complete = true;
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 2; column++) {
                    if (!occupied(column, depth, state.getValue(OPEN),right(state))) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (!level.hasChunkAt(target)) {
                        level.scheduleTick(pos, this, 20);
                        return;
                    }
                    BlockState part = level.getBlockState(target);
                    complete &= matches(part, facing, column, row, depth)
                            && part.getValue(OPEN) == state.getValue(OPEN) && part.getValue(HINGE)==state.getValue(HINGE);
                }
            }
        }
        if (!complete || !supported(level, origin, facing)) dismantle(level, origin, facing, true, null);
    }

    private void dismantle(Level level, BlockPos origin, Direction facing, boolean drop, BlockPos mined) {
        if (!origin.equals(mined) && matches(level.getBlockState(origin), facing, 0, 0, 0)) {
            level.destroyBlock(origin, drop);
        }
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 2; column++) {
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
                ? state.getValue(COLUMN) : 1 - state.getValue(COLUMN);
        return state.setValue(FACING, reflected).setValue(COLUMN, column).setValue(HINGE,mirror==Mirror.NONE?state.getValue(HINGE):(right(state)?net.minecraft.world.level.block.state.properties.DoorHingeSide.LEFT:net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT));
    }
}
