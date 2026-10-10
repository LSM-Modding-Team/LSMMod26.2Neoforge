"""External geometry preview of assembled railing slopes, not a game screenshot."""
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
from PIL import Image
import numpy as np
from create_school_railing_slopes import A,ROOT,pieces,FLIGHT_RISE
from check_school_railing_slopes import mesh

def color(name):return np.array(Image.open(A/f'textures/block/{name}.png').convert('RGB')).mean(axis=(0,1))/255
wall=color('light_school_wall');metal=color('school_gate_edge');dark=color('dark_school_wall')
def cube(x,y,z,height=1,width=1,depth=1):
 p=np.array([[x,y,z],[x+width,y,z],[x+width,y+depth,z],[x,y+depth,z],[x,y,z+height],[x+width,y,z+height],[x+width,y+depth,z+height],[x,y+depth,z+height]])
 return [p[i] for i in ([0,1,2,3],[4,7,6,5],[0,4,5,1],[1,5,6,2],[2,6,7,3],[3,7,4,0])]
def main():
 fig=plt.figure(figsize=(16,7));fig.patch.set_facecolor('#e8ecee');plan=list(pieces())
 for idx,group in enumerate((plan[:4],plan[4:]),1):
  ax=fig.add_subplot(1,2,idx,projection='3d');ax.set_facecolor('#e8ecee')
  for ident,col,row,low,rise,post in group:
   vertices,uv,faces=mesh(A/f'models/block/{ident}.obj')
   # Display rising left to right; model north goes from z=1 to z=0.
   points=np.column_stack((col+1-vertices[:,2],vertices[:,0],row+vertices[:,1]))
   for material,face in faces:
    poly=points[face];n=np.cross(poly[1]-poly[0],poly[2]-poly[0]);n/=np.linalg.norm(n)
    shade=.7+.3*max(0,n@np.array([-.4,-.6,.7]))
    ax.add_collection3d(Poly3DCollection([poly],facecolors=[(wall if material=='wall' else metal)*shade],edgecolors='none'))
   for below in (range(row) if idx==1 else ()):ax.add_collection3d(Poly3DCollection(cube(col,0,below),facecolors=wall*.85,edgecolors='none'))
  end=7 if idx==1 else 6
  for landing,height in ((-1,0),(end,5 if idx==1 else FLIGHT_RISE)):
   ax.add_collection3d(Poly3DCollection(cube(landing,0,height,.75),facecolors=wall,edgecolors='none'))
   ax.add_collection3d(Poly3DCollection(cube(landing,.4375,height+1.125,.125,1,.125),facecolors=metal,edgecolors='none'))
   ax.add_collection3d(Poly3DCollection(cube(landing+.4375,.4375,height+.75,.375,.125,.125),facecolors=metal,edgecolors='none'))
  if idx==1:
   for col in (2,3,4):ax.add_collection3d(Poly3DCollection(cube(col,0,0,6.5),facecolors=dark,edgecolors='none'))
  ax.set_xlim(-1,end+1);ax.set_ylim(0,1.2);ax.set_zlim(0,7);ax.set_box_aspect((end+2,2,7));ax.view_init(elev=12,azim=-74);ax.set_axis_off()
  ax.set_title('4 piezas: tramo 7x5 interrumpido por soporte de 3' if idx==1 else '6 piezas: extremos a 4 bloques de desnivel',fontsize=13)
 fig.text(.5,.03,'Vista previa externa: muro claro, metal del portón y borde inferior inclinado continuo.',ha='center')
 (ROOT/'previews').mkdir(exist_ok=True);fig.savefig(ROOT/'previews/school_railing_slopes.png',dpi=140,bbox_inches='tight');plt.close(fig)
if __name__=='__main__':main()
