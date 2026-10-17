package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Fixed L-shaped solid, with the shared world-space 24-square paint palette. */
public class SchoolStairProjectingWindowPaintEntity extends SchoolPaintableCustomBlockEntity {
    public SchoolStairProjectingWindowPaintEntity(BlockPos pos,BlockState state) {
        super(ModBlockEntities.SCHOOL_STAIR_PROJECTING_WINDOW_PAINT.get(),pos,state);
    }
    public static int solidMask(BlockState state) {
        boolean left=state.getValue(SchoolProjectingWindowBlock.WALL_LEFT);
        boolean bottom=state.getValue(SchoolStairProjectingWindowBlock.HALF)==Half.BOTTOM;
        int turns=switch(state.getValue(SchoolProjectingWindowBlock.FACING)){case EAST->1;case SOUTH->2;case WEST->3;default->0;};
        int mask=0;
        for(int cube=0;cube<8;cube++) {
            int x=cube&1,y=(cube>>1)&1,z=(cube>>2)&1;
            if(x!=(left?0:1)&&y!=(bottom?0:1))continue;
            for(int turn=0;turn<turns;turn++){int old=x;x=1-z;z=old;}
            mask|=1<<(x+2*y+4*z);
        }
        return mask;
    }
    @Override public int occupied() {return solidMask(getBlockState());}
    @Override public boolean hasCube(int cube) {return cube>=0&&cube<8&&(occupied()&(1<<cube))!=0;}
    @Override public boolean removeCube(Direction face,Vec3 hit) {return false;}
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {
        double x=hit.x-worldPosition.getX()-face.getStepX()*1e-6;
        double y=hit.y-worldPosition.getY()-face.getStepY()*1e-6;
        double z=hit.z-worldPosition.getZ()-face.getStepZ()*1e-6;
        if(x<0||x>=1||y<0||y>=1||z<0||z>=1)return false;
        return hasCube((x>=.5?1:0)+(y>=.5?2:0)+(z>=.5?4:0));
    }
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint paint) {
        if(canPaintAt(face,hit))super.paintAt(face,hit,paint);
    }
}
