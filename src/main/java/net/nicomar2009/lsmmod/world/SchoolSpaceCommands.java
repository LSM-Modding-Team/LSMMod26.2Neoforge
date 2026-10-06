package net.nicomar2009.lsmmod.world;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.world.SchoolSpaces.Space;

/** Operator tools for the surveyed Overworld school; never change its blocks. */
@EventBusSubscriber(modid = LSMMod.MOD_ID)
public final class SchoolSpaceCommands {
    private record Preview(List<Vec3> points, long expires) {}
    private static final Map<UUID, Preview> PREVIEWS = new HashMap<>();
    private static final int PAGE_SIZE = 12;

    private SchoolSpaceCommands() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("lsmmod")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(c -> help(c.getSource()))
                .then(Commands.literal("rooms")
                        .executes(c -> list(c.getSource(), 1))
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(c -> list(c.getSource(), IntegerArgumentType.getInteger(c, "page")))))
                .then(Commands.literal("where").executes(c -> where(c.getSource())))
                .then(Commands.literal("rename").then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(c -> rename(c.getSource(), null, StringArgumentType.getString(c, "name")))))
                .then(Commands.literal("renameid").then(Commands.argument("space", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(suggestions(c.getSource()), b))
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(c -> rename(c.getSource(), StringArgumentType.getString(c, "space"),
                                        StringArgumentType.getString(c, "name"))))))
                .then(Commands.literal("export").executes(c -> export(c.getSource())))
                .then(Commands.literal("tp").then(Commands.argument("space", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(suggestions(c.getSource()), b))
                        .executes(c -> teleport(c.getSource(), StringArgumentType.getString(c, "space")))))
                .then(Commands.literal("boundingbox")
                        .then(Commands.literal("off").executes(c -> clear(c.getSource())))
                        .then(Commands.argument("space", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(suggestions(c.getSource()), b))
                                .executes(c -> preview(c.getSource(), StringArgumentType.getString(c, "space"), 30))
                                .then(Commands.argument("seconds", IntegerArgumentType.integer(5, 120))
                                        .executes(c -> preview(c.getSource(), StringArgumentType.getString(c, "space"),
                                                IntegerArgumentType.getInteger(c, "seconds")))))));
    }

    private static int help(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Colegio: /lsmmod rooms [página], /lsmmod tp <espacio>, "
                + "/lsmmod boundingbox <espacio> [segundos], /lsmmod boundingbox off, /lsmmod where. "
                + "/lsmmod rename <nombre>, /lsmmod renameid <espacio> <nombre>, /lsmmod export. "
                + "Catálogo del mundo original, en el Overworld."), false);
        return 1;
    }

    private static int list(CommandSourceStack source, int page) {
        SchoolSpaceLabels labels = labels(source);
        if (labels == null) return 0;
        var spaces = new ArrayList<>(SchoolSpaces.ALL.values());
        int pages = (spaces.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        if (page > pages) return fail(source, "La última página es " + pages + ".");
        source.sendSuccess(() -> Component.literal("Espacios del colegio: " + spaces.size() + " — página " + page + "/" + pages), false);
        for (int i = (page - 1) * PAGE_SIZE; i < Math.min(page * PAGE_SIZE, spaces.size()); i++) {
            Space space = spaces.get(i);
            source.sendSuccess(() -> Component.literal(labels.alias(space.id()) + " — " + labels.name(space.id(), space.label())
                    + (labels.alias(space.id()).equals(space.id()) ? "" : " (ID: " + space.id() + ")")), false);
        }
        return 1;
    }

    private static Space lookup(CommandSourceStack source, String id) {
        SchoolSpaceLabels labels = labels(source);
        if (labels == null) return null;
        String original = labels.resolve(id);
        Space space = original == null ? null : SchoolSpaces.ALL.get(original);
        if (space == null) fail(source, "Espacio desconocido: " + id + ". Usa /lsmmod rooms o la tecla Tab.");
        return space == null ? null : new Space(space.id(), labels.name(space.id(), space.label()), space.boxes());
    }

    private static SchoolSpaceLabels readLabels(CommandSourceStack source) throws IOException {
        return new SchoolSpaceLabels(source.getServer().getWorldPath(LevelResource.ROOT).resolve("lsmmod"), SchoolSpaces.ALL.keySet());
    }

    private static SchoolSpaceLabels labels(CommandSourceStack source) {
        try { return readLabels(source); }
        catch (IOException e) {
            LSMMod.LOGGER.error("Cannot read school space names", e);
            fail(source, "No se pudieron leer los nombres guardados. " + e.getMessage());
            return null;
        }
    }

    private static Set<String> suggestions(CommandSourceStack source) {
        try { return readLabels(source).suggestions(); }
        catch (IOException e) { return SchoolSpaces.ALL.keySet(); }
    }

    private static double volume(Space space) {
        return space.boxes().stream().mapToDouble(b -> (b.maxX-b.minX)*(b.maxY-b.minY)*(b.maxZ-b.minZ)).sum();
    }

    private static int rename(CommandSourceStack source, String token, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!player.level().dimension().equals(Level.OVERWORLD)) return fail(source, "El colegio catalogado está en el Overworld.");
        SchoolSpaceLabels labels = labels(source);
        if (labels == null) return 0;
        var matches = SchoolSpaces.ALL.values().stream().filter(s -> s.contains(player.getX(), player.getY(), player.getZ()))
                .sorted(Comparator.comparingDouble(SchoolSpaceCommands::volume).thenComparing(Space::id)).toList();
        if (matches.isEmpty()) return fail(source, "Estás fuera de los espacios catalogados. Usa /lsmmod where.");
        Space chosen;
        if (token == null) {
            chosen = matches.getFirst();
            if (matches.size() > 1 && Double.compare(volume(chosen), volume(matches.get(1))) == 0) {
                return fail(source, "Hay varios recintos del mismo tamaño: " + String.join(", ", matches.stream().map(Space::id).toList())
                        + ". Elige con /lsmmod renameid <espacio> <nombre>.");
            }
        } else {
            String id = labels.resolve(token);
            chosen = matches.stream().filter(s -> s.id().equals(id)).findFirst().orElse(null);
            if (chosen == null) return fail(source, "Ese espacio no está donde estás. Opciones: "
                    + String.join(", ", matches.stream().map(Space::id).toList()));
        }
        try {
            var renamed = labels.rename(chosen.id(), name);
            Space selected = chosen;
            source.sendSuccess(() -> Component.literal("Guardado: " + selected.id() + " → " + renamed.name()
                    + ". Usa /lsmmod tp " + renamed.alias() + " o /lsmmod boundingbox " + renamed.alias() + "."), false);
            if (token == null && matches.size() > 1) {
                source.sendSuccess(() -> Component.literal("Se eligió el espacio más pequeño. Para otro: /lsmmod renameid <espacio> <nombre>. "
                        + "Coinciden: " + String.join(", ", matches.stream().map(Space::id).toList())), false);
            }
            return preview(source, selected.id(), 30);
        } catch (IllegalArgumentException e) {
            return fail(source, e.getMessage());
        } catch (IOException e) {
            LSMMod.LOGGER.error("Cannot save school space name", e);
            return fail(source, "No se pudo guardar el nombre; el anterior se conserva. " + e.getMessage());
        }
    }

    private static int export(CommandSourceStack source) {
        SchoolSpaceLabels labels = labels(source);
        if (labels == null) return 0;
        try (var stream = SchoolSpaces.class.getResourceAsStream("/data/lsmmod/school_spaces.json")) {
            if (stream == null) throw new IOException("Falta el catálogo original");
            var catalog = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            var path = labels.export(catalog).toAbsolutePath().normalize();
            source.sendSuccess(() -> Component.literal("Exportado: " + path + ". Puedes adjuntar este archivo en el chat."), false);
            source.sendSuccess(() -> Component.literal("[Copiar nombres para pegar en el chat]")
                    .withStyle(style -> style.withColor(ChatFormatting.AQUA).withUnderlined(true)
                            .withClickEvent(new ClickEvent.CopyToClipboard(labels.shareText()))), false);
            return 1;
        } catch (IOException e) {
            LSMMod.LOGGER.error("Cannot export school spaces", e);
            return fail(source, "No se pudo exportar el archivo. " + e.getMessage());
        }
    }

    private static int where(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!player.level().dimension().equals(Level.OVERWORLD)) return fail(source, "El colegio catalogado está en el Overworld.");
        SchoolSpaceLabels labels = labels(source);
        if (labels == null) return 0;
        var matches = SchoolSpaces.ALL.values().stream().filter(s -> s.contains(player.getX(), player.getY(), player.getZ()))
                .map(s -> labels.alias(s.id()) + " — " + labels.name(s.id(), s.label()) + " (ID: " + s.id() + ")").toList();
        source.sendSuccess(() -> Component.literal(matches.isEmpty() ? "Fuera de los espacios catalogados."
                : "Espacios aquí: " + String.join(", ", matches)), false);
        return matches.size();
    }

    private static int teleport(CommandSourceStack source, String id) throws CommandSyntaxException {
        Space space = lookup(source, id);
        if (space == null) return 0;
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = source.getServer().getLevel(Level.OVERWORLD);
        if (level == null) return fail(source, "Overworld no disponible.");
        Vec3 target = safeTarget(level, player, space);
        if (target == null) return fail(source, "No hay un punto seguro para estar de pie en " + id
                + ". Puede ser demasiado pequeño o faltar la estructura. Puedes ver su delimitación.");
        if (!player.teleportTo(level, target.x, target.y, target.z, Set.of(), player.getYRot(), player.getXRot(), true)) {
            return fail(source, "No se pudo completar el teletransporte.");
        }
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0;
        source.sendSuccess(() -> Component.literal("Teletransporte: " + space.id() + " — " + space.label()), false);
        return 1;
    }

    /** Check standing clearance and a real support surface, including slabs and furniture. */
    private static Vec3 safeTarget(ServerLevel level, ServerPlayer player, Space space) {
        var candidates = new ArrayList<Vec3>();
        for (AABB box : space.boxes()) {
            for (int x = (int) Math.floor(box.minX); x < box.maxX; x++) {
                for (int z = (int) Math.floor(box.minZ); z < box.maxZ; z++) {
                    level.getChunk(x >> 4, z >> 4);
                    for (double y = box.minY; y + 1.8 <= box.maxY; y += 0.5) {
                        candidates.add(new Vec3(x + 0.5, y, z + 0.5));
                    }
                }
            }
        }
        Vec3 center = space.boxes().getFirst().getCenter();
        candidates.sort(Comparator.comparingDouble(v -> v.distanceToSqr(center)));
        for (Vec3 p : candidates) {
            var body = new AABB(p.x - 0.3, p.y + 0.001, p.z - 0.3, p.x + 0.3, p.y + 1.8, p.z + 0.3);
            if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(p)) || !level.noCollision(player, body, true)) continue;
            var support = new AABB(p.x - 0.25, p.y - 0.06, p.z - 0.25, p.x + 0.25, p.y, p.z + 0.25);
            if (!level.getBlockCollisions(player, support).iterator().hasNext()) continue;
            boolean hazard = false;
            for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(body.minX, p.y - 0.1, body.minZ),
                    BlockPos.containing(body.maxX, body.maxY, body.maxZ))) {
                var state = level.getBlockState(pos);
                if (!state.getFluidState().isEmpty() || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)
                        || state.is(Blocks.END_PORTAL) || state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.MAGMA_BLOCK)
                        || state.is(Blocks.CACTUS) || state.is(Blocks.POWDER_SNOW)) { hazard = true; break; }
            }
            if (!hazard) return p;
        }
        return null;
    }

    private static int preview(CommandSourceStack source, String id, int seconds) throws CommandSyntaxException {
        Space space = lookup(source, id);
        if (space == null) return 0;
        ServerPlayer player = source.getPlayerOrException();
        if (!player.level().dimension().equals(Level.OVERWORLD)) return fail(source, "Ve al Overworld para ver esta delimitación.");
        List<Vec3> points = outline(space);
        long now = source.getServer().overworld().getGameTime();
        PREVIEWS.put(player.getUUID(), new Preview(points, now + seconds * 20L));
        draw(source.getServer().overworld(), player, points);
        source.sendSuccess(() -> Component.literal(id + " — " + space.label() + ": " + space.boxes().size()
                + " caja(s), partículas durante " + seconds + " segundos. /lsmmod boundingbox off para ocultar."), false);
        for (AABB box : space.boxes()) {
            source.sendSuccess(() -> Component.literal("Límites: " + box.minX + " " + box.minY + " " + box.minZ
                    + " → " + box.maxX + " " + box.maxY + " " + box.maxZ + " (máximo exclusivo)"), false);
        }
        return 1;
    }

    private static List<Vec3> outline(Space space) {
        var points = new java.util.LinkedHashSet<Vec3>();
        for (AABB b : space.boxes()) {
            for (double y : new double[]{b.minY, b.maxY}) {
                for (double z : new double[]{b.minZ, b.maxZ}) line(points, new Vec3(b.minX,y,z), new Vec3(b.maxX,y,z));
                for (double x : new double[]{b.minX, b.maxX}) line(points, new Vec3(x,y,b.minZ), new Vec3(x,y,b.maxZ));
            }
            for (double x : new double[]{b.minX, b.maxX}) {
                for (double z : new double[]{b.minZ, b.maxZ}) line(points, new Vec3(x,b.minY,z), new Vec3(x,b.maxY,z));
            }
        }
        return List.copyOf(points);
    }

    private static void line(Set<Vec3> points, Vec3 a, Vec3 b) {
        int steps = Math.max(1, (int) Math.ceil(a.distanceTo(b) * 2));
        for (int i = 0; i <= steps; i++) points.add(a.lerp(b, (double) i / steps));
    }

    private static void draw(ServerLevel level, ServerPlayer player, List<Vec3> points) {
        for (Vec3 p : points) {
            if (player.position().distanceToSqr(p) <= 96 * 96) {
                level.sendParticles(player, ParticleTypes.END_ROD, true, true, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
        }
    }

    private static int clear(CommandSourceStack source) throws CommandSyntaxException {
        PREVIEWS.remove(source.getPlayerOrException().getUUID());
        source.sendSuccess(() -> Component.literal("Delimitación desactivada; las partículas restantes se desvanecerán."), false);
        return 1;
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        var server = event.getServer();
        long now = server.overworld().getGameTime();
        PREVIEWS.entrySet().removeIf(e -> now >= e.getValue().expires() || server.getPlayerList().getPlayer(e.getKey()) == null);
        if (now % 10 != 0) return;
        for (var entry : PREVIEWS.entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null && player.level().dimension().equals(Level.OVERWORLD)) draw(server.overworld(), player, entry.getValue().points());
        }
    }

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent event) { PREVIEWS.clear(); }

    private static int fail(CommandSourceStack source, String text) {
        source.sendFailure(Component.literal(text));
        return 0;
    }
}
