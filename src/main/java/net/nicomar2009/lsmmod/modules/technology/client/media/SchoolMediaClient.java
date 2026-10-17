package net.nicomar2009.lsmmod.modules.technology.client.media;
import net.nicomar2009.lsmmod.modules.technology.media.SchoolScreenArea;
import net.nicomar2009.lsmmod.modules.technology.media.SchoolMediaRoute;
import net.nicomar2009.lsmmod.modules.technology.media.SchoolMediaDevices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.technology.media.*;
import net.nicomar2009.lsmmod.modules.technology.network.SchoolMediaPayloads;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;

@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class SchoolMediaClient {
    public record Projection(SchoolScreenArea area, BlockPos source, BlockPos projector) { }
    private static ClientLevel level;
    private static List<SchoolMediaRoute> routes = List.of();
    private static List<Projection> projections = List.of();
    private static boolean dirty;
    private static int ticks;
    private SchoolMediaClient() { }
    public static void accept(SchoolMediaPayloads.Snapshot snapshot) {
        ClientLevel current = Minecraft.getInstance().level;
        if (current != null && current.dimension().identifier().equals(snapshot.dimension())) {
            if (level != current) reset(current);
            routes = snapshot.routes(); dirty = true;
        }
    }
    public static List<Projection> projections() { return projections; }
    private static boolean loaded(BlockPos pos) { return level != null && level.hasChunkAt(pos); }
    public static List<BlockPos> speakers(BlockPos source) {
        if (!loaded(source)) return List.of();
        var result = new ArrayList<BlockPos>();
        for (SchoolMediaRoute route : routes) {
            if (route.kind() == SchoolMediaRoute.SPEAKER && route.input().equals(source) && loaded(route.output())
                    && SchoolMediaRules.canConnect(SchoolMediaDevices.role(level.getBlockState(source)),
                        SchoolMediaDevices.role(level.getBlockState(route.output())))) result.add(route.output());
        }
        return List.copyOf(result);
    }
    public static SchoolMediaRoute controller(BlockPos cabinet) {
        if (!loaded(cabinet) || SchoolMediaDevices.role(level.getBlockState(cabinet)) != SchoolMediaRules.Role.AUDIO_CONTROLLER) return null;
        return routes.stream().filter(r -> r.kind() == SchoolMediaRoute.AUDIO_CONTROLLER && r.output().equals(cabinet)
                && loaded(r.input()) && SchoolMediaDevices.role(level.getBlockState(r.input())) == SchoolMediaRules.Role.MOISES_LAPTOP)
                .findFirst().orElse(null);
    }
    public static SchoolMediaRoute audioSettings(BlockPos source) {
        return routes.stream().filter(r -> r.kind() == SchoolMediaRoute.AUDIO_CONTROLLER && r.input().equals(source)
                && controller(r.output()) != null).findFirst().orElse(null);
    }
    public static int speakerRadius(BlockPos speaker) {
        return SchoolMediaRules.speakerRadius(loaded(speaker) ? SchoolMediaDevices.role(level.getBlockState(speaker)) : SchoolMediaRules.Role.NONE);
    }
    public static boolean hasOutputs(BlockPos source) {
        return projections.stream().anyMatch(p -> p.source().equals(source)) || !speakers(source).isEmpty()
                || audioSettings(source) != null;
    }
    private static void resolve() {
        var result = new ArrayList<Projection>();
        Set<SchoolScreenArea> seen = new HashSet<>();
        for (SchoolMediaRoute screen : routes) {
            if (screen.kind() != SchoolMediaRoute.SCREEN || !loaded(screen.output()) || !loaded(screen.input())
                    || SchoolMediaDevices.role(level.getBlockState(screen.input())) != SchoolMediaRules.Role.PROJECTOR) continue;
            for (SchoolMediaRoute projector : routes) {
                if (projector.kind() != SchoolMediaRoute.PROJECTOR || !projector.output().equals(screen.input())
                        || !loaded(projector.input()) || !SchoolMediaRules.source(SchoolMediaDevices.role(level.getBlockState(projector.input())))) continue;
                SchoolScreenArea area = SchoolScreenArea.find(level, screen.output());
                if (area != null && seen.add(area)) result.add(new Projection(area, projector.input(), screen.input()));
            }
        }
        projections = List.copyOf(result); dirty = false;
    }
    private static void reset(ClientLevel next) {
        if (ModList.get().isLoaded("rinku")) SchoolBrowserSessions.closeAll();
        level = next; routes = List.of(); projections = List.of(); dirty = true; ticks = 0;
    }
    public static void prepareLevel() {
        ClientLevel current = Minecraft.getInstance().level;
        if (current != level) reset(current);
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        var minecraft = Minecraft.getInstance();
        prepareLevel();
        if (level == null || minecraft.player == null) return;
        if (ticks++ == 0) ClientPacketDistributor.sendToServer(new SchoolMediaPayloads.Request(BlockPos.ZERO, 0));
        if (dirty || ticks % 20 == 0) resolve();
        if (ModList.get().isLoaded("rinku")) SchoolBrowserSessions.tick();
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { reset(null); }
}
