package net.nicomar2009.lsmmod.modules.windows_and_enclosures.registry;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nicomar2009.lsmmod.modules.windows_and_enclosures.door.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCK_ENTITIES;

public final class ModuleDoorEntities {
    public static final Supplier<net.minecraft.world.entity.EntityType<net.nicomar2009.lsmcore.door.entity.DoorBody>> BODY=
        net.nicomar2009.lsmmod.registry.ModRegistries.ENTITIES.registerEntityType("school_door_surface",net.nicomar2009.lsmcore.door.entity.DoorBody::new,net.minecraft.world.entity.MobCategory.MISC,b->b.sized(.01F,.01F).clientTrackingRange(12).updateInterval(Integer.MAX_VALUE).noSummon());
    public static final Supplier<BlockEntityType<DoorPieceEntity>> PIECE=BLOCK_ENTITIES.register("assembly_door_piece",()->new BlockEntityType<>(DoorPieceEntity::new,DoorIntegration.blocks()));
    public static void initialize() {}
    private ModuleDoorEntities() {}
}
