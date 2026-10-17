package net.nicomar2009.lsmmod.modules.chairs.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> ELEMENTARY_CHAIR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.ELEMENTARY_CHAIR);

    public static final DeferredItem<BlockItem> HIGH_SCHOOL_CHAIR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.HIGH_SCHOOL_CHAIR);

    public static final DeferredItem<BlockItem> TEACHERS_CHAIR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.TEACHERS_CHAIR);

    public static final DeferredItem<BlockItem> ENGLISHROOMCHAIR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.ENGLISHROOMCHAIR);

    public static final DeferredItem<BlockItem> HALLCHAIR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.HALLCHAIR);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.PLASTICCHAIR_WHITE);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.PLASTICCHAIR_RED);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE_ARMS =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.PLASTICCHAIR_WHITE_ARMS);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED_ARMS =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.PLASTICCHAIR_RED_ARMS);

    public static final DeferredItem<BlockItem> DINING_CHAIR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_CHAIR);

    public static final DeferredItem<BlockItem> SOFA =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.SOFA);

    public static final DeferredItem<BlockItem> STOOL =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.STOOL);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
