package net.nicomar2009.lsmmod.modules.shelving.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.shelving.block.SchoolShelvingBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolShelvingBlock> SCHOOL_SHELVING = BLOCKS.registerBlock("school_shelving", SchoolShelvingBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
    private ModuleBlocks() {}
}
