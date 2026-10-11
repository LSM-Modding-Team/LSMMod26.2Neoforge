package net.nicomar2009.lsmmod.block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class EntrancePavementSlabBlock extends SlabBlock {
    public EntrancePavementSlabBlock(Properties properties) {
        super(properties);
        BlockState state=defaultBlockState();
        for(Direction side:Direction.values())state=state.setValue(SchoolYellowBorderWallBlock.property(side),false);
        registerDefaultState(state);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);
        for(Direction side:Direction.values())builder.add(SchoolYellowBorderWallBlock.property(side));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=super.getStateForPlacement(context);
        return state==null?null:EntrancePavementConnections.update(state,context.getLevel(),context.getClickedPos());
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        ticks.scheduleTick(pos,this,1);
        return EntrancePavementConnections.update(super.updateShape(state,level,ticks,pos,side,neighborPos,neighbor,random),level,pos);
    }
    @Override
    protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState oldState,boolean moved) {
        super.onPlace(state,level,pos,oldState,moved);EntrancePavementConnections.refresh(level,pos);
    }
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        super.affectNeighborsAfterRemoval(state,level,pos,moved);EntrancePavementConnections.refresh(level,pos);
    }
    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        BlockState next=EntrancePavementConnections.update(state,level,pos);
        if(next!=state)level.setBlock(pos,next,Block.UPDATE_ALL);
    }
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation) {
        BlockState next=state;
        for(Direction side:Direction.values())next=next.setValue(SchoolYellowBorderWallBlock.property(rotation.rotate(side)),state.getValue(SchoolYellowBorderWallBlock.property(side)));
        return next;
    }
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {
        BlockState next=state;
        for(Direction side:Direction.values())next=next.setValue(SchoolYellowBorderWallBlock.property(mirror.mirror(side)),state.getValue(SchoolYellowBorderWallBlock.property(side)));
        return next;
    }
}
