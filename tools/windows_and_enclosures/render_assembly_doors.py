"""External review of active door geometry, UV/materials, rigid poses and inventory models."""
import copy,json,sys
from pathlib import Path
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
from PIL import Image,ImageDraw
from render_texture_review import render,add,load
from resource_json import preview_path
ROOT=Path(__file__).resolve().parents[2]
A=ROOT/'src/main/resources/windows_and_enclosures/assets/lsmmod'
CAT=json.loads((A/'assembly_doors.json').read_text())

def scene(ident,opened=(),mirror=False,finish=None):
 d=CAT[ident];out=[];tex={m:'lsmmod:block/assembly_door_'+m for m in ['wood','trim','glass','handle','wall','checker']}
 for s in d['surfaces']:
  e={'from':s['box'][:3],'to':s['box'][3:],'faces':copy.deepcopy(s['faces'])}
  if finish and s['paintable']:
   for face in e['faces'].values():face['texture']='#'+finish
  if s['leaf'] in opened:
   l=next(l for l in d['leaves'] if l['id']==s['leaf']);e['rotation']={'origin':list(l['pivot']),'axis':'y','angle':-90*l['turn']}
  add(out,[{'textures':tex,'elements':[e],'render_type':'minecraft:translucent' if s['material']=='glass' else 'minecraft:solid'}])
 if mirror:
  # Mirror source cubes and pivots, reverse rigid turn. Preserve local UVs as runtime does.
  for e in out:
   lo,hi=e['from'],e['to'];e['from']=[-hi[0],lo[1],lo[2]];e['to']=[-lo[0],hi[1],hi[2]]
   if 'rotation' in e:e['rotation']['origin'][0]*=-1;e['rotation']['angle']*=-1
   e['faces']={dict(east='west',west='east').get(f,f):v for f,v in e['faces'].items()}
   for f,v in e['faces'].items():
    if f in ('north','south','up','down'):v['uv']=[v['uv'][2],v['uv'][1],v['uv'][0],v['uv'][3]]
 return out

def main():
 tiles=[]
 for ident,d in CAT.items():
  leaves=[l['id'] for l in d['leaves']];states=[(),(leaves[0],)]+([(leaves[1],),tuple(leaves)] if len(leaves)==2 else [])
  for opened in states:
   for az,label in [(-70,'front'),(110,'back')]:
    tiles.append((ident+' '+(','.join(opened) or 'closed')+' '+label,render(scene(ident,opened),(d['width']*8,24,3),5,size=(360,350),azimuth=az,elevation=13)))
  item=[];add(item,load('lsmmod:item/'+ident));tiles.append((ident+' item (actual model)',render(item,(d['width']*8/3,8,2.5),14,size=(360,350),azimuth=-55,elevation=20)))
  if any(s['paintable'] for s in d['surfaces']):
   tiles.append((ident+' DEFAULT paint, fixed wall only',render(scene(ident,tuple(leaves),finish='checker'),(d['width']*8,24,3),5,size=(360,350),azimuth=45,elevation=30)))
  for turns in range(4):
   # Rotating camera by -90 matches the placement's +90 geometry rotation.
   tiles.append((ident+f' mirrored, facing turn {turns}',render(scene(ident,tuple(leaves),True),(-d['width']*8,24,3),5,size=(360,350),azimuth=-70-turns*90,elevation=18)))
 # Compare the posed source geometry with the supplied open example, using its element rotations.
 ident='school_double_windowed_assembly_door'
 reference=json.loads((Path(__file__).parent/'fixtures/assembly_doors/windowedschooldooropenexample.bbmodel').read_text())
 closed=json.loads((Path(__file__).parent/'fixtures/assembly_doors/windowedschooldoor.bbmodel').read_text())
 by_uuid={e['uuid']:s for e,s in zip(closed['elements'],CAT[ident]['surfaces'])}
 exact=[]
 for e in reference['elements']:
  s=by_uuid[e['uuid']];part={'from':[e['from'][0]+16,e['from'][1]+16,e['from'][2]],'to':[e['to'][0]+16,e['to'][1]+16,e['to'][2]],'faces':s['faces']}
  if e.get('rotation'):
   origin=e['origin'];part['rotation']={'origin':[origin[0]+16,origin[1]+16,origin[2]],'axis':'y','angle':e['rotation'][1]}
  tex={m:'lsmmod:block/assembly_door_'+m for m in ['wood','trim','glass','handle','wall','checker']}
  add(exact,[{'textures':tex,'elements':[part],'render_type':'minecraft:translucent' if s['material']=='glass' else 'minecraft:solid'}])
 tiles.append(('SUPPLIED OPEN EXAMPLE (element transforms)',render(exact,(24,24,3),5,size=(360,350),azimuth=-35,elevation=25)))
 tiles.append(('SOLVED RIGID PIVOT (no translation)',render(scene(ident,('doorright',)),(24,24,3),5,size=(360,350),azimuth=-35,elevation=25)))
 cols=4;w,h=360,380;sheet=Image.new('RGB',(cols*w,((len(tiles)+cols-1)//cols)*h),(220,220,220));draw=ImageDraw.Draw(sheet)
 for i,(label,img) in enumerate(tiles):
  x=i%cols*w;y=i//cols*h;sheet.paste(img,(x,y));draw.text((x+5,y+352),label.replace('school_','').replace('_assembly_door',''),fill=(15,15,15))
 p=preview_path('assembly_doors_review.png');sheet.save(p);print(p)
if __name__=='__main__':main()
