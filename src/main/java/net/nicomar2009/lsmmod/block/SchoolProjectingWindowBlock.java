package net.nicomar2009.lsmmod.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Thin independent 24x24px sashes; controllers store poses, never an entire wall's opening. */
public class SchoolProjectingWindowBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN=BlockStateProperties.OPEN;
    public static final EnumProperty<ProjectingWindowHinge> HINGE=EnumProperty.create("hinge",ProjectingWindowHinge.class);
    private static final int SCAN_LIMIT=64;
    /** Cell bounds and full pane origin in local north-facing coordinates. */
    public record Part(int x0,int x1,int y0,int y1,int ox,int oy) {}
    private record Axis(int low,int high,int origin) {}
    private static final Axis[][] AXES={
            {new Axis(0,16,0)},
            {new Axis(0,8,-16),new Axis(8,16,8)},
            {new Axis(0,16,-8)}
    };
    private static final List<List<Part>> PARTS=new ArrayList<>();
    private record Geometry(VoxelShape collision,VoxelShape outline) {}
    // Geometry keys include lattice phase, every selected pane pose and orientation.
    private static final Map<Integer,Geometry> GEOMETRY=new ConcurrentHashMap<>();
    private static final Map<Integer,VoxelShape> FRAGMENTS=new ConcurrentHashMap<>();
    static {
        for(int y=0;y<3;y++)for(int x=0;x<3;x++) {
            List<Part> parts=new ArrayList<>();
            for(Axis ay:AXES[y])for(Axis ax:AXES[x])parts.add(new Part(ax.low,ax.high,ay.low,ay.high,ax.origin,ay.origin));
            PARTS.add(List.copyOf(parts));
        }
    }
    public SchoolProjectingWindowBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH)
                .setValue(OPEN,false).setValue(HINGE,ProjectingWindowHinge.TOP));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,OPEN,HINGE);}
    private static int u(BlockPos pos,Direction right) {
        int coordinate=right.getAxis()==Direction.Axis.X?pos.getX():pos.getZ();
        return right.getStepX()+right.getStepZ()>0?coordinate:-coordinate-1;
    }
    public static int phase(BlockPos pos,Direction facing) {
        return Math.floorMod(u(pos,facing.getClockWise()),3)+3*Math.floorMod(pos.getY(),3);
    }
    public static List<Part> parts(int phase) {return PARTS.get(phase);}
    private static BlockPos controller(BlockPos pos,Direction right,Part part) {
        return pos.relative(right,Math.floorDiv(part.ox,16)).above(Math.floorDiv(part.oy,16));
    }
    private static BlockState read(BlockGetter level,BlockPos pos) {
        if(level==null||(level instanceof LevelReader reader&&!reader.hasChunkAt(pos)))return Blocks.AIR.defaultBlockState();
        return level.getBlockState(pos);
    }
    private static boolean same(BlockState value,BlockState state) {
        return value.getBlock()==state.getBlock()&&value.hasProperty(FACING)&&value.getValue(FACING)==state.getValue(FACING);
    }
    private static boolean complete(BlockGetter level,BlockPos origin,BlockState state) {
        Direction right=state.getValue(FACING).getClockWise();
        for(int y=0;y<2;y++)for(int x=0;x<2;x++)if(!same(read(level,origin.relative(right,x).above(y)),state))return false;
        return true;
    }
    /** Closed fallback for missing controllers/chunks or a stale renderer state. */
    public static int pose(BlockGetter level,BlockPos pos,BlockState state,int index) {
        if(state==null||!(state.getBlock() instanceof SchoolProjectingWindowBlock)||pos==null)return 0;
        List<Part> parts=parts(phase(pos,state.getValue(FACING)));
        if(index<0||index>=parts.size()||!same(read(level,pos),state))return 0;
        BlockPos origin=controller(pos,state.getValue(FACING).getClockWise(),parts.get(index));
        BlockState value=read(level,origin);
        return same(value,state)&&complete(level,origin,state)&&value.getValue(OPEN)?1+value.getValue(HINGE).ordinal():0;
    }
    private static double[] pivot(Part part,int pose) {
        return new double[]{part.ox+(pose==4?24:0),part.oy+(pose==1?24:0),7};
    }
    /** Rigid rotation preserves the two-pixel thickness; positive Z faces away from the viewer. */
    public static double[] transform(Part part,int pose,double x,double y,double z,boolean inverse) {
        if(pose==0)return new double[]{x,y,z};
        double[] origin=pivot(part,pose);
        double angle=(pose==1||pose==3?1:-1)*Math.PI/4*(inverse?-1:1),c=Math.cos(angle),s=Math.sin(angle);
        x-=origin[0];y-=origin[1];z-=origin[2];
        if(pose<=2)return new double[]{x+origin[0],c*y-s*z+origin[1],s*y+c*z+origin[2]};
        return new double[]{c*x+s*z+origin[0],y+origin[1],-s*x+c*z+origin[2]};
    }
    private static VoxelShape fragment(int phase,int index,int pose,int facing) {
        int key=((phase*4+index)*5+pose)*4+facing;
        return FRAGMENTS.computeIfAbsent(key,ignored->{
            Part p=parts(phase).get(index);List<AABB> boxes=new ArrayList<>();
            if(pose==0)boxes.add(ExactVoxelShapes.box(p.x0,p.y0,7,p.x1,p.y1,9));
            else {
                // Quarter-pixel strips conservatively approximate the rotated physical pane.
                boolean vertical=pose<=2;double first=vertical?p.y0:p.x0,last=vertical?p.y1:p.x1;
                for(double start=first;start<last;start+=.25) {
                    double[] low={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};
                    double[] high={Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                    for(double x:new double[]{vertical?p.x0:start,vertical?p.x1:Math.min(last,start+.25)})
                        for(double y:new double[]{vertical?start:p.y0,vertical?Math.min(last,start+.25):p.y1})
                            for(double z:new double[]{7,9}) {
                                double[] v=transform(p,pose,x,y,z,false);
                                for(int axis=0;axis<3;axis++){low[axis]=Math.min(low[axis],v[axis]);high[axis]=Math.max(high[axis],v[axis]);}
                            }
                    boxes.add(ExactVoxelShapes.box(low[0],low[1],low[2],high[0],high[1],high[2]));
                }
            }
            VoxelShape shape=ExactVoxelShapes.union(boxes);
            for(int turn=0;turn<facing;turn++) {
                List<AABB> rotated=new ArrayList<>();
                shape.forAllBoxes((x0,y0,z0,x1,y1,z1)->rotated.add(new AABB(1-z1,y0,x0,1-z0,y1,x1)));
                shape=ExactVoxelShapes.union(rotated);
            }
            return shape;
        });
    }
    private Geometry geometry(BlockState state,BlockGetter level,BlockPos pos) {
        int phase=phase(pos,state.getValue(FACING)),facing=switch(state.getValue(FACING)){case EAST->1;case SOUTH->2;case WEST->3;default->0;};
        List<Part> parts=parts(phase);int code=0;int[] poses=new int[parts.size()];
        for(int i=0;i<parts.size();i++){poses[i]=pose(level,pos,state,i);code=code*5+poses[i];}
        int key=(phase*625+code)*4+facing;
        return GEOMETRY.computeIfAbsent(key,ignored->{
            VoxelShape collision=Shapes.empty(),outline=Shapes.empty();
            for(int i=0;i<parts.size();i++) {
                VoxelShape part=fragment(phase,i,poses[i],facing);
                collision=Shapes.or(collision,part);outline=Shapes.or(outline,SimpleBlockOutline.boundingBox(part));
            }
            return new Geometry(collision,outline);
        });
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return geometry(state,level,pos).collision;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return geometry(state,level,pos).outline;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction side=context.getClickedFace(),facing=side.getAxis().isHorizontal()?side:context.getHorizontalDirection().getOpposite();
        BlockState neighbor=context.getLevel().getBlockState(context.getClickedPos().relative(side.getOpposite()));
        if(neighbor.is(this))facing=neighbor.getValue(FACING);
        return defaultBlockState().setValue(FACING,facing);
    }
    private static int clickedPart(BlockState state,Level level,BlockPos pos,BlockHitResult hit) {
        Direction right=state.getValue(FACING).getClockWise(),back=state.getValue(FACING).getOpposite();
        double dx=hit.getLocation().x-pos.getX()-.5,dz=hit.getLocation().z-pos.getZ()-.5;
        double x=8+16*(dx*right.getStepX()+dz*right.getStepZ()),y=16*(hit.getLocation().y-pos.getY());
        double z=8+16*(dx*back.getStepX()+dz*back.getStepZ());
        List<Part> parts=parts(phase(pos,state.getValue(FACING)));int best=0;double distance=Double.POSITIVE_INFINITY;
        for(int i=0;i<parts.size();i++) {
            Part p=parts.get(i);double[] v=transform(p,pose(level,pos,state,i),x,y,z,true);
            double a=Math.max(p.x0-v[0],Math.max(0,v[0]-p.x1)),b=Math.max(p.y0-v[1],Math.max(0,v[1]-p.y1));
            double c=Math.max(7-v[2],Math.max(0,v[2]-9)),d=a*a+b*b+c*c;
            if(d<distance){distance=d;best=i;}
        }
        return best;
    }
    private static ProjectingWindowHinge nearestWall(Level level,BlockPos origin,BlockState state) {
        Direction right=state.getValue(FACING).getClockWise();
        Direction[] directions={Direction.UP,Direction.DOWN,right.getOpposite(),right};
        ProjectingWindowHinge best=ProjectingWindowHinge.TOP;double nearest=Double.POSITIVE_INFINITY;
        int ox=Math.floorMod(u(origin,right),3)==1?8:0,oy=Math.floorMod(origin.getY(),3)==1?8:0;
        for(int edge=0;edge<directions.length;edge++)for(int line=0;line<2;line++) {
            Direction direction=directions[edge];boolean vertical=edge<2;
            BlockPos start=vertical?origin.relative(right,line):origin.above(line);
            for(int step=1;step<=SCAN_LIMIT;step++) {
                BlockPos target=start.relative(direction,step);
                if(!level.hasChunkAt(target)||level.isOutsideBuildHeight(target))break;
                BlockState neighbor=level.getBlockState(target);
                if(same(neighbor,state))continue;
                if(neighbor.isFaceSturdy(level,target,direction.getOpposite())) {
                    int offset=vertical?oy:ox;
                    double distance=edge==0||edge==3?step*16-offset-24:(step-1)*16+offset;
                    if(distance<nearest){nearest=distance;best=ProjectingWindowHinge.values()[edge];}
                }
                break;
            }
        }
        return best;
    }
    private static void refresh(Level level,BlockPos origin,Direction facing) {
        Direction right=facing.getClockWise();
        for(int y=-1;y<=2;y++)for(int x=-1;x<=2;x++) {
            BlockPos target=origin.relative(right,x).above(y);
            if(level.hasChunkAt(target)) {
                BlockState value=level.getBlockState(target);
                if(value.getBlock() instanceof SchoolProjectingWindowBlock)level.sendBlockUpdated(target,value,value,Block.UPDATE_CLIENTS);
            }
        }
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!player.mayBuild())return InteractionResult.PASS;
        Direction facing=state.getValue(FACING),right=facing.getClockWise();
        Part part=parts(phase(pos,facing)).get(clickedPart(state,level,pos,hit));BlockPos origin=controller(pos,right,part);
        if(!complete(level,origin,state))return InteractionResult.PASS;
        for(int y=0;y<2;y++)for(int x=0;x<2;x++) {
            BlockPos target=origin.relative(right,x).above(y);
            if(!level.mayInteract(player,target)||!player.mayUseItemAt(target,hit.getDirection(),player.getMainHandItem()))return InteractionResult.PASS;
        }
        if(level.isClientSide())return InteractionResult.SUCCESS;
        BlockState value=level.getBlockState(origin);boolean open=!value.getValue(OPEN);
        ProjectingWindowHinge hinge=open?nearestWall(level,origin,state):value.getValue(HINGE);
        level.setBlock(origin,value.setValue(OPEN,open).setValue(HINGE,hinge),Block.UPDATE_ALL);
        refresh(level,origin,facing);
        level.playSound(null,pos,open?SoundEvents.IRON_DOOR_OPEN:SoundEvents.IRON_DOOR_CLOSE,SoundSource.BLOCKS,.4F,1.2F);
        return InteractionResult.SUCCESS;
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moved) {
        super.onPlace(state,level,pos,old,moved);
        if(!level.isClientSide()&&!old.is(this))refresh(level,pos,state.getValue(FACING));
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        Direction facing=state.getValue(FACING),right=facing.getClockWise();
        for(Part part:parts(phase(pos,facing))) {
            BlockPos origin=controller(pos,right,part);BlockState value=read(level,origin);
            if(value.is(this)&&value.getValue(OPEN))level.setBlock(origin,value.setValue(OPEN,false),Block.UPDATE_ALL);
        }
        refresh(level,pos,facing);
        super.affectNeighborsAfterRemoval(state,level,pos,moved);
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {
        Direction facing=state.getValue(FACING),reflected=mirror.mirror(facing);ProjectingWindowHinge hinge=state.getValue(HINGE);
        if(mirror.mirror(facing.getClockWise())!=reflected.getClockWise()) {
            if(hinge==ProjectingWindowHinge.LEFT)hinge=ProjectingWindowHinge.RIGHT;
            else if(hinge==ProjectingWindowHinge.RIGHT)hinge=ProjectingWindowHinge.LEFT;
        }
        return state.setValue(FACING,reflected).setValue(HINGE,hinge);
    }
}
