package net.nicomar2009.lsmmod.modules.facilities.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_REFRIGERATOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_REFRIGERATOR);

    public static final DeferredItem<BlockItem> SCHOOL_MICROWAVE = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_MICROWAVE);

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN_RAISED = ITEMS.registerSimpleBlockItem(ModuleBlocks.DRINKING_FOUNTAIN_RAISED);

    public static final DeferredItem<BlockItem> SCHOOL_DRAIN = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_DRAIN);

    public static final DeferredItem<BlockItem> SCHOOL_SINK = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_SINK);

    public static final DeferredItem<BlockItem> SCHOOL_CONNECTED_SINK = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CONNECTED_SINK);

    public static final DeferredItem<BlockItem> TOILET =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.TOILET);

    public static final DeferredItem<BlockItem> ADULT_MEN_URINAL =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.ADULT_MEN_URINAL);

    public static final DeferredItem<BlockItem> STANDALONE_TOILET = ITEMS.registerSimpleBlockItem(ModuleBlocks.STANDALONE_TOILET);

    public static final DeferredItem<BlockItem> MEN_URINAL =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.MEN_URINAL);

    public static final DeferredItem<BlockItem> GREEN_TRASH_BIN =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.GREEN_TRASH_BIN);

    public static final DeferredItem<BlockItem> BLACK_TRASH_BIN =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.BLACK_TRASH_BIN);

    public static final DeferredItem<BlockItem> DRINKING_FOUNTAIN =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.DRINKING_FOUNTAIN);

    public static final DeferredItem<BlockItem> INFIRMARY_COT =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.INFIRMARY_COT);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
