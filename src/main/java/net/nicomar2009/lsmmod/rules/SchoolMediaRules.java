package net.nicomar2009.lsmmod.rules;

/** Shared connection policy; general speakers accept only Moises' laptop. */
public final class SchoolMediaRules {
    public enum Role { NONE, PC, LAPTOP, MOISES_LAPTOP, PROJECTOR, SCREEN, SPEAKER, GENERAL_SPEAKER }
    private SchoolMediaRules() { }
    public static boolean source(Role role) {
        return role == Role.PC || role == Role.LAPTOP || role == Role.MOISES_LAPTOP;
    }
    public static boolean canConnect(Role input, Role output) {
        return switch (output) {
            case PROJECTOR, SPEAKER -> source(input);
            case SCREEN -> input == Role.PROJECTOR;
            case GENERAL_SPEAKER -> input == Role.MOISES_LAPTOP;
            default -> false;
        };
    }
}
