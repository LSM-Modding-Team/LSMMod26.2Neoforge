package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** One independently placed inclined school wall and metal rail. Never inverted. */
public class SchoolRailingSlopeBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    private final double low,rise;
    private final VoxelShape[] collisions=new VoxelShape[4];
    public SchoolRailingSlopeBlock(double low,double rise,boolean post,boolean band,Properties properties) {
        super(properties);this.low=low;this.rise=rise;
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(WATERLOGGED,false));
        VoxelShape shape=Shapes.empty();
        for(int i=0;i<32;i++) {
            double t0=i/32.0,t1=(i+1)/32.0;
            shape=Shapes.or(shape,Shapes.box(0,band?low+rise*t0-12/16.0:0,1-t1,1,low+rise*t1,1-t0));
            shape=Shapes.or(shape,Shapes.box(7/16.0,low+rise*t0+6/16.0,1-t1,9/16.0,low+rise*t1+8/16.0,1-t0));
        }
        if(post)shape=Shapes.or(shape,Shapes.box(7/16.0,low+rise*7/16.0,7/16.0,9/16.0,low+rise*9/16.0+6/16.0,9/16.0));
        collisions[0]=shape.optimize();
        for(int i=1;i<4;i++) {
            VoxelShape[] next={Shapes.empty()};
            collisions[i-1].forAllBoxes((x1,y1,z1,x2,y2,z2)->next[0]=Shapes.or(next[0],Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
            collisions[i]=next[0].optimize();
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,WATERLOGGED);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING,context.getHorizontalDirection())
                .setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER);
    }
    private void refreshLandings(Level level,BlockPos pos) {
        if(level.isClientSide())return;
        for(Direction side:Direction.Plane.HORIZONTAL)for(int dy=-1;dy<=1;dy++) {
            BlockPos neighbor=pos.relative(side).offset(0,dy,0);
            if(!level.hasChunkAt(neighbor))continue;
            BlockState state=level.getBlockState(neighbor);
            if(state.getBlock() instanceof SchoolWallRailingBlock)level.scheduleTick(neighbor,state.getBlock(),1);
        }
    }
    @Override
    protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moved) {
        super.onPlace(state,level,pos,previous,moved);refreshLandings(level,pos);
    }
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        super.affectNeighborsAfterRemoval(state,level,pos,moved);refreshLandings(level,pos);
    }
    /** Checks actual endpoint height, including a slope placed one cell above/below. */
    public static boolean connectsFlat(LevelReader level,BlockPos flat,Direction toward) {
        for(int dy=-1;dy<=1;dy++) {
            BlockPos pos=flat.relative(toward).offset(0,dy,0);
            if(!level.hasChunkAt(pos))continue;
            BlockState state=level.getBlockState(pos);
            if(!(state.getBlock() instanceof SchoolRailingSlopeBlock slope))continue;
            Direction facing=state.getValue(FACING);
            if(facing.getAxis()!=toward.getAxis())continue;
            double endpoint=slope.low+(facing==toward.getOpposite()?slope.rise:0);
            if(Math.abs(dy+endpoint-12/16.0)<1e-8)return true;
        }
        return false;
    }
    @Override
    protected FluidState getFluidState(BlockState state) {return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);}
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
            Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        if(state.getValue(WATERLOGGED))ticks.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(level));
        return super.updateShape(state,level,ticks,pos,side,neighborPos,neighbor,random);
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return collisions[switch(state.getValue(FACING)) {case EAST->1;case SOUTH->2;case WEST->3;default->0;}];
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
}
