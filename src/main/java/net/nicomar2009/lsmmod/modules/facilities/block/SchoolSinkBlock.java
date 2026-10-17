package net.nicomar2009.lsmmod.modules.facilities.block;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.ChildrenLockerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Passive 1.5-block-high pedestal sink; reuse paired placement and single-drop teardown. */
public class SchoolSinkBlock extends ChildrenLockerBlock {
    private static final VoxelShape[][] COLLISIONS = new VoxelShape[2][4];
    static {
        for(int half=0;half<2;half++) {
            var rotations=Shapes.rotateHorizontal(ExactVoxelShapes.pixels(half==0 ? SchoolSinkShapes.LOWER : SchoolSinkShapes.UPPER));
            for(Direction facing:Direction.Plane.HORIZONTAL) COLLISIONS[half][(facing.get2DDataValue()+2)%4]=rotations.get(facing);
        }
    }
    public SchoolSinkBlock(Properties properties) { super(properties); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getClickedFace()==Direction.UP ? super.getStateForPlacement(context) : null;
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return COLLISIONS[state.getValue(HALF)==DoubleBlockHalf.UPPER ? 1 : 0][(state.getValue(FACING).get2DDataValue()+2)%4];
    }
}
