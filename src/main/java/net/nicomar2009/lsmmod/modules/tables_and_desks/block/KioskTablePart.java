package net.nicomar2009.lsmmod.modules.tables_and_desks.block;

import net.minecraft.util.StringRepresentable;

/** Keep serialized left/right names so existing two-cell tables still load. */
public enum KioskTablePart implements StringRepresentable {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");
    private final String name;
    KioskTablePart(String name) {this.name=name;}
    @Override public String getSerializedName() {return name;}
}
