package com.maersk.wms.inbound.domain.operations_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result DTO for refund calculation.
 */
@Data
@Builder
public class RefundCalculationResult {
    private String returnKey;
    private BigDecimal originalOrderAmount;
    private BigDecimal refundAmount;
    private BigDecimal restockingFee;
    private BigDecimal shippingRefund;
    private String currencyCode;
    private List<LineRefund> lineRefunds;

    @Data
    @Builder
    public static class LineRefund {
        private String lineNumber;
        private String sku;
        private int returnedQuantity;
        private BigDecimal unitPrice;
        private BigDecimal lineRefundAmount;
        private BigDecimal restockingFee;
    }
}
