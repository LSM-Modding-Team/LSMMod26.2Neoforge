"""Render final models/UV/materials offline; these previews are not Minecraft screenshots."""
import argparse
import copy
import json
import math
from pathlib import Path
import numpy as np
from PIL import Image, ImageDraw
from create_school_glass_assets import ROOT, A

VANILLA = Path('/tmp/lsm-render-assets')


def load(ident, mask=3, predicate=None):
    namespace, name = ident.split(':',1)
    path = A/f'models/{name}.json' if namespace=='lsmmod' else VANILLA/(Path(name).name+'.json')
    if namespace=='minecraft' and name=='block/block':
        return [{}]
    data = json.loads(path.read_text())
    if data.get('type')=='composite':
        result = []
        for group in data['models']:
            for entry in group:
                if isinstance(entry,str):
                    result.extend(load(entry,mask,predicate));break
                condition = entry.get('condition',{})
                selected=mask.get(condition.get('channel','main'),3) if isinstance(mask,dict) else mask
                if condition:
                    matches=predicate(condition) if predicate is not None else selected in condition.get('masks',[condition.get('mask')])
                    if not matches:continue
                result.extend(load(entry['model'],mask,predicate))
                break
        return result
    parents = load(data['parent'],mask,predicate) if 'parent' in data else [{}]
    return [dict(parent,**{k:v for k,v in data.items() if k not in ('textures','parent')},
                 textures={**parent.get('textures',{}),**data.get('textures',{})}) for parent in parents]


def add(scene, models, offset=(0,0,0)):
    for model in models:
        textures = model.get('textures',{})
        for original in model.get('elements',[]):
            e = copy.deepcopy(original)
            for key in ('from','to'):
                e[key] = [v+offset[i] for i,v in enumerate(e[key])]
            if 'rotation' in e:
                e['rotation']['origin'] = [v+offset[i] for i,v in enumerate(e['rotation']['origin'])]
            for face in e['faces'].values():
                texture = face['texture']
                while texture.startswith('#'):
                    texture = textures[texture[1:]]
                face['texture'] = texture
            e['render_type'] = model.get('render_type','minecraft:solid')
            scene.append(e)


def block(ident, state, mask=3):
    definition = json.loads((A/f'blockstates/{ident}.json').read_text())
    if 'multipart' in definition:
        applies = [p['apply'] for p in definition['multipart']
                   if all(str(state.get(k)) in v.split('|') for k,v in p.get('when',{}).items())]
    else:
        applies = [v for k,v in definition['variants'].items()
                   if all(str(state.get(a))==b for a,b in (p.split('=') for p in k.split(',') if p))]
    models = []
    for apply in applies:
        assert apply.get('y',0)==0 and apply.get('x',0)==0
        models.extend(load(apply['model'],mask))
    return models


def door(ident, opened=False, right=False, connected=False):
    scene = []
    for col in range(2):
        for row in range(3):
            for depth in range(2):
                state = {'facing':'north','column':col,'row':row,'depth':depth,
                         'open':str(opened).lower(),'hinge':'right' if right else 'left'}
                side=(3 if not connected else (1 if not right else 2))|(8 if row==1 else 16)
                main=1 if col==0 else (0 if connected else 2)
                add(scene,block(ident,state,{'main':main,'side':side}),(16*col,16*row,16*depth))
    if connected:
        add(scene,load('lsmmod:block/school_connected_tinted_glass',2),(32,32,0))
        for row in (1,2):
            neighbor=[];add(neighbor,load('lsmmod:block/school_connected_tinted_glass',2|(8 if row==1 else 16)))
            for e in neighbor:
                lo,hi=e['from'],e['to'];e['from']=[16-hi[2]+16,lo[1]+row*16,lo[0]+16];e['to']=[16-lo[2]+16,hi[1]+row*16,hi[0]+16]
                e['faces']={dict(north='east',east='south',south='west',west='north').get(f,f):v for f,v in e['faces'].items()}
                for f in ('up','down'):
                    if f in e['faces']:e['faces'][f]['rotation']=90
            scene+=neighbor
    return scene


def small_door(ident):
    scene = []
    for row,half in enumerate(('bottom','top')):
        add(scene,load(f'lsmmod:block/{ident}_{half}_left'),(0,16*row,0))
    # The actual small-door blockstate uses y=270 for facing=north.
    remap = {'west':'north','east':'south','north':'east','south':'west','up':'up','down':'down'}
    for element in scene:
        lo,hi = element['from'],element['to']
        element['from'] = [16-hi[2],lo[1],lo[0]]
        element['to'] = [16-lo[2],hi[1],hi[0]]
        element['faces'] = {remap[f]:material for f,material in element['faces'].items()}
        for face in ('up','down'):
            if face in element['faces']:
                material = element['faces'][face]
                material['rotation'] = (material.get('rotation',0)+270)%360
    return scene


def texture(ident):
    namespace, name = ident.split(':',1)
    path = A/f'textures/{name}.png' if namespace=='lsmmod' else VANILLA/(Path(name).name+'.png')
    return np.array(Image.open(path).convert('RGBA'))


def render(scene, center, scale, size=(420,560), azimuth=-70, elevation=15):
    width,height = size
    az,el = np.radians([azimuth,elevation])
    eye = np.array([math.cos(az)*math.cos(el),math.sin(el),math.sin(az)*math.cos(el)])
    right = np.array([-math.sin(az),0,math.cos(az)])
    up = np.cross(right,eye)
    # Neutral checker backdrop makes transparency and dark surfaces visible.
    yy,xx = np.indices((height,width))
    checker = ((xx//24+yy//24)%2)*8+186
    rgb = np.repeat(checker[:,:,None],3,axis=2).astype(float)
    depth = np.full((height,width),-np.inf)
    normals = {'north':(0,0,-1),'south':(0,0,1),'east':(1,0,0),'west':(-1,0,0),'up':(0,1,0),'down':(0,-1,0)}
    lighting = {'north':.8,'south':.8,'east':.6,'west':.6,'up':1.,'down':.5}
    surfaces = []
    tex_cache = {}
    for element in scene:
        x1,y1,z1 = element['from'];x2,y2,z2 = element['to']
        corners = {'north':[(x2,y2,z1),(x2,y1,z1),(x1,y1,z1),(x1,y2,z1)],
                   'south':[(x1,y2,z2),(x1,y1,z2),(x2,y1,z2),(x2,y2,z2)],
                   'east':[(x2,y2,z2),(x2,y1,z2),(x2,y1,z1),(x2,y2,z1)],
                   'west':[(x1,y2,z1),(x1,y1,z1),(x1,y1,z2),(x1,y2,z2)],
                   'up':[(x1,y2,z1),(x1,y2,z2),(x2,y2,z2),(x2,y2,z1)],
                   'down':[(x2,y1,z1),(x2,y1,z2),(x1,y1,z2),(x1,y1,z1)]}
        for face, material in element['faces'].items():
            normal = np.array(normals[face], float)
            vertices = np.array(corners[face],float)
            rotation = element.get('rotation')
            if rotation:
                origin = np.array(rotation['origin'])
                angles = {rotation['axis']:rotation['angle']} if 'axis' in rotation else {a:rotation.get(a,0) for a in ('x','y','z')}
                for axis, angle in angles.items():
                    c,s = math.cos(math.radians(angle)),math.sin(math.radians(angle))
                    matrix = {'x':[[1,0,0],[0,c,-s],[0,s,c]],'y':[[c,0,s],[0,1,0],[-s,0,c]],'z':[[c,-s,0],[s,c,0],[0,0,1]]}[axis]
                    vertices = (vertices-origin)@np.array(matrix).T+origin
                    normal = normal@np.array(matrix).T
            if normal@eye<=0:
                continue
            vertices -= center
            projected = np.column_stack((width/2+vertices@right*scale,height/2-vertices@up*scale,vertices@eye))
            u0,v0,u1,v1 = material.get('uv',[0,0,16,16])
            uv = np.array([(u0,v0),(u0,v1),(u1,v1),(u1,v0)])
            uv = np.roll(uv,material.get('rotation',0)//90,axis=0)
            ident = material['texture']
            if ident not in tex_cache:
                tex_cache[ident] = texture(ident)
            surfaces.append((projected[:,2].mean(),projected,uv,tex_cache[ident],element['render_type'],lighting[face]))
    # Depth-test opaque/cutout pixels first. Then blend translucent glass in
    # back-to-front order against that depth, avoiding painter-sort artifacts.
    ordered = sorted(surfaces,key=lambda s:s[0])
    for phase,(_,poly,uv,tex,layer,light) in ((phase,surface) for phase in (0,1) for surface in ordered):
        for tri in ((0,1,2),(0,2,3)):
            p,t = poly[list(tri)],uv[list(tri)]
            x0,y0 = np.floor(p[:,:2].min(axis=0)).astype(int)
            x1,y1 = np.ceil(p[:,:2].max(axis=0)).astype(int)
            x0,y0,x1,y1 = max(0,x0),max(0,y0),min(width-1,x1),min(height-1,y1)
            if x1<x0 or y1<y0:
                continue
            xx,yy = np.meshgrid(np.arange(x0,x1+1)+.5,np.arange(y0,y1+1)+.5)
            a,b,c = p
            den = (b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
            if abs(den)<1e-9:
                continue
            s = ((b[1]-c[1])*(xx-c[0])+(c[0]-b[0])*(yy-c[1]))/den
            q = ((c[1]-a[1])*(xx-c[0])+(a[0]-c[0])*(yy-c[1]))/den
            r = 1-s-q
            z = s*p[0,2]+q*p[1,2]+r*p[2,2]
            u = s*t[0,0]+q*t[1,0]+r*t[2,0]
            v = s*t[0,1]+q*t[1,1]+r*t[2,1]
            colors = tex[np.clip((v*tex.shape[0]/16).astype(int),0,tex.shape[0]-1),np.clip((u*tex.shape[1]/16).astype(int),0,tex.shape[1]-1)]
            mask = (s>=0)&(q>=0)&(r>=0)
            if tri==(0,2,3):
                mask &= r>1e-9
            alpha = colors[:,:,3:4]/255
            if layer=='minecraft:cutout':
                mask &= colors[:,:,3]>=26
                alpha = np.ones_like(alpha)
                if phase==1:
                    continue
            elif layer=='minecraft:solid':
                alpha = np.ones_like(alpha)
                if phase==1:
                    continue
            else:
                mask &= colors[:,:,3]==255 if phase==0 else (colors[:,:,3]>0)&(colors[:,:,3]<255)
            target = depth[y0:y1+1,x0:x1+1]
            mask &= z>=target-1e-7
            if phase==0:
                target[mask] = z[mask]
            patch = rgb[y0:y1+1,x0:x1+1]
            blended = colors[:,:,:3]*light*alpha+patch*(1-alpha)
            patch[mask] = blended[mask]
    return Image.fromarray(np.clip(rgb,0,255).astype('uint8'))


def sheet(items, filename):
    images = [render(scene,center,scale,**kwargs) for label,scene,center,scale,kwargs in items]
    height = max(im.height for im in images)+52
    canvas = Image.new('RGB',(sum(im.width for im in images),height),(225,225,225))
    draw = ImageDraw.Draw(canvas)
    x = 0
    for (label,*_),im in zip(items,images):
        canvas.paste(im,(x,32));draw.text((x+12,10),label,fill='black');x+=im.width
    draw.text((12,height-16),'Render externo de modelos y texturas finales; no es una captura de Minecraft.',fill='black')
    canvas.save(ROOT/'previews'/filename)


def main():
    (ROOT/'previews').mkdir(exist_ok=True)
    sheet([('infirmary_door / frontal',door('infirmary_door'),[16,24,8],9,{}),
           ('infirmary_door / lateral posterior',door('infirmary_door'),[16,24,8],9,{'azimuth':55}),
           ('infirmary_door / bisagra derecha',door('infirmary_door',False,True),[16,24,8],9,{'azimuth':140}),
           ('infirmary_door / abierta',door('infirmary_door',True),[16,24,8],9,{'azimuth':-55})],
          'infirmary_door_texture_review.png')
    sheet([('Transom y lateral conectados',door('infirmary_door',connected=True),[24,24,16],8,{'azimuth':55}),
           ('dining_door_wide / un solo marco',door('dining_door_wide'),[16,24,5],9,{}),
           ('dining_door_wide_light / frente claro',door('dining_door_wide_light'),[16,24,5],9,{}),
           ('dining_door_wide_light / posterior blanco',door('dining_door_wide_light'),[16,24,5],9,{'azimuth':110})],
          'door_variants_and_connections_review.png')
    inventory=[]
    for ident in ('infirmary_door','dining_door_wide_light'):
        scene=[];add(scene,load('lsmmod:item/'+ident));inventory.append((ident,scene,[8,8,8],25,{}))
    sheet(inventory,'new_door_inventory_review.png')
    items = []
    for ident in ('dining_door_wide','bathroom_door_wide'):
        items += [(ident+' / cerrada',door(ident),[16,24,5],9,{}),
                  (ident+' / abierta',door(ident,True),[16,24,10],9,{'azimuth':-55})]
    sheet(items,'wide_doors_texture_review.png')
    comparison = []
    for ident in ('dining_door','bathroom_door'):
        comparison += [(ident+' / modelo pequeno',small_door(ident),[8,20,1.5],9,{}),
                       (ident+'_wide / ampliada',door(ident+'_wide',False,True),[16,24,5],9,{})]
    sheet(comparison,'wide_doors_original_comparison.png')
    right_items = []
    for ident in ('dining_door_wide','bathroom_door_wide'):
        right_items += [(ident+' / bisagra derecha',door(ident,False,True),[16,24,5],9,{}),
                        (ident+' / derecha abierta',door(ident,True,True),[16,24,10],9,{'azimuth':110})]
    sheet(right_items,'wide_doors_right_hinge_review.png')
    sheet([(ident+' / cara posterior',door(ident),[16,24,5],9,{'azimuth':110})
           for ident in ('dining_door_wide','bathroom_door_wide')],'wide_doors_back_review.png')
    inventory = []
    for ident in ('dining_door_wide','bathroom_door_wide'):
        scene=[];add(scene,load('lsmmod:item/'+ident))
        inventory.append((ident+' / inventario',scene,[8,8,8],25,{}))
    sheet(inventory,'wide_doors_inventory_review.png')
    tinted=[]
    for x in range(3):
        for y in range(2):
            mask = (1,4,2)[x] | (8 if y==0 else 16)
            add(tinted,load('lsmmod:block/school_connected_tinted_glass',mask),(x*16,y*16,0))
    counter=[];add(counter,load('lsmmod:block/dining_room_counter'))
    sheet([('Cristal tintado conectado / 3 x 2',tinted,[24,16,4],7,{}),
           ('Cubierta gris / solo 2 px',counter,[8,15,8],19,{'elevation':30})],
          'tinted_glass_and_counter_review.png')
    statue=[]
    for x in range(2):
        for y in range(3):
            state={'facing':'north','column':x,'row':y,'expanded':'true','half':'lower'}
            add(statue,block('san_martin_de_porres',state),(x*16,y*16,0))
    sheet([('school_wood aclarada / modelo real',statue,[16,24,8],9,{})],
          'school_wood_texture_review.png')


if __name__=='__main__':
    parser = argparse.ArgumentParser();parser.add_argument('--vanilla-dir',type=Path,default=VANILLA)
    VANILLA = parser.parse_args().vanilla_dir
    main()
