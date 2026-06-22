package com.maersk.wms.picking.acl.inventory;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import com.maersk.wms.picking.shared.kernel.valueobjects.Quantity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Inventory Service integration.
 * Translates picking domain concepts to inventory service calls.
 */
public interface InventoryFacade {

    // Inventory Queries
    Optional<InventoryInfo> getInventoryAtLocation(LocationKey location, SkuKey sku);
    Optional<InventoryInfo> getInventoryByLpn(LpnKey lpn);
    BigDecimal getAvailableQuantity(LocationKey location, SkuKey sku);
    BigDecimal getAllocatedQuantity(LocationKey location, SkuKey sku);
    List<InventoryInfo> getInventoryBySku(SkuKey sku);

    // Pick Consumption
    void consumeInventory(LocationKey location, LpnKey lpn, SkuKey sku, Quantity quantity, String pickDetailKey);
    void reserveInventory(LocationKey location, LpnKey lpn, SkuKey sku, Quantity quantity, String allocationKey);
    void releaseReservation(String allocationKey);

    // Short Handling
    void reportShortage(LocationKey location, SkuKey sku, Quantity shortQty, String reason);
    boolean isInventoryAvailableElsewhere(SkuKey sku, Quantity requiredQty);
    List<LocationKey> findAlternateLocations(SkuKey sku, Quantity requiredQty);

    // LPN Operations
    void updateLpnLocation(LpnKey lpn, LocationKey newLocation);
    void splitLpn(LpnKey sourceLpn, LpnKey targetLpn, SkuKey sku, Quantity quantity);
    LpnKey createChildLpn(LpnKey parentLpn, SkuKey sku, Quantity quantity);

    // Validation
    boolean validateInventoryExists(LocationKey location, LpnKey lpn, SkuKey sku);
    boolean validateQuantityAvailable(LocationKey location, LpnKey lpn, SkuKey sku, Quantity quantity);

    /**
     * Inventory information DTO.
     */
    record InventoryInfo(
            LocationKey location,
            LpnKey lpn,
            SkuKey sku,
            BigDecimal onHandQty,
            BigDecimal allocatedQty,
            BigDecimal availableQty,
            String lotNumber,
            String status
    ) {}
}
