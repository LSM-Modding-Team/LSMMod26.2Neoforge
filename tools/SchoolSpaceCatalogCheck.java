import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.nicomar2009.lsmmod.world.SchoolSpaceCatalog;
import net.nicomar2009.lsmmod.world.SchoolSpaceCatalog.Box;

/** Checks active IDs, legacy migration and durable geometry editing against the real catalog. */
public class SchoolSpaceCatalogCheck {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    public static void main(String[] args) throws Exception {
        Path resource=Path.of("src/main/resources/data/lsmmod");
        JsonObject base=JsonParser.parseString(Files.readString(resource.resolve("school_spaces.json"))).getAsJsonObject();
        JsonObject migration=JsonParser.parseString(Files.readString(resource.resolve("school_spaces_migration.json"))).getAsJsonObject();
        Path root=Files.createTempDirectory(Path.of("build"),"school-catalog-check-");
        var clean=new SchoolSpaceCatalog(root,base,migration);
        check(clean.areas().size()==38,"Expected 38 reviewed regions");
        JsonObject names=new JsonObject();
        for (var entry : migration.getAsJsonObject("targets").entrySet()) {
            if (entry.getValue().isJsonNull()) {
                check(!clean.areas().containsKey(entry.getKey()),"Retired region remains active");
            } else {
                String id=entry.getValue().getAsString();
                check(clean.areas().containsKey(id),"Missing confirmed ID: "+id);
                if (!id.equals(entry.getKey()) || id.equals("comedor")) {
                    JsonObject label=new JsonObject(); label.addProperty("name",id); label.addProperty("alias",id);
                    names.add(entry.getKey(),label);
                    if (!id.equals(entry.getKey())) check(!clean.areas().containsKey(entry.getKey()),"Old ID is still accepted");
                }
            }
        }
        check(names.size()==32,"Expected the supplied 32-name mapping");
        // Include a retired legacy label: migration must not resurrect the removed provisional region.
        JsonObject retired=new JsonObject(); retired.addProperty("name","rincon_incorrecto"); retired.addProperty("alias","rincon_incorrecto");
        names.add("recinto_01",retired);
        JsonObject old=new JsonObject(); old.addProperty("schema",1); old.add("names",names);
        Path legacy=root.resolve("school_space_names.json"); Files.writeString(legacy,old.toString());
        var migrated=new SchoolSpaceCatalog(root,base,migration);
        check(migrated.areas().equals(clean.areas()),"Migration changed confirmed geometry or revived old regions");
        check(Files.readString(legacy).equals(old.toString()),"Legacy file should remain recoverable");
        Box single=Box.between(-60,140,10,-60,140,10);
        check(single.volume()==1 && single.contains(-59.5,140.5,10.5),"Inclusive corners must define a one-block box");
        Box reversed=Box.between(-58,143,14,-62,140,10);
        migrated.define("sala_nueva",reversed);
        check(new SchoolSpaceCatalog(root,base,migration).areas().get("sala_nueva").boxes().equals(java.util.List.of(reversed)),"Created geometry did not survive reload");
        migrated.define("sala_nueva",single);
        migrated.addBox("sala_nueva",Box.between(-55,140,10,-54,143,12));
        check(migrated.areas().get("sala_nueva").boxes().size()==2,"Replace/addbox semantics failed");
        migrated.rename("sala_nueva","Sala de Música");
        check(!migrated.areas().containsKey("sala_nueva") && migrated.areas().containsKey("sala_de_musica"),"Rename retained an obsolete ID");
        String before=Files.readString(root.resolve("school_space_edits.json"));
        try { migrated.rename("5tosec","4tosec"); throw new AssertionError("Duplicate ID accepted"); }
        catch (IllegalArgumentException expected) {}
        try { migrated.define("gigante",Box.between(0,0,0,128,128,128)); throw new AssertionError("Oversized region accepted"); }
        catch (IllegalArgumentException expected) {}
        check(Files.readString(root.resolve("school_space_edits.json")).equals(before),"Rejected edits modified saved state");
        migrated.delete("5tosec"); migrated.delete("sala_de_musica");
        var reopened=new SchoolSpaceCatalog(root,base,migration);
        check(!reopened.areas().containsKey("5tosec") && !reopened.areas().containsKey("sala_de_musica"),"Deleted regions reappeared from defaults or legacy names");
        check(new SchoolSpaceCatalog(root.resolve("other-world"),base,migration).areas().containsKey("5tosec"),"Edits leaked between worlds");
        JsonObject report=JsonParser.parseString(Files.readString(reopened.export())).getAsJsonObject();
        check(report.getAsJsonArray("spaces").size()==37 && report.getAsJsonArray("deleted_ids").size()==3,"Export must include only live regions and durable deletions");
        check(report.equals(JsonParser.parseString(reopened.shareText())),"Clipboard and file reports disagree");
        reopened.define("5tosec",reversed);
        check(new SchoolSpaceCatalog(root,base,migration).areas().get("5tosec").boxes().equals(java.util.List.of(reversed)),"Explicit recreation should replace a tombstone");
        Path blocked=root.resolve("blocked"); var unavailable=new SchoolSpaceCatalog(blocked,base,migration);
        Files.createDirectories(blocked.resolve("school_space_edits.json"));
        try { unavailable.delete("5tosec"); throw new AssertionError("Failed disk write accepted"); }
        catch (IOException expected) {}
        check(unavailable.areas().containsKey("5tosec"),"Failed write must preserve in-memory regions");
        Files.writeString(root.resolve("school_space_edits.json"),"bad-json");
        try { new SchoolSpaceCatalog(root,base,migration); throw new AssertionError("Corruption silently reset the catalog"); }
        catch (IOException expected) {}
        check(Files.readString(root.resolve("school_space_edits.json")).equals("bad-json"),"Corrupt files must remain recoverable");
        System.out.println("School catalog: 32-name migration, retired IDs, geometry, rename, deletion, export, isolation and write failures OK");
    }
}
