package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.DoubleSchoolEntranceBlock;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original double leaves and controller with a stationary, full-depth paintable upper slab. */
public class SchoolPaintableDoubleEntranceBlock extends DoubleSchoolEntranceBlock implements EntityBlock {
    private static final java.util.Map<BlockState,VoxelShape> SHAPES=new java.util.concurrent.ConcurrentHashMap<>();
    public SchoolPaintableDoubleEntranceBlock(Properties properties) {super(properties);}
    public static boolean slabCell(BlockState state) {return state.getValue(ROW)==2&&state.getValue(DEPTH)==0;}
    @Override public boolean hasTransomGlazing() {return false;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return slabCell(state)?new SchoolEntranceSlabPaintEntity(pos,state):null;
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        VoxelShape original=super.getCollisionShape(state,level,pos,context);
        if(!slabCell(state))return original;
        VoxelShape cached=SHAPES.get(state);
        if(cached!=null)return cached;
        var boxes=new ArrayList<AABB>();
        for(AABB box:original.toAabbs()) {
            double high=Math.min(box.maxY,.5);
            if(box.minY<high)boxes.add(new AABB(box.minX,box.minY,box.minZ,box.maxX,high,box.maxZ));
        }
        boxes.add(new AABB(0,.5,0,1,1,1));
        VoxelShape built=ExactVoxelShapes.union(boxes);
        VoxelShape existing=SHAPES.putIfAbsent(state,built);
        return existing==null?built:existing;
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return getCollisionShape(state,level,pos,context);
    }
}
