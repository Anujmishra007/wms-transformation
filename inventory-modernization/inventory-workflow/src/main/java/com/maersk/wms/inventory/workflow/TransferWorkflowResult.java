package com.maersk.wms.inventory.workflow;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransferWorkflowResult {
    private String transferKey;
    private boolean success;
    private BigDecimal transferredQty;
    private String errorMessage;

    public static TransferWorkflowResult success(String key, BigDecimal qty) {
        return TransferWorkflowResult.builder()
                .transferKey(key)
                .success(true)
                .transferredQty(qty)
                .build();
    }

    public static TransferWorkflowResult failure(String key, String error) {
        return TransferWorkflowResult.builder()
                .transferKey(key)
                .success(false)
                .errorMessage(error)
                .build();
    }
}
