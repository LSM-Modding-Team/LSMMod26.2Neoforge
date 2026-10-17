package net.nicomar2009.lsmmod.modules.technology.client.media;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.technology.media.SchoolMediaDevices;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules.Role;

/** Extract immutable world geometry first; submitting it does not inspect mutable world/browser state. */
@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class SchoolProjectionRenderer {
    private record Surface(Identifier texture, Vec3 topLeft, Vec3 right, int width, int height, Direction facing) { }
    private static final ContextKey<List<Surface>> SURFACES = new ContextKey<>(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "projected_screens"));
    private SchoolProjectionRenderer() { }
    @SubscribeEvent public static void extract(ExtractLevelRenderStateEvent event) {
        List<Surface> result = new ArrayList<>();
        if (ModList.get().isLoaded("rinku")) for (var projection : SchoolMediaClient.projections()) {
            var area = projection.area();
            if (!event.getLevel().hasChunkAt(projection.projector()) || !event.getLevel().hasChunkAt(area.origin())
                    || SchoolMediaDevices.role(event.getLevel().getBlockState(projection.projector())) != Role.PROJECTOR
                    || SchoolMediaDevices.role(event.getLevel().getBlockState(area.origin())) != Role.SCREEN) continue;
            if (event.getCamera().position().distanceToSqr(Vec3.atCenterOf(area.origin())) > 128 * 128) continue;
            Identifier texture = SchoolBrowserSessions.texture(projection.source());
            if (texture == null) continue;
            Direction across = area.facing().getClockWise();
            Vec3 normal = Vec3.atLowerCornerOf(area.facing().getUnitVec3i());
            Vec3 right = Vec3.atLowerCornerOf(across.getOpposite().getUnitVec3i());
            Vec3 left = Vec3.atCenterOf(area.origin()).add(right.scale(-(area.width() - .5)))
                    .add(normal.scale(-.4375 + .003)).add(0, area.height() - .5, 0);
            result.add(new Surface(texture, left, right, area.width(), area.height(), area.facing()));
        }
        event.getRenderState().setRenderData(SURFACES, List.copyOf(result));
    }
    @SubscribeEvent public static void submit(SubmitCustomGeometryEvent event) {
        List<Surface> surfaces = event.getLevelRenderState().getRenderData(SURFACES);
        if (surfaces == null) return;
        Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
        for (Surface surface : surfaces) {
            PoseStack pose = event.getPoseStack();
            pose.pushPose();
            Vec3 origin = surface.topLeft().subtract(camera);
            pose.translate(origin.x, origin.y, origin.z);
            event.getSubmitNodeCollector().submitCustomGeometry(pose, RenderTypes.entityTranslucentEmissive(surface.texture()), (matrix, vertices) -> {
                float x = (float) (surface.right().x * surface.width()), z = (float) (surface.right().z * surface.width());
                // U and V span the entire page independently: no crop or letterboxing when aspect ratios differ.
                vertex(vertices, matrix, 0, 0, 0, 0, 0, surface.facing());
                vertex(vertices, matrix, 0, -surface.height(), 0, 0, 1, surface.facing());
                vertex(vertices, matrix, x, -surface.height(), z, 1, 1, surface.facing());
                vertex(vertices, matrix, x, 0, z, 1, 0, surface.facing());
            });
            pose.popPose();
        }
    }
    private static void vertex(VertexConsumer output, PoseStack.Pose pose, float x, float y, float z, float u, float v, Direction facing) {
        output.addVertex(pose, x, y, z).setColor(255, 255, 255, 255).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0)
                .setNormal(pose, facing.getStepX(), 0, facing.getStepZ());
    }
}
