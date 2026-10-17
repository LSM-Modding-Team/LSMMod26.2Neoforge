package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Four 8px paint squares per face, constrained to the fixed wall; glass is never paintable. */
public class SchoolProjectingWindowPaintEntity extends SchoolPaintableCustomBlockEntity {
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
    @Override public boolean removeCube(Direction face,Vec3 hit) {return false;}
    @Override protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        if(input.getIntOr("paint_bits",0)==0) {loadWholeFaces(input);invalidateClientGeometry();}
        else super.loadAdditional(input);
    }
}
