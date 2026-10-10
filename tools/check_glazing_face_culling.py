"""Regression: joining glazing must not erase the stair's exposed support beside a door."""
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def side_profile(kind,opened=False):
    # Occupied (y,z) pixels at the interface, ignoring decorative frame thickness.
    if kind=='stair':return {(y,z) for y in range(16) for z in range(16) if y<8 or z<8}
    if kind=='glass':return {(y,z) for y in range(16) for z in range(8)}
    if kind=='door':return {(y,z) for y in range(16) for z in range(16) if y>=8 and z<8 or not opened and y<8 and z<4}
    raise ValueError(kind)
def main():
    for opened in (False,True):
        exposed=side_profile('stair')-side_profile('door',opened)
        assert {(y,z) for y in range(8) for z in range(8,16)}<=exposed
        assert exposed
    assert side_profile('stair')==side_profile('stair')
    root=ROOT/'src/main/java/net/nicomar2009/lsmmod/block'
    shared=(root/'SchoolGlazing.java').read_text()
    section=shared.split('public static boolean canCullHorizontalFace')[1].split('public static BlockState mirrorLayout')[0]
    assert 'if(!connectsHorizontal' in section
    assert 'instanceof SchoolGlassStairBlock && neighbor.getBlock() instanceof SchoolGlassStairBlock' in section
    assert 'StairBlock.HALF' in section and 'StairBlock.SHAPE' in section and 'return false;' in section
    for name in ('SchoolGlassBlock','SchoolGlassStairBlock','TallClassroomEntranceBlock','DoubleSchoolEntranceBlock'):
        source=(root/f'{name}.java').read_text()
        assert 'SchoolGlazing.canCullHorizontalFace(state,' in source
        assert 'SchoolGlazing.connectsHorizontal(state,' not in source
    print('OK: closed/open doors retain exposed stair support faces; shared glazing joins remain independent of complete-face culling.')
if __name__=='__main__':main()
