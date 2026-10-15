package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.block.AwningBlockEntity;
import net.nicomar2009.lsmmod.block.DeskBlockEntity;
import net.nicomar2009.lsmmod.block.ElementaryDeskBlockEntity;
import net.nicomar2009.lsmmod.block.LockerBlockEntity;
import net.nicomar2009.lsmmod.block.LostAndFoundBoxBlockEntity;

import java.util.function.Supplier;

/** Block entity registry. */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LSMMod.MOD_ID);

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity>> SCHOOL_PAINTABLE = BLOCK_ENTITIES.register(
            "school_paintable_block", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_BLOCK.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolProjectingWindowPaintEntity>> SCHOOL_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register(
            "school_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolProjectingWindowPaintEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolPaintableCustomBlockEntity>> SCHOOL_PAINTABLE_CUSTOM = BLOCK_ENTITIES.register(
            "school_paintable_custom_block", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolPaintableCustomBlockEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_CUSTOM_BLOCK.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolDoorFramePaintEntity>> SCHOOL_DOOR_FRAME_PAINT = BLOCK_ENTITIES.register(
            "school_door_frame_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolDoorFramePaintEntity::new,
                    ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE.get(),ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolMiniCubePaintEntity>> SCHOOL_MINI_CUBE_PAINT = BLOCK_ENTITIES.register(
            "school_mini_cube_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolMiniCubePaintEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolGlassStairPaintEntity>> SCHOOL_GLASS_STAIR_PAINT = BLOCK_ENTITIES.register("school_glass_stair_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolGlassStairPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolBathroomColumnPaintEntity>> SCHOOL_BATHROOM_COLUMN_PAINT = BLOCK_ENTITIES.register("school_bathroom_column_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolBathroomColumnPaintEntity::new, ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolEntranceSlabPaintEntity>> SCHOOL_ENTRANCE_SLAB_PAINT = BLOCK_ENTITIES.register("school_entrance_slab_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolEntranceSlabPaintEntity::new, ModBlocks.DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolSlabProjectingWindowPaintEntity>> SCHOOL_SLAB_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register("school_slab_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolSlabProjectingWindowPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.block.SchoolStairProjectingWindowPaintEntity>> SCHOOL_STAIR_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register("school_stair_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.block.SchoolStairProjectingWindowPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS.get()));

    // Only the LEFT half creates a DeskBlockEntity, but the type is valid for the whole block.
    // Only the student's desk has storage; the teacher's desk is decorative and has no block entity.
    public static final Supplier<BlockEntityType<DeskBlockEntity>> STUDENTS_DESK = BLOCK_ENTITIES.register(
            "students_desk",
            () -> new BlockEntityType<>(DeskBlockEntity::new,
                    ModBlocks.STUDENTS_DESK.get()));

    public static final Supplier<BlockEntityType<LockerBlockEntity>> LOCKER = BLOCK_ENTITIES.register(
            "locker",
            () -> new BlockEntityType<>(LockerBlockEntity::new, ModBlocks.LOCKER.get()));

    public static final Supplier<BlockEntityType<ElementaryDeskBlockEntity>> ELEMENTARY_DESK = BLOCK_ENTITIES.register(
            "elementary_desk",
            () -> new BlockEntityType<>(ElementaryDeskBlockEntity::new, ModBlocks.ELEMENTARY_DESK.get()));

    public static final Supplier<BlockEntityType<LostAndFoundBoxBlockEntity>> LOST_AND_FOUND_BOX = BLOCK_ENTITIES.register(
            "lost_and_found_box",
            () -> new BlockEntityType<>(LostAndFoundBoxBlockEntity::new, ModBlocks.LOST_AND_FOUND_BOX.get()));

    public static final Supplier<BlockEntityType<AwningBlockEntity>> AWNING = BLOCK_ENTITIES.register(
            "awning", () -> new BlockEntityType<>(AwningBlockEntity::new,
                    ModBlocks.awningEntityBlocks()));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(ModBlockEntities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, LOST_AND_FOUND_BOX.get(),
                (box, side) -> VanillaContainerWrapper.of(box));
    }
}
