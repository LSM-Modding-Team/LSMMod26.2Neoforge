package net.nicomar2009.lsmmod.ball;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class BallItem extends Item {
    private final BallKind kind;
    public BallItem(Properties properties, BallKind kind) { super(properties); this.kind = kind; }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel level) {
            BallEntity ball = new BallEntity(kind.entityType(), level);
            Vec3 point = context.getClickLocation().add(Vec3.atLowerCornerOf(context.getClickedFace().getUnitVec3i()).scale(0.28));
            ball.setPos(point.x, point.y - 0.25, point.z);
            if (!level.noCollision(ball, ball.getBoundingBox()) || !level.addFreshEntity(ball)) return InteractionResult.FAIL;
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel server) {
            BallEntity ball = new BallEntity(kind.entityType(), server);
            Vec3 direction = player.getLookAngle();
            Vec3 position = player.getEyePosition().add(direction.scale(0.6));
            ball.setPos(position.x, position.y - 0.25, position.z);
            ball.launch(player, direction);
            if (!server.addFreshEntity(ball)) return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
