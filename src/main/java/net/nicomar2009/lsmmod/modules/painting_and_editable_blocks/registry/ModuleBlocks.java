package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolStairProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableGlassStairBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableContinuousGlassStairBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableCustomBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlock;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintableBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolPaintableProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW = BLOCKS.registerBlock("school_paintable_projecting_window", SchoolPaintableProjectingWindowBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolPaintableGlassStairBlock> SCHOOL_PAINTABLE_GLASS_STAIRS = BLOCKS.registerBlock("school_paintable_glass_stairs", SchoolPaintableGlassStairBlock::new, props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().dynamicShape());

    public static final DeferredBlock<SchoolPaintableContinuousGlassStairBlock> SCHOOL_PAINTABLE_CONTINUOUS_GLASS_STAIRS = BLOCKS.registerBlock("school_paintable_continuous_glass_stairs", SchoolPaintableContinuousGlassStairBlock::new, props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().dynamicShape());

    public static final DeferredBlock<SchoolSlabProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB = BLOCKS.registerBlock("school_paintable_projecting_window_slab", SchoolSlabProjectingWindowBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape());

    public static final DeferredBlock<SchoolStairProjectingWindowBlock> SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS = BLOCKS.registerBlock("school_paintable_projecting_window_stairs", SchoolStairProjectingWindowBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolMiniCubePaintableBlock> SCHOOL_PAINTABLE_MINI_CUBE_BLOCK = BLOCKS.registerBlock("school_paintable_mini_cube_block", SchoolMiniCubePaintableBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops().dynamicShape().noOcclusion());

    public static final DeferredBlock<SchoolPaintableCustomBlock> SCHOOL_PAINTABLE_CUSTOM_BLOCK = BLOCKS.registerBlock(
            "school_paintable_custom_block", SchoolPaintableCustomBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().dynamicShape().noOcclusion());

    public static final DeferredBlock<SchoolPaintableBlock> SCHOOL_PAINTABLE_BLOCK = BLOCKS.registerBlock(
            "school_paintable_block", SchoolPaintableBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
