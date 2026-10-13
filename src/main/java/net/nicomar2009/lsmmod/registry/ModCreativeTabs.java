package net.nicomar2009.lsmmod.registry;

import java.util.function.Supplier;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.ball.BallItem;
import net.nicomar2009.lsmmod.item.AwningItem;
import net.nicomar2009.lsmmod.item.RulerItem;
import net.nicomar2009.lsmmod.item.SchoolCurtainItem;
import net.nicomar2009.lsmmod.item.SchoolUniformItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Every registered item belongs to exactly one school category. */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LSMMod.MOD_ID);

    private enum Group { DEFAULT, BLOCKS, FUNCTIONAL_BLOCKS, ITEMS, ENTITIES }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LSM_TAB = tab(
            "lsm_mod", "itemGroup.lsm_mod", () -> new ItemStack(ModItems.UNIFORM_POLO.get()), Group.DEFAULT);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCHOOL_BLOCKS = tab(
            "school_blocks", "itemGroup.lsmmod.school_blocks", () -> new ItemStack(ModItems.LIGHT_SCHOOL_WALL.get()), Group.BLOCKS);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCHOOL_FUNCTIONAL_BLOCKS = tab(
            "school_functional_blocks", "itemGroup.lsmmod.school_functional_blocks", () -> new ItemStack(ModItems.LOCKER.get()), Group.FUNCTIONAL_BLOCKS);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCHOOL_ITEMS = tab(
            "school_items", "itemGroup.lsmmod.school_items", () -> new ItemStack(ModItems.GUITAR.get()), Group.ITEMS);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SCHOOL_ENTITIES = tab(
            "school_entities", "itemGroup.lsmmod.school_entities", () -> new ItemStack(ModItems.STUDENT_SPAWN_EGG.get()), Group.ENTITIES);

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> tab(
            String id, String title, Supplier<ItemStack> icon, Group group) {
        return TABS.register(id, () -> CreativeModeTab.builder()
                .title(Component.translatable(title))
                .icon(icon)
                .displayItems((parameters, output) -> ModItems.ITEMS.getEntries().forEach(entry -> {
                    if (entry.getId().getPath().equals("classroom_painted_block")) return; // Legacy registry alias.
                    Item item = entry.get();
                    if (category(item, entry.getId().getPath()) == group) output.accept(item);
                }))
                .build());
    }

    private static Group category(Item item, String id) {
        if (item instanceof SchoolUniformItem || item instanceof RulerItem) return Group.DEFAULT;
        if (item instanceof BallItem || item instanceof SpawnEggItem || id.equals("npc_summon_helper"))
            return Group.ENTITIES;
        if (item instanceof BlockItem || item instanceof AwningItem || item instanceof SchoolCurtainItem) {
            // Doors remain functional, including the two IDs ending in entrance.
            if (id.endsWith("_door") || id.contains("_door_") || id.endsWith("_entrance") || id.equals("school_gate"))
                return Group.FUNCTIONAL_BLOCKS;
            if (id.contains("_slope_") || id.startsWith("school_entrance_pavement")
                    || id.contains("_wall") || id.endsWith("_floor") || id.equals("school_parquet")
                    || id.contains("_glass") || id.endsWith("_window") || id.equals("classroom_white_wall")
                    || id.startsWith("school_paintable_") || id.equals("school_foundation")
                    || id.equals("school_circular_column") || id.equals("school_synthetic_grass") || id.equals("yellow_school_block")) return Group.BLOCKS;
            return Group.FUNCTIONAL_BLOCKS;
        }
        return Group.ITEMS;
    }

    private ModCreativeTabs() {}

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
