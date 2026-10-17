package net.nicomar2009.lsmmod.modules.tables_and_desks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> DINING_ROOM_COUNTER = ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_ROOM_COUNTER);

    public static final DeferredItem<BlockItem> STUDENTS_DESK =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.STUDENTS_DESK);

    public static final DeferredItem<BlockItem> TEACHERS_DESK =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.TEACHERS_DESK);

    public static final DeferredItem<BlockItem> ELEMENTARY_DESK =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.ELEMENTARY_DESK);

    public static final DeferredItem<BlockItem> COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.COMPUTER_DESK);

    public static final DeferredItem<BlockItem> GRAY_COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.GRAY_COMPUTER_DESK);

    public static final DeferredItem<BlockItem> DINING_TABLE =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.DINING_TABLE);

    public static final DeferredItem<BlockItem> KIOSK_TABLE =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.KIOSK_TABLE);

    public static final DeferredItem<BlockItem> ART_TABLE =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.ART_TABLE);
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
