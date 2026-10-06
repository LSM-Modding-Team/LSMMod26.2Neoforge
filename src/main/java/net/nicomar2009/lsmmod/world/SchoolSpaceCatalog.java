package net.nicomar2009.lsmmod.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Active regions plus durable world edits. Removed and renamed IDs never act as aliases. */
public final class SchoolSpaceCatalog {
    public record Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        public Box {
            for (double v : new double[]{minX,minY,minZ,maxX,maxY,maxZ}) {
                if (!Double.isFinite(v) || Math.abs(v) > 30_000_000) throw new IllegalArgumentException("Coordenadas fuera de rango.");
            }
            if (minX >= maxX || minY >= maxY || minZ >= maxZ) throw new IllegalArgumentException("La caja debe tener volumen positivo.");
            if (maxX-minX > 128 || maxY-minY > 128 || maxZ-minZ > 128) {
                throw new IllegalArgumentException("Cada lado de una caja admite hasta 128 bloques.");
            }
        }
        public static Box between(int x1, int y1, int z1, int x2, int y2, int z2) {
            return new Box(Math.min(x1,x2), Math.min(y1,y2), Math.min(z1,z2),
                    (double)Math.max(x1,x2)+1, (double)Math.max(y1,y2)+1, (double)Math.max(z1,z2)+1);
        }
        public double volume() { return (maxX-minX)*(maxY-minY)*(maxZ-minZ); }
        public boolean contains(double x, double y, double z) {
            return x>=minX && x<maxX && y>=minY && y<maxY && z>=minZ && z<maxZ;
        }
    }
    public record Area(String id, String label, List<Box> boxes) {
        public Area {
            checkId(id);
            checkLabel(label);
            boxes = List.copyOf(boxes);
            if (boxes.isEmpty() || boxes.size()>16) throw new IllegalArgumentException("Un espacio admite de 1 a 16 cajas.");
            if (boxes.stream().mapToDouble(Box::volume).sum()>65_536) throw new IllegalArgumentException("Un espacio admite hasta 65536 bloques de volumen.");
        }
        public boolean contains(double x, double y, double z) { return boxes.stream().anyMatch(b -> b.contains(x,y,z)); }
        public double volume() { return boxes.stream().mapToDouble(Box::volume).sum(); }
    }

    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Path directory;
    private final Map<String, Area> defaults;
    private final Map<String, Area> upserts = new LinkedHashMap<>();
    private final Set<String> deleted = new LinkedHashSet<>();

    public SchoolSpaceCatalog(Path directory, JsonObject base, JsonObject migration) throws IOException {
        this.directory = directory;
        try {
            defaults = parseAreas(base.getAsJsonArray("spaces"));
            Path path = directory.resolve("school_space_edits.json");
            if (Files.exists(path)) {
                JsonObject saved = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
                if (saved.get("schema").getAsInt()!=2) throw new IllegalArgumentException("Unsupported edits schema");
                upserts.putAll(parseAreas(saved.getAsJsonArray("upserts")));
                for (var id : saved.getAsJsonArray("deleted")) {
                    String value=id.getAsString(); checkId(value); deleted.add(value);
                }
                if (upserts.keySet().stream().anyMatch(deleted::contains)) throw new IllegalArgumentException("A space is both edited and deleted");
            } else if (Files.exists(directory.resolve("school_space_names.json"))) {
                migrate(migration);
            }
        } catch (RuntimeException e) {
            throw new IOException("Catálogo de espacios inválido: " + e.getMessage(), e);
        }
    }

    private void migrate(JsonObject migration) throws IOException {
        JsonObject old = JsonParser.parseString(Files.readString(directory.resolve("school_space_names.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        if (old.get("schema").getAsInt()!=1) throw new IllegalArgumentException("Unsupported names schema");
        JsonObject targets=migration.getAsJsonObject("targets");
        for (var entry : old.getAsJsonObject("names").entrySet()) {
            if (!targets.has(entry.getKey())) throw new IllegalArgumentException("Unknown legacy ID: " + entry.getKey());
            if (targets.get(entry.getKey()).isJsonNull()) continue; // Deliberately retired provisional region.
            String currentId=targets.get(entry.getKey()).getAsString();
            Area area=areas().get(currentId);
            if (area==null) throw new IllegalArgumentException("Conflicting legacy names: " + currentId);
            String name=entry.getValue().getAsJsonObject().get("name").getAsString();
            String newId=idForName(name);
            if (!newId.equals(entry.getValue().getAsJsonObject().get("alias").getAsString())) throw new IllegalArgumentException("Invalid legacy alias");
            if (!newId.equals(currentId) && areas().containsKey(newId)) throw new IllegalArgumentException("Nombre duplicado: " + newId);
            if (!newId.equals(currentId)) { deleted.add(currentId); upserts.remove(currentId); }
            upserts.put(newId,new Area(newId,name,area.boxes()));
            deleted.remove(newId);
        }
        save(upserts,deleted); // Keep the legacy file intact; never read it again once edits exist.
    }

    public Map<String, Area> areas() {
        var result=new LinkedHashMap<>(defaults);
        deleted.forEach(result::remove);
        result.putAll(upserts);
        return Collections.unmodifiableMap(result);
    }

    public Area rename(String id, String name) throws IOException {
        Area old=require(id);
        String newId=idForName(name);
        if (!id.equals(newId) && areas().containsKey(newId)) throw new IllegalArgumentException("Ese ID ya pertenece a otro espacio: " + newId);
        var edits=new LinkedHashMap<>(upserts); var removals=new LinkedHashSet<>(deleted);
        if (!id.equals(newId)) { edits.remove(id); removals.add(id); }
        Area renamed=new Area(newId,name.strip(),old.boxes());
        edits.put(newId,renamed); removals.remove(newId);
        commit(edits,removals);
        return renamed;
    }

    /** Replace the entire geometry for an existing ID or create a new region. */
    public Area define(String id, Box box) throws IOException {
        checkId(id);
        Area old=areas().get(id);
        Area replacement=new Area(id,old==null?id:old.label(),List.of(box));
        put(replacement);
        return replacement;
    }

    public Area addBox(String id, Box box) throws IOException {
        Area old=require(id); var boxes=new ArrayList<>(old.boxes());
        if (boxes.contains(box)) throw new IllegalArgumentException("Esa caja ya pertenece al espacio.");
        boxes.add(box);
        Area replacement=new Area(id,old.label(),boxes);
        put(replacement);
        return replacement;
    }

    public void delete(String id) throws IOException {
        require(id);
        var edits=new LinkedHashMap<>(upserts); var removals=new LinkedHashSet<>(deleted);
        edits.remove(id); removals.add(id);
        commit(edits,removals);
    }

    private Area require(String id) {
        Area area=areas().get(id);
        if (area==null) throw new IllegalArgumentException("Espacio desconocido: " + id + ". Usa /lsmmod rooms o Tab.");
        return area;
    }

    private void put(Area area) throws IOException {
        var edits=new LinkedHashMap<>(upserts); var removals=new LinkedHashSet<>(deleted);
        edits.put(area.id(),area); removals.remove(area.id());
        commit(edits,removals);
    }

    private void commit(Map<String,Area> edits, Set<String> removals) throws IOException {
        save(edits,removals);
        upserts.clear(); upserts.putAll(edits); deleted.clear(); deleted.addAll(removals);
    }

    private void save(Map<String,Area> edits, Set<String> removals) throws IOException {
        JsonObject root=new JsonObject(); root.addProperty("schema",2);
        root.add("upserts",serializeAreas(edits)); root.add("deleted",JSON.toJsonTree(removals));
        write(directory.resolve("school_space_edits.json"),JSON.toJson(root));
    }

    public JsonObject report() {
        JsonObject root=new JsonObject(); root.addProperty("schema",2);
        root.addProperty("source","world (3).zip"); root.addProperty("dimension","minecraft:overworld");
        root.addProperty("bounds_mode","maximum_exclusive");
        root.add("spaces",serializeAreas(areas())); root.add("deleted_ids",JSON.toJsonTree(deleted));
        return root;
    }

    public String shareText() { return JSON.toJson(report()); }
    public Path export() throws IOException {
        Path path=directory.resolve("school_spaces_export.json"); write(path,shareText()); return path;
    }

    public static String idForName(String name) {
        checkLabel(name);
        String id=Normalizer.normalize(name.strip(),Normalizer.Form.NFD).replaceAll("\\p{M}+","")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","_").replaceAll("^_+|_+$","");
        checkId(id); return id;
    }

    private static void checkId(String id) {
        if (id==null || !id.matches("[a-z0-9][a-z0-9_]{0,79}") || id.equals("off")) {
            throw new IllegalArgumentException("ID: usa 1–80 letras minúsculas, números o guiones bajos; off está reservado.");
        }
    }
    private static void checkLabel(String name) {
        if (name==null || name.isBlank() || name.length()>80 || name.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("El nombre debe tener entre 1 y 80 caracteres, sin saltos de línea.");
        }
    }

    private static Map<String,Area> parseAreas(JsonArray array) {
        var result=new LinkedHashMap<String,Area>();
        for (var value : array) {
            JsonObject object=value.getAsJsonObject(); var boxes=new ArrayList<Box>();
            for (var box : object.getAsJsonArray("boxes")) {
                JsonArray b=box.getAsJsonArray();
                if (b.size()!=6) throw new IllegalArgumentException("A box must have six coordinates");
                boxes.add(new Box(b.get(0).getAsDouble(),b.get(1).getAsDouble(),b.get(2).getAsDouble(),
                        b.get(3).getAsDouble(),b.get(4).getAsDouble(),b.get(5).getAsDouble()));
            }
            String id=object.get("id").getAsString();
            if (result.put(id,new Area(id,object.get("label").getAsString(),boxes))!=null) throw new IllegalArgumentException("Duplicate ID: " + id);
        }
        return result;
    }

    private static JsonArray serializeAreas(Map<String,Area> areas) {
        JsonArray array=new JsonArray();
        for (Area area : areas.values()) {
            JsonObject object=new JsonObject(); object.addProperty("id",area.id()); object.addProperty("label",area.label());
            JsonArray boxes=new JsonArray();
            for (Box box : area.boxes()) boxes.add(JSON.toJsonTree(new double[]{box.minX(),box.minY(),box.minZ(),box.maxX(),box.maxY(),box.maxZ()}));
            object.add("boxes",boxes); array.add(object);
        }
        return array;
    }

    private static void write(Path path, String text) throws IOException {
        Files.createDirectories(path.getParent()); Path temp=Files.createTempFile(path.getParent(),".school-spaces-",".tmp");
        try {
            Files.writeString(temp,text+"\n",StandardCharsets.UTF_8);
            try { Files.move(temp,path,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
}
