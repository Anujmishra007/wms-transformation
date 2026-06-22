package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecordReceiptRequest {
    private BigDecimal quantity;
    private String receiptKey;
}
