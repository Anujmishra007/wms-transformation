package com.maersk.wms.picking.domain.cancellation_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickCancellation - records pick cancellation events.
 * Part of Cancellation Service bounded context.
 */
@Data
@Builder
public class PickCancellation {

    private String cancellationId;
    private PickTaskKey taskKey;
    private PickDetailKey pickDetailKey;
    private PickListKey listKey;
    private OrderKey orderKey;
    private WaveKey waveKey;

    // Cancellation scope
    private CancellationScope scope;
    private CancellationReasonCode reasonCode;
    private String notes;

    // State at cancellation
    private String statusBeforeCancellation;
    private BigDecimal qtyBeforeCancellation;
    private BigDecimal qtyCancelled;
    private String assignedUserBefore;

    // Inventory impact
    private boolean inventoryReleased;
    private boolean replenishmentAdjusted;
    private boolean allocationReversed;

    // Order impact
    private boolean orderLineImpacted;
    private boolean orderCancelled;

    // Approval workflow
    private CancellationApprovalStatus approvalStatus;
    private UserKey approvedBy;
    private UserKey rejectedBy;
    private LocalDateTime approvalTime;
    private String approvalNotes;
    private String rejectionReason;

    // Context
    private UserKey requestedBy;
    private DeviceKey deviceId;
    private LocalDateTime requestedTime;
    private LocalDateTime executedTime;

    // Rollback
    private boolean rolledBack;
    private String rollbackReason;
    private LocalDateTime rollbackTime;

    // Business methods
    public boolean isApproved() {
        return approvalStatus == CancellationApprovalStatus.NOT_REQUIRED ||
               approvalStatus == CancellationApprovalStatus.APPROVED;
    }

    public boolean isPending() {
        return approvalStatus == CancellationApprovalStatus.PENDING;
    }

    public boolean canExecute() {
        return isApproved() && executedTime == null;
    }

    public void approve(UserKey approver, String notes) {
        this.approvedBy = approver;
        this.approvalStatus = CancellationApprovalStatus.APPROVED;
        this.approvalTime = LocalDateTime.now();
        this.approvalNotes = notes;
    }

    public void reject(UserKey rejecter, String reason) {
        this.rejectedBy = rejecter;
        this.approvalStatus = CancellationApprovalStatus.REJECTED;
        this.rejectionReason = reason;
        this.approvalTime = LocalDateTime.now();
    }
}
