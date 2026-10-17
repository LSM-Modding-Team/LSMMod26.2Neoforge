package net.nicomar2009.lsmmod.modules.technology.media;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.util.datafix.DataFixTypes;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules.Role;

/** Dimension-local wiring, independent of block entities and browser profiles. */
public final class SchoolMediaLinks extends SavedData {
    public static final int MAX_ROUTES = 1024;
    private static final SavedDataType<SchoolMediaLinks> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "media_links"), SchoolMediaLinks::new,
            SchoolMediaRoute.CODEC.listOf().fieldOf("routes").codec()
                    .xmap(SchoolMediaLinks::new, SchoolMediaLinks::routes), DataFixTypes.LEVEL);
    private final List<SchoolMediaRoute> routes = new ArrayList<>();
    private SchoolMediaLinks() { }
    private SchoolMediaLinks(List<SchoolMediaRoute> saved) {
        for (SchoolMediaRoute route : saved) {
            if (routes.size() >= MAX_ROUTES) break;
            routes.removeIf(r -> r.output().equals(route.output())
                    || route.kind() == SchoolMediaRoute.AUDIO_CONTROLLER && r.kind() == SchoolMediaRoute.AUDIO_CONTROLLER
                    && r.input().equals(route.input()));
            routes.add(route);
        }
    }
    public static SchoolMediaLinks get(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }
    public List<SchoolMediaRoute> routes() { return List.copyOf(routes); }
    public void put(SchoolMediaRoute route) {
        if (routes.size() >= MAX_ROUTES && routes.stream().noneMatch(r -> r.output().equals(route.output())
                || route.kind() == SchoolMediaRoute.AUDIO_CONTROLLER && r.kind() == SchoolMediaRoute.AUDIO_CONTROLLER
                && r.input().equals(route.input())))
            throw new IllegalArgumentException("limit");
        routes.removeIf(r -> r.output().equals(route.output())
                    || route.kind() == SchoolMediaRoute.AUDIO_CONTROLLER && r.kind() == SchoolMediaRoute.AUDIO_CONTROLLER
                    && r.input().equals(route.input()));
        routes.add(route);
        setDirty();
    }
    public boolean remove(BlockPos device) {
        boolean changed = routes.removeIf(r -> r.output().equals(device) || r.input().equals(device));
        if (changed) setDirty();
        return changed;
    }
    public boolean prune(ServerLevel level) {
        boolean changed = routes.removeIf(r -> {
            if (!level.hasChunkAt(r.output()) || !level.hasChunkAt(r.input())) return false;
            Role input = SchoolMediaDevices.role(level.getBlockState(r.input()));
            Role output = SchoolMediaDevices.role(level.getBlockState(r.output()));
            return !SchoolMediaRules.canConnect(input, output) || kind(output) != r.kind();
        });
        if (changed) setDirty();
        return changed;
    }
    public static int kind(Role role) {
        return switch (role) {
            case PROJECTOR -> SchoolMediaRoute.PROJECTOR;
            case SCREEN -> SchoolMediaRoute.SCREEN;
            case SPEAKER, GENERAL_SPEAKER -> SchoolMediaRoute.SPEAKER;
            case AUDIO_CONTROLLER -> SchoolMediaRoute.AUDIO_CONTROLLER;
            default -> -1;
        };
    }
}
