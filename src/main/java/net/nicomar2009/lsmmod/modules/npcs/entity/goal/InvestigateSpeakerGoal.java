package net.nicomar2009.lsmmod.modules.npcs.entity.goal;



import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.nicomar2009.lsmmod.modules.npcs.entity.SchoolNpcEntity;

/** Walks towards live audio using navigation, without replacing combat memories. */
public final class InvestigateSpeakerGoal extends Goal {
    private final SchoolNpcEntity teacher;
    private Vec3 destination;
    private Vec3 lastPathDestination;
    private int repath;

    public InvestigateSpeakerGoal(SchoolNpcEntity teacher) {
        this.teacher = teacher;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        var source = teacher.speakerToInvestigate();
        if (source == null) return false;
        destination = source.position();
        return true;
    }

    @Override
    public boolean canContinueToUse() { return canUse(); }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void start() {
        teacher.setTarget(null);
        teacher.setAggressive(false);
        teacher.getNavigation().stop();
        repath = 0;
        lastPathDestination = null;
    }

    @Override
    public void tick() {
        // Target selection must not reintroduce melee while investigating.
        teacher.setTarget(null);
        teacher.getLookControl().setLookAt(destination.x(), destination.y(), destination.z());
        double dx = destination.x() - teacher.getX(), dz = destination.z() - teacher.getZ();
        if (dx * dx + dz * dz <= 1.5 * 1.5 && Math.abs(destination.y() - teacher.getY()) <= 2) {
            teacher.getNavigation().stop();
            return;
        }
        if (--repath <= 0 || lastPathDestination == null || lastPathDestination.distanceToSqr(destination) > 1) {
            teacher.getNavigation().moveTo(destination.x(), destination.y(), destination.z(), 1.0);
            lastPathDestination = destination;
            repath = 10;
        }
    }

    @Override
    public void stop() {
        teacher.getNavigation().stop();
        destination = null;
        lastPathDestination = null;
    }
}
