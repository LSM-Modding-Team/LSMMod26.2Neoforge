package net.nicomar2009.lsmmod.block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

/** Local height-aware surface joins, including adjacent cells above/below a ramp. */
public final class EntrancePavementConnections {
    private EntrancePavementConnections() {}
    public static boolean family(BlockState state) {
        return state.getBlock() instanceof SchoolYellowBorderWallBlock || state.getBlock() instanceof EntrancePavementSlabBlock;
    }
    public static double height(BlockState state,double x,double z) {
        if(state.getBlock() instanceof EntrancePavementSlopeBlock slope) {
            double t=switch(state.getValue(EntrancePavementSlopeBlock.FACING)) {case EAST->x;case SOUTH->z;case WEST->1-x;default->1-z;};
            return slope.low()+.375*t;
        }
        if(state.getBlock() instanceof EntrancePavementSlabBlock)return state.getValue(EntrancePavementSlabBlock.TYPE)==SlabType.BOTTOM?.5:1;
        return 1;
    }
    private static double edge(BlockState state,Direction side,double t) {
        return switch(side) {case NORTH->height(state,t,0);case SOUTH->height(state,t,1);case EAST->height(state,1,t);case WEST->height(state,0,t);default->height(state,.5,.5);};
    }
    public static BlockState update(BlockState state,LevelReader level,BlockPos pos) {
        for(Direction side:Direction.values()) {
            boolean join=false;
            if(side.getAxis().isHorizontal()) {
                for(int dy=-2;dy<=2;dy++) {
                    BlockPos neighbor=pos.relative(side).offset(0,dy,0);
                    if(!level.hasChunkAt(neighbor))continue;
                    BlockState other=level.getBlockState(neighbor);
                    if(family(other)&&Math.abs(edge(state,side,0)-dy-edge(other,side.getOpposite(),0))<1e-8
                            &&Math.abs(edge(state,side,1)-dy-edge(other,side.getOpposite(),1))<1e-8) {join=true;break;}
                }
            } else {
                BlockPos neighbor=pos.relative(side);
                if(level.hasChunkAt(neighbor)) {
                    BlockState other=level.getBlockState(neighbor);
                    if(family(other)) {
                        BlockState lower=side==Direction.UP?state:other;
                        BlockState upper=side==Direction.UP?other:state;
                        boolean upperStartsAtZero=!(upper.getBlock() instanceof EntrancePavementSlabBlock)
                                ||upper.getValue(EntrancePavementSlabBlock.TYPE)!=SlabType.TOP;
                        join=upperStartsAtZero&&Math.max(height(lower,0,0),height(lower,1,1))>=1;
                    }
                }
            }
            state=state.setValue(SchoolYellowBorderWallBlock.property(side),join);
        }
        return state;
    }
    public static void refresh(Level level,BlockPos pos) {
        if(level.isClientSide())return;
        for(Direction side:Direction.values())for(int dy=-2;dy<=2;dy++) {
            BlockPos target=pos.relative(side).offset(0,dy,0);
            if(level.hasChunkAt(target)) {
                BlockState state=level.getBlockState(target);
                if(family(state))level.scheduleTick(target,state.getBlock(),1);
            }
        }
        BlockState state=level.getBlockState(pos);
        if(family(state))level.scheduleTick(pos,state.getBlock(),1);
    }
}
