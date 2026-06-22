package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateAsnDetailRequest {
    private String skuKey;
    private BigDecimal expectedQty;
    private String uom;
    private String packKey;
    private String lot;
    private LocalDate expiryDate;
    private String poKey;
    private String poDetailKey;
    private Integer lineNumber;
}
