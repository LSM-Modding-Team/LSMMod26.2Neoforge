package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Framed vertical glass with optional lower-slab sill; vertical joins lose their seam. */
public class SchoolGlassBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty TOP_CONNECTED = BooleanProperty.create("top_connected");
    public static final BooleanProperty BOTTOM_CONNECTED = BooleanProperty.create("bottom_connected");
    private final boolean lowerSill;
    private final VoxelShape[] collisions = new VoxelShape[4];

    public SchoolGlassBlock(Properties properties, double[][] boxes) {
        super(properties);
        lowerSill = boxes.length > 1;
        VoxelShape shape = Shapes.empty();
        for (double[] b : boxes) shape = Shapes.or(shape, Block.box(b[0], b[1], b[2], b[3], b[4], b[5]));
        collisions[0] = shape;
        for (int i = 1; i < 4; i++) {
            VoxelShape[] next = {Shapes.empty()};
            collisions[i-1].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    next[0] = Shapes.or(next[0], Shapes.box(1-z2, y1, x1, 1-z1, y2, x2)));
            collisions[i] = next[0];
        }
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(TOP_CONNECTED, false).setValue(BOTTOM_CONNECTED, false).setValue(SchoolGlazing.FRAME_LAYOUT, 3).setValue(SchoolCurtains.CURTAIN,CurtainPart.NONE).setValue(SchoolCurtains.VERTICAL,CurtainPart.SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TOP_CONNECTED, BOTTOM_CONNECTED, SchoolGlazing.FRAME_LAYOUT, SchoolCurtains.CURTAIN,SchoolCurtains.VERTICAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockState state = defaultBlockState().setValue(FACING, face.getAxis().isHorizontal()
                ? face : context.getHorizontalDirection().getOpposite());
        if (face.getAxis().isVertical()) {
            BlockState neighbor = context.getLevel().getBlockState(context.getClickedPos().relative(face.getOpposite()));
            if (neighbor.getBlock() instanceof SchoolGlassBlock || neighbor.getBlock() instanceof SchoolGlassStairBlock
                    || (neighbor.getBlock() instanceof TallClassroomEntranceBlock && neighbor.getValue(TallClassroomEntranceBlock.ROW)==2))
                state=state.setValue(FACING,neighbor.getValue(FACING));
        }
        return connections(state, context.getLevel(), context.getClickedPos());
    }

    public boolean glassReachesBottom() { return !lowerSill; }

    private boolean connects(BlockState state, BlockState neighbor, Direction side) {
        return SchoolGlazing.connects(state, neighbor, side);
    }

    private BlockState connections(BlockState state, LevelReader level, BlockPos pos) {
        return state.setValue(TOP_CONNECTED, connects(state, level.getBlockState(pos.above()), Direction.UP))
                .setValue(BOTTOM_CONNECTED, connects(state, level.getBlockState(pos.below()), Direction.DOWN));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        ticks.scheduleTick(pos,this,1);
        return direction.getAxis().isVertical() ? connections(state, level, pos) : state;
    }

    @Override
    protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState oldState,boolean moved) {
        super.onPlace(state,level,pos,oldState,moved);
        if(!level.isClientSide() && moved && SchoolCurtains.part(state)!=CurtainPart.NONE)
            level.setBlock(pos,state.setValue(SchoolCurtains.CURTAIN,CurtainPart.NONE).setValue(SchoolCurtains.VERTICAL,CurtainPart.SINGLE),Block.UPDATE_CLIENTS);
        if(!level.isClientSide() && !oldState.is(this))level.scheduleTick(pos,this,1);
    }

    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        SchoolGlazing.refreshHorizontal(level,pos);
        SchoolCurtains.tick(level,pos);
    }

    @Override
    public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player) {
        if(!level.isClientSide())SchoolCurtains.remove((ServerLevel)level,pos,state,!player.isCreative());
        return super.playerWillDestroy(level,pos,state.setValue(SchoolCurtains.CURTAIN,CurtainPart.NONE).setValue(SchoolCurtains.VERTICAL,CurtainPart.SINGLE),player);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        SchoolCurtains.remove(level,pos,state,true);
        super.affectNeighborsAfterRemoval(state,level,pos,moved);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleBlockOutline.forState(state, () -> getCollisionShape(state, level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = switch (state.getValue(FACING)) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return collisions[index];
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        Direction facing = state.getValue(FACING);
        return SchoolGlazing.canCullHorizontalFace(state,adjacent,side) || (side.getAxis().isVertical() && connects(state, adjacent, side))
                || (side.getAxis().isHorizontal() && adjacent.is(this) && adjacent.getValue(FACING) == facing
                && side.getAxis() != facing.getAxis()) || super.skipRendering(state, adjacent, side);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return SchoolGlazing.mirrorLayout(state,state.rotate(mirror.getRotation(state.getValue(FACING))),mirror);
    }
}
