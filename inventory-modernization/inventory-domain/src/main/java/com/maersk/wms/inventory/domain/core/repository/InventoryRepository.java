package com.maersk.wms.inventory.domain.core.repository;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Inventory aggregate root.
 * Follows DDD repository pattern for LOTxLOCxID inventory records.
 */
public interface InventoryRepository {

    // ═══════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Save inventory record (create or update).
     */
    Inventory save(Inventory inventory);

    /**
     * Save multiple inventory records.
     */
    List<Inventory> saveAll(List<Inventory> inventories);

    /**
     * Delete inventory by key.
     */
    void delete(InventoryKey inventoryKey);

    /**
     * Delete inventory record.
     */
    void delete(Inventory inventory);

    // ═══════════════════════════════════════════════════════════════
    // FIND BY KEY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find inventory by primary key.
     */
    Optional<Inventory> findByKey(InventoryKey inventoryKey);

    /**
     * Find inventory by composite key (LOTxLOCxID).
     */
    Optional<Inventory> findByCompositeKey(LotKey lot, LocationKey location, LpnKey lpn);

    /**
     * Find inventory by composite key value object.
     */
    Optional<Inventory> findByCompositeKey(InventoryCompositeKey compositeKey);

    /**
     * Check if inventory exists by key.
     */
    boolean existsByKey(InventoryKey inventoryKey);

    /**
     * Check if inventory exists by composite key.
     */
    boolean existsByCompositeKey(LotKey lot, LocationKey location, LpnKey lpn);

    // ═══════════════════════════════════════════════════════════════
    // FIND BY ATTRIBUTE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find all inventory for SKU.
     */
    List<Inventory> findBySku(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Find all inventory at location.
     */
    List<Inventory> findByLocation(LocationKey locationKey, WarehouseKey warehouseKey);

    /**
     * Find all inventory for LPN.
     */
    List<Inventory> findByLpn(LpnKey lpnKey, WarehouseKey warehouseKey);

    /**
     * Find all inventory for lot.
     */
    List<Inventory> findByLot(LotKey lotKey, WarehouseKey warehouseKey);

    /**
     * Find all inventory for storer.
     */
    List<Inventory> findByStorer(StorerKey storerKey, WarehouseKey warehouseKey);

    /**
     * Find inventory by status.
     */
    List<Inventory> findByStatus(Inventory.InventoryStatusCode status, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // COMBINED FIND OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find inventory by SKU and location.
     */
    List<Inventory> findBySkuAndLocation(SkuKey skuKey, LocationKey locationKey);

    /**
     * Find inventory by SKU and storer.
     */
    List<Inventory> findBySkuAndStorer(SkuKey skuKey, StorerKey storerKey, WarehouseKey warehouseKey);

    /**
     * Find inventory by location and status.
     */
    List<Inventory> findByLocationAndStatus(LocationKey locationKey, Inventory.InventoryStatusCode status);

    // ═══════════════════════════════════════════════════════════════
    // AVAILABILITY QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find available inventory for SKU (not on hold, not fully allocated).
     */
    List<Inventory> findAvailable(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Find available inventory in zone.
     */
    List<Inventory> findAvailableInZone(SkuKey skuKey, String zoneCode, WarehouseKey warehouseKey);

    /**
     * Find inventory on hold.
     */
    List<Inventory> findOnHold(WarehouseKey warehouseKey);

    /**
     * Find inventory by hold code.
     */
    List<Inventory> findByHoldCode(String holdCode, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find allocated inventory.
     */
    List<Inventory> findAllocated(WarehouseKey warehouseKey);

    /**
     * Find inventory allocated for order.
     */
    List<Inventory> findAllocatedForOrder(OrderKey orderKey);

    /**
     * Find over-allocated inventory (allocated > on-hand).
     */
    List<Inventory> findOverallocated(WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // FIFO ALLOCATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find inventory ordered by FIFO date (receipt date).
     */
    List<Inventory> findBySkuOrderByFifo(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Find inventory ordered by expiry date (FEFO).
     */
    List<Inventory> findBySkuOrderByExpiry(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Find inventory ordered by lot number (LIFO by lot).
     */
    List<Inventory> findBySkuOrderByLot(SkuKey skuKey, WarehouseKey warehouseKey, boolean descending);

    // ═══════════════════════════════════════════════════════════════
    // QUANTITY AGGREGATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Sum on-hand quantity for SKU.
     */
    Quantity sumOnHandQuantity(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Sum available quantity for SKU.
     */
    Quantity sumAvailableQuantity(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Sum allocated quantity for SKU.
     */
    Quantity sumAllocatedQuantity(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Sum on-hand quantity at location.
     */
    Quantity sumOnHandQuantityAtLocation(SkuKey skuKey, LocationKey locationKey);

    /**
     * Sum total on-hand for storer.
     */
    Quantity sumOnHandForStorer(StorerKey storerKey, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // LOCKING OPERATIONS (FOR CONCURRENCY)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find and lock inventory for update (pessimistic lock).
     */
    Optional<Inventory> findByKeyForUpdate(InventoryKey inventoryKey);

    /**
     * Find and lock multiple inventory records.
     */
    List<Inventory> findByKeysForUpdate(List<InventoryKey> inventoryKeys);

    // ═══════════════════════════════════════════════════════════════
    // BATCH OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Count inventory records matching criteria.
     */
    long count(WarehouseKey warehouseKey);

    /**
     * Count inventory by status.
     */
    long countByStatus(Inventory.InventoryStatusCode status, WarehouseKey warehouseKey);

    /**
     * Find inventory with zero quantity (cleanup candidates).
     */
    List<Inventory> findZeroQuantity(WarehouseKey warehouseKey);
}
