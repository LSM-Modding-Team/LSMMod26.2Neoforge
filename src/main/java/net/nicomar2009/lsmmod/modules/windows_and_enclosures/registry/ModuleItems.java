package net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CONNECTED_TINTED_GLASS);

    public static final DeferredItem<BlockItem> SCHOOL_PROJECTING_WINDOW = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_PROJECTING_WINDOW);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE_2X4 = ITEMS.registerSimpleBlockItem(ModuleBlocks.BATHROOM_DOOR_WIDE_2X4);

    public static final DeferredItem<BlockItem> DINING_DOOR_WIDE = ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_DOOR_WIDE);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR_WIDE = ITEMS.registerSimpleBlockItem(ModuleBlocks.BATHROOM_DOOR_WIDE);

    public static final DeferredItem<BlockItem> INFIRMARY_DOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.INFIRMARY_DOOR);

    public static final DeferredItem<BlockItem> DINING_DOOR_WIDE_LIGHT = ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_DOOR_WIDE_LIGHT);

    public static final DeferredItem<BlockItem> SCHOOL_WOOD_DOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_WOOD_DOOR);

    public static final DeferredItem<BlockItem> SCHOOL_SLIDING_ENTRANCE = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_SLIDING_ENTRANCE);

    public static final DeferredItem<BlockItem> SCHOOL_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_GLASS);

    public static final DeferredItem<BlockItem> DOUBLE_SCHOOL_ENTRANCE = ITEMS.registerSimpleBlockItem(ModuleBlocks.DOUBLE_SCHOOL_ENTRANCE);

    public static final DeferredItem<BlockItem> TALL_CLASSROOM_ENTRANCE = ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_CLASSROOM_ENTRANCE);

    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR_FULL_3 = ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_BATHROOM_DOOR_FULL_3);

    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR_FULL_4 = ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_BATHROOM_DOOR_FULL_4);

    public static final DeferredItem<BlockItem> SCHOOL_TINTED_GLASS = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_TINTED_GLASS);

    public static final DeferredItem<BlockItem> TALL_BATHROOM_DOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_BATHROOM_DOOR);

    public static final DeferredItem<BlockItem> TALL_TEACHERS_OFFICE_DOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_TEACHERS_OFFICE_DOOR);

    public static final DeferredItem<BlockItem> TALL_DINING_DOOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_DINING_DOOR);

    public static final DeferredItem<BlockItem> CLASSROOM_DOOR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.CLASSROOM_DOOR);

    public static final DeferredItem<BlockItem> DINING_DOOR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_DOOR);

    public static final DeferredItem<BlockItem> TEACHERS_OFFICE_DOOR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.TEACHERS_OFFICE_DOOR);

    public static final DeferredItem<BlockItem> BATHROOM_DOOR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.BATHROOM_DOOR);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
