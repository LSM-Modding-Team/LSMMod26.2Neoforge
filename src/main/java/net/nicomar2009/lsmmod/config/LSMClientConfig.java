package net.nicomar2009.lsmmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Client presentation preferences; no Jade dependency is required to load the config. */
public final class LSMClientConfig {
    public enum JadeNpcDisplayMode {
        NAME_ONLY,
        LEVELS,
        FULL_STATS
    }

    public enum JadeAppearanceDisplayMode { NONE, TYPES, FULL }

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<JadeNpcDisplayMode> JADE_NPC_DISPLAY_MODE;

    public static final ModConfigSpec.EnumValue<JadeAppearanceDisplayMode> JADE_APPEARANCE_DISPLAY_MODE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("jade");
        JADE_NPC_DISPLAY_MODE = builder
                .comment("School NPC tooltip: NAME_ONLY, LEVELS (stat levels only), or FULL_STATS (levels, multipliers and values).")
                .defineEnum("npcDisplayMode", JadeNpcDisplayMode.FULL_STATS);
        builder.push("appearance");
        JADE_APPEARANCE_DISPLAY_MODE = builder
                .comment("Appearance rows: NONE, TYPES (labels only), FULL (all values). Independent of npcDisplayMode.")
                .defineEnum("displayMode", JadeAppearanceDisplayMode.FULL);
        builder.pop();
        builder.pop();
        SPEC = builder.build();
    }

    private LSMClientConfig() { }
}
