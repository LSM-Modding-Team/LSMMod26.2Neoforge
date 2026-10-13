package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** The ordinary six-face palette, constrained to the original 8x40x16px side column. */
public class SchoolBathroomColumnPaintEntity extends SchoolPaintableBlockEntity {
    public SchoolBathroomColumnPaintEntity(BlockPos pos,BlockState state) {
        super(ModBlockEntities.SCHOOL_BATHROOM_COLUMN_PAINT.get(),pos,state);
    }
    /** World-space bounds within this cell; rotation changes geometry, never saved face indices. */
    public static double[] bounds(BlockState state) {
        Direction right=state.getValue(SchoolFramedDiningDoorBlock.FACING).getClockWise();
        int axis=right.getAxis()==Direction.Axis.X?0:2;
        boolean positive=(right.getAxisDirection()==Direction.AxisDirection.POSITIVE)
                !=(state.getValue(SchoolFramedDiningDoorBlock.HINGE)==DoorHingeSide.RIGHT);
        double[] bounds={0,0,0,1,state.getValue(SchoolFramedDiningDoorBlock.ROW)==2?.5:1,1};
        if(positive)bounds[axis]=.5;else bounds[axis+3]=.5;
        return bounds;
    }
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {
        BlockState state=getBlockState();
        if(!SchoolPaintableBathroomColumnDoorBlock.columnCell(state))return false;
        double[] bounds=bounds(state),point={hit.x-worldPosition.getX(),hit.y-worldPosition.getY(),hit.z-worldPosition.getZ()};
        int axis=face.getAxis()==Direction.Axis.X?0:face.getAxis()==Direction.Axis.Y?1:2;
        double plane=bounds[axis+(face.getAxisDirection()==Direction.AxisDirection.POSITIVE?3:0)];
        if(Math.abs(point[axis]-plane)>1e-6)return false;
        int[] steps={face.getStepX(),face.getStepY(),face.getStepZ()};
        for(int i=0;i<3;i++) {
            point[i]-=steps[i]*1e-6;
            if(point[i]<bounds[i]||point[i]>=bounds[i+3])return false;
        }
        return true;
    }
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint paint) {
        if(canPaintAt(face,hit))super.paintAt(face,hit,paint);
    }
}
