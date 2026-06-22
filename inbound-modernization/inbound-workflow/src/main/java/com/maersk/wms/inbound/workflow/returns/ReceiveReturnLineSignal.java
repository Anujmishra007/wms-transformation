package com.maersk.wms.inbound.workflow.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Signal to receive a return line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveReturnLineSignal {

    private String sku;
    private BigDecimal quantity;
    private BigDecimal expectedQty;

    // Return reason
    private String returnReasonCode;
    private String returnReasonDescription;

    // Location/tracking
    private String lot;
    private String toId;
    private String toLoc;
    private String serialNumber;

    // Package info
    private String packKey;
    private String uom;

    // Lottables
    private String lottable01;
    private String lottable02;
    private String lottable03;

    // Notes
    private String notes;

    // User performing the receive
    private String userId;
}
