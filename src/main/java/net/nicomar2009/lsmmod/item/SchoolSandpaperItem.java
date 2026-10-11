package net.nicomar2009.lsmmod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.nicomar2009.lsmmod.block.SchoolPaint;
import net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity;
import net.nicomar2009.lsmmod.registry.ModItems;

public class SchoolSandpaperItem extends Item {
    public SchoolSandpaperItem(Properties properties) { super(properties.stacksTo(1)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();
        if(!(level.getBlockEntity(pos) instanceof SchoolPaintableBlockEntity block)) return InteractionResult.PASS;
        if(player==null||player.isSpectator()||!level.mayInteract(player,pos)
                ||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand())) return InteractionResult.FAIL;
        SchoolPaint color=block.paintAt(context.getClickedFace(),context.getClickLocation());
        if(color==SchoolPaint.UNPAINTED) return InteractionResult.FAIL;
        if(!level.isClientSide()) {
            Item item=switch(color) {
                case LIGHT -> ModItems.LIGHT_SCHOOL.get(); case DARK -> ModItems.DARK_SCHOOL.get();
                case STREET -> ModItems.STREET_SCHOOL.get(); case WHITE -> ModItems.CLASSROOM_WHITE.get();
                default -> throw new IllegalStateException("Unpainted square cannot yield paint");
            };
            block.paintAt(context.getClickedFace(),context.getClickLocation(),SchoolPaint.UNPAINTED);
            ItemStack recovered=new ItemStack(item);
            if(!player.getInventory().add(recovered)) player.drop(recovered,false);
        }
        return InteractionResult.SUCCESS;
    }
}
