package net.nicomar2009.lsmmod.modules.lockers_and_storage.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> DINING_WALL_CABINET = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_WALL_CABINET);

    public static final DeferredItem<BlockItem> NIDO_STORAGE_BUCKET = ITEMS.registerSimpleBlockItem(ModBlocks.NIDO_STORAGE_BUCKET);

    public static final DeferredItem<BlockItem> LOST_AND_FOUND_BOX = ITEMS.registerSimpleBlockItem(ModBlocks.LOST_AND_FOUND_BOX);

    public static final DeferredItem<BlockItem> CHILDREN_LOCKER = ITEMS.registerSimpleBlockItem(ModBlocks.CHILDREN_LOCKER);

    public static final DeferredItem<BlockItem> LOCKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.LOCKER);
    private ModuleItems() {}
}
