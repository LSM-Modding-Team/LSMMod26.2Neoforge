package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Standard six saved paints, constrained to the stationary solid half; no paint on glass. */
public class SchoolProjectingWindowPaintEntity extends SchoolPaintableBlockEntity {
    public SchoolProjectingWindowPaintEntity(BlockPos pos,BlockState state) {
        super(ModBlockEntities.SCHOOL_PROJECTING_WINDOW_PAINT.get(),pos,state);
    }
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {
        BlockState state=getBlockState();Direction right=state.getValue(SchoolProjectingWindowBlock.FACING).getClockWise();
        double x=hit.x-worldPosition.getX()-face.getStepX()*1e-6;
        double y=hit.y-worldPosition.getY()-face.getStepY()*1e-6;
        double z=hit.z-worldPosition.getZ()-face.getStepZ()*1e-6;
        if(x<0||x>1||y<0||y>1||z<0||z>1)return false;
        double local=.5+(x-.5)*right.getStepX()+(z-.5)*right.getStepZ();
        return state.getValue(SchoolProjectingWindowBlock.WALL_LEFT)?local<.5:local>=.5;
    }
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint color) {
        if(canPaintAt(face,hit))super.paintAt(face,hit,color);
    }
}
