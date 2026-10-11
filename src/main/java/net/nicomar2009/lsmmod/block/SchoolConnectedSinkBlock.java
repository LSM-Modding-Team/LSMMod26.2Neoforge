package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Continuous 24px trough; matching neighbors remove the interior side partitions. */
public class SchoolConnectedSinkBlock extends ChildrenLockerBlock {
    private static final VoxelShape[][][] COLLISIONS=new VoxelShape[2][4][4];
    private static final VoxelShape[][][] OUTLINES=new VoxelShape[2][4][4];
    static {
        for(int half=0;half<2;half++) for(int mask=0;mask<4;mask++) {
            var rotations=Shapes.rotateHorizontal(ExactVoxelShapes.pixels(SchoolConnectedSinkShapes.BOXES[half][mask]));
            for(Direction facing:Direction.Plane.HORIZONTAL) {
                int rotation=(facing.get2DDataValue()+2)%4;
                COLLISIONS[half][mask][rotation]=rotations.get(facing);
                OUTLINES[half][mask][rotation]=SimpleBlockOutline.boundingBox(rotations.get(facing));
            }
        }
    }
    public SchoolConnectedSinkBlock(Properties properties) { super(properties); }
    private static boolean joins(BlockState state,BlockState other) {
        return other.is(state.getBlock()) && other.getValue(FACING)==state.getValue(FACING) && other.getValue(HALF)==state.getValue(HALF);
    }
    public static int connections(BlockGetter level,BlockPos pos,BlockState state) {
        if(level==null || pos==null || !(state.getBlock() instanceof SchoolConnectedSinkBlock)) return 0;
        Direction facing=state.getValue(FACING);
        return (joins(state,level.getBlockState(pos.relative(facing.getCounterClockWise()))) ? 1 : 0)
                | (joins(state,level.getBlockState(pos.relative(facing.getClockWise()))) ? 2 : 0);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getClickedFace()==Direction.UP ? super.getStateForPlacement(context) : null;
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return COLLISIONS[state.getValue(HALF)==DoubleBlockHalf.UPPER?1:0][connections(level,pos,state)][(state.getValue(FACING).get2DDataValue()+2)%4];
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return OUTLINES[state.getValue(HALF)==DoubleBlockHalf.UPPER?1:0][connections(level,pos,state)][(state.getValue(FACING).get2DDataValue()+2)%4];
    }
}
