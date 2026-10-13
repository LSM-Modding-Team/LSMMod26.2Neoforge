package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Four removable 8px cubes in the solid stair half; glass and curtains remain fixed. */
public class SchoolGlassStairPaintEntity extends SchoolPaintableCustomBlockEntity {
    public SchoolGlassStairPaintEntity(BlockPos pos,BlockState state) {
        super(ModBlockEntities.SCHOOL_GLASS_STAIR_PAINT.get(),pos,state);restrictOccupancy(slabMask(state));
    }
    public static int slabMask(BlockState state) {return state.getValue(StairBlock.HALF)==Half.TOP?0xcc:0x33;}
    @Override public int occupied() {return super.occupied()&slabMask(getBlockState());}
    @Override public boolean hasCube(int cube) {return cube>=0&&cube<8&&(occupied()&(1<<cube))!=0;}
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {
        double x=hit.x-worldPosition.getX()-face.getStepX()*1e-6;
        double y=hit.y-worldPosition.getY()-face.getStepY()*1e-6;
        double z=hit.z-worldPosition.getZ()-face.getStepZ()*1e-6;
        return x>=0&&x<1&&y>=0&&y<1&&z>=0&&z<1&&hasCube(hitCube(face,hit));
    }
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint paint) {if(canPaintAt(face,hit))super.paintAt(face,hit,paint);}
    @Override public boolean removeCube(Direction face,Vec3 hit) {return canPaintAt(face,hit)&&super.removeCube(face,hit);}
    @Override protected boolean removeEmptyBlock() {return false;}
    @Override protected void loadAdditional(ValueInput input) {super.loadAdditional(input);restrictOccupancy(slabMask(getBlockState()));invalidateClientGeometry();}
}
