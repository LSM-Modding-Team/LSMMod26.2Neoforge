package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Original fountain shifted 8px upward, reserving both physical cells. */
public final class RaisedDrinkingFountainBlock extends SchoolRectangularBlock {
    public RaisedDrinkingFountainBlock(Properties properties) {
        super(properties,SchoolEntranceAccessoryShapes.FOUNTAIN,SchoolEntranceAccessoryShapes.FOUNTAIN,true,1,2,false);
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,HALF);
    }
}
