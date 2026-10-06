import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import net.nicomar2009.lsmmod.world.SchoolSpaceLabels;

/** Persistence, collision and export contracts; run once against the compiled class. */
public class SchoolSpaceLabelsCheck {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory(Path.of("build"), "school-labels-check-");
        Set<String> ids = Set.of("salon_01", "salon_02");
        var labels = new SchoolSpaceLabels(root, ids);
        labels.rename("salon_01", "5to secundaria");
        var reopened = new SchoolSpaceLabels(root, ids);
        check("salon_01".equals(reopened.resolve("5to_secundaria")), "Alias must survive reopening");
        check("salon_01".equals(reopened.resolve("salon_01")), "Original ID must remain usable");
        Path saved = root.resolve("school_space_names.json");
        String before = Files.readString(saved);
        for (String conflict : new String[]{"5to secundaria", "salon_01", "off", "!!!"}) {
            try {
                reopened.rename("salon_02", conflict);
                throw new AssertionError("Accepted conflicting or invalid name: " + conflict);
            } catch (IllegalArgumentException expected) {}
            check(Files.readString(saved).equals(before), "Rejected rename must not modify saved names");
        }
        reopened.rename("salon_01", "Sala de Música");
        check(reopened.resolve("5to_secundaria") == null, "Replaced alias must be released");
        check("salon_01".equals(reopened.resolve("sala_de_musica")), "Accented names must have command aliases");
        var freshWorld = new SchoolSpaceLabels(root.resolve("another-world"), ids);
        check(freshWorld.resolve("sala_de_musica") == null, "Names must remain world-specific");
        var original = JsonParser.parseString("""
                {"spaces":[{"id":"salon_01","label":"Salón 01","boxes":[[1,2,3,4,5,6]]},
                           {"id":"salon_02","label":"Salón 02","boxes":[[7,8,9,10,11,12]]}]}
                """).getAsJsonObject();
        var exported = JsonParser.parseString(Files.readString(reopened.export(original))).getAsJsonObject();
        var first = exported.getAsJsonArray("spaces").get(0).getAsJsonObject();
        check(first.get("label").getAsString().equals("Sala de Música"), "Export must include edited names");
        check(first.get("original_label").getAsString().equals("Salón 01"), "Export must retain original labels");
        check(first.getAsJsonArray("boxes").equals(original.getAsJsonArray("spaces").get(0).getAsJsonObject().getAsJsonArray("boxes")),
                "Export must preserve geometry");
        check(original.getAsJsonArray("spaces").get(0).getAsJsonObject().get("label").getAsString().equals("Salón 01"),
                "Export must not mutate the base catalog");
        check(JsonParser.parseString(reopened.shareText()).getAsJsonObject().getAsJsonObject("names").size() == 1,
                "Clipboard report must contain the saved changes");
        Files.writeString(saved, "invalid-json", StandardCharsets.UTF_8);
        try {
            new SchoolSpaceLabels(root, ids);
            throw new AssertionError("Corrupted file was silently accepted");
        } catch (java.io.IOException expected) {}
        check(Files.readString(saved).equals("invalid-json"), "Corrupted file must remain recoverable");
        Path blocked = root.resolve("blocked-write");
        var unavailable = new SchoolSpaceLabels(blocked, ids);
        Files.createDirectories(blocked.resolve("school_space_names.json"));
        try {
            unavailable.rename("salon_01", "No guardado");
            throw new AssertionError("Failed disk write was accepted");
        } catch (java.io.IOException expected) {}
        check(unavailable.resolve("no_guardado") == null, "Failed writes must not update names in memory");
        System.out.println("School labels: persistence, alias collisions, world isolation, export and failed writes OK");
    }
}
