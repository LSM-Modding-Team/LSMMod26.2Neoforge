package net.nicomar2009.lsmmod.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.nicomar2009.lsmmod.block.CurtainPart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.block.SchoolCurtains;
import net.nicomar2009.lsmmod.registry.ModItems;

/** Invalid targets must not activate a door or another block before Item.useOn runs. */
@EventBusSubscriber(modid=LSMMod.MOD_ID)
public final class SchoolCurtainEvents {
    private SchoolCurtainEvents() { }
    @SubscribeEvent public static void block(PlayerInteractEvent.RightClickBlock event) {
        if(event.getItemStack().is(ModItems.SCHOOL_CURTAIN.get())
                && !SchoolCurtains.canAttach(event.getLevel().getBlockState(event.getPos()))) {
            if(!event.getLevel().isClientSide())
                event.getEntity().sendOverlayMessage(Component.translatable("message.lsmmod.curtain.target"));
            event.setCancellationResult(InteractionResult.FAIL);event.setCanceled(true);
        }
    }
    @SubscribeEvent public static void piston(PistonEvent.Pre event) {
        if(event.getPistonMoveType()==PistonEvent.PistonMoveType.RETRACT
                && !event.getLevel().getBlockState(event.getPos()).is(Blocks.STICKY_PISTON))return;
        var helper=event.getStructureHelper();
        if(helper==null || !helper.resolve())return;
        for(var pos:helper.getToPush()) {
            if(SchoolCurtains.part(event.getLevel().getBlockState(pos))!=CurtainPart.NONE) {
                event.setCanceled(true);return;
            }
        }
        for(var pos:helper.getToDestroy()) {
            if(SchoolCurtains.part(event.getLevel().getBlockState(pos))!=CurtainPart.NONE) {
                event.setCanceled(true);return;
            }
        }
    }
}
