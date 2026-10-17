package net.nicomar2009.lsmmod.modules.facilities.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_REFRIGERATOR = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_REFRIGERATOR);

    public static final DeferredItem<BlockItem> SCHOOL_MICROWAVE = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_MICROWAVE);

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN_RAISED = ITEMS.registerSimpleBlockItem(ModBlocks.DRINKING_FOUNTAIN_RAISED);

    public static final DeferredItem<BlockItem> SCHOOL_DRAIN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_DRAIN);

    public static final DeferredItem<BlockItem> SCHOOL_SINK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SINK);

    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_SINK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CONNECTED_SINK);

    public static final DeferredItem<BlockItem> TOILET =
            ITEMS.registerSimpleBlockItem(ModBlocks.TOILET);

    public static final DeferredItem<BlockItem> ADULT_MEN_URINAL =
            ITEMS.registerSimpleBlockItem(ModBlocks.ADULT_MEN_URINAL);

    public static final DeferredItem<BlockItem> STANDALONE_TOILET = ITEMS.registerSimpleBlockItem(ModBlocks.STANDALONE_TOILET);

    public static final DeferredItem<BlockItem> MEN_URINAL =
            ITEMS.registerSimpleBlockItem(ModBlocks.MEN_URINAL);

    public static final DeferredItem<BlockItem> GREEN_TRASH_BIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.GREEN_TRASH_BIN);

    public static final DeferredItem<BlockItem> BLACK_TRASH_BIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.BLACK_TRASH_BIN);

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN =
            ITEMS.registerSimpleBlockItem(ModBlocks.DRINKING_FOUNTAIN);

    public static final DeferredItem<BlockItem> INFIRMARY_COT =
            ITEMS.registerSimpleBlockItem(ModBlocks.INFIRMARY_COT);
    private ModuleItems() {}
}
