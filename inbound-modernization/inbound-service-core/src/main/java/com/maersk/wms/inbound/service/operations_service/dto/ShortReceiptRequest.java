package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShortReceiptRequest {
    private String receiptDetailKey;
    private BigDecimal shortQty;
    private String uom;
    private String reason;
}
