package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** One full-cube state, six saved face colors, no connections or ticking. */
public class SchoolPaintableBlock extends Block implements EntityBlock {
    public SchoolPaintableBlock(Properties properties) { super(properties); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SchoolPaintableBlockEntity(pos, state);
    }
}
