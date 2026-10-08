package net.nicomar2009.lsmmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.nicomar2009.lsmmod.ball.BallEntity;

/** Reuses the actual cuboid-sphere item mesh for both server-synchronized entity modes. */
public final class BallRenderer extends ThrownItemRenderer<BallEntity> {
    public BallRenderer(EntityRendererProvider.Context context) { super(context, 0.5F, false); }
    @Override public void submit(ThrownItemRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
        stack.pushPose(); stack.translate(0, 0.25, 0);
        super.submit(state, stack, collector, camera);
        stack.popPose();
    }
}
