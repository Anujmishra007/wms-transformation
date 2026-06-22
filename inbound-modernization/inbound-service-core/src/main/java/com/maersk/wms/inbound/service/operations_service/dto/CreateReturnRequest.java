package com.maersk.wms.inbound.service.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnType;
import lombok.Data;

@Data
public class CreateReturnRequest {
    private String storerKey;
    private String rmaNumber;
    private String externalReference;
    private ReturnType returnType;
    private String returnReason;
    private String originalOrderKey;
    private String customerKey;
    private String customerName;
    private String carrierCode;
    private String trackingNumber;
    private String notes;
    private String createdBy;
}
