package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.registry;
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
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCK_ENTITIES;

public final class ModuleBlockEntities {
    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlockEntity>> SCHOOL_PAINTABLE = BLOCK_ENTITIES.register(
            "school_paintable_block", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableBlockEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_BLOCK.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolProjectingWindowPaintEntity>> SCHOOL_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register(
            "school_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolProjectingWindowPaintEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableCustomBlockEntity>> SCHOOL_PAINTABLE_CUSTOM = BLOCK_ENTITIES.register(
            "school_paintable_custom_block", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaintableCustomBlockEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_CUSTOM_BLOCK.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolDoorFramePaintEntity>> SCHOOL_DOOR_FRAME_PAINT = BLOCK_ENTITIES.register(
            "school_door_frame_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolDoorFramePaintEntity::new,
                    ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE.get(),ModBlocks.SCHOOL_ENTRANCE_DOOR_PAINTABLE.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintEntity>> SCHOOL_MINI_CUBE_PAINT = BLOCK_ENTITIES.register(
            "school_mini_cube_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintEntity::new,
                    ModBlocks.SCHOOL_PAINTABLE_MINI_CUBE_BLOCK.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolGlassStairPaintEntity>> SCHOOL_GLASS_STAIR_PAINT = BLOCK_ENTITIES.register("school_glass_stair_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolGlassStairPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_GLASS_STAIRS.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolBathroomColumnPaintEntity>> SCHOOL_BATHROOM_COLUMN_PAINT = BLOCK_ENTITIES.register("school_bathroom_column_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolBathroomColumnPaintEntity::new, ModBlocks.BATHROOM_DOOR_WIDE_PAINTABLE_COLUMN.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolEntranceSlabPaintEntity>> SCHOOL_ENTRANCE_SLAB_PAINT = BLOCK_ENTITIES.register("school_entrance_slab_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolEntranceSlabPaintEntity::new, ModBlocks.DOUBLE_SCHOOL_ENTRANCE_PAINTABLE_SLAB.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowPaintEntity>> SCHOOL_SLAB_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register("school_slab_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolSlabProjectingWindowPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_SLAB.get()));

    public static final Supplier<BlockEntityType<net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolStairProjectingWindowPaintEntity>> SCHOOL_STAIR_PROJECTING_WINDOW_PAINT = BLOCK_ENTITIES.register("school_stair_projecting_window_paint", () -> new BlockEntityType<>(net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolStairProjectingWindowPaintEntity::new, ModBlocks.SCHOOL_PAINTABLE_PROJECTING_WINDOW_STAIRS.get()));
    private ModuleBlockEntities() {}
}
