package net.nicomar2009.lsmmod.block;
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

/** Wall-mounted static decoration, with a simple cached outline and collision. */
public class SchoolWallDecorationBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    private final double depth;
    public SchoolWallDecorationBlock(double depth,Properties properties) {
        super(properties);this.depth=depth;
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(!context.getClickedFace().getAxis().isHorizontal())return null;
        BlockState state=defaultBlockState().setValue(FACING,context.getClickedFace().getOpposite());
        return canSurvive(state,context.getLevel(),context.getClickedPos())?state:null;
    }
    @Override
    protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {
        Direction wall=state.getValue(FACING);BlockPos support=pos.relative(wall);
        return level.getBlockState(support).isFaceSturdy(level,support,wall.getOpposite());
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        return side==state.getValue(FACING)&&!canSurvive(state,level,pos)?Blocks.AIR.defaultBlockState():state;
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return switch(state.getValue(FACING)) {case EAST->Block.box(16-depth,1,1,16,15,15);case SOUTH->Block.box(1,1,16-depth,15,15,16);case WEST->Block.box(0,1,1,depth,15,15);default->Block.box(1,1,0,15,15,depth);};
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
}
