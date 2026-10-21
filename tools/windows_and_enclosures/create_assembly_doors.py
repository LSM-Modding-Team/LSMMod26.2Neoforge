"""Authoritative Blockbench geometry -> Core catalog, materials and inventory models.
No build, game launch or automated checks. Runtime poses are supplied exclusively by Core.
Reviewed APIs: Core 37cec9c, PaintingMod 0c6ccb9.
"""
import json
from pathlib import Path
from PIL import Image, ImageDraw
import sys
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
from resource_json import resource_json
ROOT=Path(__file__).resolve().parents[2]
BASE=ROOT/'src/main/resources/windows_and_enclosures'
A=BASE/'assets/lsmmod'
FIX=Path(__file__).parent/'fixtures/assembly_doors'
SPECS=[('schooldoor','school_solid_assembly_door',2,'Puerta escolar con pared pintable','School door with paintable wall'),('schooldiningglassdoor','school_dining_assembly_door',2,'Puerta acristalada de comedor con pared pintable','Dining glass door with paintable wall'),('windowedschooldooralonewithwall','school_windowed_panel_assembly_door',3,'Puerta escolar con ventanilla y panel de madera','Windowed school door with wood panel'),('windowedschooldoor','school_double_windowed_assembly_door',3,'Puerta doble escolar con ventanillas','Double windowed school door')]
MATERIALS=['wood','trim','glass','handle','wall','checker']

def write(path,value):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(resource_json(path,value)+"\n",encoding="utf-8")

def faces(lo,hi,material):
    # One texel per source pixel, using a 64x64 atlas; never stretch retired door tiles.
    x,y,z=lo;X,Y,Z=hi
    return {f:{'uv':[u/4,v/4,U/4,V/4],'texture':'#'+material} for f,(u,v,U,V) in {
      'north':(64-X,48-Y,64-x,48-y),'south':(x,48-Y,X,48-y),
      'east':(z,48-Y,Z,48-y),'west':(64-Z,48-Y,64-z,48-y),
      'up':(x,z,X,Z),'down':(x,64-Z,X,64-z)}.items()}

def main():
    textures=A/'textures/block';textures.mkdir(parents=True,exist_ok=True)
    # Derive the wood palette from the retained retired-door textures, redraw grain at 1px density.
    old=Image.open(textures/'classroom_door_bottom.png').convert('RGB')
    colors=[c for n,c in old.getcolors(old.width*old.height) if 35<c[0]<200 and c[0]>c[2]*1.15]
    base=tuple(round(sum(c[i] for c in colors)/len(colors)) for i in range(3)) if colors else (120,84,48)
    for mat in MATERIALS:
      img=Image.new('RGBA',(64,64));px=img.load()
      for y in range(64):
       for x in range(64):
        noise=((x*17+y*7+x*y*3)%9)-4
        if mat=='wood':
          grain=(-9 if x%8==0 else 3 if x%8==1 else 0)+noise
          rgb=tuple(max(0,min(255,c+grain)) for c in base);alpha=255
        elif mat=='trim':rgb=(66+noise,48+noise,31+noise);alpha=255
        elif mat=='handle':rgb=(176+noise,180+noise,181+noise);alpha=255
        elif mat=='glass':rgb=(205,227,231);alpha=38 # Clean material, no vanilla glass marks.
        elif mat=='checker':rgb=(210,210,210) if (x//8+y//8)%2 else (148,148,148);alpha=255
        else:rgb=(203+noise//2,202+noise//2,193+noise//2);alpha=255
        px[x,y]=(*rgb,alpha)
      img.save(textures/f'assembly_door_{mat}.png')
    catalog={}
    for src,ident,width,es,en in SPECS:
      d=json.loads((FIX/(src+'.bbmodel')).read_text());groups={g['uuid']:g['name'] for g in d['groups']}
      membership={uuid:groups[g['uuid']] for g in d['outliner'] for uuid in g['children']}
      surfaces=[]
      for i,e in enumerate(d['elements']):
        group=membership[e['uuid']];name=e['name'];lo=[e['from'][0]+16,e['from'][1]+16,e['from'][2]];hi=[e['to'][0]+16,e['to'][1]+16,e['to'][2]]
        mat='wall' if group=='paintablewall' else 'handle' if name=='handle' else 'trim' if 'cover' in name or 'frame' in name else 'glass' if 'window' in name else 'wood'
        surfaces.append(dict(id='surface_'+e['uuid'],leaf=group if group.startswith('door') else None,paintable=group=='paintablewall',material=mat,box=lo+hi,faces=faces(lo,hi,mat)))
      # The example's pre-rotated cubes are shifted +3 X, then rotated BB +90 about (-13,15).
      # Solve T(c)=R(c-p)+p: p=(-14.5,13.5), NOT the outliner origin (-24,12).
      # Normalize X/Y by +16 for all models. Opposite hinges reflect about the leaf span midpoint.
      leaves=([dict(id='doorright',pivot=[1.5,0,13.5],turn=-1),dict(id='doorleft',pivot=[46.5,0,13.5],turn=1)] if width==3 and src=='windowedschooldoor' else [dict(id='door',pivot=[22.5 if width==2 else 30.5,0,13.5],turn=1)])
      catalog[ident]=dict(width=width,leaves=leaves,surfaces=surfaces)
      # Tiny material swatches supply baked texture/layer handles; the dynamic model replaces their quads.
      swatches=[]
      for i,mat in enumerate(MATERIALS):
        fs={k:dict(v,tintindex=i) for k,v in faces([0,0,0],[1,1,1],mat).items()}
        swatches.append({'from':[0,0,0],'to':[1,1,1],'faces':fs})
      tex={m:'lsmmod:block/assembly_door_'+m for m in MATERIALS};tex['particle']=tex['wood']
      write(A/f'models/block/{ident}.json',{'parent':'minecraft:block/block','textures':tex,'elements':swatches,'render_type':'minecraft:translucent'})
      write(A/f'blockstates/{ident}.json',{'variants':{'':{'model':'lsmmod:block/'+ident}}})
      # Fit the complete assembled geometry into one inventory model without changing its proportions.
      elements=[]
      for s in surfaces:
        lo=s['box'][:3];hi=s['box'][3:];scale=1/3
        elements.append({'from':[v*scale for v in lo],'to':[v*scale for v in hi],'faces':s['faces']})
      write(A/f'models/item/{ident}.json',{'parent':'minecraft:block/block','textures':tex,'elements':elements,'render_type':'minecraft:translucent','display':{'gui':{'rotation':[20,145,0],'translation':[0,0,0],'scale':[.85,.85,.85]},'ground':{'scale':[.35,.35,.35]},'fixed':{'rotation':[0,180,0],'scale':[.75,.75,.75]}}})
      write(A/f'items/{ident}.json',{'model':{'type':'minecraft:model','model':'lsmmod:item/'+ident}})
      # Core emits the sole item after removal; no carrier may emit a vanilla per-cell drop.
      write(BASE/f'data/lsmmod/loot_table/blocks/{ident}.json',{'type':'minecraft:block','pools':[]})
      for lang,text in [('es_es',es),('en_us',en)]:
        p=A/f'lang/{lang}.json';data=json.loads(p.read_text());data['block.lsmmod.'+ident]=text;write(p,data)
    write(A/'assembly_doors.json',catalog)
    for tag in ['mineable/axe','doors']:
      p=BASE/f'data/minecraft/tags/block/{tag}.json';data=json.loads(p.read_text()) if p.exists() else {'replace':False,'values':[]}
      data['values']=list(dict.fromkeys(data['values']+['lsmmod:'+s[1] for s in SPECS]));write(p,data)
if __name__=='__main__':main()
