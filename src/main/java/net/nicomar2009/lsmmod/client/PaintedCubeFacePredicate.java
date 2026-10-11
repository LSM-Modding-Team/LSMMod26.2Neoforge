package net.nicomar2009.lsmmod.client;

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
import net.nicomar2009.lsmmod.block.SchoolPaint;
import net.nicomar2009.lsmmod.block.SchoolPaintableCustomBlockEntity;

/** Hide removed cubes and internal faces before selecting the appropriate shared paint. */
public record PaintedCubeFacePredicate(int cube,Direction face,int paint) implements ModelPredicate {
    public static final Serializer<PaintedCubeFacePredicate> SERIALIZER=new Serializer<>() {
        @Override public PaintedCubeFacePredicate deserialize(JsonObject json) {
            int cube=json.get("cube").getAsInt(),paint=json.get("paint").getAsInt();
            if(cube<0||cube>7||!SchoolPaint.validId(paint)) throw new JsonParseException("Invalid painted cube face");
            return new PaintedCubeFacePredicate(cube,Direction.valueOf(json.get("face").getAsString().toUpperCase(Locale.ROOT)),paint);
        }
        @Override public JsonObject serialize(PaintedCubeFacePredicate predicate) {
            JsonObject json=new JsonObject();json.addProperty("cube",predicate.cube);json.addProperty("face",predicate.face.getSerializedName());json.addProperty("paint",predicate.paint);return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {
        int x=cube&1,y=(cube>>1)&1,z=(cube>>2)&1;
        int nx=x+face.getStepX(),ny=y+face.getStepY(),nz=z+face.getStepZ();
        boolean internal=nx>=0&&nx<2&&ny>=0&&ny<2&&nz>=0&&nz<2;
        if(level!=null&&pos!=null&&level.getBlockEntity(pos) instanceof SchoolPaintableCustomBlockEntity block) {
            if(!block.hasCube(cube)||(internal&&block.hasCube(nx+2*ny+4*nz))) return false;
            return block.squarePaint(face,SchoolPaintableCustomBlockEntity.quadrant(face,x,y,z)).ordinal()==paint;
        }
        return !internal&&paint==0;
    }
    @Override public boolean testForItem(ItemStack stack) { return false; } // The item uses a native gray cube.
    @Override public Serializer<? extends ModelPredicate> getSerializer() { return SERIALIZER; }
}
