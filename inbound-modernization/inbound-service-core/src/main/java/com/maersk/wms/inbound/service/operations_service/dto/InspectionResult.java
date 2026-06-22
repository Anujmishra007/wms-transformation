package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnDisposition;
import lombok.Data;

@Data
public class InspectionResult {
    private String returnKey;
    private String returnDetailKey;
    private String conditionCode;
    private ReturnDisposition suggestedDisposition;
    private boolean success;
    private String errorMessage;
}
