package net.nicomar2009.lsmmod.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nicomar2009.lsmmod.item.InstrumentItem;

/** Equipment and item-use test subject; no NPC traits, school rules or combat AI. */
public class TestNpcEntity extends PathfinderMob {
    public TestNpcEntity(EntityType<? extends TestNpcEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        setCanPickUpLoot(false);
        for (EquipmentSlot slot : EquipmentSlot.values()) setDropChance(slot, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // Technical defaults for a usable test subject, not the planned NPC stat system.
        return createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) { return false; }

    /** Consumables use LivingEntity's normal use pipeline; instruments expose a mob-safe hook. */
    public boolean useHeldItem(InteractionHand hand) {
        ItemStack stack = getItemInHand(hand);
        if (stack.isEmpty()) return false;
        if (isUsingItem()) { releaseUsingItem(); return true; }
        if (stack.getItem() instanceof InstrumentItem instrument) { instrument.playFor(this); return true; }
        if (stack.getUseDuration(this) > 0) { startUsingItem(hand); return true; }
        return false; // Player-only actions and world placement require their own future adapter.
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            return useHeldItem(hand) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        // This exchange bypasses normal vanilla equipping: enforce player-only clothing here.
        if (held.getItem() instanceof net.nicomar2009.lsmmod.item.SchoolUniformItem) return InteractionResult.PASS;
        EquipmentSlot slot = held.isEmpty() ? (hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
                : getEquipmentSlotForItem(held);
        if (slot == EquipmentSlot.MAINHAND && hand == InteractionHand.OFF_HAND) slot = EquipmentSlot.OFFHAND;
        if (!canUseSlot(slot)) return InteractionResult.PASS;
        ItemStack previous = getItemBySlot(slot).copy();
        if (held.isEmpty()) {
            if (previous.isEmpty()) return InteractionResult.PASS;
            setItemSlot(slot, ItemStack.EMPTY);
            player.setItemInHand(hand, previous);
        } else {
            setItemSlot(slot, held.copyWithCount(1));
            if (!player.isCreative()) held.shrink(1);
            if (!previous.isEmpty() && !player.getInventory().add(previous)) player.drop(previous, false);
        }
        // Exchanging gear does not decide the future death-loot policy.
        setDropChance(slot, 0.0F);
        return InteractionResult.SUCCESS;
    }
}
