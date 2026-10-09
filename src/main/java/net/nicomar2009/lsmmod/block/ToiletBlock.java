package net.nicomar2009.lsmmod.block;

/** Two-block-high floor cubicle with a passive toilet and an integrated operable door. */
public class ToiletBlock extends TallBathroomBlock {
    public ToiletBlock(Properties properties) {
        super(properties, TallBathroomShapes.TOILET_CLOSED, TallBathroomShapes.TOILET_OPEN, false);
    }
}
