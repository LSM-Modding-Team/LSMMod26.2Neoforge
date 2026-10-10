package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Vanilla stair placement, corners, inversion and waterlogging, with school glazing joins. */
public class SchoolGlassStairBlock extends StairBlock {
    public static final BooleanProperty TOP_CONNECTED = SchoolGlassBlock.TOP_CONNECTED;
    public static final BooleanProperty BOTTOM_CONNECTED = SchoolGlassBlock.BOTTOM_CONNECTED;

    public SchoolGlassStairBlock(Properties properties) {
        super(Blocks.STONE.defaultBlockState(), properties);
        registerDefaultState(defaultBlockState().setValue(TOP_CONNECTED,false).setValue(BOTTOM_CONNECTED,false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);builder.add(TOP_CONNECTED,BOTTOM_CONNECTED);
    }
    private BlockState connections(BlockState state,LevelReader level,BlockPos pos) {
        return state.setValue(TOP_CONNECTED,SchoolGlazing.connects(state,level.getBlockState(pos.above()),Direction.UP))
                .setValue(BOTTOM_CONNECTED,SchoolGlazing.connects(state,level.getBlockState(pos.below()),Direction.DOWN));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=super.getStateForPlacement(context);
        return state==null?null:connections(state,context.getLevel(),context.getClickedPos());
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
                                     Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        return connections(super.updateShape(state,level,ticks,pos,side,neighborPos,neighbor,random),level,pos);
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
        return SchoolGlazing.connects(state,neighbor,side)||super.skipRendering(state,neighbor,side);
    }
}
