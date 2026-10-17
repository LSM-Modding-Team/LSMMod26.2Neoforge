package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;

/** Stable saved IDs: changing the enum order would change existing painted faces. */
public enum SchoolPaint {
    UNPAINTED("school_paintable_block", "minecraft:block/light_gray_concrete"),
    LIGHT("light_school_wall", "lsmmod:block/light_school_wall"),
    DARK("dark_school_wall", "lsmmod:block/dark_school_wall"),
    STREET("street_school_wall", "lsmmod:block/street_school_wall"),
    WHITE("classroom_white_wall", "lsmmod:block/classroom_white_wall"),
    FLOOR("classroom_floor", "lsmmod:block/classroom_floor"),
    YELLOW("yellow_school_block", "lsmmod:block/yellow_school_block"),
    PLAYGROUND_WHITE("white_playground_floor", "lsmmod:block/white_playground_floor"),
    PLAYGROUND_GRAY("gray_playground_floor", "lsmmod:block/gray_playground_floor"),
    PLAYGROUND_GREEN("green_playground_floor", "lsmmod:block/green_playground_floor"),
    PLAYGROUND_ORANGE("orange_playground_floor", "lsmmod:block/orange_playground_floor"),
    DARK_TILED("dark_tiled_floor", "lsmmod:block/dark_tiled_floor"),
    RED_STREET("red_street_wall", "lsmmod:block/red_street_wall"),
    LABORATORY_TILE("school_laboratory_tile", "lsmmod:block/school_laboratory_tile"),
    LABORATORY_TABLE_TILE("laboratory_table_tile", "lsmmod:block/laboratory_table_tile"),
    AUDITORIUM_PARQUET("auditorium_parquet", "lsmmod:block/auditorium_parquet");

    private static final SchoolPaint[] COLORS = values();
    public static boolean validId(int id) { return id >= 0 && id < COLORS.length; }
    public final String blockName;
    public final String texture;
    SchoolPaint(String blockName, String texture) { this.blockName = blockName; this.texture = texture; }
    public static SchoolPaint fromId(int id) { return validId(id) ? COLORS[id] : UNPAINTED; }
}
