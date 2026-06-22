package com.maersk.wms.inventory.kafka;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class InventoryHoldEvent {
    private String holdKey;
    private String holdCode;
    private String scope;
    private String sku;
    private String lot;
    private String action;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
