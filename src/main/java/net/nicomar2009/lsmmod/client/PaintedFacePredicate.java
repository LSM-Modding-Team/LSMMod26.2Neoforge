package net.nicomar2009.lsmmod.client;

import java.util.Locale;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity;

public record PaintedFacePredicate(Direction face, int paint) implements ModelPredicate {
    public static final Serializer<PaintedFacePredicate> SERIALIZER = new Serializer<>() {
        @Override public PaintedFacePredicate deserialize(JsonObject json) {
            int paint = json.get("paint").getAsInt();
            if (paint < 0 || paint > 4) throw new JsonParseException("Unknown school paint");
            return new PaintedFacePredicate(Direction.valueOf(json.get("face").getAsString().toUpperCase(Locale.ROOT)), paint);
        }
        @Override public JsonObject serialize(PaintedFacePredicate value) {
            JsonObject json = new JsonObject(); json.addProperty("face", value.face.getSerializedName()); json.addProperty("paint", value.paint); return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        int selected = pos != null && level != null && level.getBlockEntity(pos) instanceof SchoolPaintableBlockEntity block
                ? block.paint(face).ordinal() : 0;
        return selected == paint;
    }
    @Override public boolean testForItem(ItemStack stack) { return paint == 0; }
    @Override public Serializer<? extends ModelPredicate> getSerializer() { return SERIALIZER; }
}
