package net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CONNECTED_TINTED_GLASS);

    public static final DeferredItem<BlockItem> SCHOOL_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PROJECTING_WINDOW);

    public static final DeferredItem<BlockItem> SCHOOL_SLIDING_ENTRANCE = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_SLIDING_ENTRANCE);

    public static final DeferredItem<BlockItem> SCHOOL_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_GLASS);

    public static final DeferredItem<BlockItem> SCHOOL_CONTINUOUS_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CONTINUOUS_GLASS);

    public static final DeferredItem<BlockItem> SCHOOL_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_TINTED_GLASS);

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem> SOLID_ASSEMBLY_DOOR = ITEMS.registerItem("school_solid_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem(ModuleBlocks.SOLID_ASSEMBLY_DOOR.get(),props));
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem> DINING_ASSEMBLY_DOOR = ITEMS.registerItem("school_dining_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem(ModuleBlocks.DINING_ASSEMBLY_DOOR.get(),props));
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem> WINDOWED_PANEL_ASSEMBLY_DOOR = ITEMS.registerItem("school_windowed_panel_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem(ModuleBlocks.WINDOWED_PANEL_ASSEMBLY_DOOR.get(),props));
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem> DOUBLE_WINDOWED_ASSEMBLY_DOOR = ITEMS.registerItem("school_double_windowed_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorItem(ModuleBlocks.DOUBLE_WINDOWED_ASSEMBLY_DOOR.get(),props));

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
