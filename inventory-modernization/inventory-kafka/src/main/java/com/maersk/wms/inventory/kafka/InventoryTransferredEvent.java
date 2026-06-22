package com.maersk.wms.inventory.kafka;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class InventoryTransferredEvent {
    private String transferKey;
    private String sku;
    private String fromLocation;
    private String toLocation;
    private BigDecimal transferQty;
    private String transferType;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
