/**
 * School door consumer for Minecraft 26.2, NeoForge 26.2.0.88 and Java 25.
 * All four stable block/item IDs remain in windows_and_enclosures; Core registers none.
 *
 * <p>New placement uses BlockPlaceContext's resolved target, including replaceable blocks and face offsets.
 * Singles anchor the bottom hinge cell; the double anchors local column 0, the exterior doorright corner.
 * DoorPlacement.anchored normalizes the FULL transformed anchor cube. The player's facing, click side and
 * closed-footprint obstruction scores choose orientation/reflection; sneaking optionally inverts reflection
 * without changing the anchor destination. Required cells occupy exactly three levels, never the arc.
 *
 * <p>Schema 2 opts explicitly into Core's VANILLA motion: instantaneous independent 90-degree poses,
 * no entity/arc/target-obstacle veto, no pushing or destruction of neighbors. Core's derived DoorBody
 * entities provide selection, collision and moving-surface rendering beyond the closed footprint. Fixed
 * panels keep their carrier meshes. Entity render extraction applies the same Core transform, with no
 * visual interpolation. No invisible sweep blocks or independent opening/collision state are stored.
 *
 * <p>Manual use is server ray-traced. FOLLOW_POWER remains per leaf: the three exterior-column cells
 * adjacent to each double leaf control that leaf (0 doorright, 2 doorleft); column 1 is neutral. Singles
 * use all required cells. Real neighbor edges drive power; no redstone polling is introduced. Painting
 * native paint tools do not toggle entity surfaces. Fixed walls render their base material.
 * Existing surface data is archived verbatim; no external painting provider is registered.
 *
 * <p>Only exact known schema-1 snapshots opt into upgrade: namespace/type, closed cells, envelope,
 * controller, removal policy, surfaces, old pivots/turns and input policies must match the frozen legacy
 * signature. Core keeps origin/UUID/open/powered states and journals removal of verified old proxies.
 * Archived surface data remains in its existing UUID-keyed file, untouched. Unknown/custom records are preserved.
 * Rendering-only 0.125px rebates and darker joint edges separate closed leaves/panels while original
 * selection/collision boxes keep the seam physically continuous. UV density is preserved, not stretched.
 *
 * <p>Authoring fixtures drive the generated geometry/material catalog and inventory models. External renders
 * cover the available geometry, both poses, independent double leaves, items and mirrors. Static API
 * inspection is not compilation or an in-game check.
 */
package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;
