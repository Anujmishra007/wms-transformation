package com.maersk.wms.inbound.domain.operations_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result DTO for inventory updates from return processing.
 */
@Data
@Builder
public class InventoryUpdateResult {
    private String returnKey;
    private boolean success;
    private String errorMessage;
    private List<InventoryTransaction> transactions;

    @Data
    @Builder
    public static class InventoryTransaction {
        private String transactionKey;
        private String sku;
        private String lpn;
        private String location;
        private BigDecimal quantity;
        private String transactionType;  // RESTOCK, QC_HOLD, DAMAGE, DISPOSE
        private String disposition;
    }
}
