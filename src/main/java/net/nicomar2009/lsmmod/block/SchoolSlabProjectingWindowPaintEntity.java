package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Fixed lower slab, using the shared world-space 24-square palette. */
public class SchoolSlabProjectingWindowPaintEntity extends SchoolPaintableCustomBlockEntity {
    public SchoolSlabProjectingWindowPaintEntity(BlockPos pos,BlockState state) {super(ModBlockEntities.SCHOOL_SLAB_PROJECTING_WINDOW_PAINT.get(),pos,state);}
    @Override public int occupied() {return 0x33;}
    @Override public boolean hasCube(int cube) {return cube>=0&&cube<8&&(occupied()&(1<<cube))!=0;}
    @Override public boolean removeCube(Direction face,Vec3 hit) {return false;}
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {
        double x=hit.x-worldPosition.getX()-face.getStepX()*1e-6;
        double y=hit.y-worldPosition.getY()-face.getStepY()*1e-6;
        double z=hit.z-worldPosition.getZ()-face.getStepZ()*1e-6;
        return x>=0&&x<1&&y>=0&&y<.5&&z>=0&&z<1;
    }
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint paint) {if(canPaintAt(face,hit))super.paintAt(face,hit,paint);}
}
