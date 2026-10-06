package net.nicomar2009.lsmmod.world;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.phys.AABB;

/** Interior regions surveyed in the supplied world, at their original coordinates. */
public final class SchoolSpaces {
    public record Space(String id, String label, List<AABB> boxes) {
        public boolean contains(double x, double y, double z) {
            return boxes.stream().anyMatch(box -> box.contains(x, y, z));
        }
    }

    public static final Map<String, Space> ALL = load();

    private SchoolSpaces() {}

    private static Map<String, Space> load() {
        var result = new LinkedHashMap<String, Space>();
        try (var stream = SchoolSpaces.class.getResourceAsStream("/data/lsmmod/school_spaces.json")) {
            if (stream == null) throw new IllegalStateException("Missing school space catalog");
            var root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (var entry : root.getAsJsonArray("spaces")) {
                var object = entry.getAsJsonObject();
                var boxes = new ArrayList<AABB>();
                for (var box : object.getAsJsonArray("boxes")) {
                    var b = box.getAsJsonArray();
                    if (b.size() != 6) throw new IllegalStateException("Invalid school box");
                    var bounds = new AABB(b.get(0).getAsDouble(), b.get(1).getAsDouble(), b.get(2).getAsDouble(),
                            b.get(3).getAsDouble(), b.get(4).getAsDouble(), b.get(5).getAsDouble());
                    if (bounds.minX >= bounds.maxX || bounds.minY >= bounds.maxY || bounds.minZ >= bounds.maxZ) {
                        throw new IllegalStateException("Empty school box");
                    }
                    boxes.add(bounds);
                }
                String id = object.get("id").getAsString();
                if (boxes.isEmpty() || result.put(id, new Space(id, object.get("label").getAsString(), List.copyOf(boxes))) != null) {
                    throw new IllegalStateException("Invalid or duplicate school space: " + id);
                }
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot load school spaces", e);
        }
        return java.util.Collections.unmodifiableMap(result);
    }
}
