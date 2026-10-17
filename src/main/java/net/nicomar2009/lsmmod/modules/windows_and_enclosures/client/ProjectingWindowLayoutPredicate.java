package net.nicomar2009.lsmmod.modules.windows_and_enclosures.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import java.lang.ref.WeakReference;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowBlock;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.block.SchoolProjectingWindowLayout;

/** The first fallback predicate refreshes each model evaluation; descendants share that snapshot scan. */
public record ProjectingWindowLayoutPredicate(String key,int part,int pose,boolean refresh) implements ModelPredicate {
    public static final Serializer<ProjectingWindowLayoutPredicate> SERIALIZER=new Serializer<>() {
        @Override public ProjectingWindowLayoutPredicate deserialize(JsonObject json) {
            String key=json.get("key").getAsString();int part=json.has("part")?json.get("part").getAsInt():-1;
            int pose=json.has("pose")?json.get("pose").getAsInt():-1;
            if((!key.equals("fallback")&&!SchoolProjectingWindowLayout.validKey(key))||part < -1||part>3
                    ||pose < -1||pose>1||(part==-1)!=(pose==-1))throw new JsonParseException("Invalid window layout predicate");
            return new ProjectingWindowLayoutPredicate(key,part,pose,json.has("refresh")&&json.get("refresh").getAsBoolean());
        }
        @Override public JsonObject serialize(ProjectingWindowLayoutPredicate value) {
            JsonObject json=new JsonObject();json.addProperty("key",value.key);
            if(value.part>=0){json.addProperty("part",value.part);json.addProperty("pose",value.pose);}
            if(value.refresh)json.addProperty("refresh",true);return json;
        }
    };
    private static final class Selection {
        WeakReference<BlockAndTintGetter> level=new WeakReference<>(null);
        BlockPos pos;BlockState state;SchoolProjectingWindowLayout.View view;
    }
    private static final ThreadLocal<Selection> SELECTION=ThreadLocal.withInitial(Selection::new);
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {
        if(level==null||pos==null||state==null||!(state.getBlock() instanceof SchoolProjectingWindowBlock))return false;
        Selection selection=SELECTION.get();
        if(refresh||selection.level.get()!=level||!pos.equals(selection.pos)||selection.state!=state) {
            selection.level=new WeakReference<>(level);selection.pos=pos.immutable();selection.state=state;
            selection.view=SchoolProjectingWindowLayout.find(level,pos,state);
        }
        var view=selection.view;
        if(key.equals("fallback"))return view==null;
        if(view==null||!view.key().equals(key))return false;
        return part<0||(part<view.parts(state).size()&&view.pose(level,state.getValue(SchoolProjectingWindowBlock.FACING),view.parts(state).get(part))==pose);
    }
    @Override public boolean testForItem(ItemStack stack) {return key.equals("fallback")&&part<0;}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
