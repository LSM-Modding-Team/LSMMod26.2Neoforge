package net.nicomar2009.lsmmod.rules;

/** Football tuning in blocks/tick; independent of Minecraft and packet timing. */
public final class FootballRules {
    public static final int CHARGE_TICKS = 24;
    public static final int FOLLOW_TICKS = 3;
    public static final int FORWARD = 1, BACKWARD = 2, LEFT = 4, RIGHT = 8;
    public static final double CONTROL_SPEED = 0.50;
    public static final double NET_REBOUND = 0.08;
    public static final double NET_DRAG = 0.22;
    private static final double[] FOLLOW_WEIGHTS = {0.55, 0.25, 0.15, 0.05};

    private FootballRules() {}

    public record Spin(double side, double top) {
        public Spin add(Spin other) { return new Spin(side + other.side, top + other.top); }
        public Spin decay(double factor) { return new Spin(side * factor, top * factor); }
    }

    public static double charge(long ticks) { return Math.max(0, Math.min(1, ticks / (double) CHARGE_TICKS)); }
    public static double shotSpeed(long ticks) { return 0.20 + 1.30 * charge(ticks); }

    public static Spin spin(int keys, int sample) {
        if (sample < 0 || sample > FOLLOW_TICKS) return new Spin(0, 0);
        double side = ((keys & RIGHT) != 0 ? 1 : 0) - ((keys & LEFT) != 0 ? 1 : 0);
        double top = ((keys & FORWARD) != 0 ? 1 : 0) - ((keys & BACKWARD) != 0 ? 1 : 0);
        double divisor = Math.max(1, Math.hypot(side, top));
        return new Spin(side / divisor * FOLLOW_WEIGHTS[sample], top / divisor * FOLLOW_WEIGHTS[sample]);
    }

    /** A perpendicular acceleration bends flight without increasing horizontal speed. */
    public static double curveAngle(double speed, Spin spin) { return Math.min(0.028, speed * 0.022) * spin.side; }
    public static double lift(double speed, Spin spin) { return -Math.min(0.014, speed * 0.011) * spin.top; }

    public static double touchDistance(boolean sprint, boolean sneak) { return sneak ? 0.55 : sprint ? 1.10 : 0.78; }
    public static double touchSpeed(double playerSpeed, boolean sprint, boolean sneak) {
        return Math.min(sneak ? 0.18 : sprint ? 0.46 : 0.36, playerSpeed + (sneak ? 0.045 : sprint ? 0.14 : 0.09));
    }
}
