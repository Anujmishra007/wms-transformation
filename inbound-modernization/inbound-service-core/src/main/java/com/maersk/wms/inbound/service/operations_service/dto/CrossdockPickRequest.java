package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CrossdockPickRequest {
    private BigDecimal pickedQuantity;
    private String uom;
    private String pickedBy;
    private String outboundLpn;
}
