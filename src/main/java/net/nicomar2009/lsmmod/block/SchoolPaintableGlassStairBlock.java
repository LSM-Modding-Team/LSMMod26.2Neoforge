package net.nicomar2009.lsmmod.block;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Vanilla glass stair corners/inversion, with a world-space editable 8px solid half. */
public class SchoolPaintableGlassStairBlock extends SchoolGlassStairBlock implements EntityBlock {
    private record ShapeKey(Direction facing,Half half,net.minecraft.world.level.block.state.properties.StairsShape corner,int occupied) {}
    // Four facings x two halves x five corners x sixteen slab masks, shared across palettes.
    private static final ConcurrentHashMap<ShapeKey,VoxelShape> SHAPES=new ConcurrentHashMap<>();
    public SchoolPaintableGlassStairBlock(Properties properties) {super(properties);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {return new SchoolGlassStairPaintEntity(pos,state);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        int mask=level.getBlockEntity(pos) instanceof SchoolGlassStairPaintEntity entity?entity.occupied():SchoolGlassStairPaintEntity.slabMask(state);
        ShapeKey key=new ShapeKey(state.getValue(FACING),state.getValue(HALF),state.getValue(SHAPE),mask);
        VoxelShape cached=SHAPES.get(key);
        if(cached!=null)return cached;
        boolean top=state.getValue(HALF)==Half.TOP;
        var boxes=new ArrayList<AABB>();
        for(AABB box:super.getCollisionShape(state,level,pos,context).toAabbs()) {
            double low=Math.max(box.minY,top?0:.5),high=Math.min(box.maxY,top?.5:1);
            if(low<high)boxes.add(new AABB(box.minX,low,box.minZ,box.maxX,high,box.maxZ));
        }
        for(int cube=0;cube<8;cube++)if((mask&(1<<cube))!=0) {
            double x=(cube&1)*.5,y=((cube>>1)&1)*.5,z=((cube>>2)&1)*.5;
            boxes.add(new AABB(x,y,z,x+.5,y+.5,z+.5));
        }
        VoxelShape built=ExactVoxelShapes.union(boxes);
        VoxelShape existing=SHAPES.putIfAbsent(key,built);
        return existing==null?built:existing;
    }
    // Carved holes depend on the entity, so the outline cannot use a BlockState-only cache.
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return getCollisionShape(state,level,pos,context);}
    @Override protected VoxelShape getOcclusionShape(BlockState state) {return Shapes.empty();}
    @Override protected boolean skipRendering(BlockState state,BlockState neighbor,Direction side) {return false;}
}
