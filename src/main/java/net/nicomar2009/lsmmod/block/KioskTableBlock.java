package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** New tables place three cells; old left/right pairs remain valid without extending into neighbors. */
public final class KioskTableBlock extends Block {
    public static final EnumProperty<Direction> FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<KioskTablePart> PART=EnumProperty.create("part",KioskTablePart.class);
    private static final VoxelShape[][] SHAPES=new VoxelShape[3][];
    static {
        for(int col=0;col<3;col++) {
            var boxes=new java.util.ArrayList<double[]>();
            for(double[] b:SchoolEntranceAccessoryShapes.KIOSK) {
                double low=Math.max(b[0],col*16),high=Math.min(b[3],col*16+16);
                if(low<high) boxes.add(new double[]{low-col*16,b[1],b[2],high-col*16,b[4],b[5]});
            }
            // Shared data uses a north-facing X span; the existing table spans native SOUTH/Z.
            SHAPES[col]=SharedChairRotations.horizontal(Shapes.rotate(ExactVoxelShapes.pixels(boxes.toArray(double[][]::new)),com.mojang.math.OctahedralGroup.BLOCK_ROT_Y_90));
        }
    }
    public KioskTableBlock(Properties properties) {super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.SOUTH).setValue(PART,KioskTablePart.LEFT));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {builder.add(FACING,PART);}
    private boolean match(BlockState state,Direction facing,KioskTablePart part) {return state.is(this)&&state.getValue(FACING)==facing&&state.getValue(PART)==part;}
    private BlockPos origin(BlockGetter level,BlockPos pos,BlockState state) {
        Direction facing=state.getValue(FACING);
        if(state.getValue(PART)==KioskTablePart.LEFT)return pos;
        if(state.getValue(PART)==KioskTablePart.CENTER)return pos.relative(facing,-1);
        // Existing two-cell pairs retain their old serialized states and original origin.
        BlockPos previous=pos.relative(facing,-1);
        return match(level.getBlockState(previous),facing,KioskTablePart.LEFT)?previous:pos.relative(facing,-2);
    }
    private int length(BlockGetter level,BlockPos base,Direction facing) {return match(level.getBlockState(base.relative(facing)),facing,KioskTablePart.RIGHT)?2:3;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level=context.getLevel();BlockPos pos=context.getClickedPos();Direction facing=context.getHorizontalDirection();Player player=context.getPlayer();
        if(context.getClickedFace()!=Direction.UP)return null;
        for(int i=0;i<3;i++) {
            BlockPos target=pos.relative(facing,i);BlockState part=defaultBlockState().setValue(FACING,facing).setValue(PART,KioskTablePart.values()[i]);
            if(!level.hasChunkAt(target)||level.isOutsideBuildHeight(target)||!level.getWorldBorder().isWithinBounds(target)
                ||!level.getBlockState(target).canBeReplaced(context)||!level.isUnobstructed(part,target,CollisionContext.empty())
                ||(player!=null&&(!player.mayBuild()||!level.mayInteract(player,target)||!player.mayUseItemAt(target,context.getClickedFace(),context.getItemInHand()))))return null;
        }
        return defaultBlockState().setValue(FACING,facing);
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide())return;
        Direction facing=state.getValue(FACING);
        for(int i=1;i<3;i++)level.setBlock(pos.relative(facing,i),state.setValue(PART,KioskTablePart.values()[i]),Block.UPDATE_CLIENTS);
        for(int i=0;i<3;i++)level.updateNeighborsAt(pos.relative(facing,i),this);
        level.scheduleTick(pos,this,1);
    }
    @Override protected BlockState updateShape(BlockState state,LevelReader level,ScheduledTickAccess ticks,BlockPos pos,Direction side,BlockPos neighborPos,BlockState neighbor,RandomSource random) {ticks.scheduleTick(pos,this,1);return state;}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        Direction facing=state.getValue(FACING);
        // Resolve only after the two possible origins are loaded.
        for(int i=-2;i<=2;i++)if(!level.hasChunkAt(pos.relative(facing,i))){level.scheduleTick(pos,this,20);return;}
        BlockPos base=origin(level,pos,state);int size=length(level,base,facing);
        for(int i=0;i<size;i++) {
            BlockPos target=base.relative(facing,i);
            if(!level.hasChunkAt(target)){level.scheduleTick(pos,this,20);return;}
            KioskTablePart expected=i==0?KioskTablePart.LEFT:i==size-1?KioskTablePart.RIGHT:KioskTablePart.CENTER;
            if(!match(level.getBlockState(target),facing,expected)){dismantle(level,base,facing,size,true,null);return;}
        }
    }
    private void dismantle(Level level,BlockPos base,Direction facing,int size,boolean drop,BlockPos mined) {
        if(!base.equals(mined)&&match(level.getBlockState(base),facing,KioskTablePart.LEFT))level.destroyBlock(base,drop);
        for(int i=0;i<size;i++) {
            BlockPos target=base.relative(facing,i);KioskTablePart expected=i==0?KioskTablePart.LEFT:i==size-1?KioskTablePart.RIGHT:KioskTablePart.CENTER;
            if(!target.equals(mined)&&level.hasChunkAt(target)&&match(level.getBlockState(target),facing,expected))level.setBlock(target,Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
        }
    }
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player) {
        if(!level.isClientSide()) {
            Direction facing=state.getValue(FACING);BlockPos base=origin(level,pos,state);
            dismantle(level,base,facing,length(level,base,facing),!player.isCreative(),pos);
        }
        return super.playerWillDestroy(level,pos,state,player);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SHAPES[state.getValue(PART).ordinal()][state.getValue(FACING).get2DDataValue()];}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {return SimpleBlockOutline.forState(state,()->getCollisionShape(state,level,pos,context));}
    @Override protected BlockState rotate(BlockState state,Rotation rotation) {return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror) {return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
}
