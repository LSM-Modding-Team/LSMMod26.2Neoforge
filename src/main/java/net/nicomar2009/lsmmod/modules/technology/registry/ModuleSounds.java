package net.nicomar2009.lsmmod.modules.technology.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.SOUNDS;

public final class ModuleSounds {
    public static final DeferredHolder<SoundEvent, SoundEvent> SCHOOL_BELL =
            SOUNDS.register("school_bell", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "school_bell")));

    public static final List<ModuleSounds.SpeakerTrack> PORTABLE_SPEAKER_TRACKS = List.of(
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

    private static ModuleSounds.SpeakerTrack speakerTrack(String name, int durationTicks) {
        String id = "portable_speaker." + name;
        return new ModuleSounds.SpeakerTrack(SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, id))), durationTicks);
    }
    public record SpeakerTrack(DeferredHolder<SoundEvent, SoundEvent> sound, int durationTicks) {}

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleSounds() {}
}
