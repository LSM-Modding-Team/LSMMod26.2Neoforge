package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** World-space 8px paint squares on the fixed upper slab; leaves cannot be painted. */
public class SchoolEntranceSlabPaintEntity extends SchoolPaintableCustomBlockEntity {
    public SchoolEntranceSlabPaintEntity(BlockPos pos,BlockState state) {super(ModuleBlockEntities.SCHOOL_ENTRANCE_SLAB_PAINT.get(),pos,state);}
    @Override public int occupied() {return 0xcc;}
    @Override public boolean hasCube(int cube) {return cube>=0&&cube<8&&(occupied()&(1<<cube))!=0;}
    @Override public boolean removeCube(Direction face,Vec3 hit) {return false;}
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {
        double[] p={hit.x-worldPosition.getX()-face.getStepX()*1e-6,
                hit.y-worldPosition.getY()-face.getStepY()*1e-6,hit.z-worldPosition.getZ()-face.getStepZ()*1e-6};
        return SchoolPaintableDoubleEntranceBlock.slabCell(getBlockState())&&p[0]>=0&&p[0]<1&&p[1]>=.5&&p[1]<1&&p[2]>=0&&p[2]<1;
    }
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint paint) {if(canPaintAt(face,hit))super.paintAt(face,hit,paint);}
}
