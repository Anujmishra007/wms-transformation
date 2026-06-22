package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DamageRequest {
    private String receiptDetailKey;
    private BigDecimal damagedQty;
    private String uom;
    private String damageType;
    private String reason;
}
