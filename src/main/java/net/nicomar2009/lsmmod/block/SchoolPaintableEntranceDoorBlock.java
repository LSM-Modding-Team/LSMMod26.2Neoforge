package net.nicomar2009.lsmmod.block;

/** A school entrance leaf with the shared 8px paintable side and a fixed glazed transom. */
public class SchoolPaintableEntranceDoorBlock extends SchoolPaintableBathroomDoorBlock {
    public SchoolPaintableEntranceDoorBlock(Properties properties) {super(properties);}
    @Override public boolean hasTransomGlazing() {return true;}
}
