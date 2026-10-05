package net.nicomar2009.lsmmod;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;
import net.nicomar2009.lsmmod.registry.ModBlocks;
import net.nicomar2009.lsmmod.registry.ModCreativeTabs;
import net.nicomar2009.lsmmod.registry.ModEntities;
import net.nicomar2009.lsmmod.registry.ModItems;
import org.slf4j.Logger;

/** Mod entry point. */
@Mod(LSMMod.MOD_ID)
public class LSMMod {
    // Single source of truth for the mod id; must match modId in neoforge.mods.toml
    public static final String MOD_ID = "lsmmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LSMMod(IEventBus modEventBus) {
        // Register every deferred register on the mod event bus
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        LOGGER.info("LSM Mod loaded");
    }
}
