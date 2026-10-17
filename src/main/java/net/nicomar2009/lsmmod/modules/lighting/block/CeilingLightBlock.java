package net.nicomar2009.lsmmod.modules.lighting.block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Ceiling disc lamp, manually toggled between block-light levels 15 and zero. */
public class CeilingLightBlock extends Block {
    public static final BooleanProperty LIT=BlockStateProperties.LIT;
    private static final VoxelShape SHAPE=Block.box(1.5,13.5,1.5,14.5,16,14.5);
    public CeilingLightBlock(Properties properties) {super(properties);registerDefaultState(stateDefinition.any().setValue(LIT,true));}
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(LIT);}
    @Override
    protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos) {return level.getBlockState(pos.above()).isFaceSturdy(level,pos.above(),Direction.DOWN);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getClickedFace()==Direction.DOWN&&canSurvive(defaultBlockState(),context.getLevel(),context.getClickedPos())?defaultBlockState():null;
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        return side==Direction.UP&&!canSurvive(state,level,pos)?Blocks.AIR.defaultBlockState():state;
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!player.mayBuild()||!level.mayInteract(player,pos))return InteractionResult.PASS;
        if(!level.isClientSide())level.setBlock(pos,state.cycle(LIT),Block.UPDATE_ALL);
        return InteractionResult.SUCCESS;
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SHAPE;}
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SHAPE;}
}
