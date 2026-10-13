package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

/** Original bathroom door and glass transom, with six saved paints per stationary column cell. */
public class SchoolPaintableBathroomColumnDoorBlock extends SchoolFramedDiningDoorBlock implements EntityBlock {
    public SchoolPaintableBathroomColumnDoorBlock(Properties properties) {super(properties);}
    public static boolean columnCell(BlockState state) {
        return state.getValue(DEPTH)==0&&state.getValue(COLUMN)==(state.getValue(HINGE)==DoorHingeSide.RIGHT?0:1);
    }
    // Preserve the small native cell volumes so the inner/upper column faces can be targeted
    // beneath the transom; the inherited bounding outline would cover them with empty space.
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return getCollisionShape(state,level,pos,context);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return columnCell(state)?new SchoolBathroomColumnPaintEntity(pos,state):null;
    }
}
