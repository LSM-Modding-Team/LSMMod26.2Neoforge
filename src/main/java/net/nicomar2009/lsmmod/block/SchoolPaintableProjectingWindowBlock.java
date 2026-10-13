package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** A half-volume paintable wall and a separately moving two-pixel pane on the far edge. */
public class SchoolPaintableProjectingWindowBlock extends SchoolProjectingWindowBlock implements EntityBlock {
    public SchoolPaintableProjectingWindowBlock(Properties properties) {super(properties);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING,OPEN,HINGE,WALL_LEFT);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return new SchoolProjectingWindowPaintEntity(pos,state);
    }
}
