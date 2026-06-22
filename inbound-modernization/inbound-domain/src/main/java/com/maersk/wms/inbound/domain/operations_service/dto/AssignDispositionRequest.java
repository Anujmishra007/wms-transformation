package com.maersk.wms.inbound.domain.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnDisposition;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for assigning disposition to a return line.
 */
@Data
@Builder
public class AssignDispositionRequest {
    private String returnKey;
    private String lineNumber;
    private String sku;
    private BigDecimal quantity;
    private ReturnDisposition disposition;
    private String dispositionReason;
    private String destinationLocation;
    private String notes;
}
