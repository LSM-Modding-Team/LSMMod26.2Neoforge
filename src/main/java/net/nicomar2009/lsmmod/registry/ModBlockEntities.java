package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;

/** Binds module-owned registrations; add content in the owning module. */
public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static void register(IEventBus modEventBus) {
        net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.initialize();
        net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlockEntities.initialize();
        net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlockEntities.initialize();
        net.nicomar2009.lsmmod.modules.awnings.registry.ModuleBlockEntities.initialize();
        ModRegistries.BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlockEntities::registerCapabilities);
    }
}
