package com.maersk.wms.inventory.domain.structure.repository;

import com.maersk.wms.inventory.domain.structure.model.InventoryHierarchy;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for InventoryHierarchy entities.
 * Manages parent-child nesting relationships: Pallet → Case → Inner Pack → Each.
 */
public interface InventoryHierarchyRepository {

    // ═══════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Save hierarchy record.
     */
    InventoryHierarchy save(InventoryHierarchy hierarchy);

    /**
     * Save multiple hierarchy records.
     */
    List<InventoryHierarchy> saveAll(List<InventoryHierarchy> hierarchies);

    /**
     * Delete hierarchy record.
     */
    void delete(NestingKey nestingKey);

    /**
     * Delete hierarchy by parent-child relationship.
     */
    void deleteByParentAndChild(LpnKey parentLpn, LpnKey childLpn);

    // ═══════════════════════════════════════════════════════════════
    // FIND OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find hierarchy by key.
     */
    Optional<InventoryHierarchy> findByKey(NestingKey nestingKey);

    /**
     * Find hierarchy by parent and child LPN.
     */
    Optional<InventoryHierarchy> findByParentAndChild(LpnKey parentLpn, LpnKey childLpn);

    /**
     * Check if hierarchy exists.
     */
    boolean exists(LpnKey parentLpn, LpnKey childLpn);

    // ═══════════════════════════════════════════════════════════════
    // PARENT-CHILD QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find parent of LPN.
     */
    Optional<LpnKey> findParent(LpnKey childLpn);

    /**
     * Find all direct children of LPN.
     */
    List<LpnKey> findChildren(LpnKey parentLpn);

    /**
     * Find all hierarchy records for parent.
     */
    List<InventoryHierarchy> findByParentLpn(LpnKey parentLpn);

    /**
     * Find hierarchy record for child.
     */
    Optional<InventoryHierarchy> findByChildLpn(LpnKey childLpn);

    // ═══════════════════════════════════════════════════════════════
    // HIERARCHY TRAVERSAL
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find complete hierarchy for LPN (all ancestors and descendants).
     */
    List<InventoryHierarchy> findCompleteHierarchy(LpnKey lpnKey);

    /**
     * Find root container (top-level parent) for LPN.
     */
    LpnKey findRootContainer(LpnKey lpnKey);

    /**
     * Find all leaf LPNs (no children) under parent.
     */
    List<LpnKey> findLeafLpns(LpnKey parentLpn);

    /**
     * Find all ancestors of LPN (parent, grandparent, etc.).
     */
    List<LpnKey> findAncestors(LpnKey lpnKey);

    /**
     * Find all descendants of LPN (children, grandchildren, etc.).
     */
    List<LpnKey> findDescendants(LpnKey lpnKey);

    // ═══════════════════════════════════════════════════════════════
    // NESTING LEVEL QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get nesting level for LPN (0 = root, 1 = first level child, etc.).
     */
    int getNestingLevel(LpnKey lpnKey);

    /**
     * Find LPNs at specific nesting level under parent.
     */
    List<LpnKey> findAtNestingLevel(LpnKey rootLpn, int level);

    // ═══════════════════════════════════════════════════════════════
    // CONTAINER TYPE QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find all pallets in warehouse.
     */
    List<LpnKey> findPallets(WarehouseKey warehouseKey);

    /**
     * Find all cases under pallet.
     */
    List<LpnKey> findCasesOnPallet(LpnKey palletLpn);

    /**
     * Find all inner packs in case.
     */
    List<LpnKey> findInnerPacksInCase(LpnKey caseLpn);

    /**
     * Find by container type.
     */
    List<InventoryHierarchy> findByParentType(InventoryHierarchy.ContainerType containerType,
                                               WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // VALIDATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Check if LPN is nested (has a parent).
     */
    boolean isNested(LpnKey lpnKey);

    /**
     * Check if LPN has children.
     */
    boolean hasChildren(LpnKey lpnKey);

    /**
     * Check if nesting would create a cycle.
     */
    boolean wouldCreateCycle(LpnKey parentLpn, LpnKey childLpn);

    // ═══════════════════════════════════════════════════════════════
    // LOCATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find all hierarchies at location.
     */
    List<InventoryHierarchy> findByLocation(LocationKey locationKey, WarehouseKey warehouseKey);

    /**
     * Count nested LPNs at location.
     */
    long countNestedAtLocation(LocationKey locationKey, WarehouseKey warehouseKey);
}
