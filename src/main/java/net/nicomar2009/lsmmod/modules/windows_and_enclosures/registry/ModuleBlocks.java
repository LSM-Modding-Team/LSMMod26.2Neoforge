package net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.TallClassroomEntranceBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolTintedGlassBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolSlidingEntranceBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolGlassBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolContinuousGlassBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolFramedDiningDoorBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolBathroomDoor2x4Block;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.FullBathroomDoorBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.DoubleSchoolEntranceBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_CONNECTED_TINTED_GLASS = BLOCKS.registerBlock("school_connected_tinted_glass", props -> new SchoolGlassBlock(props, new double[][]{{0,0,0,16,16,8}}), props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolProjectingWindowBlock> SCHOOL_PROJECTING_WINDOW = BLOCKS.registerBlock("school_projecting_window", SchoolProjectingWindowBlock::new, props -> props.strength(0.5F).sound(SoundType.GLASS).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolBathroomDoor2x4Block> BATHROOM_DOOR_WIDE_2X4 = BLOCKS.registerBlock("bathroom_door_wide_2x4", SchoolBathroomDoor2x4Block::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE = BLOCKS.registerBlock("dining_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolFramedDiningDoorBlock> BATHROOM_DOOR_WIDE = BLOCKS.registerBlock("bathroom_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolFramedDiningDoorBlock> INFIRMARY_DOOR = BLOCKS.registerBlock("infirmary_door", props -> new SchoolFramedDiningDoorBlock(props,true), props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE_LIGHT = BLOCKS.registerBlock("dining_door_wide_light", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<FullBathroomDoorBlock> SCHOOL_WOOD_DOOR = BLOCKS.registerBlock("school_wood_door", props -> new FullBathroomDoorBlock(3,props), props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolSlidingEntranceBlock> SCHOOL_SLIDING_ENTRANCE = BLOCKS.registerBlock("school_sliding_entrance", SchoolSlidingEntranceBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_GLASS = BLOCKS.registerBlock(
            "school_glass", props -> new SchoolGlassBlock(props, new double[][]{{0, 0, 0, 16, 16, 8}}),
            props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolContinuousGlassBlock> SCHOOL_CONTINUOUS_GLASS = BLOCKS.registerBlock(
            "school_continuous_glass", SchoolContinuousGlassBlock::new,
            props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<DoubleSchoolEntranceBlock> DOUBLE_SCHOOL_ENTRANCE = BLOCKS.registerBlock(
            "double_school_entrance", DoubleSchoolEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_CLASSROOM_ENTRANCE = BLOCKS.registerBlock(
            "tall_classroom_entrance", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<FullBathroomDoorBlock> TALL_BATHROOM_DOOR_FULL_3 = BLOCKS.registerBlock("tall_bathroom_door_full_3", props -> new FullBathroomDoorBlock(3,props), props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<FullBathroomDoorBlock> TALL_BATHROOM_DOOR_FULL_4 = BLOCKS.registerBlock("tall_bathroom_door_full_4", props -> new FullBathroomDoorBlock(4,props), props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolTintedGlassBlock> SCHOOL_TINTED_GLASS = BLOCKS.registerBlock("school_tinted_glass", SchoolTintedGlassBlock::new, props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_BATHROOM_DOOR = BLOCKS.registerBlock(
            "tall_bathroom_door", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_TEACHERS_OFFICE_DOOR = BLOCKS.registerBlock(
            "tall_teachers_office_door", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallClassroomEntranceBlock> TALL_DINING_DOOR = BLOCKS.registerBlock(
            "tall_dining_door", TallClassroomEntranceBlock::new,
            props -> props.strength(3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<DoorBlock> CLASSROOM_DOOR = BLOCKS.registerBlock(
            "classroom_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DoorBlock> DINING_DOOR = BLOCKS.registerBlock(
            "dining_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DoorBlock> TEACHERS_OFFICE_DOOR = BLOCKS.registerBlock(
            "teachers_office_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DoorBlock> BATHROOM_DOOR = BLOCKS.registerBlock(
            "bathroom_door", props -> new DoorBlock(BlockSetType.OAK, props),
            props -> props.strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
