package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.block.SchoolCircularColumnBlock;
import net.nicomar2009.lsmmod.block.SchoolPaint;

public record ColumnPartPredicate(int side,int quarter,int paint) implements ModelPredicate {
    public static final Serializer<ColumnPartPredicate> SERIALIZER=new Serializer<>() {
        @Override public ColumnPartPredicate deserialize(JsonObject json) {
            int side=json.get("side").getAsInt(),quarter=json.get("quarter").getAsInt(),paint=json.get("paint").getAsInt();
            if(side< -1 || side>3 || quarter<0 || quarter>3 || !SchoolPaint.validId(paint)) throw new JsonParseException("Invalid column part");
            return new ColumnPartPredicate(side,quarter,paint);
        }
        @Override public JsonObject serialize(ColumnPartPredicate value) {
            JsonObject json=new JsonObject();json.addProperty("side",value.side());json.addProperty("quarter",value.quarter());json.addProperty("paint",value.paint());return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {
        if(state==null || !(state.getBlock() instanceof SchoolCircularColumnBlock)) return false;
        return side<0 ? SchoolCircularColumnBlock.hasBase(level,pos) : SchoolCircularColumnBlock.wallPaint(level,pos,side,quarter)==paint;
    }
    @Override public boolean testForItem(ItemStack stack) { return side<0; }
    @Override public Serializer<? extends ModelPredicate> getSerializer() { return SERIALIZER; }
}
