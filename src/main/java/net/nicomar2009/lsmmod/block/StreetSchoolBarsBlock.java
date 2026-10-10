package net.nicomar2009.lsmmod.block;

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
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Vanilla bars connections and collision, with a repeating four-by-three mesh frame. */
public class StreetSchoolBarsBlock extends IronBarsBlock {
    public static final IntegerProperty COLUMN=IntegerProperty.create("column",0,3);
    public static final IntegerProperty ROW=IntegerProperty.create("row",0,2);
    public StreetSchoolBarsBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLUMN,0).setValue(ROW,0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);builder.add(COLUMN,ROW);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return profile(super.getStateForPlacement(context),context.getLevel(),context.getClickedPos());
    }
    private BlockState profile(BlockState state,LevelReader level,BlockPos pos) {
        // Straight runs start at their western/northern edge, and panels start at the bottom.
        boolean alongX=state.getValue(EAST)||state.getValue(WEST);
        Direction back=alongX?Direction.WEST:Direction.NORTH;
        int column=0,row=0;
        BlockPos cursor=pos;
        while(column<256) {
            cursor=cursor.relative(back);
            if(!level.hasChunkAt(cursor)||!level.getBlockState(cursor).is(this))break;
            column++;
        }
        cursor=pos;
        while(row<256) {
            cursor=cursor.below();
            if(!level.hasChunkAt(cursor)||!level.getBlockState(cursor).is(this))break;
            row++;
        }
        return state.setValue(COLUMN,column%4).setValue(ROW,row%3);
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
    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        BlockState next=profile(state,level,pos);
        if(next!=state)level.setBlock(pos,next,Block.UPDATE_ALL);
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
}
