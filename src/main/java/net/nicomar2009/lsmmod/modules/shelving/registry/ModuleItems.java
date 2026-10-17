package net.nicomar2009.lsmmod.modules.shelving.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> SCHOOL_SHELVING = ITEMS.registerSimpleBlockItem(ModBlocks.SCHOOL_SHELVING);
    private ModuleItems() {}
}
