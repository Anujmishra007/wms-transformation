package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShortPutawayRequest {
    private BigDecimal actualQuantity;
    private String uom;
    private String reason;
}
