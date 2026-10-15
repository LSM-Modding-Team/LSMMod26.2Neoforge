package net.nicomar2009.lsmmod.rules;

/** Shared connection policy; restricted outputs accept only Moises' laptop. */
public final class SchoolMediaRules {
    public enum Role { NONE, PC, LAPTOP, MOISES_LAPTOP, PROJECTOR, SCREEN, SPEAKER, GENERAL_SPEAKER, AUDIO_CONTROLLER }
    private SchoolMediaRules() { }
    public static boolean source(Role role) {
        return role == Role.PC || role == Role.LAPTOP || role == Role.MOISES_LAPTOP;
    }
    public static boolean canConnect(Role input, Role output) {
        return switch (output) {
            case PROJECTOR, SPEAKER -> source(input);
            case SCREEN -> input == Role.PROJECTOR;
            case GENERAL_SPEAKER, AUDIO_CONTROLLER -> input == Role.MOISES_LAPTOP;
            default -> false;
        };
    }
    public static boolean validAudioSettings(int volume, int speed) {
        return volume >= 0 && volume <= 200 && speed >= 25 && speed <= 200;
    }
    public static int speakerRadius(Role role) { return role == Role.GENERAL_SPEAKER ? 64 : 32; }
    public static int pcmSample(float value, float gain) {
        if (!Float.isFinite(value) || !Float.isFinite(gain)) return 0;
        return Math.round(Math.clamp(value * gain, -1, 1) * 32767);
    }

}
