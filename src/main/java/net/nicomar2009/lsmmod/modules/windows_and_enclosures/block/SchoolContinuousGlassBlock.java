package net.nicomar2009.lsmmod.modules.windows_and_enclosures.block;

/** Full-height school glazing with perimeter-only frames and local, unbounded joins. */
public final class SchoolContinuousGlassBlock extends SchoolGlassBlock {
    @Override public boolean continuousConnections() { return true; }

    public SchoolContinuousGlassBlock(Properties properties) {
        super(properties, new double[][]{{0, 0, 0, 16, 16, 8}});
    }
}
