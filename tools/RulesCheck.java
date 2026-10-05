import net.nicomar2009.lsmmod.rules.Hostility;
import net.nicomar2009.lsmmod.rules.NpcGroup;
import net.nicomar2009.lsmmod.rules.NpcState;
import net.nicomar2009.lsmmod.rules.PlayerGear;

/**
 * Small self-check for everything under rules/ (pure logic; it never says the mod compiles).
 * Command: see START_HERE.md section 5. Exit code 0 = all passed, 1 = something failed.
 */
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

    static PlayerGear gear(boolean suit, int stu, int tea, boolean studious, boolean sticker) {
        return new PlayerGear(suit, stu, tea, studious, sticker);
    }

    public static void main(String[] args) {
        NpcGroup S = NpcGroup.STUDENTS, T = NpcGroup.TEACHERS, N = NpcGroup.NO_GROUP;
        NpcState NEU = NpcState.NEUTRAL, ANG = NpcState.ANGRY, PAC = NpcState.PACIFIED;
        PlayerGear none = PlayerGear.NONE;

        // Baseline: the state decides when nothing else applies.
        check("angry attacks", Hostility.attacksPlayer(S, ANG, false, none), true);
        check("neutral does not attack", Hostility.attacksPlayer(S, NEU, false, none), false);
        check("neutral but provoked attacks", Hostility.attacksPlayer(S, NEU, true, none), true);

        // Decided: the suit wins over everything; pacified suspends hostility.
        check("suit beats angry+provoked+sticker", Hostility.attacksPlayer(T, ANG, true, gear(true, 0, 0, false, true)), false);
        check("pacified beats provoked", Hostility.attacksPlayer(S, PAC, true, none), false);
        check("pacified beats sticker (pick)", Hostility.attacksPlayer(T, PAC, false, gear(false, 0, 0, false, true)), false);

        // Garment immunity is per group.
        check("student piece calms angry student", Hostility.attacksPlayer(S, ANG, false, gear(false, 1, 0, false, false)), false);
        check("student piece does not calm teacher", Hostility.attacksPlayer(T, ANG, false, gear(false, 1, 0, false, false)), true);
        check("teacher piece calms angry teacher", Hostility.attacksPlayer(T, ANG, false, gear(false, 0, 1, false, false)), false);
        check("no group ignores garments", Hostility.attacksPlayer(N, ANG, false, gear(false, 2, 2, true, false)), true);

        // Studious only calms teachers.
        check("studious calms teacher", Hostility.attacksPlayer(T, ANG, false, gear(false, 0, 0, true, false)), false);
        check("studious does not calm student", Hostility.attacksPlayer(S, ANG, false, gear(false, 0, 0, true, false)), true);

        // Pick (precedence): sticker beats teacher immunity and Studious; provoked beats immunity.
        check("sticker beats teacher piece", Hostility.attacksPlayer(T, NEU, false, gear(false, 0, 2, false, true)), true);
        check("sticker beats studious", Hostility.attacksPlayer(T, NEU, false, gear(false, 0, 0, true, true)), true);
        check("sticker does not affect students", Hostility.attacksPlayer(S, NEU, false, gear(false, 0, 0, false, true)), false);
        check("provoked beats immunity", Hostility.attacksPlayer(S, NEU, true, gear(false, 3, 0, false, false)), true);

        // Vision: 1.0 / 0.5 / undefined (V6).
        total++;
        if (Hostility.visionMultiplier(none).getAsDouble() != 1.0) { failures++; System.out.println("FAIL: vision 0 pieces"); }
        total++;
        if (Hostility.visionMultiplier(gear(false, 0, 1, false, false)).getAsDouble() != 0.5) { failures++; System.out.println("FAIL: vision 1 piece"); }
        total++;
        if (Hostility.visionMultiplier(gear(false, 1, 1, false, false)).isPresent()) { failures++; System.out.println("FAIL: vision 2 pieces must be undefined"); }

        System.out.println(total - failures + "/" + total + " checks passed");
        System.exit(failures == 0 ? 0 : 1);
    }
}
