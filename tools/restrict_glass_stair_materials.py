"""Repair existing stair models without rebuilding connections, curtains or block states."""
import json
from create_school_glass_assets import A,write
from create_school_glass_stair_assets import restrict_lower_material

LAYOUTS={
    'supported_school_glass': ('light_wall','support'),
    'mixed_school_glass': ('concrete','light_wall'),
    'supported_school_glass_reversed': ('support','light_wall'),
    'mixed_school_glass_reversed': ('light_wall','concrete'),
}


def family(stem):
    return next((name for name in sorted(LAYOUTS,key=len,reverse=True) if stem==name or stem.startswith(name+'_')),None)


def main():
    models=faces=0
    for path in (A/'models/block').glob('*.json'):
        name=family(path.stem)
        if not name: continue
        model=json.loads(path.read_text())
        changed=restrict_lower_material(model.get('elements',[]),*LAYOUTS[name])
        if changed:
            write(path,model);models+=1;faces+=changed
    print(f'Updated {models} glass stair models / {faces} lower faces; geometry, UVs, joins and states unchanged.')


if __name__=='__main__':main()
