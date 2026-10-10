package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Reusable smooth slopes with stair-style orientation, inversion and waterlogging. */
public class SchoolSlopeBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Half> HALF=BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    private final VoxelShape[][] collisions=new VoxelShape[2][4];
    public SchoolSlopeBlock(double offset,Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(HALF,Half.BOTTOM).setValue(WATERLOGGED,false));
        for(int half=0;half<2;half++) {
            VoxelShape shape=Shapes.empty();
            for(int i=0;i<32;i++) {
                double height=Math.clamp(offset+(i+1.0)/32,0.0,1.0);
                if(height>0)shape=Shapes.or(shape,Shapes.box(0,half==0?0:1-height,1-(i+1.0)/32,1,half==0?height:1,1-i/32.0));
            }
            collisions[half][0]=shape.optimize();
            for(int turn=1;turn<4;turn++) {
                VoxelShape[] next={Shapes.empty()};
                collisions[half][turn-1].forAllBoxes((x1,y1,z1,x2,y2,z2)->next[0]=Shapes.or(next[0],Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                collisions[half][turn]=next[0].optimize();
            }
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,HALF,WATERLOGGED);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face=context.getClickedFace();
        boolean top=face==Direction.DOWN || face!=Direction.UP && context.getClickLocation().y-context.getClickedPos().getY()>0.5;
        return defaultBlockState().setValue(FACING,context.getHorizontalDirection()).setValue(HALF,top?Half.TOP:Half.BOTTOM)
                .setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER);
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
        return collisions[state.getValue(HALF)==Half.TOP?1:0][switch(state.getValue(FACING)) {case EAST->1;case SOUTH->2;case WEST->3;default->0;}];
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
