package net.nicomar2009.lsmmod.modules.wall_blocks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_NAME_SIGN = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_NAME_SIGN);

    public static final DeferredItem<BlockItem> SCHOOL_WOOD_PANEL = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_WOOD_PANEL);

    public static final DeferredItem<BlockItem> SCHOOL_CRUCIFIX = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CRUCIFIX);

    public static final DeferredItem<BlockItem> SCHOOL_CLOCK = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_CLOCK);

    public static final DeferredItem<BlockItem> SCHOOL_FAN = ITEMS.registerSimpleBlockItem(ModuleBlocks.SCHOOL_FAN);

    public static final DeferredItem<BlockItem> CLASSROOM_TIMETABLE =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.CLASSROOM_TIMETABLE);

    public static final DeferredItem<BlockItem> FIRE_ALARM =
            ITEMS.registerSimpleBlockItem(ModuleBlocks.FIRE_ALARM);


    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
