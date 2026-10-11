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

/** Upright walkable ramp: descends two pixels toward facing, with 32 tiny collision steps. */
public class YellowSchoolSlopeBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    private final VoxelShape[] collisions=new VoxelShape[4];
    public YellowSchoolSlopeBlock(double high,Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));
        java.util.List<net.minecraft.world.phys.AABB> boxes=new java.util.ArrayList<>(32);
        for(int i=0;i<32;i++) boxes.add(new net.minecraft.world.phys.AABB(
                0,0,1-(i+1)/32.0,1,high-.125*i/32.0,1-i/32.0));
        var rotations=Shapes.rotateHorizontal(ExactVoxelShapes.union(boxes));
        for(Direction facing:Direction.Plane.HORIZONTAL) collisions[(facing.get2DDataValue()+2)%4]=rotations.get(facing);
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING,context.getHorizontalDirection()); }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return collisions[(state.getValue(FACING).get2DDataValue()+2)%4]; }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context)); }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return state.setValue(FACING,rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state,Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
}
