package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateOsdDetailRequest {
    private String skuKey;
    private BigDecimal expectedQty;
    private BigDecimal actualQty;
    private BigDecimal varianceQty;
    private BigDecimal damageQty;
    private String damageType;
    private String lot;
    private String lpnKey;
    private String notes;
}
