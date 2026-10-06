package net.nicomar2009.lsmmod.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.nicomar2009.lsmmod.registry.ModItems;

/** Sneak-use selects the width stored on this stack, before placing a support. */
public class AwningSupportItem extends BlockItem {
    private static final String WIDTH_KEY = "lsmmod_awning_width";
    public AwningSupportItem(Block block, Properties properties) { super(block, properties); }
    public static int width(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return Math.clamp(tag.getIntOr(WIDTH_KEY, 1), 1, 16);
    }
    private static void setWidth(ItemStack stack, int width) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(WIDTH_KEY, Math.clamp(width, 1, 16));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    public static ItemStack returnedSupport(int width) {
        ItemStack stack = new ItemStack(ModItems.AWNING_SUPPORT.get());
        setWidth(stack, width);
        return stack;
    }
    private InteractionResult select(Level level, Player player, ItemStack stack) {
        if (!level.isClientSide()) {
            int next = width(stack) % 16 + 1;
            setWidth(stack, next);
            player.sendOverlayMessage(Component.translatable("message.lsmmod.awning.width", next));
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return player.isShiftKeyDown() ? select(level, player, player.getItemInHand(hand)) : InteractionResult.PASS;
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) return select(context.getLevel(), player, context.getItemInHand());
        return super.useOn(context);
    }
}
