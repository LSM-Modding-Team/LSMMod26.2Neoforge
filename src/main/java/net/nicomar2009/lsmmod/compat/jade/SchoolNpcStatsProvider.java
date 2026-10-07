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
            if (mode == JadeNpcDisplayMode.NAME_ONLY) return;
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

        private static String format(double value) {
            return String.format(Locale.ROOT, "%.3f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
    }
}
