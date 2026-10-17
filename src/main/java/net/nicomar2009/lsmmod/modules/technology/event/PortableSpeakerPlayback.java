package net.nicomar2009.lsmmod.modules.technology.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.technology.item.PortableSpeakerItem;
import net.nicomar2009.lsmmod.modules.technology.network.SpeakerSoundPayload;

/** Finds the actual stack, including inside nested portable containers. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class PortableSpeakerPlayback {
    public static final String ID_KEY = "lsmmod_speaker_source";
    private static final Map<UUID, Playback> ACTIVE = new HashMap<>();
    private static final Map<ServerLevel, Set<LevelChunk>> CHUNKS = new ConcurrentHashMap<>();

    public static UUID id(ItemStack stack) {
        String raw = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getStringOr(ID_KEY, "");
        try { return UUID.fromString(raw); }
        catch (IllegalArgumentException ignored) { return UUID.randomUUID(); }
    }

    public static boolean isPlaying(UUID id) { return ACTIVE.containsKey(id); }

    public record AudibleSource(UUID id, UUID clipId, Vec3 position, boolean heldByPlayer) {}

    public static boolean isClipPlaying(UUID clipId) {
        return ACTIVE.values().stream().anyMatch(playback -> playback.clipId.equals(clipId));
    }

    /** Fixed cylindrical hearing range, independent of NPC perception stats. */
    public static AudibleSource nearestSource(ServerLevel level, Vec3 position) {
        AudibleSource nearest = null;
        double best = Double.MAX_VALUE;
        for (Playback playback : ACTIVE.values()) {
            if (playback.remaining <= 0 || playback.missing > 0 || playback.last.level() != level) continue;
            Vec3 source = playback.last.position();
            double dx = source.x() - position.x(), dz = source.z() - position.z();
            double horizontal = dx * dx + dz * dz;
            if (horizontal > 20 * 20 || Math.abs(source.y() - position.y()) > 6) continue;
            double distance = source.distanceToSqr(position);
            if (distance < best) {
                boolean held = playback.holder instanceof ServerPlayer player
                        && (containsDirectSpeaker(player.getMainHandItem(), playback.id)
                            || containsDirectSpeaker(player.getOffhandItem(), playback.id));
                nearest = new AudibleSource(playback.id, playback.clipId, source, held);
                best = distance;
            }
        }
        return nearest;
    }

    private static boolean containsDirectSpeaker(ItemStack stack, UUID id) {
        return stack.getItem() instanceof PortableSpeakerItem
                && id.toString().equals(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                        .copyTag().getStringOr(ID_KEY, ""));
    }

    public static void start(ServerPlayer player, UUID id, int track, int duration) {
        Playback playback = new Playback(player.level().getServer(), id, track, duration, player);
        ACTIVE.put(id, playback);
        Location location = new Location((ServerLevel) player.level(), player.position());
        // Keep each listener's stream alive while its spatial source moves away/back.
        for (ServerPlayer listener : player.level().getServer().getPlayerList().getPlayers()) {
            if (listener.level() == player.level() && listener.position().distanceToSqr(location.position()) <= 64 * 64) {
                playback.listeners.add(listener);
                PacketDistributor.sendToPlayer(listener, playback.packet(location, true));
            }
        }
    }

    @SubscribeEvent
    public static void loaded(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level)
            CHUNKS.computeIfAbsent(level, key -> ConcurrentHashMap.newKeySet()).add(event.getChunk());
    }

    @SubscribeEvent
    public static void unloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Set<LevelChunk> chunks = CHUNKS.get(level);
            if (chunks != null) chunks.remove(event.getChunk());
        }
    }

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent event) {
        ACTIVE.values().removeIf(playback -> playback.server == event.getServer());
        CHUNKS.keySet().removeIf(level -> level.getServer() == event.getServer());
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        var iterator = ACTIVE.values().iterator();
        while (iterator.hasNext()) {
            Playback playback = iterator.next();
            if (playback.server != event.getServer()) continue;
            Location location = playback.locate();
            if (location == null) playback.missing++;
            else playback.missing = 0;
            if (--playback.remaining <= 0 || playback.missing > 5) {
                playback.remaining = 0;
                Location last = location == null ? playback.last : location;
                for (ServerPlayer listener : playback.listeners)
                    PacketDistributor.sendToPlayer(listener, playback.packet(last, false));
                iterator.remove();
            } else if (location != null) {
                playback.last = location;
                for (ServerPlayer listener : playback.listeners)
                    PacketDistributor.sendToPlayer(listener, playback.packet(location, false));
            }
        }
    }

    private record Location(ServerLevel level, Vec3 position) {}

    private static boolean contains(ItemStack stack, UUID id, int depth) {
        if (stack.isEmpty() || depth > 32) return false;
        if (stack.getItem() instanceof PortableSpeakerItem) {
            String raw = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getStringOr(ID_KEY, "");
            if (id.toString().equals(raw)) return true;
        }
        var contents = stack.get(DataComponents.CONTAINER);
        if (contents != null) {
            for (var item : contents.nonEmptyItems())
                if (contains(item.create(), id, depth + 1)) return true;
        }
        var bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            for (var item : bundle.items())
                if (contains(item.create(), id, depth + 1)) return true;
        }
        if (contents == null && bundle == null) {
            var storage = ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM);
            if (storage != null && contains(storage, id, depth + 1)) return true;
        }
        return false;
    }

    private static boolean contains(ResourceHandler<ItemResource> storage, UUID id, int depth) {
        for (int slot = 0; slot < storage.size(); slot++) {
            if (storage.getAmountAsLong(slot) > 0 && contains(storage.getResource(slot).toStack(), id, depth)) return true;
        }
        return false;
    }

    private static boolean contains(Container container, UUID id) {
        for (int slot = 0; slot < container.getContainerSize(); slot++)
            if (contains(container.getItem(slot), id, 0)) return true;
        return false;
    }

    private static Location at(Object holder, UUID id) {
        if (holder instanceof ServerPlayer player && !player.isRemoved()
                && (contains(player.getInventory(), id) || contains(player.containerMenu.getCarried(), id, 0)))
            return new Location((ServerLevel) player.level(), player.position());
        if (holder instanceof ItemEntity item && !item.isRemoved() && contains(item.getItem(), id, 0))
            return new Location((ServerLevel) item.level(), item.position());
        if (holder instanceof Entity entity && holder instanceof Container container
                && !entity.isRemoved() && contains(container, id))
            return new Location((ServerLevel) entity.level(), entity.position());
        if (holder instanceof BlockEntity block && holder instanceof Container container && !block.isRemoved()
                && block.getLevel() instanceof ServerLevel level && level.hasChunkAt(block.getBlockPos())
                && contains(container, id))
            return new Location(level, Vec3.atCenterOf(block.getBlockPos()));
        if (holder instanceof BlockEntity block && !(holder instanceof Container) && !block.isRemoved()
                && block.getLevel() instanceof ServerLevel level && level.hasChunkAt(block.getBlockPos())) {
            var storage = level.getCapability(Capabilities.Item.BLOCK, block.getBlockPos(), null);
            if (storage != null && contains(storage, id, 0))
                return new Location(level, Vec3.atCenterOf(block.getBlockPos()));
            for (Direction side : Direction.values()) {
                storage = level.getCapability(Capabilities.Item.BLOCK, block.getBlockPos(), side);
                if (storage != null && contains(storage, id, 0))
                    return new Location(level, Vec3.atCenterOf(block.getBlockPos()));
            }
        }
        if (holder instanceof Entity entity && !entity.isRemoved() && !(entity instanceof ServerPlayer)) {
            if (entity instanceof LivingEntity living) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (contains(living.getItemBySlot(slot), id, 0))
                        return new Location((ServerLevel) entity.level(), entity.position());
                }
            }
            var storage = Capabilities.Item.ENTITY.getCapability(entity, null);
            if (storage != null && contains(storage, id, 0))
                return new Location((ServerLevel) entity.level(), entity.position());
        }
        return null;
    }

    private static final class Playback {
        final MinecraftServer server;
        final UUID id;
        final UUID clipId = UUID.randomUUID();
        final int track;
        final List<ServerPlayer> listeners = new ArrayList<>();
        int remaining;
        int missing;
        Object holder;
        Location last;

        Playback(MinecraftServer server, UUID id, int track, int duration, ServerPlayer player) {
            this.server = server;
            this.id = id;
            this.track = track;
            remaining = duration;
            holder = player;
            last = new Location((ServerLevel) player.level(), player.position());
        }

        SpeakerSoundPayload packet(Location location, boolean start) {
            return new SpeakerSoundPayload(id, location.level().dimension().identifier().toString(), track,
                    remaining, start, location.position().x(), location.position().y(), location.position().z());
        }

        Location locate() {
            Location cached = at(holder, id);
            if (cached != null) return cached;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Location found = at(player, id);
                if (found != null) { holder = player; return found; }
            }
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    Location found = at(entity, id);
                    if (found != null) { holder = entity; return found; }
                }
                for (LevelChunk chunk : CHUNKS.getOrDefault(level, Set.of())) {
                    for (BlockEntity block : chunk.getBlockEntities().values()) {
                        Location found = at(block, id);
                        if (found != null) { holder = block; return found; }
                    }
                }
            }
            return null;
        }
    }
}
