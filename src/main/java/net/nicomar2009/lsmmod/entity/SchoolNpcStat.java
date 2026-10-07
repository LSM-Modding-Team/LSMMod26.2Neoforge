package net.nicomar2009.lsmmod.entity;

/** Persistent universal stat levels; level three leaves the role's base value unchanged. */
public enum SchoolNpcStat {
    VITALITY("Vitality", "Vitalidad", "vitality"),
    STRENGTH("Strength", "Fuerza", "strength"),
    SPEED("Speed", "Velocidad", "speed"),
    PERCEPTION("Perception", "Percepcion", "perception"),
    ATTACK_SPEED("AttackSpeed", "VelocidadAtaque", "attack_speed");

    private final String nbtKey;
    private final String translationSuffix;
    private final String legacyNbtKey;

    SchoolNpcStat(String nbtKey, String legacyNbtKey, String translationSuffix) {
        this.nbtKey = nbtKey;
        this.legacyNbtKey = legacyNbtKey;
        this.translationSuffix = translationSuffix;
    }

    public String nbtKey() { return nbtKey; }
    /** Read-only migration alias; saves always use the English key. */
    public String legacyNbtKey() { return legacyNbtKey; }
    public String translationKey() { return "stat.lsmmod." + translationSuffix; }
    public static int clampLevel(int level) { return Math.clamp(level, 1, 5); }
    public static double multiplier(int level) { return 0.625 + 0.125 * clampLevel(level); }
}
