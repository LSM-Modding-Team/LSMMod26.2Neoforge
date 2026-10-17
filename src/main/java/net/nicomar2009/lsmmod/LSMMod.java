package net.nicomar2009.lsmmod;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.nicomar2009.lsmmod.config.LSMClientConfig;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;
import net.nicomar2009.lsmmod.registry.ModBlocks;
import net.nicomar2009.lsmmod.registry.ModCreativeTabs;
import net.nicomar2009.lsmmod.registry.ModEntities;
import net.nicomar2009.lsmmod.registry.ModItems;
import net.nicomar2009.lsmmod.registry.ModSounds;
import org.slf4j.Logger;

/** Mod entry point. */
@Mod(LSMMod.MOD_ID)
public class LSMMod {
    // Single source of truth for the mod id; must match modId in neoforge.mods.toml
    public static final String MOD_ID = "lsmmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LSMMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, LSMClientConfig.SPEC, "lsmmod-client.toml");
        // Register every deferred register on the mod event bus
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        net.nicomar2009.lsmmod.registry.ModEffects.EFFECTS.register(modEventBus);
        modEventBus.addListener(net.nicomar2009.lsmmod.network.SchoolStudyPayload::register);
        modEventBus.addListener(net.nicomar2009.lsmmod.modules.school_supplies.network.PaintCopyPayload::register);
        modEventBus.addListener(net.nicomar2009.lsmmod.modules.balls.network.FootballInputPayload::register);
        modEventBus.addListener(net.nicomar2009.lsmmod.network.SchoolMediaPayloads::register);
        modEventBus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) ->
                event.registrar("1").playToClient(
                        net.nicomar2009.lsmmod.network.SpeakerSoundPayload.TYPE,
                        net.nicomar2009.lsmmod.network.SpeakerSoundPayload.CODEC));
        LOGGER.info("LSM Mod loaded");
    }
}
