package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnDisposition;
import lombok.Data;

@Data
public class AssignDispositionRequest {
    private String returnDetailKey;
    private ReturnDisposition disposition;
    private String assignedBy;
}
