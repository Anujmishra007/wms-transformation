package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CrossdockPickResult {
    private String crossdockKey;
    private BigDecimal pickedQuantity;
    private String outboundLpn;
    private boolean success;
    private String errorMessage;
}
