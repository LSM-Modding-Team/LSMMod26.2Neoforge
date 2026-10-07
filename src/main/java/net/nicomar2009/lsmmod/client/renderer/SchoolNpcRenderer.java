package net.nicomar2009.lsmmod.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.client.model.SchoolNpcModel;
import net.nicomar2009.lsmmod.entity.SchoolNpcEntity;
import net.nicomar2009.lsmmod.client.renderer.state.SchoolNpcRenderState;

public class SchoolNpcRenderer extends HumanoidMobRenderer<SchoolNpcEntity, HumanoidRenderState, SchoolNpcModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/school_npc.png");
    private static final Identifier FALLBACK = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");
    public SchoolNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new SchoolNpcModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), SchoolNpcModel.Armor::new),
                context.getEquipmentRenderer()));
    }
    @Override
    public HumanoidRenderState createRenderState() { return new SchoolNpcRenderState(); }
    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        if (state instanceof SchoolNpcRenderState npcState && npcState.isStudent) {
            int grade = npcState.classGrade;
            if (grade >= 1 && grade <= 11) {
                String file = grade <= 6 ? grade + "p" : (grade - 6) + "s";
                Identifier studentTexture = Identifier.fromNamespaceAndPath("lsmmod",
                        "textures/entity/students/" + file + ".png");
                // Check the active resources so an absent grade never displays the missing texture.
                if (Minecraft.getInstance().getResourceManager().getResource(studentTexture).isPresent()) {
                    return studentTexture;
                }
            }
        }
        return Minecraft.getInstance().getResourceManager().getResource(TEXTURE).isPresent() ? TEXTURE : FALLBACK;
    }
    @Override
    public void extractRenderState(SchoolNpcEntity entity, HumanoidRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof SchoolNpcRenderState npcState) {
            npcState.isTeacher = entity.isTeacher();
            npcState.isStudent = entity.isStudent();
            npcState.classGrade = entity.getClassGrade();
        }
        // No consumption, attack, crouching, swimming, idle or independent head animation.
        state.attackTime = 0;
    }
}
