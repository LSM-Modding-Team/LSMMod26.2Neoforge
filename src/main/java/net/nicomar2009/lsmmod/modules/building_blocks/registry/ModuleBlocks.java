package net.nicomar2009.lsmmod.modules.building_blocks.registry;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import net.nicomar2009.lsmmod.block.*;
import net.nicomar2009.lsmmod.modules.building_blocks.block.StreetSchoolBarsBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolYellowBorderWallBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolWoodenHandrailBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolWallRailingBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolGateBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.SchoolCircularColumnBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.FloorTransitionBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.EntrancePavementSlabBlock;
import net.nicomar2009.lsmmod.modules.building_blocks.block.BathroomFloorBlock;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCKS;

public final class ModuleBlocks {
    public static final DeferredBlock<Block> SCHOOL_LABORATORY_TILE = BLOCKS.registerBlock("school_laboratory_tile", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE));

    public static final DeferredBlock<Block> LABORATORY_TABLE_TILE = BLOCKS.registerBlock("laboratory_table_tile", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE));

    public static final DeferredBlock<Block> AUDITORIUM_PARQUET = BLOCKS.registerBlock("auditorium_parquet", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.WOOD));

    public static final DeferredBlock<Block> AUDITORIUM_STAGE = BLOCKS.registerBlock("auditorium_stage", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.WOOD));

    public static final DeferredBlock<Block> DARK_TILED_FLOOR = BLOCKS.registerBlock("dark_tiled_floor", Block::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolCircularColumnBlock> SCHOOL_CIRCULAR_COLUMN = BLOCKS.registerBlock("school_circular_column", SchoolCircularColumnBlock::new, props -> props.strength(1.8F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape().requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> SCHOOL_FOUNDATION = BLOCKS.registerBlock(
            "school_foundation", Block::new,
            props -> props.strength(3.0F, 8.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> YELLOW_SCHOOL_BLOCK = BLOCKS.registerBlock(
            "yellow_school_block", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<FloorTransitionBlock> TILED_PARQUET_FLOOR = BLOCKS.registerBlock(
            "tiled_parquet_floor", FloorTransitionBlock::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> WHITE_PLAYGROUND_FLOOR = BLOCKS.registerBlock("white_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> GRAY_PLAYGROUND_FLOOR = BLOCKS.registerBlock("gray_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> GREEN_PLAYGROUND_FLOOR = BLOCKS.registerBlock("green_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> ORANGE_PLAYGROUND_FLOOR = BLOCKS.registerBlock("orange_playground_floor", Block::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> CLASSROOM_WHITE_WALL = BLOCKS.registerBlock(
            "classroom_white_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> CLASSROOM_PAINTED_BLOCK = BLOCKS.registerBlock(
            "classroom_painted_block", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> SCHOOL_SYNTHETIC_GRASS = BLOCKS.registerBlock(
            "school_synthetic_grass", Block::new,
            props -> props.strength(.8F).sound(SoundType.MOSS));

    public static final DeferredBlock<Block> RED_STREET_WALL = BLOCKS.registerBlock(
            "red_street_wall", Block::new,
            props -> props.strength(1.8F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolWoodenHandrailBlock> SCHOOL_WOODEN_HANDRAIL = BLOCKS.registerBlock("school_wooden_handrail", SchoolWoodenHandrailBlock::new, props -> props.strength(2.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<EntrancePavementSlabBlock> SCHOOL_ENTRANCE_PAVEMENT_SLAB = BLOCKS.registerBlock("school_entrance_pavement_slab", EntrancePavementSlabBlock::new, props -> props.strength(1.8F,6.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());

    public static final DeferredBlock<SchoolWallRailingBlock> LIGHT_SCHOOL_WALL_RAILING = BLOCKS.registerBlock(
            "light_school_wall_railing", SchoolWallRailingBlock::new,
            props -> props.strength(1.5F).sound(SoundType.STONE).noOcclusion().forceSolidOn());

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
    private ModuleBlocks() {}
}
