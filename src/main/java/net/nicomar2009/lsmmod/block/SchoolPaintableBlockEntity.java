package net.nicomar2009.lsmmod.block;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nicomar2009.lsmmod.registry.ModBlockEntities;

public class SchoolPaintableBlockEntity extends BlockEntity {
    private final int[] paints = new int[6];
    public SchoolPaintableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCHOOL_PAINTABLE.get(), pos, state);
    }
    public SchoolPaint paint(Direction face) { return SchoolPaint.fromId(paints[face.ordinal()]); }
    public int[] snapshot() { return paints.clone(); }
    public boolean painted() { for (int paint : paints) if (paint != 0) return true; return false; }
    public void paintFace(Direction face, SchoolPaint paint) {
        int[] copy = snapshot(); copy[face.ordinal()] = paint.ordinal(); apply(copy);
    }
    public void clean() { apply(new int[6]); }
    public void apply(int[] colors) {
        if (colors.length != 6) throw new IllegalArgumentException("Expected six face colors");
        int[] validated = Arrays.stream(colors).map(id -> SchoolPaint.fromId(id).ordinal()).toArray();
        if (Arrays.equals(paints, validated)) return;
        System.arraycopy(validated, 0, paints, 0, 6);
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (Direction face : Direction.values()) output.putInt("paint_" + face.getSerializedName(), paints[face.ordinal()]);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (Direction face : Direction.values())
            paints[face.ordinal()] = SchoolPaint.fromId(input.getIntOr("paint_" + face.getSerializedName(), 0)).ordinal();
        // A BE data packet arrives after the block update; invalidate client geometry again.
        if (level != null && level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
