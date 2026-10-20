package net.nicomar2009.lsmmod.modules.windows_and_enclosures.block;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.StairBlock;

/** Shared joins require matching glass footprints and actual contact at a cell boundary. */
public final class SchoolGlazing {
    /** Bits: left edge, right edge, middle post. Collision stays unchanged. */
    public static final IntegerProperty FRAME_LAYOUT = IntegerProperty.create("frame_layout", 0, 7);
    private SchoolGlazing() {}
    private static boolean continuous(BlockState state) {
        return state.getBlock() instanceof SchoolGlassBlock glass && glass.continuousConnections()
                || state.getBlock() instanceof SchoolGlassStairBlock stair && stair.continuousConnections();
    }

    public static boolean isGlass(BlockState state) {
        return state.getBlock() instanceof SchoolGlassBlock || state.getBlock() instanceof SchoolGlassStairBlock;
    }

    private static Direction defaultFacing(BlockState state) {
        return state.getValue(SchoolGlassBlock.FACING);
    }
    /** Pane extents and quarter footprints for flat glazing and vanilla stair corners. */
    private record Pane(int low, int high, int start, int end, int footprint) {}
    private static final Pane FULL=new Pane(0,16,0,16,3);
    private static final Pane[][] STAIRS=new Pane[2][12];
    static {
        for(int footprint:new int[]{1,2,3,7,11}) {
            STAIRS[0][footprint]=new Pane(0,8,0,16,footprint);
            STAIRS[1][footprint]=new Pane(8,16,0,16,footprint);
        }
    }
    private static Pane pane(BlockState state,Direction facing) {
        if(!isGlass(state)||state.getValue(SchoolGlassBlock.FACING)!=facing)return null;
        if(state.getBlock() instanceof SchoolGlassStairBlock) {
            int footprint=switch(state.getValue(StairBlock.SHAPE)) {
                case STRAIGHT->3;case OUTER_LEFT->1;case OUTER_RIGHT->2;case INNER_LEFT->11;case INNER_RIGHT->7;
            };
            return STAIRS[state.getValue(StairBlock.HALF)==Half.TOP?0:1][footprint];
        }
        if(state.getBlock() instanceof SchoolGlassBlock)
            return FULL;
        return null;
    }
    private static boolean connects(BlockState state,BlockState neighbor,Direction side,Direction facing) {
        if(continuous(state)!=continuous(neighbor))return false;
        Pane a=pane(state,facing),b=pane(neighbor,facing);
        if(!side.getAxis().isVertical()||a==null||b==null||a.footprint()!=b.footprint()
                ||a.start()!=b.start()||a.end()!=b.end())return false;
        return side==Direction.UP?a.high()==16&&reachesBottom(neighbor,b):reachesBottom(state,a)&&b.high()==16;
    }
    private static boolean reachesBottom(BlockState state,Pane pane) {
        return state.getBlock() instanceof SchoolGlassBlock glass?glass.glassReachesBottom():pane.low()==0;
    }
    public static boolean connects(BlockState state,BlockState neighbor,Direction side) {
        if(!isGlass(state))return false;
        return connects(state,neighbor,side,defaultFacing(state));
    }
    private static boolean sameRow(BlockState state,BlockState other,Direction facing) {
        if(continuous(state)!=continuous(other))return false;
        Pane a=pane(state,facing),b=pane(other,facing);
        return a!=null&&b!=null&&(continuous(state)||a.footprint()==3&&b.footprint()==3)
                &&a.low()==b.low()&&a.high()==b.high();
    }
    /** Consecutive cells connect only when their visible glass actually reaches the shared edge. */
    private static boolean adjacent(BlockState left,BlockState right,Direction facing) {
        if(!sameRow(left,right,facing))return false;
        Pane a=pane(left,facing),b=pane(right,facing);
        if(continuous(left)) {
            int edge=(a.footprint()>>1)&5;
            return edge!=0&&edge==(b.footprint()&5);
        }
        return a.end()==16&&b.start()==0;
    }
    public static boolean connectsHorizontal(BlockState state,BlockState neighbor,Direction side) {
        if(!side.getAxis().isHorizontal()||!isGlass(state))return false;
        Direction facing=side.getCounterClockWise();
        if(adjacent(state,neighbor,facing))return true;
        facing=side.getClockWise();
        return adjacent(neighbor,state,facing);
    }

    /** A glass join does not imply that the neighbor covers the entire block-side face. */
    public static boolean canCullHorizontalFace(BlockState state,BlockState neighbor,Direction side) {
        if(!connectsHorizontal(state,neighbor,side))return false;
        if(state.getBlock() instanceof SchoolGlassBlock && neighbor.getBlock() instanceof SchoolGlassBlock)return true;
        // Straight stairs have matching complete side profiles, even with different materials.
        if(state.getBlock() instanceof SchoolGlassStairBlock && neighbor.getBlock() instanceof SchoolGlassStairBlock)
            return state.getValue(StairBlock.HALF)==neighbor.getValue(StairBlock.HALF)
                    && state.getValue(StairBlock.SHAPE)==neighbor.getValue(StairBlock.SHAPE);
        return false;
    }

    public static BlockState mirrorLayout(BlockState original,BlockState reflected,Mirror mirror) {
        Direction right=original.getValue(SchoolGlassBlock.FACING).getClockWise();
        if(mirror.mirror(right)==reflected.getValue(SchoolGlassBlock.FACING).getClockWise())return reflected;
        BlockState result=reflected;
        if(original.hasProperty(FRAME_LAYOUT)) {
            int layout=original.getValue(FRAME_LAYOUT);
            result=result.setValue(FRAME_LAYOUT,((layout & 1)<<1)|((layout & 2)>>1)|(layout & 4));
        }
        if(result.hasProperty(SchoolCurtains.CURTAIN)) {
            CurtainPart part=SchoolCurtains.part(result);
            if(part==CurtainPart.LEFT)result=SchoolCurtains.withParts(result,CurtainPart.RIGHT,result.getValue(SchoolCurtains.CURTAIN).vertical());
            else if(part==CurtainPart.RIGHT)result=SchoolCurtains.withParts(result,CurtainPart.LEFT,result.getValue(SchoolCurtains.CURTAIN).vertical());
        }
        return result;
    }

    /** World-dependent perimeter masks; continuous variants only inspect direct neighbors. */
    public static int visualMask(net.minecraft.world.level.LevelReader level,BlockPos pos,BlockState state) {
        return visualMask(level,pos,state,false);
    }
    public static int visualMask(net.minecraft.world.level.LevelReader level,BlockPos pos,BlockState state,boolean side) {
        if(!isGlass(state)||side)return 3;
        Direction facing=defaultFacing(state),right=facing.getClockWise();
        Pane current=pane(state,facing);
        int mask=3;
        // Map renderers may supply a cached state after the live block has changed.
        if(current==null||!level.hasChunkAt(pos)||!current.equals(pane(level.getBlockState(pos),facing)))return mask;
        if(continuous(state)) {
            if(!level.getBlockState(pos).is(state.getBlock()))return mask;
            BlockPos leftPos=pos.relative(right.getOpposite()),rightPos=pos.relative(right);
            mask=0;
            if(!level.hasChunkAt(leftPos)||!adjacent(level.getBlockState(leftPos),state,facing))mask|=1;
            if(!level.hasChunkAt(rightPos)||!adjacent(state,level.getBlockState(rightPos),facing))mask|=2;
        } else if(current.footprint()==3) {
            BlockPos start=pos;
            int index=0;
            while(level.hasChunkAt(start.relative(right.getOpposite()))
                    &&adjacent(level.getBlockState(start.relative(right.getOpposite())),level.getBlockState(start),facing)) {start=start.relative(right.getOpposite());index++;}
            int length=0;
            while(level.hasChunkAt(start.relative(right,length))&&(length==0||adjacent(level.getBlockState(start.relative(right,length-1)),level.getBlockState(start.relative(right,length)),facing)))length++;
            if(length>index&&level.hasChunkAt(start.relative(right.getOpposite()))&&level.hasChunkAt(start.relative(right,length))) {
                // Resolve once: another thread can replace a block after the row scan.
                Pane firstPane=pane(level.getBlockState(start),facing);
                Pane lastPane=pane(level.getBlockState(start.relative(right,length-1)),facing);
                if(firstPane!=null&&lastPane!=null) {
                    int first=firstPane.start();
                    int last=(length-1)*16+lastPane.end();
                    mask=layout(current,index,length,first,last);
                }
            }
        }
        if(level.hasChunkAt(pos.above())&&connects(state,level.getBlockState(pos.above()),Direction.UP,facing))mask|=8;
        if(level.hasChunkAt(pos.below())&&connects(state,level.getBlockState(pos.below()),Direction.DOWN,facing))mask|=16;
        return mask;
    }

    private static int layout(Pane pane,int index,int length,int first,int last) {
        int x=16*index,spacing=length % 2 == 0 ? 32 : 24;
        return (x+pane.start()==first||(x+pane.start()-first)%spacing==0?1:0)
                |(x+pane.end()==last||(x+pane.end()-first)%spacing==0?2:0)
                |(pane.start()<8&&pane.end()>8&&(x+8-first)%spacing==0?4:0);
    }

    /** Refresh normal frame layouts or the local perimeter of continuous glazing. */
    public static void refreshHorizontal(ServerLevel level,BlockPos pos) { refreshHorizontal(level,pos,false); }
    public static void refreshHorizontal(ServerLevel level,BlockPos pos,boolean side) {
        BlockState state=level.getBlockState(pos);
        if(!isGlass(state)||side)return;
        Direction facing=defaultFacing(state),right=facing.getClockWise();
        if(continuous(state)) {
            // Only neighboring perimeter cells can change; never scan the row.
            level.sendBlockUpdated(pos,state,state,Block.UPDATE_CLIENTS);
            for(Direction direction:new Direction[]{right,right.getOpposite(),Direction.UP,Direction.DOWN}) {
                BlockPos target=pos.relative(direction);
                if(!level.hasChunkAt(target))continue;
                BlockState neighbor=level.getBlockState(target);
                if(continuous(neighbor)&&neighbor.getValue(SchoolGlassBlock.FACING)==facing)
                    level.sendBlockUpdated(target,neighbor,neighbor,Block.UPDATE_CLIENTS);
            }
            return;
        }
        Pane current=pane(state,facing);
        if(current==null||current.footprint()!=3) {
            if(state.hasProperty(FRAME_LAYOUT)&&state.getValue(FRAME_LAYOUT)!=3)
                level.setBlock(pos,state.setValue(FRAME_LAYOUT,3),Block.UPDATE_CLIENTS);
            return;
        }
        BlockPos start=pos;
        while(level.hasChunkAt(start.relative(right.getOpposite()))
                &&adjacent(level.getBlockState(start.relative(right.getOpposite())),level.getBlockState(start),facing))start=start.relative(right.getOpposite());
        int length=0;
        while(level.hasChunkAt(start.relative(right,length))&&(length==0||adjacent(level.getBlockState(start.relative(right,length-1)),level.getBlockState(start.relative(right,length)),facing)))length++;
        if(!level.hasChunkAt(start.relative(right.getOpposite()))||!level.hasChunkAt(start.relative(right,length))) {
            level.scheduleTick(pos,state.getBlock(),20);
            return;
        }
        if(length==0)return;
        Pane firstPane=pane(level.getBlockState(start),facing);
        Pane lastPane=pane(level.getBlockState(start.relative(right,length-1)),facing);
        if(firstPane==null||lastPane==null)return;
        int first=firstPane.start();
        int last=(length-1)*16+lastPane.end();
        for(int index=0;index < length;index++) {
            BlockPos target=start.relative(right,index);
            BlockState value=level.getBlockState(target);
            Pane targetPane=pane(value,facing);
            if(targetPane==null)continue;
            int layout=layout(targetPane,index,length,first,last);
            if(value.hasProperty(FRAME_LAYOUT)) {
                if(value.getValue(FRAME_LAYOUT)!=layout)level.setBlock(target,value.setValue(FRAME_LAYOUT,layout),Block.UPDATE_CLIENTS);
            } else level.sendBlockUpdated(target,value,value,Block.UPDATE_CLIENTS);
        }
    }
}
