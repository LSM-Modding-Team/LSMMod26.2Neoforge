"""Render Minecraft model faces with per-pixel depth, including intersecting parts."""
import json
import math
from pathlib import Path
import numpy as np
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
FACES = {'west': [0,1,3,2], 'east': [4,6,7,5],
         'down': [0,4,5,1], 'up': [2,3,7,6],
         'north': [0,2,6,4], 'south': [1,5,7,3]}

def render(name, width=800, height=900, center=(8,8,8), scale=35, elevation=23, azimuth=55, base_only=False):
    rgb = np.full((height,width,3), 250, dtype=np.uint8)
    depth = np.full((height,width), -np.inf)
    az, el = np.radians([azimuth,elevation])
    eye = np.array([math.cos(az)*math.cos(el), math.sin(el), math.sin(az)*math.cos(el)])
    right = np.array([math.sin(az),0,-math.cos(az)])
    up = np.cross(eye,right)
    light = np.array([.3,.85,.43]);light /= np.linalg.norm(light)
    model = json.loads((ROOT/f'src/main/resources/assets/lsmmod/models/block/{name}.json').read_text())
    for element in model['elements']:
        if base_only and element['from'][1] >= 14:
            continue
        lo, hi = element['from'][:],element['to'][:]
        if base_only: hi[1] = min(hi[1],5)
        vertices = np.array([[x,y,z] for x in [lo[0],hi[0]] for y in [lo[1],hi[1]] for z in [lo[2],hi[2]]],float)
        rotation = element.get('rotation')
        if rotation:
            angle = math.radians(rotation['angle']); c,s = math.cos(angle),math.sin(angle)
            matrix = np.array([[1,0,0],[0,c,-s],[0,s,c]]) if rotation['axis']=='x' else np.array([[c,0,s],[0,1,0],[-s,0,c]])
            origin = np.array(rotation['origin'])
            vertices = (vertices-origin) @ matrix.T + origin
        relative = vertices-np.array(center)
        projected = np.column_stack((width/2+relative@right*scale,height/2-relative@up*scale,relative@eye))
        for direction, indices in FACES.items():
            face = element['faces'].get(direction)
            if not face:
                continue
            normal = np.zeros(3); normal[['x','y','z'].index({'west':'x','east':'x','up':'y','down':'y','north':'z','south':'z'}[direction])] = -1 if direction in ['west','down','north'] else 1
            if rotation: normal = matrix@normal
            if normal@eye <= 0: continue
            value = 37 if face['texture']=='#feet' else 244 if face['uv'][0]==1 else 209
            color = np.clip(value*(.68+.32*max(0,normal@light)),0,255).astype(np.uint8)
            polygon = projected[indices]
            for ids in [(0,1,2),(0,2,3)]:
                triangle = polygon[list(ids)]
                x0,y0 = np.floor(triangle[:,:2].min(axis=0)).astype(int)
                x1,y1 = np.ceil(triangle[:,:2].max(axis=0)).astype(int)
                x0,y0=max(0,x0),max(0,y0);x1,y1=min(width-1,x1),min(height-1,y1)
                if x1<x0 or y1<y0: continue
                xx,yy = np.meshgrid(np.arange(x0,x1+1)+.5,np.arange(y0,y1+1)+.5)
                a,b,c = triangle
                denominator = (b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
                if abs(denominator)<1e-10: continue
                u=((b[1]-c[1])*(xx-c[0])+(c[0]-b[0])*(yy-c[1]))/denominator
                v=((c[1]-a[1])*(xx-c[0])+(a[0]-c[0])*(yy-c[1]))/denominator
                w=1-u-v
                z=u*a[2]+v*b[2]+w*c[2]
                target=depth[y0:y1+1,x0:x1+1]
                mask=(u>=-1e-9)&(v>=-1e-9)&(w>=-1e-9)&(z>target)
                target[mask]=z[mask]
                rgb[y0:y1+1,x0:x1+1][mask]=color
    return rgb

if __name__=='__main__':
    furniture = np.concatenate([render('dining_table'),render('dining_chair')],axis=1)
    Image.fromarray(furniture).save(ROOT/'newresources/dining_reference_render.png')
    closeups = np.concatenate([render('dining_table',height=700,center=(8,2.5,8),scale=45,elevation=28,base_only=True),
                              render('dining_table',height=700,center=(8,1,8),scale=45,elevation=90,azimuth=0,base_only=True)],axis=1)
    Image.fromarray(closeups).save(ROOT/'newresources/dining_table_base_render.png')
