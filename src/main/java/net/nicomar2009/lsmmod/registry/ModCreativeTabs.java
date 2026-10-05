package net.nicomar2009.lsmmod.registry;

import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Creative tab registry. The icon is a vanilla item until the user provides a custom one. */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LSMMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LSM_TAB = TABS.register(
            "lsm_mod",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lsm_mod"))
                    .icon(() -> new ItemStack(Items.BOOK))
                    .displayItems((parameters, output) ->
                            ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
