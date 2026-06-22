package com.maersk.wms.inbound.acl.putaway;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import java.util.List;
import java.util.Optional;

/**
 * Facade interface for Putaway subdomain.
 *
 * Used by Receiving and Returns subdomains to trigger putaway
 * and get location suggestions without direct coupling.
 */
public interface PutawayFacade {

    /**
     * Request putaway location suggestion for an LPN.
     */
    Optional<LocationSuggestion> suggestLocation(LocationRequest request);

    /**
     * Create putaway task for an LPN.
     */
    String createPutawayTask(CreatePutawayTaskRequest request);

    /**
     * Get putaway status for a receipt.
     */
    PutawayStatus getPutawayStatus(ReceiptKey receiptKey);

    /**
     * Check if all items from a receipt have been put away.
     */
    boolean isReceiptFullyPutAway(ReceiptKey receiptKey);

    /**
     * Get pending putaway tasks for a receipt.
     */
    List<PutawayTaskSummary> getPendingTasks(ReceiptKey receiptKey);

    /**
     * Request for location suggestion.
     */
    record LocationRequest(
            LpnKey lpn,
            SkuKey sku,
            Quantity quantity,
            String packKey,
            String zone,           // Optional zone restriction
            String strategyKey,    // Putaway strategy to use
            boolean isReturn,      // Is this a return item?
            String disposition     // Return disposition (if applicable)
    ) {}

    /**
     * Suggested putaway location.
     */
    record LocationSuggestion(
            LocationKey location,
            String zone,
            String aisle,
            String bay,
            String level,
            String locationType,
            double score,          // How good is this suggestion (0-100)
            String reason          // Why this location was chosen
    ) {}

    /**
     * Request to create a putaway task.
     */
    record CreatePutawayTaskRequest(
            ReceiptKey receiptKey,
            LpnKey lpn,
            SkuKey sku,
            Quantity quantity,
            LocationKey fromLocation,
            LocationKey toLocation,
            String priority,
            boolean isDirected     // Is this a directed putaway?
    ) {}

    /**
     * Overall putaway status for a receipt.
     */
    record PutawayStatus(
            ReceiptKey receiptKey,
            int totalTasks,
            int completedTasks,
            int inProgressTasks,
            int pendingTasks,
            double percentComplete
    ) {}

    /**
     * Summary of a putaway task.
     */
    record PutawayTaskSummary(
            String taskKey,
            LpnKey lpn,
            String sku,
            Quantity quantity,
            LocationKey fromLocation,
            LocationKey toLocation,
            String status,
            String assignedTo
    ) {}
}
