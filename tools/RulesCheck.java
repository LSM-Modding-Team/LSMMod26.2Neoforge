import net.nicomar2009.lsmmod.rules.FootballRules;
import net.nicomar2009.lsmmod.rules.SchoolStudyRules;
import net.nicomar2009.lsmmod.block.SportsGoalShapes;

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

    static FootballRules.Bounds rotate(FootballRules.Bounds box, int turns) {
        for (int i = 0; i < turns; i++) box = new FootballRules.Bounds(1-box.z1(),box.y0(),box.x0(),1-box.z0(),box.y1(),box.x1());
        return box;
    }

    static int material(FootballRules.Bounds patch, int turns) {
        boolean net = false;
        for (int i = 0; i < 162; i++) {
            int c = i < 144 ? i%8 : 8, r = i < 144 ? i/8%6 : (i-144)%6, d = i < 144 ? i/48 : (i-144)/6;
            for (double[] box : SportsGoalShapes.RIGID_CELLS[i]) {
                if (patch.intersects(rotate(new FootballRules.Bounds(c+box[0]/16,r+box[1]/16,d+box[2]/16,
                        c+box[3]/16,r+box[4]/16,d+box[5]/16),turns))) return -1;
            }
            for (double[] box : SportsGoalShapes.NET_CELLS[i]) {
                net |= patch.intersects(rotate(new FootballRules.Bounds(c+box[0]/16,r+box[1]/16,d+box[2]/16,
                        c+box[3]/16,r+box[4]/16,d+box[5]/16),turns));
            }
        }
        return net ? 1 : 0;
    }

    static int contact(FootballRules.Bounds ball, int axis, double incoming, int turns) {
        int result = material(FootballRules.contactPatch(ball,axis,incoming),turns);
        return result == 0 ? material(FootballRules.contactFace(ball,axis,incoming),turns) : result;
    }

    public static void main(String[] args) {
        study();
        // Football acceptance checks: power limits, foot follow-through and flight direction.
        check("tap is weaker than a charged pass", FootballRules.shotSpeed(0) < FootballRules.shotSpeed(12), true);
        check("power increases up to full charge", FootballRules.shotSpeed(12) < FootballRules.shotSpeed(24), true);
        check("holding beyond full charge does not add power", FootballRules.shotSpeed(24) == FootballRules.shotSpeed(2400), true);
        check("negative charge cannot lower the tap speed", FootballRules.shotSpeed(-1) == FootballRules.shotSpeed(0), true);
        double carried = FootballRules.shotSpeed(8, Math.abs(.28 - .27));
        double runUp = FootballRules.shotSpeed(8, .28);
        double oncoming = FootballRules.shotSpeed(8, Math.abs(.28 - (-.28)));
        check("dribbling at the same speed adds less than two bar points", FootballRules.charge(8,.01) - FootballRules.charge(8) < .02, true);
        check("running into a stationary ball adds more power than dribbling", runUp > carried, true);
        check("an oncoming ball adds more power than a stationary ball", oncoming > runUp, true);
        check("no relative motion preserves charge-only power", FootballRules.shotSpeed(8, 0) == FootballRules.shotSpeed(8), true);
        check("extreme impact speed cannot exceed the original cap", FootballRules.shotSpeed(0, 100) <= FootballRules.MAX_SHOT_SPEED
                && FootballRules.shotSpeed(12, 100) == FootballRules.MAX_SHOT_SPEED, true);
        check("full charge plus a run-up stays at the same cap", FootballRules.shotSpeed(2400, .8) == FootballRules.shotSpeed(24), true);
        check("a normal run-up adds fifty points to the total bar", Math.abs(FootballRules.charge(8,.28)
                - FootballRules.charge(8) - .5) < .000001, true);
        check("an oncoming ball can add more than fifty bar points", FootballRules.charge(0,.42) > .5, true);
        check("a head-on arrival can fill the bar without precharge", FootballRules.charge(0,.56) == 1, true);
        check("run-up bonus is independent of prepared charge", Math.abs((FootballRules.charge(4,.28)-FootballRules.charge(4))
                - (FootballRules.charge(8,.28)-FootballRules.charge(8))) < .000001, true);
        check("quarter charge plus a run-up becomes three-quarter charge", FootballRules.shotSpeed(6,.28) == FootballRules.shotSpeed(18), true);
        check("three-quarter charge plus a run-up caps at the full bar", FootballRules.charge(18,.28) == 1, true);
        check("negative relative speed cannot remove charge", FootballRules.charge(8,-1) == FootballRules.charge(8), true);
        var still = new FootballRules.Direction(0,0,0);
        var sprint = new FootballRules.Direction(0,0,.28);
        var controlled = new FootballRules.Direction(0,0,.27);
        var slowed = new FootballRules.Direction(0,0,.05);
        var ballMotion = new FootballRules.MotionHistory();
        var approach = new FootballRules.MotionHistory();
        for (int tick = 0; tick <= 7; tick++) {
            ballMotion.record(tick, tick < 7 ? still : controlled);
            approach.record(tick, tick == 0 ? sprint : slowed);
        }
        var beforeTouch = ballMotion.priorVelocity(7,controlled);
        double approachSpeed = approach.peakRelativeSpeed(7,beforeTouch,slowed);
        check("regression: a last-moment dribble cannot erase the stationary-ball sample", beforeTouch.equals(still), true);
        check("recent running survives charge deceleration", Math.abs(approachSpeed-.28) < .000001, true);
        check("a run-up still adds fifty bar points after an automatic dribble", Math.abs(FootballRules.charge(8,approachSpeed)
                - FootballRules.charge(8) - .5) < .000001, true);
        approach.record(8,slowed);
        check("old sprint motion expires after the contact window", Math.abs(approach.peakRelativeSpeed(8,still,slowed)-.05) < .000001, true);
        for (int tick = 8; tick <= 16; tick++) {
            ballMotion.record(tick,controlled); approach.record(tick,sprint);
        }
        check("sustained dribbling adds less than two bar points", FootballRules.charge(8,
                approach.peakRelativeSpeed(16,ballMotion.priorVelocity(16,controlled),sprint)) - FootballRules.charge(8) < .02, true);
        check("expired history uses the current ball instead of a stale stationary sample", ballMotion.priorVelocity(100,controlled).equals(controlled), true);
        ballMotion.record(100,slowed);
        check("a newly resumed history starts from its fresh sample", ballMotion.priorVelocity(100,controlled).equals(slowed), true);
        ballMotion.record(0,still);
        check("clock rewind clears old motion", ballMotion.priorVelocity(0,controlled).equals(still), true);
        check("a new ball safely uses its actual velocity", new FootballRules.MotionHistory().priorVelocity(0,controlled).equals(controlled), true);
        var sameTick = new FootballRules.MotionHistory();
        sameTick.record(0,sprint); sameTick.record(0,slowed);
        check("same-tick charge updates preserve the approach before slowdown", sameTick.peakRelativeSpeed(0,still,slowed) == .28, true);
        check("relative speed includes all three vector components", Math.abs(new FootballRules.Direction(.2,.2,.2)
                .relativeSpeed(still) - Math.sqrt(.12)) < .000001, true);
        var right = new FootballRules.Spin(0, 0);
        for (int tick = 1; tick <= 24; tick++) right = FootballRules.buildSpin(right, FootballRules.RIGHT, tick);
        right = FootballRules.releaseSpin(right, FootballRules.RIGHT);
        for (int sample = 1; sample <= FootballRules.FOLLOW_TICKS; sample++) right = right.add(FootballRules.spin(FootballRules.RIGHT, sample));
        check("a completed right-foot sweep stays within the spin cap", Math.abs(right.side() - 1) < 0.000001, true);
        check("holding movement through the charge contributes most of the spin", right.side() > 0.9, true);
        check("later input cannot steer the shot", FootballRules.spin(15, 4).equals(new FootballRules.Spin(0, 0)), true);
        check("opposing movement keys cancel spin", FootballRules.spin(15, 0).equals(new FootballRules.Spin(0, 0)), true);
        for (int keys = 0; keys < 16; keys++) {
            var sum = new FootballRules.Spin(0, 0);
            for (int tick = 1; tick <= 200; tick++) sum = FootballRules.buildSpin(sum, keys, tick);
            sum = FootballRules.releaseSpin(sum, keys);
            for (int sample = 1; sample <= FootballRules.FOLLOW_TICKS; sample++) sum = sum.add(FootballRules.spin(keys, sample));
            check("spin cap for keys " + keys, Math.hypot(sum.side(), sum.top()) <= 1.000001, true);
        }
        double speed = FootballRules.shotSpeed(24), angle = FootballRules.curveAngle(speed, right, 1, 1);
        // Looking south (+Z), rightward spin must bend toward west (-X).
        check("D bends toward the shooter's right", -speed * Math.sin(angle) < 0, true);
        check("curve preserves horizontal kinetic speed", Math.abs(Math.hypot(speed * Math.sin(angle), speed * Math.cos(angle)) - speed) < 0.000001, true);
        check("W now adds lift", FootballRules.lift(speed, FootballRules.spin(FootballRules.FORWARD, 0), 1, 1) > 0, true);
        check("S now adds dip", FootballRules.lift(speed, FootballRules.spin(FootballRules.BACKWARD, 0), 1, 1) < 0, true);
        check("stronger strikes have more curl at the same speed and charge", FootballRules.curveAngle(speed,right,.5,1)
                > FootballRules.curveAngle(speed,right,.5,.3), true);
        var backspin = new FootballRules.Spin(0,-1);
        check("stronger strikes also amplify lift", FootballRules.lift(speed,backspin,.5,1) > FootballRules.lift(speed,backspin,.5,.3), true);
        check("full-strength curl is visibly stronger than weaker strikes", FootballRules.curveAngle(speed,right,.5,1)
                > FootballRules.curveAngle(speed,right,.5,.3) * 2, true);
        check("a strong full-spin shot has a substantial flight bend", angle > .10, true);
        check("even strong backspin cannot overcome football gravity", FootballRules.lift(speed,backspin,1,1) < .05, true);
        check("power alone cannot create spin", FootballRules.curveAngle(speed,new FootballRules.Spin(0,0),1,1) == 0
                && FootballRules.lift(speed,new FootballRules.Spin(0,0),1,1) == 0, true);
        check("sprinting exposes the ball farther than walking", FootballRules.touchDistance(true, false) > FootballRules.touchDistance(false, false), true);
        check("crouching keeps the ball closer", FootballRules.touchDistance(false, true) < FootballRules.touchDistance(false, false), true);
        check("sprint touches stay below the controllable speed", FootballRules.touchSpeed(0.28, true, false) < FootballRules.CONTROL_SPEED, true);
        check("the net removes over 90 percent of normal impact energy", FootballRules.NET_REBOUND * FootballRules.NET_REBOUND < 0.1, true);
        check("the net also damps tangential motion", FootballRules.NET_DRAG < 0.25, true);
        var shortHold = FootballRules.buildSpin(new FootballRules.Spin(0,0), FootballRules.RIGHT, 24);
        var longHold = shortHold;
        for (int tick = 25; tick <= 28; tick++) longHold = FootballRules.buildSpin(longHold, FootballRules.RIGHT, tick);
        check("longer directional hold at high charge creates more spin", longHold.side() > shortHold.side() * 3, true);
        check("high charge amplifies directional duration more strongly", FootballRules.spinGain(24) > FootballRules.spinGain(12) * 3, true);
        check("full-charge curl exceeds half-charge curl", FootballRules.curveAngle(speed,right,1,1) > FootballRules.curveAngle(speed,right,.5,1) * 3, true);
        check("charge slowdown increases monotonically", FootballRules.movementScale(24) < FootballRules.movementScale(12)
                && FootballRules.movementScale(12) < FootballRules.movementScale(0), true);
        check("full charge leaves about 12 percent movement", Math.abs(FootballRules.movementScale(24)-.12) < .000001, true);
        check("high charge recovers longer than a tap", FootballRules.recoveryTicks(24) > FootballRules.recoveryTicks(0), true);
        check("release preserves the slowdown initially", FootballRules.recoveryScale(24,0) == FootballRules.movementScale(24), true);
        check("movement returns fully at the recovery deadline", FootballRules.recoveryScale(24,FootballRules.recoveryTicks(24)) == 1, true);
        var aim = FootballRules.aimDirection(0,.25,0,10,1,0);
        check("a target below the eyes but above the ball requires upward launch", aim.y() > 0, true);
        check("moving the ball changes direction toward the same crosshair target", !aim.equals(FootballRules.aimDirection(0,.25,1,10,1,0)), true);
        check("targeting ground creates a rolling pass", FootballRules.aimDirection(0,.25,0,10,0,0).y() == 0, true);
        check("a coincident target cannot produce NaN", FootballRules.aimDirection(1,1,1,1,1,1).equals(new FootballRules.Direction(0,0,0)), true);
        check("an aerial header can aim downward", FootballRules.aimDirection(0,1.7,0,10,.3,0,false).y() < 0, true);
        var lofted = FootballRules.loft(aim,true);
        check("normal grounded shots launch higher", lofted.y() > aim.y(), true);
        check("loft does not exceed the speed cap by lengthening direction", Math.abs(Math.sqrt(lofted.x()*lofted.x()
                + lofted.y()*lofted.y() + lofted.z()*lofted.z()) - 1) < .000001, true);
        var lowAim = FootballRules.aimDirection(0,.25,0,10,0,0);
        check("loft preserves a deliberately low shot", FootballRules.loft(lowAim,true).equals(lowAim), true);
        check("loft preserves aerial aim", FootballRules.loft(aim,false).equals(aim), true);
        check("straight upward aim remains valid", FootballRules.loft(new FootballRules.Direction(0,1,0),true)
                .equals(new FootballRules.Direction(0,1,0)), true);
        check("loft fades smoothly near a low shot", FootballRules.loft(new FootballRules.Direction(1,1e-8,0),true).y() < 1e-6, true);
        check("a low grounded ball uses a normal kick", FootballRules.strike(1.2,.15,true) == FootballRules.Strike.GROUND, true);
        check("a knee-high incoming ball uses a volley", FootballRules.strike(.7,.35,false) == FootballRules.Strike.VOLLEY, true);
        check("a chest-high ball can be volleyed when releasing a charge", FootballRules.strike(.7,.65,false) == FootballRules.Strike.VOLLEY, true);
        check("a head-high ball uses a header", FootballRules.strike(.7,.95,false) == FootballRules.Strike.HEADER, true);
        check("a ball above header reach cannot be struck", FootballRules.strike(.7,1.30,false) == FootballRules.Strike.NONE, true);
        check("an airborne ball outside body reach cannot be struck", FootballRules.strike(1.2,.65,false) == FootballRules.Strike.NONE, true);
        check("a ball below the player's feet cannot be struck", FootballRules.strike(.5,-.1,false) == FootballRules.Strike.NONE, true);
        check("normal torso arrivals can be cushioned", FootballRules.chestHeight(.60), true);
        check("head arrivals cannot be cushioned with the chest", FootballRules.chestHeight(.95), false);
        check("ankle arrivals use dribbling rather than chest control", FootballRules.chestHeight(.15), false);
        check("headers use less power than volleys", FootballRules.strikePower(FootballRules.Strike.HEADER)
                < FootballRules.strikePower(FootballRules.Strike.VOLLEY), true);
        check("a missed strike has no launch power", FootballRules.strikePower(FootballRules.Strike.NONE) == 0, true);
        check("a frontal torso pass is cushioned", FootballRules.canChest(.60,.5,1,-.8,0), true);
        check("a descending frontal ball is cushioned", FootballRules.canChest(.60,.5,1,0,-.3), true);
        check("a departing ball is not pulled back", FootballRules.canChest(.60,.5,1,.8,.1), false);
        check("a pass from behind is not a chest trap", FootballRules.canChest(.60,.5,-1,-.8,0), false);
        check("a distant pass cannot be caught remotely", FootballRules.canChest(.60,1.5,1,-.8,0), false);
        check("a head-high pass cannot be trapped with the chest", FootballRules.canChest(.95,.5,1,-.8,0), false);

        var rolling = new FootballRules.Bounds(4.25,0,2.375,4.75,.5,2.875);
        check("regression: the old full-ball probe overlaps the rear floor rail", material(new FootballRules.Bounds(4.25,0,2.376,4.75,.5,2.876),0) == -1, true);
        for (int turns = 0; turns < 4; turns++) {
            int axis = turns%2 == 0 ? 2 : 0;
            double incoming = turns == 0 || turns == 3 ? .18 : -.18;
            check("rolling rear net stops the ball, orientation " + turns,
                    contact(rotate(rolling,turns),axis,incoming,turns) == 1, true);
            var post = new FootballRules.Bounds(.3125,0,.375,.8125,.5,.875);
            check("a direct post hit stays rigid, orientation " + turns,
                    contact(rotate(post,turns),axis,incoming,turns) == -1, true);
            var roof = new FootballRules.Bounds(4.25,2.875,1.125,4.75,3.375,1.625);
            check("the roof net stops a descending ball, orientation " + turns,
                    contact(rotate(roof,turns),1,-.18,turns) == 1, true);
            var slope = new FootballRules.Bounds(4.25,2,35.66666666666667/16-.5,4.75,2.5,35.66666666666667/16);
            check("a corner-first sloped-net impact is soft, orientation " + turns,
                    contact(rotate(slope,turns),axis,incoming,turns) == 1, true);
            var side = new FootballRules.Bounds(.625,0,1.5,1.125,.5,2);
            check("a rolling side-net impact is soft, orientation " + turns,
                    contact(rotate(side,turns),turns%2 == 0 ? 0 : 2,turns < 2 ? -.18 : .18,turns) == 1, true);
        }
        System.out.println(total - failures + "/" + total + " checks passed");
        System.exit(failures == 0 ? 0 : 1);
    }

    static void study() {
        // Study has the same neutrality as the full uniform, without forgiving witnessed attacks.
        check("an unprotected player remains a teacher target", SchoolStudyRules.teacherHostile(false, false, false), true);
        check("study makes an ununiformed player neutral", SchoolStudyRules.teacherHostile(false, false, true), false);
        check("the complete uniform retains its neutrality", SchoolStudyRules.teacherHostile(false, true, false), false);
        check("study and uniform can coexist", SchoolStudyRules.teacherHostile(false, true, true), false);
        for (boolean uniform : new boolean[]{false, true}) {
            for (boolean studious : new boolean[]{false, true}) {
                check("witnessed attacks override study=" + studious + " and uniform=" + uniform,
                        SchoolStudyRules.teacherHostile(true, uniform, studious), true);
            }
        }
        var clocks = new SchoolStudyRules.Cooldowns<String>(java.util.Map.of());
        check("the first player can study at a PC", clocks.tryUse("desk", 100), true);
        check("a second player cannot reuse that PC immediately", clocks.tryUse("desk", 100), false);
        check("another physical PC remains available", clocks.tryUse("other", 100), true);
        check("study cannot be refreshed after its three-minute effect expires",
                clocks.tryUse("desk", 100 + SchoolStudyRules.STUDY_TICKS), false);
        var saved = clocks.snapshot();
        var restored = new SchoolStudyRules.Cooldowns<>(saved);
        check("saving and reloading preserves the remaining cooldown",
                restored.remainingTicks("desk", 101) == SchoolStudyRules.PC_COOLDOWN_TICKS - 1, true);
        check("replacing or unloading a PC does not reset its saved position",
                restored.tryUse("desk", 101), false);
        long ready = 100L + SchoolStudyRules.PC_COOLDOWN_TICKS;
        check("the last cooldown tick still blocks study", restored.tryUse("desk", ready - 1), false);
        check("study is available on the exact deadline", restored.tryUse("desk", ready), true);
        check("using a PC prunes other expired positions", !restored.snapshot().containsKey("other"), true);
        check("saved snapshots do not change when the live clock changes", saved.get("desk") == ready, true);
        var resetClock = new SchoolStudyRules.Cooldowns<>(java.util.Map.of("desk", ready + 50_000));
        check("a world clock rollback cannot lock a PC indefinitely", resetClock.tryUse("desk", 0), true);
        var invalidClock = new SchoolStudyRules.Cooldowns<>(java.util.Map.of("desk", -1L));
        check("negative saved deadlines do not block use", invalidClock.tryUse("desk", 0), true);
        var nearLimit = new SchoolStudyRules.Cooldowns<String>(java.util.Map.of());
        check("a large world clock still accepts first use", nearLimit.tryUse("desk", Long.MAX_VALUE - 10), true);
        check("deadline overflow cannot allow immediate repeated use", nearLimit.tryUse("desk", Long.MAX_VALUE - 9), false);
    }
}
