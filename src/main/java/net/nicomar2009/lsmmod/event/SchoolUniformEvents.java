package net.nicomar2009.lsmmod.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.item.SchoolUniformItem;

/** Covers direct entity exchanges, including armor stands that bypass Equippable checks. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class SchoolUniformEvents {
    private SchoolUniformEvents() { }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() instanceof SchoolUniformItem
                && !(event.getTarget() instanceof Player)
                && !(event.getTarget() instanceof ArmorStand)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }
}
