package net.nicomar2009.lsmmod.modules.technology.block;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.GrayComputerDeskBlock;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ComputerDeskBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.nicomar2009.lsmmod.modules.technology.media.SchoolMediaDevices;
import net.nicomar2009.lsmmod.registry.ModEffects;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolMediaRules;
import net.nicomar2009.lsmmod.modules.technology.rules.SchoolStudyRules;

public final class SchoolStudyInteraction {
    private SchoolStudyInteraction() { }

    public static void use(Player actor, BlockPos clicked) {
        if (!(actor instanceof ServerPlayer player) || !player.isAlive() || player.isSpectator()
                || player.isShiftKeyDown() || !player.getMainHandItem().isEmpty()
                || !player.getOffhandItem().isEmpty()) return;
        ServerLevel level = (ServerLevel) player.level();
        if (!level.hasChunkAt(clicked) || !player.isWithinBlockInteractionRange(clicked, 0)
                || !level.mayInteract(player, clicked)) return;
        var picked = player.pick(player.blockInteractionRange(), 1, false);
        if (!(picked instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK
                || !hit.getBlockPos().equals(clicked)
                || !player.mayUseItemAt(clicked, hit.getDirection(), player.getMainHandItem())) return;
        BlockState clickedState = level.getBlockState(clicked);
        if (SchoolMediaDevices.role(clickedState) != SchoolMediaRules.Role.PC) return;
        BlockPos pc = SchoolMediaDevices.anchor(level, clicked);
        if (!completeAndAccessible(level, pc, clickedState, player, hit)) return;
        SchoolStudyCooldowns cooldowns = SchoolStudyCooldowns.get(level);
        long now = level.getGameTime();
        if (!cooldowns.tryUse(pc, now)) {
            int seconds = (cooldowns.remainingTicks(pc, now) + 19) / 20;
            player.sendOverlayMessage(Component.translatable("message.lsmmod.pc.study_cooldown", seconds));
            return;
        }
        player.addEffect(new MobEffectInstance(ModEffects.STUDIOUS, SchoolStudyRules.STUDY_TICKS, 0));
        player.sendOverlayMessage(Component.translatable("message.lsmmod.pc.studied"));
    }

    private static boolean completeAndAccessible(ServerLevel level, BlockPos root, BlockState clickedState,
                                                 ServerPlayer player, BlockHitResult hit) {
        if (!level.hasChunkAt(root)) return false;
        BlockState state = level.getBlockState(root);
        if (!state.is(clickedState.getBlock()) || SchoolMediaDevices.role(state) != SchoolMediaRules.Role.PC) return false;
        boolean desk = state.getBlock() instanceof ComputerDeskBlock;
        boolean wide = state.getBlock() instanceof GrayComputerDeskBlock;
        if (desk && state.getValue(ComputerDeskBlock.FACING) != clickedState.getValue(ComputerDeskBlock.FACING)) return false;
        for (int column = 0; column < (wide ? 2 : 1); column++) {
            for (int half = 0; half < (desk ? 2 : 1); half++) {
                BlockPos cell = root.above(half);
                if (wide) cell = cell.relative(state.getValue(ComputerDeskBlock.FACING).getCounterClockWise(), column);
                if (!level.hasChunkAt(cell) || !level.mayInteract(player, cell)
                        || !player.mayUseItemAt(cell, hit.getDirection(), player.getMainHandItem())) return false;
                BlockState part = level.getBlockState(cell);
                if (!part.is(state.getBlock())) return false;
                if (desk && (!part.getValue(ComputerDeskBlock.HAS_PC)
                        || part.getValue(ComputerDeskBlock.FACING) != state.getValue(ComputerDeskBlock.FACING)
                        || part.getValue(ComputerDeskBlock.HALF) != (half == 0 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER))) return false;
                if (wide && part.getValue(GrayComputerDeskBlock.COLUMN) != column) return false;
            }
        }
        return true;
    }
}
