package com.maersk.wms.outbound.domain.picking_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickDetailUpdate - tracks pick detail changes during picking.
 * Part of Picking Operations Service bounded context.
 */
@Data
@Builder
public class PickDetailUpdate {

    private String updateId;
    private PickDetailKey pickDetailKey;

    // Update type
    private PickUpdateType updateType;

    // Quantities
    private BigDecimal qtyBefore;
    private BigDecimal qtyAfter;
    private BigDecimal qtyChange;

    // Location changes
    private LocationKey fromLocationBefore;
    private LocationKey fromLocationAfter;
    private LpnKey fromLpnBefore;
    private LpnKey fromLpnAfter;

    // Status changes
    private String statusBefore;
    private String statusAfter;

    // Context
    private String reason;
    private String deviceId;
    private String userId;
    private LocalDateTime updateTime;

    // Business methods
    public boolean isQuantityChange() {
        return updateType == PickUpdateType.QUANTITY_CHANGE ||
               updateType == PickUpdateType.PARTIAL_PICK ||
               updateType == PickUpdateType.SHORT_PICK;
    }

    public boolean isStatusChange() {
        return updateType == PickUpdateType.STATUS_CHANGE;
    }
}
