package net.nicomar2009.lsmmod.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.supermartijn642.fusion.api.model.predicates.ModelPredicate;
import com.supermartijn642.fusion.api.util.Serializer;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.nicomar2009.lsmmod.block.EntrancePavementConnections;
import net.nicomar2009.lsmmod.block.StreetSchoolBarsBlock;

/** Balanced decisions with one baked leaf per distinct native model. */
public record ModelSelectionPredicate(Layout layout,int min,int max,boolean refresh,List<Range> ranges) implements ModelPredicate {
    public record Range(int min,int max) {}
    public record Field(String name,List<String> values) {}
    public record Layout(String kind,List<Field> fields) {}
    private static final ConcurrentHashMap<Layout,Layout> LAYOUTS=new ConcurrentHashMap<>();
    public static final Serializer<ModelSelectionPredicate> SERIALIZER=new Serializer<>() {
        @Override public ModelSelectionPredicate deserialize(JsonObject json) {
            String kind=json.get("kind").getAsString();
            if(!kind.equals("state")&&!kind.equals("pavement")&&!kind.equals("bars"))throw new JsonParseException("Unknown school model selector kind: "+kind);
            List<Field> fields=new ArrayList<>();int size=1;
            for(var value:json.getAsJsonArray("keys")) {
                JsonObject field=value.getAsJsonObject();List<String> values=new ArrayList<>();
                for(var item:field.getAsJsonArray("values"))values.add(item.getAsString());
                if(values.isEmpty()||values.size()!=new java.util.HashSet<>(values).size())throw new JsonParseException("Selector domains must be nonempty and unique");
                fields.add(new Field(field.get("name").getAsString(),List.copyOf(values)));size=Math.multiplyExact(size,values.size());
            }
            Layout candidate=new Layout(kind,List.copyOf(fields));Layout layout=LAYOUTS.computeIfAbsent(candidate,key->key);
            int min=json.get("min").getAsInt(),max=json.get("max").getAsInt();
            if(min<0||max<min||max>=size)throw new JsonParseException("Selector range outside domain");
            List<Range> ranges=new ArrayList<>();
            if(json.has("ranges")) {
                int previous=-1;
                for(var entry:json.getAsJsonArray("ranges")) {
                    JsonArray pair=entry.getAsJsonArray();
                    if(pair.size()!=2)throw new JsonParseException("Selector range requires two bounds");
                    int first=pair.get(0).getAsInt(),last=pair.get(1).getAsInt();
                    if(first<min||last>max||last<first||first<=previous)throw new JsonParseException("Selector ranges must be ordered, disjoint and inside bounds");
                    ranges.add(new Range(first,last));previous=last;
                }
                if(ranges.isEmpty())throw new JsonParseException("Selector ranges must be nonempty");
            } else ranges.add(new Range(min,max));
            return new ModelSelectionPredicate(layout,min,max,json.has("refresh")&&json.get("refresh").getAsBoolean(),List.copyOf(ranges));
        }
        @Override public JsonObject serialize(ModelSelectionPredicate predicate) {
            JsonObject json=new JsonObject();json.addProperty("kind",predicate.layout.kind());JsonArray fields=new JsonArray();
            for(Field field:predicate.layout.fields()) {
                JsonObject value=new JsonObject();value.addProperty("name",field.name());JsonArray values=new JsonArray();
                field.values().forEach(values::add);value.add("values",values);fields.add(value);
            }
            json.add("keys",fields);json.addProperty("min",predicate.min);json.addProperty("max",predicate.max);
            if(predicate.ranges.size()>1||predicate.ranges.getFirst().min()!=predicate.min||predicate.ranges.getFirst().max()!=predicate.max) {
                JsonArray ranges=new JsonArray();
                for(Range range:predicate.ranges) {
                    JsonArray pair=new JsonArray();pair.add(range.min());pair.add(range.max());ranges.add(pair);
                }
                json.add("ranges",ranges);
            }
            if(predicate.refresh)json.addProperty("refresh",true);return json;
        }
    };
    private static final class Selection {
        WeakReference<ClientLevel> world=new WeakReference<>(null);
        long pos;BlockState state;Layout layout;int index;
    }
    private static final ThreadLocal<Selection> SELECTION=ThreadLocal.withInitial(Selection::new);
    private static <T extends Comparable<T>> String value(BlockState state,Property<T> property) {return property.getName(state.getValue(property));}
    private static int index(Layout layout,ClientLevel world,BlockPos pos,BlockState state) {
        if(state==null)return 0;
        int mask=0,profile=0,index=0;
        if(layout.kind.equals("pavement")&&world!=null&&pos!=null)mask=EntrancePavementConnections.visualMask(state,world,pos);
        if(layout.kind.equals("bars")&&world!=null&&pos!=null)profile=StreetSchoolBarsBlock.visualProfile(state,world,pos);
        for(Field field:layout.fields) {
            String value;
            if(layout.kind.equals("pavement")&&!field.name.equals("type")) {
                int bit=switch(field.name) {case "north"->0;case "east"->1;case "south"->2;case "west"->3;case "up"->4;case "down"->5;default->throw new IllegalStateException("Unknown border side");};
                value=Boolean.toString((mask&(1<<bit))!=0);
            } else if(layout.kind.equals("bars"))value=Integer.toString(field.name.equals("column")?profile/3:profile%3);
            else {
                Property<?> property=state.getBlock().getStateDefinition().getProperty(field.name);
                if(property==null)return -1;
                value=value(state,property);
            }
            int ordinal=field.values.indexOf(value);if(ordinal<0)return -1;
            index=index*field.values.size()+ordinal;
        }
        return index;
    }
    @Override public boolean testForBlockState(BlockAndTintGetter ignored,BlockPos pos,BlockState state) {
        ClientLevel world=Minecraft.getInstance().level;Selection selected=SELECTION.get();long packed=pos==null?0:pos.asLong();
        boolean dynamic=!layout.kind.equals("state");
        if(selected.layout!=layout||selected.state!=state||(dynamic&&(refresh||selected.world.get()!=world||selected.pos!=packed))) {
            selected.layout=layout;selected.state=state;selected.world=new WeakReference<>(world);selected.pos=packed;
            selected.index=index(layout,world,pos,state);
        }
        return contains(selected.index);
    }
    private boolean contains(int value) {
        if(value<min||value>max)return false;
        int low=0,high=ranges.size()-1;
        while(low<=high) {
            int middle=(low+high)>>>1;Range range=ranges.get(middle);
            if(value<range.min())high=middle-1;
            else if(value>range.max())low=middle+1;
            else return true;
        }
        return false;
    }
    @Override public boolean testForItem(ItemStack stack) {return contains(0);}
    @Override public Serializer<? extends ModelPredicate> getSerializer() {return SERIALIZER;}
}
