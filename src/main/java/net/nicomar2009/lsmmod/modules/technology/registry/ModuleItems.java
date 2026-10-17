package net.nicomar2009.lsmmod.modules.technology.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.technology.item.PortableSpeakerItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> CEILING_PROJECTOR = ITEMS.registerSimpleBlockItem(ModBlocks.CEILING_PROJECTOR);

    public static final DeferredItem<BlockItem> SCHOOL_CABINET = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CABINET);

    public static final DeferredItem<BlockItem> SCHOOL_ROUTER = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_ROUTER);

    public static final DeferredItem<BlockItem> PROJECTOR_SCREEN =
            ITEMS.registerSimpleBlockItem(ModBlocks.PROJECTOR_SCREEN);

    public static final DeferredItem<BlockItem> WALL_PROJECTOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.WALL_PROJECTOR);

    public static final DeferredItem<BlockItem> LAPTOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.LAPTOP);

    public static final DeferredItem<BlockItem> MOISES_LAPTOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.MOISES_LAPTOP);

    public static final DeferredItem<BlockItem> PC =
            ITEMS.registerSimpleBlockItem(ModBlocks.PC);

    public static final DeferredItem<BlockItem> SCHOOL_BELL =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_BELL);

    public static final DeferredItem<BlockItem> SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.SPEAKER);

    public static final DeferredItem<BlockItem> GENERAL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.GENERAL_SPEAKER);

    public static final DeferredItem<BlockItem> TALL_SPEAKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.TALL_SPEAKER);

    public static final DeferredItem<Item> PORTABLE_SPEAKER =
            ITEMS.registerItem("portable_speaker", PortableSpeakerItem::new);
    private ModuleItems() {}
}
