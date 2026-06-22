package com.maersk.wms.inventory.domain.core.repository;

import com.maersk.wms.inventory.domain.core.model.InventorySnapshot;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for InventorySnapshot entities.
 * Supports point-in-time inventory state queries.
 */
public interface InventorySnapshotRepository {

    // ═══════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Save snapshot.
     */
    InventorySnapshot save(InventorySnapshot snapshot);

    /**
     * Save multiple snapshots.
     */
    List<InventorySnapshot> saveAll(List<InventorySnapshot> snapshots);

    /**
     * Find snapshot by key.
     */
    Optional<InventorySnapshot> findByKey(SnapshotKey snapshotKey);

    /**
     * Delete snapshot.
     */
    void delete(SnapshotKey snapshotKey);

    // ═══════════════════════════════════════════════════════════════
    // SNAPSHOT QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find snapshot for inventory at specific point in time.
     */
    Optional<InventorySnapshot> findByInventoryKeyAtTime(InventoryKey inventoryKey, Instant asOfTime);

    /**
     * Find latest snapshot for inventory.
     */
    Optional<InventorySnapshot> findLatestByInventoryKey(InventoryKey inventoryKey);

    /**
     * Find all snapshots for inventory.
     */
    List<InventorySnapshot> findByInventoryKey(InventoryKey inventoryKey);

    // ═══════════════════════════════════════════════════════════════
    // PERIOD-BASED QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find snapshots for period (e.g., month-end close).
     */
    List<InventorySnapshot> findByPeriod(String period, WarehouseKey warehouseKey);

    /**
     * Find snapshots by snapshot type.
     */
    List<InventorySnapshot> findByType(InventorySnapshot.SnapshotType snapshotType,
                                        WarehouseKey warehouseKey);

    /**
     * Find snapshots in date range.
     */
    List<InventorySnapshot> findByDateRange(LocalDate from, LocalDate to, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // ATTRIBUTE-BASED QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find snapshots for SKU.
     */
    List<InventorySnapshot> findBySku(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Find snapshots for location.
     */
    List<InventorySnapshot> findByLocation(LocationKey locationKey, WarehouseKey warehouseKey);

    /**
     * Find snapshots for storer.
     */
    List<InventorySnapshot> findByStorer(StorerKey storerKey, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // CLEANUP OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Delete snapshots older than specified date.
     */
    int deleteOlderThan(Instant olderThan, WarehouseKey warehouseKey);

    /**
     * Count snapshots.
     */
    long count(WarehouseKey warehouseKey);

    /**
     * Count snapshots by period.
     */
    long countByPeriod(String period, WarehouseKey warehouseKey);
}
