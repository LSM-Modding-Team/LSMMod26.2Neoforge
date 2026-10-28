package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;

import java.io.IOException;
import java.nio.file.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.nicomar2009.lsmcore.door.model.DoorRecord;

/** Preserve existing surface data verbatim; no external paint classes or schema interpretation. */
public final class DoorPaintStore {
    private static final String ARCHIVE="assembly_door_paint_archive";
    private static final String ITEM_DATA="lsmmod_archived_item_data";
    private DoorPaintStore() {}
    private static Path path(ServerLevel level,DoorRecord record) {
        var dimension=level.dimension().identifier();
        return level.getServer().getWorldPath(LevelResource.ROOT).resolve("lsmmod/door_paint")
                .resolve(dimension.getNamespace()).resolve(dimension.getPath()).resolve(record.id()+".nbt");
    }
    public static void initialize(ServerLevel level,DoorRecord record,CompoundTag itemData) throws IOException {
        Path file=path(level,record);
        if(Files.exists(file)||itemData==null)return;
        CompoundTag saved;
        if(itemData.contains(ARCHIVE))saved=itemData.getCompoundOrEmpty(ARCHIVE).copy();
        else if(itemData.contains("assembly_door_paint")) {
            var original=new CompoundTag();
            original.put("assembly_door_paint",itemData.getCompoundOrEmpty("assembly_door_paint").copy());
            saved=new CompoundTag();saved.put(ITEM_DATA,original);
        }else return;
        Files.createDirectories(file.getParent());
        Path temp=Files.createTempFile(file.getParent(),"paint-archive-",".tmp");
        try {
            NbtIo.writeCompressed(saved,temp);
            Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
        }finally {Files.deleteIfExists(temp);}
    }
    public static CompoundTag itemData(ServerLevel level,DoorRecord record) throws IOException {
        Path file=path(level,record);
        if(!Files.exists(file))return new CompoundTag();
        var saved=NbtIo.readCompressed(file,NbtAccounter.unlimitedHeap());
        if(saved.contains(ITEM_DATA))return saved.getCompoundOrEmpty(ITEM_DATA).copy();
        var result=new CompoundTag();result.put(ARCHIVE,saved);return result;
    }
}
