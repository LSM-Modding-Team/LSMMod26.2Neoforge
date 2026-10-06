package net.nicomar2009.lsmmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

/** Persistent ownership; rendering is entirely determined by block states. */
public class AwningBlockEntity extends BlockEntity {
    private BlockPos supportRoot;
    private BlockPos first;
    private BlockPos second;
    private int width = 4; // Old four-wide structures have no saved width.
    private String variant = "awning";
    public AwningBlock awning() { return net.nicomar2009.lsmmod.registry.ModBlocks.awningVariant(variant); }
    boolean removalHandled;

    public AwningBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AWNING.get(), pos, state);
        supportRoot = pos;
        if (state.getBlock() instanceof AwningBlock cloth) variant = cloth.variant();
    }

    public int width() { return width; }
    public BlockPos supportRoot() { return supportRoot; }
    public BlockPos first() { return first; }
    public BlockPos second() { return second; }
    public boolean linked() { return first != null && second != null; }
    public void setSupportRoot(BlockPos root, int width) { this.width = Math.clamp(width, 1, 16); supportRoot = root.immutable(); setChanged(); }
    public void link(BlockPos a, BlockPos b, int width, AwningBlock awning) { this.variant = awning.variant(); this.width = Math.clamp(width, 1, 16); first = a.immutable(); second = b.immutable(); setChanged(); }
    public void unlink() { first = null; second = null; setChanged(); }
    public boolean belongsTo(BlockPos a, BlockPos b) { return linked() && first.equals(a) && second.equals(b); }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("awning_variant", variant);
        output.putInt("width", width);
        output.putLong("support_root", supportRoot.asLong());
        output.putBoolean("linked", linked());
        if (linked()) {
            output.putLong("first", first.asLong());
            output.putLong("second", second.asLong());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        variant = input.getStringOr("awning_variant", "awning");
        width = Math.clamp(input.getIntOr("width", 4), 1, 16);
        supportRoot = BlockPos.of(input.getLongOr("support_root", worldPosition.asLong()));
        if (input.getBooleanOr("linked", false)) {
            first = BlockPos.of(input.getLongOr("first", worldPosition.asLong()));
            second = BlockPos.of(input.getLongOr("second", worldPosition.asLong()));
        } else { first = null; second = null; }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide()) AwningStructure.removed(level, this, true);
        super.preRemoveSideEffects(pos, state);
    }
}
