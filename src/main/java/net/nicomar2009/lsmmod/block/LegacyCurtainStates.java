package net.nicomar2009.lsmmod.block;

import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/** Copy-on-write conversion preserves palette indices and packed block data. */
public final class LegacyCurtainStates {
    private static final Set<String> HOSTS=Set.of("lsmmod:school_glass","lsmmod:supported_school_glass",
            "lsmmod:mixed_school_glass","lsmmod:supported_school_glass_reversed","lsmmod:mixed_school_glass_reversed",
            "lsmmod:tall_classroom_entrance","lsmmod:tall_bathroom_door","lsmmod:tall_teachers_office_door",
            "lsmmod:tall_dining_door","lsmmod:double_school_entrance");
    private static final Set<String> PAVEMENT=Set.of("lsmmod:school_entrance_pavement","lsmmod:school_entrance_pavement_slab",
            "lsmmod:school_entrance_pavement_slope_1","lsmmod:school_entrance_pavement_slope_2",
            "lsmmod:school_entrance_pavement_slope_3","lsmmod:school_entrance_pavement_slope_4",
            "lsmmod:school_entrance_pavement_slope_5","lsmmod:school_entrance_pavement_slope_6");
    private static final String[] BORDER_PROPERTIES={"north","east","south","west","up","down"};
    private LegacyCurtainStates() {}
    public static CompoundTag migrateState(CompoundTag tag) {
        String name=tag.getStringOr("Name","");
        if(PAVEMENT.contains(name)||name.equals("lsmmod:street_school_bars")) {
            CompoundTag properties=tag.getCompoundOrEmpty("Properties");
            String[] retired=PAVEMENT.contains(name)?BORDER_PROPERTIES:new String[]{"column","row"};
            boolean legacy=false;for(String property:retired)legacy|=properties.contains(property);
            if(!legacy)return tag;
            CompoundTag converted=properties.copy(),copy=tag.copy();
            for(String property:retired)converted.remove(property);
            copy.put("Properties",converted);return copy;
        }
        if(!HOSTS.contains(name))return tag;
        CompoundTag properties=tag.getCompoundOrEmpty("Properties");
        if(!properties.contains("curtain_vertical")&&!properties.contains("frame_layout")
                &&!properties.contains("top_connected")&&!properties.contains("bottom_connected"))return tag;
        CompoundTag copy=tag.copy(), converted=properties.copy();
        String horizontal=properties.getStringOr("curtain","none");
        if(!horizontal.equals("none")&&!horizontal.contains("_")) {
            String vertical=properties.getStringOr("curtain_vertical","single");
            if(vertical.equals("none"))vertical="single";
            converted.putString("curtain",horizontal+"_"+vertical);
        }
        converted.remove("curtain_vertical");converted.remove("frame_layout");
        converted.remove("top_connected");converted.remove("bottom_connected");
        copy.put("Properties",converted);
        return copy;
    }
    public static CompoundTag migrateChunk(CompoundTag tag) {
        CompoundTag result=tag;
        ListTag sections=tag.getListOrEmpty("sections");
        for(int section=0;section<sections.size();section++) {
            ListTag palette=sections.getCompoundOrEmpty(section).getCompoundOrEmpty("block_states").getListOrEmpty("palette");
            for(int entry=0;entry<palette.size();entry++) {
                CompoundTag old=palette.getCompoundOrEmpty(entry), converted=migrateState(old);
                if(converted==old)continue;
                if(result==tag)result=tag.copy();
                result.getListOrEmpty("sections").getCompoundOrEmpty(section).getCompoundOrEmpty("block_states")
                        .getListOrEmpty("palette").set(entry,converted);
            }
        }
        return result;
    }
}
