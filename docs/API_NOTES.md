# API: puntos de partida en Minecraft 26.2

Busca primero un uso actual en el proyecto con `rg`. Si falta, consulta fuentes/JAR locales de Minecraft **26.2**, NeoForge **26.2.0.88** y Fusion **1.3.16**. No copies firmas de 1.21.x de memoria. Indica si una firma fue vista en fuentes o verificada por compilación; no conviertas comprobación estática en «compila».

| Necesidad | Referencia local |
|---|---|
| Registro de bloque/ítem y ResourceKey | `registry/ModBlocks.java`, `registry/ModItems.java` |
| Colocación, interacción, soporte y drop | Bloque equivalente en `block/`; `useItemOn`, `useWithoutItem`, `updateShape` |
| Formas/rotaciones/contornos | `block/ExactVoxelShapes.java`, `SimpleBlockOutline.java` |
| Entidad, atributos, daño y guardado | `modules/npcs/entity/{SchoolNpcEntity,StudentEntity,TeacherEntity}.java`; ValueInput/ValueOutput |
| Tamaño de NPC | `modules/npcs/entity/SchoolNpcSize.java`; `getDefaultDimensions` (`getDimensions` es final) |
| Renderer/modelo de entidad | `client/renderer/SchoolNpcRenderer.java`, `client/model/PlaceholderSchoolNpcModel.java` |
| Equipment de jugador | `modules/uniforms/item/SchoolUniformItem.java`, `modules/uniforms/client/ClientSetup.java`; ruta de pantalón explícita |
| Pantalla/entrada de GUI 26.2 | `modules/npcs/client/screen/NpcSummonScreen.java`; `GuiGraphicsExtractor`, `MouseButtonEvent`, `KeyEvent` |
| Navegador y salidas de PC/laptop | `modules/technology/client/SchoolPcInput.java`, `modules/technology/client/screen/RinkuSchoolPcScreen.java`, `client/media/` y `media/`. Rinku `3.0.4-26.2`: navegador compartido por dispositivo; `Screen.removed()` libera la referencia de GUI, `SchoolBrowserSessions` conserva sesiones con salidas y las cierra al descargar el origen/mundo. `CefAudioHandler` entrega PCM planar válido solo durante el callback y silencia su reproducción nativa; copiar a colas limitadas. `SchoolBrowserStreamMixin` resuelve únicamente streams registrados en `SoundBufferLibrary.getStream(Identifier, boolean)`. Extraer proyecciones con `ExtractLevelRenderStateEvent` y enviar geometría inmutable con `SubmitCustomGeometryEvent`, usando `LevelRenderState.set/getRenderData`. Enlaces con `SavedDataType`/codec y snapshots validados en servidor. Firmas vistas en binarios/fuentes oficiales, sin compilación ni prueba en juego. |
| Red y separación cliente/servidor | `network/PaintCopyPayload.java`, `client/PaintCopyInput.java` |
| Predicados de modelo Fusion | `client/GlazingFusionRegistration.java`, `SchoolGlazingPredicate.java`, `ModelSelectionPredicate.java`. Fusion 1.3.16 rechaza tanto IDs como instancias de Serializer repetidas; cada tipo necesita su propio serializador y debe devolverlo desde `getSerializer()`. `PaintedMiniCubePredicate` delega la lógica del marco con un serializador propio; comprobación estática: `tools/check_custom_paintable_blocks.py --fine-only`. |
| Conexiones y máscaras de vidrio | `modules/windows_and_enclosures/block/SchoolGlazing.java`; canales independientes en puertas de enfermería |
| Comandos, persistencia y permisos | `world/SchoolSpaceCommands.java`, `SchoolSpaceCatalog.java` |
| Compatibilidad Jade | `compat/jade/`; opcional, sin cargar sus clases desde el arranque común |

Recursos 26.x: `items/` contiene la definición moderna de ítem; carpetas de datos `recipe`, `loot_table`, `structure` en singular. Reutiliza un JSON existente como muestra.

Trampas de renderizado Fusion/OBJ: [ASSETS](ASSETS.md). Para worldgen, efectos, raid y bosses sin ejemplo local, verifica la API antes de implementar. Si hace falta conservar una nota nueva, añade aquí una fila breve con firma, fuente y estado, sin copiar registros de investigación externos.
