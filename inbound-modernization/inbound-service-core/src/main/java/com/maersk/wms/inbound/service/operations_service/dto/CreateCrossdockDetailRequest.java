package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateCrossdockDetailRequest {
    private String sku;
    private BigDecimal allocatedQty;
    private String uom;
    private String lot;
    private String serialNumber;
    private String orderKey;
    private String orderDetailKey;
    private Integer lineNumber;
}
