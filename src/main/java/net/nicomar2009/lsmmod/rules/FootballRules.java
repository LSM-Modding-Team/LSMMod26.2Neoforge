package net.nicomar2009.lsmmod.rules;

/** Football tuning in blocks/tick; independent of Minecraft and packet timing. */
public final class FootballRules {
    public static final int CHARGE_TICKS = 24;
    public static final int FOLLOW_TICKS = 3;
    public static final int FORWARD = 1, BACKWARD = 2, LEFT = 4, RIGHT = 8;
    public static final double CONTROL_SPEED = 0.50;
    public static final double NET_REBOUND = 0.02;
    public static final double NET_DRAG = 0.10;
    private static final double[] FOLLOW_WEIGHTS = {0.10, 0.08, 0.07, 0.05};

    private FootballRules() {}

    public record Spin(double side, double top) {
        public Spin add(Spin other) { return new Spin(side + other.side, top + other.top); }
        public Spin decay(double factor) { return new Spin(side * factor, top * factor); }
        public Spin bounded() { return decay(1 / Math.max(1, Math.hypot(side, top))); }
    }

    public record Direction(double x, double y, double z) {}
    public record Bounds(double x0, double y0, double z0, double x1, double y1, double z1) {
        public boolean intersects(Bounds other) {
            return x1 > other.x0 && x0 < other.x1 && y1 > other.y0 && y0 < other.y1 && z1 > other.z0 && z0 < other.z1;
        }
    }

    public static Direction aimDirection(double ballX, double ballY, double ballZ, double targetX, double targetY, double targetZ) {
        double x = targetX - ballX, y = Math.max(0, targetY - ballY), z = targetZ - ballZ;
        double length = Math.sqrt(x*x + y*y + z*z);
        return length < 0.000001 ? new Direction(0, 0, 0) : new Direction(x/length, y/length, z/length);
    }

    /** Central contact area excludes a floor rail merely overlapping a rolling ball's lower edge. */
    public static Bounds contactPatch(Bounds ball, int axis, double movement) {
        return contactPatch(ball, axis, movement, 0.30);
    }
    public static Bounds contactFace(Bounds ball, int axis, double movement) {
        return contactPatch(ball, axis, movement, 0);
    }
    private static Bounds contactPatch(Bounds ball, int axis, double movement, double insetFraction) {
        double[] low = {ball.x0, ball.y0, ball.z0}, high = {ball.x1, ball.y1, ball.z1};
        for (int a = 0; a < 3; a++) {
            double inset = (high[a] - low[a]) * insetFraction;
            if (a != axis) { low[a] += inset; high[a] -= inset; }
        }
        double edge = movement > 0 ? high[axis] : low[axis];
        low[axis] = edge - 0.0001; high[axis] = edge + 0.0001;
        return new Bounds(low[0], low[1], low[2], high[0], high[1], high[2]);
    }

    public static double charge(long ticks) { return Math.max(0, Math.min(1, ticks / (double) CHARGE_TICKS)); }
    public static double shotSpeed(long ticks) { return 0.20 + 1.30 * charge(ticks); }
    public static double movementScale(long ticks) { double q = charge(ticks); return 0.65 - 0.53 * q*q; }
    public static int recoveryTicks(long held) { return 2 + (int) Math.round(5 * charge(held)); }
    public static double recoveryScale(long held, long elapsed) {
        double progress = Math.max(0, Math.min(1, elapsed / (double) recoveryTicks(held)));
        return movementScale(held) + (1 - movementScale(held)) * progress;
    }
    public static double spinGain(long ticks) { double q = charge(ticks); return 0.025 + 0.16 * q*q*q; }
    public static Spin buildSpin(Spin prepared, int keys, long held) {
        return prepared.add(spin(keys, 0).decay(spinGain(held) / FOLLOW_WEIGHTS[0])).bounded();
    }
    public static Spin releaseSpin(Spin prepared, int keys) { return prepared.decay(0.70).add(spin(keys, 0)).bounded(); }

    public static Spin spin(int keys, int sample) {
        if (sample < 0 || sample > FOLLOW_TICKS) return new Spin(0, 0);
        double side = ((keys & RIGHT) != 0 ? 1 : 0) - ((keys & LEFT) != 0 ? 1 : 0);
        double top = ((keys & FORWARD) != 0 ? 1 : 0) - ((keys & BACKWARD) != 0 ? 1 : 0);
        double divisor = Math.max(1, Math.hypot(side, top));
        return new Spin(side / divisor * FOLLOW_WEIGHTS[sample], top / divisor * FOLLOW_WEIGHTS[sample]);
    }

    /** A perpendicular acceleration bends flight without increasing horizontal speed. */
    public static double curveAngle(double speed, Spin spin, double charge) {
        double q = Math.max(0, Math.min(1, charge));
        return Math.min(speed, 1.2) * (0.008 + 0.040 * q*q*q) * spin.side;
    }
    public static double lift(double speed, Spin spin, double charge) {
        double q = Math.max(0, Math.min(1, charge));
        return -Math.min(0.024, speed * (0.003 + 0.020 * q*q)) * spin.top;
    }

    public static double touchDistance(boolean sprint, boolean sneak) { return sneak ? 0.55 : sprint ? 1.10 : 0.78; }
    public static double touchSpeed(double playerSpeed, boolean sprint, boolean sneak) {
        return Math.min(sneak ? 0.18 : sprint ? 0.46 : 0.36, playerSpeed + (sneak ? 0.045 : sprint ? 0.14 : 0.09));
    }
}
