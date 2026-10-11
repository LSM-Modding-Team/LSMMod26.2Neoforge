package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonObject;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** The same AND/OR/property tests as native multipart, without duplicating its state table. */
public record ModelPropertiesPredicate(JsonObject properties) implements ModelPredicate {
    public static final Serializer<ModelPropertiesPredicate> SERIALIZER=new Serializer<>() {
        @Override public ModelPropertiesPredicate deserialize(JsonObject json) {return new ModelPropertiesPredicate(json.getAsJsonObject("properties").deepCopy());}
        @Override public JsonObject serialize(ModelPropertiesPredicate predicate) {JsonObject json=new JsonObject();json.add("properties",predicate.properties.deepCopy());return json;}
    };
    private static <T extends Comparable<T>> String value(BlockState state,Property<T> property) {return property.getName(state.getValue(property));}
    private static boolean matches(JsonObject properties,BlockState state) {
        for(var entry:properties.entrySet()) {
            String name=entry.getKey();
            if(name.equals("OR")||name.equals("AND")) {
                boolean result=name.equals("AND");
                for(var child:entry.getValue().getAsJsonArray()) {
                    boolean match=matches(child.getAsJsonObject(),state);
                    result=name.equals("OR")?result||match:result&&match;
                }
                if(!result)return false;
            } else {
                if(state==null)return false;
                Property<?> property=state.getBlock().getStateDefinition().getProperty(name);
                if(property==null)return false;
                String actual=value(state,property);boolean result=false;
                for(String allowed:entry.getValue().getAsString().split("\\|"))result|=allowed.equals(actual);
                if(!result)return false;
            }
        }
        return true;
    }
    @Override public boolean testForBlockState(BlockAndTintGetter level,BlockPos pos,BlockState state) {return matches(properties,state);}
    @Override public boolean testForItem(ItemStack stack) {return properties.size()==0;}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
