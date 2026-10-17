package net.nicomar2009.lsmmod.modules.music_discs.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.SOUNDS;

public final class ModuleSounds {
    public static final DeferredHolder<SoundEvent, SoundEvent> MY_DISC =
            SOUNDS.register("music_disc.lsm_anthem", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_anthem")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_MARCH =
            SOUNDS.register("music_disc.lsm_march", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_march")));

    public static final DeferredHolder<SoundEvent, SoundEvent> QAPACKACHAMPA =
            SOUNDS.register("music_disc.qapackachampa", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.qapackachampa")));

    public static final DeferredHolder<SoundEvent, SoundEvent> COFRADIA_SAN_MIGUEL =
            SOUNDS.register("music_disc.cofradia_san_miguel", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.cofradia_san_miguel")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CARNAVAL_HUACCANA =
            SOUNDS.register("music_disc.carnaval_huaccana", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.carnaval_huaccana")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CARNAVAL_HUANUPAMPA =
            SOUNDS.register("music_disc.carnaval_huanupampa", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.carnaval_huanupampa")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CARNAVAL_MACARI =
            SOUNDS.register("music_disc.carnaval_macari", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.carnaval_macari")));

    public static final DeferredHolder<SoundEvent, SoundEvent> FORMACION =
            SOUNDS.register("music_disc.formacion", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.formacion")));

    public static final DeferredHolder<SoundEvent, SoundEvent> HIMNOPERU =
            SOUNDS.register("music_disc.himnoperu", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.himnoperu")));

    private ModuleSounds() {}
}
