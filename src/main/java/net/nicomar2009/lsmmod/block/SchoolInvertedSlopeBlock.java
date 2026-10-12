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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Fixed Y-inverted wall slope, without railing; first inclined endpoint is Y=0. */
public class SchoolInvertedSlopeBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    private record Profile(double start,double rise,double offset,double top) {}
    private static final java.util.concurrent.ConcurrentMap<Profile,VoxelShape[]> SHARED_SHAPES=new java.util.concurrent.ConcurrentHashMap<>();
    private final VoxelShape[] collisions;
    public SchoolInvertedSlopeBlock(double lowerStart,double rise,Properties properties) {
        this(lowerStart,rise,0,properties);
    }
    public SchoolInvertedSlopeBlock(double lowerStart,double rise,double verticalOffset,Properties properties) {
        this(lowerStart,rise,verticalOffset,1+verticalOffset,properties);
    }
    public SchoolInvertedSlopeBlock(double lowerStart,double rise,double verticalOffset,double topHeight,Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(WATERLOGGED,false));
        collisions=SHARED_SHAPES.computeIfAbsent(new Profile(lowerStart,rise,verticalOffset,topHeight),SchoolInvertedSlopeBlock::buildShapes);
    }
    private static VoxelShape[] buildShapes(Profile profile) {
        VoxelShape[] shapes=new VoxelShape[4];
        java.util.List<net.minecraft.world.phys.AABB> boxes=new java.util.ArrayList<>(32);
        for(int i=0;i<32;i++) {
            double t0=i/32.0,t1=(i+1)/32.0;
            double y0=profile.start()-profile.rise()*t0+profile.offset(),y1=profile.start()-profile.rise()*t1+profile.offset();
            boxes.add(new net.minecraft.world.phys.AABB(0,Math.min(y0,y1),1-t1,1,profile.top(),1-t0));
        }
        shapes[0]=ExactVoxelShapes.union(boxes);
        for(int i=1;i<4;i++) shapes[i]=Shapes.rotate(shapes[i-1],com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90);
        return shapes;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,WATERLOGGED);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING,context.getHorizontalDirection())
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
