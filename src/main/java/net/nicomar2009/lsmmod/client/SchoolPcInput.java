package net.nicomar2009.lsmmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.client.screen.SchoolPcScreen;

/** Open locally before a normal use packet; Shift and item interactions retain their behavior. */
@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class SchoolPcInput {
    private SchoolPcInput() { }

    @SubscribeEvent
    public static void open(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!event.isUseItem() || minecraft.player == null || minecraft.level == null) return;
        // NeoForge still emits the off-hand event after a cancelled main-hand event.
        if (minecraft.gui.screen() instanceof SchoolPcScreen) {
            event.setCanceled(true);
            event.setSwingHand(false);
            return;
        }
        if (minecraft.gui.screen() != null || minecraft.player.isShiftKeyDown()
                || !minecraft.player.getMainHandItem().isEmpty()
                || !minecraft.player.getOffhandItem().isEmpty()
                || !(minecraft.hitResult instanceof BlockHitResult hit)
                || !SchoolPcScreen.hasPc(minecraft.level.getBlockState(hit.getBlockPos()))) return;
        event.setCanceled(true);
        event.setSwingHand(false);
        if (event.getHand() == InteractionHand.MAIN_HAND) SchoolPcScreen.open(hit.getBlockPos());
    }
}
