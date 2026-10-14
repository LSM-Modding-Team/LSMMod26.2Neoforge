package net.nicomar2009.lsmmod.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.network.SpeakerSoundPayload;
import net.nicomar2009.lsmmod.registry.ModSounds;

/** Moves an existing audio stream instead of restarting it on inventory transfers. */
@EventBusSubscriber(modid = LSMMod.MOD_ID, value = Dist.CLIENT)
public final class PortableSpeakerSounds {
    private static final Map<UUID, MovingClip> PLAYING = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(SpeakerSoundPayload.TYPE, (payload, context) -> receive(payload));
    }

    @SubscribeEvent
    public static void cleanup(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        for (MovingClip clip : java.util.List.copyOf(PLAYING.values())) {
            if (client.level != clip.level || clip.isStopped()) clip.finish();
        }
    }

    private static void receive(SpeakerSoundPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        MovingClip current = PLAYING.get(payload.id());
        if (payload.remaining() <= 0 || !client.level.dimension().identifier().toString().equals(payload.dimension())) {
            if (current != null) current.finish();
            return;
        }
        if (payload.start() && payload.track() >= 0 && payload.track() < ModSounds.PORTABLE_SPEAKER_TRACKS.size()) {
            if (current != null) current.finish();
            current = new MovingClip(payload);
            PLAYING.put(payload.id(), current);
            client.getSoundManager().play(current);
        } else if (current != null) {
            current.move(payload);
        }
    }

    private static final class MovingClip extends AbstractTickableSoundInstance {
        private final UUID id;
        private final Object level;
        private final String dimension;
        private int remaining;

        MovingClip(SpeakerSoundPayload payload) {
            super(ModSounds.PORTABLE_SPEAKER_TRACKS.get(payload.track()).sound().get(),
                    SoundSource.RECORDS, RandomSource.create());
            id = payload.id();
            level = Minecraft.getInstance().level;
            dimension = payload.dimension();
            volume = 1.0F;
            pitch = 1.0F;
            move(payload);
        }

        void move(SpeakerSoundPayload payload) {
            x = payload.x();
            y = payload.y();
            z = payload.z();
            remaining = payload.remaining();
        }

        void finish() {
            stop();
            PLAYING.remove(id, this);
        }

        @Override
        public boolean canStartSilent() { return true; }

        @Override
        public void tick() {
            Minecraft client = Minecraft.getInstance();
            if (--remaining <= 0 || client.level != level || client.level == null
                    || !client.level.dimension().identifier().toString().equals(dimension)) finish();
        }
    }
}
