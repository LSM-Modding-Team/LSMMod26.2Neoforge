package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.entity.LivingEntity;

/** Vanilla slab placement/merging, sharing sash controllers and opening with normal windows. */
public class SchoolSlabProjectingWindowBlock extends SchoolProjectingWindowBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final EnumProperty<SlabType> TYPE=BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public SchoolSlabProjectingWindowBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TYPE,SlabType.BOTTOM).setValue(WATERLOGGED,false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);builder.add(TYPE,WATERLOGGED);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {return new SchoolSlabProjectingWindowPaintEntity(pos,state);}
    @Override protected BlockState placementState(BlockPlaceContext context) {
        var pos=context.getClickedPos();Direction face=context.getClickedFace();
        boolean top=face==Direction.DOWN||(face!=Direction.UP&&context.getClickLocation().y-pos.getY()>.5);
        return defaultBlockState().setValue(TYPE,top?SlabType.TOP:SlabType.BOTTOM)
                .setValue(WATERLOGGED,context.getLevel().getFluidState(pos).getType()==Fluids.WATER);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState old=context.getLevel().getBlockState(context.getClickedPos());
        if(old.is(this))return old.setValue(TYPE,SlabType.DOUBLE).setValue(WATERLOGGED,false).setValue(OPEN,false);
        return super.getStateForPlacement(context);
    }
    @Override protected boolean canBeReplaced(BlockState state,BlockPlaceContext context) {
        SlabType type=state.getValue(TYPE);
        if(type==SlabType.DOUBLE||!context.getItemInHand().is(asItem()))return false;
        if(!context.replacingClickedOnBlock())return true;
        boolean upper=context.getClickLocation().y-context.getClickedPos().getY()>.5;
        Direction face=context.getClickedFace();
        return type==SlabType.BOTTOM?(face==Direction.UP||upper&&face.getAxis().isHorizontal())
                :(face==Direction.DOWN||!upper&&face.getAxis().isHorizontal());
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moved) {
        super.onPlace(state,level,pos,old,moved);
        if(!level.isClientSide()&&old.is(this)&&old.getValue(TYPE)!=state.getValue(TYPE))refresh(level,pos,state.getValue(FACING));
    }
    @Override protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
            Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        if(state.getValue(WATERLOGGED))ticks.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(level));
        return super.updateShape(state,level,ticks,pos,side,neighborPos,neighbor,random);
    }
    @Override public boolean canPlaceLiquid(LivingEntity entity,BlockGetter level,BlockPos pos,BlockState state,Fluid fluid) {
        return state.getValue(TYPE)!=SlabType.DOUBLE&&SimpleWaterloggedBlock.super.canPlaceLiquid(entity,level,pos,state,fluid);
    }
    @Override public boolean placeLiquid(LevelAccessor level,BlockPos pos,BlockState state,FluidState fluid) {
        return state.getValue(TYPE)!=SlabType.DOUBLE&&SimpleWaterloggedBlock.super.placeLiquid(level,pos,state,fluid);
    }
}
