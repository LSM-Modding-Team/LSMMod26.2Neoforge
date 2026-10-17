package net.nicomar2009.lsmmod.modules.tables_and_desks.registry;

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
                    ModBlocks.STUDENTS_DESK.get()));

    public static final Supplier<BlockEntityType<ElementaryDeskBlockEntity>> ELEMENTARY_DESK = BLOCK_ENTITIES.register(
            "elementary_desk",
            () -> new BlockEntityType<>(ElementaryDeskBlockEntity::new, ModBlocks.ELEMENTARY_DESK.get()));
    private ModuleBlockEntities() {}
}
