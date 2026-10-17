package net.nicomar2009.lsmmod.modules.special_blocks.block;
import net.nicomar2009.lsmmod.block.SchoolRectangularBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolFacilityShapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Six-wide, four-high trophy display; shared shapes, no inventory or ticker. */
public final class SchoolTrophyGalleryBlock extends SchoolRectangularBlock {
    public SchoolTrophyGalleryBlock(Properties properties) {super(properties,SchoolFacilityShapes.TROPHIES,SchoolFacilityShapes.TROPHIES,false,6,4,false);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,COLUMN,ROW);}
}
