package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OverageRequest {
    private String receiptDetailKey;
    private BigDecimal overageQty;
    private String uom;
    private String reason;
}
