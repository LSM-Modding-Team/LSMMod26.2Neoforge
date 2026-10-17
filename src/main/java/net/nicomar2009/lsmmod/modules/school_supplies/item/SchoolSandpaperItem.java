package net.nicomar2009.lsmmod.modules.school_supplies.item;
import net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems;

import net.nicomar2009.lsmmod.modules.school_supplies.item.RectanglePaintTool;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaint;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlockEntity;

public class SchoolSandpaperItem extends Item {
    public SchoolSandpaperItem(Properties properties) { super(properties.stacksTo(1)); }
    @Override public InteractionResult use(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand) {
        return RectanglePaintTool.cancel(level,player,player.getItemInHand(hand));
    }
    public static void refund(net.minecraft.world.entity.player.Player player,SchoolPaint color) {
        Item item=switch(color) {
            case LIGHT -> ModuleItems.LIGHT_SCHOOL.get(); case DARK -> ModuleItems.DARK_SCHOOL.get();
            case STREET -> ModuleItems.STREET_SCHOOL.get(); case WHITE -> ModuleItems.CLASSROOM_WHITE.get();
            case FLOOR -> ModuleItems.CLASSROOM_FLOOR_PAINT.get(); case YELLOW -> ModuleItems.YELLOW_SCHOOL_PAINT.get();
            case PLAYGROUND_WHITE -> ModuleItems.WHITE_PLAYGROUND_FLOOR_PAINT.get();
            case PLAYGROUND_GRAY -> ModuleItems.GRAY_PLAYGROUND_FLOOR_PAINT.get();
            case PLAYGROUND_GREEN -> ModuleItems.GREEN_PLAYGROUND_FLOOR_PAINT.get();
            case PLAYGROUND_ORANGE -> ModuleItems.ORANGE_PLAYGROUND_FLOOR_PAINT.get();
            case DARK_TILED -> ModuleItems.DARK_TILED_FLOOR_PAINT.get();
            case RED_STREET -> ModuleItems.RED_STREET_WALL_PAINT.get();
            case LABORATORY_TILE -> ModuleItems.SCHOOL_LABORATORY_TILE_PAINT.get();
            case LABORATORY_TABLE_TILE -> ModuleItems.LABORATORY_TABLE_TILE_PAINT.get();
            case AUDITORIUM_PARQUET -> ModuleItems.AUDITORIUM_PARQUET_PAINT.get();
            default -> throw new IllegalArgumentException("Unpainted faces cannot yield paint");
        };
        ItemStack recovered=new ItemStack(item);
        if(!player.getInventory().add(recovered))player.drop(recovered,false);
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();
        if(!(level.getBlockEntity(pos) instanceof SchoolPaintableBlockEntity block)) return InteractionResult.PASS;
        if(!block.canPaintAt(context.getClickedFace(),context.getClickLocation())) return InteractionResult.PASS;
        if(player==null||player.isSpectator()||!level.mayInteract(player,pos)
                ||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand())) return InteractionResult.FAIL;
        InteractionResult rectangle=RectanglePaintTool.use(context,null);
        if(rectangle!=null)return rectangle;
        SchoolPaint color=block.paintAt(context.getClickedFace(),context.getClickLocation());
        if(color==SchoolPaint.UNPAINTED) return InteractionResult.FAIL;
        if(!level.isClientSide()) {
            block.paintAt(context.getClickedFace(),context.getClickLocation(),SchoolPaint.UNPAINTED);
            refund(player,color);
        }
        return InteractionResult.SUCCESS;
    }
}
