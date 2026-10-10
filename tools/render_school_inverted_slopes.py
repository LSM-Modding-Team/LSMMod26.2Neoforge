"""External preview of the fixed Y-inverted wall-only slope assemblies."""
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
import numpy as np
from create_school_inverted_slopes import ROOT,A,pieces
from check_school_railing_slopes import mesh
from render_school_railing_slopes import cube,wall,dark

def main():
 fig=plt.figure(figsize=(16,7));fig.patch.set_facecolor('#e8ecee');plan=list(pieces())
 for idx,group in enumerate((plan[:4],plan[4:]),1):
  ax=fig.add_subplot(1,2,idx,projection='3d');ax.set_facecolor('#e8ecee')
  lift=5 if idx==1 else 4;end=7 if idx==1 else 6
  for ident,col,row,low,rise in group:
   vertices,uv,faces=mesh(A/f'models/block/{ident}.obj')
   points=np.column_stack((col+1-vertices[:,2],vertices[:,0],lift+row+vertices[:,1]))
   for material,face in faces:
    p=points[face];n=np.cross(p[1]-p[0],p[2]-p[0]);n/=np.linalg.norm(n)
    shade=.7+.3*max(0,n@np.array([-.4,-.6,.7]))
    ax.add_collection3d(Poly3DCollection([p],facecolors=[wall*shade],edgecolors='none'))
  if idx==1:
   for col in (2,3,4):ax.add_collection3d(Poly3DCollection(cube(col,0,0,6.5),facecolors=dark,edgecolors='none'))
  ax.set_xlim(-.1,end+.1);ax.set_ylim(0,1.2);ax.set_zlim(-.1,7);ax.set_box_aspect((end+1,2,7));ax.view_init(elev=-8,azim=-75);ax.set_axis_off()
  ax.set_title('4 piezas invertidas: interrupcion central de 3 bloques' if idx==1 else '6 piezas invertidas: mismo perfil, sin baranda',fontsize=13)
 fig.text(.5,.04,'Vista previa externa. Borde inclinado inicial Y=0; caras planas arriba.',ha='center')
 fig.savefig(ROOT/'previews/school_inverted_slopes.png',dpi=140,bbox_inches='tight');plt.close(fig)
if __name__=='__main__':main()
