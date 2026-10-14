package net.nicomar2009.lsmmod.ball;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.network.FootballInputPayload;
import net.nicomar2009.lsmmod.rules.FootballRules;

/** Preparation belongs to the player, so a ball can arrive after charging begins. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class FootballActions {
    private static final Map<UUID, Charge> ACTIVE = new HashMap<>();
    private static final class Charge {
        final ServerPlayer player;
        final Level level;
        final long start;
        long lastInput, sampled, releaseTime;
        int keys, followSample;
        boolean released;
        BallEntity struck;
        FootballRules.Spin spin = new FootballRules.Spin(0, 0);
        Charge(ServerPlayer player, int keys) {
            this.player = player; level = player.level(); start = level.getGameTime();
            sampled = lastInput = start; this.keys = keys;
        }
        void accumulate(long now) {
            for (long tick = sampled + 1; tick <= now; tick++) spin = FootballRules.buildSpin(spin, keys, tick - start);
            sampled = now;
        }
    }
    private FootballActions() {}
    private static boolean ready(Player player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger() && !player.getAbilities().flying;
    }
    public static boolean isCharging(Player player) {
        Charge charge = ACTIVE.get(player.getUUID());
        return charge != null && charge.player == player && !charge.released;
    }

    public static void input(ServerPlayer player, int action, int keys, float yaw, float pitch) {
        long now = player.level().getGameTime();
        Charge charge = ACTIVE.get(player.getUUID());
        if (action == FootballInputPayload.CANCEL) { cancel(player, true); return; }
        if (!ready(player)) { cancel(player, true); return; }
        if (action == FootballInputPayload.START) {
            if (charge != null && !charge.released) return;
            charge = new Charge(player, keys); ACTIVE.put(player.getUUID(), charge);
            FootballMovement.charging(player, 0); show(charge, now); return;
        }
        if (charge == null || charge.player != player || charge.level != player.level()) return;
        if (charge.released) {
            int sample = action - FootballInputPayload.FOLLOW + 1;
            if (action >= FootballInputPayload.FOLLOW && sample == charge.followSample + 1
                    && now - charge.releaseTime <= FootballRules.FOLLOW_TICKS && now >= charge.releaseTime) {
                charge.followSample = sample;
                if (charge.struck != null && !charge.struck.isRemoved()) charge.struck.followThrough(player, sample, keys);
            }
            return;
        }
        if (now - charge.lastInput > 10 || now < charge.start) { cancel(player, true); return; }
        charge.accumulate(now);
        if (action == FootballInputPayload.HOLD) { charge.keys = keys; charge.lastInput = now; return; }
        if (action != FootballInputPayload.RELEASE) return;
        charge.released = true; charge.releaseTime = now;
        // This is an attempt now, never a queued kick that fires when a ball later arrives.
        charge.struck = player.level().getEntitiesOfClass(BallEntity.class, player.getBoundingBox().inflate(1.8, 0.8, 1.8),
                ball -> ball.strikeKind(player) != FootballRules.Strike.NONE)
                .stream().min(Comparator.comparingDouble(ball -> ball.distanceToSqr(player))).orElse(null);
        if (charge.struck == null) {
            FootballMovement.cancel(player); player.sendOverlayMessage(Component.empty());
        } else {
            long held = now - charge.start;
            charge.struck.strike(player, held, FootballRules.releaseSpin(charge.spin, keys), yaw, pitch);
            FootballMovement.released(player, held);
        }
    }

    private static void show(Charge charge, long now) {
        int percent = (int) Math.round(FootballRules.charge(now - charge.start) * 100), filled = percent / 10;
        var preview = FootballRules.releaseSpin(charge.spin, charge.keys);
        charge.player.sendOverlayMessage(Component.translatable("message.lsmmod.football_charge",
                "▰".repeat(filled) + "▱".repeat(10 - filled), percent,
                Component.translatable("message.lsmmod.football_side_" + (preview.side() < 0 ? "left" : preview.side() > 0 ? "right" : "none")),
                Component.translatable("message.lsmmod.football_top_" + (preview.top() < 0 ? "back" : preview.top() > 0 ? "front" : "none"))));
    }
    private static void cancel(ServerPlayer player, boolean notify) {
        Charge removed = ACTIVE.remove(player.getUUID());
        if (removed != null && !removed.released) FootballMovement.cancel(player);
        if (notify && removed != null && !removed.released && player.isAlive())
            player.sendOverlayMessage(Component.translatable("message.lsmmod.football_cancelled"));
    }

    @SubscribeEvent public static void tick(ServerTickEvent.Post event) {
        var iterator = ACTIVE.values().iterator();
        while (iterator.hasNext()) {
            Charge charge = iterator.next();
            if (charge.level.getServer() != event.getServer()) continue;
            long now = charge.level.getGameTime();
            boolean invalid = event.getServer().getPlayerList().getPlayer(charge.player.getUUID()) != charge.player
                    || charge.player.level() != charge.level || !ready(charge.player) || now < charge.start;
            boolean expired = charge.released ? now - charge.releaseTime > FootballRules.FOLLOW_TICKS : now - charge.lastInput > 10;
            if (invalid || expired) {
                if (!charge.released || invalid) FootballMovement.cancel(charge.player);
                if (!charge.released && !invalid) charge.player.sendOverlayMessage(Component.translatable("message.lsmmod.football_cancelled"));
                iterator.remove();
            } else if (!charge.released) {
                charge.accumulate(now); FootballMovement.charging(charge.player, now - charge.start);
                if (now % 4 == 0) show(charge, now);
            }
        }
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) {
        ACTIVE.values().removeIf(charge -> charge.level.getServer() == event.getServer());
    }
}
