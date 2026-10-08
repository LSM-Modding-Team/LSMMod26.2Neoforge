import bpy, json, math
from pathlib import Path
from mathutils import Vector
root=Path(__file__).resolve().parents[1]; assets=root/'src/main/resources/assets/lsmmod'
bpy.ops.object.select_all(action='SELECT'); bpy.ops.object.delete(use_global=False)
faces={'north': [0,2,6,4], 'south':[1,5,7,3], 'east':[4,6,7,5], 'west':[0,1,3,2], 'up':[2,3,7,6], 'down':[0,4,5,1]}
materials={}
def material(tex):
    if tex in materials:return materials[tex]
    mat=bpy.data.materials.new(tex);mat.use_nodes=True
    bs=mat.node_tree.nodes.get('Principled BSDF');bs.inputs['Roughness'].default_value=.42 if 'cover_' in tex else .8
    img=mat.node_tree.nodes.new('ShaderNodeTexImage');img.image=bpy.data.images.load(str(assets/('textures/'+tex.split(':')[1]+'.png')));img.interpolation='Closest'
    mat.node_tree.links.new(img.outputs['Color'],bs.inputs['Base Color']);mat.node_tree.links.new(img.outputs['Alpha'],bs.inputs['Alpha']);materials[tex]=mat;return mat
for kind,color,offset in [('folder','blue',-6.2),('notebook','red',6.2)]:
    model=json.loads((assets/f'models/item/{kind}.json').read_text()); textures=model['textures'];textures['cover']=f'lsmmod:item/stationery/cover_{color}'
    for e in model['elements']:
        a,b=e['from'],e['to']; vs=[(x-8+offset,-z+8,y-1) for x in [a[0],b[0]] for y in [a[1],b[1]] for z in [a[2],b[2]]]
        mesh=bpy.data.meshes.new(kind);active_faces={k:v for k,v in faces.items() if k in e['faces']};mesh.from_pydata(vs,[],list(active_faces.values()));mesh.update();ob=bpy.data.objects.new(kind,mesh);bpy.context.collection.objects.link(ob)
        uv=mesh.uv_layers.new()
        for poly,(name,indices) in zip(mesh.polygons,active_faces.items()):
            f=e['faces'].get(name);
            if f is None:
                poly.material_index=0
                continue
            key=f['texture'][1:];mat=material(textures[key]); ob.data.materials.append(mat);poly.material_index=len(ob.data.materials)-1
            u0,v0,u1,v1=f['uv']; coords=[(u0/16,1-v1/16),(u0/16,1-v0/16),(u1/16,1-v0/16),(u1/16,1-v1/16)]
            if name == 'south': coords=[(u0/16,1-v1/16),(u1/16,1-v1/16),(u1/16,1-v0/16),(u0/16,1-v0/16)]
            if f.get('rotation'):coords=coords[1:]+coords[:1]
            for li,coord in zip(poly.loop_indices,coords):uv.data[li].uv=coord
bpy.ops.mesh.primitive_plane_add(size=200,location=(0,0,-.15));plane=bpy.context.object
m=bpy.data.materials.new('Background');m.diffuse_color=(.12,.15,.2,1);plane.data.materials.append(m)
world=bpy.context.scene.world;world.use_nodes=True;world.node_tree.nodes['Background'].inputs[0].default_value=(.25,.28,.35,1);world.node_tree.nodes['Background'].inputs[1].default_value=.7
for loc,power,size in [((-12,15,27),2400,15),((18,0,18),1600,12)]:
    bpy.ops.object.light_add(type='AREA',location=loc);light=bpy.context.object;light.data.energy=power;light.data.shape='DISK';light.data.size=size;light.rotation_euler=(Vector((0,0,7))-light.location).to_track_quat('-Z','Y').to_euler()
bpy.ops.object.camera_add(location=(22,-45,27));cam=bpy.context.object;cam.rotation_euler=(Vector((0,0,7))-cam.location).to_track_quat('-Z','Y').to_euler();cam.data.type='ORTHO';cam.data.ortho_scale=29
s=bpy.context.scene;s.camera=cam;s.render.engine='CYCLES';s.cycles.samples=24;s.cycles.use_denoising=False;s.render.resolution_x=1300;s.render.resolution_y=850;s.render.resolution_percentage=100;s.render.image_settings.file_format='PNG';s.render.filepath=str(root/'newresources/stationery_preview.png');s.view_settings.view_transform='Standard';bpy.ops.render.render(write_still=True)
cam.location=(0,-48,7);cam.rotation_euler=(Vector((0,0,7))-cam.location).to_track_quat('-Z','Y').to_euler();s.render.filepath=str(root/'newresources/stationery_front.png');bpy.ops.render.render(write_still=True)
