package net.nicomar2009.lsmmod.modules.technology.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolStudyRules;

/** Saved per dimension, so unloading or replacing a PC cannot reset its study cooldown. */
public final class SchoolStudyCooldowns extends SavedData {
    private record Entry(BlockPos pos, long readyAt) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
                Codec.LONG.fieldOf("ready_at").forGetter(Entry::readyAt)
        ).apply(instance, Entry::new));
    }
    private static final SavedDataType<SchoolStudyCooldowns> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "pc_study_cooldowns"), SchoolStudyCooldowns::new,
            Entry.CODEC.listOf().fieldOf("cooldowns").codec()
                    .xmap(SchoolStudyCooldowns::new, SchoolStudyCooldowns::entries), DataFixTypes.LEVEL);
    private final SchoolStudyRules.Cooldowns<BlockPos> cooldowns;

    private SchoolStudyCooldowns() { this(List.of()); }

    private SchoolStudyCooldowns(List<Entry> entries) {
        var saved = new HashMap<BlockPos, Long>();
        for (Entry entry : entries) saved.merge(entry.pos(), entry.readyAt(), Math::max);
        cooldowns = new SchoolStudyRules.Cooldowns<>(saved);
    }

    private List<Entry> entries() {
        return cooldowns.snapshot().entrySet().stream().map(e -> new Entry(e.getKey(), e.getValue())).toList();
    }

    public static SchoolStudyCooldowns get(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }

    public int remainingTicks(BlockPos pc, long now) { return cooldowns.remainingTicks(pc, now); }

    public boolean tryUse(BlockPos pc, long now) {
        if (!cooldowns.tryUse(pc.immutable(), now)) return false;
        setDirty();
        return true;
    }
}
