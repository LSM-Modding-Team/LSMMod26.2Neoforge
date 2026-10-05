package net.nicomar2009.lsmmod.block;

import net.minecraft.util.StringRepresentable;

/**
 * The two halves of the students' desk.
 * LEFT is the "primary" half: it owns the block entity (inventory) and drops the item.
 * RIGHT is placed at pos.relative(facing) from the LEFT half.
 */
public enum DeskPart implements StringRepresentable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    DeskPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        // Must match the "part=" value used in the blockstate and loot table JSON
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
