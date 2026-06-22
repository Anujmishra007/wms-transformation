package com.maersk.wms.inbound.service.document_service.dto;

import lombok.Data;

@Data
public class GrnPrintResult {
    private String grnKey;
    private String grnNumber;
    private boolean success;
    private String errorMessage;
}
