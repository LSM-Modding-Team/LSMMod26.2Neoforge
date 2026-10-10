package net.nicomar2009.lsmmod.block;

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
    private static boolean isGlass(BlockState state) {
        return state.getBlock() instanceof SchoolGlassBlock || state.getBlock() instanceof SchoolGlassStairBlock
                || (state.getBlock() instanceof TallClassroomEntranceBlock && state.getValue(TallClassroomEntranceBlock.ROW)==2)
                || (state.getBlock() instanceof DoubleSchoolEntranceBlock && state.getValue(DoubleSchoolEntranceBlock.ROW)==2 && state.getValue(DoubleSchoolEntranceBlock.DEPTH)==0);
    }
    private static boolean reachesBottom(BlockState state) {
        if(state.getBlock() instanceof SchoolGlassStairBlock)return state.getValue(StairBlock.HALF)==Half.TOP;
        if(state.getBlock() instanceof TallClassroomEntranceBlock||state.getBlock() instanceof DoubleSchoolEntranceBlock)return false;
        return ((SchoolGlassBlock)state.getBlock()).glassReachesBottom();
    }
    private static boolean reachesTop(BlockState state) {
        return !(state.getBlock() instanceof SchoolGlassStairBlock)||state.getValue(StairBlock.HALF)==Half.BOTTOM;
    }
    private static int footprint(BlockState state) {
        if(!(state.getBlock() instanceof SchoolGlassStairBlock))return 3;
        return switch(state.getValue(StairBlock.SHAPE)) {
            case STRAIGHT->3;case OUTER_LEFT->1;case OUTER_RIGHT->2;case INNER_LEFT->11;case INNER_RIGHT->7;
        };
    }
    public static boolean connects(BlockState state,BlockState neighbor,Direction side) {
        if(!side.getAxis().isVertical()||!isGlass(state)||!isGlass(neighbor)
                ||state.getValue(SchoolGlassBlock.FACING)!=neighbor.getValue(SchoolGlassBlock.FACING)
                ||footprint(state)!=footprint(neighbor))return false;
        return side==Direction.UP?reachesTop(state)&&reachesBottom(neighbor):reachesBottom(state)&&reachesTop(neighbor);
    }

    private static int band(BlockState state) {
        if (!isGlass(state) || footprint(state) != 3) return -1;
        if (state.getBlock() instanceof SchoolGlassStairBlock)
            return state.getValue(StairBlock.HALF) == Half.TOP ? 1 : 2;
        return state.getBlock() instanceof TallClassroomEntranceBlock || state.getBlock() instanceof DoubleSchoolEntranceBlock ? 2 : 0;
    }

    private static boolean sameRow(BlockState state, BlockState other) {
        return band(state) >= 0 && band(state) == band(other)
                && state.getValue(SchoolGlassBlock.FACING) == other.getValue(SchoolGlassBlock.FACING);
    }

    public static boolean connectsHorizontal(BlockState state,BlockState neighbor,Direction side) {
        return side.getAxis().isHorizontal() && sameRow(state,neighbor)
                && side.getAxis()!=state.getValue(SchoolGlassBlock.FACING).getAxis();
    }

    /** A glass join does not imply that the neighbor covers the entire block-side face. */
    public static boolean canCullHorizontalFace(BlockState state,BlockState neighbor,Direction side) {
        if(!connectsHorizontal(state,neighbor,side))return false;
        if(state.getBlock() instanceof SchoolGlassBlock && neighbor.getBlock() instanceof SchoolGlassBlock)return true;
        // Straight stairs have matching complete side profiles, even with different materials.
        if(state.getBlock() instanceof SchoolGlassStairBlock && neighbor.getBlock() instanceof SchoolGlassStairBlock)
            return state.getValue(StairBlock.HALF)==neighbor.getValue(StairBlock.HALF)
                    && state.getValue(StairBlock.SHAPE)==neighbor.getValue(StairBlock.SHAPE);
        // Door leaves are only four pixels thick; stair supports span sixteen pixels.
        // Keep exposed support/frame faces, also when either door is open.
        return false;
    }

    public static BlockState mirrorLayout(BlockState original,BlockState reflected,Mirror mirror) {
        Direction right=original.getValue(SchoolGlassBlock.FACING).getClockWise();
        if(mirror.mirror(right)==reflected.getValue(SchoolGlassBlock.FACING).getClockWise())return reflected;
        int layout=original.getValue(FRAME_LAYOUT);
        BlockState result=reflected.setValue(FRAME_LAYOUT,((layout & 1)<<1)|((layout & 2)>>1)|(layout & 4));
        if(result.hasProperty(SchoolCurtains.CURTAIN)) {
            CurtainPart part=result.getValue(SchoolCurtains.CURTAIN);
            if(part==CurtainPart.LEFT)result=result.setValue(SchoolCurtains.CURTAIN,CurtainPart.RIGHT);
            else if(part==CurtainPart.RIGHT)result=result.setValue(SchoolCurtains.CURTAIN,CurtainPart.LEFT);
        }
        return result;
    }

    /** Recompute the entire loaded row, so changing its parity refreshes distant separators too. */
    public static void refreshHorizontal(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (band(state) < 0) {
            if (state.hasProperty(FRAME_LAYOUT) && state.getValue(FRAME_LAYOUT) != 3)
                level.setBlock(pos, state.setValue(FRAME_LAYOUT, 3), Block.UPDATE_CLIENTS);
            return;
        }
        Direction right = state.getValue(SchoolGlassBlock.FACING).getClockWise();
        BlockPos start = pos;
        while (level.hasChunkAt(start.relative(right.getOpposite()))
                && sameRow(state, level.getBlockState(start.relative(right.getOpposite()))))
            start = start.relative(right.getOpposite());
        int length = 0;
        while (level.hasChunkAt(start.relative(right, length))
                && sameRow(state, level.getBlockState(start.relative(right, length)))) length++;
        if(!level.hasChunkAt(start.relative(right.getOpposite())) || !level.hasChunkAt(start.relative(right,length))) {
            level.scheduleTick(pos,state.getBlock(),20);
            return;
        }
        int spacing = length % 2 == 0 ? 32 : 24;
        for (int index = 0; index < length; index++) {
            int x = 16 * index;
            int layout = (index == 0 || x % spacing == 0 ? 1 : 0)
                    | (index == length - 1 || (x + 16) % spacing == 0 ? 2 : 0)
                    | ((x + 8) % spacing == 0 ? 4 : 0);
            BlockPos target = start.relative(right, index);
            BlockState current = level.getBlockState(target);
            if (current.getValue(FRAME_LAYOUT) != layout)
                level.setBlock(target, current.setValue(FRAME_LAYOUT, layout), Block.UPDATE_CLIENTS);
        }
    }
}
