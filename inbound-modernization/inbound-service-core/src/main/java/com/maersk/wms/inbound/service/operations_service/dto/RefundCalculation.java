package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RefundCalculation {
    private String returnKey;
    private BigDecimal totalRefundAmount;
    private int refundableLines;
    private int totalLines;
}
