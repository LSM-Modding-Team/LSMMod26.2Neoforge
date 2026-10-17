package net.nicomar2009.lsmmod.modules.wall_blocks.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.WallDecorationBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolWallDecorationBlock;
import net.nicomar2009.lsmmod.modules.special_blocks.block.SchoolShieldBlock;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolPassiveAccessoryBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolNameSignBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolDecorationShapes;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.SchoolCrucifixBlock;
import net.nicomar2009.lsmmod.modules.wall_blocks.block.FireAlarmShapes;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<SchoolNameSignBlock> SCHOOL_NAME_SIGN = BLOCKS.registerBlock("school_name_sign", SchoolNameSignBlock::new, props -> props.strength(1F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolPassiveAccessoryBlock> SCHOOL_WOOD_PANEL = BLOCKS.registerBlock("school_wood_panel", props -> new SchoolPassiveAccessoryBlock(props, new double[][]{{0,0,8,16,16,16}}), props -> props.strength(2F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<SchoolCrucifixBlock> SCHOOL_CRUCIFIX = BLOCKS.registerBlock("school_crucifix", SchoolCrucifixBlock::new, props -> props.strength(1.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<SchoolWallDecorationBlock> SCHOOL_CLOCK = BLOCKS.registerBlock(
            "school_clock", props -> new SchoolWallDecorationBlock(2,props),
            props -> props.strength(.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<SchoolWallDecorationBlock> SCHOOL_FAN = BLOCKS.registerBlock(
            "school_fan", props -> new SchoolWallDecorationBlock(6,props),
            props -> props.strength(.8F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<WallDecorationBlock> CLASSROOM_TIMETABLE = BLOCKS.registerBlock(
            "classroom_timetable", props -> new WallDecorationBlock(props, SchoolDecorationShapes.TIMETABLE),
            props -> props.strength(0.2F).sound(SoundType.WOOL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<WallDecorationBlock> FIRE_ALARM = BLOCKS.registerBlock(
            "fire_alarm", props -> new WallDecorationBlock(props, net.nicomar2009.lsmmod.modules.wall_blocks.block.FireAlarmShapes.BOXES),
            props -> props.strength(1.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));


    private ModuleBlocks() {}
}
