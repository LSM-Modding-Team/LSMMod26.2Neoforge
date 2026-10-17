package net.nicomar2009.lsmmod.modules.technology.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.technology.item.PortableSpeakerItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> CEILING_PROJECTOR = ITEMS.registerSimpleBlockItem(ModuleBlocks.CEILING_PROJECTOR);

    public static final DeferredItem<BlockItem> SCHOOL_CABINET = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CABINET);

    public static final DeferredItem<BlockItem> SCHOOL_ROUTER = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_ROUTER);

    public static final DeferredItem<BlockItem> PROJECTOR_SCREEN =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.PROJECTOR_SCREEN);

    public static final DeferredItem<BlockItem> WALL_PROJECTOR =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.WALL_PROJECTOR);

    public static final DeferredItem<BlockItem> LAPTOP =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.LAPTOP);

    public static final DeferredItem<BlockItem> MOISES_LAPTOP =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.MOISES_LAPTOP);

    public static final DeferredItem<BlockItem> PC =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.PC);

    public static final DeferredItem<BlockItem> SCHOOL_BELL =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_BELL);

    public static final DeferredItem<BlockItem> SPEAKER =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.SPEAKER);

    public static final DeferredItem<BlockItem> GENERAL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.GENERAL_SPEAKER);

    public static final DeferredItem<BlockItem> TALL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.TALL_SPEAKER);

    public static final DeferredItem<Item> PORTABLE_SPEAKER =
            ITEMS.registerItem("portable_speaker", PortableSpeakerItem::new);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
