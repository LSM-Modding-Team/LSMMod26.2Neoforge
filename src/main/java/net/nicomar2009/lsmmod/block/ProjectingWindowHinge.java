package net.nicomar2009.lsmmod.block;

import net.minecraft.util.StringRepresentable;

/** Local pane edges; chosen when opening and retained until the pane closes. */
public enum ProjectingWindowHinge implements StringRepresentable {
    TOP, BOTTOM, LEFT, RIGHT;
    @Override public String getSerializedName() {return name().toLowerCase(java.util.Locale.ROOT);}
}
