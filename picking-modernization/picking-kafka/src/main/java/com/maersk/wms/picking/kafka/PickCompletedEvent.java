package com.maersk.wms.picking.kafka;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PickCompletedEvent {
    private String taskId;
    private String orderId;
    private String sku;
    private BigDecimal pickedQty;
    private String fromLocation;
    private String fromLpn;
    private String toLpn;
    private String userId;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
