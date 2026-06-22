package com.maersk.wms.masterdata.acl.outbound;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

/**
 * ACL facade for Outbound Service communication.
 * Provides master data queries needed by shipping operations.
 */
public interface OutboundFacade {

    /**
     * Check if storer has pending orders.
     */
    boolean storerHasPendingOrders(StorerKey storerKey);

    /**
     * Check if dock is assigned to shipping.
     */
    boolean dockHasActiveShipping(DockKey dockKey);

    /**
     * Get pending shipping count for dock.
     */
    int getPendingShippingCount(DockKey dockKey);

    /**
     * Check if SKU has open allocations.
     */
    boolean skuHasOpenAllocations(SkuKey skuKey);

    /**
     * Check if location has picks.
     */
    boolean locationHasPendingPicks(LocationKey locationKey);
}
