package com.maersk.wms.task.acl.inventory;

import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Facade interface for Inventory Service integration.
 * Used by Task Management for inventory-related task context.
 */
public interface InventoryFacade {

    /**
     * Get inventory details for an LPN.
     */
    Optional<InventoryDetails> getInventoryByLpn(LpnKey lpn);

    /**
     * Get inventory at a location.
     */
    List<InventoryDetails> getInventoryAtLocation(LocationKey location);

    /**
     * Get location details.
     */
    Optional<LocationInfo> getLocationInfo(LocationKey location);

    /**
     * Check if location is available for putaway.
     */
    boolean isLocationAvailable(LocationKey location);

    /**
     * Notify inventory of task completion.
     */
    void notifyTaskCompleted(String taskKey, LpnKey lpn, LocationKey location, BigDecimal quantity);

    /**
     * Record for inventory details.
     */
    record InventoryDetails(
            LpnKey lpn,
            SkuKey sku,
            LocationKey location,
            BigDecimal quantity,
            String status,
            String lotNumber
    ) {}

    /**
     * Record for location info.
     */
    record LocationInfo(
            LocationKey location,
            String zone,
            String aisle,
            String bay,
            String level,
            String locationType,
            boolean available,
            boolean pickable
    ) {}
}
