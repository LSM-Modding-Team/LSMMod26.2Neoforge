package net.nicomar2009.lsmmod.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.nicomar2009.lsmmod.block.SchoolPaint;
import net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity;

/** Reusable paint; a copied six-face palette is applied once, then normal painting resumes. */
public class SchoolPaintItem extends Item {
    private static final String COPY = "lsmmod_paint_copy";
    private final SchoolPaint paint;
    public SchoolPaintItem(SchoolPaint paint, Properties properties) { super(properties.stacksTo(1)); this.paint = paint; }
    public static void copyTo(ItemStack stack, SchoolPaintableBlockEntity source) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag copy = new CompoundTag(); int[] colors = source.snapshot();
        copy.putBoolean("quadrants", colors.length == 24);
        for (Direction face : Direction.values()) {
            if (colors.length == 6) copy.putInt(face.getSerializedName(), colors[face.ordinal()]);
            else for (int q = 0; q < 4; q++) copy.putInt(face.getSerializedName()+"_"+q, colors[face.ordinal()*4+q]);
        }
        tag.put(COPY, copy); stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    private static int[] copied(CompoundTag tag) {
        if (!tag.contains(COPY)) return null;
        CompoundTag copy = tag.getCompoundOrEmpty(COPY);
        int count = copy.getBooleanOr("quadrants", false) ? 4 : 1;
        int[] colors = new int[6*count];
        for (Direction face : Direction.values()) for (int q = 0; q < count; q++) {
            String key = face.getSerializedName() + (count == 4 ? "_"+q : "");
            if (!copy.contains(key)) return null;
            int color = copy.getIntOr(key, -1);
            if (!SchoolPaint.validId(color)) return null;
            colors[face.ordinal()*count+q] = color;
        }
        return colors;
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var pos = context.getClickedPos(); var player = context.getPlayer();
        if (!(level.getBlockEntity(pos) instanceof SchoolPaintableBlockEntity block)) return InteractionResult.PASS;
        if (player == null || player.isSpectator() || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand())) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            ItemStack stack = context.getItemInHand();
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            int[] colors = copied(tag);
            if (colors == null) block.paintAt(context.getClickedFace(), context.getClickLocation(), paint);
            else {
                block.apply(colors); tag.remove(COPY);
                if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
                else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
