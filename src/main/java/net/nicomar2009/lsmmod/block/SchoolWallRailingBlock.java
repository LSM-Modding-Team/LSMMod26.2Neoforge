package net.nicomar2009.lsmmod.block;

import java.util.ArrayDeque;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Twelve-pixel school wall with connected metal handrail and alternating posts. */
public class SchoolWallRailingBlock extends FenceBlock {
    public static final BooleanProperty POST=BooleanProperty.create("post");
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape BASE=Block.box(0,0,0,16,12,16);
    public SchoolWallRailingBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POST,true).setValue(FACING,Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);builder.add(POST,FACING);
    }
    @Override
    public boolean connectsTo(BlockState state,boolean solid,Direction direction) {
        return state.getBlock() instanceof SchoolWallRailingBlock || super.connectsTo(state,solid,direction);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING,context.getHorizontalDirection()).setValue(POST,true);
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
            Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        ticks.scheduleTick(pos,this,1);
        return super.updateShape(state,level,ticks,pos,side,neighborPos,neighbor,random);
    }
    @Override
    protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState oldState,boolean moved) {
        super.onPlace(state,level,pos,oldState,moved);
        if(!level.isClientSide())level.scheduleTick(pos,this,1);
    }
    private static boolean linked(BlockState state,Direction direction) {
        return state.getValue(PROPERTY_BY_DIRECTION.get(direction));
    }
    private boolean post(BlockState state,ServerLevel level,BlockPos pos) {
        boolean x=linked(state,Direction.EAST)||linked(state,Direction.WEST);
        boolean z=linked(state,Direction.NORTH)||linked(state,Direction.SOUTH);
        // Isolated blocks, corners and junctions need their own support.
        if(x==z)return true;
        Direction backward=x?Direction.WEST:Direction.NORTH;
        BlockPos start=pos;
        while(true) {
            BlockPos next=start.relative(backward);
            if(!level.hasChunkAt(next))break;
            BlockState neighbor=level.getBlockState(next);
            if(neighbor.getBlock()!=this)break;
            boolean nx=linked(neighbor,Direction.EAST)||linked(neighbor,Direction.WEST);
            boolean nz=linked(neighbor,Direction.NORTH)||linked(neighbor,Direction.SOUTH);
            start=next;
            if(nx&&nz)break;
        }
        int distance=x?pos.getX()-start.getX():pos.getZ()-start.getZ();
        return (distance&1)==0;
    }
    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        // Recalculate the loaded connected component after insertion/removal.
        // Missing chunks are never loaded; boundary pieces retry when neighbors update.
        ArrayDeque<BlockPos> pending=new ArrayDeque<>();HashSet<BlockPos> visited=new HashSet<>();pending.add(pos);
        while(!pending.isEmpty()) {
            BlockPos current=pending.removeFirst();
            if(!visited.add(current)||!level.hasChunkAt(current))continue;
            BlockState member=level.getBlockState(current);
            if(member.getBlock()!=this)continue;
            boolean support=post(member,level,current);
            if(member.getValue(POST)!=support)level.setBlock(current,member.setValue(POST,support),Block.UPDATE_CLIENTS);
            for(Direction side:Direction.Plane.HORIZONTAL)pending.add(current.relative(side));
        }
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        VoxelShape shape=BASE;
        if(state.getValue(POST))shape=Shapes.or(shape,Block.box(7,12,7,9,18,9));
        boolean any=false;
        for(Direction side:Direction.Plane.HORIZONTAL) {
            if(!linked(state,side))continue;any=true;
            shape=Shapes.or(shape,switch(side) {
                case NORTH->Block.box(7,18,0,9,20,9);
                case SOUTH->Block.box(7,18,7,9,20,16);
                case WEST->Block.box(0,18,7,9,20,9);
                default->Block.box(7,18,7,16,20,9);
            });
        }
        if(!any)shape=Shapes.or(shape,state.getValue(FACING).getAxis()==Direction.Axis.X?
                Block.box(0,18,7,16,20,9):Block.box(7,18,0,9,20,16));
        return shape;
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {return BASE;}
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation) {
        return super.rotate(state,rotation).setValue(FACING,rotation.rotate(state.getValue(FACING)));
    }
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {
        return super.mirror(state,mirror).setValue(FACING,mirror.mirror(state.getValue(FACING)));
    }
}
