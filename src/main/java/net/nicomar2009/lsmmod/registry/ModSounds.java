package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, LSMMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_DISC =
            SOUNDS.register("music_disc.lsm_anthem", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_anthem")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MY_MARCH =
            SOUNDS.register("music_disc.lsm_march", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "music_disc.lsm_march")));

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