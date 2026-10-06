package net.nicomar2009.lsmmod.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Per-world labels and command aliases; original catalog IDs remain stable. */
public final class SchoolSpaceLabels {
    public record Label(String name, String alias) {}
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Path directory;
    private final Set<String> ids;
    private final Map<String, Label> labels = new LinkedHashMap<>();

    public SchoolSpaceLabels(Path directory, Set<String> ids) throws IOException {
        this.directory = directory;
        this.ids = Set.copyOf(ids);
        Path path = directory.resolve("school_space_names.json");
        if (!Files.exists(path)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.get("schema").getAsInt() != 1) throw new IllegalArgumentException("Unsupported schema");
            for (var entry : root.getAsJsonObject("names").entrySet()) {
                String id = entry.getKey();
                if (!ids.contains(id)) throw new IllegalArgumentException("Unknown original ID: " + id);
                String name = entry.getValue().getAsJsonObject().get("name").getAsString();
                String alias = validate(id, name);
                if (!alias.equals(entry.getValue().getAsJsonObject().get("alias").getAsString())) {
                    throw new IllegalArgumentException("Invalid alias: " + id);
                }
                labels.put(id, new Label(name, alias));
            }
        } catch (RuntimeException e) {
            throw new IOException("No se pudo leer " + path + ": " + e.getMessage(), e);
        }
    }

    public String name(String id, String fallback) { return labels.containsKey(id) ? labels.get(id).name() : fallback; }
    public String alias(String id) { return labels.containsKey(id) ? labels.get(id).alias() : id; }

    public String resolve(String token) {
        if (ids.contains(token)) return token;
        for (var entry : labels.entrySet()) if (entry.getValue().alias().equals(token)) return entry.getKey();
        return null;
    }

    public Set<String> suggestions() {
        var values = new LinkedHashSet<>(ids);
        labels.values().forEach(label -> values.add(label.alias()));
        return values;
    }

    public Label rename(String id, String input) throws IOException {
        String name = input.strip();
        Label replacement = new Label(name, validate(id, name));
        var updated = new LinkedHashMap<>(labels);
        updated.put(id, replacement);
        JsonObject root = new JsonObject();
        root.addProperty("schema", 1);
        root.add("names", JSON.toJsonTree(updated));
        write(directory.resolve("school_space_names.json"), JSON.toJson(root));
        labels.clear();
        labels.putAll(updated);
        return replacement;
    }

    private String validate(String id, String name) {
        if (!ids.contains(id)) throw new IllegalArgumentException("Espacio desconocido: " + id);
        if (name.isBlank() || name.length() > 80 || name.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("El nombre debe tener entre 1 y 80 caracteres, sin saltos de línea.");
        }
        String alias = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (alias.isEmpty() || alias.equals("off")) throw new IllegalArgumentException("Usa un nombre con letras o números; off está reservado.");
        if (ids.contains(alias) && !alias.equals(id)) throw new IllegalArgumentException("Ese nombre coincide con el ID de otro espacio: " + alias);
        for (var entry : labels.entrySet()) {
            if (!entry.getKey().equals(id) && entry.getValue().alias().equals(alias)) {
                throw new IllegalArgumentException("Ese nombre ya pertenece a " + entry.getKey() + ".");
            }
        }
        return alias;
    }

    public Path export(JsonObject catalog) throws IOException {
        JsonObject output = catalog.deepCopy();
        output.addProperty("export_schema", 1);
        output.addProperty("renamed_count", labels.size());
        for (var entry : output.getAsJsonArray("spaces")) {
            JsonObject space = entry.getAsJsonObject();
            String id = space.get("id").getAsString();
            String original = space.get("label").getAsString();
            space.addProperty("original_label", original);
            space.addProperty("label", name(id, original));
            space.addProperty("alias", alias(id));
            space.addProperty("renamed", labels.containsKey(id));
        }
        Path path = directory.resolve("school_spaces_export.json");
        write(path, JSON.toJson(output));
        return path;
    }

    /** Compact copy/paste report, with IDs sufficient to merge into the original catalog. */
    public String shareText() {
        JsonObject root = new JsonObject();
        root.addProperty("schema", 1);
        root.addProperty("source", "world (3).zip");
        root.addProperty("dimension", "minecraft:overworld");
        root.add("names", JSON.toJsonTree(labels));
        return JSON.toJson(root);
    }

    private static void write(Path path, String text) throws IOException {
        Files.createDirectories(path.getParent());
        Path temp = Files.createTempFile(path.getParent(), ".school-spaces-", ".tmp");
        try {
            Files.writeString(temp, text + "\n", StandardCharsets.UTF_8);
            try {
                Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
