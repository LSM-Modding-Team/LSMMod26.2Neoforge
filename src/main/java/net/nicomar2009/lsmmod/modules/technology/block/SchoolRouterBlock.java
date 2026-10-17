package net.nicomar2009.lsmmod.modules.technology.block;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Passive desktop router, including two centered rear antennas. */
public class SchoolRouterBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[] COLLISIONS = new VoxelShape[4];
    static {
        var shapes = Shapes.rotateHorizontal(ExactVoxelShapes.pixels(new double[][] {
                {3,0,4,13,3,12}, {4,3,10,5,12,11}, {11,3,10,12,12,11}
        }));
        for (Direction facing : Direction.Plane.HORIZONTAL)
            COLLISIONS[(facing.get2DDataValue()+2)%4] = shapes.get(facing);
    }

    public SchoolRouterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace()!=Direction.UP) return null;
        BlockState state=defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());
        return state.canSurvive(context.getLevel(),context.getClickedPos()) ? state : null;
    }
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {
        BlockPos support=pos.below();
        return level.getBlockState(support).isFaceSturdy(level,support,Direction.UP);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
            Direction direction,BlockPos neighborPos,BlockState neighborState,RandomSource random) {
        if(direction==Direction.DOWN) ticks.scheduleTick(pos,this,1);
        return state;
    }
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        if(!state.canSurvive(level,pos)) level.destroyBlock(pos,true);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return COLLISIONS[(state.getValue(FACING).get2DDataValue()+2)%4];
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return state.setValue(FACING,rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state,Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
}
