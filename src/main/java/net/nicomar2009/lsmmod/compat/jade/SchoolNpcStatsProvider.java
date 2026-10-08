package net.nicomar2009.lsmmod.compat.jade;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.config.LSMClientConfig;
import net.nicomar2009.lsmmod.config.LSMClientConfig.JadeNpcDisplayMode;
import net.nicomar2009.lsmmod.entity.SchoolNpcEntity;
import net.nicomar2009.lsmmod.entity.StudentEntity;
import net.nicomar2009.lsmmod.config.LSMClientConfig.JadeAppearanceDisplayMode;
import net.nicomar2009.lsmmod.entity.SchoolNpcStat;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** Send authoritative values: the client must not guess server difficulty or combat state. */
public final class SchoolNpcStatsProvider implements IServerDataProvider<EntityAccessor> {
    public static final SchoolNpcStatsProvider INSTANCE = new SchoolNpcStatsProvider();
    private static final Identifier UID = Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "school_npc_stats");
    private static final String DATA_KEY = "lsmmod_school_stats";

    private SchoolNpcStatsProvider() { }

    @Override
    public Identifier getUid() { return UID; }

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        if (!(accessor.getEntity() instanceof SchoolNpcEntity npc)) return;
        CompoundTag stats = new CompoundTag();
        for (SchoolNpcStat stat : SchoolNpcStat.values()) {
            stats.putInt(stat.nbtKey(), npc.getStatLevel(stat));
        }
        stats.putDouble("MaxHealth", npc.getMaxHealth());
        stats.putDouble("Damage", npc.getMeleeDamage());
        stats.putDouble("MovementSpeed", npc.getAttributeValue(Attributes.MOVEMENT_SPEED));
        stats.putDouble("Range", npc.getPerceptionRange());
        stats.putDouble("AttackInterval", npc.getAttackIntervalTicks() / 20.0);
        data.put(DATA_KEY, stats);
        data.put("lsmmod_npc_appearance", appearance(npc));
    }

    private static CompoundTag appearance(SchoolNpcEntity npc) {
        CompoundTag result = new CompoundTag();
        result.putInt("Width", npc.getWidthLevel());
        result.putInt("Height", npc.getHeightLevel());
        if (npc instanceof StudentEntity student) {
            int classroom = student.getClassGrade();
            result.putString("StudentData.Level", classroom == 0 ? "unassigned" : classroom <= 6 ? "primary" : "secondary");
            result.putInt("StudentData.Grade", classroom <= 6 ? classroom : classroom - 6);
            result.putString("gender", student.getGender());
            result.putInt("skinColor", student.getSkinColor());
            result.putInt("eyeColor", student.getEyeColor());
            result.putInt(student.isFemale() ? "haircutFemale" : "haircutMale", student.getHaircut());
            result.putInt("glassesType", student.getGlassesType());
            if (student.isFemale()) result.putBoolean("freeHair", student.hasFreeHair());
        }
        return result;
    }

    /** Loaded by registerClient only; no client tooltip implementation is loaded on the server. */
    public static final class Client implements IEntityComponentProvider {
        public static final Client INSTANCE = new Client();
        private Client() { }

        @Override
        public Identifier getUid() { return UID; }

        @Override
        public int getDefaultPriority() { return Integer.MAX_VALUE; }

        // Display policy is controlled by the mod config rather than a second Jade toggle.
        @Override
        public boolean isRequired() { return true; }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof SchoolNpcEntity npc)) return;
            JadeNpcDisplayMode mode = LSMClientConfig.JADE_NPC_DISPLAY_MODE.get();
            if (mode != JadeNpcDisplayMode.FULL_STATS) {
                // Run after standard providers so health/armor/mod-name rows do not leak into compact modes.
                tooltip.clear();
                tooltip.add(npc.getName());
            }
            if (mode != JadeNpcDisplayMode.NAME_ONLY) {
                CompoundTag data = accessor.getServerData();
                CompoundTag stats = data.getCompoundOrEmpty(DATA_KEY);
                for (SchoolNpcStat stat : SchoolNpcStat.values()) {
                    int level = SchoolNpcStat.clampLevel(stats.getIntOr(stat.nbtKey(), npc.getStatLevel(stat)));
                    Component label = Component.translatable(stat.translationKey());
                    if (mode == JadeNpcDisplayMode.LEVELS) {
                        tooltip.add(Component.translatable("jade.lsmmod.stat_level_only", label, level));
                        continue;
                    }
                    String multiplier = format(SchoolNpcStat.multiplier(level));
                    // Without Jade server data, synced levels still display, but effective values are omitted.
                    if (!data.contains(DATA_KEY)) {
                        tooltip.add(Component.translatable("jade.lsmmod.stat_level", label, level, multiplier));
                        continue;
                    }
                    String key = switch (stat) {
                        case VITALITY -> "MaxHealth";
                        case STRENGTH -> "Damage";
                        case SPEED -> "MovementSpeed";
                        case PERCEPTION -> "Range";
                        case ATTACK_SPEED -> "AttackInterval";
                    };
                    Component value = Component.translatable("jade.lsmmod.value." + stat.name().toLowerCase(Locale.ROOT),
                            format(stats.getDoubleOr(key, 0.0)));
                    tooltip.add(Component.translatable("jade.lsmmod.stat_value", label, level, multiplier, value));
                }
            }
            appendAppearance(tooltip, accessor, npc);
        }

        private static void appendAppearance(ITooltip tooltip, EntityAccessor accessor, SchoolNpcEntity npc) {
            JadeAppearanceDisplayMode mode = LSMClientConfig.JADE_APPEARANCE_DISPLAY_MODE.get();
            if (mode == JadeAppearanceDisplayMode.NONE) return;
            CompoundTag synced = appearance(npc);
            CompoundTag data = accessor.getServerData().getCompoundOrEmpty("lsmmod_npc_appearance");
            tooltip.add(Component.translatable("jade.lsmmod.appearance"));
            String[] keys = npc instanceof StudentEntity student
                    ? (student.isFemale()
                        ? new String[]{"Width", "Height", "StudentData.Level", "StudentData.Grade", "gender", "skinColor", "eyeColor", "haircutFemale", "freeHair", "glassesType"}
                        : new String[]{"Width", "Height", "StudentData.Level", "StudentData.Grade", "gender", "skinColor", "eyeColor", "haircutMale", "glassesType"})
                    : new String[]{"Width", "Height"};
            for (String key : keys) {
                Component label = Component.translatable("appearance.lsmmod." + key);
                if (mode == JadeAppearanceDisplayMode.TYPES) {
                    tooltip.add(label);
                    continue;
                }
                Component value;
                if (key.equals("StudentData.Level")) {
                    value = Component.literal(data.getStringOr(key, synced.getStringOr(key, "unassigned")));
                } else if (key.equals("gender")) {
                    String gender = data.getStringOr(key, synced.getStringOr(key, "male"));
                    value = Component.literal(gender + " / ").append(Component.translatable("appearance.lsmmod.gender." + gender));
                } else if (key.equals("freeHair")) {
                    boolean loose = data.getBooleanOr(key, synced.getBooleanOr(key, false));
                    value = Component.literal(loose + " / ").append(Component.translatable("appearance.lsmmod.hair." + (loose ? "loose" : "tied")));
                } else {
                    int index = data.getIntOr(key, synced.getIntOr(key, -1));
                    value = index < 0 ? Component.literal("-1 / ").append(Component.translatable("appearance.lsmmod.unset"))
                            : key.equals("glassesType") && index == 0 ? Component.literal("0 / ").append(Component.translatable("appearance.lsmmod.no_glasses"))
                            : Component.literal(index + net.nicomar2009.lsmmod.util.NpcAppearanceCatalog.details(key, index));
                }
                tooltip.add(Component.translatable("jade.lsmmod.appearance_value", label, key, value));
            }
        }

        private static String format(double value) {
            return String.format(Locale.ROOT, "%.3f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
    }
}
