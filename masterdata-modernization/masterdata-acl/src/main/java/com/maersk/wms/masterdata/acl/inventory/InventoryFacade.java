package com.maersk.wms.masterdata.acl.inventory;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * ACL facade for Inventory Service communication.
 * Provides master data queries needed by inventory operations.
 */
public interface InventoryFacade {

    /**
     * Get current inventory quantity for SKU at location.
     */
    Optional<BigDecimal> getInventoryQuantity(SkuKey skuKey, LocationKey locationKey);

    /**
     * Check if location has any inventory.
     */
    boolean hasInventory(LocationKey locationKey);

    /**
     * Get inventory count by SKU across all locations.
     */
    BigDecimal getTotalInventoryBySku(SkuKey skuKey);

    /**
     * Get inventory count for storer.
     */
    BigDecimal getTotalInventoryByStorer(StorerKey storerKey);

    /**
     * Check if SKU is allocated.
     */
    boolean isSkuAllocated(SkuKey skuKey);

    /**
     * Check if location is allocated.
     */
    boolean isLocationAllocated(LocationKey locationKey);
}
