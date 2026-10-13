package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.block.SchoolProjectingWindowBlock;

/** Select from the supplied render view, including minimap snapshots; no global ClientLevel lookup. */
public record ProjectingWindowPredicate(int phase,int part,int pose) implements ModelPredicate {
    public static final Serializer<ProjectingWindowPredicate> SERIALIZER=new Serializer<>() {
        @Override public ProjectingWindowPredicate deserialize(JsonObject json) {
            int phase=json.get("phase").getAsInt(),part=json.has("part")?json.get("part").getAsInt():-1;
            int pose=json.has("pose")?json.get("pose").getAsInt():-1;
            if(phase<0||phase>8||part < -1||part>=SchoolProjectingWindowBlock.parts(phase).size()
                    ||pose < -1||pose>4||(part==-1)!=(pose==-1))throw new JsonParseException("Invalid projecting window selection");
            return new ProjectingWindowPredicate(phase,part,pose);
        }
        @Override public JsonObject serialize(ProjectingWindowPredicate value) {
            JsonObject json=new JsonObject();json.addProperty("phase",value.phase);
            if(value.part>=0){json.addProperty("part",value.part);json.addProperty("pose",value.pose);}return json;
        }
    };
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {
        if(pos==null||state==null||!(state.getBlock() instanceof SchoolProjectingWindowBlock))return false;
        if(SchoolProjectingWindowBlock.phase(pos,state.getValue(SchoolProjectingWindowBlock.FACING))!=phase)return false;
        return part<0||SchoolProjectingWindowBlock.pose(level,pos,state,part)==pose;
    }
    @Override public boolean testForItem(ItemStack stack) {return phase==0&&(part<0||pose==0);}
    @Override public Serializer<? extends ModelPredicate> getSerializer(){return SERIALIZER;}
}
