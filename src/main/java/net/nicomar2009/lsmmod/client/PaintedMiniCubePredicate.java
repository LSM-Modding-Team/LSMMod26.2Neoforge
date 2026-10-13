package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonObject;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Share face selection logic, but use a unique serializer for Fusion's bidirectional registry. */
public record PaintedMiniCubePredicate(PaintedDoorFramePredicate delegate) implements ModelPredicate {
    public static final Serializer<PaintedMiniCubePredicate> SERIALIZER=new Serializer<>() {
        @Override public PaintedMiniCubePredicate deserialize(JsonObject json) {
            return new PaintedMiniCubePredicate(PaintedDoorFramePredicate.SERIALIZER.deserialize(json));
        }
        @Override public JsonObject serialize(PaintedMiniCubePredicate value) {
            return PaintedDoorFramePredicate.SERIALIZER.serialize(value.delegate);
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter view,BlockPos pos,BlockState state) {
        return delegate.testForBlockState(view,pos,state);
    }
    @Override public boolean testForItem(ItemStack stack) {return delegate.testForItem(stack);}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
