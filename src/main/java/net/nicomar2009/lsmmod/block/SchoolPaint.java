package net.nicomar2009.lsmmod.block;

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
    PLAYGROUND_ORANGE("orange_playground_floor", "lsmmod:block/orange_playground_floor");

    private static final SchoolPaint[] COLORS = values();
    public static boolean validId(int id) { return id >= 0 && id < COLORS.length; }
    public final String blockName;
    public final String texture;
    SchoolPaint(String blockName, String texture) { this.blockName = blockName; this.texture = texture; }
    public static SchoolPaint fromId(int id) { return validId(id) ? COLORS[id] : UNPAINTED; }
}
