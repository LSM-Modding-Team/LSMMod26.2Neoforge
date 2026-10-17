package net.nicomar2009.lsmmod.modules.musical_instruments.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.SOUNDS;

public final class ModuleSounds {
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

    private ModuleSounds() {}
}
