package net.nicomar2009.lsmmod.modules.lighting.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.lighting.block.CeilingLightBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<CeilingLightBlock> CEILING_LIGHT = BLOCKS.registerBlock(
            "ceiling_light", CeilingLightBlock::new,
            props -> props.strength(.5F).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(CeilingLightBlock.LIT) ? 15 : 0));
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
