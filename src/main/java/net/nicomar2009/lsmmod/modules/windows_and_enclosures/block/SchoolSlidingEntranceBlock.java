package net.nicomar2009.lsmmod.modules.windows_and_enclosures.block;
import net.nicomar2009.lsmmod.block.SchoolRectangularBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Fixed decorative sides; four central leaves retract into the lateral pockets. */
public final class SchoolSlidingEntranceBlock extends SchoolRectangularBlock {
    public SchoolSlidingEntranceBlock(Properties properties) {
        super(properties,SchoolEntranceAccessoryShapes.GATE_CLOSED,SchoolEntranceAccessoryShapes.GATE_OPEN,false,6,4,true);
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,COLUMN,ROW,OPEN);
    }
}
