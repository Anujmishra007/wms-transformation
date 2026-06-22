package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Event published when an order is released for picking.
 */
@Data
@Builder
public class OrderReleasedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String orderNumber;
    private String waveNumber;
    private String releasedBy;
    private LocalDateTime releasedAt;
    private int lineCount;
    private int allocationCount;
}
