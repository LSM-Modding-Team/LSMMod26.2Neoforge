package net.nicomar2009.lsmmod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.block.AwningBlockEntity;
import net.nicomar2009.lsmmod.block.DeskBlockEntity;
import net.nicomar2009.lsmmod.block.ElementaryDeskBlockEntity;
import net.nicomar2009.lsmmod.block.LockerBlockEntity;

import java.util.function.Supplier;

/** Block entity registry. */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LSMMod.MOD_ID);

    // Only the LEFT half creates a DeskBlockEntity, but the type is valid for the whole block.
    // Only the student's desk has storage; the teacher's desk is decorative and has no block entity.
    public static final Supplier<BlockEntityType<DeskBlockEntity>> STUDENTS_DESK = BLOCK_ENTITIES.register(
            "students_desk",
            () -> new BlockEntityType<>(DeskBlockEntity::new,
                    ModBlocks.STUDENTS_DESK.get()));

    public static final Supplier<BlockEntityType<LockerBlockEntity>> LOCKER = BLOCK_ENTITIES.register(
            "locker",
            () -> new BlockEntityType<>(LockerBlockEntity::new, ModBlocks.LOCKER.get()));

    public static final Supplier<BlockEntityType<ElementaryDeskBlockEntity>> ELEMENTARY_DESK = BLOCK_ENTITIES.register(
            "elementary_desk",
            () -> new BlockEntityType<>(ElementaryDeskBlockEntity::new, ModBlocks.ELEMENTARY_DESK.get()));

    public static final Supplier<BlockEntityType<AwningBlockEntity>> AWNING = BLOCK_ENTITIES.register(
            "awning", () -> new BlockEntityType<>(AwningBlockEntity::new,
                    ModBlocks.AWNING.get(), ModBlocks.AWNING_SUPPORT.get()));


    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
