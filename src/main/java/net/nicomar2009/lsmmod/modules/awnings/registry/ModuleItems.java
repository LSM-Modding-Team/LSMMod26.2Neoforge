package net.nicomar2009.lsmmod.modules.awnings.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;
import net.nicomar2009.lsmmod.modules.awnings.item.AwningSupportItem;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.awnings.item.AwningItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<BlockItem> AWNING_SUPPORT =
            ITEMS.registerItem("awning_support", props -> new net.nicomar2009.lsmmod.modules.awnings.item.AwningSupportItem(ModuleBlocks.AWNING_SUPPORT.get(), props));

    public static final DeferredItem<Item> PLAYGROUND_AWNING =
            ITEMS.registerItem("playground_awning", props -> new AwningItem(props, ModuleBlocks.PLAYGROUND_AWNING));

    public static final DeferredItem<Item> ELEMENTARY_PLAYGROUND_AWNING =
            ITEMS.registerItem("elementary_playground_awning", props -> new AwningItem(props, ModuleBlocks.ELEMENTARY_PLAYGROUND_AWNING));
    public static Item awningVariant(String id) {
        return switch (id) {
            case "playground_awning" -> PLAYGROUND_AWNING.get();
            case "elementary_playground_awning" -> ELEMENTARY_PLAYGROUND_AWNING.get();
            default -> PLAYGROUND_AWNING.get();
        };
    }

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleItems() {}
}
