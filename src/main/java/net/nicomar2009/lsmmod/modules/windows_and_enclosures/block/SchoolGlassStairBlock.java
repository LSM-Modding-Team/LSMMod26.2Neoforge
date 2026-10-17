package net.nicomar2009.lsmmod.modules.windows_and_enclosures.block;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Vanilla stair placement, corners, inversion and waterlogging, with school glazing joins. */
public class SchoolGlassStairBlock extends StairBlock {
    public static final BooleanProperty TOP_CONNECTED = SchoolGlassBlock.TOP_CONNECTED;
    public static final BooleanProperty BOTTOM_CONNECTED = SchoolGlassBlock.BOTTOM_CONNECTED;

    public SchoolGlassStairBlock(Properties properties) {
        super(Blocks.STONE.defaultBlockState(), properties);
        registerDefaultState(defaultBlockState().setValue(SchoolCurtains.CURTAIN,CurtainLayout.NONE));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);builder.add(SchoolCurtains.CURTAIN);
    }
    private BlockState connections(BlockState state, LevelReader level, BlockPos pos) {return state;}

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=super.getStateForPlacement(context);
        return state==null?null:connections(state,context.getLevel(),context.getClickedPos());
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
                                     Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        ticks.scheduleTick(pos,this,1);
        return connections(super.updateShape(state,level,ticks,pos,side,neighborPos,neighbor,random),level,pos);
    }
    @Override
    protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState oldState,boolean moved) {
        super.onPlace(state,level,pos,oldState,moved);
        if(!level.isClientSide() && moved && SchoolCurtains.part(state)!=CurtainPart.NONE)
            level.setBlock(pos,SchoolCurtains.clearState(state),Block.UPDATE_CLIENTS);
        if(!level.isClientSide() && !oldState.is(this))level.scheduleTick(pos,this,1);
    }
    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        super.tick(state,level,pos,random);
        SchoolGlazing.refreshHorizontal(level,pos);
        SchoolCurtains.tick(level,pos);
    }
    @Override
    public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player) {
        if(!level.isClientSide())SchoolCurtains.remove((ServerLevel)level,pos,state,!player.isCreative());
        return super.playerWillDestroy(level,pos,SchoolCurtains.clearState(state),player);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        SchoolCurtains.remove(level,pos,state,true);
        super.affectNeighborsAfterRemoval(state,level,pos,moved);
    }

    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return super.getShape(state,level,pos,context);
    }
    @Override
    protected boolean skipRendering(BlockState state,BlockState neighbor,Direction side) {
        return SchoolGlazing.canCullHorizontalFace(state,neighbor,side)||SchoolGlazing.connects(state,neighbor,side)||super.skipRendering(state,neighbor,side);
    }    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {
        return SchoolGlazing.mirrorLayout(state,super.mirror(state,mirror),mirror);
    }

}
