package net.nicomar2009.lsmmod.modules.facilities.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.facilities.block.WideBathroomShapes;
import net.nicomar2009.lsmmod.modules.facilities.block.WideBathroomBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.TrashBinShapes;
import net.nicomar2009.lsmmod.modules.facilities.block.TrashBinBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.ToiletBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.TallBathroomShapes;
import net.nicomar2009.lsmmod.modules.facilities.block.TallBathroomBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolSinkBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolRefrigeratorBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolPassiveAccessoryBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolFacilityShapes;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolDrainBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolConnectedSinkBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.RaisedDrinkingFountainBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.InfirmaryCotBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.DrinkingFountainBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolRefrigeratorBlock> SCHOOL_REFRIGERATOR = BLOCKS.registerBlock("school_refrigerator", SchoolRefrigeratorBlock::new, props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_MICROWAVE = BLOCKS.registerBlock("school_microwave", props -> new SchoolPassiveAccessoryBlock(props,SchoolFacilityShapes.MICROWAVE), props -> props.strength(2F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<RaisedDrinkingFountainBlock> DRINKING_FOUNTAIN_RAISED = BLOCKS.registerBlock("drinking_fountain_raised", RaisedDrinkingFountainBlock::new, props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolDrainBlock> SCHOOL_DRAIN = BLOCKS.registerBlock("school_drain", SchoolDrainBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolSinkBlock> SCHOOL_SINK = BLOCKS.registerBlock(
            "school_sink", SchoolSinkBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolConnectedSinkBlock> SCHOOL_CONNECTED_SINK = BLOCKS.registerBlock(
            "school_connected_sink", SchoolConnectedSinkBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().dynamicShape().pushReaction(PushReaction.BLOCK));

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

    public static final DeferredBlock<DrinkingFountainBlock> DRINKING_FOUNTAIN = BLOCKS.registerBlock(
            "drinking_fountain", DrinkingFountainBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<InfirmaryCotBlock> INFIRMARY_COT = BLOCKS.registerBlock(
            "infirmary_cot", InfirmaryCotBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
