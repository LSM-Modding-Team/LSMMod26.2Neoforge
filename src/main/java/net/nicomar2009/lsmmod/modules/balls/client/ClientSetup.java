package net.nicomar2009.lsmmod.modules.balls.client;
import net.nicomar2009.lsmmod.modules.balls.registry.ModuleEntities;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.nicomar2009.lsmmod.LSMMod;

@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModuleEntities.PLASTIC_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
        event.registerEntityRenderer(ModuleEntities.FOOTBALL_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
        event.registerEntityRenderer(ModuleEntities.BASKETBALL_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
        event.registerEntityRenderer(ModuleEntities.VOLLEYBALL_BALL.get(), net.nicomar2009.lsmmod.modules.balls.client.renderer.BallRenderer::new);
    }
}
