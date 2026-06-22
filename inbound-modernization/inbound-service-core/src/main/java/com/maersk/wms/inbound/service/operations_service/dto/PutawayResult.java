package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PutawayResult {
    private String putawayKey;
    private String actualLocation;
    private BigDecimal quantity;
    private boolean success;
    private String errorMessage;
}
