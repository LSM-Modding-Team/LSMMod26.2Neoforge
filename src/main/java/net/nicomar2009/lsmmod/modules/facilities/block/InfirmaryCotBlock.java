package net.nicomar2009.lsmmod.modules.facilities.block;
import net.nicomar2009.lsmmod.modules.facilities.registry.ModuleEntities;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nicomar2009.lsmmod.modules.facilities.entity.CotRestEntity;

/** Three-cell cot; old two-cell pairs retain their original shapes and serialized segments. */
public class InfirmaryCotBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty SEGMENT=IntegerProperty.create("segment",0,2);
    public static final BooleanProperty EXTENDED=BooleanProperty.create("extended");
    public InfirmaryCotBlock(Properties properties) {super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.SOUTH).setValue(SEGMENT,0).setValue(EXTENDED,false));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,SEGMENT,EXTENDED);}
    private static int length(BlockState state) {return state.getValue(EXTENDED)?3:2;}
    private static BlockPos cell(BlockPos root,BlockState state,int segment) {return root.relative(state.getValue(FACING),segment);}
    private static BlockPos anchor(BlockPos pos,BlockState state) {return cell(pos,state,-state.getValue(SEGMENT));}
    private boolean matches(BlockState other,BlockState state,int segment) {return other.is(this)&&other.getValue(SEGMENT)==segment&&other.getValue(FACING)==state.getValue(FACING)&&other.getValue(EXTENDED)==state.getValue(EXTENDED);}
    public boolean isComplete(Level level,BlockPos root,BlockState state) {
        for(int segment=0;segment<length(state);segment++) {
            BlockPos pos=cell(root,state,segment);
            if(!level.hasChunkAt(pos)||!matches(level.getBlockState(pos),state,segment))return false;
        }
        return true;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(level.isClientSide())return InteractionResult.SUCCESS;
        if(!player.isAlive()||player.isSpectator()||player.isSleeping()||player.isPassenger())return InteractionResult.CONSUME;
        BlockPos root=anchor(pos,state);
        if(!isComplete(level,root,state))return InteractionResult.CONSUME;
        for(CotRestEntity rest:level.getEntitiesOfClass(CotRestEntity.class,new AABB(root))) {
            if(rest.isVehicle()) {
                player.sendOverlayMessage(Component.translatable("block.minecraft.bed.occupied"));
                return InteractionResult.CONSUME;
            }
            rest.discard();
        }
        for(int segment=0;segment<length(state);segment++) {
            BlockPos above=cell(root,state,segment).above();
            if(!level.getBlockState(above).getCollisionShape(level,above).isEmpty()) {
                player.sendOverlayMessage(Component.translatable("block.minecraft.bed.obstructed"));
                return InteractionResult.CONSUME;
            }
        }
        CotRestEntity rest=new CotRestEntity(ModuleEntities.COT_REST.get(),level);
        rest.setPos(root.getX()+0.5D,root.getY()+13.0D/16.0D,root.getZ()+0.5D);
        rest.setYRot(state.getValue(FACING).toYRot());
        if(!level.addFreshEntity(rest))return InteractionResult.CONSUME;
        if(player.startRiding(rest)) {
            // Only the pose is shared with beds: no sleeping position, timer or respawn change.
            player.setPose(Pose.SLEEPING);
            rest.positionRider(player);
        } else rest.discard();
        return InteractionResult.SUCCESS;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite()).setValue(EXTENDED,true);Level level=context.getLevel();Player player=context.getPlayer();
        if(context.getClickedFace()!=Direction.UP)return null;
        for(int segment=0;segment<3;segment++) {
            BlockPos pos=cell(context.getClickedPos(),state,segment);
            if(!level.hasChunkAt(pos)||level.isOutsideBuildHeight(pos)||!level.getWorldBorder().isWithinBounds(pos)||!level.getBlockState(pos).canBeReplaced(context)
                ||!level.isUnobstructed(state.setValue(SEGMENT,segment),pos,CollisionContext.empty())
                ||(player!=null&&(!player.mayBuild()||!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand()))))return null;
        }
        return state;
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide())return;
        for(int segment=1;segment<length(state);segment++)level.setBlock(cell(pos,state,segment),state.setValue(SEGMENT,segment),Block.UPDATE_CLIENTS);
        for(int segment=0;segment<length(state);segment++)level.updateNeighborsAt(cell(pos,state,segment),this);
        level.scheduleTick(pos,this,1);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {ticks.scheduleTick(anchor(pos,state),this,1);return state;}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        BlockPos root=anchor(pos,state);
        for(int segment=0;segment<length(state);segment++)if(!level.hasChunkAt(cell(root,state,segment))){level.scheduleTick(pos,this,20);return;}
        for(int segment=0;segment<length(state);segment++)if(!matches(level.getBlockState(cell(root,state,segment)),state,segment)){dismantle(level,root,state,true,null);return;}
    }
    private void dismantle(Level level,BlockPos root,BlockState state,boolean drop,BlockPos mined) {
        if(!root.equals(mined)&&matches(level.getBlockState(root),state,0))level.destroyBlock(root,drop);
        for(int segment=0;segment<length(state);segment++) {
            BlockPos other=cell(root,state,segment);
            if(!other.equals(mined)&&level.hasChunkAt(other)&&matches(level.getBlockState(other),state,segment))level.setBlock(other,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
        }
    }
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player) {if(!level.isClientSide())dismantle(level,anchor(pos,state),state,!player.isCreative(),pos);return super.playerWillDestroy(level,pos,state,player);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return state.getValue(EXTENDED)?InfirmaryCotExtendedShapes.SHAPES[state.getValue(SEGMENT)][state.getValue(FACING).get2DDataValue()]:InfirmaryCotShapes.SHAPES[Math.min(state.getValue(SEGMENT),1)][state.getValue(FACING).get2DDataValue()];
    }
}
