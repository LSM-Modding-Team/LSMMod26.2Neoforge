package net.nicomar2009.lsmmod.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.StairBlock;

/** Shared joins require matching glass footprints and actual contact at a cell boundary. */
public final class SchoolGlazing {
    private SchoolGlazing() {}
    private static boolean isGlass(BlockState state) {
        return state.getBlock() instanceof SchoolGlassBlock || state.getBlock() instanceof SchoolGlassStairBlock
                || (state.getBlock() instanceof TallClassroomEntranceBlock && state.getValue(TallClassroomEntranceBlock.ROW)==2);
    }
    private static boolean reachesBottom(BlockState state) {
        if(state.getBlock() instanceof SchoolGlassStairBlock)return state.getValue(StairBlock.HALF)==Half.TOP;
        if(state.getBlock() instanceof TallClassroomEntranceBlock)return false;
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
}
