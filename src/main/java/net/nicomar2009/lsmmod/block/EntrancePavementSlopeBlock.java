package net.nicomar2009.lsmmod.block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** One of six fixed upright pieces, rising four pixels along the facing direction. */
public class EntrancePavementSlopeBlock extends SchoolYellowBorderWallBlock {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    private final double low;
    private final VoxelShape[] collisions=new VoxelShape[4];
    public double low() {return low;}
    public EntrancePavementSlopeBlock(double low,Properties properties) {
        super(properties);this.low=low;
        registerDefaultState(defaultBlockState().setValue(FACING,Direction.NORTH));
        java.util.List<net.minecraft.world.phys.AABB> boxes=new java.util.ArrayList<>(32);
        for(int i=0;i<32;i++)boxes.add(new net.minecraft.world.phys.AABB(0,0,1-(i+1)/32.0,1,low+.25*(i+1)/32.0,1-i/32.0));
        collisions[0]=ExactVoxelShapes.union(boxes);
        for(int i=1;i<4;i++) {
            collisions[i] = Shapes.rotate(collisions[i-1], com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90);
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {super.createBlockStateDefinition(builder);builder.add(FACING);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return EntrancePavementConnections.update(defaultBlockState().setValue(FACING,context.getHorizontalDirection()),context.getLevel(),context.getClickedPos());
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return collisions[switch(state.getValue(FACING)) {case EAST->1;case SOUTH->2;case WEST->3;default->0;}];
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation) {return super.rotate(state,rotation).setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {return super.mirror(state,mirror).setValue(FACING,mirror.mirror(state.getValue(FACING)));}
}
