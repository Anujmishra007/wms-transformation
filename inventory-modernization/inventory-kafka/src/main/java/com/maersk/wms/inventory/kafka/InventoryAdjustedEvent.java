package com.maersk.wms.inventory.kafka;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class InventoryAdjustedEvent {
    private String adjustmentKey;
    private String sku;
    private String location;
    private String adjustmentType;
    private BigDecimal systemQty;
    private BigDecimal adjustedQty;
    private BigDecimal variance;
    private String reasonCode;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
