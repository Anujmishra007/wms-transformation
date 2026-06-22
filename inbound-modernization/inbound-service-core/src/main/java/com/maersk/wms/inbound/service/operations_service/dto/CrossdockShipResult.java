package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CrossdockShipResult {
    private String crossdockKey;
    private BigDecimal shippedQuantity;
    private String orderKey;
    private boolean success;
    private String errorMessage;
}
