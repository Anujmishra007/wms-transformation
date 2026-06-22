package com.maersk.wms.inbound.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Response DTO for receipt.
 */
@Data
@Builder
public class ReceiptResponse {

    private String receiptKey;
    private String externalReceiptKey;
    private String storerKey;
    private String receiptType;
    private String status;
    private String statusDescription;
    private String poKey;
    private String asnKey;
    private BigDecimal totalExpectedQty;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalDamagedQty;
    private BigDecimal variance;
}
