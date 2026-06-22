package com.maersk.wms.inbound.kafka;

import lombok.Builder;
import lombok.Data;

/**
 * Event published when a receipt is created.
 */
@Data
@Builder
public class ReceiptCreatedEvent {

    private String receiptKey;
    private String storerKey;
    private String receiptType;
    private String poKey;
    private String asnKey;
    private String status;
}
