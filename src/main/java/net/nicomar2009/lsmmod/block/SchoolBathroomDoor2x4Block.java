package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** A full 32x64px bathroom leaf with the shared four-row lifecycle. */
public class SchoolBathroomDoor2x4Block extends SchoolFramedDiningDoorBlock {
    public static final IntegerProperty ROW = IntegerProperty.create("row",0,3);
    public SchoolBathroomDoor2x4Block(Properties properties) {super(properties);}
    private static final java.util.Map<BlockState,VoxelShape> SHAPES=new java.util.concurrent.ConcurrentHashMap<>();
    @Override public boolean hasTransomGlazing() {return false;}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SHAPES.computeIfAbsent(state,key -> {
            boolean open=key.getValue(OPEN),right=key.getValue(HINGE)==DoorHingeSide.RIGHT;
            double[] box=open?new double[]{right?28:0,0,0,right?32:4,64,32}:new double[]{0,0,0,32,64,4};
            int[] offset={key.getValue(COLUMN)*16,key.getValue(ROW)*16,key.getValue(DEPTH)*16};
            double[] part=new double[6];
            for(int axis=0;axis<3;axis++) {
                part[axis]=Math.max(0,box[axis]-offset[axis]);part[axis+3]=Math.min(16,box[axis+3]-offset[axis]);
                if(part[axis]>=part[axis+3])return Shapes.empty();
            }
            return Shapes.rotateHorizontal(ExactVoxelShapes.pixels(new double[][]{part})).get(key.getValue(FACING));
        });
    }
    @Override public int rows() {return 4;}
    @Override protected IntegerProperty rowProperty() {return ROW;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,OPEN,COLUMN,ROW,DEPTH,HINGE);
    }
}
