package net.nicomar2009.lsmmod.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import com.mojang.blaze3d.platform.InputConstants;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.network.FootballInputPayload;
import net.nicomar2009.lsmmod.rules.FootballRules;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class FootballInput {
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "football"));
    private static final KeyMapping SHOOT = new KeyMapping("key.lsmmod.football_shoot", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    private static boolean wasDown, charging;
    private static int followSample;

    @SubscribeEvent public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY); event.register(SHOOT);
    }

    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) { reset(); return; }
        if (minecraft.gui.screen() != null || !minecraft.player.isAlive() || minecraft.player.isSpectator()
                || !minecraft.isWindowActive() || minecraft.isPaused()) {
            if (charging || followSample > 0) send(minecraft, FootballInputPayload.CANCEL);
            reset(); wasDown = SHOOT.isDown(); return;
        }
        boolean down = SHOOT.isDown();
        // Complete the previous strike before accepting another charge.
        if (followSample > 0) {
            send(minecraft, FootballInputPayload.FOLLOW + followSample - 1);
            if (++followSample > FootballRules.FOLLOW_TICKS) followSample = 0;
        } else if (down && !wasDown) {
            charging = true; send(minecraft, FootballInputPayload.START);
        } else if (charging) {
            if (down) {
                send(minecraft, FootballInputPayload.HOLD);
            } else {
                send(minecraft, FootballInputPayload.RELEASE); charging = false; followSample = 1;
            }
        }
        wasDown = down;
    }

    private static void send(Minecraft minecraft, int action) {
        int keys = (minecraft.options.keyUp.isDown() ? FootballRules.FORWARD : 0)
                | (minecraft.options.keyDown.isDown() ? FootballRules.BACKWARD : 0)
                | (minecraft.options.keyLeft.isDown() ? FootballRules.LEFT : 0)
                | (minecraft.options.keyRight.isDown() ? FootballRules.RIGHT : 0);
        ClientPacketDistributor.sendToServer(new FootballInputPayload(-1, action, keys,
                minecraft.player.getYRot(), minecraft.player.getXRot()));
    }
    private static void reset() { wasDown = false; charging = false; followSample = 0; }
}
