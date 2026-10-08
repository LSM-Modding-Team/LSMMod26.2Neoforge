package net.nicomar2009.lsmmod.entity;

/** Sixteen independent sizes per axis, with an exact vanilla-player option at level fourteen. */
public final class SchoolNpcSize {
    public static final int PLAYER_LEVEL = 14;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 16;

    private SchoolNpcSize() { }

    public static int clampLevel(int level) { return Math.clamp(level, MIN_LEVEL, MAX_LEVEL); }

    private static float interpolate(int level, float smallest, float player, float largest) {
        int bounded = clampLevel(level);
        return bounded <= PLAYER_LEVEL
                ? smallest + (player - smallest) * (bounded - MIN_LEVEL) / (PLAYER_LEVEL - MIN_LEVEL)
                : player + (largest - player) * (bounded - PLAYER_LEVEL) / (MAX_LEVEL - PLAYER_LEVEL);
    }

    // The base mesh spans 16 model units across the arms and 32 from head to feet.
    // AvatarRenderer applies 0.9375 to the vanilla player's mesh.
    public static float modelWidthScale(int level) { return interpolate(level, 0.5F, 0.9375F, 1.0F); }
    public static float modelHeightScale(int level) { return interpolate(level, 0.5F, 0.9375F, 1.0F); }

    // Collision and visible arm span are distinct, just as they are for a vanilla player.
    public static float collisionWidth(int level) { return interpolate(level, 0.3F, 0.6F, 0.64F); }
    public static float collisionHeight(int level) { return interpolate(level, 0.975F, 1.8F, 2.0F); }
    public static float eyeHeight(int level) { return interpolate(level, 0.93F, 1.62F, 1.8F); }
}
