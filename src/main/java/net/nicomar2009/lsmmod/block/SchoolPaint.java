package net.nicomar2009.lsmmod.block;

/** Stable saved IDs: changing the enum order would change existing painted faces. */
public enum SchoolPaint {
    UNPAINTED("school_paintable_block", "minecraft:block/light_gray_concrete"),
    LIGHT("light_school_wall", "lsmmod:block/light_school_wall"),
    DARK("dark_school_wall", "lsmmod:block/dark_school_wall"),
    STREET("street_school_wall", "lsmmod:block/street_school_wall"),
    WHITE("classroom_white_wall", "lsmmod:block/classroom_painted_block");

    private static final SchoolPaint[] COLORS = values();
    public static boolean validId(int id) { return id >= 0 && id < COLORS.length; }
    public final String blockName;
    public final String texture;
    SchoolPaint(String blockName, String texture) { this.blockName = blockName; this.texture = texture; }
    public static SchoolPaint fromId(int id) { return validId(id) ? COLORS[id] : UNPAINTED; }
}
