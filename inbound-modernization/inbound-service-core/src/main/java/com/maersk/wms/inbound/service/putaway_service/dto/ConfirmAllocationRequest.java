package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConfirmAllocationRequest {
    private BigDecimal actualQuantity;
    private String uom;
    private String confirmedBy;
}
