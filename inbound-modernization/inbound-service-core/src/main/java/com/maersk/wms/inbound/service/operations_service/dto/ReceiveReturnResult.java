package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReceiveReturnResult {
    private String returnKey;
    private String returnDetailKey;
    private BigDecimal receivedQty;
    private boolean success;
    private String errorMessage;
}
