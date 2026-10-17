package net.nicomar2009.lsmmod.modules.lockers_and_storage.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LockerBlockEntity;
import net.nicomar2009.lsmmod.modules.lockers_and_storage.block.LostAndFoundBoxBlockEntity;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCK_ENTITIES;

public final class ModuleBlockEntities {
    public static final Supplier<BlockEntityType<LockerBlockEntity>> LOCKER = BLOCK_ENTITIES.register(
            "locker",
            () -> new BlockEntityType<>(LockerBlockEntity::new, ModBlocks.LOCKER.get()));

    public static final Supplier<BlockEntityType<LostAndFoundBoxBlockEntity>> LOST_AND_FOUND_BOX = BLOCK_ENTITIES.register(
            "lost_and_found_box",
            () -> new BlockEntityType<>(LostAndFoundBoxBlockEntity::new, ModBlocks.LOST_AND_FOUND_BOX.get()));

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, LOST_AND_FOUND_BOX.get(),
                (box, side) -> VanillaContainerWrapper.of(box));
    }
    private ModuleBlockEntities() {}
}
