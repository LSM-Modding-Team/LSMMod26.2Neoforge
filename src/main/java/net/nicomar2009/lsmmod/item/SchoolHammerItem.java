package net.nicomar2009.lsmmod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.nicomar2009.lsmmod.block.SchoolPaintableCustomBlockEntity;

public class SchoolHammerItem extends Item {
    public SchoolHammerItem(Properties properties) { super(properties.stacksTo(1)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();
        if(level.getBlockEntity(pos) instanceof net.nicomar2009.lsmmod.block.SchoolDoorFramePaintEntity frame) {
            if(!frame.canPaintAt(context.getClickedFace(),context.getClickLocation()))return InteractionResult.PASS;
            if(player==null||player.isSpectator()||!player.mayBuild()||!level.mayInteract(player,pos)
                    ||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand()))return InteractionResult.FAIL;
            if(!level.isClientSide())frame.removeCube(context.getClickedFace(),context.getClickLocation());
            return InteractionResult.SUCCESS;
        }
        if(!(level.getBlockEntity(pos) instanceof SchoolPaintableCustomBlockEntity block)) return InteractionResult.PASS;
        if(block instanceof net.nicomar2009.lsmmod.block.SchoolProjectingWindowPaintEntity)return InteractionResult.PASS;
        if(player==null||player.isSpectator()||!level.mayInteract(player,pos)
                ||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand())) return InteractionResult.FAIL;
        if(!level.isClientSide()) block.removeCube(context.getClickedFace(),context.getClickLocation());
        return InteractionResult.SUCCESS;
    }
}
