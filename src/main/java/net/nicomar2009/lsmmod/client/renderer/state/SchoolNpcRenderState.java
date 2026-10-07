package net.nicomar2009.lsmmod.client.renderer.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Synced roles are available for future texture selection, without adding skins now. */
public class SchoolNpcRenderState extends HumanoidRenderState {
    public boolean isTeacher;
    public boolean isStudent;
    public int classGrade;
    public boolean isFemale;
}
