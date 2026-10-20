package net.nicomar2009.lsmmod.modules.painting_and_editable_blocks.block;

/** Editable school stair base with unlimited, perimeter-only glazing connections. */
public final class SchoolPaintableContinuousGlassStairBlock extends SchoolPaintableGlassStairBlock {
    public SchoolPaintableContinuousGlassStairBlock(Properties properties) {super(properties);}
    @Override public boolean continuousConnections() {return true;}
}
