package com.maersk.wms.inventory.domain.core.service;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.domain.core.model.InventoryTransaction;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Core service interface for inventory operations.
 * Central inventory management including queries and basic operations.
 */
public interface InventoryCoreService {

    // ═══════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    Optional<Inventory> findByKey(InventoryKey inventoryKey);

    Optional<Inventory> findByCompositeKey(LotKey lot, LocationKey location, LpnKey lpn);

    List<Inventory> findBySku(SkuKey skuKey, WarehouseKey warehouseKey);

    List<Inventory> findByLocation(LocationKey locationKey, WarehouseKey warehouseKey);

    List<Inventory> findByLpn(LpnKey lpnKey, WarehouseKey warehouseKey);

    List<Inventory> findByLot(LotKey lotKey, WarehouseKey warehouseKey);

    List<Inventory> findByStorer(StorerKey storerKey, WarehouseKey warehouseKey);

    List<Inventory> findAvailableInventory(SkuKey skuKey, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // QUANTITY AGGREGATIONS
    // ═══════════════════════════════════════════════════════════════

    Quantity getOnHandQuantity(SkuKey skuKey, WarehouseKey warehouseKey);

    Quantity getAvailableQuantity(SkuKey skuKey, WarehouseKey warehouseKey);

    Quantity getAllocatedQuantity(SkuKey skuKey, WarehouseKey warehouseKey);

    Quantity getOnHandQuantityAtLocation(SkuKey skuKey, LocationKey locationKey);

    Quantity getTotalOnHandForStorer(StorerKey storerKey, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    AllocationResult allocateInventory(AllocationCriteria criteria);

    void deallocateInventory(AllocationKey allocationKey, String reason, UserKey deallocatedBy);

    AllocationResult reallocateInventory(AllocationKey existingAllocation, AllocationCriteria newCriteria);

    // ═══════════════════════════════════════════════════════════════
    // STATUS OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    void applyHold(InventoryKey inventoryKey, String holdCode, String reason, UserKey appliedBy);

    void releaseHold(InventoryKey inventoryKey, String reason, UserKey releasedBy);

    void changeStatus(InventoryKey inventoryKey, Inventory.InventoryStatusCode newStatus,
                      String reason, UserKey changedBy);

    // ═══════════════════════════════════════════════════════════════
    // TRANSACTION HISTORY
    // ═══════════════════════════════════════════════════════════════

    List<InventoryTransaction> getTransactionHistory(InventoryKey inventoryKey);

    List<InventoryTransaction> getTransactionsBySource(String sourceType, String sourceKey);

    List<InventoryTransaction> getRecentTransactions(WarehouseKey warehouseKey, int limit);
}
