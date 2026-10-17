package net.nicomar2009.lsmmod.modules.slopes.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.slopes.block.YellowSchoolSlopeBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.SchoolRailingSlopeBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.SchoolInvertedSlopeBlock;
import net.nicomar2009.lsmmod.modules.slopes.block.EntrancePavementSlopeBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_1 = BLOCKS.registerBlock("school_inverted_slope_short_raised_1", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_2 = BLOCKS.registerBlock("school_inverted_slope_short_raised_2", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SHORT_RAISED_3 = BLOCKS.registerBlock("school_inverted_slope_short_raised_3", props -> new SchoolInvertedSlopeBlock(0,-1,0.5,2,props), props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

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

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_1 = BLOCKS.registerBlock("school_entrance_pavement_slope_1", props -> new EntrancePavementSlopeBlock(0.5,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_2 = BLOCKS.registerBlock("school_entrance_pavement_slope_2", props -> new EntrancePavementSlopeBlock(0.75,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_3 = BLOCKS.registerBlock("school_entrance_pavement_slope_3", props -> new EntrancePavementSlopeBlock(0,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_4 = BLOCKS.registerBlock("school_entrance_pavement_slope_4", props -> new EntrancePavementSlopeBlock(0.25,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_5 = BLOCKS.registerBlock("school_entrance_pavement_slope_5", props -> new EntrancePavementSlopeBlock(0.5,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<EntrancePavementSlopeBlock> SCHOOL_ENTRANCE_PAVEMENT_SLOPE_6 = BLOCKS.registerBlock("school_entrance_pavement_slope_6", props -> new EntrancePavementSlopeBlock(0.75,props), props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_1 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_extra_1", props -> new SchoolInvertedSlopeBlock(0.571428571428571,0.714285714285714,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_2 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_extra_2", props -> new SchoolInvertedSlopeBlock(0.857142857142857,0.714285714285714,0.5,2.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<SchoolInvertedSlopeBlock> SCHOOL_INVERTED_SLOPE_SPLIT_RAISED_EXTRA_3 = BLOCKS.registerBlock(
            "school_inverted_slope_split_raised_extra_3", props -> new SchoolInvertedSlopeBlock(0.142857142857143,0.714285714285714,0.5,1.0,props),
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion());

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
    private ModuleBlocks() {}
}
