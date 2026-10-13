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
import net.nicomar2009.lsmmod.block.SchoolPaint;
import net.nicomar2009.lsmmod.block.SchoolPaintableBlockEntity;

public record PaintedFacePredicate(Direction face, int paint, boolean local,int quadrant) implements ModelPredicate {
    public PaintedFacePredicate(Direction face,int paint) {this(face,paint,false,-1);}
    public PaintedFacePredicate(Direction face,int paint,boolean local) {this(face,paint,local,-1);}
    public static final Serializer<PaintedFacePredicate> SERIALIZER = new Serializer<>() {
        @Override public PaintedFacePredicate deserialize(JsonObject json) {
            int paint = json.get("paint").getAsInt();
            if (!SchoolPaint.validId(paint)) throw new JsonParseException("Unknown school paint");
            int quadrant=json.has("quadrant")?json.get("quadrant").getAsInt():-1;
            if(quadrant<-1||quadrant>3)throw new JsonParseException("Unknown paint quadrant");
            return new PaintedFacePredicate(Direction.valueOf(json.get("face").getAsString().toUpperCase(Locale.ROOT)), paint,
                    json.has("local")&&json.get("local").getAsBoolean(),quadrant);
        }
        @Override public JsonObject serialize(PaintedFacePredicate value) {
            JsonObject json = new JsonObject(); json.addProperty("face", value.face.getSerializedName()); json.addProperty("paint", value.paint);
            if(value.local)json.addProperty("local",true);
            if(value.quadrant>=0)json.addProperty("quadrant",value.quadrant);return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        Direction target=face;
        int x=face.getAxis()==Direction.Axis.X?0:quadrant&1;
        int y=face.getAxis()==Direction.Axis.Y?0:(quadrant>>1)&1;
        int z=face.getAxis()==Direction.Axis.X?quadrant&1:face.getAxis()==Direction.Axis.Y?(quadrant>>1)&1:0;
        if(local&&state!=null&&state.hasProperty(net.nicomar2009.lsmmod.block.SchoolProjectingWindowBlock.FACING)) {
            int turns=switch(state.getValue(net.nicomar2009.lsmmod.block.SchoolProjectingWindowBlock.FACING)) {case EAST->1;case SOUTH->2;case WEST->3;default->0;};
            for(int i=0;i<turns;i++) {
                if(target.getAxis().isHorizontal())target=target.getClockWise();
                int old=x;x=1-z;z=old;
            }
        }
        int selected = pos != null && level != null && level.getBlockEntity(pos) instanceof SchoolPaintableBlockEntity block
                ? block.paint(target).ordinal() : 0;
        if(quadrant>=0&&pos!=null&&level!=null&&level.getBlockEntity(pos) instanceof net.nicomar2009.lsmmod.block.SchoolPaintableCustomBlockEntity block)
            selected=block.squarePaint(target,net.nicomar2009.lsmmod.block.SchoolPaintableCustomBlockEntity.quadrant(target,x,y,z)).ordinal();
        return selected == paint;
    }
    @Override public boolean testForItem(ItemStack stack) { return paint == 0; }
    @Override public Serializer<? extends ModelPredicate> getSerializer() { return SERIALIZER; }
}
