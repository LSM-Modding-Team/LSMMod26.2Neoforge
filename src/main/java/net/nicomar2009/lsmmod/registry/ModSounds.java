package net.nicomar2009.lsmmod.registry;

import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = ModRegistries.SOUNDS;

    public record SpeakerTrack(DeferredHolder<SoundEvent, SoundEvent> sound, int durationTicks) {}

    public static final DeferredHolder<SoundEvent, SoundEvent> SCHOOL_BELL =
            SOUNDS.register("school_bell", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "school_bell")));

    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_KICK = net.nicomar2009.lsmmod.modules.balls.registry.ModuleSounds.FOOTBALL_KICK;
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_WALL = net.nicomar2009.lsmmod.modules.balls.registry.ModuleSounds.FOOTBALL_WALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_NET = net.nicomar2009.lsmmod.modules.balls.registry.ModuleSounds.FOOTBALL_NET;
    public static final DeferredHolder<SoundEvent, SoundEvent> FOOTBALL_FRAME = net.nicomar2009.lsmmod.modules.balls.registry.ModuleSounds.FOOTBALL_FRAME;



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

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_DISC = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.MY_DISC;

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_MARCH = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.MY_MARCH;

    public static final DeferredHolder<SoundEvent, SoundEvent> QAPACKACHAMPA = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.QAPACKACHAMPA;

    public static final DeferredHolder<SoundEvent, SoundEvent> COFRADIA_SAN_MIGUEL = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.COFRADIA_SAN_MIGUEL;

    public static final DeferredHolder<SoundEvent, SoundEvent> CARNAVAL_HUACCANA = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.CARNAVAL_HUACCANA;

    public static final DeferredHolder<SoundEvent, SoundEvent> CARNAVAL_HUANUPAMPA = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.CARNAVAL_HUANUPAMPA;

    public static final DeferredHolder<SoundEvent, SoundEvent> CARNAVAL_MACARI = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.CARNAVAL_MACARI;

    public static final DeferredHolder<SoundEvent, SoundEvent> FORMACION = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.FORMACION;

    public static final DeferredHolder<SoundEvent, SoundEvent> HIMNOPERU = net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.HIMNOPERU;

    public static final DeferredHolder<SoundEvent, SoundEvent> RULER_HIT = net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleSounds.RULER_HIT;

    public static final DeferredHolder<SoundEvent, SoundEvent> GUITAR = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.GUITAR;

    public static final DeferredHolder<SoundEvent, SoundEvent> MANDOLIN = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.MANDOLIN;

    public static final DeferredHolder<SoundEvent, SoundEvent> FLUTE = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.FLUTE;

    public static final DeferredHolder<SoundEvent, SoundEvent> TAMBOURINE = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.TAMBOURINE;

    public static final DeferredHolder<SoundEvent, SoundEvent> VIOLIN = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.VIOLIN;

    public static final DeferredHolder<SoundEvent, SoundEvent> MELODICA = net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.MELODICA;
}
