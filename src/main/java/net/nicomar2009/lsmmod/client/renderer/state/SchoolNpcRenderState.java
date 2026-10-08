package net.nicomar2009.lsmmod.client.renderer.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Synced roles are available for future texture selection, without adding skins now. */
public class SchoolNpcRenderState extends HumanoidRenderState {
    public boolean isTeacher;
    public boolean isStudent;
    public int classGrade;
    public boolean isFemale;
    public boolean freeHair;
    public boolean hasPortrait() {
        return isStudent && (skinColor >= 0 || eyeColor >= 0 || haircut >= 0 || glassesType >= 0 || freeHair);
    }
    public int skinColor = -1, eyeColor = -1, haircut = -1, glassesType = -1;
    public float widthScale = 0.9375F;
    public float heightScale = 0.9375F;
}
