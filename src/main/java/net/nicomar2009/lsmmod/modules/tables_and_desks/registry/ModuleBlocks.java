package net.nicomar2009.lsmmod.modules.tables_and_desks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.TeachersDeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.KioskTableBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.GrayComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ElementaryDeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DiningTableBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DiningRoomCounterBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ArtTableBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<DiningRoomCounterBlock> DINING_ROOM_COUNTER = BLOCKS.registerBlock("dining_room_counter", DiningRoomCounterBlock::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<DeskBlock> STUDENTS_DESK = BLOCKS.registerBlock(
            "students_desk",
            DeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    // The RIGHT half has no block entity, so pistons must never move just one half
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ComputerDeskBlock> COMPUTER_DESK = BLOCKS.registerBlock(
            "computer_desk", ComputerDeskBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<GrayComputerDeskBlock> GRAY_COMPUTER_DESK = BLOCKS.registerBlock(
            "gray_computer_desk", GrayComputerDeskBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<DiningTableBlock> DINING_TABLE = BLOCKS.registerBlock(
            "dining_table", DiningTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<KioskTableBlock> KIOSK_TABLE = BLOCKS.registerBlock(
            "kiosk_table", KioskTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ArtTableBlock> ART_TABLE = BLOCKS.registerBlock(
            "art_table", ArtTableBlock::new,
            props -> props.strength(2.5F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TeachersDeskBlock> TEACHERS_DESK = BLOCKS.registerBlock(
            "teachers_desk",
            TeachersDeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    // The RIGHT half has no block entity, so pistons must never move just one half
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<ElementaryDeskBlock> ELEMENTARY_DESK = BLOCKS.registerBlock(
            "elementary_desk",
            ElementaryDeskBlock::new,
            props -> props
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    // The model is not a full cube, so neighbouring faces must not be culled
                    .noOcclusion());
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlocks() {}
}
