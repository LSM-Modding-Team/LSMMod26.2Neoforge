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

public record ColumnPartPredicate(int side,int quarter,int paint,int height,int base) implements ModelPredicate {
    public static final Serializer<ColumnPartPredicate> SERIALIZER=new Serializer<>() {
        @Override public ColumnPartPredicate deserialize(JsonObject json) {
            int side=json.get("side").getAsInt(),quarter=json.get("quarter").getAsInt(),paint=json.get("paint").getAsInt();
            int height=json.has("height")?json.get("height").getAsInt():-1,base=json.has("base")?json.get("base").getAsInt():-1;
            if((height!=-1 && height!=0 && height!=12 && height!=16) || base< -1 || base>1 || side< -1 || side>3 || quarter<0 || quarter>3 || (paint!=-1 && !SchoolPaint.validId(paint))) throw new JsonParseException("Invalid column part");
            return new ColumnPartPredicate(side,quarter,paint,height,base);
        }
        @Override public JsonObject serialize(ColumnPartPredicate value) {
            JsonObject json=new JsonObject();json.addProperty("side",value.side());json.addProperty("quarter",value.quarter());json.addProperty("paint",value.paint());if(value.height()!=-1)json.addProperty("height",value.height());if(value.base()!=-1)json.addProperty("base",value.base());return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {
        if(state==null || !(state.getBlock() instanceof SchoolCircularColumnBlock)) return false;
        if(base>=0 && SchoolCircularColumnBlock.hasBase(level,pos)!=(base==1)) return false;
        if(side<0) return SchoolCircularColumnBlock.hasBase(level,pos);
        if(height>=0 && SchoolCircularColumnBlock.wallHeight(level,pos,side,quarter)!=height) return false;
        return paint<0 || SchoolCircularColumnBlock.wallPaint(level,pos,side,quarter)==paint;
    }
    @Override public boolean testForItem(ItemStack stack) { return side<0; }
    @Override public Serializer<? extends ModelPredicate> getSerializer() { return SERIALIZER; }
}
