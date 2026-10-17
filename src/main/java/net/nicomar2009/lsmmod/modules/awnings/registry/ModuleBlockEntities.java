package net.nicomar2009.lsmmod.modules.awnings.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nicomar2009.lsmmod.modules.awnings.block.AwningBlockEntity;
import java.util.function.Supplier;
import net.nicomar2009.lsmmod.registry.*;
import static net.nicomar2009.lsmmod.registry.ModRegistries.BLOCK_ENTITIES;

public final class ModuleBlockEntities {
    public static final Supplier<BlockEntityType<AwningBlockEntity>> AWNING = BLOCK_ENTITIES.register(
            "awning", () -> new BlockEntityType<>(AwningBlockEntity::new,
                    ModBlocks.awningEntityBlocks()));
    private ModuleBlockEntities() {}
}
