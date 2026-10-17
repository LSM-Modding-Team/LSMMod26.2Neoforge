package net.nicomar2009.lsmmod.modules.lockers_and_storage.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> DINING_WALL_CABINET = ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_WALL_CABINET);

    public static final DeferredItem<BlockItem> NIDO_STORAGE_BUCKET = ITEMS.registerSimpleBlockItem(ModuleBlocks.NIDO_STORAGE_BUCKET);

    public static final DeferredItem<BlockItem> LOST_AND_FOUND_BOX = ITEMS.registerSimpleBlockItem(ModuleBlocks.LOST_AND_FOUND_BOX);

    public static final DeferredItem<BlockItem> CHILDREN_LOCKER = ITEMS.registerSimpleBlockItem(ModuleBlocks.CHILDREN_LOCKER);

    public static final DeferredItem<BlockItem> LOCKER =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.LOCKER);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
