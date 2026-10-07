package net.nicomar2009.lsmmod.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.client.renderer.state.SchoolNpcRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

/** Baked vanilla player mesh, with a mob render state and only the walking cycle. */
public class PlaceholderSchoolNpcModel extends HumanoidModel<HumanoidRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("lsmmod", "placeholder_school_npc"), "main");
    private final ModelPart femaleHead, femaleBody, femaleRightArm, femaleLeftArm, femaleRightLeg, femaleLeftLeg;
    private final ModelPart femaleBust, femaleBustFallback;

    public PlaceholderSchoolNpcModel(ModelPart root) {
        super(root);
        femaleHead = root.getChild("female_head");
        femaleBody = root.getChild("female_body");
        femaleRightArm = root.getChild("female_right_arm");
        femaleLeftArm = root.getChild("female_left_arm");
        femaleRightLeg = root.getChild("female_right_leg");
        femaleLeftLeg = root.getChild("female_left_leg");
        femaleBust = femaleBody.getChild("female_bust");
        femaleBustFallback = femaleBody.getChild("female_bust_fallback");
    }

    public static LayerDefinition createBodyLayer() {
        var mesh = PlayerModel.createMesh(CubeDeformation.NONE, false);
        var root = mesh.getRoot();
        // Rest-pose geometry from girl.java, split into limbs to keep the existing walking cycle.
        root.addOrReplaceChild("female_head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4, -8, -5, 8, 8, 8), PartPose.ZERO);
        var torso = root.addOrReplaceChild("female_body", CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4, 0, -3, 8, 12, 4), PartPose.ZERO);
        root.addOrReplaceChild("female_right_arm", CubeListBuilder.create().texOffs(40, 16)
                .addBox(-2, -2, -2, 4, 12, 4), PartPose.offset(-6, 2, -1));
        root.addOrReplaceChild("female_left_arm", CubeListBuilder.create().texOffs(32, 48)
                .addBox(-2, -2, -2, 4, 12, 4), PartPose.offset(6, 2, -1));
        root.addOrReplaceChild("female_right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(-2, 12, -1));
        root.addOrReplaceChild("female_left_leg", CubeListBuilder.create().texOffs(16, 48)
                .addBox(-2, 0, -2, 4, 12, 4), PartPose.offset(2, 12, -1));
        // Exact exported cube_r1 box and rotation, including bb_main's +24 Y origin.
        torso.addOrReplaceChild("female_bust", CubeListBuilder.create().texOffs(16, 32)
                .addBox(-4, -18, 11, 8, 3, 4),
                PartPose.offsetAndRotation(0, 24, 0, 0.7854F, 0, 0));
        // Unassigned Student keeps its existing atlas, which has no dedicated bust patch.
        torso.addOrReplaceChild("female_bust_fallback", CubeListBuilder.create().texOffs(16, 19)
                .addBox(-4, -18, 11, 8, 3, 4),
                PartPose.offsetAndRotation(0, 24, 0, 0.7854F, 0, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        animateWalking(this, state);
        boolean female = state instanceof SchoolNpcRenderState npc && npc.isStudent && npc.isFemale;
        head.visible = body.visible = rightArm.visible = leftArm.visible = rightLeg.visible = leftLeg.visible = !female;
        femaleHead.visible = femaleBody.visible = femaleRightArm.visible = femaleLeftArm.visible
                = femaleRightLeg.visible = femaleLeftLeg.visible = female;
        boolean assigned = state instanceof SchoolNpcRenderState npc && npc.classGrade >= 1 && npc.classGrade <= 11;
        femaleBust.visible = female && assigned;
        femaleBustFallback.visible = female && !assigned;
        femaleRightArm.xRot = rightArm.xRot;
        femaleLeftArm.xRot = leftArm.xRot;
        femaleRightLeg.xRot = rightLeg.xRot;
        femaleLeftLeg.xRot = leftLeg.xRot;
        if (female) {
            // Held-item layers still use the humanoid arm pivots.
            rightArm.x = -6; leftArm.x = 6;
            rightArm.z = leftArm.z = -1;
        }
    }

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
