package com.maersk.wms.inventory.domain.discovery.service;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for inventory discovery/search operations.
 * Supports searching by SKU, Location, Lot, LPN, Storer, Status, etc.
 */
public interface InventorySearchService {

    // ═══════════════════════════════════════════════════════════════
    // SINGLE ATTRIBUTE SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchBySku(SkuKey skuKey, WarehouseKey warehouseKey);

    List<Inventory> searchByLocation(LocationKey locationKey, WarehouseKey warehouseKey);

    List<Inventory> searchByLot(LotKey lotKey, WarehouseKey warehouseKey);

    List<Inventory> searchByLpn(LpnKey lpnKey, WarehouseKey warehouseKey);

    List<Inventory> searchByStorer(StorerKey storerKey, WarehouseKey warehouseKey);

    List<Inventory> searchByStatus(Inventory.InventoryStatusCode status, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // COMBINED SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchBySkuAndLocation(SkuKey skuKey, LocationKey locationKey);

    List<Inventory> searchBySkuAndStorer(SkuKey skuKey, StorerKey storerKey, WarehouseKey warehouseKey);

    List<Inventory> searchByLocationAndStatus(LocationKey locationKey,
                                               Inventory.InventoryStatusCode status);

    List<Inventory> searchByStorerAndStatus(StorerKey storerKey,
                                             Inventory.InventoryStatusCode status,
                                             WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // AVAILABILITY SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchAvailable(SkuKey skuKey, WarehouseKey warehouseKey);

    List<Inventory> searchAvailableInZone(SkuKey skuKey, String zoneCode, WarehouseKey warehouseKey);

    List<Inventory> searchAvailableWithMinQuantity(SkuKey skuKey, Quantity minQuantity,
                                                    WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // EXPIRY-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchExpiringSoon(int daysToExpiry, WarehouseKey warehouseKey);

    List<Inventory> searchExpired(WarehouseKey warehouseKey);

    List<Inventory> searchByExpiryRange(LocalDate fromDate, LocalDate toDate, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // LOTTABLE-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchByLottable(String lottableField, String lottableValue, WarehouseKey warehouseKey);

    List<Inventory> searchByLottables(LottableAttributes criteria, boolean[] matchFlags,
                                       WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // HOLD-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchOnHold(WarehouseKey warehouseKey);

    List<Inventory> searchByHoldCode(String holdCode, WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    List<Inventory> searchAllocated(WarehouseKey warehouseKey);

    List<Inventory> searchAllocatedForOrder(OrderKey orderKey);

    List<Inventory> searchOverallocated(WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // ADVANCED SEARCH
    // ═══════════════════════════════════════════════════════════════

    /**
     * Search with flexible criteria.
     */
    List<Inventory> search(InventorySearchCriteria criteria);

    /**
     * Search with pagination.
     */
    SearchResult<Inventory> searchPaginated(InventorySearchCriteria criteria, int page, int pageSize);

    /**
     * Count matching inventory records.
     */
    long count(InventorySearchCriteria criteria);

    // ═══════════════════════════════════════════════════════════════
    // SEARCH CRITERIA
    // ═══════════════════════════════════════════════════════════════

    record InventorySearchCriteria(
            SkuKey skuKey,
            LocationKey locationKey,
            LotKey lotKey,
            LpnKey lpnKey,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            Inventory.InventoryStatusCode status,
            String holdCode,
            LottableAttributes lottables,
            boolean[] lottableMatchFlags,
            Quantity minQuantity,
            Quantity maxQuantity,
            LocalDate expiryBefore,
            LocalDate expiryAfter,
            boolean excludeOnHold,
            boolean excludeAllocated,
            boolean onlyAvailable,
            String sortBy,
            boolean ascending
    ) {
        public static InventorySearchCriteria forSku(SkuKey skuKey, WarehouseKey warehouseKey) {
            return new InventorySearchCriteria(skuKey, null, null, null, null, warehouseKey,
                    null, null, null, null, null, null, null, null, false, false, false, null, true);
        }

        public static InventorySearchCriteria forLocation(LocationKey locationKey, WarehouseKey warehouseKey) {
            return new InventorySearchCriteria(null, locationKey, null, null, null, warehouseKey,
                    null, null, null, null, null, null, null, null, false, false, false, null, true);
        }
    }

    record SearchResult<T>(
            List<T> items,
            long totalCount,
            int page,
            int pageSize,
            int totalPages
    ) {}
}
