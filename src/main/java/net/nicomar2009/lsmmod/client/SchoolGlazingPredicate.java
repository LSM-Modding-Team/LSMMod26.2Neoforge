package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nicomar2009.lsmmod.block.SchoolGlazing;

/** Legacy single masks and grouped masks; the first group refreshes the shared selection. */
public record SchoolGlazingPredicate(int mask, int members) implements ModelPredicate {
    public SchoolGlazingPredicate(int mask) { this(mask, 1 << mask); }
    public static final Serializer<SchoolGlazingPredicate> SERIALIZER=new Serializer<>() {
        @Override public SchoolGlazingPredicate deserialize(JsonObject json) {
            if(json.has("masks")) {
                if(!json.get("masks").isJsonArray() || json.getAsJsonArray("masks").size()==0)
                    throw new JsonParseException("Glazing masks must be a nonempty array");
                int members=0;
                for(var entry:json.getAsJsonArray("masks")) {
                    if(!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isNumber())
                        throw new JsonParseException("Glazing masks must be integers from 0 to 31");
                    int value=entry.getAsInt();
                    if(value<0 || value>31 || entry.getAsDouble()!=value || (members & (1<<value))!=0)
                        throw new JsonParseException("Glazing masks must be unique integers from 0 to 31");
                    members |= 1<<value;
                }
                return new SchoolGlazingPredicate(Integer.numberOfTrailingZeros(members), members);
            }
            if(!json.has("mask")||!json.get("mask").isJsonPrimitive()||!json.getAsJsonPrimitive("mask").isNumber())
                throw new JsonParseException("Glazing mask must be an integer from 0 to 31");
            int mask=json.get("mask").getAsInt();
            if(mask<0||mask>31)throw new JsonParseException("Glazing mask must be from 0 to 31");
            return new SchoolGlazingPredicate(mask);
        }
        @Override public JsonObject serialize(SchoolGlazingPredicate value) {
            JsonObject json=new JsonObject();
            if(Integer.bitCount(value.members())==1) json.addProperty("mask",value.mask());
            else {
                JsonArray masks=new JsonArray();
                for(int mask=0;mask<32;mask++) if((value.members() & (1<<mask))!=0) masks.add(mask);
                json.add("masks",masks);
            }
            return json;
        }
    };
    private static final class Selection {
        WeakReference<ClientLevel> world=new WeakReference<>(null);
        long pos;
        BlockState state;
        int mask;
    }
    private static final ThreadLocal<Selection> SELECTION=ThreadLocal.withInitial(Selection::new);
    @Override public boolean testForBlockState(BlockAndTintGetter ignored,BlockPos pos,BlockState state) {
        ClientLevel world=Minecraft.getInstance().level;
        if(world==null||pos==null||state==null)return (members & (1<<3))!=0;
        Selection selection=SELECTION.get();
        // No long-lived row cache: refresh at the beginning of every ordered model selection.
        if(mask==0||selection.world.get()!=world||selection.pos!=pos.asLong()||selection.state!=state) {
            selection.world=new WeakReference<>(world);selection.pos=pos.asLong();selection.state=state;
            selection.mask=SchoolGlazing.visualMask(world,pos,state);
        }
        return (members & (1<<selection.mask))!=0;
    }
    @Override public boolean testForItem(ItemStack stack) {return (members & (1<<3))!=0;}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
