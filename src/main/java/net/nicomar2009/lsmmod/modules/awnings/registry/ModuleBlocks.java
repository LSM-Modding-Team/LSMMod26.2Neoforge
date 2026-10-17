package net.nicomar2009.lsmmod.modules.awnings.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.awnings.block.AwningSupportBlock;
import net.nicomar2009.lsmmod.modules.awnings.block.AwningBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<AwningSupportBlock> AWNING_SUPPORT = BLOCKS.registerBlock(
            "awning_support", AwningSupportBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> PLAYGROUND_AWNING = BLOCKS.registerBlock(
            "playground_awning", props -> new AwningBlock(props, "playground_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> ELEMENTARY_PLAYGROUND_AWNING = BLOCKS.registerBlock(
            "elementary_playground_awning", props -> new AwningBlock(props, "elementary_playground_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));
    private ModuleBlocks() {}
}
