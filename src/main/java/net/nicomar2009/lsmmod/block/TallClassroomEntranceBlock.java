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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** One wooden door item places three rows. Adjacent doors can use opposite hinges. */
public class TallClassroomEntranceBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty TOP_CONNECTED = SchoolGlassBlock.TOP_CONNECTED;
    public static final BooleanProperty BOTTOM_CONNECTED = SchoolGlassBlock.BOTTOM_CONNECTED;
    public static final IntegerProperty ROW = IntegerProperty.create("row",0,2);
    private static final VoxelShape[][][][] SHAPES = buildShapes();

    public TallClassroomEntranceBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(HINGE,DoorHingeSide.LEFT)
                .setValue(OPEN,false).setValue(POWERED,false).setValue(ROW,0).setValue(TOP_CONNECTED,false).setValue(BOTTOM_CONNECTED,false).setValue(SchoolGlazing.FRAME_LAYOUT,3));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,HINGE,OPEN,POWERED,ROW,TOP_CONNECTED,BOTTOM_CONNECTED,SchoolGlazing.FRAME_LAYOUT);
    }

    private static VoxelShape[][][][] buildShapes() {
        VoxelShape[][][][] out = new VoxelShape[3][2][2][4];
        for (int row=0;row<3;row++) for (int hinge=0;hinge<2;hinge++) for (int open=0;open<2;open++) {
            double[][] boxes = row == 2 ? new double[][]{{0,0,0,16,8,4},{0,8,0,16,16,8}}
                    : new double[][]{{0,0,0,16,16,4}};
            VoxelShape shape=Shapes.empty();
            for (double[] b:boxes) {
                if (open==1 && (row<2 || b[4]<=8)) b=hinge==0?new double[]{b[2],b[1],16-b[3],b[5],b[4],16-b[0]}
                        :new double[]{16-b[5],b[1],b[0],16-b[2],b[4],b[3]};
                shape=Shapes.or(shape,Block.box(b[0],b[1],b[2],b[3],b[4],b[5]));
            }
            out[row][hinge][open][0]=shape.optimize();
            for (int turn=1;turn<4;turn++) {
                VoxelShape[] next={Shapes.empty()};
                out[row][hinge][open][turn-1].forAllBoxes((x1,y1,z1,x2,y2,z2)->
                        next[0]=Shapes.or(next[0],Shapes.box(1-z2,y1,x1,1-z1,y2,x2)));
                out[row][hinge][open][turn]=next[0].optimize();
            }
        }
        return out;
    }

    private static BlockPos origin(BlockPos pos,BlockState state) {return pos.below(state.getValue(ROW));}
    private boolean matches(BlockState candidate,BlockState base,int row) {
        return candidate.is(this)&&candidate.getValue(ROW)==row&&candidate.getValue(FACING)==base.getValue(FACING)
                &&candidate.getValue(HINGE)==base.getValue(HINGE);
    }
    private static boolean supported(LevelReader level,BlockPos bottom) {
        BlockPos below=bottom.below();return level.getBlockState(below).isFaceSturdy(level,below,Direction.UP);
    }
    private static boolean powered(Level level,BlockPos bottom) {
        for(int row=0;row<3;row++) if(level.hasNeighborSignal(bottom.above(row))) return true;
        return false;
    }
    @Override
    protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {
        return supported(level,origin(pos,state));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level=context.getLevel();BlockPos pos=context.getClickedPos();
        if(!supported(level,pos)) return null;
        Direction facing=context.getHorizontalDirection().getOpposite(),right=facing.getClockWise();
        BlockState left=level.getBlockState(pos.relative(right.getOpposite())),neighbor=level.getBlockState(pos.relative(right));
        DoorHingeSide hinge;
        if(left.is(this)&&left.getValue(FACING)==facing&&left.getValue(ROW)==0) hinge=DoorHingeSide.RIGHT;
        else if(neighbor.is(this)&&neighbor.getValue(FACING)==facing&&neighbor.getValue(ROW)==0) hinge=DoorHingeSide.LEFT;
        else {
            int leftSolid=0,rightSolid=0;
            for(int row=0;row<3;row++) {
                BlockPos l=pos.above(row).relative(right.getOpposite()),r=pos.above(row).relative(right);
                if(level.getBlockState(l).isCollisionShapeFullBlock(level,l)) leftSolid++;
                if(level.getBlockState(r).isCollisionShapeFullBlock(level,r)) rightSolid++;
            }
            double hit=facing.getAxis()==Direction.Axis.Z?context.getClickLocation().x-pos.getX():context.getClickLocation().z-pos.getZ();
            if(right.getAxisDirection()==Direction.AxisDirection.NEGATIVE) hit=1-hit;
            hinge=rightSolid>leftSolid?DoorHingeSide.RIGHT:leftSolid>rightSolid?DoorHingeSide.LEFT:
                    hit>0.5?DoorHingeSide.RIGHT:DoorHingeSide.LEFT;
        }
        boolean power=powered(level,pos);
        BlockState base=defaultBlockState().setValue(FACING,facing).setValue(HINGE,hinge).setValue(POWERED,power).setValue(OPEN,power);
        Player player=context.getPlayer();
        for(int row=0;row<3;row++) {
            BlockPos target=pos.above(row);
            if(!level.hasChunkAt(target)||level.isOutsideBuildHeight(target)||!level.getWorldBorder().isWithinBounds(target)
                    ||!level.getBlockState(target).canBeReplaced(context)
                    ||!level.isUnobstructed(base.setValue(ROW,row),target,CollisionContext.empty())
                    ||(player!=null&&(!player.mayBuild()||!level.mayInteract(player,target))))return null;
        }
        return base;
    }
    @Override
    public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide())return;
        for(int row=1;row<3;row++)level.setBlock(pos.above(row),connections(state.setValue(ROW,row),level,pos.above(row)),Block.UPDATE_CLIENTS);
        for(int row=0;row<3;row++)level.updateNeighborsAt(pos.above(row),this);
        level.scheduleTick(pos,this,1);
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
                                    Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        ticks.scheduleTick(pos,this,1);return connections(state,level,pos);
    }
    private BlockState connections(BlockState state, LevelReader level, BlockPos pos) {
        boolean glass = state.getValue(ROW) == 2;
        BlockState current=level.getBlockState(pos);
        if(glass && current.is(this) && current.getValue(ROW)==2
                && current.getValue(FACING)==state.getValue(FACING))
            state=state.setValue(SchoolGlazing.FRAME_LAYOUT,current.getValue(SchoolGlazing.FRAME_LAYOUT));
        return state.setValue(TOP_CONNECTED, glass && SchoolGlazing.connects(state, level.getBlockState(pos.above()), Direction.UP))
                .setValue(BOTTOM_CONNECTED, glass && SchoolGlazing.connects(state, level.getBlockState(pos.below()), Direction.DOWN));
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighbor, Direction side) {
        return SchoolGlazing.canCullHorizontalFace(state,neighbor,side) || SchoolGlazing.connects(state, neighbor, side) || super.skipRendering(state, neighbor, side);
    }

    @Override
    protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,Orientation orientation,boolean moved) {
        if(!level.isClientSide())level.scheduleTick(pos,this,1);
    }
    private void change(Level level,BlockPos bottom,BlockState base,boolean open,boolean power) {
        for(int row=0;row<3;row++) {
            BlockPos p=bottom.above(row);if(!matches(level.getBlockState(p),base,row))return;
        }
        for(int row=0;row<3;row++)level.setBlock(bottom.above(row),connections(base.setValue(ROW,row).setValue(OPEN,open)
                .setValue(POWERED,power),level,bottom.above(row)),Block.UPDATE_CLIENTS);
        for(int row=0;row<3;row++)level.updateNeighborsAt(bottom.above(row),this);
        level.scheduleTick(bottom.above(2),this,1);
        if(base.getValue(OPEN)!=open) {
            level.playSound(null,bottom,open?SoundEvents.WOODEN_DOOR_OPEN:SoundEvents.WOODEN_DOOR_CLOSE,SoundSource.BLOCKS,1F,1F);
            level.gameEvent(null,open?GameEvent.BLOCK_OPEN:GameEvent.BLOCK_CLOSE,bottom);
        }
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        BlockPos bottom=origin(pos,state);
        if(!player.mayBuild())return InteractionResult.PASS;
        for(int row=0;row<3;row++) if(!level.mayInteract(player,bottom.above(row)))return InteractionResult.PASS;
        if(!level.isClientSide()) {
            BlockState base=level.getBlockState(bottom);
            if(matches(base,state,0))change(level,bottom,base,!base.getValue(OPEN),base.getValue(POWERED));
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        BlockPos bottom=origin(pos,state);boolean complete=true;
        for(int row=0;row<3;row++)complete&=matches(level.getBlockState(bottom.above(row)),state,row);
        if(!complete||!supported(level,bottom)){dismantle(level,bottom,state,true,null);return;}
        SchoolGlazing.refreshHorizontal(level,bottom.above(2));
        BlockState base=level.getBlockState(bottom);boolean power=powered(level,bottom);
        if(power!=base.getValue(POWERED))change(level,bottom,base,power,power);
    }
    private void dismantle(Level level,BlockPos bottom,BlockState base,boolean drop,BlockPos mined) {
        if(!bottom.equals(mined)&&matches(level.getBlockState(bottom),base,0))level.destroyBlock(bottom,drop);
        for(int row=0;row<3;row++) {
            BlockPos p=bottom.above(row);
            if(!p.equals(mined)&&matches(level.getBlockState(p),base,row))
                level.setBlock(p,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
        }
    }
    @Override
    public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player) {
        if(!level.isClientSide())dismantle(level,origin(pos,state),state,!player.isCreative(),pos);
        return super.playerWillDestroy(level,pos,state,player);
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        int turn=switch(state.getValue(FACING)){case EAST->1;case SOUTH->2;case WEST->3;default->0;};
        return SHAPES[state.getValue(ROW)][state.getValue(HINGE)==DoorHingeSide.LEFT?0:1][state.getValue(OPEN)?1:0][turn];
    }
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror){
        return mirror==Mirror.NONE?state:SchoolGlazing.mirrorLayout(state,state.setValue(FACING,mirror.mirror(state.getValue(FACING)))
                .setValue(HINGE,state.getValue(HINGE)==DoorHingeSide.LEFT?DoorHingeSide.RIGHT:DoorHingeSide.LEFT),mirror);
    }
}
