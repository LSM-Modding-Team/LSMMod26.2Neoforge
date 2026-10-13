package net.nicomar2009.lsmmod.block;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Six 4x4 face palettes and 64 occupancy bits; only the original fixed frame is editable. */
public class SchoolDoorFramePaintEntity extends SchoolPaintableBlockEntity {
    private final int[] squares=new int[96];
    private long removed;
    private BlockState collisionState;
    private long collisionMask;
    private net.minecraft.world.phys.shapes.VoxelShape collision;
    public SchoolDoorFramePaintEntity(BlockPos pos,BlockState state) {
        this(ModBlockEntities.SCHOOL_DOOR_FRAME_PAINT.get(),pos,state);
    }
    protected SchoolDoorFramePaintEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type,BlockPos pos,BlockState state) {super(type,pos,state);}
    public int miniSize() {return 8;}
    /** Keep fine storage for old paint and partial cuts, while editing four 8px squares per face. */
    public int[] squareGroup(int index) {
        if(miniSize()==4)return new int[]{index};
        int face=index/16,q=index%16,start=face*16+(q%4/2)*2+(q/4/2)*8;
        return new int[]{start,start+1,start+4,start+5};
    }
    protected long removedMask() {return removed;}
    public long occupied() {return SchoolPaintableBathroomDoorBlock.initialFrame(getBlockState())&~removed;}
    public net.minecraft.world.phys.shapes.VoxelShape collision(BlockState state,java.util.function.Supplier<net.minecraft.world.phys.shapes.VoxelShape> build) {
        long mask=occupied();
        if(collision==null||collisionState!=state||collisionMask!=mask) {collision=build.get();collisionState=state;collisionMask=mask;}
        return collision;
    }
    public boolean hasCube(int cube) {return cube>=0&&cube<64&&(occupied()&(1L<<cube))!=0;}
    public static int quadrant(Direction face,int x,int y,int z) {
        return switch(face.getAxis()) {case X->z+4*y;case Y->x+4*z;case Z->x+4*y;};
    }
    public SchoolPaint squarePaint(Direction localFace,int quadrant) {return SchoolPaint.fromId(squares[localFace.ordinal()*16+quadrant]);}
    private int turns() {
        if(!getBlockState().hasProperty(SchoolFramedDiningDoorBlock.FACING))return 0;
        return switch(getBlockState().getValue(SchoolFramedDiningDoorBlock.FACING)) {case EAST->1;case SOUTH->2;case WEST->3;default->0;};
    }
    public Direction worldFace(Direction face) {
        if(face.getAxis().isHorizontal())for(int i=0;i<turns();i++)face=face.getClockWise();return face;
    }
    private Direction localFace(Direction face) {
        if(face.getAxis().isHorizontal())for(int i=0;i<turns();i++)face=face.getCounterClockWise();return face;
    }
    private int hitCube(Direction face,Vec3 hit) {
        double x=hit.x-worldPosition.getX()-face.getStepX()*1e-6;
        double y=hit.y-worldPosition.getY()-face.getStepY()*1e-6;
        double z=hit.z-worldPosition.getZ()-face.getStepZ()*1e-6;
        for(int i=0;i<turns();i++) {double previous=x;x=z;z=1-previous;}
        if(x<0||x>=1||y<0||y>=1||z<0||z>=1)return -1;
        return (int)(x*4)+4*(int)(y*4)+16*(int)(z*4);
    }
    private int square(Direction face,Vec3 hit) {
        int cube=hitCube(face,hit);if(cube<0)return -1;
        Direction local=localFace(face);
        return local.ordinal()*16+quadrant(local,cube&3,(cube>>2)&3,(cube>>4)&3);
    }
    public int exposedSquare(Direction face,Vec3 hit,net.minecraft.world.level.BlockGetter view) {
        int cube=hitCube(face,hit);
        return exposed(cube,localFace(face),view)?square(face,hit):-1;
    }
    @Override public boolean canPaintAt(Direction face,Vec3 hit) {return hasCube(hitCube(face,hit));}
    @Override public SchoolPaint paintAt(Direction face,Vec3 hit) {
        int index=square(face,hit);return index<0?SchoolPaint.UNPAINTED:SchoolPaint.fromId(squares[index]);
    }
    @Override public SchoolPaint paint(Direction face) {return squarePaint(localFace(face),0);}
    @Override public void paintAt(Direction face,Vec3 hit,SchoolPaint color) {
        if(!canPaintAt(face,hit))return;int index=square(face,hit);
        int[] copy=snapshot();for(int square:squareGroup(index))copy[square]=color.ordinal();apply(copy);
    }
    @Override public void paintFace(Direction face,SchoolPaint color) {
        int[] copy=snapshot();int start=localFace(face).ordinal()*16;
        Arrays.fill(copy,start,start+16,color.ordinal());apply(copy);
    }
    @Override public int[] snapshot() {return squares.clone();}
    @Override public boolean painted() {for(int color:squares)if(color!=0)return true;return false;}
    @Override public void clean() {apply(new int[96]);}
    @Override public void apply(int[] colors) {
        if(colors.length!=6&&colors.length!=24&&colors.length!=96)throw new IllegalArgumentException("Expected six faces, 24 or 96 squares");
        int[] copy=new int[96];
        for(int face=0;face<6;face++)for(int q=0;q<16;q++) {
            int index=colors.length==6?face:colors.length==24?face*4+(q%4)/2+2*(q/4/2):face*16+q;
            copy[face*16+q]=SchoolPaint.fromId(colors[index]).ordinal();
        }
        if(!Arrays.equals(copy,squares)) {System.arraycopy(copy,0,squares,0,96);changed();}
    }
    public boolean removeCube(Direction face,Vec3 hit) {
        int cube=hitCube(face,hit);if(!hasCube(cube))return false;
        if(miniSize()==4)removed|=1L<<cube;
        else {
            int x=(cube&3)/2*2,y=((cube>>2)&3)/2*2,z=((cube>>4)&3)/2*2;
            for(int dx=0;dx<2;dx++)for(int dy=0;dy<2;dy++)for(int dz=0;dz<2;dz++)removed|=1L<<(x+dx+4*(y+dy)+16*(z+dz));
        }
        changed();
        // Keep the controller/leaf even when the last masonry cube has been removed.
        if(level!=null) {
            level.updateNeighborsAt(worldPosition,getBlockState().getBlock());
            for(Direction direction:Direction.values()) {
                BlockPos neighbor=worldPosition.relative(direction);
                if(level.hasChunkAt(neighbor)&&level.getBlockEntity(neighbor) instanceof SchoolDoorFramePaintEntity) {
                    BlockState state=level.getBlockState(neighbor);
                    level.sendBlockUpdated(neighbor,state,state,net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
                }
            }
        }
        return true;
    }
    /** Hide internal faces, including seams with other cells of this same door. */
    public boolean exposed(int cube,Direction face,net.minecraft.world.level.BlockGetter view) {
        if(!hasCube(cube))return false;
        int x=(cube&3)+face.getStepX(),y=((cube>>2)&3)+face.getStepY(),z=((cube>>4)&3)+face.getStepZ();
        if(x>=0&&x<4&&y>=0&&y<4&&z>=0&&z<4)return !hasCube(x+4*y+16*z);
        BlockPos neighbor=worldPosition.relative(worldFace(face));BlockState other=view.getBlockState(neighbor),state=getBlockState();
        int column=state.getValue(SchoolFramedDiningDoorBlock.COLUMN)+face.getStepX();
        int row=state.getValue(SchoolFramedDiningDoorBlock.ROW)+face.getStepY();
        int depth=state.getValue(SchoolFramedDiningDoorBlock.DEPTH)+face.getStepZ();
        if(other.is(state.getBlock())&&other.getValue(SchoolFramedDiningDoorBlock.FACING)==state.getValue(SchoolFramedDiningDoorBlock.FACING)
                &&other.getValue(SchoolFramedDiningDoorBlock.HINGE)==state.getValue(SchoolFramedDiningDoorBlock.HINGE)
                &&other.getValue(SchoolFramedDiningDoorBlock.COLUMN)==column&&other.getValue(SchoolFramedDiningDoorBlock.ROW)==row
                &&other.getValue(SchoolFramedDiningDoorBlock.DEPTH)==depth&&view.getBlockEntity(neighbor) instanceof SchoolDoorFramePaintEntity entity)
            return !entity.hasCube(Math.floorMod(x,4)+4*Math.floorMod(y,4)+16*Math.floorMod(z,4));
        return true;
    }
    @Override protected void saveAdditional(ValueOutput output) {
        output.putLong("removed",removed);
        for(int part=0;part<6;part++) {
            long packed=0;for(int q=0;q<16;q++)packed|=(long)squares[part*16+q]<<(q*4);
            output.putLong("paints_"+part,packed);
        }
    }
    @Override protected void loadAdditional(ValueInput input) {
        removed=input.getLongOr("removed",0);
        for(int part=0;part<6;part++) {
            long packed=input.getLongOr("paints_"+part,0);
            for(int q=0;q<16;q++)squares[part*16+q]=SchoolPaint.fromId((int)((packed>>>(q*4))&15)).ordinal();
        }
        invalidateClientGeometry();
    }
    @Override protected void invalidateClientGeometry() {
        super.invalidateClientGeometry();
        // Neighbor caps depend on this mask too; refresh after the BE packet has been applied.
        if(level!=null&&level.isClientSide())for(Direction direction:Direction.values()) {
            BlockPos neighbor=worldPosition.relative(direction);
            if(level.hasChunkAt(neighbor)&&level.getBlockEntity(neighbor) instanceof SchoolDoorFramePaintEntity) {
                BlockState state=level.getBlockState(neighbor);
                level.sendBlockUpdated(neighbor,state,state,net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
            }
        }
    }
}
