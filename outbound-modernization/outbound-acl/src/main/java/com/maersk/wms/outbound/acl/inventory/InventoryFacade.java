package com.maersk.wms.outbound.acl.inventory;

import com.maersk.wms.outbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Facade for Inventory Service integration.
 * Anti-Corruption Layer for cross-service inventory operations.
 */
public interface InventoryFacade {

    /**
     * Gets available inventory for allocation.
     */
    List<AvailableInventory> getAvailableInventory(SkuKey sku, StorerKey storerKey,
                                                    AllocationCriteria criteria);

    /**
     * Reserves inventory for allocation.
     */
    ReservationResult reserveInventory(SkuKey sku, LocationKey location, LpnKey lpn,
                                        BigDecimal quantity, String allocationKey);

    /**
     * Releases reserved inventory.
     */
    void releaseReservation(String reservationKey);

    /**
     * Confirms inventory pick (reduces on-hand).
     */
    void confirmPick(String reservationKey, BigDecimal qtyPicked);

    /**
     * Gets inventory balance at a location.
     */
    Optional<InventoryBalance> getBalance(LocationKey location, LpnKey lpn, SkuKey sku);

    /**
     * Available inventory record.
     */
    record AvailableInventory(
            LocationKey location,
            LpnKey lpn,
            String lot,
            BigDecimal availableQty,
            BigDecimal allocatedQty,
            java.time.LocalDateTime receiptDate,
            java.time.LocalDateTime expirationDate,
            String zone,
            String aisle,
            int pickSequence
    ) {}

    /**
     * Criteria for finding available inventory.
     */
    record AllocationCriteria(
            List<String> preferredZones,
            List<String> excludedZones,
            boolean respectFifo,
            boolean respectFefo,
            java.util.Map<String, String> lottableConstraints,
            int maxLocations
    ) {}

    /**
     * Result of inventory reservation.
     */
    record ReservationResult(
            boolean success,
            String reservationKey,
            BigDecimal reservedQty,
            String message
    ) {}

    /**
     * Inventory balance.
     */
    record InventoryBalance(
            SkuKey sku,
            LocationKey location,
            LpnKey lpn,
            String lot,
            BigDecimal onHand,
            BigDecimal allocated,
            BigDecimal available
    ) {}
}
