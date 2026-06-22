package com.maersk.wms.inventory.domain.core.repository;

import com.maersk.wms.inventory.domain.core.model.InventoryTransaction;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for InventoryTransaction entities.
 * Provides audit trail and transaction history access.
 */
public interface InventoryTransactionRepository {

    // ═══════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Save transaction record.
     */
    InventoryTransaction save(InventoryTransaction transaction);

    /**
     * Save multiple transaction records.
     */
    List<InventoryTransaction> saveAll(List<InventoryTransaction> transactions);

    /**
     * Find transaction by key.
     */
    Optional<InventoryTransaction> findByKey(TransactionKey transactionKey);

    // ═══════════════════════════════════════════════════════════════
    // HISTORY QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get transaction history for inventory record.
     */
    List<InventoryTransaction> findByInventoryKey(InventoryKey inventoryKey);

    /**
     * Get transaction history for inventory record with limit.
     */
    List<InventoryTransaction> findByInventoryKey(InventoryKey inventoryKey, int limit);

    /**
     * Get transactions by source (receipt, order, adjustment, etc.).
     */
    List<InventoryTransaction> findBySource(InventoryTransaction.TransactionSource sourceType,
                                             String sourceKey, WarehouseKey warehouseKey);

    /**
     * Get transactions by type.
     */
    List<InventoryTransaction> findByType(InventoryTransaction.TransactionType transactionType,
                                           WarehouseKey warehouseKey);

    /**
     * Get transactions by user.
     */
    List<InventoryTransaction> findByUser(UserKey userKey, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // TIME-BASED QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get transactions in time range.
     */
    List<InventoryTransaction> findByTimeRange(Instant from, Instant to, WarehouseKey warehouseKey);

    /**
     * Get recent transactions.
     */
    List<InventoryTransaction> findRecent(WarehouseKey warehouseKey, int limit);

    /**
     * Get transactions for inventory in time range.
     */
    List<InventoryTransaction> findByInventoryKeyAndTimeRange(InventoryKey inventoryKey,
                                                               Instant from, Instant to);

    // ═══════════════════════════════════════════════════════════════
    // SKU/LOT/LOCATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get transactions for SKU.
     */
    List<InventoryTransaction> findBySku(SkuKey skuKey, WarehouseKey warehouseKey);

    /**
     * Get transactions for location.
     */
    List<InventoryTransaction> findByLocation(LocationKey locationKey, WarehouseKey warehouseKey);

    /**
     * Get transactions for lot.
     */
    List<InventoryTransaction> findByLot(LotKey lotKey, WarehouseKey warehouseKey);

    /**
     * Get transactions for LPN.
     */
    List<InventoryTransaction> findByLpn(LpnKey lpnKey, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // AGGREGATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Count transactions by type.
     */
    long countByType(InventoryTransaction.TransactionType transactionType, WarehouseKey warehouseKey);

    /**
     * Count transactions in time range.
     */
    long countByTimeRange(Instant from, Instant to, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // CORRELATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Find transactions by correlation ID (for saga tracking).
     */
    List<InventoryTransaction> findByCorrelationId(String correlationId);

    /**
     * Find compensating transactions.
     */
    List<InventoryTransaction> findCompensatingTransactions(TransactionKey originalTransactionKey);
}
