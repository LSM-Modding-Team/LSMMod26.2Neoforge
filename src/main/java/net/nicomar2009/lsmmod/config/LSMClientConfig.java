package net.nicomar2009.lsmmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Client presentation preferences; no Jade dependency is required to load the config. */
public final class LSMClientConfig {
    public enum JadeNpcDisplayMode {
        NAME_ONLY,
        LEVELS,
        FULL_STATS
    }

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<JadeNpcDisplayMode> JADE_NPC_DISPLAY_MODE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("jade");
        JADE_NPC_DISPLAY_MODE = builder
                .comment("School NPC tooltip: NAME_ONLY, LEVELS (stat levels only), or FULL_STATS (levels, multipliers and values).")
                .defineEnum("npcDisplayMode", JadeNpcDisplayMode.FULL_STATS);
        builder.pop();
        SPEC = builder.build();
    }

    private LSMClientConfig() { }
}
