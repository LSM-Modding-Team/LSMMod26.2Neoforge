package net.nicomar2009.lsmmod.client;

import com.supermartijn642.fusion.api.model.predicates.FusionModelPredicateRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.nicomar2009.lsmmod.LSMMod;

/** Client-only constructor registers the predicate before Fusion freezes model registries. */
@Mod(value=LSMMod.MOD_ID,dist=Dist.CLIENT)
public final class GlazingFusionRegistration {
    public GlazingFusionRegistration() {
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"painted_mini_cube"),PaintedDoorFramePredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"painted_door_frame"),PaintedDoorFramePredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"projecting_window_layout"),ProjectingWindowLayoutPredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"projecting_window"),ProjectingWindowPredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"column_part"),ColumnPartPredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"sink_connections"),SinkConnectionsPredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"painted_cube_face"),PaintedCubeFacePredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"painted_face"),PaintedFacePredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(
                Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"glazing_mask"),SchoolGlazingPredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"model_selection"),ModelSelectionPredicate.SERIALIZER);
        FusionModelPredicateRegistry.registerModelPredicate(Identifier.fromNamespaceAndPath(LSMMod.MOD_ID,"model_properties"),ModelPropertiesPredicate.SERIALIZER);
    }
}
