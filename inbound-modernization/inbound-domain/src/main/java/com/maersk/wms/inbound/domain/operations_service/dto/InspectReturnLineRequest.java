package com.maersk.wms.inbound.domain.operations_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for inspecting a return line.
 */
@Data
@Builder
public class InspectReturnLineRequest {
    private String returnKey;
    private String lineNumber;
    private String sku;
    private BigDecimal inspectedQuantity;
    private String inspectionGrade;  // A, B, C, D, F
    private String conditionCode;
    private String defectCode;
    private String defectDescription;
    private boolean repackRequired;
    private boolean refurbishRequired;
    private String notes;
}
