package com.maersk.wms.picking.domain.shorts_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ShortPickRecord - records short pick events for analysis and resolution.
 * Part of Shorts Service bounded context.
 */
@Data
@Builder
public class ShortPickRecord {

    private String shortRecordId;
    private PickTaskKey pickTaskKey;
    private PickDetailKey pickDetailKey;
    private OrderKey orderKey;

    // SKU info
    private SkuKey sku;
    private String skuDescription;

    // Location info
    private LocationKey location;
    private LpnKey lpn;
    private String lot;
    private String zone;

    // Quantities
    private BigDecimal expectedQty;
    private BigDecimal pickedQty;
    private BigDecimal shortedQty;

    // Reason
    private ShortReasonCode reasonCode;
    private String notes;

    // Resolution
    private ShortResolutionStatus resolutionStatus;
    private ShortResolutionAction resolutionAction;
    private String resolutionNotes;
    private LocalDateTime resolvedTime;
    private UserKey resolvedBy;

    // Verification
    private boolean requiresVerification;
    private boolean verified;
    private UserKey verifiedBy;
    private LocalDateTime verifiedTime;
    private String verificationNotes;

    // Supervisor Approval
    private UserKey approvedBy;
    private LocalDateTime approvalTime;
    private String approvalNotes;

    // Backorder
    private String backorderReference;

    // Context
    private UserKey recordedBy;
    private DeviceKey deviceId;
    private LocalDateTime recordedTime;

    // Impact tracking
    private boolean orderImpacted;
    private boolean replenishmentTriggered;
    private String reallocatedFrom;

    // Business methods
    public boolean isResolved() {
        return resolutionStatus == ShortResolutionStatus.RESOLVED ||
               resolutionStatus == ShortResolutionStatus.CANCELLED;
    }

    public boolean isPending() {
        return resolutionStatus == ShortResolutionStatus.PENDING ||
               resolutionStatus == ShortResolutionStatus.IN_PROGRESS;
    }

    public BigDecimal getShortPercentage() {
        if (expectedQty == null || expectedQty.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return shortedQty.divide(expectedQty, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public void verify(UserKey verifier, boolean confirmed, String notes) {
        this.verified = confirmed;
        this.verifiedBy = verifier;
        this.verifiedTime = LocalDateTime.now();
        this.verificationNotes = notes;
        if (confirmed) {
            this.resolutionStatus = ShortResolutionStatus.VERIFIED;
        }
    }

    public void resolve(ShortResolutionAction action, String notes) {
        this.resolutionAction = action;
        this.resolutionNotes = notes;
        this.resolvedTime = LocalDateTime.now();
        this.resolutionStatus = ShortResolutionStatus.RESOLVED;
    }
}
