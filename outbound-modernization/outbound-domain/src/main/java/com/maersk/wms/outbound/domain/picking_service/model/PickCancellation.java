package com.maersk.wms.outbound.domain.picking_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickCancellation - records pick cancellation events.
 * Part of Picking Operations Service - Cancel Handling module.
 */
@Data
@Builder
public class PickCancellation {

    private String cancellationId;
    private PickDetailKey pickDetailKey;
    private PickHeaderKey pickHeaderKey;

    // Cancellation details
    private CancellationReasonCode reasonCode;
    private String reasonDescription;
    private CancellationScope scope;

    // Quantities
    private BigDecimal qtyBeforeCancellation;
    private BigDecimal qtyCancelled;

    // Status before cancellation
    private String statusBeforeCancellation;

    // Inventory impact
    private boolean inventoryReleased;
    private boolean replenishmentTriggered;

    // Context
    private String deviceId;
    private String userId;
    private LocalDateTime cancellationTime;

    // Approval (if required)
    private boolean approvalRequired;
    private String approvedBy;
    private LocalDateTime approvedTime;

    // Business methods
    public boolean isApproved() {
        return !approvalRequired || approvedBy != null;
    }
}
