package com.maersk.wms.picking.acl.replenishment;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import com.maersk.wms.picking.shared.kernel.valueobjects.Quantity;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Replenishment Service integration.
 * Handles replenishment triggers from short pick scenarios.
 */
public interface ReplenishmentFacade {

    // Replenishment Triggers
    String triggerReplenishment(LocationKey location, SkuKey sku, Quantity requiredQty, String priority);
    String triggerUrgentReplenishment(LocationKey location, SkuKey sku, Quantity requiredQty, String pickDetailKey);
    void cancelReplenishment(String replenishmentId, String reason);

    // Query
    Optional<ReplenishmentInfo> getReplenishmentStatus(String replenishmentId);
    List<ReplenishmentInfo> getPendingReplenishments(LocationKey location);
    List<ReplenishmentInfo> getPendingReplenishmentsBySku(SkuKey sku);
    boolean hasActiveReplenishment(LocationKey location, SkuKey sku);

    // Notifications
    void subscribeToReplenishmentComplete(String replenishmentId, ReplenishmentCallback callback);

    /**
     * Replenishment information DTO.
     */
    record ReplenishmentInfo(
            String replenishmentId,
            LocationKey targetLocation,
            SkuKey sku,
            Quantity requestedQty,
            Quantity replenishedQty,
            String status,
            String priority,
            java.time.LocalDateTime requestedTime,
            java.time.LocalDateTime completedTime
    ) {}

    /**
     * Callback for replenishment completion.
     */
    @FunctionalInterface
    interface ReplenishmentCallback {
        void onReplenishmentComplete(String replenishmentId, boolean success, Quantity replenishedQty);
    }
}
