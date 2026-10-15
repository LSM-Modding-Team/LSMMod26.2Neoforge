package net.nicomar2009.lsmmod.registry;

import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, LSMMod.MOD_ID);

    public record SpeakerTrack(DeferredHolder<SoundEvent, SoundEvent> sound, int durationTicks) {}

    public static final DeferredHolder<SoundEvent, SoundEvent> SCHOOL_BELL =
            SOUNDS.register("school_bell", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "school_bell")));

    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_KICK = footballSound("kick");
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_WALL = footballSound("wall");
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_NET = footballSound("net");
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_FRAME = footballSound("frame");

    private static DeferredHolder<SoundEvent, SoundEvent> footballSound(String name) {
        String id = "football." + name;
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, id)));
    }

    public static final List<SpeakerTrack> PORTABLE_SPEAKER_TRACKS = List.of(
            speakerTrack("bby_wow", 340),
            speakerTrack("delincuente", 200),
            speakerTrack("bailan_rochas_y_chetas", 260),
            speakerTrack("4k", 240),
            speakerTrack("el_efecto", 450),
            speakerTrack("pa_que_retozen", 200),
            speakerTrack("equis", 255),
            speakerTrack("si_te_dejas_llevar", 346),
            speakerTrack("cuatro_babys", 308),
            speakerTrack("dile_que_tu_me_quieres", 380),
            speakerTrack("se_preparo", 220));

    private static SpeakerTrack speakerTrack(String name, int durationTicks) {
        String id = "portable_speaker." + name;
        return new SpeakerTrack(SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, id))), durationTicks);
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_DISC =
            SOUNDS.register("music_disc.lsm_anthem", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_anthem")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_MARCH =
            SOUNDS.register("music_disc.lsm_march", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_march")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RULER_HIT =
            SOUNDS.register("ruler_hit", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "ruler_hit")));

    public static final DeferredHolder<SoundEvent, SoundEvent> GUITAR =
            SOUNDS.register("guitar", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "guitar")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MANDOLIN =
            SOUNDS.register("mandolin", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "mandolin")));

    public static final DeferredHolder<SoundEvent, SoundEvent> FLUTE =
            SOUNDS.register("flute", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "flute")));

    public static final DeferredHolder<SoundEvent, SoundEvent> TAMBOURINE =
            SOUNDS.register("tambourine", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "tambourine")));

    public static final DeferredHolder<SoundEvent, SoundEvent> VIOLIN =
            SOUNDS.register("violin", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "violin")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MELODICA =
            SOUNDS.register("melodica", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "melodica")));
}
