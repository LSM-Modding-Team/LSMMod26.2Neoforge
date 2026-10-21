package net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolTintedGlassBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolSlidingEntranceBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolGlassBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolContinuousGlassBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_CONNECTED_TINTED_GLASS = BLOCKS.registerBlock("school_connected_tinted_glass", props -> new SchoolGlassBlock(props, new double[][]{{0,0,0,16,16,8}}), props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolProjectingWindowBlock> SCHOOL_PROJECTING_WINDOW = BLOCKS.registerBlock("school_projecting_window", SchoolProjectingWindowBlock::new, props -> props.strength(0.5F).sound(SoundType.GLASS).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolSlidingEntranceBlock> SCHOOL_SLIDING_ENTRANCE = BLOCKS.registerBlock("school_sliding_entrance", SchoolSlidingEntranceBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_GLASS = BLOCKS.registerBlock(
            "school_glass", props -> new SchoolGlassBlock(props, new double[][]{{0, 0, 0, 16, 16, 8}}),
            props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolContinuousGlassBlock> SCHOOL_CONTINUOUS_GLASS = BLOCKS.registerBlock(
            "school_continuous_glass", SchoolContinuousGlassBlock::new,
            props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolTintedGlassBlock> SCHOOL_TINTED_GLASS = BLOCKS.registerBlock("school_tinted_glass", SchoolTintedGlassBlock::new, props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock> SOLID_ASSEMBLY_DOOR = BLOCKS.registerBlock("school_solid_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock(props,"school_solid_assembly_door"), props -> props.strength(3F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock> DINING_ASSEMBLY_DOOR = BLOCKS.registerBlock("school_dining_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock(props,"school_dining_assembly_door"), props -> props.strength(3F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock> WINDOWED_PANEL_ASSEMBLY_DOOR = BLOCKS.registerBlock("school_windowed_panel_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock(props,"school_windowed_panel_assembly_door"), props -> props.strength(3F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock> DOUBLE_WINDOWED_ASSEMBLY_DOOR = BLOCKS.registerBlock("school_double_windowed_assembly_door", props -> new net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.AssemblyDoorBlock(props,"school_double_windowed_assembly_door"), props -> props.strength(3F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));

    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
