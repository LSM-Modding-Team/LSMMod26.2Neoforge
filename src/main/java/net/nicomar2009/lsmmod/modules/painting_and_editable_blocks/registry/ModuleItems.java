package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_GLASS_STAIRS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_CONTINUOUS_GLASS_STAIRS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_CONTINUOUS_GLASS_STAIRS);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_CUSTOM_BLOCK = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_CUSTOM_BLOCK);

    public static final DeferredItem<BlockItem> SCHOOL_PAINTABLE_BLOCK = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PAINTABLE_BLOCK);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
