package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;

/** Binds module-owned registrations; add content in the owning module. */
public final class ModBlocks {
    private ModBlocks() {}

    public static void register(IEventBus modEventBus) {
        net.nicomar2009.lsmmod.modules.slopes.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.facilities.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.shelving.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.technology.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.lighting.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.balls.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.backpacks.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.chairs.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.awnings.registry.ModuleBlocks.initialize();
        net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleBlocks.addAliases();
        ModRegistries.BLOCKS.register(modEventBus);
    }
}
