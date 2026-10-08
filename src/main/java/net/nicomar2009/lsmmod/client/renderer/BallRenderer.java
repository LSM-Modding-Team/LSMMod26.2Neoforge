package net.nicomar2009.lsmmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.nicomar2009.lsmmod.ball.BallEntity;

/** A world-oriented sphere rolls with its actual movement instead of facing the camera. */
public final class BallRenderer extends ThrownItemRenderer<BallEntity> {
    public BallRenderer(EntityRendererProvider.Context context) { super(context, 0.5F, false); }
    public static final class BallRenderState extends ThrownItemRenderState {
        float roll, heading;
    }
    @Override public BallRenderState createRenderState() { return new BallRenderState(); }
    @Override public void extractRenderState(BallEntity entity, ThrownItemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        BallRenderState ball = (BallRenderState) state;
        ball.roll = entity.roll(partialTick); ball.heading = entity.heading();
    }
    @Override public void submit(ThrownItemRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
        BallRenderState ball = (BallRenderState) state;
        stack.pushPose(); stack.translate(0, 0.25, 0); stack.scale(0.5F, 0.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(ball.heading));
        stack.mulPose(Axis.ZP.rotationDegrees(-ball.roll));
        state.item.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        stack.popPose();
    }
}
