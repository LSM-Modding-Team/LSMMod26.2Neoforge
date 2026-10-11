package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Full pavement cube; yellow borders disappear along joins on each face. */
public class SchoolYellowBorderWallBlock extends Block {
    public static final BooleanProperty NORTH=BooleanProperty.create("north");
    public static final BooleanProperty EAST=BooleanProperty.create("east");
    public static final BooleanProperty SOUTH=BooleanProperty.create("south");
    public static final BooleanProperty WEST=BooleanProperty.create("west");
    public static final BooleanProperty UP=BooleanProperty.create("up");
    public static final BooleanProperty DOWN=BooleanProperty.create("down");
    public SchoolYellowBorderWallBlock(Properties properties) {
        super(properties);
    }
    public static BooleanProperty property(Direction side) {
        return switch(side) {case NORTH->NORTH;case EAST->EAST;case SOUTH->SOUTH;case WEST->WEST;case UP->UP;case DOWN->DOWN;};
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        // Border connections are selected from the world by Fusion.
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return EntrancePavementConnections.update(defaultBlockState(),context.getLevel(),context.getClickedPos());
    }
    @Override
    protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,
            Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {
        ticks.scheduleTick(pos,this,1);
        return EntrancePavementConnections.update(state,level,pos);
    }
    @Override
    protected void onPlace(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,BlockState oldState,boolean moved) {
        super.onPlace(state,level,pos,oldState,moved);EntrancePavementConnections.refresh(level,pos);
    }
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state,net.minecraft.server.level.ServerLevel level,BlockPos pos,boolean moved) {
        super.affectNeighborsAfterRemoval(state,level,pos,moved);EntrancePavementConnections.refresh(level,pos);
    }
    @Override
    protected void tick(BlockState state,net.minecraft.server.level.ServerLevel level,BlockPos pos,RandomSource random) {
        level.sendBlockUpdated(pos,state,state,Block.UPDATE_CLIENTS);
    }
    @Override
    protected BlockState rotate(BlockState state,Rotation rotation) {
        return state;
    }
    @Override
    protected BlockState mirror(BlockState state,Mirror mirror) {
        return state;
    }
}
