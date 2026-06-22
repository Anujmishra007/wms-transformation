package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnDisposition;
import lombok.Data;

@Data
public class DispositionResult {
    private String returnKey;
    private String returnDetailKey;
    private ReturnDisposition disposition;
    private String targetZone;
    private boolean refundEligible;
    private boolean success;
    private String errorMessage;
}
