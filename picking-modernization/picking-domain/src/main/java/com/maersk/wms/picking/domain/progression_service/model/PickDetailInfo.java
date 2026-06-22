package com.maersk.wms.picking.domain.progression_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickDetailInfo - read model for pick detail information and audit.
 * Part of Progression Service bounded context.
 */
@Data
@Builder
public class PickDetailInfo {

    // Keys
    private PickTaskKey pickTaskKey;
    private PickDetailKey pickDetailKey;
    private PickListKey pickListKey;
    private OrderKey orderKey;
    private WaveKey waveKey;

    // SKU display info
    private String sku;
    private String skuDescription;
    private String skuBarcode;
    private String packKey;
    private String uom;

    // Location info
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
    private String assignedDevice;

    // Order context
    private String orderNumber;
    private String consigneeName;
    private int orderPriority;

    // Timing
    private LocalDateTime createdTime;
    private LocalDateTime releasedTime;
    private LocalDateTime assignedTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private long totalDurationMs;

    // Audit counts
    private int updateCount;
    private int shortCount;
    private int reassignCount;

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

    public double getShortPercentage() {
        if (qtyToPick.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return qtyShorted.divide(qtyToPick, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}
