package net.nicomar2009.lsmmod.modules.facilities.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Closed 2x3 display refrigerator; one item places the six floor-supported cells. */
public class SchoolRefrigeratorBlock extends WideBathroomBlock {
    // The closed enclosure is solid for entities; the two handles project one pixel.
    private static final double[][] BOXES = {
            {0, 0, 1, 32, 48, 16},
            {13.5, 18, 0, 14.25, 28, 1},
            {17.75, 18, 0, 18.5, 28, 1}
    };

    public SchoolRefrigeratorBlock(Properties properties) {
        super(properties, BOXES, BOXES, false, 3, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROW);
    }
}
