package net.nicomar2009.lsmmod.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.entity.SchoolNpcEntity;

/** Teachers witness school attacks; student witnesses retaliate only for their own classroom. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class SchoolNpcCombatEvents {
    private SchoolNpcCombatEvents() { }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void meleeAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof SchoolNpcEntity victim) observe(event.getEntity(), victim);
    }

    @SubscribeEvent
    public static void damage(LivingDamageEvent.Post event) {
        // Includes projectiles: DamageSource#getEntity is the responsible shooter.
        if (event.getOriginalDamage() > 0 && event.getSource().getEntity() instanceof Player player
                && event.getEntity() instanceof SchoolNpcEntity victim) observe(player, victim);
    }

    private static void observe(Player player, SchoolNpcEntity victim) {
        if (!(victim.level() instanceof ServerLevel level) || (!victim.isTeacher() && !victim.isStudent())) return;
        for (SchoolNpcEntity witness : level.getEntitiesOfClass(SchoolNpcEntity.class,
                player.getBoundingBox().inflate(128.0), npc -> npc.isTeacher() || npc.isStudent())) {
            witness.witnessSchoolAttack(player, victim);
        }
    }
}
