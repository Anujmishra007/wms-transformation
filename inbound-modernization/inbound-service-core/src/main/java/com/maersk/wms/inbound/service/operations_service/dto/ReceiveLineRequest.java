package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiveLineRequest {
    private String skuKey;
    private BigDecimal quantity;
    private String uom;
    private String lpnKey;
    private String locationKey;
    private String lot;
    private LocalDate expiryDate;
    private String poKey;
    private String poDetailKey;
    private String conditionCode;
    private String receivedBy;
}
