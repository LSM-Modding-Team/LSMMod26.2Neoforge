package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Two decorative wooden cubbies, no inventory, entity or ticker. */
public class ChildrenLockerBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF=BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape[] COLLISION = {
        ExactVoxelShapes.pixels(new double[][]{{1,0,2,15,1,15},{1,15,2,15,16,15},{1,1,2,2,15,15},{14,1,2,15,15,15},{2,1,14,14,15,15}}),
        null,null,null
    };
    static { var rotations=Shapes.rotateHorizontal(COLLISION[0]); for(int i=1;i<4;i++) COLLISION[i]=rotations.get(Direction.from2DDataValue((i+2)%4)); }
    public ChildrenLockerBlock(Properties properties) {
        super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(HALF,DoubleBlockHalf.LOWER));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,HALF);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var level=context.getLevel();var pos=context.getClickedPos();
        if(level.isOutsideBuildHeight(pos.above())||!level.getWorldBorder().isWithinBounds(pos.above())
                ||!level.getBlockState(pos.above()).canBeReplaced(context)||!level.getBlockState(pos.below()).isFaceSturdy(level,pos.below(),Direction.UP)) return null;
        if(context.getPlayer()!=null&&!context.getPlayer().mayUseItemAt(pos.above(),context.getClickedFace(),context.getItemInHand())) return null;
        return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        level.setBlock(pos.above(),state.setValue(HALF,DoubleBlockHalf.UPPER),Block.UPDATE_ALL);
    }
    private boolean counterpart(BlockState state,BlockState other) {
        return other.is(this)&&other.getValue(FACING)==state.getValue(FACING)
                &&other.getValue(HALF)!=state.getValue(HALF);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        boolean upper=state.getValue(HALF)==DoubleBlockHalf.UPPER;
        if(side==(upper?Direction.DOWN:Direction.UP)&&!counterpart(state,neighbor)) return Blocks.AIR.defaultBlockState();
        if(!upper&&side==Direction.DOWN&&!neighbor.isFaceSturdy(level,neighborPos,Direction.UP)) return Blocks.AIR.defaultBlockState();
        return state;
    }
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player) {
        boolean upper=state.getValue(HALF)==DoubleBlockHalf.UPPER;
        BlockPos other=upper?pos.below():pos.above();
        // Survival removal lets the neighbor update remove the other half: only LOWER drops.
        // Creative must clear the counterpart without triggering a survival neighbor drop.
        if(!level.isClientSide()&&player.isCreative()&&counterpart(state,level.getBlockState(other)))
            level.setBlock(other,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_SUPPRESS_DROPS);
        return super.playerWillDestroy(level,pos,state,player);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return COLLISION[(state.getValue(FACING).get2DDataValue()+2)%4];
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {return state.rotate(mirror.getRotation(state.getValue(FACING)));}
}
