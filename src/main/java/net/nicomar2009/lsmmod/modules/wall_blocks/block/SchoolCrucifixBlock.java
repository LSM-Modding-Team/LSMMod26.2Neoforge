package net.nicomar2009.lsmmod.modules.wall_blocks.block;
import net.nicomar2009.lsmmod.modules.facilities.block.WideBathroomBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Single wall decoration, 22px wide and 40px high, with six linked cells and one drop. */
public class SchoolCrucifixBlock extends WideBathroomBlock {
    public SchoolCrucifixBlock(Properties properties) {
        super(properties,SchoolCrucifixShapes.BOXES,SchoolCrucifixShapes.BOXES,true,3,false);
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROW);
    }
}
