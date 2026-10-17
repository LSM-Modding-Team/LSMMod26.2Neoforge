package net.nicomar2009.lsmmod.modules.technology.media;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.technology.network.SchoolMediaPayloads;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules.Role;

@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class SchoolMediaServer {
    private record Selection(ResourceKey<Level> dimension, BlockPos source, BlockPos projector) { }
    private static final Map<UUID, Selection> SELECTED = new HashMap<>();
    private static final Map<UUID, Long> LAST_REQUEST = new HashMap<>();
    private static final Map<UUID, Long> LAST_SYNC = new HashMap<>();
    private SchoolMediaServer() { }
    public static void handle(SchoolMediaPayloads.Request request, Player actor) {
        if (!(actor instanceof ServerPlayer player)) return;
        ServerLevel level = (ServerLevel) player.level();
        if (request.action() < 0 || request.action() > 2) return;
        long tick = level.getGameTime();
        if (request.action() == 0) {
            Long last = LAST_SYNC.get(player.getUUID());
            if (last != null && tick >= last && tick - last < 20) return;
            LAST_SYNC.put(player.getUUID(), tick);
            send(level, player); return;
        }
        if (LAST_REQUEST.getOrDefault(player.getUUID(), Long.MIN_VALUE) == tick) return;
        LAST_REQUEST.put(player.getUUID(), tick);
        if (player.isSpectator() || !player.mayBuild() || !player.getMainHandItem().isEmpty()
                || !player.getOffhandItem().isEmpty() || !level.hasChunkAt(request.pos())
                || !player.isWithinBlockInteractionRange(request.pos(), 0) || !level.mayInteract(player, request.pos())) return;
        var hit = player.pick(player.blockInteractionRange(), 1, false);
        if (!(hit instanceof BlockHitResult block) || !block.getBlockPos().equals(request.pos())) return;
        if (!player.mayUseItemAt(request.pos(), block.getDirection(), player.getMainHandItem())) return;
        BlockPos target = SchoolMediaDevices.anchor(level, request.pos());
        if (!level.hasChunkAt(target) || !level.mayInteract(player, target)) return;
        Role role = SchoolMediaDevices.role(level.getBlockState(target));
        var links = SchoolMediaLinks.get(level);
        if (request.action() == 2) {
            boolean changed = links.remove(target);
            if (role == Role.SCREEN) {
                SchoolScreenArea area = SchoolScreenArea.find(level, target);
                if (area != null) for (SchoolMediaRoute route : links.routes())
                    if (route.kind() == SchoolMediaRoute.SCREEN && area.contains(route.output())) changed |= links.remove(route.output());
            }
            if (changed) broadcast(level);
            SELECTED.remove(player.getUUID());
            message(player, "disconnected"); return;
        }
        Selection old = SELECTED.get(player.getUUID());
        if (old != null && !old.dimension().equals(level.dimension())) old = null;
        if (SchoolMediaRules.source(role)) {
            SELECTED.put(player.getUUID(), new Selection(level.dimension(), target, null));
            message(player, "source_selected"); return;
        }
        BlockPos input = old == null ? null : role == Role.SCREEN ? old.projector() : old.source();
        if (input == null || !level.hasChunkAt(input) || !level.mayInteract(player, input)) {
            message(player, "select_source"); return;
        }
        Role inputRole = SchoolMediaDevices.role(level.getBlockState(input));
        if (!SchoolMediaRules.canConnect(inputRole, role)) {
            message(player, (role == Role.GENERAL_SPEAKER || role == Role.AUDIO_CONTROLLER) ? "moises_only" : "invalid_connection"); return;
        }
        if (target.distSqr(input) > 64 * 64) { message(player, "too_far"); return; }
        if (role == Role.SPEAKER || role == Role.GENERAL_SPEAKER) {
            BlockPos output = target;
            if (links.routes().stream().filter(r -> r.kind() == SchoolMediaRoute.SPEAKER
                    && r.input().equals(input) && !r.output().equals(output)).count() >= 8) {
                message(player, "limit"); return;
            }
        }
        if (role == Role.SCREEN) {
            SchoolScreenArea area = SchoolScreenArea.find(level, target);
            if (area == null) { message(player, "rectangle_required"); return; }
            for (SchoolMediaRoute route : links.routes())
                if (route.kind() == SchoolMediaRoute.SCREEN && area.contains(route.output())) links.remove(route.output());
            target = area.origin();
        }
        try { links.put(new SchoolMediaRoute(SchoolMediaLinks.kind(role), target, input)); }
        catch (IllegalArgumentException full) { message(player, "limit"); return; }
        if (role == Role.PROJECTOR)
            SELECTED.put(player.getUUID(), new Selection(level.dimension(), old.source(), target));
        broadcast(level);
        message(player, role == Role.PROJECTOR ? "projector_connected" : role == Role.SCREEN ? "screen_connected" : role == Role.AUDIO_CONTROLLER ? "controller_connected" : "speaker_connected");
    }
    public static void settings(SchoolMediaPayloads.AudioSettings request, Player actor) {
        if (!(actor instanceof ServerPlayer player) || player.isSpectator() || !player.mayBuild()
                || !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()
                || !SchoolMediaRules.validAudioSettings(request.volumePercent(), request.speedPercent())) return;
        ServerLevel level = (ServerLevel) player.level();
        BlockPos clicked = request.pos();
        if (!level.hasChunkAt(clicked) || !level.mayInteract(player, clicked)
                || !player.isWithinBlockInteractionRange(clicked, 0)
                || SchoolMediaDevices.role(level.getBlockState(clicked)) != Role.AUDIO_CONTROLLER) return;
        var hit = player.pick(player.blockInteractionRange(), 1, false);
        if (!(hit instanceof BlockHitResult block) || !block.getBlockPos().equals(clicked)
                || !player.mayUseItemAt(clicked, block.getDirection(), player.getMainHandItem())) return;
        BlockPos pos = SchoolMediaDevices.anchor(level, clicked);
        if (!level.hasChunkAt(pos) || !level.mayInteract(player, pos)
                || SchoolMediaDevices.role(level.getBlockState(pos)) != Role.AUDIO_CONTROLLER) return;
        long tick = level.getGameTime();
        if (LAST_REQUEST.getOrDefault(player.getUUID(), Long.MIN_VALUE) == tick) return;
        LAST_REQUEST.put(player.getUUID(), tick);
        var links = SchoolMediaLinks.get(level);
        for (SchoolMediaRoute route : links.routes()) {
            if (route.kind() != SchoolMediaRoute.AUDIO_CONTROLLER || !route.output().equals(pos)) continue;
            if (!level.hasChunkAt(route.input()) || !level.mayInteract(player, route.input())
                    || SchoolMediaDevices.role(level.getBlockState(route.input())) != Role.MOISES_LAPTOP
                    || pos.distSqr(route.input()) > 64 * 64) return;
            links.put(new SchoolMediaRoute(route.kind(), pos, route.input(), request.volumePercent(), request.speedPercent()));
            broadcast(level);
            return;
        }
    }
    private static void message(ServerPlayer player, String key) {
        player.sendOverlayMessage(Component.translatable("message.lsmmod.media." + key));
    }
    private static SchoolMediaPayloads.Snapshot snapshot(ServerLevel level) {
        return new SchoolMediaPayloads.Snapshot(level.dimension().identifier(), SchoolMediaLinks.get(level).routes());
    }
    private static void send(ServerLevel level, ServerPlayer player) { PacketDistributor.sendToPlayer(player, snapshot(level)); }
    private static void broadcast(ServerLevel level) { PacketDistributor.sendToPlayersInDimension(level, snapshot(level)); }
    @SubscribeEvent public static void tick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level && level.getGameTime() % 20 == 0 && SchoolMediaLinks.get(level).prune(level)) broadcast(level);
    }
    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        SELECTED.remove(event.getEntity().getUUID()); LAST_REQUEST.remove(event.getEntity().getUUID()); LAST_SYNC.remove(event.getEntity().getUUID());
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) { SELECTED.clear(); LAST_REQUEST.clear(); LAST_SYNC.clear(); }
}
