package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Closed decorative cabinet, two wide and two high; solid top supports other blocks. */
public class SchoolCabinetBlock extends WideBathroomBlock {
    private static final double[][] BOXES = {{0, 0, 0, 32, 32, 16}};

    public SchoolCabinetBlock(Properties properties) {
        super(properties, BOXES, BOXES, true, 2, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }
}
