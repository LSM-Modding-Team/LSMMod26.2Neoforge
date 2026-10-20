"""Append missing stable paint IDs to existing native Fusion face libraries without regenerating geometry."""

# Allow direct execution while keeping cross-module imports explicit.
import sys as _sys
from pathlib import Path as _Path
_tools = str(_Path(__file__).resolve().parents[1])
if _tools not in _sys.path:
    _sys.path.insert(0, _tools)

from resource_modules import module_read_text, module_rglob
import copy,json,re
from windows_and_enclosures.create_school_glass_assets import A,write
from painting_and_editable_blocks.create_paintable_school_block import PAINTS


def colored_name(ident,old,new):
    matches=list(re.finditer(r'_'+str(old)+r'(?=_|$)',ident))
    if not matches:raise ValueError('Paint model has no color suffix: '+ident)
    match=matches[-1];return ident[:match.start()]+'_'+str(new)+ident[match.end():]


def main():
    assert len(PAINTS)<=16,'Saved palettes reserve four bits per paint'
    cached={};added=0
    def clone(ident,old,new):
        key=(ident,old,new)
        if key in cached:return cached[key]
        target=colored_name(ident,old,new);cached[key]=target
        path=A/f'models/{ident.split(":")[1]}.json';data=copy.deepcopy(json.loads(module_read_text(path)))
        old_texture,new_texture=PAINTS[old],PAINTS[new]
        old_alias='#column_'+old_texture.replace(':','_').replace('/','_')
        for name,value in list(data.get('textures',{}).items()):
            if value==old_texture:data['textures'][name]=new_texture
            elif value==old_alias:data['textures'][name]='#column_'+new_texture.replace(':','_').replace('/','_')
        parent=data.get('parent')
        if parent and parent.startswith('lsmmod:') and re.search(r'_'+str(old)+r'(?=_|$)',parent):data['parent']=clone(parent,old,new)
        write(A/f'models/{target.split(":")[1]}.json',data)
        return target
    for path in list(module_rglob(A/'models/block', '*.json')):
        data=json.loads(module_read_text(path));changed=False
        for group in data.get('models',[]):
            entries=[e for e in group if isinstance(e,dict) and isinstance(e.get('condition',{}).get('paint'),int) and e['condition']['paint']>=0]
            if not entries:continue
            colors={e['condition']['paint'] for e in entries};old=max(colors)
            if old==0:continue  # Structural column/base predicates use zero, not a material palette.
            templates=[e for e in entries if e['condition']['paint']==old]
            additions=[]
            for new in range(1,len(PAINTS)):
                if new in colors:continue
                for original in templates:
                    entry=copy.deepcopy(original);entry['condition']['paint']=new
                    entry['model']=clone(original['model'],old,new);additions.append(entry);added+=1
            if additions:
                # Preserve all original ordering and any unconditional fallback as the last choice.
                index=next((i for i,e in enumerate(group) if isinstance(e,str) or 'condition' not in e),len(group))
                group[index:index]=additions;changed=True
        if any(isinstance(e,dict) and e.get('condition',{}).get('type')=='lsmmod:column_part' and e['condition'].get('paint',-1)>0 for group in data.get('models',[]) for e in group):
            for texture in PAINTS:
                alias='column_'+texture.replace(':','_').replace('/','_')
                if data.setdefault('textures',{}).get(alias)!=texture:data['textures'][alias]=texture;changed=True
        if changed:write(path,data)
    print(f'Extended palette: {added} face selections, {len(cached)} native material models.')

if __name__=='__main__':main()
