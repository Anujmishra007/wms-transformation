package com.maersk.wms.picking.kafka;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class ShortPickEvent {
    private String taskId;
    private String orderId;
    private String sku;
    private BigDecimal requestedQty;
    private BigDecimal pickedQty;
    private BigDecimal shortQty;
    private String shortReason;
    private String location;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
