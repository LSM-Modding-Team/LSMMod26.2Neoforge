package net.nicomar2009.lsmmod.modules.awnings.registry;
import net.nicomar2009.lsmmod.modules.awnings.item.AwningSupportItem;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.awnings.item.AwningItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> AWNING_SUPPORT =
            ITEMS.registerItem("awning_support", props -> new net.nicomar2009.lsmmod.modules.awnings.item.AwningSupportItem(ModBlocks.AWNING_SUPPORT.get(), props));

    public static final DeferredItem<Item> PLAYGROUND_AWNING =
            ITEMS.registerItem("playground_awning", props -> new AwningItem(props, ModBlocks.PLAYGROUND_AWNING));

    public static final DeferredItem<Item> ELEMENTARY_PLAYGROUND_AWNING =
            ITEMS.registerItem("elementary_playground_awning", props -> new AwningItem(props, ModBlocks.ELEMENTARY_PLAYGROUND_AWNING));
    private ModuleItems() {}
}
