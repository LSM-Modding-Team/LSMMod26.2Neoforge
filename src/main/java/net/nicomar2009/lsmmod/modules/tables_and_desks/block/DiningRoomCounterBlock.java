package net.nicomar2009.lsmmod.modules.tables_and_desks.block;
import net.nicomar2009.lsmmod.modules.building_blocks.block.BathroomFloorBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Keep saved tile phases while the counter becomes only its upper two pixels. */
public final class DiningRoomCounterBlock extends BathroomFloorBlock {
    private static final VoxelShape TOP = Block.box(0,14,0,16,16,16);
    public DiningRoomCounterBlock(Properties properties) { super(properties); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return TOP; }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return TOP; }
}
