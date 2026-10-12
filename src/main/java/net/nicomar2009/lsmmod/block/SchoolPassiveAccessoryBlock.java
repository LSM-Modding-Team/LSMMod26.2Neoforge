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

/** Four-state passive accessory: cached physical grid and simple selection box. */
public final class SchoolPassiveAccessoryBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    private final java.util.Map<Direction,VoxelShape> shapes;
    public SchoolPassiveAccessoryBlock(Properties properties,double[][] boxes) {
        super(properties);
        shapes=Shapes.rotateHorizontal(ExactVoxelShapes.pixels(boxes));
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face=context.getClickedFace();
        return defaultBlockState().setValue(FACING,face.getAxis().isVertical()?context.getHorizontalDirection().getOpposite():face);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return shapes.get(state.getValue(FACING));}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));}
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
}
