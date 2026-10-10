"""Tall decorative door variants inherit the complete classroom geometry and glazing."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
A=ROOT/'src/main/resources/assets/lsmmod'
VARIANTS=('bathroom_door','teachers_office_door','dining_door')
def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(value,indent=2,ensure_ascii=False)+'\n')
def main():
    source=json.loads((A/'blockstates/tall_classroom_entrance.json').read_text())
    for base in VARIANTS:
        name='tall_'+base
        textures={part:f'lsmmod:block/{base}_{part}' for part in ('bottom','top')}
        states=json.loads(json.dumps(source))
        for value in states['variants'].values():
            original=value['model']
            stem=original.split(':')[1].removeprefix('block/')
            target=name+'_'+stem.removeprefix('tall_classroom_')
            write(A/f'models/block/{target}.json',{'parent':original,'render_type':'minecraft:translucent','textures':textures})
            value['model']='lsmmod:block/'+target
        write(A/f'blockstates/{name}.json',states)
        write(A/f'models/item/{name}.json',{'parent':'lsmmod:item/tall_classroom_entrance','textures':textures})
        write(A/f'items/{name}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+name}})
        loot=json.loads((ROOT/'src/main/resources/data/lsmmod/loot_table/blocks/tall_classroom_entrance.json').read_text())
        loot=json.loads(json.dumps(loot).replace('lsmmod:tall_classroom_entrance','lsmmod:'+name))
        write(ROOT/f'src/main/resources/data/lsmmod/loot_table/blocks/{name}.json',loot)
    for locale,labels in [('es_es',('Puerta alta de baño','Puerta alta de sala de profesores','Puerta alta de comedor')),('en_us',('Tall Bathroom Door','Tall Teachers Office Door','Tall Dining Door'))]:
        path=A/f'lang/{locale}.json';data=json.loads(path.read_text())
        data.update({'block.lsmmod.tall_'+base:label for base,label in zip(VARIANTS,labels)});write(path,data)
    path=ROOT/'src/main/resources/data/minecraft/tags/block/mineable/axe.json';data=json.loads(path.read_text())
    data['values']=list(dict.fromkeys(data['values']+['lsmmod:tall_'+base for base in VARIANTS]));write(path,data)
if __name__=='__main__':main()
