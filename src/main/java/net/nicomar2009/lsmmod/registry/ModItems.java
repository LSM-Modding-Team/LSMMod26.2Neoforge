package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;

/** Binds module-owned registrations; add content in the owning module. */
public final class ModItems {
    private ModItems() {}

    public static void register(IEventBus modEventBus) {
        net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.facilities.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.shelving.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.wall_blocks.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.special_blocks.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.technology.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.school_supplies.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.slopes.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.lighting.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry.ModuleCurtainItems.initialize();
        net.nicomar2009.lsmmod.modules.balls.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.backpacks.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.books_notebooks_and_folders.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.chairs.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.music_discs.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.musical_instruments.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.awnings.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.npcs.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.uniforms.registry.ModuleItems.initialize();
        net.nicomar2009.lsmmod.modules.building_blocks.registry.ModuleItems.addAliases();
        ModRegistries.ITEMS.register(modEventBus);
    }
}
