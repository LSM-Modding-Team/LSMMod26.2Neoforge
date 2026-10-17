package net.nicomar2009.lsmmod.modules.balls.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.ModEntities;

@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PLASTIC_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
        event.registerEntityRenderer(ModEntities.FOOTBALL_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
        event.registerEntityRenderer(ModEntities.BASKETBALL_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
        event.registerEntityRenderer(ModEntities.VOLLEYBALL_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
    }
}
