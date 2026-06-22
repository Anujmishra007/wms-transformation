package com.maersk.wms.inbound.domain.operations_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for receiving a return line.
 */
@Data
@Builder
public class ReceiveReturnLineRequest {
    private String returnKey;
    private String lineNumber;
    private String sku;
    private BigDecimal receivedQuantity;
    private BigDecimal damagedQuantity;
    private String lpn;
    private String location;
    private String conditionCode;
    private String disposition;
    private String returnReason;
    private String notes;
}
