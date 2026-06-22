package com.maersk.wms.inventory.domain.structure.service;

import com.maersk.wms.inventory.domain.structure.model.InventoryHierarchy;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for inventory nesting/hierarchy operations.
 * Manages parent-child relationships: Pallet → Case → Inner Pack → Each.
 */
public interface InventoryNestingService {

    // ═══════════════════════════════════════════════════════════════
    // NESTING OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Nest child LPN under parent LPN.
     */
    InventoryHierarchy nest(LpnKey parentLpn, InventoryHierarchy.ContainerType parentType,
                            LpnKey childLpn, InventoryHierarchy.ContainerType childType,
                            int quantity, UserKey nestedBy, WarehouseKey warehouseKey);

    /**
     * Unnest child LPN from parent.
     */
    void unnest(LpnKey parentLpn, LpnKey childLpn, String reason,
                LocationKey newLocation, UserKey unnestedBy);

    /**
     * Build pallet from cases.
     */
    InventoryHierarchy buildPallet(LpnKey palletLpn, List<LpnKey> caseLpns,
                                    LocationKey locationKey, UserKey builtBy,
                                    WarehouseKey warehouseKey);

    /**
     * Break pallet into cases.
     */
    List<LpnKey> breakPallet(LpnKey palletLpn, String reason, UserKey brokenBy);

    /**
     * Consolidate multiple LPNs into one.
     */
    LpnKey consolidate(List<LpnKey> sourceLpns, LpnKey targetLpn,
                       UserKey consolidatedBy, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get parent of LPN.
     */
    Optional<LpnKey> getParent(LpnKey childLpn);

    /**
     * Get all children of LPN.
     */
    List<LpnKey> getChildren(LpnKey parentLpn);

    /**
     * Get complete hierarchy for LPN.
     */
    List<InventoryHierarchy> getHierarchy(LpnKey lpnKey);

    /**
     * Get root container for LPN.
     */
    LpnKey getRootContainer(LpnKey lpnKey);

    /**
     * Get all leaf LPNs (no children) under parent.
     */
    List<LpnKey> getLeafLpns(LpnKey parentLpn);

    /**
     * Get nesting level for LPN.
     */
    int getNestingLevel(LpnKey lpnKey);

    /**
     * Check if LPN is nested.
     */
    boolean isNested(LpnKey lpnKey);

    /**
     * Check if LPN has children.
     */
    boolean hasChildren(LpnKey lpnKey);

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Validate nesting is allowed.
     */
    boolean canNest(LpnKey parentLpn, LpnKey childLpn);

    /**
     * Check for circular nesting.
     */
    boolean wouldCreateCycle(LpnKey parentLpn, LpnKey childLpn);
}
