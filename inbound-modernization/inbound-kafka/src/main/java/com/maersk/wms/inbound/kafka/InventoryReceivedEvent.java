package com.maersk.wms.inbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Event published when inventory is received.
 */
@Data
@Builder
public class InventoryReceivedEvent {

    private String receiptKey;
    private String lineNumber;
    private String sku;
    private String lot;
    private String lpn;
    private String location;
    private BigDecimal receivedQty;
    private BigDecimal damagedQty;
}
