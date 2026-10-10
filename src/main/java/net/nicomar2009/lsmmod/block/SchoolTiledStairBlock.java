package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** One reusable vanilla stair, with a simple outline and the classroom floor texture. */
public class SchoolTiledStairBlock extends StairBlock {
    public SchoolTiledStairBlock(Properties properties) { super(Blocks.STONE.defaultBlockState(),properties); }
    @Override
    protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        // BlockBehaviour's default collision calls state.getShape(), which would
        // re-enter our outline override. Ask StairBlock for its physical shape.
        return super.getShape(state,level,pos,context);
    }
    @Override
    protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));
    }
}
