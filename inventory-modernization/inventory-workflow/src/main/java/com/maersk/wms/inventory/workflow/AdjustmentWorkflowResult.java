package com.maersk.wms.inventory.workflow;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AdjustmentWorkflowResult {
    private String adjustmentKey;
    private boolean success;
    private BigDecimal adjustedQty;
    private BigDecimal variance;
    private String errorMessage;

    public static AdjustmentWorkflowResult success(String key, BigDecimal adjustedQty, BigDecimal variance) {
        return AdjustmentWorkflowResult.builder()
                .adjustmentKey(key)
                .success(true)
                .adjustedQty(adjustedQty)
                .variance(variance)
                .build();
    }

    public static AdjustmentWorkflowResult failure(String key, String error) {
        return AdjustmentWorkflowResult.builder()
                .adjustmentKey(key)
                .success(false)
                .errorMessage(error)
                .build();
    }
}
