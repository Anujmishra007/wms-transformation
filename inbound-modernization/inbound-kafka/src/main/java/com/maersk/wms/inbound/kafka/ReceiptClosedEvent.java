package com.maersk.wms.inbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a receipt is closed.
 */
@Data
@Builder
public class ReceiptClosedEvent {

    private String receiptKey;
    private String storerKey;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalDamagedQty;
    private BigDecimal variance;
    private LocalDateTime closedDate;
}
