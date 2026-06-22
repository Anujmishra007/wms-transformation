package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReceiptDetailStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReceiveResult {
    private String receiptKey;
    private String receiptDetailKey;
    private BigDecimal receivedQty;
    private String lpnKey;
    private boolean success;
    private String errorMessage;
    private ReceiptDetailStatus status;
}
