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

/** A 3x3 double wooden entrance, controlled by its bottom center cell. Both leaves swing together. */
public class DoubleSchoolEntranceBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty TOP_CONNECTED = SchoolGlassBlock.TOP_CONNECTED;
    public static final BooleanProperty BOTTOM_CONNECTED = SchoolGlassBlock.BOTTOM_CONNECTED;
    public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 2);
    public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 2);
    public static final IntegerProperty DEPTH = IntegerProperty.create("depth", 0, 1);

    private static final VoxelShape[][][][][] COLLISIONS=buildCollisions();

    public DoubleSchoolEntranceBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false).setValue(POWERED,false).setValue(TOP_CONNECTED,false).setValue(BOTTOM_CONNECTED,false).setValue(SchoolGlazing.FRAME_LAYOUT,3).setValue(COLUMN, 1).setValue(ROW, 0).setValue(DEPTH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, COLUMN, ROW, DEPTH,TOP_CONNECTED,BOTTOM_CONNECTED,SchoolGlazing.FRAME_LAYOUT);
    }

    private static BlockPos cell(BlockPos origin, Direction facing, int column, int row, int depth) {
        return origin.relative(facing.getClockWise(), column - 1).above(row)
                .relative(facing.getOpposite(), depth);
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        return pos.relative(facing.getClockWise(), 1 - state.getValue(COLUMN))
                .below(state.getValue(ROW)).relative(facing, state.getValue(DEPTH));
    }

    private boolean matches(BlockState state, Direction facing, int column, int row, int depth) {
        return state.is(this) && state.getValue(FACING) == facing && state.getValue(COLUMN) == column
                && state.getValue(ROW) == row && state.getValue(DEPTH) == depth;
    }

    private static boolean occupied(int column, int depth, boolean open) {
        // Keep the original plane as empty controller cells while the leaves are open.
        return depth == 0 || (open && (column == 0 || column == 2));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        Player player = context.getPlayer();
        BlockState base = defaultBlockState().setValue(FACING, facing);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                BlockPos target = cell(origin, facing, column, row, 0);
                BlockState part = base.setValue(COLUMN, column).setValue(ROW, row);
                if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                        || !level.getWorldBorder().isWithinBounds(target)
                        || !level.getBlockState(target).canBeReplaced(context)
                        || !level.isUnobstructed(part, target, CollisionContext.empty())
                        || (player != null && (!player.mayBuild() || !level.mayInteract(player, target)))) return null;
            }
        }
        return supported(level, origin, facing) ? base : null;
    }

    private static boolean supported(LevelReader level, BlockPos origin, Direction facing) {
        for (int column : new int[]{0, 2}) {
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
            for (int column = 0; column < 3; column++) {
                if (column == 1 && row == 0) continue;
                level.setBlock(cell(pos, state.getValue(FACING), column, row, 0),
                        connections(state.setValue(COLUMN, column).setValue(ROW, row),level,cell(pos,state.getValue(FACING),column,row,0)), Block.UPDATE_CLIENTS);
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
        if (!matches(controller, facing, 1, 0, 0)) return InteractionResult.PASS;
        return change(level,origin,controller,!controller.getValue(OPEN),controller.getValue(POWERED),player)
                ?InteractionResult.SUCCESS:InteractionResult.PASS;
    }

    private boolean change(Level level,BlockPos origin,BlockState controller,boolean open,boolean power,Player player) {
        Direction facing=controller.getValue(FACING);

        // Validate the entire operation before moving either leaf. A blocked swing changes nothing.
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (!occupied(column, depth, open || controller.getValue(OPEN))) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (!level.hasChunkAt(target) || level.isOutsideBuildHeight(target)
                            || !level.getWorldBorder().isWithinBounds(target)
                            || (player!=null && !level.mayInteract(player, target))) return false;
                    BlockState existing = level.getBlockState(target);
                    if (occupied(column, depth, controller.getValue(OPEN))) {
                        if (!matches(existing, facing, column, row, depth)
                                || existing.getValue(OPEN) != controller.getValue(OPEN)) return false;
                    } else if (!existing.isAir()) return false;
                    if (occupied(column, depth, open)) {
                        BlockState next = controller.setValue(OPEN, open).setValue(POWERED,power).setValue(COLUMN, column)
                                .setValue(ROW, row).setValue(DEPTH, depth);
                        if (!level.isUnobstructed(next, target, CollisionContext.empty())) return false;
                    }
                }
            }
        }
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (!occupied(column, depth, open || controller.getValue(OPEN))) continue;
                    BlockState next = occupied(column, depth, open)
                            ? controller.setValue(OPEN, open).setValue(POWERED,power).setValue(COLUMN, column).setValue(ROW, row).setValue(DEPTH, depth)
                            : Blocks.AIR.defaultBlockState();
                    BlockPos target=cell(origin,facing,column,row,depth);
                    if(next.is(this))next=connections(next,level,target);
                    level.setBlock(target, next,
                            Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        notifyCells(level, origin, facing);
        if(controller.getValue(OPEN)!=open) {
            level.playSound(null, origin, open ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null,open?net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN:
                    net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE,origin);
        }
        return true;
    }

    private BlockState connections(BlockState state,LevelReader level,BlockPos pos) {
        boolean glass=state.getValue(ROW)==2 && state.getValue(DEPTH)==0;
        BlockState current=level.getBlockState(pos);
        if(glass && current.is(this))state=state.setValue(SchoolGlazing.FRAME_LAYOUT,current.getValue(SchoolGlazing.FRAME_LAYOUT));
        return state.setValue(TOP_CONNECTED,glass&&SchoolGlazing.connects(state,level.getBlockState(pos.above()),Direction.UP))
                .setValue(BOTTOM_CONNECTED,glass&&SchoolGlazing.connects(state,level.getBlockState(pos.below()),Direction.DOWN));
    }
    @Override
    protected boolean skipRendering(BlockState state,BlockState neighbor,Direction side) {
        return SchoolGlazing.canCullHorizontalFace(state,neighbor,side)||SchoolGlazing.connects(state,neighbor,side)||super.skipRendering(state,neighbor,side);
    }
    @Override
    protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,
            net.minecraft.world.level.redstone.Orientation orientation,boolean moved) {
        if(!level.isClientSide())level.scheduleTick(pos,this,1);
    }

    private void notifyCells(Level level, BlockPos origin, Direction facing) {
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (depth != 0 && column != 0 && column != 2) continue;
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

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int turn=switch(state.getValue(FACING)) {case EAST->1;case SOUTH->2;case WEST->3;default->0;};
        return COLLISIONS[state.getValue(COLUMN)][state.getValue(ROW)][state.getValue(DEPTH)][state.getValue(OPEN)?1:0][turn];
    }
    private static VoxelShape[][][][][] buildCollisions() {
        VoxelShape[][][][][] all=new VoxelShape[3][3][2][2][4];
        for(int column=0;column<3;column++)for(int row=0;row<3;row++)for(int depth=0;depth<2;depth++)for(int open=0;open<2;open++) {
            VoxelShape shape=Shapes.empty();
            if(depth==0 && row==2)shape=Block.box(0,8,0,16,16,8);
            double height=row==2?8:16;
            if(open==0) {
                if(depth==0)shape=Shapes.or(shape,Block.box(0,0,0,16,height,4));
            } else if(column==0 || column==2) {
                double x=column==0?0:12;
                shape=Shapes.or(shape,Block.box(x,0,0,x+4,height,depth==0?16:8));
            }
            all[column][row][depth][open][0]=shape.optimize();
            for(int turn=1;turn<4;turn++) {
                VoxelShape[] next={Shapes.empty()};
                shape.forAllBoxes((x1,y1,z1,x2,y2,z2)->next[0]=Shapes.or(next[0],Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                shape=next[0].optimize();all[column][row][depth][open][turn]=shape;
            }
        }
        return all;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(pos, this, 1);
        return connections(state,level,pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos origin = origin(pos, state);
        Direction facing = state.getValue(FACING);
        boolean complete = true;
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (!occupied(column, depth, state.getValue(OPEN))) continue;
                    BlockPos target = cell(origin, facing, column, row, depth);
                    if (!level.hasChunkAt(target)) {
                        level.scheduleTick(pos, this, 20);
                        return;
                    }
                    BlockState part = level.getBlockState(target);
                    complete &= matches(part, facing, column, row, depth)
                            && part.getValue(OPEN) == state.getValue(OPEN);
                }
            }
        }
        if (!complete || !supported(level, origin, facing)) { dismantle(level, origin, facing, true, null);return; }
        for(int column=0;column<3;column++)SchoolGlazing.refreshHorizontal(level,cell(origin,facing,column,2,0));
        BlockState controller=level.getBlockState(origin);
        boolean power=false;
        for(int depth=0;depth<2;depth++)for(int row=0;row<3;row++)for(int column=0;column<3;column++)
            if(occupied(column,depth,controller.getValue(OPEN)))power|=level.hasNeighborSignal(cell(origin,facing,column,row,depth));
        if(power!=controller.getValue(POWERED))change(level,origin,controller,power,power,null);
    }

    private void dismantle(Level level, BlockPos origin, Direction facing, boolean drop, BlockPos mined) {
        if (!origin.equals(mined) && matches(level.getBlockState(origin), facing, 1, 0, 0)) {
            level.destroyBlock(origin, drop);
        }
        for (int depth = 0; depth < 2; depth++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
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
                ? state.getValue(COLUMN) : 2 - state.getValue(COLUMN);
        return SchoolGlazing.mirrorLayout(state,state.setValue(FACING, reflected).setValue(COLUMN, column),mirror);
    }
}
