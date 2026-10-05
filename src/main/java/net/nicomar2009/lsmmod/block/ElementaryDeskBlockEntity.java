package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Inventory of the elementary desk: 9 slots (3x3, same layout as a dispenser) and nothing else. */
public class ElementaryDeskBlockEntity extends RandomizableContainerBlockEntity {
    private static final int CONTAINER_SIZE = 9;

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public ElementaryDeskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEMENTARY_DESK.get(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        // If the desk still has an unopened loot table, that is saved instead of the items
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.lsmmod.elementary_desk");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        // The only new Minecraft API of this chunk: the vanilla 3x3 menu (dispenser/dropper screen)
        return new DispenserMenu(containerId, inventory, this);
    }
}
