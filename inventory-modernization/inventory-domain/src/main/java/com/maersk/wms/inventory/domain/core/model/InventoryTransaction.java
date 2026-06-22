package com.maersk.wms.inventory.domain.core.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;

/**
 * Inventory Transaction entity.
 * Records all inventory movements and changes for audit trail.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {

    private TransactionKey transactionKey;
    private InventoryKey inventoryKey;

    // Transaction Type
    private TransactionType transactionType;
    private String transactionCode;

    // Source/Target
    private TransactionSource sourceType;
    private String sourceKey;
    private String sourceLineNumber;

    // Inventory Details
    private SkuKey skuKey;
    private StorerKey storerKey;
    private WarehouseKey warehouseKey;

    // Location Details
    private LocationKey fromLocationKey;
    private LocationKey toLocationKey;
    private LpnKey fromLpnKey;
    private LpnKey toLpnKey;
    private LotKey lotKey;

    // Quantities
    private Quantity quantity;
    private Quantity previousQuantity;
    private Quantity newQuantity;
    private Quantity quantityBefore;  // Alias for previousQuantity
    private Quantity quantityAfter;   // Alias for newQuantity

    // Lottables (snapshot at transaction time)
    private LottableAttributes lottables;

    // Status Change
    private Inventory.InventoryStatusCode previousStatus;
    private Inventory.InventoryStatusCode newStatus;

    // Audit
    private Instant transactionDate;
    private UserKey performedBy;
    private String reason;
    private String notes;

    // Reference to related transactions (for compensation/reversal)
    private TransactionKey relatedTransactionKey;
    private String referenceKey;  // Reference to allocation key, hold code, etc.
    private boolean isReversal;

    public enum TransactionType {
        // Create Operations
        RECEIPT,            // Inventory receipt from inbound
        RETURN_RECEIPT,     // Receipt from customer return
        ADJUSTMENT_IN,      // Positive adjustment

        // Change Operations
        TRANSFER,           // Location to location movement
        STATUS_CHANGE,      // Status update
        ATTRIBUTE_CHANGE,   // Lottable/attribute update
        PACK_CHANGE,        // Pack level change

        // Remove Operations
        PICK,               // Pick for order
        SHIPMENT,           // Shipped to customer
        ADJUSTMENT_OUT,     // Negative adjustment
        WRITE_OFF,          // Write off damaged/expired

        // Allocation Operations
        ALLOCATE,           // Reserve for order
        DEALLOCATE,         // Release reservation

        // Count Operations
        COUNT_ADJUSTMENT,   // Cycle count adjustment
        PHYSICAL_COUNT,     // Physical inventory

        // Nesting Operations
        NEST,               // Add to parent container
        UNNEST,             // Remove from parent container

        // Reversal
        REVERSAL,           // Undo previous transaction

        // Hold Operations
        HOLD,               // Apply hold
        RELEASE_HOLD,       // Release hold

        // Additional Operations
        CREATE,             // Create inventory
        ADJUST,             // Adjustment (alias for ADJUSTMENT_IN/OUT)
        REMOVE,             // Remove inventory
        SHIP,               // Ship inventory
        DELETE,             // Delete inventory record
        FINALIZE,           // Period finalization
        OWNERSHIP_CHANGE    // Change storer/owner
    }

    /**
     * Transaction source types - identifies the origin/trigger of the transaction.
     */
    public enum TransactionSource {
        RECEIPT,        // Inbound receipt
        ORDER,          // Outbound order
        ADJUSTMENT,     // Manual adjustment
        TRANSFER,       // Location transfer
        COUNT,          // Cycle count
        HOLD,           // Hold operation
        PICK,           // Picking operation
        SHIPMENT,       // Shipment
        RETURN,         // Customer return
        SYSTEM,         // System-generated
        MANUAL          // Manual operation
    }

    /**
     * Check if this is a quantity increase.
     */
    public boolean isIncrease() {
        return quantity != null && quantity.isPositive();
    }

    /**
     * Check if this is a quantity decrease.
     */
    public boolean isDecrease() {
        return quantity != null && quantity.isNegative();
    }

    /**
     * Get the absolute quantity change.
     */
    public Quantity getAbsoluteQuantity() {
        if (quantity == null) return Quantity.ZERO;
        return quantity.isNegative() ?
                quantity.multiply(java.math.BigDecimal.valueOf(-1)) : quantity;
    }

    /**
     * Check if this is a movement transaction.
     */
    public boolean isMovement() {
        return transactionType == TransactionType.TRANSFER
                || transactionType == TransactionType.NEST
                || transactionType == TransactionType.UNNEST;
    }
}
