package net.nicomar2009.lsmmod.modules.facilities.block;
import net.nicomar2009.lsmmod.block.SharedChairRotations;
import net.nicomar2009.lsmmod.block.ExactVoxelShapes;

/** Extended cot reuses the original angled headrest and shares direct grid rotations. */
public final class InfirmaryCotExtendedShapes {
 public static final net.minecraft.world.phys.shapes.VoxelShape[][] SHAPES=new net.minecraft.world.phys.shapes.VoxelShape[3][];
 static {
  SHAPES[0]=InfirmaryCotShapes.SHAPES[0];
  SHAPES[1]=SharedChairRotations.horizontal(ExactVoxelShapes.pixels(new double[][] {{1.0, 8.5, 0.0, 15.0, 10.5, 16}, {1.5, 10.5, 0.0, 14.5, 11.0, 16}, {2.0, 7.5, 0.0, 3.5, 8.5, 16}, {2.0, 3.0, 0.0, 3.5, 4.0, 16}, {12.5, 7.5, 0.0, 14.0, 8.5, 16}, {12.5, 3.0, 0.0, 14.0, 4.0, 16}}));
  SHAPES[2]=SharedChairRotations.horizontal(ExactVoxelShapes.pixels(new double[][] {{1.0, 8.5, 0, 15.0, 10.5, 14.0}, {1.5, 10.5, 0, 14.5, 11.0, 13.0}, {2.0, 0.5, 9.0, 3.5, 8.5, 12.0}, {2.0, 0.0, 9.0, 3.5, 0.5, 12.0}, {2.0, 7.5, 0, 3.5, 8.5, 12.0}, {2.0, 3.0, 0, 3.5, 4.0, 9.0}, {12.5, 0.5, 9.0, 14.0, 8.5, 12.0}, {12.5, 0.0, 9.0, 14.0, 0.5, 12.0}, {12.5, 7.5, 0, 14.0, 8.5, 12.0}, {12.5, 3.0, 0, 14.0, 4.0, 9.0}, {3.5, 7.5, 9.0, 12.5, 8.5, 12.0}, {3.5, 3.0, 9.0, 12.5, 4.0, 12.0}}));
 }
 private InfirmaryCotExtendedShapes() {}
}
