package com.maersk.wms.inbound.service.operations_service.dto;

import lombok.Data;

@Data
public class InspectReturnLineRequest {
    private String returnDetailKey;
    private String conditionCode;
    private String inspectionNotes;
    private String inspectedBy;
}
