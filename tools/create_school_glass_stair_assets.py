"""Real vanilla stair geometry with material regions matching the school reference."""
import json
from PIL import Image,ImageDraw
from create_school_glass_assets import A,T,write,model,ROOT,item_and_loot,D
from create_bathroom_statue_assets import solid_model

QUARTERS={'straight':[(0,0),(1,0)],'outer_left':[(0,0)],'outer_right':[(1,0)],
          'inner_left':[(0,0),(1,0),(0,1)],'inner_right':[(0,0),(1,0),(1,1)]}

def main():
    for top in (False,True):
        for bottom in (False,True):
            im=Image.open(A/'textures/block/school_glass.png').convert('RGBA');draw=ImageDraw.Draw(im)
            brown=(58,43,35,255)
            draw.line((0,0,0,15),fill=brown);draw.line((15,0,15,15),fill=brown)
            if not top:draw.line((0,0,15,0),fill=brown)
            if not bottom:draw.line((0,15,15,15),fill=brown)
            im.save(A/f'textures/block/school_stair_glass_{int(top)}_{int(bottom)}.png')
    for name,under,side in [('supported_school_glass','light_wall','support'),('mixed_school_glass','concrete','light_wall'),
                            ('supported_school_glass_reversed','support','light_wall'),('mixed_school_glass_reversed','light_wall','concrete')]:
        write(A/f'models/item/{name}.json',{'parent':f'lsmmod:block/{name}'})
        item_and_loot(name)
        if name.endswith('_reversed'):
            for locale,label in [('es_es','Cristal con materiales inferiores invertidos: '+('paredes' if name.startswith('supported') else 'muro y concreto')),('en_us','School Glass Stairs with Swapped Lower Materials: '+('Walls' if name.startswith('supported') else 'Wall and Concrete'))]:
                p=A/f'lang/{locale}.json';v=json.loads(p.read_text());v['block.lsmmod.'+name]=label;write(p,v)
            p=D/'minecraft/tags/block/mineable/pickaxe.json';v=json.loads(p.read_text())
            if 'lsmmod:'+name not in v['values']:v['values'].append('lsmmod:'+name)
            write(p,v)
        variants={}
        for half in ('bottom','top'):
            for shape,high in QUARTERS.items():
                for connected_top in (False,True):
                    for connected_bottom in (False,True):
                        parts=[]
                        for x,z in [(0,0),(1,0),(0,1),(1,1)]:
                            parts.append(([8*x,0,8*z,8*x+8,8,8*z+8],under if (x,z) in high else side))
                        # Physical side posts match standalone glass, with no texture-painted seams.
                        lower_join=connected_bottom if half=="bottom" else connected_top
                        upper_join=connected_top if half=="bottom" else connected_bottom
                        low=8 if lower_join else 9
                        high_y=16 if upper_join else 15
                        for zrow in (0,1):
                            xs=sorted(x for x,z in high if z==zrow)
                            if not xs:continue
                            lo_x,hi_x=8*min(xs),8*(max(xs)+1)
                            parts.extend([([lo_x+1,low,8*zrow,hi_x-1,high_y,8*zrow+8],'glass'),
                                          ([lo_x,8,8*zrow,lo_x+1,16,8*zrow+8],'frame'),
                                          ([hi_x-1,8,8*zrow,hi_x,16,8*zrow+8],'frame')])
                            if not lower_join:parts.append(([lo_x+1,8,8*zrow,hi_x-1,9,8*zrow+8],'frame'))
                            if not upper_join:parts.append(([lo_x+1,15,8*zrow,hi_x-1,16,8*zrow+8],'frame'))
                        if half=='top':parts=[([b[0],16-b[4],b[2],b[3],16-b[1],b[5]],m) for b,m in parts]
                        # Glass transmits light: it must not erase the inside faces of its frame.
                        # Clip glass against all solids, but clip opaque faces against opaque parts only.
                        opaque=solid_model([part for part in parts if part[1]!='glass'])['elements']
                        glass=[e for e in solid_model(parts)['elements']
                               if next(iter(e['faces'].values()))['texture']=='#glass']
                        elements=opaque+glass
                        for e in elements:
                            lo,hi=e['from'],e['to']
                            for face,f in e['faces'].items():
                                material=f['texture'][1:]
                                if face in ('north','south'):
                                    f['uv']=[hi[0],16-hi[1],lo[0],16-lo[1]]
                                elif face in ('west','east'):
                                    f['uv']=[lo[2],16-hi[1],hi[2],16-lo[1]]
                                else:
                                    f['uv']=[lo[0],lo[2],hi[0],hi[2]]
                                if material in ('glass','frame'):
                                    # Vanilla face orientation; keep grain/reflections consistent on both sides.
                                    if face=='north':f['uv']=[16-hi[0],16-hi[1],16-lo[0],16-lo[1]]
                                    elif face=='south':f['uv']=[lo[0],16-hi[1],hi[0],16-lo[1]]
                                    elif face=='east':f['uv']=[16-hi[2],16-hi[1],16-lo[2],16-lo[1]]
                                axis={'west':0,'east':0,'down':1,'up':1,'north':2,'south':2}[face]
                                positive=face in ('east','up','south')
                                if (hi if positive else lo)[axis]==(16 if positive else 0):f['cullface']=face
                        textures=dict(T)
                        suffix=f'{name}_{half}_{shape}_{int(connected_top)}_{int(connected_bottom)}'
                        result=model(elements);result['textures']=textures
                        write(A/f'models/block/{suffix}.json',result)
                        if half=='bottom' and shape=='straight' and not connected_top and not connected_bottom:
                            write(A/f'models/block/{name}.json',result)
                        for facing,angle in [('north',0),('east',90),('south',180),('west',270)]:
                            for water in (False,True):
                                variants[f'bottom_connected={str(connected_bottom).lower()},facing={facing},half={half},shape={shape},top_connected={str(connected_top).lower()},waterlogged={str(water).lower()}']={'model':f'lsmmod:block/{suffix}','y':angle}
        write(A/f'blockstates/{name}.json',{'variants':variants})

if __name__=='__main__':main()
