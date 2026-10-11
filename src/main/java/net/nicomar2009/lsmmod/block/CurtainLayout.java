package net.nicomar2009.lsmmod.block;

import net.minecraft.util.StringRepresentable;

/** Only the seventeen valid curtain combinations, instead of a 5 x 5 Cartesian product. */
public enum CurtainLayout implements StringRepresentable {
    NONE(CurtainPart.NONE,CurtainPart.SINGLE),
    SINGLE_SINGLE(CurtainPart.SINGLE,CurtainPart.SINGLE),
    SINGLE_LEFT(CurtainPart.SINGLE,CurtainPart.LEFT),
    SINGLE_MIDDLE(CurtainPart.SINGLE,CurtainPart.MIDDLE),
    SINGLE_RIGHT(CurtainPart.SINGLE,CurtainPart.RIGHT),
    LEFT_SINGLE(CurtainPart.LEFT,CurtainPart.SINGLE),
    LEFT_LEFT(CurtainPart.LEFT,CurtainPart.LEFT),
    LEFT_MIDDLE(CurtainPart.LEFT,CurtainPart.MIDDLE),
    LEFT_RIGHT(CurtainPart.LEFT,CurtainPart.RIGHT),
    MIDDLE_SINGLE(CurtainPart.MIDDLE,CurtainPart.SINGLE),
    MIDDLE_LEFT(CurtainPart.MIDDLE,CurtainPart.LEFT),
    MIDDLE_MIDDLE(CurtainPart.MIDDLE,CurtainPart.MIDDLE),
    MIDDLE_RIGHT(CurtainPart.MIDDLE,CurtainPart.RIGHT),
    RIGHT_SINGLE(CurtainPart.RIGHT,CurtainPart.SINGLE),
    RIGHT_LEFT(CurtainPart.RIGHT,CurtainPart.LEFT),
    RIGHT_MIDDLE(CurtainPart.RIGHT,CurtainPart.MIDDLE),
    RIGHT_RIGHT(CurtainPart.RIGHT,CurtainPart.RIGHT);
    private final CurtainPart horizontal, vertical;
    CurtainLayout(CurtainPart horizontal,CurtainPart vertical) {this.horizontal=horizontal;this.vertical=vertical;}
    public CurtainPart horizontal() {return horizontal;}
    public CurtainPart vertical() {return vertical;}
    public static CurtainLayout of(CurtainPart horizontal,CurtainPart vertical) {
        if(horizontal==CurtainPart.NONE)return NONE;
        if(vertical==CurtainPart.NONE)vertical=CurtainPart.SINGLE;
        for(CurtainLayout layout:values())if(layout.horizontal==horizontal&&layout.vertical==vertical)return layout;
        return NONE;
    }
    @Override public String getSerializedName() {return name().toLowerCase(java.util.Locale.ROOT);}
}
