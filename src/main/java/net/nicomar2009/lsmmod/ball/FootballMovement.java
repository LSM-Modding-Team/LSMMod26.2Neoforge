package net.nicomar2009.lsmmod.ball;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.rules.FootballRules;

/** Temporary movement modifier; expires even when its ball unloads or is picked up. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class FootballMovement {
    private static final Identifier MODIFIER = Identifier.fromNamespaceAndPath(LSMMod.MOD_ID, "football_charge");
    private static final Map<UUID, State> ACTIVE = new HashMap<>();
    private record State(ServerPlayer player, Level level, long held, long start, long expires, boolean charging) {}
    private FootballMovement() {}

    public static void charging(ServerPlayer player, long held) {
        long now = player.level().getGameTime();
        ACTIVE.put(player.getUUID(), new State(player, player.level(), held, now, now + 2, true));
        apply(player, FootballRules.movementScale(held));
    }

    public static void released(ServerPlayer player, long held) {
        long now = player.level().getGameTime();
        ACTIVE.put(player.getUUID(), new State(player, player.level(), held, now, now + FootballRules.recoveryTicks(held), false));
        apply(player, FootballRules.movementScale(held));
    }

    public static void cancel(ServerPlayer player) { ACTIVE.remove(player.getUUID()); apply(player, 1); }

    private static void apply(ServerPlayer player, double scale) {
        var attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) return;
        attribute.removeModifier(MODIFIER);
        if (scale < 0.999999) attribute.addTransientModifier(new AttributeModifier(MODIFIER, scale - 1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @SubscribeEvent public static void tick(ServerTickEvent.Post event) {
        var iterator = ACTIVE.values().iterator();
        while (iterator.hasNext()) {
            State state = iterator.next();
            if (state.player.level().getServer() != event.getServer()) continue;
            long now = state.level.getGameTime();
            if (event.getServer().getPlayerList().getPlayer(state.player.getUUID()) != state.player
                    || state.player.level() != state.level || !state.player.isAlive() || state.player.isSpectator()
                    || now >= state.expires || now < state.start) {
                apply(state.player, 1); iterator.remove();
            } else if (!state.charging) apply(state.player, FootballRules.recoveryScale(state.held, now - state.start));
        }
    }

    @SubscribeEvent public static void stopped(ServerStoppedEvent event) {
        ACTIVE.values().removeIf(state -> {
            if (state.player.level().getServer() != event.getServer()) return false;
            apply(state.player, 1); return true;
        });
    }
}
