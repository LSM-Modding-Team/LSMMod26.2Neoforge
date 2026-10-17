package net.nicomar2009.lsmmod.modules.school_supplies.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.SOUNDS;

public final class ModuleSounds {
    public static final DeferredHolder<SoundEvent, SoundEvent> RULER_HIT =
            SOUNDS.register("ruler_hit", () ->
                    SoundEvent.createVariableRangeEvent(
                            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "ruler_hit")));

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleSounds() {}
}
