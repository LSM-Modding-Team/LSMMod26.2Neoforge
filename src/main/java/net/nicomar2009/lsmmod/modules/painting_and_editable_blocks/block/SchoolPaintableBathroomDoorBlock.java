package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolFramedDiningDoorBlock;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original bathroom leaf and lifecycle, with a stationary, editable 8px masonry frame. */
public class SchoolPaintableBathroomDoorBlock extends SchoolFramedDiningDoorBlock implements EntityBlock {
    private static final long[][][] FRAME_MASKS=frameMasks();
    private static long[][][] frameMasks() {
        long[][][] masks=new long[2][3][2];
        for(int right=0;right<2;right++)for(int row=0;row<3;row++)for(int column=0;column<2;column++)
            for(int cube=0;cube<64;cube++) {
                int x=(cube&3)*4+column*16,y=((cube>>2)&3)*4+row*16;
                if(y>=40||(right==1?x<8:x>=24))masks[right][row][column]|=1L<<cube;
            }
        return masks;
    }
    public SchoolPaintableBathroomDoorBlock(Properties properties) { super(properties); }
    @Override public boolean hasTransomGlazing() { return false; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SchoolDoorFramePaintEntity(pos,state);
    }
    public static long initialFrame(BlockState state) {
        if(state.getValue(DEPTH)!=0)return 0;
        return FRAME_MASKS[state.getValue(HINGE)==DoorHingeSide.RIGHT?1:0][state.getValue(ROW)][state.getValue(COLUMN)];
    }
    private static double[] leaf(BlockState state) {
        double[] b=state.getValue(OPEN)?new double[]{0,0,0,4,40,24}:new double[]{0,0,0,24,40,4};
        if(state.getValue(HINGE)==DoorHingeSide.RIGHT) {double x=b[0];b[0]=32-b[3];b[3]=32-x;}
        return b;
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        if(level.getBlockEntity(pos) instanceof SchoolDoorFramePaintEntity entity)
            return entity.collision(state,()->physical(state,entity.occupied()));
        return physical(state,initialFrame(state));
    }
    private static VoxelShape physical(BlockState state,long mask) {
        var boxes=new ArrayList<double[]>();
        for(int cube=0;cube<64;cube++)if((mask&(1L<<cube))!=0) {
            double x=(cube&3)*4,y=((cube>>2)&3)*4,z=((cube>>4)&3)*4;
            boxes.add(new double[]{x,y,z,x+4,y+4,z+4});
        }
        double[] b=leaf(state);int[] offset={state.getValue(COLUMN)*16,state.getValue(ROW)*16,state.getValue(DEPTH)*16};
        double[] part=new double[6];boolean present=true;
        for(int axis=0;axis<3;axis++) {
            part[axis]=Math.max(0,b[axis]-offset[axis]);part[axis+3]=Math.min(16,b[axis+3]-offset[axis]);
            present&=part[axis]<part[axis+3];
        }
        if(present)boxes.add(part);
        return Shapes.rotateHorizontal(ExactVoxelShapes.pixels(boxes.toArray(double[][]::new))).get(state.getValue(FACING));
    }
    // World-dependent holes must remain targetable; a cache keyed only by BlockState is invalid here.
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return getCollisionShape(state,level,pos,context);
    }
}
