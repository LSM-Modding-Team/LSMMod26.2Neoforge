package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonObject;
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

/** Fusion evaluates one ordered series; mask zero starts a fresh per-thread selection. */
public record SchoolGlazingPredicate(int mask) implements ModelPredicate {
    public static final Serializer<SchoolGlazingPredicate> SERIALIZER=new Serializer<>() {
        @Override public SchoolGlazingPredicate deserialize(JsonObject json) {
            if(!json.has("mask")||!json.get("mask").isJsonPrimitive()||!json.getAsJsonPrimitive("mask").isNumber())
                throw new JsonParseException("Glazing mask must be an integer from 0 to 31");
            int mask=json.get("mask").getAsInt();
            if(mask<0||mask>31)throw new JsonParseException("Glazing mask must be from 0 to 31");
            return new SchoolGlazingPredicate(mask);
        }
        @Override public JsonObject serialize(SchoolGlazingPredicate value) {JsonObject json=new JsonObject();json.addProperty("mask",value.mask());return json;}
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
        if(world==null||pos==null||state==null)return mask==3;
        Selection selection=SELECTION.get();
        // No long-lived row cache: refresh at the beginning of every ordered model selection.
        if(mask==0||selection.world.get()!=world||selection.pos!=pos.asLong()||selection.state!=state) {
            selection.world=new WeakReference<>(world);selection.pos=pos.asLong();selection.state=state;
            selection.mask=SchoolGlazing.visualMask(world,pos,state);
        }
        return mask==selection.mask;
    }
    @Override public boolean testForItem(ItemStack stack) {return mask==3;}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
