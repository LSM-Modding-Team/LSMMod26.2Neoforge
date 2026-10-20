package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.client;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolMiniCubePaintableBlock;

import java.util.Locale;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolDoorFramePaintEntity;
import net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block.SchoolPaint;

/** Select one existing material on each exposed minicube face; retain the model predicate ID. */
public record PaintedDoorFramePredicate(int cube,Direction face,int paint) implements ModelPredicate {
    public static final Serializer<PaintedDoorFramePredicate> SERIALIZER=new Serializer<>() {
        @Override public PaintedDoorFramePredicate deserialize(JsonObject json) {
            int cube=json.get("cube").getAsInt(),paint=json.get("paint").getAsInt();
            if(cube<0||cube>=64||!SchoolPaint.validId(paint))throw new JsonParseException("Invalid painted minicube face");
            return new PaintedDoorFramePredicate(cube,Direction.valueOf(json.get("face").getAsString().toUpperCase(Locale.ROOT)),paint);
        }
        @Override public JsonObject serialize(PaintedDoorFramePredicate value) {
            JsonObject json=new JsonObject();json.addProperty("cube",value.cube);json.addProperty("face",value.face.getSerializedName());json.addProperty("paint",value.paint);return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter view,BlockPos pos,BlockState state) {
        if(state==null||!(state.getBlock() instanceof SchoolMiniCubePaintableBlock))return false;
        int x=cube&3,y=(cube>>2)&3,z=(cube>>4)&3;
        if(view!=null&&pos!=null&&view.getBlockEntity(pos) instanceof SchoolDoorFramePaintEntity entity)
            return entity.exposed(cube,face,view)&&entity.squarePaint(face,SchoolDoorFramePaintEntity.quadrant(face,x,y,z)).ordinal()==paint;
        long occupied=-1L;
        if(paint!=0||(occupied&(1L<<cube))==0)return false;
        int nx=x+face.getStepX(),ny=y+face.getStepY(),nz=z+face.getStepZ();
        return nx<0||nx>=4||ny<0||ny>=4||nz<0||nz>=4||(occupied&(1L<<(nx+4*ny+16*nz)))==0;
    }
    @Override public boolean testForItem(ItemStack stack) {return false;}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
