package net.nicomar2009.lsmmod.item;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.block.CurtainPart;
import net.nicomar2009.lsmmod.block.SchoolCurtains;
import net.nicomar2009.lsmmod.block.SchoolGlassBlock;
import net.nicomar2009.lsmmod.registry.ModItems;

/** Two clicks attach one curtain to a bounded 4 by 8 rectangle of compatible glass. */
public final class SchoolCurtainItem extends Item {
    private static final String START="lsmmod_curtain_start";
    private static final String DIMENSION="lsmmod_curtain_dimension";
    private static final String OWNER="lsmmod_curtain_player";
    public SchoolCurtainItem(Properties properties) { super(properties.stacksTo(16)); }
    private static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
    }
    private static void cancel(ItemStack stack) {
        CompoundTag tag=data(stack);tag.remove(START);tag.remove(DIMENSION);tag.remove(OWNER);
        if(tag.isEmpty())stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
    }
    private static InteractionResult fail(Player player,String key) {
        player.sendOverlayMessage(Component.translatable("message.lsmmod.curtain."+key));
        return InteractionResult.FAIL;
    }
    @Override
    public InteractionResult use(Level level,Player player,InteractionHand hand) {
        if(!player.isShiftKeyDown())return InteractionResult.PASS;
        if(!level.isClientSide()) {
            cancel(player.getItemInHand(hand));
            player.sendOverlayMessage(Component.translatable("message.lsmmod.curtain.cancelled"));
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player=context.getPlayer();
        if(player==null)return InteractionResult.FAIL;
        Level world=context.getLevel();BlockPos end=context.getClickedPos();
        BlockState endState=world.getBlockState(end);
        if(!SchoolCurtains.canAttach(endState))return InteractionResult.FAIL;
        if(world.isClientSide())return InteractionResult.SUCCESS;
        ServerLevel level=(ServerLevel)world;ItemStack stack=context.getItemInHand();
        if(!player.mayBuild()||!level.mayInteract(player,end))return fail(player,"denied");
        if(player.isShiftKeyDown()) {
            cancel(stack);
            if(SchoolCurtains.part(endState)==CurtainPart.NONE)return InteractionResult.SUCCESS;
            Direction right=endState.getValue(SchoolGlassBlock.FACING).getClockWise();
            for(int x=-3;x<=3;x++)for(int y=-7;y<=7;y++) {
                BlockPos p=end.relative(right,x).above(y);
                if(!level.hasChunkAt(p))return fail(player,"unloaded");
            }
            for(BlockPos p:SchoolCurtains.cells(level,end,endState))
                if(!level.mayInteract(player,p))return fail(player,"denied");
            if(SchoolCurtains.remove(level,end,endState,false) && !player.isCreative()) {
                ItemStack returned=new ItemStack(ModItems.SCHOOL_CURTAIN.get());
                if(!player.getInventory().add(returned))player.drop(returned,false);
            }
            return InteractionResult.SUCCESS;
        }
        if(SchoolCurtains.part(endState)!=CurtainPart.NONE)return fail(player,"occupied");
        CompoundTag tag=data(stack);
        String dimension=level.dimension().identifier().toString();
        if(!tag.contains(START)||!tag.getStringOr(DIMENSION,"").equals(dimension)
                ||!tag.getStringOr(OWNER,"").equals(player.getUUID().toString())) {
            tag.putLong(START,end.asLong());tag.putString(DIMENSION,dimension);tag.putString(OWNER,player.getUUID().toString());
            stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
            player.sendOverlayMessage(Component.translatable("message.lsmmod.curtain.start",end.getX(),end.getY(),end.getZ()));
            return InteractionResult.SUCCESS;
        }
        BlockPos first=BlockPos.of(tag.getLongOr(START,0));
        if(!level.hasChunkAt(first))return fail(player,"unloaded");
        BlockState base=level.getBlockState(first);
        if(!SchoolCurtains.compatible(base,endState))return fail(player,"line");
        Direction facing=base.getValue(SchoolGlassBlock.FACING),right=facing.getClockWise();
        int dx=end.getX()-first.getX(),dz=end.getZ()-first.getZ();
        int distance=dx*right.getStepX()+dz*right.getStepZ();
        if(dx*facing.getStepX()+dz*facing.getStepZ()!=0)return fail(player,"line");
        int length=Math.abs(distance)+1;
        int height=Math.abs(end.getY()-first.getY())+1;
        if(length>SchoolCurtains.MAX_LENGTH||height>SchoolCurtains.MAX_HEIGHT)return fail(player,"length");
        BlockPos origin=first.relative(right,Math.min(distance,0)).above(Math.min(end.getY()-first.getY(),0));
        // Validate every target before changing any block or consuming an item.
        for(int y=0;y<height;y++)for(int i=0;i<length;i++) {
            BlockPos target=origin.relative(right,i).above(y);
            if(!level.hasChunkAt(target))return fail(player,"unloaded");
            if(!level.getWorldBorder().isWithinBounds(target)||!level.mayInteract(player,target))return fail(player,"denied");
            BlockState state=level.getBlockState(target);
            if(!SchoolCurtains.compatible(base,state))return fail(player,"line");
            if(SchoolCurtains.part(state)!=CurtainPart.NONE)return fail(player,"occupied");
        }
        for(int y=0;y<height;y++)for(int i=0;i<length;i++) {
            BlockPos target=origin.relative(right,i).above(y);BlockState current=level.getBlockState(target);
            CurtainPart part=length==1?CurtainPart.SINGLE:i==0?CurtainPart.LEFT:i==length-1?CurtainPart.RIGHT:CurtainPart.MIDDLE;
            level.setBlock(target,current.setValue(SchoolCurtains.CURTAIN,part).setValue(SchoolCurtains.VERTICAL,SchoolCurtains.axisPart(y,height)),Block.UPDATE_CLIENTS);
            level.scheduleTick(target,current.getBlock(),20);
        }
        cancel(stack);if(!player.isCreative())stack.shrink(1);
        level.playSound(null,origin,SoundEvents.WOOL_PLACE,SoundSource.BLOCKS,1F,1F);
        player.sendOverlayMessage(Component.translatable("message.lsmmod.curtain.placed",length,height));
        return InteractionResult.SUCCESS;
    }
    @Override
    public void appendHoverText(ItemStack stack,TooltipContext context,TooltipDisplay display,Consumer<Component> output,TooltipFlag flag) {
        output.accept(Component.translatable("tooltip.lsmmod.school_curtain.select"));
        output.accept(Component.translatable("tooltip.lsmmod.school_curtain.remove"));
    }
}
