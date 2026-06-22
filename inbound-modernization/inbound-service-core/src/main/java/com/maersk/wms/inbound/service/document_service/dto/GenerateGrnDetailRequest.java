package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GenerateGrnDetailRequest {
    private String skuKey;
    private BigDecimal receivedQty;
    private BigDecimal acceptedQty;
    private BigDecimal rejectedQty;
    private String uom;
    private String lot;
    private LocalDate expiryDate;
    private BigDecimal unitCost;
    private Integer lineNumber;
    private String poDetailKey;
    private String receiptDetailKey;
}
