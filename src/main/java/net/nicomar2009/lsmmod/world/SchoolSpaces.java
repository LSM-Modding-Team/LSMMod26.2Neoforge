package net.nicomar2009.lsmmod.world;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.AABB;

/** Interior regions surveyed in the supplied world, at their original coordinates. */
public final class SchoolSpaces {
    public record Space(String id, String label, List<AABB> boxes) {
        public boolean contains(double x, double y, double z) {
            return boxes.stream().anyMatch(box -> box.contains(x, y, z));
        }
    }

    public static final JsonObject BASE = read("school_spaces.json");
    public static final JsonObject MIGRATION = read("school_spaces_migration.json");

    private SchoolSpaces() {}

    private static JsonObject read(String name) {
        try (var stream = SchoolSpaces.class.getResourceAsStream("/data/lsmmod/" + name)) {
            if (stream == null) throw new IllegalStateException("Missing school catalog resource: " + name);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot load school spaces", e);
        }
    }

    public static Space space(SchoolSpaceCatalog.Area area) {
        return new Space(area.id(), area.label(), area.boxes().stream().map(b ->
                new AABB(b.minX(),b.minY(),b.minZ(),b.maxX(),b.maxY(),b.maxZ())).toList());
    }
}
