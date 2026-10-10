package net.nicomar2009.lsmmod.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.nicomar2009.lsmmod.registry.ModItems;

/** Curtain state never replaces glass, changes collision, or attaches to doors. */
public final class SchoolCurtains {
    private SchoolCurtains() { }
    public static final int MAX_LENGTH=4, MAX_HEIGHT=8;
    // LEFT/MIDDLE/RIGHT denote bottom/interior/top along the vertical axis.
    public static final EnumProperty<CurtainPart> VERTICAL=EnumProperty.create("curtain_vertical",CurtainPart.class);
    public static final EnumProperty<CurtainPart> CURTAIN=EnumProperty.create("curtain",CurtainPart.class);

    public static boolean canAttach(BlockState state) {
        return state.getBlock() instanceof SchoolGlassBlock || state.getBlock() instanceof SchoolGlassStairBlock;
    }
    public static boolean compatible(BlockState a,BlockState b) {
        if(!canAttach(a)||!canAttach(b)||a.getValue(SchoolGlassBlock.FACING)!=b.getValue(SchoolGlassBlock.FACING))return false;
        return true;
    }
    public static CurtainPart axisPart(int index,int size) {
        return size==1?CurtainPart.SINGLE:index==0?CurtainPart.LEFT:index==size-1?CurtainPart.RIGHT:CurtainPart.MIDDLE;
    }
    public static CurtainPart part(BlockState state) {
        return state.hasProperty(CURTAIN)?state.getValue(CURTAIN):CurtainPart.NONE;
    }
    private static BlockState originalAt(LevelReader level,BlockPos target,BlockPos oldPos,BlockState old) {
        return target.equals(oldPos)?old:level.getBlockState(target);
    }
    private static CurtainPart vertical(BlockState state) {
        return state.hasProperty(VERTICAL)?state.getValue(VERTICAL):CurtainPart.SINGLE;
    }
    private static BlockPos start(LevelReader level,BlockPos pos,BlockState old) {
        BlockPos cursor=pos;
        Direction left=old.getValue(SchoolGlassBlock.FACING).getCounterClockWise();
        for(int axis=0;axis<2;axis++) {
            int limit=axis==0?MAX_LENGTH:MAX_HEIGHT;
            boolean found=false;
            for(int i=0;i<limit;i++) {
                if(!level.hasChunkAt(cursor))return null;
                BlockState state=originalAt(level,cursor,pos,old);
                if(!compatible(old,state)||part(state)==CurtainPart.NONE)return null;
                CurtainPart marker=axis==0?part(state):vertical(state);
                if(marker==CurtainPart.SINGLE||marker==CurtainPart.LEFT) { found=true;break; }
                if(marker!=CurtainPart.MIDDLE&&marker!=CurtainPart.RIGHT)return null;
                cursor=axis==0?cursor.relative(left):cursor.below();
            }
            if(!found)return null;
        }
        return cursor;
    }
    private static boolean nearbyLoaded(LevelReader level,BlockPos pos,Direction right) {
        // Wait for every potentially affected chunk without loading it.
        for(int x=1-MAX_LENGTH;x<MAX_LENGTH;x++)
            for(int y=1-MAX_HEIGHT;y<MAX_HEIGHT;y++)
                if(!level.hasChunkAt(pos.relative(right,x).above(y)))return false;
        return true;
    }
    private static void clear(ServerLevel level,BlockPos pos) {
        BlockState current=level.getBlockState(pos);
        if(part(current)!=CurtainPart.NONE)
            level.setBlock(pos,current.setValue(CURTAIN,CurtainPart.NONE).setValue(VERTICAL,CurtainPart.SINGLE),Block.UPDATE_CLIENTS);
    }
    private static int extent(LevelReader level,BlockPos origin,BlockPos oldPos,BlockState old,Direction direction,boolean up,int limit) {
        for(int i=0;i<limit;i++) {
            BlockPos target=up?origin.above(i):origin.relative(direction,i);
            if(!level.hasChunkAt(target))return 0;
            BlockState state=originalAt(level,target,oldPos,old);
            if(!compatible(old,state)||part(state)==CurtainPart.NONE)return 0;
            CurtainPart marker=up?vertical(state):part(state);
            if(i==0&&marker==CurtainPart.SINGLE)return 1;
            if(i==0&&marker!=CurtainPart.LEFT)return 0;
            if(i>0&&marker==CurtainPart.RIGHT)return i+1;
            if(i>0&&marker!=CurtainPart.MIDDLE)return 0;
        }
        return 0;
    }
    /** Returns the bounded rectangle, using the old state when its host was already removed. */
    public static List<BlockPos> cells(LevelReader level,BlockPos pos,BlockState old) {
        BlockPos origin=start(level,pos,old);
        if(origin==null)return List.of();
        Direction right=old.getValue(SchoolGlassBlock.FACING).getClockWise();
        int width=extent(level,origin,pos,old,right,false,MAX_LENGTH);
        int height=extent(level,origin,pos,old,right,true,MAX_HEIGHT);
        if(width==0||height==0)return List.of();
        List<BlockPos> cells=new ArrayList<>();
        for(int y=0;y<height;y++)for(int x=0;x<width;x++)cells.add(origin.relative(right,x).above(y));
        return cells;
    }
    /** Clear all parts before dropping so callbacks cannot refund twice. */
    public static boolean remove(ServerLevel level,BlockPos pos,BlockState old,boolean drop) {
        if(part(old)==CurtainPart.NONE)return false;
        List<BlockPos> cells=cells(level,pos,old);
        if(cells.isEmpty()) { clear(level,pos);return false; }
        BlockPos origin=cells.getFirst();
        Direction right=old.getValue(SchoolGlassBlock.FACING).getClockWise();
        int width=extent(level,origin,pos,old,right,false,MAX_LENGTH);
        int height=cells.size()/width;
        // Only clear matching members; never touch an adjacent independent curtain.
        for(int y=0;y<height;y++)for(int x=0;x<width;x++) {
            BlockPos target=origin.relative(right,x).above(y);
            BlockState state=originalAt(level,target,pos,old);
            if(compatible(old,state)&&part(state)==axisPart(x,width)&&vertical(state)==axisPart(y,height))clear(level,target);
        }
        if(drop)Block.popResource(level,pos,new ItemStack(ModItems.SCHOOL_CURTAIN.get()));
        return true;
    }
    public static void tick(ServerLevel level,BlockPos pos) {
        BlockState state=level.getBlockState(pos);
        if(part(state)==CurtainPart.NONE)return;
        Direction right=state.getValue(SchoolGlassBlock.FACING).getClockWise();
        if(!nearbyLoaded(level,pos,right)) { level.scheduleTick(pos,state.getBlock(),20);return; }
        List<BlockPos> cells=cells(level,pos,state);
        if(cells.isEmpty()) { clear(level,pos);return; }
        BlockPos origin=cells.getFirst();
        int width=extent(level,origin,pos,state,right,false,MAX_LENGTH),height=cells.size()/width;
        boolean valid=true;
        for(int y=0;y<height;y++)for(int x=0;x<width;x++) {
            BlockState next=level.getBlockState(origin.relative(right,x).above(y));
            if(!compatible(state,next)||part(next)!=axisPart(x,width)||vertical(next)!=axisPart(y,height))valid=false;
        }
        if(!valid)remove(level,pos,state,true);
        else level.scheduleTick(pos,state.getBlock(),20);
    }
}
