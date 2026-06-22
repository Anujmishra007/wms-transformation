package com.maersk.wms.inventory.api.dto;

import com.maersk.wms.inventory.domain.InventoryAdjustment;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AdjustmentResponse {
    private String adjustmentKey;
    private String status;
    private BigDecimal variance;
    private boolean requiresApproval;

    public static AdjustmentResponse from(InventoryAdjustment adj) {
        return AdjustmentResponse.builder()
                .adjustmentKey(adj.getAdjustmentKey())
                .status(adj.getStatus().name())
                .variance(adj.getVariance())
                .requiresApproval(adj.getStatus() == com.maersk.wms.inventory.domain.AdjustmentStatus.PENDING_APPROVAL)
                .build();
    }
}
