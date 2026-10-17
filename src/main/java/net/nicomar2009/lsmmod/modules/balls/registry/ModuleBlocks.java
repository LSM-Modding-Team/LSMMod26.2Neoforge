package net.nicomar2009.lsmmod.modules.balls.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.registry.*;
import net.nicomar2009.lsmmod.modules.balls.block.SportsGoalBlock;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SportsGoalBlock> FOOTBALL_BASKETBALL_GOAL = BLOCKS.registerBlock(
            "football_basketball_goal", SportsGoalBlock::new,
            props -> props.strength(3.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
