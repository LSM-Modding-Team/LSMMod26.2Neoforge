package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.nicomar2009.lsmcore.door.model.DoorRecord;
import net.nicomar2009.paintingmod.api.*;

/** ONE paint authority per structure. No Core state or per-cell paint copies are persisted here. */
public final class DoorPaintStore {
    private static final Map<ServerLevel,Map<UUID,PaintLayer>> CACHE=new WeakHashMap<>();
    private DoorPaintStore() {}
    private static Path path(ServerLevel level,UUID id) {
        var dimension=level.dimension().identifier();
        return level.getServer().getWorldPath(LevelResource.ROOT).resolve("lsmmod/door_paint")
                .resolve(dimension.getNamespace()).resolve(dimension.getPath()).resolve(id+".nbt");
    }
    public static PaintLayer get(ServerLevel level,DoorRecord r) throws IOException {
        var cache=CACHE.computeIfAbsent(level,k->new HashMap<>());var layer=cache.get(r.id());
        if(layer==null) {
            var tag=NbtIo.readCompressed(path(level,r.id()),NbtAccounter.unlimitedHeap());
            layer=PaintLayer.load(tag,r.id(),DoorCatalog.patches(r));cache.put(r.id(),layer);
        }
        return layer;
    }
    public static void write(ServerLevel level,DoorRecord r,PaintLayer layer) throws IOException {
        Path file=path(level,r.id());Files.createDirectories(file.getParent());
        Path temp=Files.createTempFile(file.getParent(),"paint-",".tmp");
        try {
            NbtIo.writeCompressed(layer.save(DoorCatalog.patches(r)),temp);
            Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
            CACHE.computeIfAbsent(level,k->new HashMap<>()).put(r.id(),layer);
        } finally {Files.deleteIfExists(temp);}
    }
    public static void initialize(ServerLevel level,DoorRecord r,PaintData data) throws IOException {
        if(Files.exists(path(level,r.id()))) {get(level,r);return;}
        write(level,r,new PaintLayer(r.id(),0,data));
    }
    public static void remove(ServerLevel level,UUID id) throws IOException {
        Files.deleteIfExists(path(level,id));var cache=CACHE.get(level);if(cache!=null)cache.remove(id);
    }
}
