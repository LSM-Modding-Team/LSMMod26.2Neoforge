package net.nicomar2009.lsmmod.modules.chairs.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> ELEMENTARY_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_CHAIR);

    public static final DeferredItem<BlockItem> HIGH_SCHOOL_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HIGH_SCHOOL_CHAIR);

    public static final DeferredItem<BlockItem> TEACHERS_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_CHAIR);

    public static final DeferredItem<BlockItem> ENGLISHROOMCHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ENGLISHROOMCHAIR);

    public static final DeferredItem<BlockItem> HALLCHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HALLCHAIR);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_WHITE);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_RED);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_WHITE_ARMS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_WHITE_ARMS);

    public static final DeferredItem<BlockItem> PLASTICCHAIR_RED_ARMS =
            ITEMS.registerSimpleBlockItem(ModBlocks.PLASTICCHAIR_RED_ARMS);

    public static final DeferredItem<BlockItem> DINING_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_CHAIR);

    public static final DeferredItem<BlockItem> SOFA =
            ITEMS.registerSimpleBlockItem(ModBlocks.SOFA);

    public static final DeferredItem<BlockItem> STOOL =
            ITEMS.registerSimpleBlockItem(ModBlocks.STOOL);
    private ModuleItems() {}
}
