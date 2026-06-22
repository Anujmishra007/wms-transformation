package com.maersk.wms.inventory.domain.discovery.service.impl;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.domain.core.repository.InventoryRepository;
import com.maersk.wms.inventory.domain.discovery.service.InventorySearchService;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for inventory discovery/search operations.
 * Supports searching by SKU, Location, Lot, LPN, Storer, Status, etc.
 */
@Service
@Transactional(readOnly = true)
public class InventorySearchServiceImpl implements InventorySearchService {

    private final InventoryRepository inventoryRepository;

    public InventorySearchServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // ═══════════════════════════════════════════════════════════════
    // SINGLE ATTRIBUTE SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchBySku(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findBySku(skuKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchByLocation(LocationKey locationKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByLocation(locationKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchByLot(LotKey lotKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByLot(lotKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchByLpn(LpnKey lpnKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByLpn(lpnKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchByStorer(StorerKey storerKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByStorer(storerKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchByStatus(Inventory.InventoryStatusCode status, WarehouseKey warehouseKey) {
        return inventoryRepository.findByStatus(status, warehouseKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // COMBINED SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchBySkuAndLocation(SkuKey skuKey, LocationKey locationKey) {
        return inventoryRepository.findBySkuAndLocation(skuKey, locationKey);
    }

    @Override
    public List<Inventory> searchBySkuAndStorer(SkuKey skuKey, StorerKey storerKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findBySkuAndStorer(skuKey, storerKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchByLocationAndStatus(LocationKey locationKey,
                                                      Inventory.InventoryStatusCode status) {
        return inventoryRepository.findByLocationAndStatus(locationKey, status);
    }

    @Override
    public List<Inventory> searchByStorerAndStatus(StorerKey storerKey,
                                                    Inventory.InventoryStatusCode status,
                                                    WarehouseKey warehouseKey) {
        return inventoryRepository.findByStorer(storerKey, warehouseKey).stream()
                .filter(inv -> inv.status() == status)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // AVAILABILITY SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchAvailable(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findAvailable(skuKey, warehouseKey);
    }

    @Override
    public List<Inventory> searchAvailableInZone(SkuKey skuKey, String zoneCode, WarehouseKey warehouseKey) {
        return inventoryRepository.findAvailableInZone(skuKey, zoneCode, warehouseKey);
    }

    @Override
    public List<Inventory> searchAvailableWithMinQuantity(SkuKey skuKey, Quantity minQuantity,
                                                           WarehouseKey warehouseKey) {
        return inventoryRepository.findAvailable(skuKey, warehouseKey).stream()
                .filter(inv -> inv.availableQuantity().isGreaterThanOrEqual(minQuantity))
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // EXPIRY-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchExpiringSoon(int daysToExpiry, WarehouseKey warehouseKey) {
        LocalDate expiryThreshold = LocalDate.now().plusDays(daysToExpiry);
        return searchByExpiryRange(LocalDate.now(), expiryThreshold, warehouseKey);
    }

    @Override
    public List<Inventory> searchExpired(WarehouseKey warehouseKey) {
        return searchByExpiryRange(LocalDate.MIN, LocalDate.now().minusDays(1), warehouseKey);
    }

    @Override
    public List<Inventory> searchByExpiryRange(LocalDate fromDate, LocalDate toDate, WarehouseKey warehouseKey) {
        // This would typically be implemented with a specialized repository method
        // For now, we filter in memory
        return inventoryRepository.findBySku(null, warehouseKey).stream()
                .filter(inv -> {
                    if (inv.lottables() == null || inv.lottables().expiryDate() == null) {
                        return false;
                    }
                    LocalDate expiry = inv.lottables().expiryDate();
                    return !expiry.isBefore(fromDate) && !expiry.isAfter(toDate);
                })
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // LOTTABLE-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchByLottable(String lottableField, String lottableValue,
                                             WarehouseKey warehouseKey) {
        // This requires specialized repository implementation
        // Filter by specific lottable field value
        return List.of();
    }

    @Override
    public List<Inventory> searchByLottables(LottableAttributes criteria, boolean[] matchFlags,
                                              WarehouseKey warehouseKey) {
        // Match inventory based on lottable attributes and match flags
        // matchFlags[i] = true means lottable[i] must match
        return List.of();
    }

    // ═══════════════════════════════════════════════════════════════
    // HOLD-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchOnHold(WarehouseKey warehouseKey) {
        return inventoryRepository.findOnHold(warehouseKey);
    }

    @Override
    public List<Inventory> searchByHoldCode(String holdCode, WarehouseKey warehouseKey) {
        return inventoryRepository.findByHoldCode(holdCode, warehouseKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION-BASED SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> searchAllocated(WarehouseKey warehouseKey) {
        return inventoryRepository.findAllocated(warehouseKey);
    }

    @Override
    public List<Inventory> searchAllocatedForOrder(OrderKey orderKey) {
        return inventoryRepository.findAllocatedForOrder(orderKey);
    }

    @Override
    public List<Inventory> searchOverallocated(WarehouseKey warehouseKey) {
        return inventoryRepository.findOverallocated(warehouseKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // ADVANCED SEARCH
    // ═══════════════════════════════════════════════════════════════

    @Override
    public List<Inventory> search(InventorySearchCriteria criteria) {
        // Build dynamic query based on criteria
        // This would typically use a specification pattern or query builder
        List<Inventory> results;

        // Start with base query
        if (criteria.skuKey() != null) {
            results = inventoryRepository.findBySku(criteria.skuKey(), criteria.warehouseKey());
        } else if (criteria.locationKey() != null) {
            results = inventoryRepository.findByLocation(criteria.locationKey(), criteria.warehouseKey());
        } else if (criteria.lpnKey() != null) {
            results = inventoryRepository.findByLpn(criteria.lpnKey(), criteria.warehouseKey());
        } else if (criteria.lotKey() != null) {
            results = inventoryRepository.findByLot(criteria.lotKey(), criteria.warehouseKey());
        } else if (criteria.storerKey() != null) {
            results = inventoryRepository.findByStorer(criteria.storerKey(), criteria.warehouseKey());
        } else {
            // No primary filter - this could be expensive
            results = List.of();
        }

        // Apply additional filters
        return results.stream()
                .filter(inv -> matchesCriteria(inv, criteria))
                .sorted((a, b) -> compareBySortCriteria(a, b, criteria))
                .collect(Collectors.toList());
    }

    @Override
    public SearchResult<Inventory> searchPaginated(InventorySearchCriteria criteria, int page, int pageSize) {
        List<Inventory> allResults = search(criteria);

        int totalCount = allResults.size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalCount);

        List<Inventory> pageResults = fromIndex < totalCount ?
                allResults.subList(fromIndex, toIndex) : List.of();

        return new SearchResult<>(pageResults, totalCount, page, pageSize, totalPages);
    }

    @Override
    public long count(InventorySearchCriteria criteria) {
        return search(criteria).size();
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private boolean matchesCriteria(Inventory inventory, InventorySearchCriteria criteria) {
        // Status filter
        if (criteria.status() != null && inventory.status() != criteria.status()) {
            return false;
        }

        // Hold code filter
        if (criteria.holdCode() != null && !criteria.holdCode().equals(inventory.holdCode())) {
            return false;
        }

        // Quantity filters
        if (criteria.minQuantity() != null &&
                inventory.onHandQuantity().isLessThan(criteria.minQuantity())) {
            return false;
        }
        if (criteria.maxQuantity() != null &&
                inventory.onHandQuantity().isGreaterThan(criteria.maxQuantity())) {
            return false;
        }

        // Exclusion filters
        if (criteria.excludeOnHold() && inventory.isOnHold()) {
            return false;
        }
        if (criteria.excludeAllocated() && inventory.allocatedQuantity().isPositive()) {
            return false;
        }
        if (criteria.onlyAvailable() && !inventory.availableQuantity().isPositive()) {
            return false;
        }

        // Expiry filters
        if (criteria.expiryBefore() != null && inventory.lottables() != null &&
                inventory.lottables().expiryDate() != null) {
            if (inventory.lottables().expiryDate().isAfter(criteria.expiryBefore())) {
                return false;
            }
        }
        if (criteria.expiryAfter() != null && inventory.lottables() != null &&
                inventory.lottables().expiryDate() != null) {
            if (inventory.lottables().expiryDate().isBefore(criteria.expiryAfter())) {
                return false;
            }
        }

        return true;
    }

    private int compareBySortCriteria(Inventory a, Inventory b, InventorySearchCriteria criteria) {
        if (criteria.sortBy() == null) {
            return 0;
        }

        int comparison = switch (criteria.sortBy()) {
            case "quantity" -> a.onHandQuantity().compareTo(b.onHandQuantity());
            case "location" -> a.locationKey().value().compareTo(b.locationKey().value());
            case "lot" -> a.lotKey().value().compareTo(b.lotKey().value());
            case "lpn" -> a.lpnKey().value().compareTo(b.lpnKey().value());
            default -> 0;
        };

        return criteria.ascending() ? comparison : -comparison;
    }
}
