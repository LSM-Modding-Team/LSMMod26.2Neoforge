package net.nicomar2009.lsmmod.modules.backpacks.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> EMERGENCY_BACKPACK =
            ITEMS.registerSimpleBlockItem(ModBlocks.EMERGENCY_BACKPACK);
    private ModuleItems() {}
}
