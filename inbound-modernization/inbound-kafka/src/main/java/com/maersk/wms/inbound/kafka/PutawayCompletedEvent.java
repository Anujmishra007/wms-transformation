package com.maersk.wms.inbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a putaway task is completed.
 */
@Data
@Builder
public class PutawayCompletedEvent {

    private String taskKey;
    private String receiptKey;
    private String sku;
    private String toLocation;
    private String toLpn;
    private BigDecimal qty;
    private LocalDateTime completedDate;
}
