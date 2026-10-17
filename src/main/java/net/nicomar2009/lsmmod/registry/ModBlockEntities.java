package net.nicomar2009.lsmmod.registry;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolStairProjectingWindowPaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowPaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolProjectingWindowPaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableCustomBlockEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlockEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolGlassStairPaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolEntranceSlabPaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolDoorFramePaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolBathroomColumnPaintEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.modules.awnings.block.AwningBlockEntity;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DeskBlockEntity;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ElementaryDeskBlockEntity;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LockerBlockEntity;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LostAndFoundBoxBlockEntity;

import java.util.function.Supplier;

/** Block entity registry. */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = ModRegistries.BLOCK_ENTITIES;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlockEntity>> SCHOOL_PAINTABLE = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_PAINTABLE;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolProjectingWindowPaintEntity>> SCHOOL_PROJECTING_WINDOW_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_PROJECTING_WINDOW_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableCustomBlockEntity>> SCHOOL_PAINTABLE_CUSTOM = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_PAINTABLE_CUSTOM;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolDoorFramePaintEntity>> SCHOOL_DOOR_FRAME_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_DOOR_FRAME_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintEntity>> SCHOOL_MINI_CUBE_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_MINI_CUBE_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolGlassStairPaintEntity>> SCHOOL_GLASS_STAIR_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_GLASS_STAIR_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolBathroomColumnPaintEntity>> SCHOOL_BATHROOM_COLUMN_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_BATHROOM_COLUMN_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolEntranceSlabPaintEntity>> SCHOOL_ENTRANCE_SLAB_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_ENTRANCE_SLAB_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowPaintEntity>> SCHOOL_SLAB_PROJECTING_WINDOW_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_SLAB_PROJECTING_WINDOW_PAINT;

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolStairProjectingWindowPaintEntity>> SCHOOL_STAIR_PROJECTING_WINDOW_PAINT = net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry.ModuleBlockEntities.SCHOOL_STAIR_PROJECTING_WINDOW_PAINT;

    // Only the LEFT half creates a DeskBlockEntity, but the type is valid for the whole block.
    // Only the student's desk has storage; the teacher's desk is decorative and has no block entity.
    public static final Supplier<BlockEntityType<DeskBlockEntity>> STUDENTS_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlockEntities.STUDENTS_DESK;

    public static final Supplier<BlockEntityType<LockerBlockEntity>> LOCKER = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlockEntities.LOCKER;

    public static final Supplier<BlockEntityType<ElementaryDeskBlockEntity>> ELEMENTARY_DESK = net.nicomar2009.lsmmod.modules.tables_and_desks.registry.ModuleBlockEntities.ELEMENTARY_DESK;

    public static final Supplier<BlockEntityType<LostAndFoundBoxBlockEntity>> LOST_AND_FOUND_BOX = net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlockEntities.LOST_AND_FOUND_BOX;

    public static final Supplier<BlockEntityType<AwningBlockEntity>> AWNING = net.nicomar2009.lsmmod.modules.awnings.registry.ModuleBlockEntities.AWNING;

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(net.nicomar2009.lsmmod.modules.lockers_and_storage.registry.ModuleBlockEntities::registerCapabilities);
    }

}
