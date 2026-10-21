/**
 * School door consumer of Core 37cec9c and PaintingMod 0c6ccb9 (26.2 / NeoForge 26.2.0.88 / Java 25).
 * Registrations, textures and authoring geometry belong only to windows_and_enclosures in LSMMod.
 * IDs deliberately differ from retired doors: dimensions, saved membership and paint schema are incompatible.
 * No legacy block/world/schematic migration is performed. The existing gate/sliding entrance are unchanged.
 *
 * <p>One item installs the entire structure, including Core's conservative sweep reservations. Facing follows
 * the player's horizontal direction; sneak-placement reflects local X. Core transforms exterior hinges and
 * all surfaces, so glass/trim/handles share each leaf's exact 90-degree pose. The supplied open example is
 * reproduced by solving the rigid transform, not copying group origins or adding an opening translation.
 *
 * <p>Manual clicks trace current Core geometry. Redstone FOLLOW_POWER is edge-driven per leaf: all three
 * rows of each exterior column control its adjacent leaf in the double (column 0 doorright, column 2 doorleft);
 * column 1 and sweep proxies are neutral. Singles use all required envelope cells. Manual use may override
 * the current signal until its next change. A blocked edge retains Core's previous power memory; another
 * neighbor notification or manual click is needed to retry. No polling ticker is installed.
 *
 * <p>Opening AND closing reject any live nonspectator entity intersecting Core's conservative continuous-arc
 * AABBs, and reject a target pose intersecting the other leaf. Contact without penetration is allowed.
 * Placement rejects occupied/fluid/unloaded reserved cells; movement verifies every stamped reservation.
 * Empty corner space can therefore be reserved, and a nearby entity can block even outside the precise arc.
 * No neighboring blocks are removed and no entities are pushed. Breaking/replacing a stamped piece retires
 * the entire structure through Core, with one survival/integrity-loss drop, none for creative/failed placement.
 * Core's durable drop claim remains at-most-once across a crash, as documented by its public contract.
 *
 * <p>Only the six planar faces of paintablewall expose 8px cells. Core owns all structure/leaf state;
 * PaintingMod owns the public cell/paint/clipboard codecs and tool workflow. LSM persists one PaintLayer per
 * UUID in an atomically replaced dimension-specific file. Carrier entities save UUID/slot links only;
 * their synchronized paint values are derived render snapshots, never another persisted authority.
 * Removal copies PaintData to the sole recovered item, replacement binds it to a fresh structure UUID.
 * Painting/cleaning/sanding never call a Core mutation or send redstone neighbor notifications. The external
 * provider registration makes PaintingMod's hammer reject all carriers; the unmarked wood panel is excluded.
 * Source-material face, UVs, tint and layer survive placement rotation/reflection. Unavailable paint/geometry
 * blocks edits rather than resetting data. Initial chunk-watch and subsequent BE updates carry snapshots.
 *
 * <p>External review is tools/windows_and_enclosures/render_assembly_doors.py. It renders active geometry and
 * inventory models, closed/open/independent leaf combinations, reflected orientations, DEFAULT wall paint,
 * and the supplied open example next to the solved pose. This is not an in-game or compilation check.
 */
package net.nicomar2009.lsmmod.modules.windows_and_enclosures.door;
