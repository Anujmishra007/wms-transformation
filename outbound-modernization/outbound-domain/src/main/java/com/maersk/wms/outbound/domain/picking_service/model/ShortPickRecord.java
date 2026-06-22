package com.maersk.wms.outbound.domain.picking_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.SkuKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ShortPickRecord - records short pick events for analysis.
 * Part of Picking Operations Service - Shorts Handling module.
 */
@Data
@Builder
public class ShortPickRecord {

    private String shortPickId;
    private PickDetailKey pickDetailKey;

    // SKU info
    private SkuKey sku;
    private String skuDescription;

    // Location info
    private LocationKey location;
    private LpnKey lpn;

    // Quantities
    private BigDecimal expectedQty;
    private BigDecimal actualQty;
    private BigDecimal shortQty;

    // Reason
    private ShortReasonCode reasonCode;
    private String reasonDescription;

    // Resolution
    private ShortResolutionStatus resolutionStatus;
    private String resolutionAction;
    private LocalDateTime resolvedTime;
    private String resolvedBy;

    // Context
    private String deviceId;
    private String userId;
    private LocalDateTime shortTime;

    // Business methods
    public boolean isResolved() {
        return resolutionStatus == ShortResolutionStatus.RESOLVED ||
               resolutionStatus == ShortResolutionStatus.CANCELLED;
    }

    public BigDecimal getShortPercentage() {
        if (expectedQty.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return shortQty.divide(expectedQty, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
