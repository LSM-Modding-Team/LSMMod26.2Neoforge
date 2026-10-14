package net.nicomar2009.lsmmod.block;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A fixed lower slab and an independent upper 2px sash, opening outward about its top edge. */
public class SchoolSlabProjectingWindowBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN=BlockStateProperties.OPEN;
    private record Geometry(VoxelShape pane,VoxelShape collision,VoxelShape outline) {}
    private static final Map<BlockState,Geometry> GEOMETRY=new ConcurrentHashMap<>();
    public SchoolSlabProjectingWindowBlock(Properties properties) {
        super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(OPEN,false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,OPEN);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {return new SchoolSlabProjectingWindowPaintEntity(pos,state);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());}
    private static Geometry geometry(BlockState state) {
        return GEOMETRY.computeIfAbsent(state,key -> {
            var boxes=new ArrayList<AABB>();
            if(!key.getValue(OPEN))boxes.add(ExactVoxelShapes.box(0,8,14,16,16,16));
            else {
                // Quarter-pixel strips bound the same -45 degree native element rotation.
                double c=Math.sqrt(.5);
                for(int iy=0;iy<32;iy++)for(int iz=0;iz<8;iz++) {
                    double y0=Double.POSITIVE_INFINITY,z0=y0,y1=Double.NEGATIVE_INFINITY,z1=y1;
                    for(int dy=0;dy<2;dy++)for(int dz=0;dz<2;dz++) {
                        double y=8+(iy+dy)*.25-16,z=14+(iz+dz)*.25-16;
                        double ry=16+(y+z)*c,rz=16+(-y+z)*c;
                        y0=Math.min(y0,ry);y1=Math.max(y1,ry);z0=Math.min(z0,rz);z1=Math.max(z1,rz);
                    }
                    boxes.add(ExactVoxelShapes.box(0,y0,z0,16,y1,z1));
                }
            }
            VoxelShape pane=Shapes.rotateHorizontal(ExactVoxelShapes.union(boxes)).get(key.getValue(FACING));
            VoxelShape slab=Block.box(0,0,0,16,8,16);
            return new Geometry(pane,Shapes.or(slab,pane),Shapes.or(slab,SimpleBlockOutline.boundingBox(pane)));
        });
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return geometry(state).collision();}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return geometry(state).outline();}
    @Override protected VoxelShape getOcclusionShape(BlockState state) {return Shapes.empty();}
    private static boolean clearSwing(Level level,BlockPos pos,BlockState next,Player player) {
        VoxelShape pane=geometry(next).pane();AABB bounds=pane.bounds();
        for(int x=(int)Math.floor(bounds.minX);x<(int)Math.ceil(bounds.maxX);x++)
            for(int y=(int)Math.floor(bounds.minY);y<(int)Math.ceil(bounds.maxY);y++)
                for(int z=(int)Math.floor(bounds.minZ);z<(int)Math.ceil(bounds.maxZ);z++) {
                    if(x==0&&y==0&&z==0)continue;
                    BlockPos target=pos.offset(x,y,z);
                    if(!level.hasChunkAt(target)||level.isOutsideBuildHeight(target)||!level.getWorldBorder().isWithinBounds(target)||!level.mayInteract(player,target))return false;
                    VoxelShape obstacle=level.getBlockState(target).getCollisionShape(level,target,CollisionContext.empty());
                    if(Shapes.joinIsNotEmpty(pane.move(-x,-y,-z),obstacle,BooleanOp.AND))return false;
                }
        return level.isUnobstructed(next,pos,CollisionContext.empty());
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(player.isSpectator()||!player.mayBuild()||!level.mayInteract(player,pos))return InteractionResult.PASS;
        if(level.getBlockEntity(pos) instanceof SchoolSlabProjectingWindowPaintEntity slab&&slab.canPaintAt(hit.getDirection(),hit.getLocation()))return InteractionResult.PASS;
        if(level.isClientSide())return InteractionResult.SUCCESS;
        BlockState next=state.cycle(OPEN);
        if(!clearSwing(level,pos,next,player))return InteractionResult.FAIL;
        level.setBlock(pos,next,Block.UPDATE_ALL);
        level.playSound(null,pos,next.getValue(OPEN)?SoundEvents.IRON_DOOR_OPEN:SoundEvents.IRON_DOOR_CLOSE,SoundSource.BLOCKS,.4F,1.2F);
        return InteractionResult.SUCCESS;
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
}
