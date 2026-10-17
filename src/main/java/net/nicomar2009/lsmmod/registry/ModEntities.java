package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;

/** Binds module-owned registrations; add content in the owning module. */
public final class ModEntities {
    private ModEntities() {}

    public static void register(IEventBus modEventBus) {
        net.nicomar2009.lsmmod.modules.chairs.registry.ModuleEntities.initialize();
        net.nicomar2009.lsmmod.modules.facilities.registry.ModuleEntities.initialize();
        net.nicomar2009.lsmmod.modules.npcs.registry.ModuleEntities.initialize();
        net.nicomar2009.lsmmod.modules.balls.registry.ModuleEntities.initialize();
        ModRegistries.ENTITIES.register(modEventBus);
    }
}
