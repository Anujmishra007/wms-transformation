package com.maersk.wms.picking.domain.progression_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickDetailUpdate - tracks pick detail state changes during lifecycle.
 * Part of Progression Service bounded context.
 */
@Data
@Builder
public class PickDetailUpdate {

    private String updateId;
    private PickTaskKey pickTaskKey;
    private PickDetailKey pickDetailKey;

    // Update type
    private ProgressionEventType eventType;

    // State before/after
    private String statusBefore;
    private String statusAfter;
    private BigDecimal qtyBefore;
    private BigDecimal qtyAfter;

    // Location changes
    private String fromLocationBefore;
    private String fromLpnBefore;
    private String toLpnBefore;
    private String toLpnAfter;

    // Assignment changes
    private String assignedUserBefore;
    private String assignedUserAfter;

    // Context
    private UserKey userId;
    private DeviceKey deviceId;
    private LocalDateTime eventTime;

    // Reason (for cancellation/short/skip)
    private String reasonCode;
    private String reasonDescription;
    private String reason;

    // Generic before/after values for tracking changes
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;

    // Business methods
    public boolean isStatusChange() {
        return statusBefore != null && !statusBefore.equals(statusAfter);
    }

    public boolean isQuantityChange() {
        return qtyBefore != null && qtyBefore.compareTo(qtyAfter) != 0;
    }
}
