package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReceiveReturnLineRequest {
    private String skuKey;
    private BigDecimal expectedQty;
    private BigDecimal receivedQty;
    private String uom;
    private String lpnKey;
    private String locationKey;
    private String lot;
    private String serialNumber;
    private String conditionCode;
    private String returnReason;
    private String originalOrderDetailKey;
    private String receivedBy;
}
