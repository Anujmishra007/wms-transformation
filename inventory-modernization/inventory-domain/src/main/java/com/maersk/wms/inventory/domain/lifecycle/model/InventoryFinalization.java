package com.maersk.wms.inventory.domain.lifecycle.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import com.maersk.wms.inventory.domain.core.model.InventorySnapshot;

import lombok.*;
import java.time.Instant;
import java.util.List;

/**
 * Entity representing inventory finalization.
 * Handles transaction completion, stock reconciliation, and event publication.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFinalization {

    private String finalizationKey;

    // Finalization Type
    private FinalizationType finalizationType;
    private String finalizationReason;

    // Scope
    private FinalizationScope scope;
    private WarehouseKey warehouseKey;
    private StorerKey storerKey;
    private LocationKey locationKey;
    private List<InventoryKey> inventoryKeys;

    // For transaction completion
    private TransactionKey transactionKey;
    private List<TransactionKey> relatedTransactionKeys;

    // For service compatibility
    private String period;
    private InventorySnapshot.SnapshotType snapshotType;
    private String notes;

    // For reconciliation
    private Quantity expectedQuantity;
    private Quantity actualQuantity;
    private Quantity varianceQuantity;

    // Status
    private FinalizationStatus status;
    private String errorMessage;

    // Events to publish
    @Builder.Default
    private List<String> pendingEvents = List.of();
    private boolean eventsPublished;

    // Audit
    private Instant requestedAt;
    private UserKey requestedBy;
    private Instant completedAt;

    public enum FinalizationType {
        TRANSACTION_COMMIT,     // Commit a single transaction
        BATCH_COMMIT,           // Commit multiple transactions
        RECEIPT_FINALIZE,       // Finalize receipt processing
        SHIPMENT_FINALIZE,      // Finalize shipment
        COUNT_FINALIZE,         // Finalize inventory count
        RECONCILIATION,         // Stock reconciliation
        PERIOD_CLOSE,           // Period-end close
        DAY_END_CLOSE           // End of day close
    }

    public enum FinalizationScope {
        SINGLE_INVENTORY,       // Single inventory record
        TRANSACTION,            // Single transaction
        BATCH,                  // Batch of transactions
        LOCATION,               // All inventory at location
        STORER,                 // All inventory for storer
        WAREHOUSE,              // Entire warehouse
        RECEIPT,                // All inventory from receipt
        ORDER                   // All inventory for order
    }

    public enum FinalizationStatus {
        PENDING,
        VALIDATING,
        RECONCILING,
        COMMITTING,
        PUBLISHING_EVENTS,
        COMPLETED,
        FAILED,
        ROLLED_BACK
    }

    /**
     * Check if there's a variance.
     */
    public boolean hasVariance() {
        return varianceQuantity != null && !varianceQuantity.isZero();
    }

    /**
     * Complete finalization.
     */
    public void complete() {
        this.status = FinalizationStatus.COMPLETED;
        this.eventsPublished = true;
        this.completedAt = Instant.now();
    }

    /**
     * Fail finalization.
     */
    public void fail(String error) {
        this.status = FinalizationStatus.FAILED;
        this.errorMessage = error;
        this.completedAt = Instant.now();
    }

    /**
     * Rollback finalization.
     */
    public void rollback(String reason) {
        this.status = FinalizationStatus.ROLLED_BACK;
        this.errorMessage = reason;
        this.completedAt = Instant.now();
    }

    /**
     * Add event to publish.
     */
    public void addPendingEvent(String eventType) {
        this.pendingEvents = new java.util.ArrayList<>(this.pendingEvents);
        this.pendingEvents.add(eventType);
    }

    // ═══════════════════════════════════════════════════════════════
    // CONVENIENCE CONSTRUCTOR (for service compatibility)
    // ═══════════════════════════════════════════════════════════════

    public InventoryFinalization(List<InventoryKey> inventoryKeys, String period,
                                  InventorySnapshot.SnapshotType snapshotType, String notes,
                                  UserKey finalizedBy) {
        this.inventoryKeys = inventoryKeys;
        this.period = period;
        this.snapshotType = snapshotType;
        this.notes = notes;
        this.requestedBy = finalizedBy;
        this.requestedAt = Instant.now();
        this.pendingEvents = List.of();
    }

    // ═══════════════════════════════════════════════════════════════
    // RECORD-STYLE ACCESSORS (for DDD compatibility)
    // ═══════════════════════════════════════════════════════════════

    public List<InventoryKey> inventoryKeys() { return inventoryKeys; }
    public String period() { return period; }
    public InventorySnapshot.SnapshotType snapshotType() { return snapshotType; }
    public String notes() { return notes; }
    public UserKey finalizedBy() { return requestedBy; }
    public FinalizationType finalizationType() { return finalizationType; }
    public WarehouseKey warehouseKey() { return warehouseKey; }
}
