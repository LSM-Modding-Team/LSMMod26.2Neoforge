package net.nicomar2009.lsmmod.modules.facilities.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Two-wide, two-high fixture; the floor cubicle retains its integrated door. */
public class TallBathroomBlock extends WideBathroomBlock {
    public TallBathroomBlock(Properties properties, double[][] closed, double[][] opened, boolean wallMounted) {
        super(properties, closed, opened, wallMounted, 2, !wallMounted);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, OPEN);
    }
}
