package net.nicomar2009.lsmmod.client.renderer;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.nicomar2009.lsmmod.entity.SchoolNpcSize;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.nicomar2009.lsmmod.client.model.PlaceholderSchoolNpcModel;
import net.nicomar2009.lsmmod.entity.SchoolNpcEntity;
import net.nicomar2009.lsmmod.client.renderer.state.SchoolNpcRenderState;

public class SchoolNpcRenderer extends HumanoidMobRenderer<SchoolNpcEntity, HumanoidRenderState, PlaceholderSchoolNpcModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/school_npc.png");
    private static final Identifier PLACEHOLDER = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/placeholder_school_npc.png");
    private static final Map<Identifier, Boolean> VALID_TEXTURES = new ConcurrentHashMap<>();
    private static final Identifier FALLBACK = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");
    public SchoolNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new PlaceholderSchoolNpcModel(context.bakeLayer(PlaceholderSchoolNpcModel.LAYER)), 0.5F);
        addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), PlaceholderSchoolNpcModel.Armor::new),
                context.getEquipmentRenderer()));
    }
    @Override
    protected float getShadowRadius(HumanoidRenderState state) {
        return super.getShadowRadius(state) * (state instanceof SchoolNpcRenderState npcState
                ? npcState.widthScale / 0.9375F : 1.0F);
    }

    @Override
    protected void scale(HumanoidRenderState state, PoseStack poseStack) {
        if (state instanceof SchoolNpcRenderState npcState) {
            poseStack.scale(npcState.widthScale, npcState.heightScale, npcState.widthScale);
            // Cancel the renderer's 0.001-unit floor padding, keeping the feet on the same plane.
            poseStack.translate(0.0F, 0.001F, 0.0F);
        }
    }

    @Override
    public HumanoidRenderState createRenderState() { return new SchoolNpcRenderState(); }
    public static void reloadSkinValidation(ResourceManager manager) {
        VALID_TEXTURES.clear();
        Minecraft.getInstance().execute(StudentAppearanceTextures::clear);
    }

    private static boolean isUsableSkin(Identifier texture) {
        return VALID_TEXTURES.computeIfAbsent(texture, path -> {
            var resource = Minecraft.getInstance().getResourceManager().getResource(path);
            if (resource.isEmpty()) return false;
            try (var input = resource.get().open(); NativeImage image = NativeImage.read(input)) {
                int width = image.getWidth();
                if (width < 64 || width > 4096 || width % 64 != 0 || image.getHeight() != width) return false;
                int scale = width / 64;
                // These are base head/body/limb regions; overlay-layer transparency remains valid.
                int[][] centers = {{12, 12}, {24, 26}, {46, 26}, {38, 58}, {6, 26}, {22, 58}};
                for (int[] center : centers) {
                    if ((image.getPixel(center[0] * scale, center[1] * scale) >>> 24) == 0) return false;
                }
                return true;
            } catch (IOException | RuntimeException invalidSkin) {
                return false;
            }
        });
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        Identifier base = getBaseTexture(state);
        return state instanceof SchoolNpcRenderState npc && npc.isStudent
                ? StudentAppearanceTextures.get(base, npc) : base;
    }

    private Identifier getBaseTexture(HumanoidRenderState state) {
        if (state instanceof SchoolNpcRenderState npcState && npcState.isStudent) {
            int grade = npcState.classGrade;
            if (grade >= 1 && grade <= 11) {
                String file = grade <= 6 ? grade + "p" : (grade - 6) + "s";
                if (npcState.isFemale) {
                    Identifier femaleSkin = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/students/" + file + "_female.png");
                    if (isUsableSkin(femaleSkin)) return femaleSkin;
                }
                Identifier skin = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/students/" + file + ".png");
                if (isUsableSkin(skin)) return skin;
                Identifier placeholder = Identifier.fromNamespaceAndPath("lsmmod",
                        "textures/entity/students/placeholder_" + file + (npcState.isFemale ? "_female" : "") + ".png");
                if (isUsableSkin(placeholder)) return placeholder;
            }
        }
        if (state instanceof SchoolNpcRenderState npcState && npcState.isStudent) {
            Identifier generic = Identifier.fromNamespaceAndPath("lsmmod", "textures/entity/placeholder_student_"
                    + (npcState.isFemale ? "female" : "male") + ".png");
            if (isUsableSkin(generic)) return generic;
        }
        if (isUsableSkin(TEXTURE)) return TEXTURE;
        return isUsableSkin(PLACEHOLDER) ? PLACEHOLDER : FALLBACK;
    }
    @Override
    public void extractRenderState(SchoolNpcEntity entity, HumanoidRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof SchoolNpcRenderState npcState) {
            npcState.isTeacher = entity.isTeacher();
            npcState.isStudent = entity.isStudent();
            npcState.classGrade = entity.getClassGrade();
            npcState.widthScale = SchoolNpcSize.modelWidthScale(entity.getWidthLevel());
            npcState.heightScale = SchoolNpcSize.modelHeightScale(entity.getHeightLevel());
            npcState.isFemale = entity instanceof net.nicomar2009.lsmmod.entity.StudentEntity student && student.isFemale();
            if (entity instanceof net.nicomar2009.lsmmod.entity.StudentEntity student) {
                npcState.freeHair = student.hasFreeHair();
                npcState.skinColor = student.getSkinColor();
                npcState.eyeColor = student.getEyeColor();
                npcState.haircut = student.getHaircut();
                npcState.glassesType = student.getGlassesType();
            } else {
                npcState.freeHair = false;
                npcState.skinColor = npcState.eyeColor = npcState.haircut = npcState.glassesType = -1;
            }
        }
        // No consumption, attack, crouching, swimming, idle or independent head animation.
        state.attackTime = 0;
    }
}
