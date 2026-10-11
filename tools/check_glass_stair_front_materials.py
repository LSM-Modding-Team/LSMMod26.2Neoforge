"""Regression for front-only lower materials across all stairs, corners and join models."""
import json
from create_school_glass_assets import A
from create_school_glass_stair_assets import QUARTERS
from restrict_glass_stair_materials import LAYOUTS,family


def read(path): return json.loads(path.read_text())


def main():
    count=0
    for path in (A/'models/block').glob('*.json'):
        name=family(path.stem)
        if not name:continue
        model=read(path);under,side=LAYOUTS[name]
        for e in model.get('elements',[]):
            for face,surface in e['faces'].items():
                material=surface['texture']
                if material=='#'+under:
                    assert face=='north' and e['from'][2]==0,(path.name,face,e)
                if material not in ('#glass','#frame'):
                    assert material in ('#'+under,'#'+side),(path.name,material)
        count+=1
    for name,(under,side) in LAYOUTS.items():
        for half in ('bottom','top'):
            for shape,quarters in QUARTERS.items():
                expected_width=8*sum(z==0 for x,z in quarters)
                for top in (0,1):
                    for bottom in (0,1):
                        paths=[A/f'models/block/{name}_{half}_{shape}_{top}_{bottom}.json']
                        if shape=='straight':paths += [A/f'models/block/{name}_{half}_{shape}_{top}_{bottom}_horizontal_{layout}.json' for layout in range(8) if layout!=3]
                        for path in paths:
                            model=read(path);rectangles=[]
                            for e in model['elements']:
                                for face,surface in e['faces'].items():
                                    if surface['texture']=='#'+under:
                                        assert (e['from'][1],e['to'][1])==((0,8) if half=='bottom' else (8,16))
                                        rectangles.append((e['from'][0],e['to'][0]))
                            assert sum(high-low for low,high in rectangles)==expected_width,(path.name,rectangles)
                            for x in range(16):
                                hits=sum(lo<=x+.5<hi for lo,hi in rectangles)
                                assert hits==int((x//8,0) in quarters),(path.name,x,hits)
    print(f'OK: {count} models; lower contrasting material only on the front rectangle, correct half-height and corner widths, all horizontal/vertical joins.')


if __name__=='__main__':main()
