package net.nicomar2009.lsmmod.modules.tables_and_desks.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> DINING_ROOM_COUNTER = ITEMS.registerSimpleBlockItem(ModBlocks.DINING_ROOM_COUNTER);

    public static final DeferredItem<BlockItem> STUDENTS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.STUDENTS_DESK);

    public static final DeferredItem<BlockItem> TEACHERS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_DESK);

    public static final DeferredItem<BlockItem> ELEMENTARY_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_DESK);

    public static final DeferredItem<BlockItem> COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.COMPUTER_DESK);

    public static final DeferredItem<BlockItem> GRAY_COMPUTER_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.GRAY_COMPUTER_DESK);

    public static final DeferredItem<BlockItem> DINING_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.DINING_TABLE);

    public static final DeferredItem<BlockItem> KIOSK_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.KIOSK_TABLE);

    public static final DeferredItem<BlockItem> ART_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.ART_TABLE);
    private ModuleItems() {}
}
