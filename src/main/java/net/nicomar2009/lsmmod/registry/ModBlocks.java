package net.nicomar2009.lsmmod.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;

/** Registers the school furniture, statue and shield. */
public final class ModBlocks {
    // Specialized register: it sets the block's resource key (required since 1.21.2) automatically
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LSMMod.MOD_ID);

    // BEGIN SCHOOL RAILING SLOPES
    // BEGIN SHORT RAISED SLOPES
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1 = BLOCKS.registerBlock("school_inverted_slope_short_raised_1", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2 = BLOCKS.registerBlock("school_inverted_slope_short_raised_2", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3 = BLOCKS.registerBlock("school_inverted_slope_short_raised_3", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SHORT RAISED SLOPES

    // BEGIN SCHOOL INVERTED SLOPES
    // BEGIN SCHOOL SPLIT RAISED EXTRAS
    // BEGIN ENTRANCE PAVEMENT SHAPES
    // BEGIN DINING COUNTER
    public static final DeferredBlock<DiningRoomCounterBlock> DINING_ROOM_COUNTER = BLOCKS.registerBlock("dining_room_counter", DiningRoomCounterBlock::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    // END DINING COUNTER

    // BEGIN CONNECTED TINTED GLASS
    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_CONNECTED_TINTED_GLASS = BLOCKS.registerBlock("school_connected_tinted_glass", props -> new SchoolGlassBlock(props, new double[][]{{0,0,0,16,16,8}}), props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());
    // END CONNECTED TINTED GLASS

    public static final DeferredBlock<SchoolRefrigeratorBlock> SCHOOL_REFRIGERATOR = BLOCKS.registerBlock("school_refrigerator", SchoolRefrigeratorBlock::new, props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<DiningWallCabinetBlock> DINING_WALL_CABINET = BLOCKS.registerBlock("dining_wall_cabinet", DiningWallCabinetBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolShelvingBlock> SCHOOL_SHELVING = BLOCKS.registerBlock("school_shelving", SchoolShelvingBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    // BEGIN FACILITY EXPANSIONS
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE = BLOCKS.registerBlock("dining_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> BATHROOM_DOOR_WIDE = BLOCKS.registerBlock("bathroom_door_wide", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> INFIRMARY_DOOR = BLOCKS.registerBlock("infirmary_door", props -> new SchoolFramedDiningDoorBlock(props,true), props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolFramedDiningDoorBlock> DINING_DOOR_WIDE_LIGHT = BLOCKS.registerBlock("dining_door_wide_light", SchoolFramedDiningDoorBlock::new, props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_MICROWAVE = BLOCKS.registerBlock("school_microwave", props -> new SchoolPassiveAccessoryBlock(props,SchoolFacilityShapes.MICROWAVE), props -> props.strength(2F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<SchoolTrophyGalleryBlock> SCHOOL_TROPHY_GALLERY = BLOCKS.registerBlock("school_trophy_gallery", SchoolTrophyGalleryBlock::new, props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    // END FACILITY EXPANSIONS

    // BEGIN KIOSK ENTRANCE ACCESSORIES
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_WOOD_PANEL = BLOCKS.registerBlock("school_wood_panel", props -> new SchoolPassiveAccessoryBlock(props, new double[][]{{0,0,8,16,16,16}}), props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<FullBathroomDoorBlock> SCHOOL_WOOD_DOOR = BLOCKS.registerBlock("school_wood_door", props -> new FullBathroomDoorBlock(3,props), props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolSlidingEntranceBlock> SCHOOL_SLIDING_ENTRANCE = BLOCKS.registerBlock("school_sliding_entrance", SchoolSlidingEntranceBlock::new, props -> props.strength(3F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<RaisedDrinkingFountainBlock> DRINKING_FOUNTAIN_RAISED = BLOCKS.registerBlock("drinking_fountain_raised", RaisedDrinkingFountainBlock::new, props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolPassiveAccessoryBlock> LOST_AND_FOUND_BOX = BLOCKS.registerBlock("lost_and_found_box", props -> new SchoolPassiveAccessoryBlock(props,SchoolEntranceAccessoryShapes.LOST_BOX), props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion());
    // END KIOSK ENTRANCE ACCESSORIES

    // BEGIN SCHOOL CRUCIFIX
    public static final DeferredBlock<SchoolCrucifixBlock> SCHOOL_CRUCIFIX = BLOCKS.registerBlock("school_crucifix", SchoolCrucifixBlock::new, props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));
    // END SCHOOL CRUCIFIX

    // BEGIN DARK TILE DRAIN COLUMN
    public static final DeferredBlock<Block> DARK_TILED_FLOOR = BLOCKS.registerBlock("dark_tiled_floor", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<SchoolDrainBlock> SCHOOL_DRAIN = BLOCKS.registerBlock("school_drain", SchoolDrainBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<SchoolCircularColumnBlock> SCHOOL_CIRCULAR_COLUMN = BLOCKS.registerBlock("school_circular_column", SchoolCircularColumnBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape().requiresCorrectToolForDrops());
    // END DARK TILE DRAIN COLUMN

    // BEGIN CHILDREN LOCKER AND CEILING PROJECTOR
    public static final DeferredBlock<ChildrenLockerBlock> CHILDREN_LOCKER = BLOCKS.registerBlock(
            "children_locker", ChildrenLockerBlock::new,
            props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion());
    public static final DeferredBlock<CeilingProjectorBlock> CEILING_PROJECTOR = BLOCKS.registerBlock(
            "ceiling_projector", CeilingProjectorBlock::new,
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion());
    // END CHILDREN LOCKER AND CEILING PROJECTOR

    // BEGIN CUSTOM PAINTABLE BLOCKS
    public static final DeferredBlock<Block> SCHOOL_FOUNDATION = BLOCKS.registerBlock(
            "school_foundation", Block::new,
            props -> props.strength(3.0F, 8.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<SchoolPaintableCustomBlock> SCHOOL_PAINTABLE_CUSTOM_BLOCK = BLOCKS.registerBlock(
            "school_paintable_custom_block", SchoolPaintableCustomBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().dynamicShape().noOcclusion());
    // END CUSTOM PAINTABLE BLOCKS

    // BEGIN PAINTABLE SCHOOL BLOCK
    public static final DeferredBlock<SchoolPaintableBlock> SCHOOL_PAINTABLE_BLOCK = BLOCKS.registerBlock(
            "school_paintable_block", SchoolPaintableBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    // END PAINTABLE SCHOOL BLOCK

    // BEGIN YELLOW SCHOOL BLOCK
    public static final DeferredBlock<Block> YELLOW_SCHOOL_BLOCK = BLOCKS.registerBlock(
            "yellow_school_block", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    // END YELLOW SCHOOL BLOCK

    // BEGIN TILED PARQUET FLOOR
    public static final DeferredBlock<FloorTransitionBlock> TILED_PARQUET_FLOOR = BLOCKS.registerBlock(
            "tiled_parquet_floor", FloorTransitionBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    // END TILED PARQUET FLOOR

    // BEGIN REMAINING GLASS MATERIAL FAMILIES
    public static final DeferredBlock<SchoolGlassStairBlock> WHITE_DARK_SCHOOL_GLASS = BLOCKS.registerBlock(
            "white_dark_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> WHITE_STREET_SCHOOL_GLASS = BLOCKS.registerBlock(
            "white_street_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> DARK_WHITE_SCHOOL_GLASS = BLOCKS.registerBlock(
            "dark_white_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> DARK_STREET_SCHOOL_GLASS = BLOCKS.registerBlock(
            "dark_street_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> LIGHT_WHITE_SCHOOL_GLASS = BLOCKS.registerBlock(
            "light_white_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> LIGHT_DARK_SCHOOL_GLASS = BLOCKS.registerBlock(
            "light_dark_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> LIGHT_STREET_SCHOOL_GLASS = BLOCKS.registerBlock(
            "light_street_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> STREET_LIGHT_SCHOOL_GLASS = BLOCKS.registerBlock(
            "street_light_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END REMAINING GLASS MATERIAL FAMILIES

    // BEGIN SCHOOL CABINET AND ROUTER
    public static final DeferredBlock<SchoolCabinetBlock> SCHOOL_CABINET = BLOCKS.registerBlock(
            "school_cabinet", SchoolCabinetBlock::new,
            props -> props.strength(2.0F).sound(SoundType.WOOD).pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<SchoolRouterBlock> SCHOOL_ROUTER = BLOCKS.registerBlock(
            "school_router", SchoolRouterBlock::new,
            props -> props.strength(0.5F).sound(SoundType.METAL).noOcclusion());
    // END SCHOOL CABINET AND ROUTER

    // BEGIN SCHOOL SINK
    public static final DeferredBlock<SchoolSinkBlock> SCHOOL_SINK = BLOCKS.registerBlock(
            "school_sink", SchoolSinkBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));
    // END SCHOOL SINK

    // BEGIN YELLOW SCHOOL SLOPES
    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_1 = BLOCKS.registerBlock(
            "yellow_school_slope_1", props -> new YellowSchoolSlopeBlock(0.5,props),
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_2 = BLOCKS.registerBlock(
            "yellow_school_slope_2", props -> new YellowSchoolSlopeBlock(0.375,props),
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_3 = BLOCKS.registerBlock(
            "yellow_school_slope_3", props -> new YellowSchoolSlopeBlock(0.25,props),
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<YellowSchoolSlopeBlock> YELLOW_SCHOOL_SLOPE_4 = BLOCKS.registerBlock(
            "yellow_school_slope_4", props -> new YellowSchoolSlopeBlock(0.125,props),
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    // END YELLOW SCHOOL SLOPES

    // BEGIN PLAYGROUND FLOORS
    public static final DeferredBlock<Block> WHITE_PLAYGROUND_FLOOR = BLOCKS.registerBlock("white_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GRAY_PLAYGROUND_FLOOR = BLOCKS.registerBlock("gray_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GREEN_PLAYGROUND_FLOOR = BLOCKS.registerBlock("green_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ORANGE_PLAYGROUND_FLOOR = BLOCKS.registerBlock("orange_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    // END PLAYGROUND FLOORS

    // BEGIN CONNECTED SCHOOL SINK
    public static final DeferredBlock<SchoolConnectedSinkBlock> SCHOOL_CONNECTED_SINK = BLOCKS.registerBlock(
            "school_connected_sink", SchoolConnectedSinkBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));
    // END CONNECTED SCHOOL SINK

    // BEGIN CLASSROOM SURFACES
    public static final DeferredBlock<Block> CLASSROOM_WHITE_WALL = BLOCKS.registerBlock(
            "classroom_white_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    // Kept as a registry alias for saved worlds and inventory stacks.
    public static final DeferredBlock<Block> CLASSROOM_PAINTED_BLOCK = BLOCKS.registerBlock(
            "classroom_painted_block", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SCHOOL_SYNTHETIC_GRASS = BLOCKS.registerBlock(
            "school_synthetic_grass", Block::new,
            props -> props.strength(.8F).sound(SoundType.MOSS));
    // END CLASSROOM SURFACES

    public static final DeferredBlock<SchoolWallDecorationBlock> SCHOOL_CLOCK = BLOCKS.registerBlock(
            "school_clock", props -> new SchoolWallDecorationBlock(2,props),
            props -> props.strength(.5F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<SchoolWallDecorationBlock> SCHOOL_FAN = BLOCKS.registerBlock(
            "school_fan", props -> new SchoolWallDecorationBlock(6,props),
            props -> props.strength(.8F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<SchoolWoodenHandrailBlock> SCHOOL_WOODEN_HANDRAIL = BLOCKS.registerBlock("school_wooden_handrail", SchoolWoodenHandrailBlock::new, props -> props.strength(2.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<CeilingLightBlock> CEILING_LIGHT = BLOCKS.registerBlock(
            "ceiling_light", CeilingLightBlock::new,
            props -> props.strength(.5F).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(CeilingLightBlock.LIT) ? 15 : 0));

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1 = BLOCKS.registerBlock("school_entrance_pavement_slope_1", props -> new EntrancePavementSlopeBlock(0.5,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2 = BLOCKS.registerBlock("school_entrance_pavement_slope_2", props -> new EntrancePavementSlopeBlock(0.75,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3 = BLOCKS.registerBlock("school_entrance_pavement_slope_3", props -> new EntrancePavementSlopeBlock(0,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4 = BLOCKS.registerBlock("school_entrance_pavement_slope_4", props -> new EntrancePavementSlopeBlock(0.25,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5 = BLOCKS.registerBlock("school_entrance_pavement_slope_5", props -> new EntrancePavementSlopeBlock(0.5,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6 = BLOCKS.registerBlock("school_entrance_pavement_slope_6", props -> new EntrancePavementSlopeBlock(0.75,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<EntrancePavementSlabBlock> SCHOOL_ENTRANCE_PAVEMENT_SLAB = BLOCKS.registerBlock("school_entrance_pavement_slab", EntrancePavementSlabBlock::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
    // END ENTRANCE PAVEMENT SHAPES

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_extra_1", props -> new SchoolInvertedSlopeBlock(0.571428571428571,0.714285714285714,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_extra_2", props -> new SchoolInvertedSlopeBlock(0.857142857142857,0.714285714285714,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_extra_3", props -> new SchoolInvertedSlopeBlock(0.142857142857143,0.714285714285714,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL SPLIT RAISED EXTRAS

    // BEGIN SCHOOL RAISED INVERTED SLOPES
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_1 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_1", props -> new SchoolInvertedSlopeBlock(0,0.714285714285714,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_2 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_2", props -> new SchoolInvertedSlopeBlock(0.285714285714286,0.714285714285714,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_3 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_3", props -> new SchoolInvertedSlopeBlock(0.428571428571429,0.714285714285714,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_4 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_4", props -> new SchoolInvertedSlopeBlock(0.714285714285714,0.714285714285714,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_1 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_1", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_2 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_2", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_3 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_3", props -> new SchoolInvertedSlopeBlock(0.666666666666667,0.666666666666667,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_4 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_4", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_5 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_5", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_RAISED_6 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_raised_6", props -> new SchoolInvertedSlopeBlock(0.666666666666666,0.666666666666667,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL RAISED INVERTED SLOPES

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_1 = BLOCKS.registerBlock(
            "school_inverted_slope_split_1", props -> new SchoolInvertedSlopeBlock(0,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_2 = BLOCKS.registerBlock(
            "school_inverted_slope_split_2", props -> new SchoolInvertedSlopeBlock(0.285714285714286,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_3 = BLOCKS.registerBlock(
            "school_inverted_slope_split_3", props -> new SchoolInvertedSlopeBlock(0.428571428571429,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_4 = BLOCKS.registerBlock(
            "school_inverted_slope_split_4", props -> new SchoolInvertedSlopeBlock(0.714285714285714,0.714285714285714,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_1 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_1", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_2 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_2", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_3 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_3", props -> new SchoolInvertedSlopeBlock(0.666666666666667,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_4 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_4", props -> new SchoolInvertedSlopeBlock(0,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_5 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_5", props -> new SchoolInvertedSlopeBlock(0.333333333333333,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_FLIGHT_6 = BLOCKS.registerBlock(
            "school_inverted_slope_flight_6", props -> new SchoolInvertedSlopeBlock(0.666666666666666,0.666666666666667,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL INVERTED SLOPES

    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_1 = BLOCKS.registerBlock(
            "school_railing_slope_split_1", props -> new SchoolRailingSlopeBlock(0.75,0.357142857142857,0.357142857142857,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_2 = BLOCKS.registerBlock(
            "school_railing_slope_split_2", props -> new SchoolRailingSlopeBlock(0.464285714285714,0.357142857142857,0.357142857142857,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_3 = BLOCKS.registerBlock(
            "school_railing_slope_split_3", props -> new SchoolRailingSlopeBlock(0.321428571428571,0.357142857142857,0.357142857142857,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_SPLIT_4 = BLOCKS.registerBlock(
            "school_railing_slope_split_4", props -> new SchoolRailingSlopeBlock(0.0357142857142856,0.357142857142857,0.357142857142857,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_1 = BLOCKS.registerBlock(
            "school_railing_slope_flight_1", props -> new SchoolRailingSlopeBlock(0.75,0.333333333333333,0.333333333333333,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_2 = BLOCKS.registerBlock(
            "school_railing_slope_flight_2", props -> new SchoolRailingSlopeBlock(0.416666666666667,0.333333333333333,0.333333333333333,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_3 = BLOCKS.registerBlock(
            "school_railing_slope_flight_3", props -> new SchoolRailingSlopeBlock(0.083333333333333,0.333333333333333,0.333333333333333,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_4 = BLOCKS.registerBlock(
            "school_railing_slope_flight_4", props -> new SchoolRailingSlopeBlock(0.75,0.333333333333333,0.333333333333333,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_5 = BLOCKS.registerBlock(
            "school_railing_slope_flight_5", props -> new SchoolRailingSlopeBlock(0.416666666666667,0.333333333333333,0.333333333333333,true,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredBlock<SchoolRailingSlopeBlock> SCHOOL_RAILING_SLOPE_FLIGHT_6 = BLOCKS.registerBlock(
            "school_railing_slope_flight_6", props -> new SchoolRailingSlopeBlock(0.0833333333333339,0.333333333333333,0.333333333333333,false,false,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());
    // END SCHOOL RAILING SLOPES

    public static final DeferredBlock<SchoolWallRailingBlock> LIGHT_SCHOOL_WALL_RAILING = BLOCKS.registerBlock(
            "light_school_wall_railing", SchoolWallRailingBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().forceSolidOn());

    public static final DeferredBlock<SchoolGlassBlock> SCHOOL_GLASS = BLOCKS.registerBlock(
            "school_glass", props -> new SchoolGlassBlock(props, new double[][]{{0, 0, 0, 16, 16, 8}}),
            props -> props.strength(0.3F).sound(SoundType.GLASS).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> SUPPORTED_SCHOOL_GLASS = BLOCKS.registerBlock(
            "supported_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> MIXED_SCHOOL_GLASS = BLOCKS.registerBlock(
            "mixed_school_glass", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> SUPPORTED_SCHOOL_GLASS_REVERSED = BLOCKS.registerBlock(
            "supported_school_glass_reversed", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolGlassStairBlock> MIXED_SCHOOL_GLASS_REVERSED = BLOCKS.registerBlock(
            "mixed_school_glass_reversed", SchoolGlassStairBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

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

    public static final DeferredBlock<SportsGoalBlock> FOOTBALL_BASKETBALL_GOAL = BLOCKS.registerBlock(
            "football_basketball_goal", SportsGoalBlock::new,
            props -> props.strength(3.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ToiletBlock> TOILET = BLOCKS.registerBlock(
            "toilet", ToiletBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TallBathroomBlock> ADULT_MEN_URINAL = BLOCKS.registerBlock(
            "adult_men_urinal", props -> new TallBathroomBlock(props, TallBathroomShapes.ADULT_URINAL,
                    TallBathroomShapes.ADULT_URINAL, true),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WideBathroomBlock> MEN_URINAL = BLOCKS.registerBlock(
            "men_urinal", props -> new WideBathroomBlock(props, WideBathroomShapes.CHILD_URINAL, true),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WideBathroomBlock> STANDALONE_TOILET = BLOCKS.registerBlock(
            "standalone_toilet", props -> new WideBathroomBlock(props, WideBathroomShapes.STANDALONE_TOILET, false),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TrashBinBlock> GREEN_TRASH_BIN = BLOCKS.registerBlock(
            "green_trash_bin", props -> new TrashBinBlock(props, TrashBinShapes.GREEN),
            props -> props.strength(0.8F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<TrashBinBlock> BLACK_TRASH_BIN = BLOCKS.registerBlock(
            "black_trash_bin", props -> new TrashBinBlock(props, TrashBinShapes.BLACK),
            props -> props.strength(0.8F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<ProjectorScreenBlock> PROJECTOR_SCREEN = BLOCKS.registerBlock(
            "projector_screen", ProjectorScreenBlock::new,
            props -> props.strength(0.5F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallProjectorBlock> WALL_PROJECTOR = BLOCKS.registerBlock(
            "wall_projector", WallProjectorBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> CLASSROOM_TIMETABLE = BLOCKS.registerBlock(
            "classroom_timetable", props -> new WallDecorationBlock(props, SchoolDecorationShapes.TIMETABLE),
            props -> props.strength(0.2F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> FIRE_ALARM = BLOCKS.registerBlock(
            "fire_alarm", props -> new WallDecorationBlock(props, net.nicomar2009.lsmmod.block.FireAlarmShapes.BOXES),
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> EMERGENCY_BACKPACK = BLOCKS.registerBlock(
            "emergency_backpack", props -> new WallDecorationBlock(props, SchoolDecorationShapes.BACKPACK),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<Block> SCHOOL_PARQUET = BLOCKS.registerBlock(
            "school_parquet", Block::new,
            props -> props.strength(2.0F,3.0F).sound(SoundType.WOOD));

    public static final DeferredBlock<BathroomFloorBlock> DARK_SCHOOL_TILED_FLOOR = BLOCKS.registerBlock(
            "dark_school_tiled_floor", BathroomFloorBlock::new,
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<BathroomFloorBlock> LIGHT_SCHOOL_TILED_FLOOR = BLOCKS.registerBlock(
            "light_school_tiled_floor", BathroomFloorBlock::new,
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> CLASSROOM_FLOOR = BLOCKS.registerBlock(
            "classroom_floor", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolYellowBorderWallBlock> SCHOOL_ENTRANCE_PAVEMENT = BLOCKS.registerBlock(
            "school_entrance_pavement", SchoolYellowBorderWallBlock::new,
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> STREET_SCHOOL_WALL = BLOCKS.registerBlock(
            "street_school_wall", Block::new,
            props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<StreetSchoolBarsBlock> STREET_SCHOOL_BARS = BLOCKS.registerBlock(
            "street_school_bars", StreetSchoolBarsBlock::new,
            props -> props.strength(5.0F,6.0F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> LIGHT_SCHOOL_WALL = BLOCKS.registerBlock(
            "light_school_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> DARK_SCHOOL_WALL = BLOCKS.registerBlock(
            "dark_school_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolGateBlock> SCHOOL_GATE = BLOCKS.registerBlock(
            "school_gate", SchoolGateBlock::new,
            props -> props.strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()
                    .pushReaction(PushReaction.BLOCK));

    /** Block 1.1 */
    public static final DeferredBlock<ChairBlock> HIGH_SCHOOL_CHAIR = BLOCKS.registerBlock(
            "high_school_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    /** Block 1.2 */
    public static final DeferredBlock<ChairBlock> ELEMENTARY_CHAIR = BLOCKS.registerBlock(
            "elementary_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    /** Block 1.3 */
    public static final DeferredBlock<DeskBlock> STUDENTS_DESK = BLOCKS.registerBlock(
            "students_desk",
            DeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    // The RIGHT half has no block entity, so pistons must never move just one half
                    .pushReaction(PushReaction.BLOCK));

    /** Block 1.4 */
    public static final DeferredBlock<LockerBlock> LOCKER = BLOCKS.registerBlock(
            "locker",
            LockerBlock::new,
            props -> props
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    // The model is not a full cube, so neighbouring faces must not be culled
                    .noOcclusion());

    /** Block 1.5 */
    public static final DeferredBlock<ChairBlock> TEACHERS_CHAIR = BLOCKS.registerBlock(
            "teachers_chair",
            ChairBlock::new,
            props -> props
                    .strength(2.0F)
                    .noOcclusion());

    public static final DeferredBlock<ChairBlock> ENGLISHROOMCHAIR = BLOCKS.registerBlock(
            "englishroomchair",
            props -> new ChairBlock(props, NewChairShapes.ENGLISHROOMCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> HALLCHAIR = BLOCKS.registerBlock(
            "hallchair",
            props -> new ChairBlock(props, NewChairShapes.HALLCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_WHITE = BLOCKS.registerBlock(
            "plasticchair_white",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_RED = BLOCKS.registerBlock(
            "plasticchair_red",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_WHITE_ARMS = BLOCKS.registerBlock(
            "plasticchair_white_arms",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR_ARMS),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<ChairBlock> PLASTICCHAIR_RED_ARMS = BLOCKS.registerBlock(
            "plasticchair_red_arms",
            props -> new ChairBlock(props, NewChairShapes.PLASTICCHAIR_ARMS),
            props -> props.strength(2.0F).noOcclusion());

    public static final DeferredBlock<LaptopBlock> LAPTOP = BLOCKS.registerBlock(
            "laptop", LaptopBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<LaptopBlock> MOISES_LAPTOP = BLOCKS.registerBlock(
            "moises_laptop", LaptopBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<MonitorBlock> PC = BLOCKS.registerBlock(
            "pc", MonitorBlock::new,
            props -> props.strength(1.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<ComputerDeskBlock> COMPUTER_DESK = BLOCKS.registerBlock(
            "computer_desk", ComputerDeskBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<GrayComputerDeskBlock> GRAY_COMPUTER_DESK = BLOCKS.registerBlock(
            "gray_computer_desk", GrayComputerDeskBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolBellBlock> SCHOOL_BELL = BLOCKS.registerBlock(
            "school_bell", SchoolBellBlock::new,
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<DiningTableBlock> DINING_TABLE = BLOCKS.registerBlock(
            "dining_table", DiningTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<ChairBlock> DINING_CHAIR = BLOCKS.registerBlock(
            "dining_chair",
            props -> new ChairBlock(props, NewChairShapes.DINING_CHAIR),
            props -> props.strength(2.0F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<SofaBlock> SOFA = BLOCKS.registerBlock(
            "sofa", SofaBlock::new,
            props -> props.strength(1.0F).sound(SoundType.WOOL).noOcclusion());

    public static final DeferredBlock<SpeakerBlock> SPEAKER = BLOCKS.registerBlock(
            "speaker", SpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<TallSpeakerBlock> TALL_SPEAKER = BLOCKS.registerBlock(
            "tall_speaker", TallSpeakerBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<DrinkingFountainBlock> DRINKING_FOUNTAIN = BLOCKS.registerBlock(
            "drinking_fountain", DrinkingFountainBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<KioskTableBlock> KIOSK_TABLE = BLOCKS.registerBlock(
            "kiosk_table", KioskTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ArtTableBlock> ART_TABLE = BLOCKS.registerBlock(
            "art_table", ArtTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<StoolBlock> STOOL = BLOCKS.registerBlock(
            "stool", StoolBlock::new,
            props -> props.strength(2.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<VaultingBoxBlock> VAULTING_BOX = BLOCKS.registerBlock(
            "vaulting_box", VaultingBoxBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

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

    public static final DeferredBlock<ManuelTiradoBustBlock> MANUEL_TIRADO_BUST = BLOCKS.registerBlock(
            "manuel_tirado_bust", ManuelTiradoBustBlock::new,
            props -> props.strength(4.0F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<InfirmaryCotBlock> INFIRMARY_COT = BLOCKS.registerBlock(
            "infirmary_cot", InfirmaryCotBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    private ModBlocks() {
    }

    /** Block 1.6 */
    public static final DeferredBlock<TeachersDeskBlock> TEACHERS_DESK = BLOCKS.registerBlock(
            "teachers_desk",
            TeachersDeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    // The RIGHT half has no block entity, so pistons must never move just one half
                    .pushReaction(PushReaction.BLOCK));

    /** Block 1.7 */
    public static final DeferredBlock<ElementaryDeskBlock> ELEMENTARY_DESK = BLOCKS.registerBlock(
            "elementary_desk",
            ElementaryDeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    // The model is not a full cube, so neighbouring faces must not be culled
                    .noOcclusion());

    /** Block 1.8 */
    public static final DeferredBlock<SchoolSanMartinBlock> SAN_MARTIN_DE_PORRES = BLOCKS.registerBlock(
            "san_martin_de_porres",
            SchoolSanMartinBlock::new,
            props -> props
                    .strength(4.0f)
                    .sound(SoundType.AMETHYST)
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolShieldBlock> SCHOOL_SHIELD = BLOCKS.registerBlock(
            "school_shield", SchoolShieldBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningSupportBlock> AWNING_SUPPORT = BLOCKS.registerBlock(
            "awning_support", AwningSupportBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> PLAYGROUND_AWNING = BLOCKS.registerBlock(
            "playground_awning", props -> new AwningBlock(props, "playground_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<AwningBlock> ELEMENTARY_PLAYGROUND_AWNING = BLOCKS.registerBlock(
            "elementary_playground_awning", props -> new AwningBlock(props, "elementary_playground_awning"),
            props -> props.strength(0.8F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static AwningBlock awningVariant(String id) {
        return switch (id) {
            case "playground_awning" -> PLAYGROUND_AWNING.get();
            case "elementary_playground_awning" -> ELEMENTARY_PLAYGROUND_AWNING.get();
            default -> PLAYGROUND_AWNING.get();
        };
    }

    public static net.minecraft.world.level.block.Block[] awningEntityBlocks() {
        return new net.minecraft.world.level.block.Block[]{AWNING_SUPPORT.get(), PLAYGROUND_AWNING.get(),
                ELEMENTARY_PLAYGROUND_AWNING.get()};
    }

    public static void register(IEventBus modEventBus) {
        // Resolve saved block palettes under the new names without duplicate blocks.
        BLOCKS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "bathroom_floor"), DARK_SCHOOL_TILED_FLOOR.getId());
        BLOCKS.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "light_bathroom_floor"), LIGHT_SCHOOL_TILED_FLOOR.getId());
        BLOCKS.register(modEventBus);
    }
}

