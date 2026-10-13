package net.nicomar2009.lsmmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Wall-mounted four-door cabinet with two empty lower cubbies. */
public class DiningWallCabinetBlock extends WideBathroomBlock {
    private static final double[][] BOXES = {{0, 0, 4, 32, 1, 16}, {0, 31, 4, 32, 32, 16}, {0, 1, 15, 32, 31, 16}, {0, 1, 4, 1, 31, 15}, {31, 1, 4, 32, 31, 15}, {1, 8, 4, 31, 9, 15}, {15.5, 1, 4, 16.5, 8, 15}, {0.125, 9, 4, 7.875, 31, 5}, {8.125, 9, 4, 15.875, 31, 5}, {16.125, 9, 4, 23.875, 31, 5}, {24.125, 9, 4, 31.875, 31, 5}, {6.5, 11, 3, 7.0, 16, 3.5}, {6.5, 11, 3.5, 7.0, 11.5, 4}, {6.5, 15.5, 3.5, 7.0, 16, 4}, {9, 11, 3, 9.5, 16, 3.5}, {9, 11, 3.5, 9.5, 11.5, 4}, {9, 15.5, 3.5, 9.5, 16, 4}, {22.5, 11, 3, 23.0, 16, 3.5}, {22.5, 11, 3.5, 23.0, 11.5, 4}, {22.5, 15.5, 3.5, 23.0, 16, 4}, {25, 11, 3, 25.5, 16, 3.5}, {25, 11, 3.5, 25.5, 11.5, 4}, {25, 15.5, 3.5, 25.5, 16, 4}};

    public DiningWallCabinetBlock(Properties properties) {
        super(properties, BOXES, BOXES, true, 2, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }
}
