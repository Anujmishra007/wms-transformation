package com.maersk.wms.inventory.domain.lifecycle.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Entity representing an inventory creation request.
 * Used for receiving, returns, and initial stock registration.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCreation {

    private String creationKey;

    // Source Information
    private CreationSource source;
    private String sourceKey;          // Receipt key, return key, etc.
    private String sourceLineNumber;

    // Inventory Identity
    private SkuKey skuKey;
    private LotKey lotKey;
    private LocationKey locationKey;
    private LpnKey lpnKey;
    private StorerKey storerKey;
    private WarehouseKey warehouseKey;

    // Quantities
    private Quantity quantity;
    private String uom;

    // Lottables
    private LottableAttributes lottables;

    // Pack Information
    private String packKey;
    private String packLevel;

    // Receipt Information
    private ReceiptKey receiptKey;
    private LocalDateTime receiptDate;

    // Status
    private CreationStatus status;
    private String errorMessage;

    // Audit
    private Instant requestedAt;
    private UserKey requestedBy;
    private Instant completedAt;

    public enum CreationSource {
        RECEIPT,            // Inbound receipt
        RETURN_RECEIPT,     // Customer return
        CROSSDOCK,          // Crossdocking
        INITIAL_STOCK,      // Initial stock load
        ADJUSTMENT,         // Positive adjustment
        TRANSFER_IN,        // Transfer from another warehouse
        PRODUCTION          // Manufacturing output
    }

    public enum CreationStatus {
        PENDING,
        VALIDATING,
        CREATING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    // ═══════════════════════════════════════════════════════════════
    // RECORD-STYLE ACCESSORS (for DDD compatibility)
    // ═══════════════════════════════════════════════════════════════

    public SkuKey skuKey() { return skuKey; }
    public LotKey lotKey() { return lotKey; }
    public LocationKey locationKey() { return locationKey; }
    public LpnKey lpnKey() { return lpnKey; }
    public StorerKey storerKey() { return storerKey; }
    public WarehouseKey warehouseKey() { return warehouseKey; }
    public Quantity quantity() { return quantity; }
    public LottableAttributes lottables() { return lottables; }
    public CreationSource source() { return source; }
    public String sourceKey() { return sourceKey; }
    public ReceiptKey receiptKey() { return receiptKey; }
    public UserKey createdBy() { return requestedBy; }
    public UserKey requestedBy() { return requestedBy; }
    public String returnKey() { return sourceKey; }  // For returns, sourceKey is the return key

    /**
     * Mark creation as completed.
     */
    public void complete(InventoryKey inventoryKey) {
        this.status = CreationStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * Mark creation as failed.
     */
    public void fail(String error) {
        this.status = CreationStatus.FAILED;
        this.errorMessage = error;
        this.completedAt = Instant.now();
    }

    /**
     * Validate creation request.
     */
    public boolean isValid() {
        return skuKey != null
                && locationKey != null
                && lpnKey != null
                && quantity != null
                && quantity.isPositive()
                && warehouseKey != null;
    }
}
