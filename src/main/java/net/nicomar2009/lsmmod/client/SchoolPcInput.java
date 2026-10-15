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
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.nicomar2009.lsmmod.media.SchoolMediaDevices;
import net.nicomar2009.lsmmod.network.SchoolMediaPayloads;
import net.nicomar2009.lsmmod.network.SchoolStudyPayload;
import net.nicomar2009.lsmmod.rules.SchoolMediaRules;
import org.lwjgl.glfw.GLFW;

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
        if (minecraft.gui.screen() != null
                || !minecraft.player.getMainHandItem().isEmpty()
                || !minecraft.player.getOffhandItem().isEmpty()
                || !(minecraft.hitResult instanceof BlockHitResult hit)) return;
        var window = minecraft.getWindow();
        boolean control = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        if (control && SchoolMediaDevices.role(minecraft.level.getBlockState(hit.getBlockPos())) != SchoolMediaRules.Role.NONE) {
            event.setCanceled(true); event.setSwingHand(false);
            if (event.getHand() == InteractionHand.MAIN_HAND)
                ClientPacketDistributor.sendToServer(new SchoolMediaPayloads.Request(hit.getBlockPos(), minecraft.player.isShiftKeyDown() ? 2 : 1));
            return;
        }
        if (minecraft.player.isShiftKeyDown() || !SchoolPcScreen.hasPc(minecraft.level.getBlockState(hit.getBlockPos()))) return;
        event.setCanceled(true);
        event.setSwingHand(false);
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            if (SchoolMediaDevices.role(minecraft.level.getBlockState(hit.getBlockPos())) == SchoolMediaRules.Role.PC)
                ClientPacketDistributor.sendToServer(new SchoolStudyPayload(hit.getBlockPos()));
            SchoolPcScreen.open(hit.getBlockPos());
        }
    }
}
