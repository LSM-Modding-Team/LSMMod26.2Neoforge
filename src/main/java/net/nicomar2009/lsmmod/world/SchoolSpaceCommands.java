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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
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
                .then(coordinateCommand("define", false))
                .then(coordinateCommand("addbox", true))
                .then(Commands.literal("delete").then(Commands.argument("space", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(suggestions(c.getSource()), b))
                        .executes(c -> delete(c.getSource(), StringArgumentType.getString(c, "space")))))
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
                + "/lsmmod define <id> <x1 y1 z1> <x2 y2 z2>, /lsmmod addbox <id> <x1 y1 z1> <x2 y2 z2>, "
                + "/lsmmod delete <id>. Esquinas inclusivas; cambios guardados en este mundo."), false);
        return 1;
    }

    private static int list(CommandSourceStack source, int page) {
        SchoolSpaceCatalog catalog = catalog(source);
        if (catalog == null) return 0;
        var spaces = new ArrayList<>(catalog.areas().values());
        int pages = Math.max(1, (spaces.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        if (page > pages) return fail(source, "La última página es " + pages + ".");
        source.sendSuccess(() -> Component.literal("Espacios del colegio: " + spaces.size() + " — página " + page + "/" + pages), false);
        for (int i = (page - 1) * PAGE_SIZE; i < Math.min(page * PAGE_SIZE, spaces.size()); i++) {
            var space = spaces.get(i);
            source.sendSuccess(() -> Component.literal(space.id() + " — " + space.label()), false);
        }
        return 1;
    }

    private static Space lookup(CommandSourceStack source, String id) {
        SchoolSpaceCatalog catalog = catalog(source);
        if (catalog == null) return null;
        var space = catalog.areas().get(id);
        if (space == null) fail(source, "Espacio desconocido: " + id + ". Usa /lsmmod rooms o la tecla Tab.");
        return space == null ? null : SchoolSpaces.space(space);
    }

    private static SchoolSpaceCatalog readCatalog(CommandSourceStack source) throws IOException {
        return new SchoolSpaceCatalog(source.getServer().getWorldPath(LevelResource.ROOT).resolve("lsmmod"), SchoolSpaces.BASE, SchoolSpaces.MIGRATION);
    }

    private static SchoolSpaceCatalog catalog(CommandSourceStack source) {
        try { return readCatalog(source); }
        catch (IOException e) {
            LSMMod.LOGGER.error("Cannot read school space edits", e);
            fail(source, "No se pudieron leer los espacios guardados. " + e.getMessage());
            return null;
        }
    }

    private static Set<String> suggestions(CommandSourceStack source) {
        try { return readCatalog(source).areas().keySet(); }
        catch (IOException e) { return Set.of(); }
    }

    private static int rename(CommandSourceStack source, String token, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!player.level().dimension().equals(Level.OVERWORLD)) return fail(source, "El colegio catalogado está en el Overworld.");
        SchoolSpaceCatalog catalog = catalog(source);
        if (catalog == null) return 0;
        var matches = catalog.areas().values().stream().filter(s -> s.contains(player.getX(), player.getY(), player.getZ()))
                .sorted(Comparator.comparingDouble(SchoolSpaceCatalog.Area::volume).thenComparing(SchoolSpaceCatalog.Area::id)).toList();
        if (matches.isEmpty()) return fail(source, "Estás fuera de los espacios catalogados. Usa /lsmmod where.");
        SchoolSpaceCatalog.Area chosen;
        if (token == null) {
            chosen = matches.getFirst();
            if (matches.size() > 1 && Double.compare(chosen.volume(), matches.get(1).volume()) == 0) {
                return fail(source, "Hay varios recintos del mismo tamaño: " + String.join(", ", matches.stream().map(SchoolSpaceCatalog.Area::id).toList())
                        + ". Elige con /lsmmod renameid <espacio> <nombre>.");
            }
        } else {
            chosen = matches.stream().filter(s -> s.id().equals(token)).findFirst().orElse(null);
            if (chosen == null) return fail(source, "Ese espacio no está donde estás. Opciones: "
                    + String.join(", ", matches.stream().map(SchoolSpaceCatalog.Area::id).toList()));
        }
        try {
            var renamed = catalog.rename(chosen.id(), name);
            String oldId = chosen.id();
            source.sendSuccess(() -> Component.literal("Guardado: " + oldId + " → " + renamed.id()
                    + ". Usa /lsmmod tp " + renamed.id() + " o /lsmmod boundingbox " + renamed.id()
                    + ". El ID anterior ya no funciona si cambió."), false);
            if (token == null && matches.size() > 1) {
                source.sendSuccess(() -> Component.literal("Se eligió el espacio más pequeño. Para otro: /lsmmod renameid <espacio> <nombre>. "
                        + "Coinciden antes del cambio: " + String.join(", ", matches.stream().map(SchoolSpaceCatalog.Area::id).toList())), false);
            }
            return preview(source, renamed.id(), 30);
        } catch (IllegalArgumentException e) {
            return fail(source, e.getMessage());
        } catch (IOException e) {
            LSMMod.LOGGER.error("Cannot save school space name", e);
            return fail(source, "No se pudo guardar el nombre; el anterior se conserva. " + e.getMessage());
        }
    }

    private static int export(CommandSourceStack source) {
        SchoolSpaceCatalog catalog = catalog(source);
        if (catalog == null) return 0;
        try {
            var path = catalog.export().toAbsolutePath().normalize();
            source.sendSuccess(() -> Component.literal("Exportado: " + path + ". Puedes adjuntar este archivo en el chat."), false);
            String text = catalog.shareText();
            if (text.length() <= 60_000) {
                source.sendSuccess(() -> Component.literal("[Copiar espacios y límites para pegar en el chat]")
                        .withStyle(style -> style.withColor(ChatFormatting.AQUA).withUnderlined(true)
                                .withClickEvent(new ClickEvent.CopyToClipboard(text))), false);
            } else {
                source.sendSuccess(() -> Component.literal("El catálogo es grande: adjunta el archivo exportado para compartirlo completo."),false);
            }
            return 1;
        } catch (IOException e) {
            LSMMod.LOGGER.error("Cannot export school spaces", e);
            return fail(source, "No se pudo exportar el archivo. " + e.getMessage());
        }
    }

    private static int where(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!player.level().dimension().equals(Level.OVERWORLD)) return fail(source, "El colegio catalogado está en el Overworld.");
        SchoolSpaceCatalog catalog = catalog(source);
        if (catalog == null) return 0;
        var matches = catalog.areas().values().stream().filter(s -> s.contains(player.getX(), player.getY(), player.getZ()))
                .map(s -> s.id() + " — " + s.label()).toList();
        source.sendSuccess(() -> Component.literal(matches.isEmpty() ? "Fuera de los espacios catalogados."
                : "Espacios aquí: " + String.join(", ", matches)), false);
        return matches.size();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> coordinateCommand(String name, boolean append) {
        return Commands.literal(name).then(Commands.argument("space", StringArgumentType.word())
                .suggests((c,b) -> SharedSuggestionProvider.suggest(suggestions(c.getSource()),b))
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .executes(c -> define(c.getSource(),StringArgumentType.getString(c,"space"),
                                        BlockPosArgument.getBlockPos(c,"from"),BlockPosArgument.getBlockPos(c,"to"),append)))));
    }

    private static int define(CommandSourceStack source, String id, BlockPos from, BlockPos to, boolean append) throws CommandSyntaxException {
        if (!source.getLevel().dimension().equals(Level.OVERWORLD)) return fail(source,"Las delimitaciones del colegio son del Overworld.");
        if (!source.getLevel().isInsideBuildHeight(from) || !source.getLevel().isInsideBuildHeight(to)) {
            return fail(source,"Las dos esquinas deben estar dentro de la altura construible del mundo.");
        }
        SchoolSpaceCatalog catalog=catalog(source);
        if (catalog==null) return 0;
        try {
            var box=SchoolSpaceCatalog.Box.between(from.getX(),from.getY(),from.getZ(),to.getX(),to.getY(),to.getZ());
            boolean existed=catalog.areas().containsKey(id);
            var result=append?catalog.addBox(id,box):catalog.define(id,box);
            PREVIEWS.clear();
            source.sendSuccess(() -> Component.literal((append?"Caja añadida a ":existed?"Delimitación reemplazada: ":"Espacio creado: ")
                    +result.id()+". Guardado en este mundo; "+result.boxes().size()+" caja(s)."),false);
            return source.getPlayer()==null?1:preview(source,result.id(),30);
        } catch (IllegalArgumentException e) { return fail(source,e.getMessage()); }
        catch (IOException e) {
            LSMMod.LOGGER.error("Cannot save school space geometry",e);
            return fail(source,"No se pudo guardar la delimitación; se conserva la anterior. "+e.getMessage());
        }
    }

    private static int delete(CommandSourceStack source, String id) {
        SchoolSpaceCatalog catalog=catalog(source);
        if (catalog==null) return 0;
        try {
            catalog.delete(id);
            PREVIEWS.clear();
            source.sendSuccess(() -> Component.literal("Espacio eliminado: "+id+". No reaparecerá al volver a abrir el mundo. No se cambiaron bloques."),false);
            return 1;
        } catch (IllegalArgumentException e) { return fail(source,e.getMessage()); }
        catch (IOException e) {
            LSMMod.LOGGER.error("Cannot delete school space",e);
            return fail(source,"No se pudo guardar la eliminación; el espacio se conserva. "+e.getMessage());
        }
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
        double length = space.boxes().stream().mapToDouble(b -> 4*((b.maxX-b.minX)+(b.maxY-b.minY)+(b.maxZ-b.minZ))).sum();
        double spacing = Math.max(0.5, length/3000);
        for (AABB b : space.boxes()) {
            for (double y : new double[]{b.minY, b.maxY}) {
                for (double z : new double[]{b.minZ, b.maxZ}) line(points, new Vec3(b.minX,y,z), new Vec3(b.maxX,y,z),spacing);
                for (double x : new double[]{b.minX, b.maxX}) line(points, new Vec3(x,y,b.minZ), new Vec3(x,y,b.maxZ),spacing);
            }
            for (double x : new double[]{b.minX, b.maxX}) {
                for (double z : new double[]{b.minZ, b.maxZ}) line(points, new Vec3(x,b.minY,z), new Vec3(x,b.maxY,z),spacing);
            }
        }
        return List.copyOf(points);
    }

    private static void line(Set<Vec3> points, Vec3 a, Vec3 b, double spacing) {
        int steps = Math.max(1, (int) Math.ceil(a.distanceTo(b) / spacing));
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
