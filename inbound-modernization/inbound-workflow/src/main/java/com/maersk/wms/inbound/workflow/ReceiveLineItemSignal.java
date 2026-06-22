package com.maersk.wms.inbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Signal to receive a line item during the receiving workflow.
 */
@Data
@Builder
public class ReceiveLineItemSignal {

    private String lineNumber;
    private String sku;
    private String lot;
    private String lpn;
    private BigDecimal receivedQty;
    private BigDecimal damagedQty;

    private String location;
    private String conditionCode;
    private String reasonCode;

    private LocalDateTime expirationDate;
    private LocalDateTime manufactureDate;
    private String countryOfOrigin;
    private String vendorLot;

    // Lottable attributes
    private String lottable01;
    private String lottable02;
    private String lottable03;
    private String lottable04;
    private String lottable05;
}
