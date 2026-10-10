package net.nicomar2009.lsmmod.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** World-aligned 24px tiles: three blocks contain exactly two tiles per axis. */
public class BathroomFloorBlock extends Block {
    public static final IntegerProperty TILE_X=IntegerProperty.create("tile_x",0,2);
    public static final IntegerProperty TILE_Z=IntegerProperty.create("tile_z",0,2);
    public BathroomFloorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TILE_X,0).setValue(TILE_Z,0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(TILE_X,TILE_Z);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(TILE_X,Math.floorMod(context.getClickedPos().getX(),3))
                .setValue(TILE_Z,Math.floorMod(context.getClickedPos().getZ(),3));
    }
}
