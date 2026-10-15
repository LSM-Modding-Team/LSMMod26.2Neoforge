package net.nicomar2009.lsmmod.rules;

import java.util.HashMap;
import java.util.Map;

/** Study makes teachers neutral; it never erases witnessed violence. */
public final class SchoolStudyRules {
    public static final int STUDY_TICKS = 3 * 60 * 20;
    public static final int PC_COOLDOWN_TICKS = 5 * 60 * 20;

    private SchoolStudyRules() { }

    public static boolean teacherHostile(boolean witnessedAttack, boolean uniform, boolean studious) {
        return witnessedAttack || !(uniform || studious);
    }

    /** One clock per physical PC, shared by every player and every cell of its desk. */
    public static final class Cooldowns<K> {
        private final Map<K, Long> readyAt = new HashMap<>();

        public Cooldowns(Map<K, Long> saved) {
            saved.forEach((key, tick) -> {
                if (tick >= 0) readyAt.put(key, tick);
            });
        }

        public int remainingTicks(K pc, long now) {
            long deadline = readyAt.getOrDefault(pc, now);
            if (deadline <= now) return 0;
            long remaining = deadline - now;
            // A restored or edited clock must not lock a PC for more than one cooldown.
            return remaining > PC_COOLDOWN_TICKS ? 0 : (int) remaining;
        }

        public boolean tryUse(K pc, long now) {
            if (remainingTicks(pc, now) > 0) return false;
            readyAt.keySet().removeIf(key -> remainingTicks(key, now) == 0);
            readyAt.put(pc, now > Long.MAX_VALUE - PC_COOLDOWN_TICKS
                    ? Long.MAX_VALUE : now + PC_COOLDOWN_TICKS);
            return true;
        }

        public Map<K, Long> snapshot() { return Map.copyOf(readyAt); }
    }
}
