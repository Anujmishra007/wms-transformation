package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReserveLocationRequest {
    private String locationKey;
    private String putawayKey;
    private String storerKey;
    private String sku;
    private BigDecimal quantity;
    private String uom;
    private String allocatedBy;
    private int expirationMinutes;
}
