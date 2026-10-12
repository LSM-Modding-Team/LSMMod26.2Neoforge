package net.nicomar2009.lsmmod.block;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nicomar2009.lsmmod.registry.ModBlocks;

/** One state, no entity: neighbor-selected base and wall-colored quadrant joins. */
public class SchoolCircularColumnBlock extends Block {
    private static final Direction[] SIDES={Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
    private record Geometry(VoxelShape collision,VoxelShape outline) {}
    private static final ConcurrentMap<Integer,Geometry> GEOMETRY=new ConcurrentHashMap<>();
    public SchoolCircularColumnBlock(Properties properties) { super(properties); }
    public static boolean hasBase(BlockGetter level,BlockPos pos) {
        return level==null || pos==null || !(level.getBlockState(pos.below()).getBlock() instanceof SchoolCircularColumnBlock);
    }
    /** -1 means no connection. Quadrants use the neighboring face's world axes. */
    public static int wallPaint(BlockGetter level,BlockPos pos,int side,int quarter) {
        if(level==null || pos==null) return -1;
        Direction direction=SIDES[side];BlockPos neighbor=pos.relative(direction);BlockState other=level.getBlockState(neighbor);
        if(other.is(ModBlocks.LIGHT_SCHOOL_WALL.get()) || other.is(ModBlocks.LIGHT_SCHOOL_WALL_RAILING.get())) return SchoolPaint.LIGHT.ordinal();
        if(level.getBlockEntity(neighbor) instanceof SchoolPaintableBlockEntity painted) {
            Direction face=direction.getOpposite();
            if(painted instanceof SchoolPaintableCustomBlockEntity custom) {
                int u=quarter&1,y=quarter>>1;
                int x=direction.getAxis()==Direction.Axis.X ? (direction==Direction.WEST?1:0) : u;
                int z=direction.getAxis()==Direction.Axis.Z ? (direction==Direction.NORTH?1:0) : u;
                if(!custom.hasCube(x+2*y+4*z)) return -1;
                return custom.squarePaint(face,quarter).ordinal();
            }
            return painted.paint(face).ordinal();
        }
        return -1;
    }
    /** The railing's masonry ends at y=12px; its wooden bars are never copied. */
    public static int wallHeight(BlockGetter level,BlockPos pos,int side,int quarter) {
        if(wallPaint(level,pos,side,quarter)<0) return 0;
        return level.getBlockState(pos.relative(SIDES[side])).is(ModBlocks.LIGHT_SCHOOL_WALL_RAILING.get()) ? 12 : 16;
    }
    private static int geometryKey(BlockGetter level,BlockPos pos) {
        int key=0;
        for(int side=0;side<4;side++) for(int q=0;q<4;q++) {
            int height=wallHeight(level,pos,side,q);
            if(height>0) key|=1<<(side*4+q);
            if(height==12) key|=1<<(16+side);
        }
        return key; // Color and base texture do not change the physical cylinder.
    }
    private static Geometry build(int key) {
        ArrayList<double[]> boxes=new ArrayList<>();
        for(double[] b:SchoolDrainColumnShapes.FIXED) boxes.add(b);
        for(int side=0;side<4;side++) for(int q=0;q<4;q++) {
            int profile=(key&(1<<(side*4+q)))==0 ? 0 : (key&(1<<(16+side)))!=0 ? 1 : 2;
            int local=side>=2 ? q^1 : q;
            for(double[] source:SchoolDrainColumnShapes.OUTER[local][profile]) {
                double[] b=source.clone();
                for(int turn=0;turn<side;turn++) b=new double[]{16-b[5],b[1],b[0],16-b[2],b[4],b[3]};
                boxes.add(b);
            }
        }
        VoxelShape shape=ExactVoxelShapes.pixels(boxes.toArray(double[][]::new));
        return new Geometry(shape,SimpleBlockOutline.boundingBox(shape));
    }
    private static Geometry geometry(BlockGetter level,BlockPos pos) { return GEOMETRY.computeIfAbsent(geometryKey(level,pos),SchoolCircularColumnBlock::build); }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return geometry(level,pos).collision(); }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return geometry(level,pos).outline(); }
    @Override protected boolean skipRendering(BlockState state,BlockState neighbor,Direction direction) {
        return direction.getAxis()==Direction.Axis.Y && neighbor.is(this) || super.skipRendering(state,neighbor,direction);
    }
}
