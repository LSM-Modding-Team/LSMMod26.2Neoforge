import net.nicomar2009.lsmmod.rules.FootballRules;

/** Pure gameplay checks; no Minecraft classes, mod compilation or game launch. */
public class RulesCheck {
    static int failures = 0;
    static int total = 0;

    static void check(String name, boolean actual, boolean expected) {
        total++;
        if (actual != expected) {
            failures++;
            System.out.println("FAIL: " + name + " (expected " + expected + ")");
        }
    }

    public static void main(String[] args) {
        // Football acceptance checks: power limits, foot follow-through and flight direction.
        check("tap is weaker than a charged pass", FootballRules.shotSpeed(0) < FootballRules.shotSpeed(12), true);
        check("power increases up to full charge", FootballRules.shotSpeed(12) < FootballRules.shotSpeed(24), true);
        check("holding beyond full charge does not add power", FootballRules.shotSpeed(24) == FootballRules.shotSpeed(2400), true);
        check("negative charge cannot lower the tap speed", FootballRules.shotSpeed(-1) == FootballRules.shotSpeed(0), true);
        var right = new FootballRules.Spin(0, 0);
        for (int sample = 0; sample <= FootballRules.FOLLOW_TICKS; sample++) right = right.add(FootballRules.spin(FootballRules.RIGHT, sample));
        check("a completed right-foot sweep stays within the spin cap", Math.abs(right.side() - 1) < 0.000001, true);
        check("the release has more influence than the remaining follow-through", FootballRules.spin(FootballRules.RIGHT, 0).side() > 0.5, true);
        check("later input cannot steer the shot", FootballRules.spin(15, 4).equals(new FootballRules.Spin(0, 0)), true);
        check("opposing movement keys cancel spin", FootballRules.spin(15, 0).equals(new FootballRules.Spin(0, 0)), true);
        for (int keys = 0; keys < 16; keys++) {
            var sum = new FootballRules.Spin(0, 0);
            for (int sample = 0; sample <= FootballRules.FOLLOW_TICKS; sample++) sum = sum.add(FootballRules.spin(keys, sample));
            check("spin cap for keys " + keys, Math.hypot(sum.side(), sum.top()) <= 1.000001, true);
        }
        double speed = FootballRules.shotSpeed(24), angle = FootballRules.curveAngle(speed, right);
        // Looking south (+Z), rightward spin must bend toward west (-X).
        check("D bends toward the shooter's right", -speed * Math.sin(angle) < 0, true);
        check("curve preserves horizontal kinetic speed", Math.abs(Math.hypot(speed * Math.sin(angle), speed * Math.cos(angle)) - speed) < 0.000001, true);
        check("W adds dip", FootballRules.lift(speed, FootballRules.spin(FootballRules.FORWARD, 0)) < 0, true);
        check("S adds lift", FootballRules.lift(speed, FootballRules.spin(FootballRules.BACKWARD, 0)) > 0, true);
        check("sprinting exposes the ball farther than walking", FootballRules.touchDistance(true, false) > FootballRules.touchDistance(false, false), true);
        check("crouching keeps the ball closer", FootballRules.touchDistance(false, true) < FootballRules.touchDistance(false, false), true);
        check("sprint touches stay below the controllable speed", FootballRules.touchSpeed(0.28, true, false) < FootballRules.CONTROL_SPEED, true);
        check("the net removes over 90 percent of normal impact energy", FootballRules.NET_REBOUND * FootballRules.NET_REBOUND < 0.1, true);
        check("the net also damps tangential motion", FootballRules.NET_DRAG < 0.25, true);


        System.out.println(total - failures + "/" + total + " checks passed");
        System.exit(failures == 0 ? 0 : 1);
    }
}
