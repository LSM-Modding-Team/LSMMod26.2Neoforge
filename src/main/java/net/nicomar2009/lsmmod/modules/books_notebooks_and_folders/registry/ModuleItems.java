package net.nicomar2009.lsmmod.modules.books_notebooks_and_folders.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final java.util.List<DeferredItem<Item>> FOLDERS = registerStationery("folder");

    public static final java.util.List<DeferredItem<Item>> NOTEBOOKS = registerStationery("notebook");

    private static java.util.List<DeferredItem<Item>> registerStationery(String kind) {
        var variants = new java.util.ArrayList<DeferredItem<Item>>();
        for (String color : new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime",
                "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}) {
            variants.add(ITEMS.registerItem(color + "_" + kind, Item::new));
        }
        return java.util.List.copyOf(variants);
    }

    private ModuleItems() {}
}
