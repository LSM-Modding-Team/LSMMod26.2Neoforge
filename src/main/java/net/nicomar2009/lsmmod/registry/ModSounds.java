package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;

/** Binds module-owned registrations; add content in the owning module. */
public final class ModSounds {
    private ModSounds() {}

    public static void register(IEventBus modEventBus) {
        net.nicomar2009.lsmmod.modules.technology.registry.ModuleSounds.initialize();
        net.nicomar2009.lsmmod.modules.balls.registry.ModuleSounds.initialize();
        net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleSounds.initialize();
        net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleSounds.initialize();
        net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleSounds.initialize();
        ModRegistries.SOUNDS.register(modEventBus);
    }
}
