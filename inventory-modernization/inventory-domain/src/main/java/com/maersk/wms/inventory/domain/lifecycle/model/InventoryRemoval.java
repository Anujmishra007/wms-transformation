package com.maersk.wms.inventory.domain.lifecycle.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import com.maersk.wms.inventory.domain.core.model.InventoryTransaction;

import lombok.*;
import java.time.Instant;

/**
 * Entity representing an inventory removal request.
 * Handles consumption, deallocation, shipment depletion, and cleanup.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRemoval {

    private String removalKey;
    private InventoryKey inventoryKey;

    // Removal Type
    private RemovalType removalType;
    private String removalReason;

    // Source Information
    private InventoryTransaction.TransactionSource sourceType;
    private String sourceKey;
    private String sourceLineNumber;

    // Inventory Details
    private SkuKey skuKey;
    private LotKey lotKey;
    private LocationKey locationKey;
    private LpnKey lpnKey;
    private StorerKey storerKey;
    private WarehouseKey warehouseKey;

    // Quantity
    private Quantity removalQuantity;
    private Quantity previousOnHand;
    private Quantity newOnHand;
    private boolean completeRemoval; // Remove entire inventory record

    // For allocation-based removal
    private AllocationKey allocationKey;

    // For shipment-based removal
    private OrderKey orderKey;
    private String shipmentKey;

    // Status
    private RemovalStatus status;
    private String errorMessage;

    // Audit
    private Instant requestedAt;
    private UserKey requestedBy;
    private Instant completedAt;

    public enum RemovalType {
        PICK,               // Pick for order fulfillment
        SHIPMENT,           // Ship to customer
        ADJUSTMENT_OUT,     // Negative adjustment
        WRITE_OFF,          // Write off (damaged, expired)
        TRANSFER_OUT,       // Transfer to another warehouse
        CONSUMPTION,        // Internal consumption
        RETURN_TO_VENDOR,   // Return to supplier
        DISPOSAL            // Dispose of inventory
    }

    public enum RemovalStatus {
        PENDING,
        VALIDATING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        PARTIALLY_COMPLETED
    }

    /**
     * Check if removal should delete the entire inventory record.
     */
    public boolean shouldDeleteRecord() {
        return completeRemoval ||
               (newOnHand != null && newOnHand.isZero());
    }

    /**
     * Complete the removal.
     */
    public void complete() {
        this.status = RemovalStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * Fail the removal.
     */
    public void fail(String error) {
        this.status = RemovalStatus.FAILED;
        this.errorMessage = error;
        this.completedAt = Instant.now();
    }

    /**
     * Partially complete (for short picks).
     */
    public void partialComplete(Quantity actualRemoved) {
        this.status = RemovalStatus.PARTIALLY_COMPLETED;
        this.newOnHand = previousOnHand.subtract(actualRemoved);
        this.completedAt = Instant.now();
    }

    // ═══════════════════════════════════════════════════════════════
    // RECORD-STYLE ACCESSORS (for DDD compatibility)
    // ═══════════════════════════════════════════════════════════════

    public InventoryKey inventoryKey() { return inventoryKey; }
    public Quantity quantity() { return removalQuantity; }
    public String reason() { return removalReason; }
    public InventoryTransaction.TransactionSource removalSource() { return sourceType; }
    public String sourceKey() { return sourceKey; }
    public UserKey removedBy() { return requestedBy; }
    public RemovalType removalType() { return removalType; }
    public SkuKey skuKey() { return skuKey; }
    public LocationKey locationKey() { return locationKey; }
    public LpnKey lpnKey() { return lpnKey; }
    public AllocationKey allocationKey() { return allocationKey; }
}
