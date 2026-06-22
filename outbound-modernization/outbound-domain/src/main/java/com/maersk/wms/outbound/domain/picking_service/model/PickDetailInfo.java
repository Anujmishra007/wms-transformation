package com.maersk.wms.outbound.domain.picking_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickDetailInfo - read model for pick detail information.
 * Optimized for RDT device display and audit/tracking.
 * Part of Picking Operations Service bounded context.
 */
@Data
@Builder
public class PickDetailInfo {

    // Keys
    private PickDetailKey pickDetailKey;
    private PickHeaderKey pickHeaderKey;
    private OrderKey orderKey;
    private WaveKey waveKey;

    // SKU display info
    private String sku;
    private String skuDescription;
    private String skuBarcode;
    private String packKey;
    private String uom;

    // Location display info
    private String fromLocation;
    private String fromLpn;
    private String toLocation;
    private String toLpn;
    private String zone;
    private String aisle;
    private int pickSequence;

    // Quantity info
    private BigDecimal qtyToPick;
    private BigDecimal qtyPicked;
    private BigDecimal qtyShorted;
    private BigDecimal qtyRemaining;

    // Status
    private String status;
    private String statusDescription;

    // Assignment
    private String assignedUser;
    private String assignedEquipment;

    // Timing
    private LocalDateTime releaseTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;

    // Order info (denormalized for display)
    private String orderNumber;
    private String consigneeName;
    private int orderPriority;

    // Lot info
    private String lot;
    private LocalDateTime expirationDate;

    // Business methods
    public boolean isComplete() {
        return qtyRemaining.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean hasShort() {
        return qtyShorted.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getProgressDisplay() {
        return String.format("%s / %s %s",
                qtyPicked.stripTrailingZeros().toPlainString(),
                qtyToPick.stripTrailingZeros().toPlainString(),
                uom);
    }
}
