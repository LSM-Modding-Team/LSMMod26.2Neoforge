package net.nicomar2009.lsmmod.registry;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;

/** Item registry. Today it only holds the block items of the 7 blocks; standalone items are planned. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LSMMod.MOD_ID);

    // Block item for the elementary chair (registry name taken from the block: "lsmmod:elementary_chair")
    public static final DeferredItem<BlockItem> ELEMENTARY_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_CHAIR);

    // Block item for the high school chair (registry name taken from the block: "lsmmod:high_school_chair")
    public static final DeferredItem<BlockItem> HIGH_SCHOOL_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.HIGH_SCHOOL_CHAIR);

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> STUDENTS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.STUDENTS_DESK);

    // Block item for the locker
    public static final DeferredItem<BlockItem> LOCKER =
            ITEMS.registerSimpleBlockItem(ModBlocks.LOCKER);

    // Block item for the teachers chair (registry name taken from the block: "lsmmod:teachers_chair")
    public static final DeferredItem<BlockItem> TEACHERS_CHAIR =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_CHAIR);

    // Block item for the desk (placing it creates both halves)
    public static final DeferredItem<BlockItem> TEACHERS_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEACHERS_DESK);

    // Block item for the elementary desk (a single block with 9 slots)
    public static final DeferredItem<BlockItem> ELEMENTARY_DESK =
            ITEMS.registerSimpleBlockItem(ModBlocks.ELEMENTARY_DESK);

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
