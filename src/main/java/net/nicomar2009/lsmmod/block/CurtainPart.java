package net.nicomar2009.lsmmod.block;

import net.minecraft.util.StringRepresentable;

/** A curtain is a bounded, directed chain stored in its glass blocks. */
public enum CurtainPart implements StringRepresentable {
    NONE("none"), SINGLE("single"), LEFT("left"), MIDDLE("middle"), RIGHT("right");
    private final String name;
    CurtainPart(String name) { this.name=name; }
    @Override public String getSerializedName() { return name; }
}
