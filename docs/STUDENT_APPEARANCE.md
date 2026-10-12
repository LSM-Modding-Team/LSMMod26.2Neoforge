# Retratos y texturas de Student

Solo Student; Teacher ignora estos NBT. Los modelos masculino/femenino, uniforme, busto, caminar y Width/Height se conservan. Contrato común: [SCHOOL_NPCS](SCHOOL_NPCS.md).

| NBT | Valores |
|---|---|
| gender | male/female |
| skinColor | 0–63 |
| eyeColor | 0–7 (negro, marrones, verdes) |
| haircutMale / haircutFemale | 0–31 del género actual |
| glassesType | 0 sin lentes, 1–8 variantes |
| freeHair | false recogido / true suelto, solo female; independiente del corte |

Sin ningún índice válido conserva placeholder. Cualquier componente válido activa retrato; omitidos quedan -1 y usan piel/ojos/corte 0 y ningún lente al componer. Negativos desactivan, superiores se limitan al máximo. Solo se guarda el corte del género actual. Huevos no sortean apariencia; conservan sorteo de género/stats.

Los retratos sustituyen cabeza completa/números/M-F y piel de manos, conservando la ropa. Son capas 2D vanilla: diseño en cuadrícula 64×64, RGBA exportado a 256×256 por vecino más cercano. Nada de modelos 3D adicionales de pelo ni PNG por tamaño de entidad.

## Fuentes de paleta y catálogo

`src/main/java/net/nicomar2009/lsmmod/util/NpcAppearanceCatalog.java` y los generadores de apariencia en `tools/` contienen colores/nombres/índices exactos. Edita esos datos junto con sus assets; no mantengas otra copia de la paleta en documentación.

32 cortes por género: 16 lisos, 8 ondulados y 8 rulosos. Se conservan los primeros seis índices femeninos. freeHair alterna 32 atlas recogidos/sueltos; no cambia el índice. Ocho ojos sin azul; ocho lentes de blanco a negro más ausencia. En total 176 capas. Reutiliza los UV y evita inferir freeHair desde haircutFemale.

Generadores: `create_student_appearance_assets.py`, `create_student_gender_skins.py`, `create_placeholder_student_skins.py`. Resolución/caché en `client/renderer/StudentAppearanceTextures.java`. Busca rutas finales en esos archivos antes de sustituir PNG; previews y revisión de texturas según [ASSETS](ASSETS.md).

```mcfunction
/summon lsmmod:student ~ ~ ~ {gender:"female",StudentData:{Level:"secondary",Grade:5},skinColor:12,eyeColor:7,haircutFemale:5,freeHair:true,glassesType:0,Width:14,Height:14}
/data merge entity @e[type=lsmmod:student,sort=nearest,limit=1] {skinColor:-1,eyeColor:-1,haircutMale:-1,haircutFemale:-1,glassesType:-1,freeHair:false}
```

El segundo comando restaura placeholder. Para editar solo freeHair, usa `/data merge` con ese booleano y conserva los otros índices. Revisa frente/dorso/perfil, capas transparentes y ambos modelos antes de publicar; comprueba fallback/F3+T en Minecraft cuando se permita.
