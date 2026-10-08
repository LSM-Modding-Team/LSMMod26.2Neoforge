package net.nicomar2009.lsmmod.event;

import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.ModItems;

/** Consume helper interactions on both sides, so its GUI does not also activate blocks/entities. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class NpcHelperInteractionEvents {
    private NpcHelperInteractionEvents() { }
    private static boolean helper(PlayerInteractEvent event) {
        return event.getItemStack().is(ModItems.NPC_SUMMON_HELPER.get());
    }
    @SubscribeEvent public static void air(PlayerInteractEvent.RightClickItem event) {
        if (!helper(event)) return;
        event.setCancellationResult(InteractionResult.SUCCESS); event.setCanceled(true);
    }
    @SubscribeEvent public static void block(PlayerInteractEvent.RightClickBlock event) {
        if (!helper(event)) return;
        event.setCancellationResult(InteractionResult.SUCCESS); event.setCanceled(true);
    }
    @SubscribeEvent public static void entity(PlayerInteractEvent.EntityInteract event) {
        if (!helper(event)) return;
        event.setCancellationResult(InteractionResult.SUCCESS); event.setCanceled(true);
    }
}
