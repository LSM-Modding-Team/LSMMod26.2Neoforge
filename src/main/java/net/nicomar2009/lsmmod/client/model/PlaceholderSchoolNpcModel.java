package net.nicomar2009.lsmmod.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

/** Baked vanilla player mesh, with a mob render state and only the walking cycle. */
public class PlaceholderSchoolNpcModel extends HumanoidModel<HumanoidRenderState> {
    public PlaceholderSchoolNpcModel(ModelPart root) { super(root); }
    @Override
    public void setupAnim(HumanoidRenderState state) { animateWalking(this, state); }

    public static void animateWalking(HumanoidModel<HumanoidRenderState> model, HumanoidRenderState state) {
        model.resetPose();
        float phase = state.walkAnimationPos * 0.6662F;
        float speed = state.walkAnimationSpeed;
        model.rightArm.xRot = Mth.cos(phase + (float)Math.PI) * speed;
        model.leftArm.xRot = Mth.cos(phase) * speed;
        model.rightLeg.xRot = Mth.cos(phase) * 1.4F * speed;
        model.leftLeg.xRot = Mth.cos(phase + (float)Math.PI) * 1.4F * speed;
    }

    public static class Armor extends HumanoidModel<HumanoidRenderState> {
        public Armor(ModelPart root) { super(root); }
        @Override
        public void setupAnim(HumanoidRenderState state) { animateWalking(this, state); }
    }
}
