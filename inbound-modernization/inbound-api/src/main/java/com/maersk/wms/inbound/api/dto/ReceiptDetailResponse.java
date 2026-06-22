package com.maersk.wms.inbound.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Response DTO for receipt detail.
 */
@Data
@Builder
public class ReceiptDetailResponse {

    private String receiptKey;
    private String lineNumber;
    private String sku;
    private String lot;
    private String lpn;
    private BigDecimal receivedQty;
    private BigDecimal damagedQty;
    private String location;
    private String status;
}
