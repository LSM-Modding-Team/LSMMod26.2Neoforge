package net.nicomar2009.lsmmod.modules.school_supplies.registry;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.nicomar2009.lsmmod.modules.school_supplies.item.RulerItem;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.ITEMS;

public final class ModuleItems {
    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolSandpaperItem> SCHOOL_SANDPAPER = ITEMS.registerItem("school_sandpaper", net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolSandpaperItem::new);

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolHammerItem> SCHOOL_HAMMER = ITEMS.registerItem("school_hammer", net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolHammerItem::new);

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> LIGHT_SCHOOL = ITEMS.registerItem("light_school", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.LIGHT, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> DARK_SCHOOL = ITEMS.registerItem("dark_school", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.DARK, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> STREET_SCHOOL = ITEMS.registerItem("street_school", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.STREET, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> CLASSROOM_WHITE = ITEMS.registerItem("classroom_white", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.WHITE, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> CLASSROOM_FLOOR_PAINT = ITEMS.registerItem("classroom_floor_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.FLOOR, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> YELLOW_SCHOOL_PAINT = ITEMS.registerItem("yellow_school_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.YELLOW, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> WHITE_PLAYGROUND_FLOOR_PAINT = ITEMS.registerItem("white_playground_floor_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.PLAYGROUND_WHITE, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> GRAY_PLAYGROUND_FLOOR_PAINT = ITEMS.registerItem("gray_playground_floor_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.PLAYGROUND_GRAY, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> GREEN_PLAYGROUND_FLOOR_PAINT = ITEMS.registerItem("green_playground_floor_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.PLAYGROUND_GREEN, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> ORANGE_PLAYGROUND_FLOOR_PAINT = ITEMS.registerItem("orange_playground_floor_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.PLAYGROUND_ORANGE, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> DARK_TILED_FLOOR_PAINT = ITEMS.registerItem("dark_tiled_floor_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.DARK_TILED, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> RED_STREET_WALL_PAINT = ITEMS.registerItem("red_street_wall_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.RED_STREET, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> SCHOOL_LABORATORY_TILE_PAINT = ITEMS.registerItem("school_laboratory_tile_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.LABORATORY_TILE, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> LABORATORY_TABLE_TILE_PAINT = ITEMS.registerItem("laboratory_table_tile_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.LABORATORY_TABLE_TILE, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem> AUDITORIUM_PARQUET_PAINT = ITEMS.registerItem("auditorium_parquet_paint", props -> new net.nicomar2009.lsmmod.modules.school_supplies.item.SchoolPaintItem(net.nicomar2009.lsmmod.block.SchoolPaint.AUDITORIUM_PARQUET, props));

    public static final DeferredItem<net.nicomar2009.lsmmod.modules.school_supplies.item.PaintCleanerItem> PAINT_CLEANER = ITEMS.registerItem("paint_cleaner", net.nicomar2009.lsmmod.modules.school_supplies.item.PaintCleanerItem::new);

    public static final DeferredItem<RulerItem> WOODEN_RULER =
            ITEMS.registerItem("wooden_ruler", props -> new RulerItem(props, ToolMaterial.WOOD));

    public static final DeferredItem<RulerItem> STONE_RULER =
            ITEMS.registerItem("stone_ruler", props -> new RulerItem(props, ToolMaterial.STONE));

    public static final DeferredItem<RulerItem> GOLDEN_RULER =
            ITEMS.registerItem("golden_ruler", props -> new RulerItem(props, ToolMaterial.GOLD));

    public static final DeferredItem<RulerItem> IRON_RULER =
            ITEMS.registerItem("iron_ruler", props -> new RulerItem(props, ToolMaterial.IRON));

    public static final DeferredItem<RulerItem> DIAMOND_RULER =
            ITEMS.registerItem("diamond_ruler", props -> new RulerItem(props, ToolMaterial.DIAMOND));

    public static final DeferredItem<RulerItem> NETHERITE_RULER =
            ITEMS.registerItem("netherite_ruler", props -> new RulerItem(props.fireResistant(), ToolMaterial.NETHERITE));

    private ModuleItems() {}
}
