# LSM Mod — instrucciones para Codex

- Responde en español. La petición actual del usuario tiene prioridad. Código, IDs y comentarios en inglés.
- Para crear bloques/modelos/texturas, entra directamente en [ASSETS: flujo](docs/ASSETS.md#flujo-rápido) y su tabla de generador/check/render. Para otros trabajos, usa [START_HERE](START_HERE.md).
- **No compiles ni ejecutes Minecraft**, por instrucción vigente del usuario. Usa comprobaciones estáticas relevantes; distingue revisión estática, render externo, compilación y prueba en juego.
- **Haz commit y push en la rama existente `master` en la misma respuesta**, sin pedir autorización. Trae e integra `origin/master` antes de publicar, conservando cambios de otras conversaciones. No crees ramas nuevas ni uses force-push.
- Reutiliza clases, materiales, modelos padre y generadores existentes. Conserva IDs, NBT y propiedades guardadas; una retirada o migración requiere petición del usuario.
- **Revisión visual obligatoria:** toda textura creada o modificada debe revisarse aplicada al modelo real mediante renders, con sus UV, materiales y transparencia. Corrige errores visibles antes de publicar; revisa también el ítem y las caras/orientaciones afectadas. Un render externo no demuestra funcionamiento en Minecraft.
- **Formas:** separa `getShape` (contorno sencillo) de `getCollisionShape` (geometría física). Usa `SimpleBlockOutline.forState` solo si depende del estado; las formas que dependen del mundo/contexto no admiten esa caché. Reutiliza cuadrículas y rotaciones; verifica orientaciones y celdas múltiples. `checkFurnitureOutlines` requiere compilar: queda pendiente mientras siga la prohibición.
- Ejecuta solo las comprobaciones existentes que cubran el cambio. Para lógica pura, usa `RulesCheck`; para arte/documentación, evita crear tests que solo repitan la implementación.
- Actualiza únicamente la guía afectada si cambia su contrato. No añadas historiales, registros de sesiones, documentos por cada bloque ni entradas de cambios en varios archivos. Git guarda la historia. Mantén START_HERE breve y sin cronología.
- Entrega un resumen corto de cambios, verificación y commit. Genera ZIP solo si se pide; excluye `.git`, `.build-tools`, `.gradle`, `build` y `run`.
