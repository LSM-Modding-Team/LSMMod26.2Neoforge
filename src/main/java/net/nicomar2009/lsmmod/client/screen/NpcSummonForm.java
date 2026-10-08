package net.nicomar2009.lsmmod.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.nicomar2009.lsmmod.entity.SchoolNpcStat;

/** Only canonical mod-owned NBT is emitted. No arbitrary command or vanilla-NBT input. */
public final class NpcSummonForm {
    public record Field(String key, String range) { }
    private NpcSummonForm() { }

    public static List<Field> fields(boolean teacher) {
        List<Field> fields = new ArrayList<>();
        for (SchoolNpcStat stat : SchoolNpcStat.values()) fields.add(new Field(stat.nbtKey(), "1-5"));
        fields.add(new Field("Width", "1-16"));
        fields.add(new Field("Height", "1-16"));
        if (teacher) {
            fields.add(new Field("WitnessedAttackers", "[\"UUID\", ...]"));
            fields.add(new Field("ObservedUniforms", "{\"UUID\":true/false}"));
        } else {
            fields.add(new Field("StudentData.Level", "primary / secondary / unassigned"));
            fields.add(new Field("StudentData.Grade", "primary:1-6 / secondary:1-5"));
            fields.add(new Field("gender", "male / female"));
            fields.add(new Field("skinColor", "-1 / 0-63"));
            fields.add(new Field("eyeColor", "-1 / 0-7"));
            fields.add(new Field("haircutMale", "-1 / 0-31 (male)"));
            fields.add(new Field("haircutFemale", "-1 / 0-31 (female)"));
            fields.add(new Field("freeHair", "true / false (female)"));
            fields.add(new Field("glassesType", "-1 / 0-8; 0=none"));
        }
        return fields;
    }

    public static String command(boolean teacher, String position, Map<String, String> values) throws Exception {
        String[] coordinates = position.trim().split("\\s+");
        if (coordinates.length != 3) throw new IllegalArgumentException("position");
        boolean local = coordinates[0].startsWith("^");
        for (String coordinate : coordinates) {
            if (!coordinate.matches("[~^]?(?:-?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+))?")) throw new IllegalArgumentException("position");
            if (coordinate.isEmpty() || local != coordinate.startsWith("^")) throw new IllegalArgumentException("position");
        }
        CompoundTag tag = new CompoundTag();
        for (SchoolNpcStat stat : SchoolNpcStat.values()) integer(tag, values, stat.nbtKey(), 1, 5);
        integer(tag, values, "Width", 1, 16);
        integer(tag, values, "Height", 1, 16);
        if (teacher) {
            if (!value(values, "WitnessedAttackers").isEmpty()) {
                var parsed = TagParser.parseCompoundFully("{WitnessedAttackers:" + value(values, "WitnessedAttackers") + "}");
                if (!(parsed.get("WitnessedAttackers") instanceof ListTag list)) throw new IllegalArgumentException("WitnessedAttackers");
                ListTag normalized = new ListTag();
                for (int i = 0; i < list.size(); i++) normalized.add(StringTag.valueOf(UUID.fromString(list.getString(i).orElseThrow()).toString()));
                tag.put("WitnessedAttackers", normalized);
            }
            if (!value(values, "ObservedUniforms").isEmpty()) {
                var parsed = TagParser.parseCompoundFully("{ObservedUniforms:" + value(values, "ObservedUniforms") + "}");
                if (!(parsed.get("ObservedUniforms") instanceof CompoundTag map)) throw new IllegalArgumentException("ObservedUniforms");
                CompoundTag normalized = new CompoundTag();
                for (String key : map.keySet()) {
                    if (!(map.get(key) instanceof ByteTag)) throw new IllegalArgumentException("ObservedUniforms");
                    normalized.putBoolean(UUID.fromString(key).toString(), map.getBooleanOr(key, false));
                }
                tag.put("ObservedUniforms", normalized);
            }
        } else {
            String gender = value(values, "gender");
            if (!gender.isEmpty() && !gender.equals("male") && !gender.equals("female")) throw new IllegalArgumentException("gender");
            if (!gender.isEmpty()) tag.putString("gender", gender);
            String level = value(values, "StudentData.Level");
            String gradeText = value(values, "StudentData.Grade");
            if (!level.isEmpty() || !gradeText.isEmpty()) {
                if (!List.of("primary", "secondary", "unassigned").contains(level)) throw new IllegalArgumentException("StudentData.Level");
                int grade = level.equals("unassigned") ? 0 : parse(gradeText, "StudentData.Grade", 1, level.equals("primary") ? 6 : 5);
                CompoundTag classroom = new CompoundTag();
                classroom.putString("Level", level); classroom.putInt("Grade", grade);
                tag.put("StudentData", classroom);
            }
            integer(tag, values, "skinColor", -1, 63);
            integer(tag, values, "eyeColor", -1, 7);
            integer(tag, values, "glassesType", -1, 8);
            boolean female = gender.equals("female");
            integer(tag, values, female ? "haircutFemale" : "haircutMale", -1, 31);
            if (female && !value(values, "freeHair").isEmpty()) {
                String free = value(values, "freeHair");
                if (!free.equals("true") && !free.equals("false")) throw new IllegalArgumentException("freeHair");
                tag.putBoolean("freeHair", Boolean.parseBoolean(free));
            }
        }
        String result = "/summon lsmmod:" + (teacher ? "teacher" : "student") + " " + String.join(" ", coordinates) + " " + tag;
        if (result.length() > 32000) throw new IllegalArgumentException("command length");
        return result;
    }

    private static String value(Map<String, String> values, String key) { return values.getOrDefault(key, "").trim(); }
    private static void integer(CompoundTag tag, Map<String, String> values, String key, int min, int max) {
        if (!value(values, key).isEmpty()) tag.putInt(key, parse(value(values, key), key, min, max));
    }
    private static int parse(String text, String key, int min, int max) {
        try {
            int n = Integer.parseInt(text);
            if (n >= min && n <= max) return n;
        } catch (NumberFormatException ignored) { }
        throw new IllegalArgumentException(key + " (" + min + "-" + max + ")");
    }
}
