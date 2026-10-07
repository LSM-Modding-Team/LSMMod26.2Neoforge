package net.nicomar2009.lsmmod.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;

/** Vanilla wide-arm player mesh; only the walking cycle is animated. */
public class TestNpcModel extends PlayerModel {
    public TestNpcModel(ModelPart root) { super(root, false); }
    @Override
    public void setupAnim(AvatarRenderState state) { animateWalking(this, state); }

    public static void animateWalking(HumanoidModel<AvatarRenderState> model, AvatarRenderState state) {
        model.resetPose();
        float phase = state.walkAnimationPos * 0.6662F;
        float speed = state.walkAnimationSpeed;
        model.rightArm.xRot = Mth.cos(phase + (float)Math.PI) * speed;
        model.leftArm.xRot = Mth.cos(phase) * speed;
        model.rightLeg.xRot = Mth.cos(phase) * 1.4F * speed;
        model.leftLeg.xRot = Mth.cos(phase + (float)Math.PI) * 1.4F * speed;
    }

    public static class Armor extends HumanoidModel<AvatarRenderState> {
        public Armor(ModelPart root) { super(root); }
        @Override
        public void setupAnim(AvatarRenderState state) { animateWalking(this, state); }
    }
}
