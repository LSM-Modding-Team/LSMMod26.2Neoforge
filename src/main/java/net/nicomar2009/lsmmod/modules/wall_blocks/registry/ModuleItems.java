package net.nicomar2009.lsmmod.modules.wall_blocks.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_NAME_SIGN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_NAME_SIGN);

    public static final DeferredItem<BlockItem> SCHOOL_WOOD_PANEL = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_WOOD_PANEL);

    public static final DeferredItem<BlockItem> SCHOOL_CRUCIFIX = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CRUCIFIX);

    public static final DeferredItem<BlockItem> SCHOOL_CLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_CLOCK);

    public static final DeferredItem<BlockItem> SCHOOL_FAN = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_FAN);

    public static final DeferredItem<BlockItem> CLASSROOM_TIMETABLE =
            ITEMS.registerSimpleBlockItem(ModBlocks.CLASSROOM_TIMETABLE);

    public static final DeferredItem<BlockItem> FIRE_ALARM =
            ITEMS.registerSimpleBlockItem(ModBlocks.FIRE_ALARM);


    private ModuleItems() {}
}
