package net.nicomar2009.lsmmod.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.ModEntities;

// The bus parameter no longer exists: NeoForge detects the correct bus from the event type
@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TEST_NPC.get(), net.nicomar2009.lsmmod.client.renderer.TestNpcRenderer::new);
        // Assign NoopRenderer to ensure the seat entity remains invisible
        event.registerEntityRenderer(ModEntities.SEAT.get(), NoopRenderer::new);
    }
}
