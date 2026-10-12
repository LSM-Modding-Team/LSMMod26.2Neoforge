# Integración con bunnidogs (pendiente)

No hay integración de juego implementada. El diseño exige bunnidogs hostiles a alumnos/profesores salvo Bianca, raids en zonas con muchos nidos y mapa al colegio fabricado con legendarios de bunnidogs.

## Datos que deben verificarse antes de usar

IDs históricos: entidad `bunnidogs:bunnidog`, bloque/POI `bunnidogs:nest`, estructura `bunnidogs:warren_village`, features `bunnidogs:warren`/`surface_warren`. Legendarios candidatos para mapa: titan_haunch, gilded_foot, moon_sinew, listening_shell, everbloom. No son contratos verificados contra la versión instalada; comprueba el JAR/código disponible antes de importar clases o registrar recetas. No hace falta para crear arte LSM.

## Diseño y propuestas

- Hostilidad bunnidog → alumnos/profesores; Bianca lleva una marca en su entidad LSM para excluirla. Alcance sobre otros grupos y contraataque LSM → bunnidogs: V23/V24 sin definir.
- Arquitectura propuesta D1: dos JAR; dependencia LSM → bunnidogs, nunca inversa; cruce en integration/bunnidogs/. Resolver antes de implementar. Posible goal añadido en EntityJoinLevelEvent; verificar su API y filtrado. Patrón histórico: registerGoals corre antes del genoma, por lo que filtrar en canUse, no al registrar.
- Nidos: consulta POIs de solo lectura; `PoiManager.take` reclama tickets y no sirve para contar. Cantidad mínima/radio/lugar D6 pendientes. Propuesta de gestor: [MECHANICS_SPECS](MECHANICS_SPECS.md).
- Mapa al colegio más cercano: [WORLD_SPECS](WORLD_SPECS.md). Ingredientes/cantidades P1 pendientes; las recetas de IDs externos necesitan la dependencia correcta.
- Debug histórico `/bunnidogs warren [1-5]`: verificar antes de asumirlo.

Orden práctico: confirmar versiones/dependencia → probar detección de entidad → goal y excepción Bianca → lectura de nidos/raids → ingredientes del mapa. No se impone una dependencia de bunnidogs al arte ni al resto del desarrollo de LSM.
