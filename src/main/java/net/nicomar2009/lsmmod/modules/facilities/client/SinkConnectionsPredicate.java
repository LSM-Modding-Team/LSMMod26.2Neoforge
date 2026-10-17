package net.nicomar2009.lsmmod.modules.facilities.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.modules.facilities.block.SchoolConnectedSinkBlock;

public record SinkConnectionsPredicate(int mask) implements ModelPredicate {
    public static final Serializer<SinkConnectionsPredicate> SERIALIZER=new Serializer<>() {
        @Override public SinkConnectionsPredicate deserialize(JsonObject json) {
            int mask=json.get("mask").getAsInt();
            if(mask<0 || mask>3) throw new JsonParseException("Sink mask must be from 0 to 3");
            return new SinkConnectionsPredicate(mask);
        }
        @Override public JsonObject serialize(SinkConnectionsPredicate value) {
            JsonObject json=new JsonObject();json.addProperty("mask",value.mask());return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {
        return state!=null && SchoolConnectedSinkBlock.connections(level,pos,state)==mask;
    }
    @Override public boolean testForItem(ItemStack stack) { return mask==0; }
    @Override public Serializer<? extends ModelPredicate> getSerializer() { return SERIALIZER; }
}
