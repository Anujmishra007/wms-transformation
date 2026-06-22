package com.maersk.wms.inbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Event published when a putaway task is created.
 */
@Data
@Builder
public class PutawayCreatedEvent {

    private String taskKey;
    private String receiptKey;
    private String sku;
    private String fromLocation;
    private String toLocation;
    private BigDecimal qty;
}
