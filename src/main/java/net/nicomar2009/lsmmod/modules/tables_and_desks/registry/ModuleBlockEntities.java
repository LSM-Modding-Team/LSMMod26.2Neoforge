package net.nicomar2009.lsmmod.modules.tables_and_desks.registry;
import net.nicomar2009.lsmmod.registry.ModRegistries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.DeskBlockEntity;
import net.nicomar2009.lsmmod.modules.tables_and_desks.block.ElementaryDeskBlockEntity;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCK_ENTITIES;

public final class ModuleBlockEntities {
    public static final Supplier<BlockEntityType<DeskBlockEntity>> STUDENTS_DESK = BLOCK_ENTITIES.register(
            "students_desk",
            () -> new BlockEntityType<>(DeskBlockEntity::new,
                    ModuleBlocks.STUDENTS_DESK.get()));

    public static final Supplier<BlockEntityType<ElementaryDeskBlockEntity>> ELEMENTARY_DESK = BLOCK_ENTITIES.register(
            "elementary_desk",
            () -> new BlockEntityType<>(ElementaryDeskBlockEntity::new, ModuleBlocks.ELEMENTARY_DESK.get()));
    /** Initializes this module before the shared registry is bound to the event bus. */
    public static void initialize() {}

    private ModuleBlockEntities() {}
}
