package net.nicomar2009.lsmmod.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.client.model.TestNpcModel;
import net.nicomar2009.lsmmod.entity.TestNpcEntity;

public class TestNpcRenderer extends HumanoidMobRenderer<TestNpcEntity, HumanoidRenderState, TestNpcModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/test_npc.png");
    private static final Identifier FALLBACK = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");
    private final Identifier texture;
    public TestNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new TestNpcModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        texture = Minecraft.getInstance().getResourceManager().getResource(TEXTURE).isPresent() ? TEXTURE : FALLBACK;
        addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), TestNpcModel.Armor::new),
                context.getEquipmentRenderer()));
    }
    @Override
    public HumanoidRenderState createRenderState() { return new HumanoidRenderState(); }
    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) { return texture; }
    @Override
    public void extractRenderState(TestNpcEntity entity, HumanoidRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        // No consumption, attack, crouching, swimming, idle or independent head animation.
        state.attackTime = 0;
    }
}
