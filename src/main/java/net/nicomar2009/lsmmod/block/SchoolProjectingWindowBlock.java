package net.nicomar2009.lsmmod.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Thin 24px sashes with a placement-anchored grid and outward opening at the far block edge. */
public class SchoolProjectingWindowBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN=BlockStateProperties.OPEN;
    public static final EnumProperty<ProjectingWindowHinge> HINGE=EnumProperty.create("hinge",ProjectingWindowHinge.class);
    public static final IntegerProperty GRID_PHASE=IntegerProperty.create("grid_phase",0,9);
    public static final BooleanProperty WALL_LEFT=BooleanProperty.create("wall_left");
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
    // Geometry keys include the window profile, grid phase, poses and orientation, never paint.
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
        BlockState initial=stateDefinition.any().setValue(FACING,Direction.NORTH)
                .setValue(OPEN,false).setValue(HINGE,ProjectingWindowHinge.TOP);
        if(initial.hasProperty(GRID_PHASE))initial=initial.setValue(GRID_PHASE,9);
        registerDefaultState(initial);
        if(defaultBlockState().hasProperty(WALL_LEFT))registerDefaultState(defaultBlockState().setValue(WALL_LEFT,true));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,OPEN,HINGE,GRID_PHASE);}
    public static int phase(BlockState state) {return state.hasProperty(GRID_PHASE)&&state.getValue(GRID_PHASE)<9?state.getValue(GRID_PHASE):0;}
    public static boolean halfWindow(BlockState state) {return state.hasProperty(WALL_LEFT);}
    public static List<Part> parts(int phase) {return PARTS.get(phase);}
    public static int glassLowY(BlockState state) {
        if(state.hasProperty(SchoolStairProjectingWindowBlock.HALF))
            return state.getValue(SchoolStairProjectingWindowBlock.HALF)==net.minecraft.world.level.block.state.properties.Half.BOTTOM?8:0;
        return state.hasProperty(SchoolSlabProjectingWindowBlock.TYPE)
                &&state.getValue(SchoolSlabProjectingWindowBlock.TYPE)==net.minecraft.world.level.block.state.properties.SlabType.BOTTOM?8:0;
    }
    public static int glassHighY(BlockState state) {
        if(state.hasProperty(SchoolStairProjectingWindowBlock.HALF))
            return state.getValue(SchoolStairProjectingWindowBlock.HALF)==net.minecraft.world.level.block.state.properties.Half.TOP?8:16;
        if(!state.hasProperty(SchoolSlabProjectingWindowBlock.TYPE))return 16;
        return switch(state.getValue(SchoolSlabProjectingWindowBlock.TYPE)){case TOP->8;case DOUBLE->0;default->16;};
    }
    private static VoxelShape slabShape(BlockState state) {
        if(state.hasProperty(SchoolStairProjectingWindowBlock.HALF))
            return state.getValue(SchoolStairProjectingWindowBlock.HALF)==net.minecraft.world.level.block.state.properties.Half.BOTTOM
                    ?Block.box(0,0,0,16,8,16):Block.box(0,8,0,16,16,16);
        if(!state.hasProperty(SchoolSlabProjectingWindowBlock.TYPE))return Shapes.empty();
        return switch(state.getValue(SchoolSlabProjectingWindowBlock.TYPE)) {
            case BOTTOM->Block.box(0,0,0,16,8,16);case TOP->Block.box(0,8,0,16,16,16);case DOUBLE->Shapes.block();
        };
    }
    private static Part verticalClip(Part part,int low,int high) {
        return new Part(part.x0,part.x1,Math.max(low,part.y0),Math.min(high,part.y1),part.ox,part.oy);
    }
    private static List<Part> parts(BlockState state) {
        int profile=halfWindow(state)?state.getValue(WALL_LEFT)?1:2:0;
        return parts(phase(state)).stream().map(part->verticalClip(clip(part,profile),glassLowY(state),glassHighY(state))).toList();
    }
    private static Part clip(Part part,int profile) {
        int low=profile==1?8:0,high=profile==2?8:16;
        return new Part(Math.max(low,part.x0),Math.min(high,part.x1),part.y0,part.y1,part.ox,part.oy);
    }
    private static boolean visible(Part part) {return part.x0<part.x1&&part.y0<part.y1;}
    private static boolean touches(BlockState a,BlockState b,Direction direction) {
        if(!same(a,b))return false;
        int al=halfWindow(a)&&a.getValue(WALL_LEFT)?8:0,ah=halfWindow(a)&&!a.getValue(WALL_LEFT)?8:16;
        int bl=halfWindow(b)&&b.getValue(WALL_LEFT)?8:0,bh=halfWindow(b)&&!b.getValue(WALL_LEFT)?8:16;
        if(direction.getAxis()==Direction.Axis.Y)return Math.max(al,bl)<Math.min(ah,bh)
                &&(direction==Direction.UP?glassHighY(a)==16&&glassLowY(b)==0:glassLowY(a)==0&&glassHighY(b)==16);
        if(Math.max(glassLowY(a),glassLowY(b))>=Math.min(glassHighY(a),glassHighY(b)))return false;
        Direction right=a.getValue(FACING).getClockWise();
        return direction==right?ah==16&&bl==0:direction==right.getOpposite()&&al==0&&bh==16;
    }
    private static BlockPos controller(BlockPos pos,Direction right,Part part) {
        return pos.relative(right,Math.floorDiv(part.ox,16)).above(Math.floorDiv(part.oy,16));
    }
    private static BlockState read(BlockGetter level,BlockPos pos) {
        if(level==null||(level instanceof LevelReader reader&&!reader.hasChunkAt(pos)))return Blocks.AIR.defaultBlockState();
        return level.getBlockState(pos);
    }
    private static boolean same(BlockState value,BlockState state) {
        // Placement can supply an ordinary wall as either argument; validate both before reading properties.
        return value!=null&&state!=null
                &&value.getBlock() instanceof SchoolProjectingWindowBlock
                &&state.getBlock() instanceof SchoolProjectingWindowBlock
                &&value.hasProperty(FACING)&&state.hasProperty(FACING)
                &&value.getValue(FACING)==state.getValue(FACING)&&glassLowY(value)<glassHighY(value)&&glassLowY(state)<glassHighY(state);
    }
    private static boolean complete(BlockGetter level,BlockPos origin,BlockState state) {
        Direction right=state.getValue(FACING).getClockWise();BlockState base=read(level,origin);
        if(!same(base,state))return false;
        int basePhase=phase(base),ox=basePhase%3==1?8:0,oy=basePhase/3==1?8:0;
        for(int y=0;y<2;y++)for(int x=0;x<2;x++) {
            BlockState value=read(level,origin.relative(right,x).above(y));
            int expected=Math.floorMod(basePhase%3+x,3)+3*Math.floorMod(basePhase/3+y,3);
            if(!same(value,state)||phase(value)!=expected)return false;
            int low=halfWindow(value)&&value.getValue(WALL_LEFT)?8:0;
            int high=halfWindow(value)&&!value.getValue(WALL_LEFT)?8:16;
            if(low>Math.max(0,ox-x*16)||high<Math.min(16,ox+24-x*16)
                    ||glassLowY(value)>Math.max(0,oy-y*16)||glassHighY(value)<Math.min(16,oy+24-y*16))return false;
        }
        return true;
    }
    /** All open sashes use the top hinge, including old saves with a different stored HINGE. */
    public static int pose(BlockGetter level,BlockPos pos,BlockState state,int index) {
        if(state==null||!(state.getBlock() instanceof SchoolProjectingWindowBlock)||pos==null)return 0;
        List<Part> parts=parts(state);
        if(index<0||index>=parts.size()||!visible(parts.get(index))||!same(read(level,pos),state))return 0;
        BlockPos origin=controller(pos,state.getValue(FACING).getClockWise(),parts.get(index));
        BlockState value=read(level,origin);
        return same(value,state)&&complete(level,origin,state)&&value.getValue(OPEN)?1:0;
    }
    private static double[] pivot(Part part,int pose) {
        return new double[]{part.ox+(pose==4?24:0),part.oy+(pose==1?24:0),16};
    }
    /** Rigid rotation preserves the two-pixel thickness; positive Z faces away from the viewer. */
    public static double[] transform(Part part,int pose,double x,double y,double z,boolean inverse,boolean half) {
        if(pose==0)return new double[]{x,y,z};
        double[] origin=pivot(part,pose);
        double angle=(pose==1||pose==3?-1:1)*Math.PI/4*(inverse?-1:1),c=Math.cos(angle),s=Math.sin(angle);
        x-=origin[0];y-=origin[1];z-=origin[2];
        if(pose<=2)return new double[]{x+origin[0],c*y-s*z+origin[1],s*y+c*z+origin[2]};
        return new double[]{c*x+s*z+origin[0],y+origin[1],-s*x+c*z+origin[2]};
    }
    private static VoxelShape fragment(int phase,int index,int pose,int facing,int profile,int lowY,int highY) {
        int key=(((((profile*3+lowY/8)*3+highY/8)*9+phase)*4+index)*5+pose)*4+facing;
        return FRAGMENTS.computeIfAbsent(key,ignored->{
            Part p=verticalClip(clip(parts(phase).get(index),profile),lowY,highY);
            if(!visible(p))return Shapes.empty();
            List<AABB> boxes=new ArrayList<>();
            if(pose==0)boxes.add(ExactVoxelShapes.box(p.x0,p.y0,14,p.x1,p.y1,16));
            else {
                // Quarter-pixel strips conservatively approximate the rotated physical pane.
                boolean vertical=pose<=2;double first=vertical?p.y0:p.x0,last=vertical?p.y1:p.x1;
                for(double start=first;start<last;start+=.25) {
                    double[] low={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};
                    double[] high={Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                    for(double x:new double[]{vertical?p.x0:start,vertical?p.x1:Math.min(last,start+.25)})
                        for(double y:new double[]{vertical?start:p.y0,vertical?Math.min(last,start+.25):p.y1})
                            for(double z:new double[]{14,16}) {
                                double[] v=transform(p,pose,x,y,z,false,profile!=0);
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
    private record LayoutGeometryKey(String key,int profile,Direction facing,List<Integer> poses) {}
    private static final Map<LayoutGeometryKey,Geometry> LAYOUT_GEOMETRY=new ConcurrentHashMap<>();
    private static double[] transform(SchoolProjectingWindowLayout.Part part,double x,double y,double z,boolean open,boolean inverse) {
        if(!open)return new double[]{x,y,z};
        double py=part.oy()+part.height(),angle=-Math.PI/4*(inverse?-1:1),c=Math.cos(angle),sin=Math.sin(angle);
        y-=py;z-=16;
        return new double[]{x,c*y-sin*z+py,sin*y+c*z+16};
    }
    private Geometry layoutGeometry(BlockState state,BlockGetter level,SchoolProjectingWindowLayout.View view) {
        Direction facing=state.getValue(FACING);int profile=halfWindow(state)?state.getValue(WALL_LEFT)?1:2:0;
        List<SchoolProjectingWindowLayout.Part> parts=view.parts(state);
        List<Integer> poses=parts.stream().map(part->view.pose(level,facing,part)).toList();
        return LAYOUT_GEOMETRY.computeIfAbsent(new LayoutGeometryKey(view.key(),profile*9+glassLowY(state)/8*3+glassHighY(state)/8,facing,poses),ignored->{
            VoxelShape collision=Shapes.empty(),outline=Shapes.empty();
            for(int i=0;i<parts.size();i++) {
                var part=parts.get(i);List<AABB> boxes=new ArrayList<>();boolean open=poses.get(i)==1;
                if(!open)boxes.add(ExactVoxelShapes.box(part.x0(),part.y0(),14,part.x1(),part.y1(),16));
                else for(double start=part.y0();start<part.y1();start+=.25) {
                    double[] low={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};
                    double[] high={Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                    for(double x:new double[]{part.x0(),part.x1()})for(double y:new double[]{start,Math.min(part.y1(),start+.25)})for(double z:new double[]{14,16}) {
                        double[] point=transform(part,x,y,z,true,false);
                        for(int axis=0;axis<3;axis++){low[axis]=Math.min(low[axis],point[axis]);high[axis]=Math.max(high[axis],point[axis]);}
                    }
                    boxes.add(ExactVoxelShapes.box(low[0],low[1],low[2],high[0],high[1],high[2]));
                }
                VoxelShape shape=Shapes.rotateHorizontal(ExactVoxelShapes.union(boxes)).get(facing);
                collision=Shapes.or(collision,shape);outline=Shapes.or(outline,SimpleBlockOutline.boundingBox(shape));
            }
            if(profile!=0) {
                VoxelShape wall=Shapes.rotateHorizontal(Block.box(profile==1?0:8,0,0,profile==1?8:16,16,16)).get(facing);
                collision=Shapes.or(collision,wall);outline=Shapes.or(outline,wall);
            }
            return new Geometry(Shapes.or(collision,slabShape(state)),Shapes.or(outline,slabShape(state)));
        });
    }
    private Geometry geometry(BlockState state,BlockGetter level,BlockPos pos) {
        var layout=SchoolProjectingWindowLayout.find(level,pos,state);
        if(layout!=null)return layoutGeometry(state,level,layout);
        int phase=phase(state),facing=switch(state.getValue(FACING)){case EAST->1;case SOUTH->2;case WEST->3;default->0;};
        int profile=halfWindow(state)?state.getValue(WALL_LEFT)?1:2:0;
        List<Part> parts=parts(state);int code=0;int[] poses=new int[parts.size()];
        for(int i=0;i<parts.size();i++){poses[i]=pose(level,pos,state,i);code=code*5+poses[i];}
        int key=(((profile*9+phase)*625+code)*4+facing)*9+glassLowY(state)/8*3+glassHighY(state)/8;
        return GEOMETRY.computeIfAbsent(key,ignored->{
            VoxelShape collision=Shapes.empty(),outline=Shapes.empty();
            for(int i=0;i<parts.size();i++) {
                VoxelShape part=fragment(phase,i,poses[i],facing,profile,glassLowY(state),glassHighY(state));
                collision=Shapes.or(collision,part);outline=Shapes.or(outline,SimpleBlockOutline.boundingBox(part));
            }
            if(profile!=0) {
                VoxelShape wall=Block.box(profile==1?0:8,0,0,profile==1?8:16,16,16);
                Map<Direction,VoxelShape> walls=Shapes.rotateHorizontal(wall);
                Direction direction=switch(facing){case 1->Direction.EAST;case 2->Direction.SOUTH;case 3->Direction.WEST;default->Direction.NORTH;};
                collision=Shapes.or(collision,walls.get(direction));outline=Shapes.or(outline,walls.get(direction));
            }
            return new Geometry(Shapes.or(collision,slabShape(state)),Shapes.or(outline,slabShape(state)));
        });
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return geometry(state,level,pos).collision;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return geometry(state,level,pos).outline;}
    protected BlockState placementState(BlockPlaceContext context) {return defaultBlockState();}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction side=context.getClickedFace(),facing=context.getHorizontalDirection().getOpposite(),right=facing.getClockWise();
        BlockState placed=placementState(context).setValue(FACING,facing);
        if(halfWindow(placed)) {
            double x=context.getClickLocation().x-context.getClickedPos().getX()-.5;
            double z=context.getClickLocation().z-context.getClickedPos().getZ()-.5;
            placed=placed.setValue(WALL_LEFT,x*right.getStepX()+z*right.getStepZ()<=0);
        }
        placed=placed.setValue(GRID_PHASE,(halfWindow(placed)?1:0)+(glassLowY(placed)==8?3:0));
        Direction[] order={side.getOpposite(),Direction.DOWN,right.getOpposite(),Direction.UP,right};
        for(Direction direction:order) {
            BlockPos target=context.getClickedPos().relative(direction);
            if(!context.getLevel().hasChunkAt(target))continue;
            BlockState neighbor=context.getLevel().getBlockState(target);
            if(!touches(placed,neighbor,direction))continue;
            if(neighbor.getValue(GRID_PHASE)==9&&!context.getLevel().isClientSide()) {
                migrateGrid(context.getLevel(),target,neighbor);
                neighbor=context.getLevel().getBlockState(target);
            }
            int offset=-direction.getStepX()*right.getStepX()-direction.getStepZ()*right.getStepZ();
            int value=phase(neighbor);
            return placed.setValue(GRID_PHASE,Math.floorMod(value%3+offset,3)+3*Math.floorMod(value/3-direction.getStepY(),3));
        }
        return placed;
    }
    /** Value 9 marks old saves without a grid origin; retain OPEN/HINGE while anchoring loaded cells. */
    private static void migrateGrid(Level level,BlockPos start,BlockState state) {
        if(state.getValue(GRID_PHASE)!=9||level.isClientSide())return;
        Direction right=state.getValue(FACING).getClockWise();
        Direction[] directions={Direction.DOWN,right.getOpposite(),Direction.UP,right};
        var visited=new HashSet<BlockPos>();var queue=new ArrayDeque<BlockPos>();var cells=new ArrayList<BlockPos>();
        BlockPos anchor=null;int anchorPhase=0,minU=Integer.MAX_VALUE,minY=Integer.MAX_VALUE;
        queue.add(start);visited.add(start);
        while(!queue.isEmpty()) {
            if(cells.size()>=4096)return;
            BlockPos pos=queue.removeFirst();cells.add(pos);
            minU=Math.min(minU,pos.getX()*right.getStepX()+pos.getZ()*right.getStepZ());minY=Math.min(minY,pos.getY());
            for(Direction direction:directions) {
                BlockPos target=pos.relative(direction);
                if(!level.hasChunkAt(target))continue;
                BlockState neighbor=level.getBlockState(target);
                if(!touches(level.getBlockState(pos),neighbor,direction))continue;
                if(neighbor.getValue(GRID_PHASE)<9) {
                    if(anchor==null){anchor=target;anchorPhase=phase(neighbor);}
                } else if(visited.add(target))queue.addLast(target);
            }
        }
        if(anchor==null) {
            for(BlockPos pos:cells)if(pos.getY()==minY&&pos.getX()*right.getStepX()+pos.getZ()*right.getStepZ()==minU) {
                if(halfWindow(level.getBlockState(pos)))anchorPhase=1;
                if(glassLowY(level.getBlockState(pos))==8)anchorPhase+=3;
                break;
            }
        }
        int anchorU=anchor==null?minU:anchor.getX()*right.getStepX()+anchor.getZ()*right.getStepZ();
        int anchorY=anchor==null?minY:anchor.getY();
        for(BlockPos pos:cells) {
            int u=pos.getX()*right.getStepX()+pos.getZ()*right.getStepZ();
            int value=Math.floorMod(anchorPhase%3+u-anchorU,3)+3*Math.floorMod(anchorPhase/3+pos.getY()-anchorY,3);
            BlockState old=level.getBlockState(pos);
            level.setBlock(pos,old.setValue(GRID_PHASE,value),Block.UPDATE_CLIENTS);
        }
    }
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {migrateGrid(level,pos,state);}
    private static int clickedPart(BlockState state,Level level,BlockPos pos,BlockHitResult hit) {
        Direction right=state.getValue(FACING).getClockWise(),back=state.getValue(FACING).getOpposite();
        double dx=hit.getLocation().x-pos.getX()-.5,dz=hit.getLocation().z-pos.getZ()-.5;
        double x=8+16*(dx*right.getStepX()+dz*right.getStepZ()),y=16*(hit.getLocation().y-pos.getY());
        double z=8+16*(dx*back.getStepX()+dz*back.getStepZ());
        List<Part> parts=parts(state);int best=0;double distance=Double.POSITIVE_INFINITY;
        for(int i=0;i<parts.size();i++) {
            Part p=parts.get(i);if(!visible(p))continue;double[] v=transform(p,pose(level,pos,state,i),x,y,z,true,halfWindow(state));
            double a=Math.max(p.x0-v[0],Math.max(0,v[0]-p.x1)),b=Math.max(p.y0-v[1],Math.max(0,v[1]-p.y1));
            double c=Math.max(14-v[2],Math.max(0,v[2]-16)),d=a*a+b*b+c*c;
            if(d<distance){distance=d;best=i;}
        }
        return best;
    }
    protected static void refresh(Level level,BlockPos origin,Direction facing) {
        Direction right=facing.getClockWise();
        int width=SchoolProjectingWindowLayout.RULES.stream().mapToInt(rule->rule.cells(8)).max().orElse(2);
        int height=SchoolProjectingWindowLayout.RULES.stream().mapToInt(rule->(rule.height()+23)/16).max().orElse(2);
        for(int y=-height;y<=height;y++)for(int x=-width;x<=width;x++) {
            BlockPos target=origin.relative(right,x).above(y);
            if(level.hasChunkAt(target)) {
                BlockState value=level.getBlockState(target);
                if(value.getBlock() instanceof SchoolProjectingWindowBlock)level.sendBlockUpdated(target,value,value,Block.UPDATE_CLIENTS);
            }
        }
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(!player.mayBuild())return InteractionResult.PASS;
        if(level.getBlockEntity(pos) instanceof SchoolProjectingWindowPaintEntity wall
                &&wall.canPaintAt(hit.getDirection(),hit.getLocation()))return InteractionResult.PASS;
        if(level.getBlockEntity(pos) instanceof SchoolSlabProjectingWindowPaintEntity slab
                &&slab.canPaintAt(hit.getDirection(),hit.getLocation()))return InteractionResult.PASS;
        if(level.getBlockEntity(pos) instanceof SchoolStairProjectingWindowPaintEntity stair
                &&stair.canPaintAt(hit.getDirection(),hit.getLocation()))return InteractionResult.PASS;
        if(glassLowY(state)>=glassHighY(state))return InteractionResult.PASS;
        var layout=SchoolProjectingWindowLayout.find(level,pos,state);
        if(layout!=null)return useLayout(state,level,pos,player,hit,layout);
        if(state.getValue(GRID_PHASE)==9) {
            if(level.isClientSide())return InteractionResult.SUCCESS;
            migrateGrid(level,pos,state);state=level.getBlockState(pos);
            if(state.getValue(GRID_PHASE)==9)return InteractionResult.PASS;
        }
        Direction facing=state.getValue(FACING),right=facing.getClockWise();
        Part part=parts(state).get(clickedPart(state,level,pos,hit));BlockPos origin=controller(pos,right,part);
        if(!complete(level,origin,state))return InteractionResult.PASS;
        for(int y=0;y<2;y++)for(int x=0;x<2;x++) {
            BlockPos target=origin.relative(right,x).above(y);
            if(!level.mayInteract(player,target)||!player.mayUseItemAt(target,hit.getDirection(),player.getMainHandItem()))return InteractionResult.PASS;
        }
        if(level.isClientSide())return InteractionResult.SUCCESS;
        BlockState value=level.getBlockState(origin);boolean open=!value.getValue(OPEN);
        ProjectingWindowHinge hinge=ProjectingWindowHinge.TOP;
        level.setBlock(origin,value.setValue(OPEN,open).setValue(HINGE,hinge),Block.UPDATE_ALL);
        refresh(level,origin,facing);
        level.playSound(null,pos,open?SoundEvents.IRON_DOOR_OPEN:SoundEvents.IRON_DOOR_CLOSE,SoundSource.BLOCKS,.4F,1.2F);
        return InteractionResult.SUCCESS;
    }
    private InteractionResult useLayout(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit,SchoolProjectingWindowLayout.View view) {
        Direction facing=state.getValue(FACING),right=facing.getClockWise(),back=facing.getOpposite();
        double dx=hit.getLocation().x-pos.getX()-.5,dz=hit.getLocation().z-pos.getZ()-.5;
        double x=8+16*(dx*right.getStepX()+dz*right.getStepZ()),y=16*(hit.getLocation().y-pos.getY());
        double z=8+16*(dx*back.getStepX()+dz*back.getStepZ());
        SchoolProjectingWindowLayout.Part chosen=null;double distance=Double.POSITIVE_INFINITY;
        for(var part:view.parts(state)) {
            double[] local=transform(part,x,y,z,view.pose(level,facing,part)==1,true);
            double a=Math.max(part.x0()-local[0],Math.max(0,local[0]-part.x1()));
            double b=Math.max(part.y0()-local[1],Math.max(0,local[1]-part.y1()));
            double c=Math.max(14-local[2],Math.max(0,local[2]-16)),d=a*a+b*b+c*c;
            if(d<distance){distance=d;chosen=part;}
        }
        if(chosen==null)return InteractionResult.PASS;
        double paneX=view.x()*16+chosen.ox(),paneY=view.y()*16+chosen.oy();
        int minX=(int)Math.floor(paneX/16),maxX=(int)Math.ceil((paneX+chosen.width())/16)-1;
        int minY=(int)Math.floor(paneY/16),maxY=(int)Math.ceil((paneY+chosen.height())/16)-1;
        for(int cy=minY;cy<=maxY;cy++)for(int cx=minX;cx<=maxX;cx++) {
            BlockPos target=view.origin().relative(right,cx).above(cy);
            if(!level.mayInteract(player,target)||!player.mayUseItemAt(target,hit.getDirection(),player.getMainHandItem()))return InteractionResult.PASS;
        }
        if(level.isClientSide())return InteractionResult.SUCCESS;
        BlockPos origin=view.controller(right,chosen);BlockState value=level.getBlockState(origin);boolean open=!value.getValue(OPEN);
        level.setBlock(origin,value.setValue(OPEN,open).setValue(HINGE,ProjectingWindowHinge.TOP),Block.UPDATE_ALL);
        refresh(level,origin,facing);
        level.playSound(null,pos,open?SoundEvents.IRON_DOOR_OPEN:SoundEvents.IRON_DOOR_CLOSE,SoundSource.BLOCKS,.4F,1.2F);
        return InteractionResult.SUCCESS;
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moved) {
        super.onPlace(state,level,pos,old,moved);
        if(!level.isClientSide()&&!old.is(this))refresh(level,pos,state.getValue(FACING));
        if(!level.isClientSide()&&state.getValue(GRID_PHASE)==9)level.scheduleTick(pos,this,1);
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel level,BlockPos pos,boolean moved) {
        Direction facing=state.getValue(FACING),right=facing.getClockWise();
        for(Part part:parts(state)) {
            if(!visible(part))continue;
            BlockPos origin=controller(pos,right,part);BlockState value=read(level,origin);
            if(same(value,state)&&value.getValue(OPEN))level.setBlock(origin,value.setValue(OPEN,false),Block.UPDATE_ALL);
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
        BlockState result=state.setValue(FACING,reflected).setValue(HINGE,hinge);
        if(halfWindow(result)&&mirror.mirror(facing.getClockWise())!=reflected.getClockWise())result=result.setValue(WALL_LEFT,!state.getValue(WALL_LEFT));
        return result;
    }
}
