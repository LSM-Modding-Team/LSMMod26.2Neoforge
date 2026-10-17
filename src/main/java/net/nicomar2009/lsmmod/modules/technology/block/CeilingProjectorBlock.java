package net.nicomar2009.lsmmod.modules.technology.block;
import net.nicomar2009.lsmmod.block.SimpleBlockOutline;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Static ceiling-mounted projector, with four shared physical shapes. */
public class CeilingProjectorBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[] COLLISION=new VoxelShape[4];
    static {
        COLLISION[0]=ExactVoxelShapes.pixels(new double[][]{{1,7,3,15,12,13},{2,12,5,3,15,11},{13,12,5,14,15,11},{2,15,5,14,16,11}});
        var rotations=net.minecraft.world.phys.shapes.Shapes.rotateHorizontal(COLLISION[0]);
        for(int i=1;i<4;i++) COLLISION[i]=rotations.get(Direction.from2DDataValue((i+2)%4));
    }
    public CeilingProjectorBlock(Properties properties) {super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING);}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {return level.getBlockState(pos.above()).isFaceSturdy(level,pos.above(),Direction.DOWN);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state=defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());
        return context.getClickedFace()==Direction.DOWN&&canSurvive(state,context.getLevel(),context.getClickedPos())?state:null;
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {return side==Direction.UP&&!canSurvive(state,level,pos)?Blocks.AIR.defaultBlockState():state;}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return COLLISION[(state.getValue(FACING).get2DDataValue()+2)%4];}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));}
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {return state.rotate(mirror.getRotation(state.getValue(FACING)));}
}
