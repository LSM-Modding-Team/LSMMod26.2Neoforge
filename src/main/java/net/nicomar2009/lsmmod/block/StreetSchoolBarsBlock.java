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
    // Weak ownership: disconnected worlds are never retained by the refresh batch.
    private static final java.util.Map<ServerLevel,RefreshBatch> REFRESHES=new java.util.WeakHashMap<>();
    private static final class RefreshBatch {
        long time=Long.MIN_VALUE;
        final java.util.HashSet<BlockPos> visited=new java.util.HashSet<>();
    }
    public StreetSchoolBarsBlock(Properties properties) {
        super(properties);

    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
    public static int visualProfile(BlockState state,LevelReader level,BlockPos pos) {
        // Straight runs start at their western/northern edge, and panels start at the bottom.
        boolean alongX=state.getValue(EAST)||state.getValue(WEST);
        Direction back=alongX?Direction.WEST:Direction.NORTH;
        int column=0,row=0;
        BlockPos cursor=pos;
        while(column<256) {
            cursor=cursor.relative(back);
            if(!level.hasChunkAt(cursor)||!level.getBlockState(cursor).is(state.getBlock()))break;
            column++;
        }
        cursor=pos;
        while(row<256) {
            cursor=cursor.below();
            if(!level.hasChunkAt(cursor)||!level.getBlockState(cursor).is(state.getBlock()))break;
            row++;
        }
        return (column%4)*3+row%3;
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
        // Inserting or removing a member can shift the panel phase far along the row.
        java.util.ArrayDeque<BlockPos> pending=new java.util.ArrayDeque<>();
        RefreshBatch batch=REFRESHES.computeIfAbsent(level,key->new RefreshBatch());
        if(batch.time!=level.getGameTime()) {batch.time=level.getGameTime();batch.visited.clear();}
        java.util.HashSet<BlockPos> visited=batch.visited;pending.add(pos);
        while(!pending.isEmpty()) {
            BlockPos target=pending.removeFirst();
            if(!visited.add(target)||!level.hasChunkAt(target))continue;
            BlockState member=level.getBlockState(target);
            if(!member.is(this))continue;
            level.sendBlockUpdated(target,member,member,Block.UPDATE_CLIENTS);
            for(Direction side:Direction.values())pending.add(target.relative(side));
        }
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
}
