package com.maersk.wms.inventory.domain.lifecycle.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import com.maersk.wms.inventory.domain.core.model.Inventory;

import lombok.*;
import java.time.Instant;
import java.util.Map;

/**
 * Entity representing an inventory change request.
 * Handles quantity updates, status updates, attribute updates, and ownership changes.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryChange {

    private String changeKey;
    private InventoryKey inventoryKey;

    // Change Type
    private ChangeType changeType;
    private String changeReason;

    // Source Information
    private String sourceType;
    private String sourceKey;

    // Quantity Change (for QUANTITY_UPDATE)
    private Quantity previousQuantity;
    private Quantity newQuantity;
    private Quantity adjustmentQuantity;

    // Status Change (for STATUS_UPDATE)
    private Inventory.InventoryStatusCode previousStatus;
    private Inventory.InventoryStatusCode newStatus;
    private String previousHoldCode;
    private String newHoldCode;

    // Attribute Change (for ATTRIBUTE_UPDATE)
    private LottableAttributes previousLottables;
    private LottableAttributes newLottables;
    private Map<String, String> changedAttributes;

    // Ownership Change (for OWNERSHIP_CHANGE)
    private StorerKey previousStorerKey;
    private StorerKey newStorerKey;

    // Location Change (for transfer - handled separately)
    private LocationKey previousLocationKey;
    private LocationKey newLocationKey;

    // Status
    private ChangeStatus status;
    private String errorMessage;

    // Approval (for changes requiring approval)
    private boolean requiresApproval;
    private ApprovalStatus approvalStatus;
    private UserKey approvedBy;
    private Instant approvedAt;
    private String approvalNotes;

    // Audit
    private Instant requestedAt;
    private UserKey requestedBy;
    private Instant completedAt;

    public enum ChangeType {
        QUANTITY_UPDATE,        // Adjust quantity
        STATUS_UPDATE,          // Change status
        ATTRIBUTE_UPDATE,       // Update lottables/attributes
        OWNERSHIP_CHANGE,       // Change storer
        HOLD_APPLY,             // Apply hold
        HOLD_RELEASE,           // Release hold
        PACK_CHANGE,            // Change pack level
        UOM_CONVERSION          // Unit of measure conversion
    }

    public enum ChangeStatus {
        PENDING,
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public enum ApprovalStatus {
        NOT_REQUIRED,
        PENDING,
        APPROVED,
        REJECTED
    }

    /**
     * Check if change requires approval based on business rules.
     */
    public boolean needsApproval() {
        // Quantity adjustments above threshold or ownership changes typically need approval
        return requiresApproval;
    }

    /**
     * Approve the change.
     */
    public void approve(UserKey approver, String notes) {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = Instant.now();
        this.approvalNotes = notes;
        this.status = ChangeStatus.APPROVED;
    }

    /**
     * Reject the change.
     */
    public void reject(UserKey rejector, String reason) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.approvedBy = rejector;
        this.approvedAt = Instant.now();
        this.approvalNotes = reason;
        this.status = ChangeStatus.REJECTED;
    }

    /**
     * Complete the change.
     */
    public void complete() {
        this.status = ChangeStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * Fail the change.
     */
    public void fail(String error) {
        this.status = ChangeStatus.FAILED;
        this.errorMessage = error;
        this.completedAt = Instant.now();
    }
}
