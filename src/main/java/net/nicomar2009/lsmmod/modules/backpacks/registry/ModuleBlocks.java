package net.nicomar2009.lsmmod.modules.backpacks.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.WallDecorationBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolDecorationShapes;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<WallDecorationBlock> EMERGENCY_BACKPACK = BLOCKS.registerBlock(
            "emergency_backpack", props -> new WallDecorationBlock(props, SchoolDecorationShapes.BACKPACK),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));
    private ModuleBlocks() {}
}
